package dev.polymarket.client.utils;

import dev.polymarket.client.dto.api.request.order.CreateAndPlaceOrderRequest;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class Eip712Signer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Private constructor to mimic Kotlin's 'object' behavior (no instantiation)
    private Eip712Signer() {}

    public static String signClobPolymarketAuthTypedData(
            long chainId,
            String address,
            long timestamp,
            String privateKey
    ) {
        Object typedData = Eip712Model.PolymarketClobAuth.of(
                chainId,
                address,
                timestamp
        );
        return signTypedData(typedData, privateKey);
    }

    public static String signClobPolymarketOrderTypedData(
            long chainId,
            String privateKey,
            CreateAndPlaceOrderRequest.Order order
    ) {
        return signClobPolymarketOrderTypedData(chainId, privateKey, order, false);
    }

    public static String signClobPolymarketOrderTypedData(
            long chainId,
            String privateKey,
            CreateAndPlaceOrderRequest.Order order,
            boolean negRisk
    ) {
        Eip712Model.PolymarketClobOrder.OrderMessage orderMessage =
                new Eip712Model.PolymarketClobOrder.OrderMessage(
                        String.valueOf(order.getSalt()),
                        order.getMaker(),
                        order.getSigner(),
                        order.getTokenId(),
                        order.getMakerAmount(),
                        order.getTakerAmount(),
                        String.valueOf(order.getSide().getIndex()),
                        String.valueOf(order.getSignatureType()),
                        order.getTimestamp(),
                        order.getMetadata(),
                        order.getBuilder()
                );

        Object typedData = Eip712Model.PolymarketClobOrder.of(chainId, negRisk, orderMessage);
        return signTypedData(typedData, privateKey);
    }

    private static <T> String signTypedData(T typedData, String privateKey) {
        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(typedData);
        } catch (JacksonException e) {
            // Jackson 3 throws unchecked JacksonException instead of JsonProcessingException
            throw new RuntimeException("Failed to serialize typed data to JSON", e);
        }

        try {
            Sign.SignatureData signature = Sign.signTypedData(
                    jsonData,
                    Credentials.create(privateKey).getEcKeyPair()
            );

            byte[] retval = new byte[65];
            System.arraycopy(signature.getR(), 0, retval, 0, 32);
            System.arraycopy(signature.getS(), 0, retval, 32, 32);
            System.arraycopy(signature.getV(), 0, retval, 64, 1);

            return Numeric.toHexString(retval);
        } catch (IOException e) {
            // Includes web3j's own (Jackson 2) parsing of the typed data JSON
            throw new RuntimeException("Failed to signTypedData", e);
        }
    }
}
