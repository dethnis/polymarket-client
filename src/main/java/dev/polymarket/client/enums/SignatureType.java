package dev.polymarket.client.enums;

public enum SignatureType {
    /**
     * ECDSA EIP712 signatures signed by EOAs.
     * Standard Ethereum wallet (MetaMask). Funder is the EOA address and will need POL to pay gas on transactions.
     */
    EOA(0),

    /**
     * EIP712 signatures signed by EOAs that own Polymarket Proxy wallets.
     * A custom proxy wallet only used with users who logged in via Magic Link email/Google.
     * Using this requires the user to have exported their PK from Polymarket.com and imported into your app.
     */
    POLY_PROXY(1),

    /**
     * EIP712 signatures signed by EOAs that own Polymarket Gnosis safes.
     * Gnosis Safe multisig proxy wallet (most common).
     * Use this for any new or returning user who does not fit the other 2 types.
     */
    POLY_GNOSIS_SAFE(2);

    private final int index;

    // Constructor
    SignatureType(int index) {
        this.index = index;
    }

    // Getter
    public int getIndex() {
        return this.index;
    }
}