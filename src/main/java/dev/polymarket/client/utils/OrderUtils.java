package dev.polymarket.client.utils;


import dev.polymarket.client.enums.TickSize;
import dev.polymarket.client.enums.TradeSide;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public final class OrderUtils {

    private OrderUtils() {}

    public static class OrderRawAmounts {
        private final BigDecimal rawMakerAmt;
        private final BigDecimal rawTakerAmt;

        public OrderRawAmounts(BigDecimal rawMakerAmt, BigDecimal rawTakerAmt) {
            this.rawMakerAmt = rawMakerAmt;
            this.rawTakerAmt = rawTakerAmt;
        }

        public BigDecimal getRawMakerAmt() { return rawMakerAmt; }
        public BigDecimal getRawTakerAmt() { return rawTakerAmt; }
    }

    /**
     * Generates a unique salt for order creation.
     * Must be a positive long value to match EIP712 uint256 type.
     */
    public static long generateOrderSalt() {
        long min = System.currentTimeMillis();
        long max = Long.MAX_VALUE;

        // Handle fallback in the rare edge case where min >= max close to Long max limit
        if (min >= max) {
            return Math.abs(ThreadLocalRandom.current().nextLong());
        }

        return Math.abs(ThreadLocalRandom.current().nextLong(min, max));
    }

    /**
     * Overload for markets with the default tick size 0.01.
     */
    public static OrderRawAmounts getOrderRawAmounts(
            TradeSide side,
            BigDecimal size,
            BigDecimal price
    ) {
        return getOrderRawAmounts(side, size, price, TickSize.TICK_0_01);
    }

    public static OrderRawAmounts getOrderRawAmounts(
            TradeSide side,
            BigDecimal size,
            BigDecimal price,
            TickSize tickSize
    ) {
        int amountDecimals = tickSize.getAmountDecimals();
        BigDecimal rawPrice = roundNormal(price, tickSize.getPriceDecimals());

        switch (side) {
            case BUY: {
                BigDecimal rawTakerAmt = roundDown(size, tickSize.getSizeDecimals());
                BigDecimal rawMakerAmt = rawTakerAmt.multiply(rawPrice);

                if (decimalPlaces(rawMakerAmt) > amountDecimals) {
                    rawMakerAmt = roundUp(rawMakerAmt, amountDecimals + 4);
                    if (decimalPlaces(rawMakerAmt) > amountDecimals) {
                        rawMakerAmt = roundDown(rawMakerAmt, amountDecimals);
                    }
                }
                return new OrderRawAmounts(rawMakerAmt, rawTakerAmt);
            }

            case SELL: {
                BigDecimal rawMakerAmt = roundDown(size, tickSize.getSizeDecimals());
                BigDecimal rawTakerAmt = rawMakerAmt.multiply(rawPrice);

                if (decimalPlaces(rawTakerAmt) > amountDecimals) {
                    rawTakerAmt = roundUp(rawTakerAmt, amountDecimals + 4);
                    if (decimalPlaces(rawTakerAmt) > amountDecimals) {
                        rawTakerAmt = roundDown(rawTakerAmt, amountDecimals);
                    }
                }
                return new OrderRawAmounts(rawMakerAmt, rawTakerAmt);
            }

            default:
                throw new IllegalArgumentException("Unknown trade side: " + side);
        }
    }

    private static BigDecimal roundDown(BigDecimal value, int decimals) {
        return value.setScale(decimals, RoundingMode.DOWN);
    }

    private static BigDecimal roundUp(BigDecimal value, int decimals) {
        return value.setScale(decimals, RoundingMode.UP);
    }

    private static BigDecimal roundNormal(BigDecimal value, int decimals) {
        return value.setScale(decimals, RoundingMode.HALF_UP);
    }

    private static int decimalPlaces(BigDecimal value) {
        int scale = value.stripTrailingZeros().scale();
        return Math.max(scale, 0);
    }
}
