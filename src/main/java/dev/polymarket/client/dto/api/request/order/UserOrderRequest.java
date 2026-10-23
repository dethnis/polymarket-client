package dev.polymarket.client.dto.api.request.order;

import dev.polymarket.client.dto.PolymarketContext;
import dev.polymarket.client.enums.OrderType;
import dev.polymarket.client.enums.TradeSide;
import dev.polymarket.client.utils.AmountUtils;
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

    // Full constructor (Primary Constructor)
    public UserOrderRequest(
            String assetId,
            TradeSide side,
            BigDecimal price,
            BigDecimal size,
            OrderType orderType,
            String expiration
    ) {
        // Init block validations
        if (price == null || price.compareTo(new BigDecimal("0.01")) < 0 || price.compareTo(new BigDecimal("1.0")) > 0) {
            throw new IllegalArgumentException("Price must be in range 0.01 to 1.0, but was " + price);
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
    }

    // Overloaded constructor with default orderType (GTC) and expiration ("0")
    public UserOrderRequest(String assetId, TradeSide side, BigDecimal price, BigDecimal size) {
        this(assetId, side, price, size, OrderType.GTC, "0");
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
        var amounts = OrderUtils.getOrderRawAmounts(this.side, this.size, this.price);

        // Instantiate internal Order sub-structure
        CreateAndPlaceOrderRequest.Order rawOrder = new CreateAndPlaceOrderRequest.Order(
                OrderUtils.generateOrderSalt(),
                context.getFunderAddress(),
                context.getAddress(),
                "0x0000000000000000000000000000000000000000", // Default taker
                this.assetId,
                AmountUtils.parseUnits(amounts.getRawMakerAmt()),
                AmountUtils.parseUnits(amounts.getRawTakerAmt()),
                this.expiration,
                String.valueOf(context.getNonce()),
                "0", // Default feeRateBps
                this.side,
                context.getSignatureType().getIndex(),
                "" // Signature starts empty
        );

        // Construct outer envelope wrapper
        CreateAndPlaceOrderRequest request = new CreateAndPlaceOrderRequest(
                false, // default deferExec value
                rawOrder,
                owner,
                this.orderType
        );

        // Generate and apply EIP-712 cryptographic signature
        String signature = Eip712Signer.signClobPolymarketOrderTypedData(
                context.getChainId(),
                context.getPrivateKey(),
                rawOrder
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
}
