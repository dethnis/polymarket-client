package dev.polymarket.client.webscoket.impl;

import dev.polymarket.client.dto.ws.subscription.WsLiveDataSubscriptionMessage;
import dev.polymarket.client.enums.system.SystemEnv;
import dev.polymarket.client.webscoket.AbstractWebSocket;
import java.util.List;
import java.util.function.Consumer;

public class WsLiveDataPolymarketWebSocketClient extends AbstractWebSocket<WsLiveDataSubscriptionMessage> {

    private final List<WsLiveDataSubscriptionMessage.Subscription> subscriptions;

    /**
     * Primary constructor with explicit WebSocket URL configuration.
     */
    public WsLiveDataPolymarketWebSocketClient(
            String webSocketUrl,
            Consumer<String> onMessageHandle,
            List<WsLiveDataSubscriptionMessage.Subscription> subscriptions
    ) {
        super(webSocketUrl, onMessageHandle);
        this.subscriptions = subscriptions;
    }

    /**
     * Convenience constructor utilizing the default system environment variable for the Polymarket WebSocket endpoint.
     */
    public WsLiveDataPolymarketWebSocketClient(
            Consumer<String> onMessageHandle,
            List<WsLiveDataSubscriptionMessage.Subscription> subscriptions
    ) {
        this(SystemEnv.WS_POLYMARKET_WS_LIVE_DATA_URL.getEnvOrDefault(), onMessageHandle, subscriptions);
    }

    @Override
    public WsLiveDataSubscriptionMessage getSubscriptionMessage() {
        return new WsLiveDataSubscriptionMessage(
                "subscribe",
                subscriptions
        );
    }
}