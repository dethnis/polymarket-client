package dev.polymarket.client.dto.api.request.clob.order;

import dev.polymarket.client.dto.PolymarketContext;
import dev.polymarket.client.dto.api.response.gamma.MarketResponse;
import dev.polymarket.client.enums.OrderType;
import dev.polymarket.client.enums.TickSize;
import dev.polymarket.client.enums.TradeSide;
import dev.polymarket.client.utils.AmountUtils;
import dev.polymarket.client.utils.Eip712Model;
import dev.polymarket.client.utils.Eip712Signer;
import dev.polymarket.client.utils.OrderUtils;

import java.math.BigDecimal;

/**
 * User-friendly order request DTO for placing orders on Polymarket.
 * <p>
 * Simplified representation of an order that can be converted to a full
 * CreateAndPlaceOrderRequest with proper signing and amounts calculation.
 */
public class UserOrderRequest {

    private final String assetId;
    private final TradeSide side;
    private final BigDecimal price;
    private final BigDecimal size;
    private final OrderType orderType;
    private final String expiration;

    /**
     * Market tick size (see MarketResponse.orderPriceMinTickSize); sets the valid price range and amount rounding.
     */
    private final TickSize tickSize;

    /**
     * Whether the market is neg-risk (see MarketResponse.negRisk); selects the exchange contract used for signing.
     */
    private final boolean negRisk;

    // Full constructor (Primary Constructor)
    public UserOrderRequest(
            String assetId,
            TradeSide side,
            BigDecimal price,
            BigDecimal size,
            OrderType orderType,
            String expiration,
            TickSize tickSize,
            boolean negRisk
    ) {
        // Init block validations
        if (tickSize == null) {
            throw new IllegalArgumentException("Tick size must not be null");
        }
        if (!tickSize.isValidPrice(price)) {
            throw new IllegalArgumentException("Price must be in range " + tickSize.getValue() + " to "
                    + BigDecimal.ONE.subtract(tickSize.getValue()) + ", but was " + price);
        }
        if (size == null || size.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Size must be positive, but was " + size);
        }

        this.assetId = assetId;
        this.side = side;
        this.price = price;
        this.size = size;
        this.orderType = orderType;
        this.expiration = expiration;
        this.tickSize = tickSize;
        this.negRisk = negRisk;
    }

    // Overloaded constructor with default orderType (GTC) and expiration ("0").
    // There are deliberately no defaults for tickSize and negRisk: a wrong guess mis-rounds the price
    // or gets the order rejected, so they must come from market data.
    public UserOrderRequest(
            String assetId,
            TradeSide side,
            BigDecimal price,
            BigDecimal size,
            TickSize tickSize,
            boolean negRisk
    ) {
        this(assetId, side, price, size, OrderType.GTC, "0", tickSize, negRisk);
    }

    /**
     * Creates an order for one outcome of a Gamma market, taking tick size and neg-risk from the market data.
     * No network call.
     *
     * @throws IllegalArgumentException if the asset is not one of the market's tokens, or the market data
     *                                  lacks orderPriceMinTickSize or negRisk.
     */
    public static UserOrderRequest forMarket(
            MarketResponse market,
            String assetId,
            TradeSide side,
            BigDecimal price,
            BigDecimal size,
            OrderType orderType,
            String expiration
    ) {
        if (market.getClobTokenIds() == null || !market.getClobTokenIds().contains(assetId)) {
            throw new IllegalArgumentException("Asset " + assetId + " is not a token of market " + market.getSlug());
        }
        if (market.getOrderPriceMinTickSize() == null) {
            throw new IllegalArgumentException("Market " + market.getSlug() + " has no orderPriceMinTickSize");
        }
        if (market.getNegRisk() == null) {
            throw new IllegalArgumentException("Market " + market.getSlug() + " has no negRisk flag");
        }
        return new UserOrderRequest(assetId, side, price, size, orderType, expiration,
                TickSize.of(market.getOrderPriceMinTickSize()), market.getNegRisk());
    }

    // Overloaded factory with default orderType (GTC) and expiration ("0")
    public static UserOrderRequest forMarket(
            MarketResponse market,
            String assetId,
            TradeSide side,
            BigDecimal price,
            BigDecimal size
    ) {
        return forMarket(market, assetId, side, price, size, OrderType.GTC, "0");
    }

    /**
     * Converts this user order to a signed CreateAndPlaceOrderRequest.
     * <p>
     * Calculates maker/taker amounts based on side, size, and price,
     * then signs the order using EIP-712 typed data signature.
     *
     * @param owner API key (owner address) for the order.
     * @param context Polymarket context with wallet credentials and chain settings.
     * @return Signed CreateAndPlaceOrderRequest ready to be sent to the API.
     */
    public CreateAndPlaceOrderRequest toCreateAndPlaceOrderRequest(
            String owner,
            PolymarketContext context
    ) {
        // Input validations mimicking Kotlin require statements
        if (owner == null || owner.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner must not be blank");
        }
        if (context.getAddress() == null || context.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address must not be blank");
        }
        if (context.getPrivateKey() == null || context.getPrivateKey().trim().isEmpty()) {
            throw new IllegalArgumentException("Private key must not be blank");
        }
        if (context.getFunderAddress() == null || context.getFunderAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Funder Address must not be blank");
        }

        // Calculate raw amounts
        var amounts = OrderUtils.getOrderRawAmounts(this.side, this.size, this.price, this.tickSize);

        // Instantiate internal Order sub-structure
        CreateAndPlaceOrderRequest.Order rawOrder = new CreateAndPlaceOrderRequest.Order(
                OrderUtils.generateOrderSalt(),
                context.getFunderAddress(),
                context.getAddress(),
                this.assetId,
                AmountUtils.parseUnits(amounts.getRawMakerAmt()),
                AmountUtils.parseUnits(amounts.getRawTakerAmt()),
                this.side,
                this.expiration,
                context.getSignatureType().getIndex(),
                String.valueOf(System.currentTimeMillis()), // timestamp
                Eip712Model.BYTES32_ZERO, // Default metadata
                Eip712Model.BYTES32_ZERO, // Default builder
                "" // Signature starts empty
        );

        // Construct outer envelope wrapper
        CreateAndPlaceOrderRequest request = new CreateAndPlaceOrderRequest(
                false, // default deferExec value
                false, // default postOnly value
                rawOrder,
                owner,
                this.orderType
        );

        // Generate and apply EIP-712 cryptographic signature
        String signature = Eip712Signer.signClobPolymarketOrderTypedData(
                context.getChainId(),
                context.getPrivateKey(),
                rawOrder,
                this.negRisk
        );
        rawOrder.setSignature(signature);

        return request;
    }

    // --- Getters ---

    public String getAssetId() { return assetId; }
    public TradeSide getSide() { return side; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getSize() { return size; }
    public OrderType getOrderType() { return orderType; }
    public String getExpiration() { return expiration; }
    public TickSize getTickSize() { return tickSize; }
    public boolean isNegRisk() { return negRisk; }
}
