package dev.polymarket.client.webscoket.impl;

import dev.polymarket.client.dto.ws.subscription.UserSubscriptionMessage;
import dev.polymarket.client.enums.system.SystemEnv;
import dev.polymarket.client.webscoket.AbstractWebSocket;

import java.util.List;
import java.util.function.Consumer;

public class UserPolymarketWebSocket extends AbstractWebSocket<UserSubscriptionMessage> {

    public static final String TYPE_MARKET = "user";

    private final UserSubscriptionMessage.Auth auth;
    private final List<String> marketConditionIds;

    /**
     * Primary constructor with explicit WebSocket URL configuration.
     */
    public UserPolymarketWebSocket(
            String webSocketUrl,
            Consumer<String> onMessageHandle,
            UserSubscriptionMessage.Auth auth,
            List<String> marketConditionIds
    ) {
        super(webSocketUrl, onMessageHandle);
        this.auth = auth;
        this.marketConditionIds = marketConditionIds;
    }

    /**
     * Convenience constructor utilizing the default system environment variable for the Polymarket WebSocket endpoint.
     */
    public UserPolymarketWebSocket(
            Consumer<String> onMessageHandle,
            UserSubscriptionMessage.Auth auth,
            List<String> marketConditionIds
    ) {
        this(SystemEnv.WS_POLYMARKET_USER_URL.getEnvOrDefault(), onMessageHandle, auth, marketConditionIds);
    }

    @Override
    public UserSubscriptionMessage getSubscriptionMessage() {
        return new UserSubscriptionMessage(
                auth,
                marketConditionIds,
                TYPE_MARKET
        );
    }
}
