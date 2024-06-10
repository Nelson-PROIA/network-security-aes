package com.dauphine.aes;

/**
 * <p>
 * Represents a cryptographic key for AES encryption.
 * This class provides methods for generating and manipulating AES keys.
 * </p>
 *
 * <p>
 * This implementation supports key expansion using the provided S-box and
 * can generate sub-keys for AES rounds.
 * </p>
 *
 * @author Sébastien GIRET-IHMAUS {@literal <sebastien.giret-ihmaus@dauphine.eu>}
 * @author Nelson PROIA {@literal <nelson.proia@dauphine.eu>}
 * @see AES
 * @see Block
 * @see Key
 * @see SBox
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
     * Initializes the array of blocks and fills it with new blocks
     * of a size determined by {@code BIT * 8}.
     *
     * @see AES
     * @see Block
     */
    public Key() {
        bytes = new Block[AES.NUMBER_BLOCKS];

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            bytes[i] = new Block(AES.NUMBER_BLOCKS * 8);
        }
    }

    /**
     * Constructs a Key object from a single block.
     * Splits the block into segments to form the key.
     *
     * @param block The block to construct the Key from.
     * @see AES
     * @see Block
     */
    public Key(Block block) {
        bytes = new Block[AES.NUMBER_BLOCKS];

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            bytes[i] = block.getSegment(AES.NUMBER_BLOCKS, i);
        }
    }

    /**
     * Constructs a Key object from an array of blocks.
     * Clones each block from the provided array.
     *
     * @param blocks The array of blocks to construct the Key from.
     * @see AES
     * @see Block
     */
    public Key(Block[] blocks) {
        bytes = new Block[AES.NUMBER_BLOCKS];

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            bytes[i] = blocks[i].clone();
        }
    }

    /**
     * Constructs a Key object from another Key object (copy constructor).
     *
     * @param key The Key object to copy.
     * @see Block
     */
    public Key(Key key) {
        this(key.bytes);
    }

    /**
     * Gets a specific block element from the key.
     *
     * @param i The row index of the block.
     * @param j The column index of the block.
     * @return The block element at the specified indices.
     * @see AES
     * @see Block
     */
    public Block element(int i, int j) {
        return bytes[i].getSegment(AES.NUMBER_BLOCKS, j);
    }

    /**
     * Generates sub-keys using the key and the provided S-box.
     *
     * @param sBox The S-box used in key expansion.
     * @return An array of sub-keys.
     * @see AES
     * @see Block
     * @see Key
     * @see SBox
     */
    public Key[] genSubKeys(SBox sBox) {
        Block roundConstant = new Block(AES.NUMBER_BLOCKS * 2, (int) Math.pow(2, ((AES.NUMBER_BLOCKS * 2) - 1)));
        Key[] subKeys = new Key[AES.NUMBER_ROUNDS];
        subKeys[0] = new Key(this);

        for (int i = 1; i <= (AES.NUMBER_ROUNDS - 1); ++i) {
            Block[] nextKeyBlocks = new Block[AES.NUMBER_BLOCKS];
            Block gApplied = subKeys[i - 1].bytes[bytes.length - 1].g(sBox, roundConstant);

            nextKeyBlocks[0] = subKeys[i - 1].bytes[0].xOr(gApplied);

            for (int j = 1; j < AES.NUMBER_BLOCKS; ++j) {
                nextKeyBlocks[j] = subKeys[i - 1].bytes[j].xOr(nextKeyBlocks[j - 1]);
            }

            subKeys[i] = new Key(nextKeyBlocks);
            roundConstant = roundConstant.modularMultiplicationByX();
        }

        return subKeys;
    }

    /**
     * Returns a hexadecimal string representation of the key.
     *
     * @return The hexadecimal string representation of the key.
     * @see Block
     */
    public String toHexadecimalString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Block block : bytes) {
            stringBuilder.append(block.toHexadecimalString()).append(" ");
        }

        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @return The string representation of the key.
     * @see Block
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Block block : bytes) {
            stringBuilder.append(block.toString()).append(" ");
        }

        return stringBuilder.toString();
    }

}
