package com.dauphine.aes;

import java.util.Arrays;

/**
 * Represents a block of binary data.
 * This class provides methods for creating, manipulating, and converting binary data blocks.
 *
 * @author Sébastien GIRET-IHMAUS {@literal <sebastien.giret-ihmaus@dauphine.eu>}
 * @author Nelson PROIA {@literal <nelson.proia@dauphine.eu>}
 */
public class Block implements Cloneable {

    /**
     * Represents the generator polynomial used in AES key expansion.
     * This polynomial is used in certain key expansion operations.
     *
     * @see Block
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
        this.bits = new boolean[size];
    }

    /**
     * Constructs a block of binary data with the specified size and value.
     *
     * @param size  The size of the block.
     * @param value The value to initialize the block with.
     */
    public Block(int size, int value) {
        // TODO
        this(size);

        for(int i = size - 1; i > -1; i--) {
            this.bits[i] = ((value % 2) == 1);
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

        for (int i = 0; i < bits.length(); i++) {
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
        int size = 0;

        for (Block block : blocks) {
            size += block.bits.length;
        }

        this.bits = new boolean[size];

        int index = 0;

        for (Block block : blocks) {
            for (boolean bit : block.bits) {
                this.bits[index++] = bit;
            }
        }
    }

    /**
     * Converts the block to its decimal representation.
     *
     * @return The decimal representation of the block.
     */
    public int toDecimal() {
        int decimalValue = 0;

        for (boolean bit : this.bits) {
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
        StringBuilder result = new StringBuilder(this.bits.length / 4);

        for (int i = 0; i < this.bits.length; i += 4) {
            int val = 0;
            for (int j = 0; j < 4; j++) {
                if (this.bits[i + j]) {
                    val += 1 << (3 - j);
                }
            }

            if (val < 10) {
                result.append(val);
            } else {
                result.append((char) ('A' + val - 10));
            }
        }

        return result.toString();
    }

    /**
     * Gets a segment of the block.
     *
     * @param numberSegments The number of segments.
     * @param index          The index of the segment.
     * @return The segment block.
     */
    public Block getSegment(int numberSegments, int index) {
        int segmentLength = this.bits.length / numberSegments;
        boolean[] segment = new boolean[segmentLength];

        System.arraycopy(this.bits, index * segmentLength, segment, 0, segmentLength);

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
        boolean[] resultBits = new boolean[this.bits.length];

        for (int i = 0; i < this.bits.length; ++i) {
            resultBits[i] = this.bits[i] ^ other.bits[i];
        }

        return new Block(resultBits);
    }

    /**
     * Performs a left shift operation on the block.
     *
     * @return The resulting block after left shift.
     */
    public Block leftShift() {
        boolean[] shiftedBits = new boolean[this.bits.length];

        System.arraycopy(this.bits, 1, shiftedBits, 0, this.bits.length - 1);
        shiftedBits[this.bits.length - 1] = false;

        return new Block(shiftedBits);
    }

    /**
     * Performs modular multiplication by X on the block.
     *
     * @return The resulting block after modular multiplication.
     */
    public Block modularMultiplicationByX() {
        if (!this.bits[0]) {
            return this.leftShift();
        } else {
            return this.leftShift().xOr(GENERATOR_POLYNOMIAL);
        }
    }

    /**
     * Performs modular multiplication operation between two blocks.
     *
     * @param other The other block to perform multiplication with.
     * @return The resulting block after multiplication.
     */
    public Block modularMultiplication(Block other) {
        System.out.println();
        Block result = new Block(this.bits.length);
        Block multiplier = this.clone();

        System.out.println("result = " + result);
        System.out.println("multiplier = " + multiplier);

        for (int i = other.bits.length - 1; i >= 0; --i) {
            if (other.bits[i]) {
                result = result.xOr(multiplier);
            }

            multiplier = multiplier.modularMultiplicationByX();
        }

        return result;
    }

    private final static int BIT = 2;

    /**
     * Performs the 'g' operation on the block.
     *
     * @param sbox          The S-box used in the operation.
     * @param roundConstant The round constant used in the operation.
     * @return The resulting block after the 'g' operation.
     * @see SBox
     */
    public Block g(SBox sbox, Block roundConstant) {
        Block[] subBlocks = new Block[BIT];

        for (int i = 0; i < BIT; ++i) {
            subBlocks[i] = sbox.cypher(this.getSegment(BIT, (i + 1) % BIT));
        }

        Block newBlock = new Block(subBlocks);
        roundConstant = new Block( roundConstant.toString() + "0".repeat(BIT * 6));

        return newBlock.xOr(roundConstant);
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
            clone.bits = this.bits.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Converts the block to a string of '0's and '1's representing its bits.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(this.bits.length);

        for (boolean bit : this.bits) {
            result.append(bit ? '1' : '0');
        }

        return result.toString();
    }

    /**
     * Converts a block to its decimal representation.
     *
     * @param block The block to convert.
     * @return The decimal representation of the block.
     */
    public int blockToDecimal(Block block) {
        return block.toDecimal();
    }

    /**
     * Converts a string to an array of blocks with the specified block size.
     *
     * @param string    The string to convert.
     * @param blockSize The size of each block.
     * @return An array of blocks.
     */
    public static Block[] stringToBlocks(String string, int blockSize) {
        int numberBlocks = string.length() / blockSize;
        Block[] result = new Block[numberBlocks];

        for (int i = 0; i < numberBlocks; ++i) {
            Block[] temp = new Block[blockSize];

            for (int j = 0; j < blockSize; ++j) {
                Block byteBlock = new Block(BIT * 2);
                char ch = string.charAt(i * blockSize + j);

                for (int k = 0; k < BIT * 2; ++k) {
                    byteBlock.bits[((BIT * 2) - 1) - k] = (ch & (1 << k)) != 0;
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
     */
    public static String blocksToString(Block[] blocks) {
        StringBuilder result = new StringBuilder();

        for (Block block : blocks) {
            int numberBytes = block.bits.length / (BIT * 2);

            for (int i = 0; i < numberBytes; ++i) {
                char value = 0;

                for (int j = 0; j < (BIT * 2); ++j) {
                    if (block.bits[i * ((BIT * 2) + ((BIT * 2) - 1)) - j]) {
                        value |= (char) (1 << j);
                    }
                }

                result.append(value);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Block f = new Block("01010111");
        Block g = new Block("10000011");

        System.out.println("GENERATOR_POLYNOMIAL = " + GENERATOR_POLYNOMIAL);

        // f + g mod m -> XOR

        System.out.println("f = " + f);
        System.out.println("g = " + g);

        System.out.println("f XOR g <=> f + g mod m = " + f.xOr(g));


        System.out.println("f * g = " + f.modularMultiplication(g));
    }

}
