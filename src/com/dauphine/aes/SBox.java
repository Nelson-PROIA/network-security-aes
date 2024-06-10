package com.dauphine.aes;

/**
 * <p>
 * Represents an S-box (Substitution-box) used in AES encryption.
 * The S-box is used to perform a substitution step during encryption.
 * This class provides a method to apply the S-box transformation to a block.
 * </p>
 *
 * @author Mathieu ANDRIN {@literal <mathieu.andrin@dauphine.eu>}
 * @see Block
 */
public class SBox {

    /**
     * The S-box matrix containing the substitution values.
     */
    private final int[][] matrix;

    /**
     * Constructs an SBox with the specified substitution matrix.
     *
     * @param matrix The substitution matrix to be used by this SBox.
     */
    public SBox(int[][] matrix) {
        this.matrix = new int[matrix.length][];

        for (int i = 0; i < matrix.length; ++i) {
            this.matrix[i] = matrix[i].clone();
        }
    }

    /**
     * Applies the S-box transformation to the specified block.
     *
     * @param block The block to be transformed.
     * @return A new Block that is the result of applying the S-box transformation.
     * @see Block
     */
    public Block cipher(Block block) {
        int value = matrix[block.rowValue()][block.columnValue()];

        return new Block(block.bits.length, value);
    }

    /**
     * {@inheritDoc}
     *
     * @return The string representation of the SBox.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int[] row : matrix) {
            for (int value : row) {
                stringBuilder.append(value).append(" ");
            }

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

}
