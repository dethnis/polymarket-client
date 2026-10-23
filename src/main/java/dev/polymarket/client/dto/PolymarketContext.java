package dev.polymarket.client.dto;

import dev.polymarket.client.api.interceptor.AuthL1Interceptor;
import dev.polymarket.client.enums.SignatureType;
import dev.polymarket.client.enums.system.SystemEnv;
import dev.polymarket.client.utils.Eip712Model;

/**
 * Configuration context for Polymarket client.
 * <p>
 * Contains wallet credentials and chain settings required for authentication and signing.
 * Default values are loaded from environment variables via SystemEnv.
 */
public class PolymarketContext {

    private final long chainId;
    private final String address;
    private final String privateKey;
    private final long nonce;
    private final String funderAddress;
    private final SignatureType signatureType;

    // Full constructor (Primary Constructor)
    public PolymarketContext(
            long chainId,
            String address,
            String privateKey,
            long nonce,
            String funderAddress,
            SignatureType signatureType
    ) {
        this.chainId = chainId;
        this.address = address;
        this.privateKey = privateKey;
        this.nonce = nonce;
        this.funderAddress = funderAddress;
        this.signatureType = signatureType;
    }

    // Default constructor replicating all Kotlin default argument values
    public PolymarketContext() {
        this(
                Long.parseLong(SystemEnv.CHAIN_ID.getEnvOrDefault()),
                SystemEnv.POLYGON_ADDRESS_WALLET.getEnvOrDefault(),
                SystemEnv.PRIVATE_KEY_WALLET.getEnvOrDefault(),
                Eip712Model.NONCE,
                SystemEnv.FUNDER_ADDRESS.getEnvOrDefault(),
                SignatureType.POLY_GNOSIS_SAFE
        );
    }

    /**
     * Creates an AuthL1Interceptor from this context.
     *
     * @return Configured L1 authentication interceptor for OkHttp.
     */
    public AuthL1Interceptor toAuthL1Interceptor() {
        return new AuthL1Interceptor(
                this.chainId,
                this.address,
                this.privateKey,
                this.nonce
        );
    }

    // --- Getters ---

    public long getChainId() { return chainId; }
    public String getAddress() { return address; }
    public String getPrivateKey() { return privateKey; }
    public long getNonce() { return nonce; }
    public String getFunderAddress() { return funderAddress; }
    public SignatureType getSignatureType() { return signatureType; }
}
