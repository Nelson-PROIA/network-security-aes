package com.dauphine.aes;

import java.util.Arrays;

public class State {

    private final static int BIT = 2;

    private final Block[][] bytes;

    public State() {
        this.bytes = new Block[BIT][BIT];

        for (int i = 0; i < BIT; i++) {
            for (int j = 0; j < BIT; j++) {
                this.bytes[i][j] = new Block(BIT * 2);
            }
        }
    }

    public State(Block block) {
        this.bytes = new Block[BIT][BIT];

        for (int i = 0; i < BIT; i++) {
            for (int j = 0; j < BIT; j++) {
                this.bytes[i][j] = block.getSegment((int) Math.pow(2, BIT), i + j * BIT);
            }
        }
    }

    public State(State toCopy) {
        this.bytes = new Block[BIT][BIT];

        for (int i = 0; i < BIT; i++) {
            for (int j = 0; j < BIT; j++) {
                this.bytes[i][j] = toCopy.bytes[i][j].clone();
            }
        }
    }

    public State(int[][] val) {
        this.bytes = new Block[BIT][BIT];

        for (int i = 0; i < BIT; i++) {
            for (int j = 0; j < BIT; j++) {
                this.bytes[i][j] = new Block(BIT * 2, val[i][j]);
            }
        }
    }

    // confidence 5/5
    public State substitute(SBox sbox) {
        State newState = new State();

        for (int i = 0; i < bytes.length; ++i) {
            for (int j = 0; j < bytes[i].length; ++j) {
                newState.bytes[i][j] = sbox.cypher(this.bytes[i][j]);
            }
        }

        return newState;
    }

    // confidence 5/5
    public State shift() {
        State newState = new State();

        for (int i = 0; i < BIT; ++i) {
            for (int j = 0; j < BIT; ++j) {
                newState.bytes[i][(j + i) % BIT] = this.bytes[i][j];
            }
        }
        
        return newState;
    }

    // confidence 5/5
    public State shiftInv() {
        State newState = new State();

        for (int i = 0; i < BIT; ++i) {
            for (int j = 0; j < BIT; ++j) {
                newState.bytes[i][(j - i + BIT) % BIT] = this.bytes[i][j];
            }
        }

        return newState;
    }

    public State mult(State other) {
        State newState = new State();
        int size = this.bytes[0][0].bits.length;

        System.out.println("State.mult");
        System.out.println("\tthis = \n" + this);
        System.out.println("\tother = \n" + other);

        for (int i = 0; i < BIT; ++i) {
            System.out.println("\tstart i (" + i + ") -----------");

            for (int j = 0; j < BIT; ++j) {
                System.out.println("\t\tstart j (" + j + ") -----------");

                Block sum = new Block(size);

                for(int k = 0; k < BIT; ++k) {
                    System.out.println("\t\t\tstart k (" + k + ") -----------");

                    System.out.println("\t\t\t\tother.bytes[" + i + "][" + k + "] = " + other.bytes[k][j]);
                    System.out.println("\t\t\t\tbytes[" + k + "][" + j + "] = " + bytes[i][k]);

                    System.out.println("\t\t\t\tsum = " + sum);
                    System.out.println("\t\t\t\tother.bytes[" + i + "][" + k + "] * bytes[" + k + "][" + j + "] (Block.modularMultiplication) = " + other.bytes[i][k].modularMultiplication(bytes[k][j]));

                    System.out.println("\t\t\t\tsum + (other.bytes[" + i + "][" + k + "] * bytes[" + k + "][" + j + "]) (Block.xOr) = " + sum.xOr(other.bytes[i][k].modularMultiplication(bytes[k][j])));
                    sum = sum.xOr(other.bytes[i][k].modularMultiplication(bytes[k][j]));
                    //sum = sum.xOr(bytes[i][k].modularMultiplication(other.bytes[k][j]));
                }
                System.out.println("\t\t\tend k -----------");

                newState.bytes[i][j] = sum;

                System.out.println("\t\t\tnewState.bytes[" + i + "][" + j + "] = " + newState.bytes[i][j]);

            }
            System.out.println("\t\t\tend j -----------");


        }
        System.out.println("\tend i -----------\n");


        System.out.println("\tnewState = \n" + newState);

        System.out.println();
        System.out.println();
        System.out.println();

        return newState;
    }

    // confidence 5/5
    public State xOr(Key key) {
        State newState = new State();

        for (int i = 0; i < BIT; ++i) {
            for (int j = 0; j < BIT; ++j) {
                newState.bytes[i][j] = this.bytes[i][j].xOr(key.element(j, i));

            }
        }

        return newState;
    }

    public Block block() {
        Block[] blocks = new Block[(int) Math.pow(2, BIT)];

        for (int i = 0; i < BIT; i++) {
            for (int j = 0; j < BIT; j++) {
                blocks[BIT * j + i] = this.bytes[i][j];
            }
        }

        return new Block(blocks);
    }

    public String toString() {
        String s = "";

        for (int i = 0; i < BIT; i++) {
            for (int j = 0; j < BIT; j++) {
                s += this.bytes[i][j] + " ";
            }

            s += "\n";
        }

        return s;
    }

}
