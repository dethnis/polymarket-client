package dev.polymarket.client.api.interceptor;

import dev.polymarket.client.dto.api.response.PolymarketCredentials;
import dev.polymarket.client.utils.HmacUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs requests through a real OkHttp chain. A stub interceptor after AuthL2Interceptor captures
 * the signed request and answers it locally, so no network is used.
 */
class AuthL2InterceptorTest {

    private static final String ADDRESS = "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266";
    private static final PolymarketCredentials CREDENTIALS = new PolymarketCredentials(
            "00000000-1111-2222-3333-444444444444",
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            "passphrase"
    );
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final AtomicReference<Request> captured = new AtomicReference<>();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AuthL2Interceptor(ADDRESS, CREDENTIALS))
            .addInterceptor(chain -> {
                captured.set(chain.request());
                return new Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(ResponseBody.create("{}", JSON))
                        .build();
            })
            .build();

    private Request send(Request request) throws IOException {
        try (Response ignored = client.newCall(request).execute()) {
            return captured.get();
        }
    }

    private static String expectedSignature(Request signed, String method, String path, String body) {
        long timestamp = Long.parseLong(signed.header("POLY_TIMESTAMP"));
        return HmacUtils.getHmacPolymarketSignature(CREDENTIALS.getSecret(), timestamp, method, path, body);
    }

    @Test
    void addsL2Headers() throws IOException {
        long before = System.currentTimeMillis() / 1000;
        Request signed = send(new Request.Builder().url("https://clob.polymarket.com/auth/api-keys").get().build());
        long after = System.currentTimeMillis() / 1000;

        assertAll(
                () -> assertEquals(ADDRESS, signed.header("POLY_ADDRESS")),
                () -> assertEquals(CREDENTIALS.getApiKey(), signed.header("POLY_API_KEY")),
                () -> assertEquals(CREDENTIALS.getPassphrase(), signed.header("POLY_PASSPHRASE")),
                () -> {
                    long timestamp = Long.parseLong(signed.header("POLY_TIMESTAMP"));
                    assertTrue(timestamp >= before && timestamp <= after, "timestamp should be current unix seconds");
                }
        );
    }

    @Test
    void signsGetWithoutBody() throws IOException {
        Request signed = send(new Request.Builder().url("https://clob.polymarket.com/auth/api-keys").get().build());

        assertEquals(expectedSignature(signed, "GET", "/auth/api-keys", null), signed.header("POLY_SIGNATURE"));
    }

    @Test
    void signsPostWithExactJsonBody() throws IOException {
        String body = "{\"order\":{\"salt\":1},\"owner\":\"key\",\"orderType\":\"GTC\"}";
        Request signed = send(new Request.Builder()
                .url("https://clob.polymarket.com/order")
                .post(RequestBody.create(body, JSON))
                .build());

        assertEquals(expectedSignature(signed, "POST", "/order", body), signed.header("POLY_SIGNATURE"));
    }

    @Test
    void treatsEmptyBodyAsNoBody() throws IOException {
        // AbstractApi sends DELETE /auth/api-key with an empty body
        Request signed = send(new Request.Builder()
                .url("https://clob.polymarket.com/auth/api-key")
                .delete(RequestBody.create("", null))
                .build());

        assertEquals(expectedSignature(signed, "DELETE", "/auth/api-key", null), signed.header("POLY_SIGNATURE"));
    }

    @Test
    void signsPathWithoutQueryString() throws IOException {
        Request signed = send(new Request.Builder()
                .url("https://clob.polymarket.com/data/orders?market=0xabc&next_cursor=MA==")
                .get()
                .build());

        assertEquals(expectedSignature(signed, "GET", "/data/orders", null), signed.header("POLY_SIGNATURE"));
    }

    @Test
    void keepsOriginalRequestIntact() throws IOException {
        String body = "{\"orderID\":\"0xabc\"}";
        Request signed = send(new Request.Builder()
                .url("https://clob.polymarket.com/order")
                .delete(RequestBody.create(body, JSON))
                .build());

        Buffer buffer = new Buffer();
        signed.body().writeTo(buffer);
        assertAll(
                () -> assertEquals("DELETE", signed.method()),
                () -> assertEquals("https://clob.polymarket.com/order", signed.url().toString()),
                () -> assertEquals(body, buffer.readUtf8())
        );
    }

    @Test
    void rejectsBlankAddress() {
        assertThrows(IllegalArgumentException.class, () -> new AuthL2Interceptor(" ", CREDENTIALS));
    }
}
