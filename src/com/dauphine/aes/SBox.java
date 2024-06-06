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

    public Block cypher(Block toCypher) {
        int int_line=toCypher.rowValue();
        int int_column=toCypher.columnValue();

        int Values_In_SBox=this.matrix[int_line][int_column];
        
        Block block_in_SBox=new Block(int_column, Values_In_SBox);
        
        return block_in_SBox;
    }

}
