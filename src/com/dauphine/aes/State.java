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
                //cette fonction renvoie le Block aprés l'affection des valeurs de la SBOX
                // On reprend la fonction Cypher qui renvoie la valeur correspondante au block dans la SBOX
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
                // ce modulo permet de faire un décalage de i (numéro de ligne) des valeurs de la ligne vers la GAUCHE 
                State_after_shift.bytes[i][j]=this.bytes[i][(j-i+4)%4];
            }
        }
        return State_after_shift;
    }

    public State shiftInv() {
        State State_after_shift=new State();
        for(int i=0;i<4;i++){
            for (int j = 0; j < 4; j++) {
                // ce modulo permet de faire un décalage de i (numéro de ligne) des valeurs de la ligne vers la DROITE 
                // autrement dit il fait l'inverse que le modulo de la méthode shift
                State_after_shift.bytes[i][j]=this.bytes[i][(i-j+4)%4];
            }
        }
        return State_after_shift;
    }

    public State mult(State prod) {
        State State_after_mult=new State();
        int taille = this.bytes[0][0].block.length;
        for(int i=0;i<4;i++){
            for (int j = 0; j < 4; j++) {
                Block multiplication_matricielle=new Block(taille);
                for(int x=0;x<4;x++){
                    //Le xor est équivalent au plus dans notre exercice et le modularMult à la multiplication, on retombre bien sur la formule du produit matriciel.
                    multiplication_matricielle=multiplication_matricielle.xOr(this.bytes[i][x].modularMult(prod.bytes[x][j]));
                }
                State_after_mult.bytes[i][j]=multiplication_matricielle;
            }
            
        }
        return State_after_mult;
    }

    public State xOr(Key key) {
        // je ne sais pas si cette fonction doit renvoyer un state ou un bloc,
        // si jamais il faut changer il suffit d'ajouter .block() au return et changer la signature de la méthode
        State State_after_XOR=new State();
        for(int i=0;i<4;i++){
            for (int j = 0; j < 4; j++) {
                State_after_XOR.bytes[i][j]=key.elmnt(i, j).xOr(State_after_XOR.bytes[i][j]);
            }
        }
        return State_after_XOR;
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
