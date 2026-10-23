package dev.polymarket.client.enums;

public enum TradeSide {
    BUY(0),
    SELL(1);

    private final int index;

    // Constructor for the enum constants
    TradeSide(int index) {
        this.index = index;
    }

    // Getter to access the index value
    public int getIndex() {
        return this.index;
    }
}
