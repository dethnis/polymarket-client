package dev.polymarket.client.webscoket;

public final class WebSocketConfig {

    private final long minBackoffMs;
    private final long maxBackoffMs;
    private final int connectionLostTimeout;
    private final double jitterFactor;

    /**
     * Primary constructor that applies all parameter validation rules.
     */
    public WebSocketConfig(long minBackoffMs, long maxBackoffMs, int connectionLostTimeout, double jitterFactor) {
        if (minBackoffMs <= 0) {
            throw new IllegalArgumentException("minBackoffMs must be positive");
        }
        if (maxBackoffMs < minBackoffMs) {
            throw new IllegalArgumentException("maxBackoffMs must be >= minBackoffMs");
        }
        if (connectionLostTimeout < 0) {
            throw new IllegalArgumentException("connectionLostTimeout must be non-negative");
        }
        if (jitterFactor < 0.0 || jitterFactor > 1.0) {
            throw new IllegalArgumentException("jitterFactor must be between 0.0 and 1.0");
        }

        this.minBackoffMs = minBackoffMs;
        this.maxBackoffMs = maxBackoffMs;
        this.connectionLostTimeout = connectionLostTimeout;
        this.jitterFactor = jitterFactor;
    }

    /**
     * Convenience no-arg constructor initializing properties with default values.
     */
    public WebSocketConfig() {
        this(2000L, 60000L, 30, 0.2);
    }

    // --- Getters ---

    public long getMinBackoffMs() {
        return minBackoffMs;
    }

    public long getMaxBackoffMs() {
        return maxBackoffMs;
    }

    public int getConnectionLostTimeout() {
        return connectionLostTimeout;
    }

    public double getJitterFactor() {
        return jitterFactor;
    }
}