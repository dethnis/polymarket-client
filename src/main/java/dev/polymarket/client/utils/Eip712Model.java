package dev.polymarket.client.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.polymarket.client.enums.MaticContract;
import java.util.Arrays;
import java.util.List;

public final class Eip712Model {

    public static final long NONCE = 0L;
    public static final String EIP712_DOMAIN = "EIP712Domain";
    public static final String AUTH_PRIMARY_TYPE = "ClobAuth";
    public static final String ORDER_PRIMARY_TYPE = "Order";
    public static final String BYTES32_ZERO = "0x0000000000000000000000000000000000000000000000000000000000000000";

    private Eip712Model() {}

    public static final class Type {
        public static final String STRING = "string";
        public static final String UINT256 = "uint256";
        public static final String UINT8 = "uint8";
        public static final String ADDRESS = "address";
        public static final String BYTES32 = "bytes32";

        private final String name;
        private final String type;

        public Type(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() { return name; }
        public String getType() { return type; }
    }

    public static final class PolymarketClobAuth {
        private final Domain domain;
        private final Types types;
        private final String primaryType;
        private final Message message;

        public PolymarketClobAuth(Domain domain, Types types, String primaryType, Message message) {
            this.domain = domain;
            this.types = types;
            this.primaryType = primaryType;
            this.message = message;
        }

        public static PolymarketClobAuth of(long chainId, String address, long timestamp) {
            return new PolymarketClobAuth(
                    new Domain(chainId),
                    new Types(),
                    AUTH_PRIMARY_TYPE,
                    new Message(address, String.valueOf(timestamp))
            );
        }

        // Getters
        public Domain getDomain() { return domain; }
        public Types getTypes() { return types; }
        public String getPrimaryType() { return primaryType; }
        public Message getMessage() { return message; }

        public static final class Domain {
            private final String name = "ClobAuthDomain";
            private final String version = "1";
            private final long chainId;

            public Domain(long chainId) {
                this.chainId = chainId;
            }

            public String getName() { return name; }
            public String getVersion() { return version; }
            public long getChainId() { return chainId; }
        }

        public static final class Types {
            @JsonProperty(EIP712_DOMAIN)
            private final List<Type> eip712Domain = Arrays.asList(
                    new Type("name", Type.STRING),
                    new Type("version", Type.STRING),
                    new Type("chainId", Type.UINT256)
            );

            @JsonProperty(AUTH_PRIMARY_TYPE)
            private final List<Type> clobAuth = Arrays.asList(
                    new Type("address", Type.ADDRESS),
                    new Type("timestamp", Type.STRING),
                    new Type("nonce", Type.UINT256),
                    new Type("message", Type.STRING)
            );

            @JsonProperty(EIP712_DOMAIN)
            public List<Type> getEip712Domain() { return eip712Domain; }

            @JsonProperty(AUTH_PRIMARY_TYPE)
            public List<Type> getClobAuth() { return clobAuth; }
        }

        public static final class Message {
            private final String address;
            private final String timestamp;
            private final long nonce = NONCE;
            private final String message = "This message attests that I control the given wallet";

            public Message(String address, String timestamp) {
                this.address = address;
                this.timestamp = timestamp;
            }

            public String getAddress() { return address; }
            public String getTimestamp() { return timestamp; }
            public long getNonce() { return nonce; }
            public String getMessage() { return message; }
        }
    }

    public static final class PolymarketClobOrder {
        private final String primaryType = ORDER_PRIMARY_TYPE;
        private final Domain domain;
        private final Types types;
        private final OrderMessage message;

        public PolymarketClobOrder(Domain domain, Types types, OrderMessage message) {
            this.domain = domain;
            this.types = types;
            this.message = message;
        }

        public static PolymarketClobOrder of(long chainId, OrderMessage order) {
            return of(chainId, false, order);
        }

        /**
         * Neg-risk markets are settled by a separate exchange contract, so orders for them
         * must be signed against NEG_RISK_EXCHANGE instead of EXCHANGE.
         */
        public static PolymarketClobOrder of(long chainId, boolean negRisk, OrderMessage order) {
            MaticContract exchange = negRisk ? MaticContract.NEG_RISK_EXCHANGE : MaticContract.EXCHANGE;
            return new PolymarketClobOrder(
                    new Domain(chainId, exchange.getAddress()),
                    new Types(),
                    order
            );
        }

        // Getters
        public String getPrimaryType() { return primaryType; }
        public Domain getDomain() { return domain; }
        public Types getTypes() { return types; }
        public OrderMessage getMessage() { return message; }

        public static final class Domain {
            private final String name = "Polymarket CTF Exchange";
            // CLOB V2 (since 2026-04-28); V1-signed orders are rejected
            private final String version = "2";
            private final long chainId;
            private final String verifyingContract;

            public Domain(long chainId) {
                this(chainId, MaticContract.EXCHANGE.getAddress());
            }

            public Domain(long chainId, String verifyingContract) {
                this.chainId = chainId;
                this.verifyingContract = verifyingContract;
            }

            public String getName() { return name; }
            public String getVersion() { return version; }
            public long getChainId() { return chainId; }
            public String getVerifyingContract() { return verifyingContract; }
        }

        public static final class Types {
            @JsonProperty(EIP712_DOMAIN)
            private final List<Type> eip712Domain = Arrays.asList(
                    new Type("name", Type.STRING),
                    new Type("version", Type.STRING),
                    new Type("chainId", Type.UINT256),
                    new Type("verifyingContract", Type.ADDRESS)
            );

            // Field order must match the CTF Exchange V2 contract's Order struct
            @JsonProperty(ORDER_PRIMARY_TYPE)
            private final List<Type> order = Arrays.asList(
                    new Type("salt", Type.UINT256),
                    new Type("maker", Type.ADDRESS),
                    new Type("signer", Type.ADDRESS),
                    new Type("tokenId", Type.UINT256),
                    new Type("makerAmount", Type.UINT256),
                    new Type("takerAmount", Type.UINT256),
                    new Type("side", Type.UINT8),
                    new Type("signatureType", Type.UINT8),
                    new Type("timestamp", Type.UINT256),
                    new Type("metadata", Type.BYTES32),
                    new Type("builder", Type.BYTES32)
            );

            @JsonProperty(EIP712_DOMAIN)
            public List<Type> getEip712Domain() { return eip712Domain; }

            @JsonProperty(ORDER_PRIMARY_TYPE)
            public List<Type> getOrder() { return order; }
        }

        public static final class OrderMessage {
            private final String salt;
            private final String maker;
            private final String signer;
            private final String tokenId;
            private final String makerAmount;
            private final String takerAmount;
            private final String side;
            private final String signatureType;
            private final String timestamp;
            private final String metadata;
            private final String builder;

            public OrderMessage(String salt, String maker, String signer, String tokenId,
                    String makerAmount, String takerAmount, String side, String signatureType,
                    String timestamp, String metadata, String builder) {
                this.salt = salt;
                this.maker = maker;
                this.signer = signer;
                this.tokenId = tokenId;
                this.makerAmount = makerAmount;
                this.takerAmount = takerAmount;
                this.side = side;
                this.signatureType = signatureType;
                this.timestamp = timestamp;
                this.metadata = metadata;
                this.builder = builder;
            }

            // Getters
            public String getSalt() { return salt; }
            public String getMaker() { return maker; }
            public String getSigner() { return signer; }
            public String getTokenId() { return tokenId; }
            public String getMakerAmount() { return makerAmount; }
            public String getTakerAmount() { return takerAmount; }
            public String getSide() { return side; }
            public String getSignatureType() { return signatureType; }
            public String getTimestamp() { return timestamp; }
            public String getMetadata() { return metadata; }
            public String getBuilder() { return builder; }
        }
    }
}
