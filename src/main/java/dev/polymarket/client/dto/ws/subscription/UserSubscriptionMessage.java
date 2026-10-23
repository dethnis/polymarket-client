package dev.polymarket.client.dto.ws.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSubscriptionMessage {

    private Auth auth;

    /**
     * conditionId of market to subscribe to
     */
    private List<String> markets;

    private String type;

    // No-arg constructor required by Jackson for deserialization
    public UserSubscriptionMessage() {}

    public UserSubscriptionMessage(Auth auth, List<String> markets, String type) {
        this.auth = auth;
        this.markets = markets;
        this.type = type;
    }

    // --- Getters and Setters ---

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public List<String> getMarkets() {
        return markets;
    }

    public void setMarkets(List<String> markets) {
        this.markets = markets;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // ==========================================
    // Inner/Nested Auth Class
    // ==========================================
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Auth {

        private String apiKey;
        private String secret;
        private String passphrase;

        // No-arg constructor required by Jackson for deserialization
        public Auth() {}

        public Auth(String apiKey, String secret, String passphrase) {
            this.apiKey = apiKey;
            this.secret = secret;
            this.passphrase = passphrase;
        }

        // --- Getters and Setters ---

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getPassphrase() {
            return passphrase;
        }

        public void setPassphrase(String passphrase) {
            this.passphrase = passphrase;
        }
    }
}
