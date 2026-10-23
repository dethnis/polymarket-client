package dev.polymarket.client.dto.ws.reponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * Example Message:
 * <pre>
 * {@code
 * {
 * "connection_id": "UERwreZFrPECI6g="
 * "topic": "crypto_prices_chainlink",
 * "type": "update",
 * "timestamp": 1753314064237,
 * "payload": {
 * "full_accuracy_value": "96068873932830260000000"
 * "symbol": "eth/usd",
 * "timestamp": 1753314064213,
 * "value": 3456.78
 * }
 * }
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveDataMessageFormatResponse {

    public static final String TOPIC_NAME = "crypto_prices_chainlink";

    @JsonProperty("connection_id")
    private String connectionId;

    /** Topic name */
    private String topic;

    /** Message type */
    private String type;

    /** Message timestamp in Unix milliseconds */
    private long timestamp;

    /** Payload with price data */
    private Payload payload;

    // No-arg constructor required by Jackson for deserialization
    public LiveDataMessageFormatResponse() {}

    public LiveDataMessageFormatResponse(
            String connectionId,
            String topic,
            String type,
            long timestamp,
            Payload payload
    ) {
        this.connectionId = connectionId;
        this.topic = topic;
        this.type = type;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    // --- Getters and Setters ---

    @JsonProperty("connection_id")
    public String getConnectionId() { return connectionId; }

    @JsonProperty("connection_id")
    public void setConnectionId(String connectionId) { this.connectionId = connectionId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Payload getPayload() { return payload; }
    public void setPayload(Payload payload) { this.payload = payload; }


    // ==========================================
    // Inner/Nested Payload Class
    // ==========================================
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {

        @JsonProperty("full_accuracy_value")
        private String fullAccuracyValue;

        /** Trading pair symbol (Chainlink: slash-separated, e.g., "eth/usd", "btc/usd") */
        private String symbol;

        /** Price timestamp in Unix milliseconds */
        private long timestamp;

        /** Current price value in the quote currency */
        private BigDecimal value;

        // No-arg constructor required by Jackson for deserialization
        public Payload() {}

        public Payload(String fullAccuracyValue, String symbol, long timestamp, BigDecimal value) {
            this.fullAccuracyValue = fullAccuracyValue;
            this.symbol = symbol;
            this.timestamp = timestamp;
            this.value = value;
        }

        // --- Getters and Setters ---

        @JsonProperty("full_accuracy_value")
        public String getFullAccuracyValue() { return fullAccuracyValue; }

        @JsonProperty("full_accuracy_value")
        public void setFullAccuracyValue(String fullAccuracyValue) { this.fullAccuracyValue = fullAccuracyValue; }

        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
    }
}
