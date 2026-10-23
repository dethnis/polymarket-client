package dev.polymarket.client.utils;


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
     * For tick size 0.01
     */
    private static final class RoundConfig {
        public static final int PRICE = 2;
        public static final int SIZE = 2;
        public static final int AMOUNT = 4;
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

    public static OrderRawAmounts getOrderRawAmounts(
            TradeSide side,
            BigDecimal size,
            BigDecimal price
    ) {
        BigDecimal rawPrice = roundNormal(price, RoundConfig.PRICE);

        switch (side) {
            case BUY: {
                BigDecimal rawTakerAmt = roundDown(size, RoundConfig.SIZE);
                BigDecimal rawMakerAmt = rawTakerAmt.multiply(rawPrice);

                if (decimalPlaces(rawMakerAmt) > RoundConfig.AMOUNT) {
                    rawMakerAmt = roundUp(rawMakerAmt, RoundConfig.AMOUNT + 4);
                    if (decimalPlaces(rawMakerAmt) > RoundConfig.AMOUNT) {
                        rawMakerAmt = roundDown(rawMakerAmt, RoundConfig.AMOUNT);
                    }
                }
                return new OrderRawAmounts(rawMakerAmt, rawTakerAmt);
            }

            case SELL: {
                BigDecimal rawMakerAmt = roundDown(size, RoundConfig.SIZE);
                BigDecimal rawTakerAmt = rawMakerAmt.multiply(rawPrice);

                if (decimalPlaces(rawTakerAmt) > RoundConfig.AMOUNT) {
                    rawTakerAmt = roundUp(rawTakerAmt, RoundConfig.AMOUNT + 4);
                    if (decimalPlaces(rawTakerAmt) > RoundConfig.AMOUNT) {
                        rawTakerAmt = roundDown(rawTakerAmt, RoundConfig.AMOUNT);
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
