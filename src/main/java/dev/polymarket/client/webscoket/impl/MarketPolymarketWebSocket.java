package dev.polymarket.client.webscoket.impl;

import dev.polymarket.client.dto.ws.subscription.MarketSubscriptionMessage;
import dev.polymarket.client.enums.system.SystemEnv;
import dev.polymarket.client.webscoket.AbstractWebSocket;

import java.util.List;
import java.util.function.Consumer;

public class MarketPolymarketWebSocket extends AbstractWebSocket<MarketSubscriptionMessage> {

    public static final String TYPE_MARKET = "market";

    private final List<String> assetsIds;

    public final Boolean customFeatureEnabled = true;

    /**
     * Primary constructor with explicit WebSocket URL configuration.
     */
    public MarketPolymarketWebSocket(
            String webSocketUrl,
            Consumer<String> onMessageHandle,
            List<String> assetsIds
    ) {
        super(webSocketUrl, onMessageHandle);
        this.assetsIds = assetsIds;
    }

    /**
     * Convenience constructor utilizing the default system environment variable for the Polymarket WebSocket endpoint.
     */
    public MarketPolymarketWebSocket(
            Consumer<String> onMessageHandle,
            List<String> assetsIds
    ) {
        this(SystemEnv.WS_POLYMARKET_MARKET_URL.getEnvOrDefault(), onMessageHandle, assetsIds);
    }

    @Override
    public MarketSubscriptionMessage getSubscriptionMessage() {
        return new MarketSubscriptionMessage(assetsIds, TYPE_MARKET, customFeatureEnabled);
    }
}
