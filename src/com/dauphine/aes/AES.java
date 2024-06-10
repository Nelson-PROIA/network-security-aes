package com.dauphine.aes;

import java.util.Arrays;

public class AES {

    private static final int[][] sBox = {
            { 0x9, 0x4, 0xA, 0xB },
            { 0xD, 0x1, 0x8, 0x5 },
            { 0x6, 0x2, 0x0, 0x3 },
            { 0xC, 0xE, 0xF, 0x7 }
    };

    private static final int[][] sBoxInvert = {
            { 0xA, 0x5, 0x9, 0xB },
            { 0x1, 0x7, 0x8, 0xF },
            { 0x6, 0x0, 0x2, 0x3 },
            { 0xC, 0x4, 0xD, 0xE }
    };

    private static final int[][] mix = {
            { 1, 4 },
            { 4, 1 }
    };

    private static final int[][] mixInvert = {
            { 9, 2 },
            { 2, 9 }
    };

    private Key[] keys;

    private SBox sbox, sboxInv;

    private State mixState, mixStateInv;

    public AES(Block key) {
        sbox = new SBox(sBox);
        sboxInv = new SBox(sBoxInvert);

        mixState = new State(mix);
        mixStateInv = new State(mixInvert);

        Key temp = new Key(key);
        keys = temp.genSubKeys(sbox);
    }

    public Block cypher(Block plainText) {
        State state = new State(plainText);

        int round = 0;
        state = state.xOr(keys[round]);

        for (round = 1; round < 2; round++) {
            state = state.substitute(sbox);
            state = state.shift();
            state = state.mult(mixState);
            state = state.xOr(keys[round]);
        }

        state = state.substitute(sbox);
        state = state.shift();
        state = state.xOr(keys[round]);

        return state.block();
    }

    public Block deCypher(Block cypherText) {
        State state = new State(cypherText);

        int round = 2;
        state = state.xOr(keys[round]);

        for (round = 1; round > 0; round--) {
            state = state.shiftInv();
            state = state.substitute(sboxInv);
            state = state.xOr(keys[round]);
            state = state.mult(mixStateInv);
        }

        state = state.shiftInv();
        state = state.substitute(sboxInv);
        state = state.xOr(keys[round]);

        return state.block();
    }


    public static void main(String[] args) {
        String plaintext = "0110111101101011";
        String key = "0010110101010101";

        Block plaintextBlock = new Block(plaintext), keyBlock = new Block(key);

        AES aes = new AES(keyBlock);

        Block cypherBlock = aes.cypher(plaintextBlock);
        System.out.println("Cypher Block : " + cypherBlock);

        Block deCypherBlock = aes.deCypher(cypherBlock);
        System.out.println("Decypher Block : " + deCypherBlock);
        System.out.println(deCypherBlock.toString().compareTo(plaintext));
    }

}