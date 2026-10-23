package dev.polymarket.client.dto.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Polymarket API credentials container.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolymarketCredentials {

    /**
     * UUID identifying the credentials
     */
    private String apiKey;

    /**
     * Secret string used to generate HMACs (not sent with requests)
     */
    private String secret;

    /**
     * Secret string sent with each request, used to encrypt/decrypt the secret (never stored)
     */
    private String passphrase;

    // No-arg constructor required by Jackson for deserialization
    public PolymarketCredentials() {}

    public PolymarketCredentials(String apiKey, String secret, String passphrase) {
        this.apiKey = apiKey;
        this.secret = secret;
        this.passphrase = passphrase;
    }

    // --- Getters and Setters ---

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public String getPassphrase() { return passphrase; }
    public void setPassphrase(String passphrase) { this.passphrase = passphrase; }
}
