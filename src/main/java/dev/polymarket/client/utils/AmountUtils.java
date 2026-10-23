package dev.polymarket.client.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AmountUtils {

    private static final int COLLATERAL_TOKEN_DECIMALS = 6;

    private AmountUtils() {}

    /**
     * Converts a decimal amount to its integer representation using the default collateral token decimals (6).
     *
     * @param amount The decimal value
     * @return The scaled integer value as a String
     */
    public static String parseUnits(BigDecimal amount) {
        return parseUnits(amount, COLLATERAL_TOKEN_DECIMALS);
    }

    /**
     * Converts a decimal amount to its integer representation based on token decimals.
     * Similar to ethers.js parseUnits.
     *
     * @param amount   The decimal value
     * @param decimals Number of decimals for the token
     * @return The scaled integer value as a String
     */
    public static String parseUnits(BigDecimal amount, int decimals) {
        BigDecimal scale = BigDecimal.TEN.pow(decimals);

        return amount.multiply(scale)
                .setScale(0, RoundingMode.DOWN)
                .toBigInteger()
                .toString();
    }

    /**
     * Converts an integer amount back to decimal representation using the default collateral token decimals (6).
     *
     * @param value The integer value as a String
     * @return The decimal value as a String
     */
    public static String formatUnits(String value) {
        return formatUnits(value, COLLATERAL_TOKEN_DECIMALS);
    }

    /**
     * Converts an integer amount back to decimal representation.
     * Similar to ethers.js formatUnits.
     *
     * @param value    The integer value as a String
     * @param decimals Number of decimals for the token
     * @return The decimal value as a String
     */
    public static String formatUnits(String value, int decimals) {
        BigDecimal amount = new BigDecimal(value);
        BigDecimal scale = BigDecimal.TEN.pow(decimals);

        return amount.divide(scale, decimals, RoundingMode.DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }
}
