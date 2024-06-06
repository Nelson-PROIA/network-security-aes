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
                this.bytes[i][j] = block.portion(16, i + j * 4);
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
        State State_after_substitue=new State();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Block new_block_to_add=sbox.cypher(this.bytes[i][j]);
                State_after_substitue.bytes[i][j]=new_block_to_add;
            }  
        }
        return State_after_substitue ;
    }

    public State shift() {
        State State_after_shift=new State();
        for(int i=0;i<4;i++){
            for (int j = 0; j < 4; j++) {

                State_after_shift.bytes[i][j]=this.bytes[i][(j-i+4)%4];
            }
        }
        return State_after_shift;
    }

    public State shiftInv() {
        State State_before_shift=new State();
        for(int i=0;i<4;i++){
            for (int j = 0; j < 4; j++) {

                State_before_shift.bytes[i][j]=this.bytes[i][(i-j+4)%4];
            }
        }
        return State_before_shift;
    }

    public State mult(State prod) {
        //TODO
        return null;
    }

    public State xOr(Key key) {
        //TODO
        return null;
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
