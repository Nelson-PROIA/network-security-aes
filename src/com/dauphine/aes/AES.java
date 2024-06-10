package com.dauphine.aes;

/**
 * <p>
 * Implementation of a simplified AES encryption algorithm.
 * This class provides methods for encryption (ciphering) and decryption (deciphering) using AES.
 * </p>
 *
 * <p>
 * It includes definitions for the S-box, inverse S-box, mix matrix, and inverse mix matrix.
 * </p>
 *
 * @author Ricardo BOKA {@literal <ricardo.boka@dauphine.eu>}
 * @see Block
 * @see Key
 * @see SBox
 * @see State
 */
public class AES {

    /**
     * The number of rounds in the AES encryption process.
     */
    public final static int NUMBER_ROUNDS = 3;

    /**
     * The number of blocks in the key.
     */
    public final static int NUMBER_BLOCKS = 2;

    /**
     * The S-box for byte substitution during encryption.
     */
    private static final int[][] sBoxValues = {
            {0x9, 0x4, 0xA, 0xB},
            {0xD, 0x1, 0x8, 0x5},
            {0x6, 0x2, 0x0, 0x3},
            {0xC, 0xE, 0xF, 0x7}
    };

    /**
     * The inverse S-box for byte substitution during decryption.
     */
    private static final int[][] sBoxInvertValues = {
            {0xA, 0x5, 0x9, 0xB},
            {0x1, 0x7, 0x8, 0xF},
            {0x6, 0x0, 0x2, 0x3},
            {0xC, 0x4, 0xD, 0xE}
    };

    /**
     * The matrix used for the mix columns step during encryption.
     */
    private static final int[][] stateValues = {
            {1, 4},
            {4, 1}
    };

    /**
     * The matrix used for the mix columns step during decryption.
     */
    private static final int[][] stateInvertValues = {
            {9, 2},
            {2, 9}
    };

    /**
     * The array of round keys derived from the original key.
     *
     * @see Key
     */
    private final Key[] keys;

    /**
     * The S-box used for substitution during encryption.
     *
     * @see SBox
     */
    private final SBox sBox;

    /**
     * The inverse S-box used for substitution during decryption.
     *
     * @see SBox
     */
    private final SBox sBoxInvert;

    /**
     * The state for the mix columns step during encryption.
     *
     * @see State
     */
    private final State state;

    /**
     * The state for the mix columns step during decryption.
     *
     * @see State
     */
    private final State stateInvert;

    /**
     * Constructs an AES instance with the given key.
     * Initializes the S-boxes, mix states, and generates the round keys.
     *
     * @param key The Block representing the key.
     * @see Block
     * @see Key
     * @see SBox
     * @see State
     */
    public AES(Block key) {
        sBox = new SBox(sBoxValues);
        sBoxInvert = new SBox(sBoxInvertValues);

        state = new State(stateValues);
        stateInvert = new State(stateInvertValues);

        Key temp = new Key(key);
        keys = temp.generateSubKeys(sBox);
    }

    /**
     * Ciphers the given plain text block.
     *
     * @param plain The Block representing the plain block.
     * @return The encrypted Block.
     * @see Block
     * @see Key
     * @see State
     */
    public Block cipher(Block plain) {
        State cipher = new State(plain);

        int round = 0;
        cipher = cipher.XOR(keys[round]);

        for (round = 1; round < NUMBER_ROUNDS - 1; ++round) {
            cipher = cipher.substitute(sBox);
            cipher = cipher.shift();
            cipher = cipher.multiply(state);
            cipher = cipher.XOR(keys[round]);
        }

        cipher = cipher.substitute(sBox);
        cipher = cipher.shift();
        cipher = cipher.XOR(keys[round]);

        return cipher.toBlock();
    }

    /**
     * Deciphers the given cipher text block.
     *
     * @param cipher The Block representing the cipher text.
     * @return The decrypted Block.
     * @see Block
     * @see Key
     * @see State
     */
    public Block decipher(Block cipher) {
        State decipher = new State(cipher);

        int round = NUMBER_ROUNDS - 1;
        decipher = decipher.XOR(keys[round]);

        for (round = NUMBER_ROUNDS - 2; round > 0; --round) {
            decipher = decipher.shiftInvert();
            decipher = decipher.substitute(sBoxInvert);
            decipher = decipher.XOR(keys[round]);
            decipher = decipher.multiply(stateInvert);
        }

        decipher = decipher.shiftInvert();
        decipher = decipher.substitute(sBoxInvert);
        decipher = decipher.XOR(keys[round]);

        return decipher.toBlock();
    }

    /**
     * The main method to test the AES implementation.
     * Encrypts and decrypts a sample plain text and compares the result.
     *
     * @param args Command-line arguments (not used).
     * @see Block
     */
    public static void main(String[] args) {
        String plain = "0110111101101011";
        String key = "0010110101010101";

        Block plainBlock = new Block(plain);
        Block keyBlock = new Block(key);

        AES aes = new AES(keyBlock);

        Block cipherBlock = aes.cipher(plainBlock);
        Block decipherBlock = aes.decipher(cipherBlock);

        System.out.println("SBox : \n" + aes.sBox);
        System.out.println("SBoxInvert : \n" + aes.sBoxInvert);
        System.out.println("State : \n" + aes.state);
        System.out.println("StateInvert : \n" + aes.stateInvert);

        System.out.println("Plain : " + plainBlock);
        System.out.println("Key : " + key);
        System.out.println();

        System.out.println("Cipher block : " + cipherBlock);
        System.out.println("Decipher block : " + decipherBlock);
        System.out.println();

        System.out.println("[TEST] Decipher block and plain are" + (decipherBlock.toString().compareTo(plain) == 0 ? " " : " not ") + "equal!");
    }

}
