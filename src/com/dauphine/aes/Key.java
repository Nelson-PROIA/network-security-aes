package com.dauphine.aes;

public class Key {

    private final Block[] bytes;

    public Key() {
        this.bytes = new Block[4];

        for (int i = 0; i < 4; i++) {
            this.bytes[i] = new Block(32);
        }
    }

    public Key(Block block) {
        this.bytes = new Block[4];

        for (int i = 0; i < 4; i++) {
            this.bytes[i] = block.portion(4, i);
        }
    }

    public Key(Block[] blocks) {
        this.bytes = new Block[4];

        for (int i = 0; i < 4; i++) {
            this.bytes[i] = blocks[i].clone();
        }
    }

    public Key(Key toCopy) {
        this.bytes = new Block[4];

        for (int i = 0; i < 4; i++) {
            this.bytes[i] = toCopy.bytes[i].clone();
        }
    }

    public Key[] genSubKeys(SBox sbox) {
        //TODO
        return null;
    }

    public Block elmnt(int i, int j) {
        return this.bytes[i].portion(4, j);
    }

    public String toString() {
        String s = "";

        for (int i = 0; i < this.bytes.length; i++) {
            s += this.bytes[i].toString() + " ";
        }

        return s;
    }

    public String toStringH() {
        String s = "";

        for (int i = 0; i < this.bytes.length; i++) {
            s += this.bytes[i].toStringH() + " ";
        }

        return s;
    }

}
