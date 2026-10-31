package dev.polymarket.client.enums;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TickSizeTest {

    @Test
    void resolvesFromNumericValueIgnoringScale() {
        assertEquals(TickSize.TICK_0_01, TickSize.of(new BigDecimal("0.01")));
        assertEquals(TickSize.TICK_0_01, TickSize.of(new BigDecimal("0.010")));
        assertEquals(TickSize.TICK_0_001, TickSize.of(new BigDecimal("0.001")));
        assertEquals(TickSize.TICK_0_0025, TickSize.of(new BigDecimal("0.0025")));
    }

    @Test
    void rejectsUnknownOrMissingValue() {
        assertThrows(IllegalArgumentException.class, () -> TickSize.of(new BigDecimal("0.02")));
        assertThrows(IllegalArgumentException.class, () -> TickSize.of(null));
    }

    @Test
    void validPriceRangeIsTickToOneMinusTick() {
        TickSize tick = TickSize.TICK_0_01;

        assertTrue(tick.isValidPrice(new BigDecimal("0.01")));
        assertTrue(tick.isValidPrice(new BigDecimal("0.99")));
        assertFalse(tick.isValidPrice(new BigDecimal("0.009")));
        assertFalse(tick.isValidPrice(new BigDecimal("0.991")));
        assertFalse(tick.isValidPrice(BigDecimal.ONE));
        assertFalse(tick.isValidPrice(null));
    }

    @Test
    void smallerTickWidensValidRange() {
        assertTrue(TickSize.TICK_0_001.isValidPrice(new BigDecimal("0.001")));
        assertTrue(TickSize.TICK_0_001.isValidPrice(new BigDecimal("0.999")));
        assertFalse(TickSize.TICK_0_01.isValidPrice(new BigDecimal("0.999")));
    }
}
