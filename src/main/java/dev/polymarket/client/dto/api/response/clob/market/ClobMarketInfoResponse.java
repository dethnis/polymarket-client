package dev.polymarket.client.dto.api.response.clob.market;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.polymarket.client.enums.TickSize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * CLOB-level parameters of a market (GET /clob-markets/{condition_id}), with the API's short keys
 * mapped to readable names.
 * <pre>{@code
 * {
 * "gst": "2026-11-01T00:30:00Z",
 * "r": {"mi": 200, "ma": 2.5, "e": true, "moas": 30},
 * "t": [{"t": "1353594...", "o": "Yes"}, {"t": "8072748...", "o": "No"}],
 * "c": "0xa32317ba83c3c83dc43ef260c3744c22ed8000db53d30bc09372c45cff5fb07a",
 * "mos": 5,
 * "mts": 0.01,
 * "mbf": 1000,
 * "tbf": 1000,
 * "nr": true,
 * "ibce": true,
 * "fd": {"r": 0, "e": 1, "to": true}
 * }
 * }</pre>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClobMarketInfoResponse {

    /**
     * Condition id of the market. Returned by the API, but not in the OpenAPI spec.
     */
    @JsonProperty("c")
    private String conditionId;

    /**
     * Tokens (outcomes) of the market.
     */
    @JsonProperty("t")
    private List<Token> tokens;

    /**
     * Minimum tick size (price increment).
     */
    @JsonProperty("mts")
    private BigDecimal minimumTickSize;

    /**
     * Whether the market is neg-risk. Not in the OpenAPI spec: the API only sends it when true,
     * so false when absent (as in Polymarket's own clients).
     */
    @JsonProperty("nr")
    private boolean negRisk;

    /**
     * Minimum order size, in shares.
     */
    @JsonProperty("mos")
    private BigDecimal minimumOrderSize;

    /**
     * Maker base fee in basis points.
     */
    @JsonProperty("mbf")
    private long makerBaseFeeBps;

    /**
     * Taker base fee in basis points.
     */
    @JsonProperty("tbf")
    private long takerBaseFeeBps;

    /**
     * Fee curve parameters.
     */
    @JsonProperty("fd")
    private FeeDetails feeDetails;

    /**
     * Rewards configuration; its shape is not specified by the API.
     */
    @JsonProperty("r")
    private Map<String, Object> rewards;

    /**
     * Game start time for sports markets, otherwise null.
     */
    @JsonProperty("gst")
    private OffsetDateTime gameStartTime;

    /**
     * Whether RFQ (Request for Quote) is enabled.
     */
    @JsonProperty("rfqe")
    private boolean rfqEnabled;

    /**
     * Whether marketable orders are held for the 250 ms taker-delay window. Omitted by the API when false.
     */
    @JsonProperty("itode")
    private boolean takerOrderDelayEnabled;

    /**
     * Whether the Blockaid check is enabled.
     */
    @JsonProperty("ibce")
    private boolean blockaidCheckEnabled;

    /**
     * Minimum order age in seconds, or null if not set.
     */
    @JsonProperty("oas")
    private Integer minimumOrderAgeSeconds;

    /**
     * Minimum tick size as a TickSize.
     *
     * @throws IllegalArgumentException if the market uses a tick size TickSize does not support.
     */
    public TickSize getTickSize() {
        return TickSize.of(minimumTickSize);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Token {

        @JsonProperty("t")
        private String tokenId;

        /**
         * Outcome label, e.g. "Yes" or "No".
         */
        @JsonProperty("o")
        private String outcome;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeeDetails {

        @JsonProperty("r")
        private BigDecimal rate;

        @JsonProperty("e")
        private BigDecimal exponent;

        @JsonProperty("to")
        private Boolean takersOnly;
    }
}
