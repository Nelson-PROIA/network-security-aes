package com.dauphine.aes;

public class State {

    private final Block[][] bytes;

    public State() {
        this.bytes = new Block[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.bytes[i][j] = new Block(8);
            }
        }
    }

    public State(Block block) {
        this.bytes = new Block[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.bytes[i][j] = block.getSegment(16, i + j * 4);
            }
        }
    }

    public State(State toCopy) {
        this.bytes = new Block[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.bytes[i][j] = toCopy.bytes[i][j].clone();
            }
        }
    }

    public State(int[][] val) {
        this.bytes = new Block[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.bytes[i][j] = new Block(8, val[i][j]);
            }
        }
    }

    public State substitute(SBox sbox) {
        State newState = new State();

        for (int i = 0; i < bytes.length; ++i) {
            for (int j = 0; j < bytes[i].length; ++j) {
                Block newBlock = sbox.cypher(this.bytes[i][j]);

                newState.bytes[i][j] = newBlock;
            }  
        }

        return newState;
    }

    public State shift() {
        State newState = new State();

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                newState.bytes[i][(j + i) % 4] = this.bytes[i][j];
            }
        }
        
        return newState;
    }

    public State shiftInv() {
        State newState = new State();

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                newState.bytes[i][(j - i + 4) % 4] = this.bytes[i][j];
            }
        }

        return newState;
    }

    public State mult(State other) {
        State newState = new State();
        int size = this.bytes[0][0].bits.length;

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                Block sum = new Block(size);

                for(int k = 0; k < 4; ++k) {
                    sum = sum.xOr(bytes[i][k].modularMultiplication(other.bytes[k][j]));
                }

                newState.bytes[i][j] = sum;
            }
        }

        return newState;
    }

    public State xOr(Key key) {
        State newState = new State();

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                newState.bytes[i][j] = key.element(j, i).xOr(this.bytes[i][j]);
            }
        }

        return newState;
    }

    public Block block() {
        Block[] blocks = new Block[16];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                blocks[4 * j + i] = this.bytes[i][j];
            }
        }

        return new Block(blocks);
    }

    public String toString() {
        String s = "";

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                s += this.bytes[i][j] + " ";
            }

            s += "\n";
        }

        return s;
    }

}
