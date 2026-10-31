package dev.polymarket.client.enums;

import java.math.BigDecimal;

/**
 * Minimum price increment of a market, with the rounding used to compute order amounts
 * (decimal places for price, size and amount), as in Polymarket's clob-client ROUNDING_CONFIG.
 */
public enum TickSize {
    TICK_0_1("0.1", 1, 2, 3),
    TICK_0_01("0.01", 2, 2, 4),
    TICK_0_005("0.005", 3, 2, 5),
    TICK_0_0025("0.0025", 4, 2, 6),
    TICK_0_001("0.001", 3, 2, 5),
    TICK_0_0001("0.0001", 4, 2, 6);

    private final BigDecimal value;
    private final int priceDecimals;
    private final int sizeDecimals;
    private final int amountDecimals;

    TickSize(String value, int priceDecimals, int sizeDecimals, int amountDecimals) {
        this.value = new BigDecimal(value);
        this.priceDecimals = priceDecimals;
        this.sizeDecimals = sizeDecimals;
        this.amountDecimals = amountDecimals;
    }

    /**
     * Resolves a tick size from its numeric value, e.g. MarketResponse.orderPriceMinTickSize.
     */
    public static TickSize of(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Tick size cannot be null");
        }
        for (TickSize tickSize : values()) {
            if (tickSize.value.compareTo(value) == 0) {
                return tickSize;
            }
        }
        throw new IllegalArgumentException("Unsupported tick size: " + value);
    }

    /**
     * Valid prices are within [tickSize, 1 - tickSize].
     */
    public boolean isValidPrice(BigDecimal price) {
        return price != null
                && price.compareTo(value) >= 0
                && price.compareTo(BigDecimal.ONE.subtract(value)) <= 0;
    }

    // Getters
    public BigDecimal getValue() { return value; }
    public int getPriceDecimals() { return priceDecimals; }
    public int getSizeDecimals() { return sizeDecimals; }
    public int getAmountDecimals() { return amountDecimals; }
}
