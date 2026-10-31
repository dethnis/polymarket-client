package dev.polymarket.client.utils;

import dev.polymarket.client.enums.TickSize;
import dev.polymarket.client.enums.TradeSide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Expected amounts were computed with the get_order_amounts rounding from Polymarket's
 * py-clob-client-v2 (order_builder/builder.py, ROUNDING_CONFIG), in collateral/share units with 6 decimals.
 */
class OrderUtilsTest {

    private static String[] tokenAmounts(TradeSide side, String size, String price, TickSize tickSize) {
        OrderUtils.OrderRawAmounts amounts =
                OrderUtils.getOrderRawAmounts(side, new BigDecimal(size), new BigDecimal(price), tickSize);
        return new String[]{
                AmountUtils.parseUnits(amounts.getRawMakerAmt()),
                AmountUtils.parseUnits(amounts.getRawTakerAmt())
        };
    }

    @ParameterizedTest(name = "{0} {1} @ {2} (tick {3}) -> maker {4}, taker {5}")
    @CsvSource({
            "BUY,  10,     0.123,  TICK_0_001,  1230000,  10000000",
            "SELL, 10,     0.123,  TICK_0_001,  10000000, 1230000",
            "BUY,  10,     0.1234, TICK_0_001,  1230000,  10000000",
            "BUY,  3.33,   0.999,  TICK_0_001,  3326670,  3330000",
            "BUY,  33.337, 0.0123, TICK_0_0001, 409959,   33330000",
            "SELL, 33.33,  0.9876, TICK_0_0001, 33330000, 32916708",
            "BUY,  7.77,   0.5,    TICK_0_1,    3885000,  7770000",
            "BUY,  21.37,  0.57,   TICK_0_01,   12180900, 21370000",
            "SELL, 21.37,  0.57,   TICK_0_01,   21370000, 12180900",
            "BUY,  100,    0.005,  TICK_0_005,  500000,   100000000",
            "BUY,  12.5,   0.0025, TICK_0_0025, 31250,    12500000"
    })
    void matchesReferenceAmounts(TradeSide side, String size, String price, TickSize tickSize,
                                 String expectedMaker, String expectedTaker) {
        String[] amounts = tokenAmounts(side, size, price, tickSize);

        assertEquals(expectedMaker, amounts[0], "maker amount");
        assertEquals(expectedTaker, amounts[1], "taker amount");
    }

    @Test
    void defaultOverloadUsesTickSize001() {
        OrderUtils.OrderRawAmounts byDefault =
                OrderUtils.getOrderRawAmounts(TradeSide.BUY, new BigDecimal("21.37"), new BigDecimal("0.57"));
        OrderUtils.OrderRawAmounts explicit = OrderUtils.getOrderRawAmounts(
                TradeSide.BUY, new BigDecimal("21.37"), new BigDecimal("0.57"), TickSize.TICK_0_01);

        assertEquals(explicit.getRawMakerAmt(), byDefault.getRawMakerAmt());
        assertEquals(explicit.getRawTakerAmt(), byDefault.getRawTakerAmt());
    }

    @Test
    void priceIsRoundedToTickSizePrecision() {
        // 0.123 keeps its third decimal on a 0.001 market but becomes 0.12 on a 0.01 market
        assertEquals("1230000", tokenAmounts(TradeSide.BUY, "10", "0.123", TickSize.TICK_0_001)[0]);
        assertEquals("1200000", tokenAmounts(TradeSide.BUY, "10", "0.123", TickSize.TICK_0_01)[0]);
    }
}
