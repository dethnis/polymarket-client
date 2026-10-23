package dev.polymarket.client.api;

//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import com.polymarket.client.api.clob.AuthL1Api;
//import com.polymarket.client.api.clob.AuthL2Api;
//import com.polymarket.client.api.interceptor.AuthL1Interceptor;
//import com.polymarket.client.api.interceptor.AuthL2Interceptor;
//import com.polymarket.client.dto.api.reponse.ApiKeysResponse;
//import com.polymarket.client.dto.api.reponse.ClosedOnlyModeStatusResponse;
//import com.polymarket.client.dto.api.reponse.PolymarketCredentials;
//import com.polymarket.client.enums.system.SystemEnv;
//import com.polymarket.client.exception.ApiException;
//import com.polymarket.client.utils.Eip712Model;
//import io.netty.handler.codec.http.HttpResponseStatus;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestMethodOrder;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthApiIntegrationTest {

//    private final AuthL1Api authL1Api = new AuthL1Api(
//            new AuthL1Interceptor(
//                    137L,
//                    SystemEnv.POLYGON_ADDRESS_WALLET.getEnvOrDefault(),
//                    SystemEnv.PRIVATE_KEY_WALLET.getEnvOrDefault(),
//                    Eip712Model.NONCE
//            )
//    );
//
//    private PolymarketCredentials credentials;
//    private AuthL2Api authL2Api;
//
//    @Test
//    @Order(1)
//    public void createApiKeyTest() {
//        try {
//            PolymarketCredentials res = authL1Api.createApiKey().join();
//            assertAll(
//                    () -> assertTrue(res.getApiKey() != null && !res.getApiKey().isBlank()),
//                    () -> assertTrue(res.getSecret() != null && !res.getSecret().isBlank()),
//                    () -> assertTrue(res.getPassphrase() != null && !res.getPassphrase().isBlank())
//            );
//        } catch (Exception ex) {
//            // Unwrap completion exception if thrown by CompletableFuture.join()
//            Throwable cause = ex.getCause() instanceof ApiException ? ex.getCause() : ex;
//            if (cause instanceof ApiException apiException) {
//                assertAll(
//                        () -> assertEquals(HttpResponseStatus.BAD_REQUEST, apiException.getStatus()),
//                        () -> assertTrue(apiException.getMessage() != null && apiException.getMessage().contains("Could not create api key"))
//                );
//            } else {
//                throw ex; // Re-throw any unexpected error types
//            }
//        }
//    }
//
//    @Test
//    @Order(2)
//    public void deriveApiKeyTest() {
//        credentials = authL1Api.deriveApiKey().join();
//
//        assertAll(
//                () -> assertTrue(credentials.getApiKey() != null && !credentials.getApiKey().isBlank()),
//                () -> assertTrue(credentials.getSecret() != null && !credentials.getSecret().isBlank()),
//                () -> assertTrue(credentials.getPassphrase() != null && !credentials.getPassphrase().isBlank())
//        );
//
//        // Mimic Kotlin's 'by lazy' instantiation pattern here since credentials are now populated
//        authL2Api = new AuthL2Api(
//                new AuthL2Interceptor(
//                        SystemEnv.POLYGON_ADDRESS_WALLET.getEnvOrDefault(),
//                        credentials
//                )
//        );
//    }
//
//    @Test
//    @Order(3)
//    public void getApiKeysTest() {
//        ApiKeysResponse response = authL2Api.getApiKeys().join();
//        assertNotNull(response.getApiKeys());
//
//        for (String apiKey : response.getApiKeys()) {
//            assertTrue(apiKey != null && !apiKey.isBlank(),
//                       () -> "API key should not be blank. ApiKeys: " + response.getApiKeys());
//        }
//    }
//
//    @Test
//    @Order(4)
//    public void getClosedOnlyModeStatusTest() {
//        ClosedOnlyModeStatusResponse response = authL2Api.getClosedOnlyModeStatus().join();
//        assertTrue(!response.isClosedOnly(), "Closed-only mode should be false by default.");
//    }
//
//    @Test
//    @Order(5)
//    public void deleteApiKeyTest() {
//        Object response = authL2Api.deleteApiKey().join();
//        assertAll(
//                () -> assertNotNull(response),
//                () -> assertEquals("OK", response)
//        );
//    }
}