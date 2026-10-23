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

    private Eip712Model() {}

    public static final class Type {
        public static final String STRING = "string";
        public static final String UINT256 = "uint256";
        public static final String UINT8 = "uint8";
        public static final String ADDRESS = "address";

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
            return new PolymarketClobOrder(
                    new Domain(chainId),
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
            private final String version = "1";
            private final long chainId;
            private final String verifyingContract = MaticContract.EXCHANGE.getAddress();

            public Domain(long chainId) {
                this.chainId = chainId;
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

            @JsonProperty(ORDER_PRIMARY_TYPE)
            private final List<Type> order = Arrays.asList(
                    new Type("salt", Type.UINT256),
                    new Type("maker", Type.ADDRESS),
                    new Type("signer", Type.ADDRESS),
                    new Type("taker", Type.ADDRESS),
                    new Type("tokenId", Type.UINT256),
                    new Type("makerAmount", Type.UINT256),
                    new Type("takerAmount", Type.UINT256),
                    new Type("expiration", Type.UINT256),
                    new Type("nonce", Type.UINT256),
                    new Type("feeRateBps", Type.UINT256),
                    new Type("side", Type.UINT8),
                    new Type("signatureType", Type.UINT8)
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
            private final String taker;
            private final String tokenId;
            private final String makerAmount;
            private final String takerAmount;
            private final String expiration;
            private final String nonce;
            private final String feeRateBps;
            private final String side;
            private final String signatureType;

            public OrderMessage(String salt, String maker, String signer, String taker, String tokenId,
                    String makerAmount, String takerAmount, String expiration, String nonce,
                    String feeRateBps, String side, String signatureType) {
                this.salt = salt;
                this.maker = maker;
                this.signer = signer;
                this.taker = taker;
                this.tokenId = tokenId;
                this.makerAmount = makerAmount;
                this.takerAmount = takerAmount;
                this.expiration = expiration;
                this.nonce = nonce;
                this.feeRateBps = feeRateBps;
                this.side = side;
                this.signatureType = signatureType;
            }

            // Getters
            public String getSalt() { return salt; }
            public String getMaker() { return maker; }
            public String getSigner() { return signer; }
            public String getTaker() { return taker; }
            public String getTokenId() { return tokenId; }
            public String getMakerAmount() { return makerAmount; }
            public String getTakerAmount() { return takerAmount; }
            public String getExpiration() { return expiration; }
            public String getNonce() { return nonce; }
            public String getFeeRateBps() { return feeRateBps; }
            public String getSide() { return side; }
            public String getSignatureType() { return signatureType; }
        }
    }
}
