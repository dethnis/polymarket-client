package dev.polymarket.client.enums.system;

public enum SystemEnv {
    POLYMARKET_CLOB_URL("https://clob.polymarket.com"),
    POLYMARKET_API_URL("https://polymarket.com/api"),
    GAMMA_API_POLYMARKET_URL("https://gamma-api.polymarket.com"),
    DATA_API_POLYMARKET_URL("https://data-api.polymarket.com"),

    // WebSocket URLs
    WS_POLYMARKET_MARKET_URL("wss://ws-subscriptions-clob.polymarket.com/ws/market"),
    WS_POLYMARKET_USER_URL("wss://ws-subscriptions-clob.polymarket.com/ws/user"),
    WS_POLYMARKET_WS_LIVE_DATA_URL("wss://ws-live-data.polymarket.com"),

    CHAIN_ID("137"),
    POLYGON_ADDRESS_WALLET,
    PRIVATE_KEY_WALLET,
    FUNDER_ADDRESS;

    private final String defaultValue;

    // Constructor with explicit default value
    SystemEnv(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    // No-arg constructor mimicking Kotlin's 'default = ""' fall-through
    SystemEnv() {
        this("");
    }

    /**
     * Looks up the environment variable by the enum's name.
     * Replicates the Kotlin Elvis behavior falling back to the configured defaultValue.
     *
     * @return The system environment value, or the default string value if empty/null.
     */
    public String getEnvOrDefault() {
        String envValue = System.getenv(this.name());
        return envValue != null ? envValue : this.defaultValue;
    }
}
