package dev.polymarket.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.polymarket.client.dto.api.request.order.CreateAndPlaceOrderRequest;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
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
        Eip712Model.PolymarketClobOrder.OrderMessage orderMessage =
                new Eip712Model.PolymarketClobOrder.OrderMessage(
                        String.valueOf(order.getSalt()),
                        order.getMaker(),
                        order.getSigner(),
                        order.getTaker(),
                        order.getTokenId(),
                        order.getMakerAmount(),
                        order.getTakerAmount(),
                        order.getExpiration(),
                        order.getNonce(),
                        order.getFeeRateBps(),
                        String.valueOf(order.getSide().getIndex()),
                        String.valueOf(order.getSignatureType())
                );

        Object typedData = Eip712Model.PolymarketClobOrder.of(chainId, orderMessage);
        return signTypedData(typedData, privateKey);
    }

    private static <T> String signTypedData(T typedData, String privateKey) {
        try {
            String jsonData = objectMapper.writeValueAsString(typedData);
            Sign.SignatureData signature = Sign.signTypedData(
                    jsonData,
                    Credentials.create(privateKey).getEcKeyPair()
            );

            byte[] retval = new byte[65];
            System.arraycopy(signature.getR(), 0, retval, 0, 32);
            System.arraycopy(signature.getS(), 0, retval, 32, 32);
            System.arraycopy(signature.getV(), 0, retval, 64, 1);

            return Numeric.toHexString(retval);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize typed data to JSON", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to signTypedData", e);
        }
    }
}
