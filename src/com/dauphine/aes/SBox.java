package com.dauphine.aes;

public class SBox {

    private final int[][] matrix;

    public SBox(int[][] matrix) {
        this.matrix = new int[matrix.length][];

        for (int i = 0; i < matrix.length; i++) {
            this.matrix[i] = new int[matrix[i].length];

            System.arraycopy(matrix[i], 0, this.matrix[i], 0, matrix[i].length);
        }
    }

    public Block cypher(Block block) {
        int rowValue = block.rowValue();
        int columnValue = block.columnValue();

        int value = this.matrix[rowValue][columnValue];

        return new Block(block.bits.length, value);
    }

}
