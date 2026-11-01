package dev.polymarket.client.api.clob;

import dev.polymarket.client.api.AbstractApi;
import dev.polymarket.client.dto.api.response.clob.market.ClobMarketInfoResponse;
import dev.polymarket.client.dto.api.response.clob.market.NegRiskResponse;
import dev.polymarket.client.dto.api.response.clob.market.TickSizeResponse;
import dev.polymarket.client.enums.TickSize;
import dev.polymarket.client.enums.system.SystemEnv;
import okhttp3.Interceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * API client for per-token market parameters on Polymarket CLOB.
 * <p>
 * Public API, no authentication required. Each call is a network round trip, so fetch these
 * ahead of time (e.g. when subscribing to a market) rather than while placing an order.
 */
public class ClobMarketApi extends AbstractApi {

    // Primary Constructor
    public ClobMarketApi(String baseUrl, List<Interceptor> interceptors) {
        super(baseUrl, interceptors);
    }

    // Default Constructor mimicking Kotlin's default argument property fallback
    public ClobMarketApi() {
        this(SystemEnv.POLYMARKET_CLOB_URL.getEnvOrDefault(), Collections.emptyList());
    }

    /**
     * Fetches the minimum tick size of a token (outcome).
     *
     * @param tokenId CLOB token id (asset id).
     * @return CompletableFuture with the token's tick size; fails with ApiException (404) for an unknown token.
     */
    public CompletableFuture<TickSize> getTickSize(String tokenId) {
        return super.get("/tick-size?token_id=" + encode(tokenId), TickSizeResponse.class)
                .thenApply(response -> TickSize.of(response.getMinimumTickSize()));
    }

    /**
     * Fetches whether a token (outcome) belongs to a neg-risk market.
     *
     * @param tokenId CLOB token id (asset id).
     * @return CompletableFuture with the neg-risk flag; fails with ApiException (404) for an unknown token.
     */
    public CompletableFuture<Boolean> getNegRisk(String tokenId) {
        return super.get("/neg-risk?token_id=" + encode(tokenId), NegRiskResponse.class)
                .thenApply(NegRiskResponse::isNegRisk);
    }

    /**
     * Fetches all CLOB-level parameters of a market in one call: its tokens, tick size, neg-risk flag,
     * minimum order size, fees and rewards.
     *
     * @param conditionId Condition id of the market (MarketResponse.conditionId).
     * @return CompletableFuture with the market info; fails with ApiException (404) for an unknown market.
     */
    public CompletableFuture<ClobMarketInfoResponse> getClobMarketInfo(String conditionId) {
        return super.get("/clob-markets/" + encode(conditionId), ClobMarketInfoResponse.class);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
