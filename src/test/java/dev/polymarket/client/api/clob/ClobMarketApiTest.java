package dev.polymarket.client.api.clob;

import dev.polymarket.client.dto.api.response.clob.market.ClobMarketInfoResponse;
import dev.polymarket.client.enums.TickSize;
import dev.polymarket.client.exception.ApiException;
import io.netty.handler.codec.http.HttpResponseStatus;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A stub interceptor answers requests locally (no network) with live /tick-size, /neg-risk and
 * /clob-markets response bodies, including the live 404 body for an unknown token or market.
 */
class ClobMarketApiTest {

    private static final String TOKEN_ID =
            "111627997607794497832861960501688747420941897325745195061811262549726329240283";
    // Live /clob-markets responses (2026-11-01): a sports market without "nr", and a neg-risk market
    private static final String STANDARD_CONDITION_ID =
            "0xd0c4ae292f951b5ced1fabbe2e6d1e3de6fe51be96f50092647fd13fa2c7ec6a";
    private static final String STANDARD_MARKET_INFO = """
            {"gst":"2026-11-01T00:30:00Z","r":{"mi":50,"ma":4.5,"moas":30},\
            "t":[{"t":"58955330758983321986791198024838898367309810402932428908233526075459497872082","o":"Thunder"},\
            {"t":"115122276415583204641361142813012787413667006611764175435050700758671014996427","o":"Rockets"}],\
            "c":"0xd0c4ae292f951b5ced1fabbe2e6d1e3de6fe51be96f50092647fd13fa2c7ec6a","mos":5,"mts":0.01,\
            "mbf":1000,"tbf":1000,"ao":true,"cbos":true,"aot":"2026-10-25T00:51:25Z","ibce":true,\
            "fd":{"r":0,"e":1,"to":true},"v":"v1"}""";
    private static final String NEG_RISK_CONDITION_ID =
            "0xa32317ba83c3c83dc43ef260c3744c22ed8000db53d30bc09372c45cff5fb07a";
    private static final String NEG_RISK_MARKET_INFO = """
            {"r":{"mi":200,"ma":2.5,"e":true,"moas":30},\
            "t":[{"t":"13535941900723044344907002451427688051727855016747811259193368015899108383450","o":"Yes"},\
            {"t":"80727480162707694817992062873857913056698208056368499870278974630056151503113","o":"No"}],\
            "c":"0xa32317ba83c3c83dc43ef260c3744c22ed8000db53d30bc09372c45cff5fb07a","mos":5,"mts":0.01,\
            "mbf":1000,"tbf":1000,"ao":true,"nr":true,"cbos":true,"aot":"2026-10-26T22:21:42Z","ibce":true,\
            "fd":{"r":0,"e":1,"to":true},"v":"v1"}""";

    private static final Map<String, String> BODY_BY_REQUEST = Map.of(
            "/tick-size?token_id=" + TOKEN_ID, "{\"minimum_tick_size\":0.001}",
            "/neg-risk?token_id=" + TOKEN_ID, "{\"neg_risk\":true}",
            "/tick-size?token_id=2", "{\"minimum_tick_size\":0.01}",
            "/neg-risk?token_id=2", "{\"neg_risk\":false}",
            "/clob-markets/" + STANDARD_CONDITION_ID, STANDARD_MARKET_INFO,
            "/clob-markets/" + NEG_RISK_CONDITION_ID, NEG_RISK_MARKET_INFO
    );

    private final List<Request> requests = new CopyOnWriteArrayList<>();

    private final ClobMarketApi api = new ClobMarketApi("https://clob.test", List.of(chain -> {
        Request request = chain.request();
        requests.add(request);
        String query = request.url().encodedQuery();
        String body = BODY_BY_REQUEST.get(request.url().encodedPath() + (query != null ? "?" + query : ""));
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(body != null ? 200 : 404)
                .message(body != null ? "OK" : "Not Found")
                .body(ResponseBody.create(body != null ? body : "{\"error\":\"market not found\"}",
                        MediaType.get("application/json")))
                .build();
    }));

    @Test
    void getsTickSize() {
        assertEquals(TickSize.TICK_0_001, api.getTickSize(TOKEN_ID).join());
        assertEquals(TickSize.TICK_0_01, api.getTickSize("2").join());
    }

    @Test
    void getsNegRisk() {
        assertTrue(api.getNegRisk(TOKEN_ID).join());
        assertFalse(api.getNegRisk("2").join());
    }

    @Test
    void sendsGetWithTokenIdQueryParameter() {
        api.getTickSize(TOKEN_ID).join();

        Request request = requests.getFirst();
        assertEquals("GET", request.method());
        assertEquals("https://clob.test/tick-size?token_id=" + TOKEN_ID, request.url().toString());
    }

    @Test
    void getsClobMarketInfo() {
        ClobMarketInfoResponse info = api.getClobMarketInfo(STANDARD_CONDITION_ID).join();

        assertAll(
                () -> assertEquals(STANDARD_CONDITION_ID, info.getConditionId()),
                () -> assertEquals(TickSize.TICK_0_01, info.getTickSize()),
                () -> assertEquals(0, new BigDecimal("0.01").compareTo(info.getMinimumTickSize())),
                () -> assertEquals(0, new BigDecimal("5").compareTo(info.getMinimumOrderSize())),
                () -> assertEquals(1000, info.getMakerBaseFeeBps()),
                () -> assertEquals(1000, info.getTakerBaseFeeBps()),
                () -> assertTrue(info.isBlockaidCheckEnabled()),
                () -> assertFalse(info.isRfqEnabled()),
                () -> assertFalse(info.isTakerOrderDelayEnabled()),
                () -> assertNull(info.getMinimumOrderAgeSeconds()),
                () -> assertEquals(OffsetDateTime.parse("2026-11-01T00:30:00Z"), info.getGameStartTime()),
                () -> assertEquals(30, ((Number) info.getRewards().get("moas")).intValue()),
                () -> assertEquals(0, BigDecimal.ONE.compareTo(info.getFeeDetails().getExponent())),
                () -> assertEquals(Boolean.TRUE, info.getFeeDetails().getTakersOnly())
        );
    }

    @Test
    void mapsTokensWithOutcomes() {
        List<ClobMarketInfoResponse.Token> tokens = api.getClobMarketInfo(STANDARD_CONDITION_ID).join().getTokens();

        assertEquals(2, tokens.size());
        assertEquals("58955330758983321986791198024838898367309810402932428908233526075459497872082",
                tokens.get(0).getTokenId());
        assertEquals("Thunder", tokens.get(0).getOutcome());
        assertEquals("Rockets", tokens.get(1).getOutcome());
    }

    @Test
    void negRiskIsFalseWhenAbsentAndTrueWhenSent() {
        assertFalse(api.getClobMarketInfo(STANDARD_CONDITION_ID).join().isNegRisk());
        assertTrue(api.getClobMarketInfo(NEG_RISK_CONDITION_ID).join().isNegRisk());
    }

    @Test
    void requestsClobMarketsByConditionId() {
        api.getClobMarketInfo(NEG_RISK_CONDITION_ID).join();

        Request request = requests.getFirst();
        assertEquals("GET", request.method());
        assertEquals("https://clob.test/clob-markets/" + NEG_RISK_CONDITION_ID, request.url().toString());
    }

    @Test
    void unknownMarketFailsWithNotFound() {
        CompletionException thrown = assertThrows(CompletionException.class,
                () -> api.getClobMarketInfo("0x" + "0".repeat(64)).join());

        ApiException cause = assertInstanceOf(ApiException.class, thrown.getCause());
        assertEquals(HttpResponseStatus.NOT_FOUND, cause.getStatus());
    }

    @Test
    void unknownTokenFailsWithNotFound() {
        CompletionException thrown = assertThrows(CompletionException.class, () -> api.getNegRisk("404").join());

        ApiException cause = assertInstanceOf(ApiException.class, thrown.getCause());
        assertEquals(HttpResponseStatus.NOT_FOUND, cause.getStatus());
    }
}
