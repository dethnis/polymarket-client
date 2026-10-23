package dev.polymarket.client.utils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class MarketUtils {

    private static final int INTERVAL_MINUTES_15 = 15;

    private MarketUtils() {}

    /**
     * Builds a 15-minute market slug using the current system UTC time, "btc", and "updown" defaults.
     */
    public static String build15MinMarketSlug() {
        return build15MinMarketSlug(OffsetDateTime.now(ZoneOffset.UTC), "btc", "updown");
    }

    /**
     * Builds a 15-minute market slug allowing a custom time target with default token parameters.
     */
    public static String build15MinMarketSlug(OffsetDateTime now) {
        return build15MinMarketSlug(now, "btc", "updown");
    }

    /**
     * Full implementation of the 15-minute market slug engine.
     */
    public static String build15MinMarketSlug(OffsetDateTime now, String symbol, String type) {
        OffsetDateTime rounded = now
                .withMinute((now.getMinute() / INTERVAL_MINUTES_15) * INTERVAL_MINUTES_15)
                .withSecond(0)
                .withNano(0);

        return symbol + "-" + type + "-" + INTERVAL_MINUTES_15 + "m-" + rounded.toEpochSecond();
    }
}
