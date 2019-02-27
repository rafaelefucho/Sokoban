import java.util.ArrayList;

public class SokobanState {


    Personnage personnage;
    private char[][] sokobanState;
    public int nbLigneTabChar;
    public int nbColTabChar;
    char sens;

    ArrayList<Box> alBox = new ArrayList<>();


    public Personnage getPersonnage() {
        return personnage;
    }

    public void setPersonnage(Personnage personnage) {
        this.personnage = personnage;
    }

    public ArrayList<Box> getAlBox() {
        return alBox;
    }

    public void setAlBox(ArrayList<Box> alBox) {
        this.alBox = alBox;
    }

    public SokobanState(char[][] sokobanState, char direction) {
        this.sokobanState = sokobanState;
        nbLigneTabChar = sokobanState.length;
        nbColTabChar = sokobanState[0].length;
        this.sens = direction;
    }

    public char[][] getSokobanState() {
        return sokobanState;
    }

    public SokobanState(char[][] sokobanState) {
        this.sokobanState = sokobanState;
        nbLigneTabChar = sokobanState.length;
        nbColTabChar = sokobanState[0].length;
    }

    public char getSens() {
        return sens;
    }

    public void setSens(char sens) {
        this.sens = sens;
    }

    @Override
    public String toString() {
        String toReturn = new String();
        for (int i = 0; i < nbLigneTabChar; i++) {
            for (int j = 0; j < nbColTabChar; j++) {

                toReturn += sokobanState[i][j];
                //System.out.print(sokobanState[i][j]);
            }
            //System.out.print("\n");
            toReturn += "\n";
        }

        return toReturn;
    }

    public boolean alreadyVistedState(ArrayList<SokobanState> alreadyVisitedStates) {


        for (SokobanState temp : alreadyVisitedStates) {
            if (areEquals(temp, sokobanState)) {
                return true;
            }

        }
        return false;


    }


    private boolean areEquals(SokobanState temp, char[][] sokobanState) {

        boolean flagEqual = true;

        for (int i = 0; i < nbLigneTabChar; i++) {


            for (int j = 0; j < nbColTabChar; j++) {

                if (temp.getSokobanState()[i][j] != sokobanState[i][j]) {
                    return false;
                }
            }


        }

        return true;
    }

    public boolean boxInAcorner(char[][] firstSokobanState) {

        for (int i = 1; i < nbLigneTabChar-1; i++) {
            for (int j = 1; j < nbColTabChar-1; j++) {

                if (this.sokobanState[i][j] == 'b' && firstSokobanState[i][j] != 's') {


                    if ((this.sokobanState[i][j+1] == '|' || this.sokobanState[i][j+1] == '*')
                            &&(this.sokobanState[i+1][j] == '|' || this.sokobanState[i+1][j] == '*')){

//                        System.out.println(toString());
                        return true;
                    }

                    if ((this.sokobanState[i][j-1] == '|' || this.sokobanState[i][j-1] == '*')
                            &&(this.sokobanState[i+1][j] == '|' || this.sokobanState[i+1][j] == '*')){
//                        System.out.println(toString());
                        return true;
                    }

                    if ((this.sokobanState[i][j-1] == '|' || this.sokobanState[i][j-1] == '*')
                            &&(this.sokobanState[i-1][j] == '|' || this.sokobanState[i-1][j] == '*')){
//                        System.out.println(toString());
                        return true;
                    }

                    if ((this.sokobanState[i][j+1] == '|' || this.sokobanState[i][j+1] == '*')
                            &&(this.sokobanState[i-1][j] == '|' || this.sokobanState[i-1][j] == '*')){
//                        System.out.println(toString());
                        return true;
                    }

                }
            }


        }
        return false;

    }
}