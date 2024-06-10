package com.dauphine.aes;

import java.util.Arrays;
import java.util.Locale;

/**
 * <p>
 * Represents a block of binary data. This class provides methods for creating,
 * manipulating, and converting binary data blocks.
 * </p>
 *
 * <p>
 * This implementation supports various operations like XOR, left shift,
 * modular multiplication, and segment extraction.
 * </p>
 *
 * @author Sébastien GIRET-IHMAUS {@literal <sebastien.giret-ihmaus@dauphine.eu>}
 * @author Nelson PROIA {@literal <nelson.proia@dauphine.eu>}
 * @see AES
 * @see Key
 * @see SBox
 */
public class Block implements Cloneable {

    /**
     * Represents the generator polynomial used in AES key expansion.
     * This polynomial is used in certain key expansion operations.
     */
    public final static Block GENERATOR_POLYNOMIAL = new Block("00011011");

    /**
     * The array of bits representing the binary data.
     */
    public boolean[] bits;

    /**
     * Constructs a block of binary data with the specified size.
     *
     * @param size The size of the block.
     */
    public Block(int size) {
        bits = new boolean[size];
    }

    /**
     * Constructs a block of binary data with the specified size and value.
     *
     * @param size  The size of the block.
     * @param value The value to initialize the block with.
     */
    public Block(int size, int value) {
        this(size);

        for (int i = size - 1; i >= 0; --i) {
            bits[i] = (value % 2) == 1;
            value /= 2;
        }
    }

    /**
     * Constructs a block of binary data from a binary string.
     *
     * @param bits The binary string representing the block.
     */
    public Block(String bits) {
        this(bits.length());

        for (int i = 0; i < bits.length(); ++i) {
            this.bits[i] = bits.charAt(i) == '1';
        }
    }

    /**
     * Constructs a block of binary data from a boolean array.
     *
     * @param bits The boolean array representing the block.
     */
    public Block(boolean[] bits) {
        this.bits = bits.clone();
    }

    /**
     * Constructs a block of binary data from an array of blocks.
     *
     * @param blocks The array of blocks to construct the block from.
     */
    public Block(Block[] blocks) {
        int size = Arrays.stream(blocks).mapToInt(block -> block.bits.length).sum();
        bits = new boolean[size];

        int index = 0;

        for (Block block : blocks) {
            System.arraycopy(block.bits, 0, bits, index, block.bits.length);
            index += block.bits.length;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a deep copy of the block.
     */
    @Override
    public Block clone() {
        try {
            Block clone = (Block) super.clone();
            clone.bits = bits.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a segment of the block.
     *
     * @param numberSegments The number of segments.
     * @param index          The index of the segment.
     * @return The segment block.
     */
    public Block getSegment(int numberSegments, int index) {
        int segmentLength = bits.length / numberSegments;
        boolean[] segment = new boolean[segmentLength];

        System.arraycopy(bits, index * segmentLength, segment, 0, segmentLength);

        return new Block(segment);
    }

    /**
     * Retrieves the decimal value of the row index from the block.
     * The row index is obtained from the first segment of the block.
     *
     * @return The decimal value of the row index.
     */
    public int rowValue() {
        return getSegment(2, 0).toDecimal();
    }

    /**
     * Retrieves the decimal value of the column index from the block.
     * The column index is obtained from the second segment of the block.
     *
     * @return The decimal value of the column index.
     */
    public int columnValue() {
        return getSegment(2, 1).toDecimal();
    }

    /**
     * Performs an exclusive OR (XOR) operation between two blocks.
     *
     * @param other The other block to perform XOR with.
     * @return The resulting block.
     */
    public Block xOr(Block other) {
        boolean[] resultBits = new boolean[bits.length];

        for (int i = 0; i < bits.length; ++i) {
            resultBits[i] = bits[i] ^ other.bits[i];
        }

        return new Block(resultBits);
    }

    /**
     * Performs a left shift operation on the block.
     *
     * @return The resulting block after left shift.
     */
    public Block leftShift() {
        boolean[] shiftedBits = new boolean[bits.length];

        System.arraycopy(bits, 1, shiftedBits, 0, bits.length - 1);
        shiftedBits[bits.length - 1] = false;

        return new Block(shiftedBits);
    }

    /**
     * Performs modular multiplication by X on the block.
     *
     * @return The resulting block after modular multiplication.
     */
    public Block modularMultiplicationByX() {
        return bits[0] ? leftShift().xOr(GENERATOR_POLYNOMIAL) : leftShift();
    }

    /**
     * Performs modular multiplication operation between two blocks.
     *
     * @param other The other block to perform multiplication with.
     * @return The resulting block after multiplication.
     */
    public Block modularMultiplication(Block other) {
        Block result = new Block(bits.length);
        Block multiplier = clone();

        for (int i = other.bits.length - 1; i >= 0; --i) {
            if (other.bits[i]) {
                result = result.xOr(multiplier);
            }

            multiplier = multiplier.modularMultiplicationByX();
        }

        return result;
    }

    /**
     * Performs the 'g' operation on the block.
     *
     * @param sbox          The S-box used in the operation.
     * @param roundConstant The round constant used in the operation.
     * @return The resulting block after the 'g' operation.
     * @see AES
     * @see SBox
     */
    public Block g(SBox sbox, Block roundConstant) {
        Block[] subBlocks = new Block[AES.NUMBER_BLOCKS];

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            subBlocks[i] = sbox.cipher(getSegment(AES.NUMBER_BLOCKS, (i + 1) % AES.NUMBER_BLOCKS));
        }

        Block newBlock = new Block(subBlocks);
        roundConstant = new Block(roundConstant.toString() + "0".repeat(AES.NUMBER_BLOCKS * 6));

        return newBlock.xOr(roundConstant);
    }

    /**
     * Converts a string to an array of blocks with the specified block size.
     *
     * @param string    The string to convert.
     * @param blockSize The size of each block.
     * @return An array of blocks.
     * @see AES
     */
    public static Block[] stringToBlocks(String string, int blockSize) {
        int numberBlocks = string.length() / blockSize;
        Block[] result = new Block[numberBlocks];

        for (int i = 0; i < numberBlocks; ++i) {
            Block[] temp = new Block[blockSize];

            for (int j = 0; j < blockSize; ++j) {
                Block byteBlock = new Block(AES.NUMBER_BLOCKS * 2);
                char ch = string.charAt(i * blockSize + j);

                for (int k = 0; k < AES.NUMBER_BLOCKS * 2; ++k) {
                    byteBlock.bits[(AES.NUMBER_BLOCKS * 2 - 1) - k] = (ch & (1 << k)) != 0;
                }

                temp[j] = byteBlock;
            }

            result[i] = new Block(temp);
        }

        return result;
    }

    /**
     * Converts an array of blocks to a string.
     *
     * @param blocks The array of blocks to convert.
     * @return The resulting string.
     * @see AES
     */
    public static String blocksToString(Block[] blocks) {
        StringBuilder result = new StringBuilder();

        for (Block block : blocks) {
            int numberBytes = block.bits.length / (AES.NUMBER_BLOCKS * 2);

            for (int i = 0; i < numberBytes; ++i) {
                char value = 0;

                for (int j = 0; j < (AES.NUMBER_BLOCKS * 2); ++j) {
                    if (block.bits[i * (AES.NUMBER_BLOCKS * 2) + j]) {
                        value |= (char) (1 << j);
                    }
                }

                result.append(value);
            }
        }

        return result.toString();
    }

    /**
     * Converts the block to its decimal representation.
     *
     * @return The decimal representation of the block.
     */
    public int toDecimal() {
        int decimalValue = 0;

        for (boolean bit : bits) {
            decimalValue = (decimalValue << 1) | (bit ? 1 : 0);
        }

        return decimalValue;
    }

    /**
     * Converts the block to a hexadecimal string.
     *
     * @return The hexadecimal string representation of the block.
     */
    public String toHexadecimalString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < this.bits.length; i += 4) {
            int val = (this.bits[i] ? 8 : 0) + (this.bits[i + 1] ? 4 : 0) + (this.bits[i + 2] ? 2 : 0) + (this.bits[i + 3] ? 1 : 0);

            stringBuilder.append(Integer.toHexString(val).toUpperCase(Locale.ROOT));
        }

        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Converts the block to a string of '0's and '1's representing its bits.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(bits.length);

        for (boolean bit : bits) {
            stringBuilder.append(bit ? '1' : '0');
        }

        return stringBuilder.toString();
    }

}
