package dev.polymarket.client.dto.ws.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WsLiveDataSubscriptionMessage {

    private String action;
    private List<Subscription> subscriptions;

    // No-arg constructor required by Jackson for deserialization
    public WsLiveDataSubscriptionMessage() {}

    public WsLiveDataSubscriptionMessage(String action, List<Subscription> subscriptions) {
        this.action = action;
        this.subscriptions = subscriptions;
    }

    // --- Getters and Setters ---

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }


    // ==========================================
    // Inner/Nested Subscription Class
    // ==========================================
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Subscription {

        private String topic;
        private String type;
        private String filters;

        // No-arg constructor required by Jackson for deserialization
        public Subscription() {}

        /**
         * Constructor mimicking Kotlin's default parameter for filters (null).
         */
        public Subscription(String topic, String type) {
            this(topic, type, null);
        }

        public Subscription(String topic, String type, String filters) {
            this.topic = topic;
            this.type = type;
            this.filters = filters;
        }

        // --- Getters and Setters ---

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFilters() {
            return filters;
        }

        public void setFilters(String filters) {
            this.filters = filters;
        }
    }
}
