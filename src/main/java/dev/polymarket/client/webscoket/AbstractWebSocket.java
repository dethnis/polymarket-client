package dev.polymarket.client.webscoket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Abstract WebSocket client with automatic reconnection support.
 *
 * @param <SM> Type of the subscription message sent on connection open.
 */
public abstract class AbstractWebSocket<SM> extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractWebSocket.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String webSocketUrl;
    private final Consumer<String> onMessageHandle;
    private final WebSocketConfig config;

    // Reconnect control
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean isReconnecting = new AtomicBoolean(false);
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private long backoffMs;

    public AbstractWebSocket(
            String webSocketUrl,
            Consumer<String> onMessageHandle,
            WebSocketConfig config
    ) {
        super(URI.create(webSocketUrl), new Draft_6455());
        this.webSocketUrl = webSocketUrl;
        this.onMessageHandle = onMessageHandle;
        this.config = config != null ? config : new WebSocketConfig();
        this.backoffMs = this.config.getMinBackoffMs();
    }

    public AbstractWebSocket(String webSocketUrl, Consumer<String> onMessageHandle) {
        this(webSocketUrl, onMessageHandle, new WebSocketConfig());
    }

    public abstract SM getSubscriptionMessage();

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("{} connection opened", webSocketUrl);

        isReconnecting.set(false);
        backoffMs = config.getMinBackoffMs();
        setConnectionLostTimeout(config.getConnectionLostTimeout());

        try {
            String json = objectMapper.writeValueAsString(getSubscriptionMessage());
            send(json);
        } catch (Exception e) {
            log.error("{} failed to serialize/send subscription message", webSocketUrl, e);
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            onMessageHandle.accept(message);
        } catch (Exception ex) {
            log.warn("{}: onMessage handler failed", webSocketUrl, ex);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("{} connection closed: code={}, remote={}, reason={}",
                 webSocketUrl, code, remote, reason != null ? reason : "");
        if (!isShutdown.get()) {
            scheduleReconnect();
        }
    }

    @Override
    public void onError(Exception ex) {
        log.error("{} error", webSocketUrl, ex);
        if (!isShutdown.get() && !isOpen() && !isReconnecting.get()) {
            scheduleReconnect();
        }
    }

    public void shutdown() {
        if (!isShutdown.compareAndSet(false, true)) {
            return;
        }
        try {
            setConnectionLostTimeout(0);
        } catch (Exception ignored) {
        }
        try {
            close();
        } catch (Exception ignored) {
        }
        scheduler.shutdownNow();
    }

    private void scheduleReconnect() {
        if (isShutdown.get()) {
            return;
        }
        if (!isReconnecting.compareAndSet(false, true)) {
            return;
        }

        // Jitter ± jitterFactor%
        long jitter = (long) (backoffMs * config.getJitterFactor());
        long delay;
        if (jitter <= 0) {
            delay = Math.max(0L, backoffMs);
        } else {
            long randomJitter = ThreadLocalRandom.current().nextLong(-jitter, jitter + 1);
            delay = Math.max(0L, backoffMs + randomJitter);
        }

        log.info("{} attempting reconnect in {}ms...", webSocketUrl, delay);

        scheduler.schedule(() -> {
            try {
                // Block inside scheduler thread, never in WS callback thread
                reconnectBlocking();
                log.info("{} reconnected", webSocketUrl);
            } catch (Exception e) {
                log.error("{} reconnect failed", webSocketUrl, e);
                isReconnecting.set(false);
                backoffMs = Math.min(config.getMaxBackoffMs(), Math.max(config.getMinBackoffMs(), backoffMs * 2));
                scheduleReconnect();
            } finally {
                // Allow next reconnect attempt to be scheduled if still not open
                if (!isOpen()) {
                    isReconnecting.set(false);
                }
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}
