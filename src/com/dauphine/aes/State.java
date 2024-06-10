package com.dauphine.aes;

/**
 * <p>
 * Represents the state in the AES encryption process.
 * This class provides methods for creating, manipulating, and converting the state.
 * </p>
 *
 * <p>
 * The state is represented as a 2D array of Blocks.
 * Each block contains binary data and operations for AES transformations.
 * </p>
 *
 * @author Mathieu ANDRIN {@literal <mathieu.andrin@dauphine.eu>}
 * @see AES
 * @see Block
 * @see Key
 * @see SBox
 */
public class State {

    /**
     * The 2D array of blocks representing the state.
     *
     * @see Block
     */
    private final Block[][] bytes;

    /**
     * Constructs a new State with empty blocks.
     *
     * @see AES
     * @see Block
     */
    public State() {
        bytes = new Block[AES.NUMBER_BLOCKS][AES.NUMBER_BLOCKS];

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                bytes[i][j] = new Block(AES.NUMBER_BLOCKS * 2);
            }
        }
    }

    /**
     * Constructs a new State from a single Block.
     *
     * @param block The Block to initialize the State.
     * @see AES
     * @see Block
     */
    public State(Block block) {
        bytes = new Block[AES.NUMBER_BLOCKS][AES.NUMBER_BLOCKS];

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                bytes[i][j] = block.getSegment((int) Math.pow(2, AES.NUMBER_BLOCKS), i + j * AES.NUMBER_BLOCKS);
            }
        }
    }

    /**
     * Constructs a new State from a 2D integer array.
     *
     * @param values The 2D integer array to initialize the State.
     * @see AES
     * @see Block
     */
    public State(int[][] values) {
        bytes = new Block[AES.NUMBER_BLOCKS][AES.NUMBER_BLOCKS];

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                bytes[i][j] = new Block(AES.NUMBER_BLOCKS * 2, values[i][j]);
            }
        }
    }

    /**
     * Constructs a new State by copying another State.
     *
     * @param other The State to copy.
     * @see AES
     * @see Block
     */
    public State(State other) {
        bytes = new Block[AES.NUMBER_BLOCKS][AES.NUMBER_BLOCKS];

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                bytes[i][j] = other.bytes[i][j].clone();
            }
        }
    }

    /**
     * Performs an XOR operation between this State and a Key.
     *
     * @param key The Key to XOR with.
     * @return A new State resulting from the XOR operation.
     * @see AES
     * @see Block
     * @see Key
     */
    public State XOR(Key key) {
        State newState = new State();

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                newState.bytes[i][j] = bytes[i][j].xOr(key.element(j, i));
            }
        }

        return newState;
    }

    /**
     * Substitutes bytes in the State using the given S-box.
     *
     * @param sbox The S-box to use for substitution.
     * @return A new State with substituted bytes.
     * @see AES
     * @see Block
     * @see SBox
     */
    public State substitute(SBox sbox) {
        State newState = new State();

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                newState.bytes[i][j] = sbox.cipher(bytes[i][j]);
            }
        }

        return newState;
    }

    /**
     * Shifts rows in the State.
     *
     * @return A new State with shifted rows.
     * @see AES
     * @see Block
     */
    public State shift() {
        State newState = new State();

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                newState.bytes[i][(j + i) % AES.NUMBER_BLOCKS] = bytes[i][j];
            }
        }

        return newState;
    }

    /**
     * Inversely shifts rows in the State.
     *
     * @return A new State with inversely shifted rows.
     * @see AES
     * @see Block
     */
    public State shiftInvert() {
        State newState = new State();

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                newState.bytes[i][(j - i + AES.NUMBER_BLOCKS) % AES.NUMBER_BLOCKS] = bytes[i][j];
            }
        }

        return newState;
    }

    /**
     * Multiplies this State with another State.
     *
     * @param other The other State to multiply with.
     * @return A new State resulting from the multiplication.
     * @see AES
     * @see Block
     */
    public State multiply(State other) {
        State newState = new State();

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                Block sum = new Block(AES.NUMBER_BLOCKS * 2);

                for (int k = 0; k < AES.NUMBER_BLOCKS; ++k) {
                    sum = sum.xOr(other.bytes[i][k].modularMultiplication(bytes[k][j]));
                }

                newState.bytes[i][j] = sum;
            }
        }

        return newState;
    }

    /**
     * Converts the State to a single Block.
     *
     * @return The resulting Block.
     * @see AES
     * @see Block
     */
    public Block toBlock() {
        Block[] blocks = new Block[(int) Math.pow(2, AES.NUMBER_BLOCKS)];

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                blocks[AES.NUMBER_BLOCKS * j + i] = bytes[i][j];
            }
        }

        return new Block(blocks);
    }

    /**
     * {@inheritDoc}
     *
     * @see AES
     * @see Block
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < AES.NUMBER_BLOCKS; ++i) {
            for (int j = 0; j < AES.NUMBER_BLOCKS; ++j) {
                stringBuilder.append(bytes[i][j]).append(" ");
            }

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

}
