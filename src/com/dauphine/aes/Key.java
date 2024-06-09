package com.dauphine.aes;

/**
 * Represents a cryptographic key for AES encryption.
 * This class provides methods for generating and manipulating AES keys.
 *
 * @author Sébastien GIRET-IHMAUS {@literal <sebastien.giret-ihmaus@dauphine.eu>}
 * @author Nelson PROIA {@literal <nelson.proia@dauphine.eu>}
 */
public class Key {

    /**
     * Array of blocks representing the key.
     *
     * @see Block
     */
    private final Block[] bytes;

    /**
     * Constructs a Key object with default block values.
     *
     * @see Block
     */
    public Key() {
        bytes = new Block[4];

        for (int i = 0; i < 4; i++) {
            bytes[i] = new Block(32);
        }
    }

    /**
     * Constructs a Key object from a single block.
     *
     * @param block The block to construct the Key from.
     * @see Block
     */
    public Key(Block block) {
        bytes = new Block[4];

        for (int i = 0; i < 4; i++) {
            bytes[i] = block.getSegment(4, i);
        }
    }

    /**
     * Constructs a Key object from an array of blocks.
     *
     * @param blocks The array of blocks to construct the Key from.
     * @see Block
     */
    public Key(Block[] blocks) {
        bytes = new Block[4];

        for (int i = 0; i < 4; i++) {
            bytes[i] = blocks[i].clone();
        }
    }

    /**
     * Constructs a Key object from another Key object (copy constructor).
     *
     * @param key The Key object to copy.
     */
    public Key(Key key) {
        this(key.bytes);
    }

    /**
     * Gets a specific block element from the key.
     *
     * @param i The row index of the block.
     * @param j The column index of the block.
     * @return The block element.
     * @see Block
     */
    public Block element(int i, int j) {
        return bytes[i].getSegment(4, j);
    }

    /**
     * Generates sub-keys using the key and the provided S-box.
     *
     * @param sBox The S-box used in key expansion.
     * @return An array of sub-keys.
     * @see SBox
     * @see Block
     */
    public Key[] genSubKeys(SBox sBox) {
        Block roundConstant = new Block(8, 1);
        Key[] subKeys = new Key[11];
        subKeys[0] = new Key(this);

        for (int i = 1; i <= 10; ++i) {
            Block[] nextKeyBlocks = new Block[4];
            Block gApplied = subKeys[i - 1].bytes[bytes.length - 1].g(sBox, roundConstant);

            nextKeyBlocks[0] = subKeys[i - 1].bytes[0].xOr(gApplied);

            for (int j = 1; j < 4; ++j) {
                nextKeyBlocks[j] = subKeys[i - 1].bytes[j].xOr(nextKeyBlocks[j - 1]);
            }

            subKeys[i] = new Key(nextKeyBlocks);
            roundConstant = roundConstant.modularMultiplicationByX();
        }

        return subKeys;
    }

    /**
     * Returns a string representation of the key.
     *
     * @return The string representation.
     * @see Block
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Block block : bytes) {
            result.append(block.toString()).append(" ");
        }

        return result.toString();
    }

    /**
     * Returns a hexadecimal string representation of the key.
     *
     * @return The hexadecimal string representation.
     * @see Block
     */
    public String toHexadecimalString() {
        StringBuilder result = new StringBuilder();

        for (Block block : bytes) {
            result.append(block.toHexadecimalString()).append(" ");
        }

        return result.toString();
    }

}
