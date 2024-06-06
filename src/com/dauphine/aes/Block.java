package com.dauphine.aes;

public class Block implements Cloneable {

    boolean[] block;

    public final static boolean[] AESmod = { false, false, false, true, true, false, true, true };

    public final static Block AESmodulo = new Block(AESmod);

    public Block(int taille) {
        this.block = new boolean[taille];
    }

    public Block(int taille, int value) {
        this(taille);

        for (int i = taille - 1; i > -1; i--) {
            this.block[i] = ((value % 2) == 1);

            value /= 2;
        }
    }

    public Block(boolean[] s) {
        this(s.length);

        System.arraycopy(s, 0, this.block, 0, s.length);
    }

    public Block(String s) {
        this(s.length());

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '0') {
                this.block[i] = false;
            } else {
                if (s.charAt(i) == '1') {
                    this.block[i] = true;
                } else {
                    System.out.println("Block: bit " + i + " has value " + s.charAt(i) + " different from 0 or 1");
                }
            }
        }
    }

    public Block(Block[] blockList) {
        int taille = 0, cpt = 0;

        for (int i = 0; i < blockList.length; i++) {
            taille += blockList[i].block.length;
        }

        this.block = new boolean[taille];

        for (int i = 0; i < blockList.length; i++) {
            for (int j = 0; j < blockList[i].block.length; j++) {
                this.block[cpt++] = blockList[i].block[j];
            }
        }
    }

    public static Block[] stringToBlock(String chaine, int size) {
        Block[] toReturn = new Block[chaine.length() / size];

        for (int i = 0; i < toReturn.length; i++) {
            Block[] temp = new Block[size];

            for (int j = 0; j < size; j++) {
                Block octetBlock = new Block(8);
                char octet = chaine.charAt(i * 8 + j);

                for (int k = 0; k < 8; k++) {
                    octetBlock.block[7 - k] = (octet % 2 == 1);
                    octet = (char) (octet / 2);
                }

                temp[j] = octetBlock;
            }

            toReturn[i] = new Block(temp);
        }

        return toReturn;
    }

    public static String blockToString(Block[] blocks) {
        String toReturn = "";

        for (int i = 0; i < blocks.length; i++) {
            Block block = blocks[i];

            for (int j = 0; j < block.block.length / 8; j++) {
                char val = 0, pow2 = 1;

                for (int k = 0; k < 8; k++) {
                    if (block.block[j * 8 + 7 - k]) {
                        val += pow2;
                    }

                    pow2 *= 2;
                }

                toReturn += val;
            }
        }

        return toReturn;
    }

    public Block clone() {
        Block clone = new Block(this.block);

        return clone;
    }


    public String toString() {
        String result = "";

        for (int i = 0; i < this.block.length; i++) {
            result += this.block[i] ? "1" : "0";
        }

        return result;
    }

    public String toStringH() {
        String result = "";

        for (int i = 0; i < this.block.length; i += 4) {
            int val = (this.block[i] ? 8 : 0) + (this.block[i + 1] ? 4 : 0) + (this.block[i + 2] ? 2 : 0) + (this.block[i + 3] ? 1 : 0);

            if (val < 10) {
                result += val;
            } else {
                result += ((char) ('A' + val - 10));
            }
        }

        return result;
    }

    public Block portion(int nbrPortion, int index) {
        boolean[] newBlock = new boolean[this.block.length / nbrPortion];

        System.arraycopy(this.block, 0 + index * newBlock.length, newBlock, 0, newBlock.length);

        return new Block(newBlock);
    }

    public Block xOr(Block secondMember) {
        boolean[] block = ;

        return new Block(block);
    }

    public Block leftShift() {
        //TODO
        return null;
    }

    public int rowValue() {
        //TODO
        return -1;
    }

    public int columnValue() {
        //TODO
        return -1;
    }

    public Block modularMultByX() {
        //TODO
        return null;
    }

    public Block modularMult(Block prod) {
        //TODO
        return null;
    }

    public Block g(SBox sbox, Block rc) {
        //TODO
        return null;
    }

}