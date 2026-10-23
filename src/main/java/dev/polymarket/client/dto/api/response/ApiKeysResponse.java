package dev.polymarket.client.dto.api.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiKeysResponse {

    private List<String> apiKeys;

    // No-arg constructor required by Jackson for deserialization
    public ApiKeysResponse() {}

    public ApiKeysResponse(List<String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    // --- Getters and Setters ---

    public List<String> getApiKeys() { return apiKeys; }
    public void setApiKeys(List<String> apiKeys) { this.apiKeys = apiKeys; }
}

