import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.*;

public class Vue extends Application {

    final int NB_LEVEL_TOTAL = 8;
    Stage window;
    Scene mainScene;
    ArrayList<Level> alLevel;
    ArrayList<ImageTuile> alImage;
    ArrayList<Box> alBox;
    ArrayList<Socle> alSocle;
    int lvlNumber = 0;
    double xImage, yImage;
    double tailleH, tailleV;
    Personnage P;
    Box boiteActive;
    double nbColTabChar, nbLigneTabChar;
    double echelleV, echelleH;
    Group groupNouveau;
    Button jouer, mainMenu, restart, chooseLvl, solve, undo;
    Scene menuPrincipal;
    float CENTER_ON_SCREEN_X_FRACTION = 1.0f / 8;
    float CENTER_ON_SCREEN_Y_FRACTION = 1.0f / 10;
    ListePlayer listePlayer;

    Stack<SokobanState> movesMadeByThePlayer = new Stack<>();

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Sokoban");
        window.setOnCloseRequest(event -> {
            event.consume();
            alertBoxDisplay("Exit", "Voulez vous sauvegarder avant de quitter ?");
            Platform.exit();
        });
        this.alImage = new ArrayList<>();
        this.alLevel = new ArrayList<>();
        initImage();

        for (int i = 0; i < NB_LEVEL_TOTAL; i++)
            this.alLevel.add(new Level(++lvlNumber));


        this.lvlNumber = 0;
        echelleH = 64;
        echelleV = 64;
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double centerX = bounds.getMinX() + (bounds.getWidth() - window.getWidth())
                * CENTER_ON_SCREEN_X_FRACTION;
        double centerY = bounds.getMinY() + (bounds.getHeight() - window.getHeight())
                * CENTER_ON_SCREEN_Y_FRACTION;
        //initMainMenu();
        initLoginMenu();
        window.setX(centerX);
        window.setY(centerY);
        window.show();
    }

    public Group setImageLevel(Group group, boolean firstTime) {
        Level lvl = this.alLevel.get(lvlNumber);
        char[][] tabChar = lvl.getTabChar();

        nbLigneTabChar = tabChar.length;
        nbColTabChar = tabChar[0].length;
        this.tailleH = nbColTabChar * echelleH;
        this.tailleV = nbLigneTabChar * echelleV;
        window.setWidth(tailleH + 134);
        window.setHeight(tailleV + 37);


        this.xImage = 0;
        this.yImage = 0;

        for (int i = 0; i < nbLigneTabChar; i++) {
            for (int j = 0; j < nbColTabChar; j++) {

                group.getChildren().addAll(chooseAndPrefImage(tabChar[i][j], tabChar[0].length, tabChar.length, firstTime));
            }
        }
        group.getChildren().add(initPersonnage(tabChar.length, tabChar[0].length));
        group.getChildren().addAll(initBox(tabChar.length, tabChar[0].length));

        return group;
    }

    public Scene initJouer() {
        restart = new Button("Restart Level");
        mainMenu = new Button("Main menu");
        undo = new Button("Undo");
        solve = new Button("Solve the level");

        this.alBox = new ArrayList<>();
        this.alSocle = new ArrayList<>();

        Group group = setImageLevel(new Group(), true);
        mainMenu.setLayoutX(tailleH + 10);
        mainMenu.setLayoutY(60);
        restart.setLayoutX(tailleH + 10);
        restart.setLayoutY(30);

        undo.setLayoutX(tailleH + 10);
        undo.setLayoutY(90);
        solve.setLayoutX(tailleH + 10);
        solve.setLayoutY(120);


        group.getChildren().addAll(restart, mainMenu, undo, solve);


        mainScene = new Scene(group, tailleH + 120, tailleV);

        mainMenu.setOnAction(event -> {
            initMainMenu();
        });

        restart.setOnAction(event -> {
            movesMadeByThePlayer.clear();
            alBox.clear();
            alSocle.clear();
            groupNouveau = setImageLevel(new Group(), true);
            groupNouveau.getChildren().addAll(restart, mainMenu, undo, solve);
            mainScene.setRoot(groupNouveau);
        });

        solve.setOnAction(event -> {
            solve();
        });

        undo.setOnAction(event -> {
            undo();
        });


        mainScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                movesMadeByThePlayer.push(new SokobanState(getCurrentSokobanState(), P.direction));

                if (event.getCode() == KeyCode.S) {
                    P.descendre(moveNextCase('d', false, false), echelleV);
                    boitePlace();
                }
                if (event.getCode() == KeyCode.Z) {
                    P.monter(moveNextCase('u', false, false), echelleV);
                    boitePlace();
                }
                if (event.getCode() == KeyCode.Q) {
                    P.gauche(moveNextCase('l', false, false), echelleH);
                    boitePlace();
                }
                if (event.getCode() == KeyCode.D) {
                    P.droite(moveNextCase('r', false, false), echelleH);
                    boitePlace();
                }
//                System.out.println();
                groupNouveau = majPerso();
                groupNouveau.getChildren().addAll(restart, mainMenu, undo, solve);
                if (estGagne()) {
                    if (++lvlNumber < NB_LEVEL_TOTAL) {
                        movesMadeByThePlayer.clear();
                        alBox.clear();
                        alSocle.clear();
                        groupNouveau = setImageLevel(new Group(), true);
                        alLevel.get(lvlNumber).unlockLevel();
                        listePlayer.getPlayerActif().setLevel(lvlNumber + 1);
                        window.setWidth(tailleH + 134);
                        window.setHeight(tailleV + 37);
                        restart.setLayoutX(tailleH + 10);
                        restart.setLayoutY(30);
                        mainMenu.setLayoutX(tailleH + 10);
                        mainMenu.setLayoutY(60);
                        groupNouveau.getChildren().addAll(restart, mainMenu, undo, solve);
                    } else
                        Platform.exit();
                }

                mainScene.setRoot(groupNouveau);
//                movesMadeByThePlayer.push(new SokobanState(getCurrentSokobanState(), P.direction));

            }
        });
        return mainScene;
    }

    private void undo() {
        System.out.println(movesMadeByThePlayer.size());
        System.out.println(new SokobanState(getCurrentSokobanState()));

        if (movesMadeByThePlayer.size() > 0) {
//            movesMadeByThePlayer.pop();
            SokobanState currentSokobanState = movesMadeByThePlayer.pop();
            updateToLastState(currentSokobanState);
            P.direction = currentSokobanState.getSens();
            groupNouveau = majPerso();
            groupNouveau.getChildren().addAll(restart, mainMenu, undo, solve);
            mainScene.setRoot(groupNouveau);
        }


    }

    private void updateToLastState(SokobanState currentSokobanState) {

        char[][] tabChar = currentSokobanState.getSokobanState();

        nbLigneTabChar = tabChar.length;
        nbColTabChar = tabChar[0].length;
        this.tailleH = nbColTabChar * echelleH;
        this.tailleV = nbLigneTabChar * echelleV;
        window.setWidth(tailleH + 134);
        window.setHeight(tailleV + 37);

        this.xImage = 0;
        this.yImage = 0;


        alBox.clear();

        ImageView imgView = null;
        for (int i = 0; i < nbLigneTabChar; i++) {
            for (int j = 0; j < nbColTabChar; j++) {

                int imageIndex = 0;

                switch (tabChar[i][j]) {
                    case '|':
                        imageIndex = 6;
                        break;
                    case '*':
                        imageIndex = 5;
                        break;
                    case ' ':
                        imageIndex = 0;
                        break;
                    case 'P':
                        imageIndex = 1;
                        P = new Personnage(j * this.tailleH / this.nbColTabChar, i * this.tailleH / this.nbColTabChar);
                        break;
                    case 'b':
                        alBox.add(new Box(j * this.tailleH / this.nbColTabChar, i * this.tailleH / this.nbColTabChar));
                        imageIndex = 7;
                        break;
                    case 's':
                        imageIndex = 8;
                        break;

                }
//                imgView = null;
//                imgView = new ImageView(new Image(this.alImage.get(imageIndex).getUrl(), this.tailleH / nbColTabChar, this.tailleV / nbLigneTabChar, false, false));
//                imgView.prefWidth(this.tailleH / (float) nbColTabChar);
//                imgView.prefHeight(this.tailleV / (float) nbLigneTabChar);
//                imgView.setX(j * this.tailleH / this.nbColTabChar);
//                imgView.setY(i * this.tailleH / this.nbColTabChar);
//                group.getChildren().add(imgView);
            }
        }

        return;

    }

    private void solve() {

        Personnage oldPersonnage = new Personnage();
        oldPersonnage = P;

        ArrayList<Box> oldBoxes = alBox;

        ArrayList<SokobanState> queueStates = new ArrayList<>();
        ArrayList<SokobanState> alreadyVisitedStates = new ArrayList<>();

        SokobanState firstSokobanState = new SokobanState(getCurrentSokobanState());
        SokobanState tempSokobanState = new SokobanState(getCurrentSokobanState());
        tempSokobanState.setPersonnage(P);
        tempSokobanState.setAlBox(alBox);

        queueStates.add(tempSokobanState);
        int toCheck = 1;
        int toErase;
        boolean flag = true;

        while(flag){

            toErase = 0;

            for (int i=0;i<toCheck;i++){

                SokobanState fatherSokobanState = queueStates.get(i); // Father
                updateToLastState(fatherSokobanState);
                //System.out.println(fatherSokobanState);
                boitePlace();

                if (estGagne()){
                    System.out.println("I won");
                    flag = false;
                    break;
                }



                updateToLastState(fatherSokobanState);
                if (moveNextCase('d', false, false)){

                    P.descendre(true, echelleV);
                    boitePlace();
                    tempSokobanState = new SokobanState(getCurrentSokobanState());
                    tempSokobanState.setPersonnage(P);
                    tempSokobanState.setAlBox(alBox);

                    if(!tempSokobanState.alreadyVistedState(alreadyVisitedStates)&&!tempSokobanState.boxInAcorner(firstSokobanState.getSokobanState())){
                        queueStates.add(tempSokobanState);
                        toErase++;
                    }
                    else {
                        if (estGagne()){
                            System.out.println("I won");
                            flag = false;
                            break;
                        }
                    }

                }


                updateToLastState(fatherSokobanState);
                if (moveNextCase('u', false, false)){
                    P.monter(true, echelleV);
                    boitePlace();
                    tempSokobanState = new SokobanState(getCurrentSokobanState());
                    tempSokobanState.setPersonnage(P);
                    tempSokobanState.setAlBox(alBox);
                    if(!tempSokobanState.alreadyVistedState(alreadyVisitedStates)&&!tempSokobanState.boxInAcorner(firstSokobanState.getSokobanState())){
                        queueStates.add(tempSokobanState);
                        toErase++;
                    }
                    else {
                        if (estGagne()){
                            System.out.println("I won");
                            flag = false;
                            break;
                        }
                    }
                }

                updateToLastState(fatherSokobanState);
                if (moveNextCase('l', false, false)){
                    P.gauche(true, echelleH);
                    boitePlace();
                    tempSokobanState = new SokobanState(getCurrentSokobanState());
                    tempSokobanState.setPersonnage(P);
                    tempSokobanState.setAlBox(alBox);
                    if(!tempSokobanState.alreadyVistedState(alreadyVisitedStates)&&!tempSokobanState.boxInAcorner(firstSokobanState.getSokobanState())){
                        queueStates.add(tempSokobanState);
                        toErase++;
                    }
                    else {
                        if (estGagne()){
                            System.out.println("I won");
                            flag = false;
                            break;
                        }
                    }
                }

                updateToLastState(fatherSokobanState);
                if (moveNextCase('r', false, false)){
                    P.droite(true, echelleH);
                    boitePlace();
                    tempSokobanState = new SokobanState(getCurrentSokobanState());
                    tempSokobanState.setPersonnage(P);
                    tempSokobanState.setAlBox(alBox);
                    if(!tempSokobanState.alreadyVistedState(alreadyVisitedStates)&&!tempSokobanState.boxInAcorner(firstSokobanState.getSokobanState())){
                        queueStates.add(tempSokobanState);
                        toErase++;
                    }
                    else {
                        if (estGagne()){
                            System.out.println("I won");
                            flag = false;
                            break;
                        }
                    }
                }




            }


            for (int i=0;i<toCheck;i++){
                alreadyVisitedStates.add(queueStates.get(0));
                queueStates.remove(0);
            }

            toCheck = toErase;

            if (toErase == 0){
                flag = false;
                System.out.println("tell me");
            }

        }


        System.out.println("I gout out");

    }

    private char[][] getCurrentSokobanState() {

        Level lvl = this.alLevel.get(lvlNumber);
        char[][] tabChar = lvl.getTabChar();

        nbLigneTabChar = tabChar.length;
        nbColTabChar = tabChar[0].length;

        char[][] state = new char[(int) nbLigneTabChar][(int) nbColTabChar];

        for (int i = 0; i < nbLigneTabChar; i++) {
            for (int j = 0; j < nbColTabChar; j++) {

                if (tabChar[i][j] == '|' || tabChar[i][j] == '*' || tabChar[i][j] == 's') {
                    state[i][j] = tabChar[i][j];
                } else {
                    state[i][j] = ' ';
                }
            }
        }

        int posXTabChar = (int) ((tabChar[0].length / this.tailleH) * this.P.getPosX());
        int posYTabChar = (int) ((tabChar.length / tailleV) * this.P.getPosY());

//        System.out.println(posXTabChar + " " + posYTabChar);

        state[posYTabChar][posXTabChar] = 'P';

        for (Box b : this.alBox) {

            posXTabChar = (int) ((tabChar[0].length / this.tailleH) * b.getPosX());
            posYTabChar = (int) ((tabChar.length / tailleV) * b.getPosY());
            state[posYTabChar][posXTabChar] = 'b';

        }

        return state;

    }

    public void initImage() {
        this.alImage.add(new ImageTuile(' ', "sol.png"));
        this.alImage.add(new ImageTuile('P', "persoF.png"));
        this.alImage.add(new ImageTuile('?', "persoD.png"));
        this.alImage.add(new ImageTuile('?', "persoL.png"));
        this.alImage.add(new ImageTuile('?', "persoR.png"));
        this.alImage.add(new ImageTuile('*', "murUpBotSide.png"));
        this.alImage.add(new ImageTuile('|', "murLeftRightSide.png"));
        this.alImage.add(new ImageTuile('b', "boulder.png"));
        this.alImage.add(new ImageTuile('s', "socle.png"));


    }

    public void initMainMenu() {

        for (int i = 0; i < listePlayer.getPlayerActif().getLevel(); i++) {
            alLevel.get(i).unlockLevel();
        }

        Scene menu;
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 20, 20, 20));
        chooseLvl = new Button("Choose your level");
        jouer = new Button("Play");
        tailleH = 210;
        tailleV = 130;
        window.setWidth(tailleH);
        window.setHeight(tailleV);

        jouer.setOnAction(event -> {
            window.setScene(initJouer());

        });


        chooseLvl.setOnAction(event -> {
            window.setScene(initChooseLvl());
            tailleH = 210;
            tailleV = 600;
            window.setWidth(tailleH);
            window.setHeight(tailleV);

        });
        vBox.getChildren().addAll(jouer, chooseLvl);
        menu = new Scene(vBox);
        window.setScene(menu);
    }

    public void initLoginMenu() {
        listePlayer = new ListePlayer();
        VBox vBoxMain = new VBox();
        vBoxMain.setPadding(new Insets(10, 10, 10, 10));
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 10, 10, 10));
        //VBox vBox = new VBox();
        TextField textField = new TextField();
        textField.setPromptText("Nickname");

        Button connect = new Button("Connect");
        connect.setOnAction(event -> {
            if (listePlayer.playerConnecting(textField.getText()))
                initMainMenu();

        });

        Button signUp = new Button("SignUp");
        signUp.setOnAction(event -> {
            if (listePlayer.playerRegistering(textField.getText()))
                initMainMenu();

        });

        hBox.getChildren().addAll(connect, signUp);
        vBoxMain.getChildren().addAll(textField, hBox);
        Scene test = new Scene(vBoxMain);
        window.setScene(test);
        //mainScene.setRoot(vBoxMain);
    }

    public Scene initChooseLvl() {
        ListView<Button> buttonListView = new ListView<>();
        Scene menu;

        StackPane stackPane = new StackPane();
        for (int i = 0; i < NB_LEVEL_TOTAL; i++)
            buttonListView.getItems().add(new Button("Level " + (i + 1)));
        for (int i = 0; i < NB_LEVEL_TOTAL; i++) {
            if (this.alLevel.get(i).isLocked()) {
                buttonListView.getItems().get(i).setDisable(true);
            }
        }
        for (Button b : buttonListView.getItems()) {
            b.setOnAction(event -> {
                int index = buttonListView.getItems().indexOf(b);
                lvlNumber = index;
                window.setScene(initJouer());
                window.setWidth(tailleH + 134);
                window.setHeight(tailleV + 37);
            });

        }
        stackPane.getChildren().add(buttonListView);
        menu = new Scene(stackPane);
        return menu;
    }

    public ImageView initPersonnage(int nbLigne, int nbCol) {
        ImageView imgView = null;
        switch (this.P.getDirection()) {
            case 'u':
                imgView = new ImageView(new Image(this.alImage.get(2).getUrl(), this.tailleH / nbColTabChar, this.tailleV / nbLigneTabChar, false, false));
                break;
            case 'd':
                imgView = new ImageView(new Image(this.alImage.get(1).getUrl(), this.tailleH / nbColTabChar, this.tailleV / nbLigneTabChar, false, false));
                break;
            case 'l':
                imgView = new ImageView(new Image(this.alImage.get(3).getUrl(), this.tailleH / nbColTabChar, this.tailleV / nbLigneTabChar, false, false));
                break;
            case 'r':
                imgView = new ImageView(new Image(this.alImage.get(4).getUrl(), this.tailleH / nbColTabChar, this.tailleV / nbLigneTabChar, false, false));
                break;
        }

        imgView.prefWidth(this.tailleH / (float) nbCol);
        imgView.prefHeight(this.tailleV / (float) nbLigne);
        imgView.setX(this.P.getPosX());
        imgView.setY(this.P.getPosY());

        return imgView;
    }

    public ArrayList<ImageView> initBox(int nbLigne, int nbCol) {
        ArrayList<ImageView> arrayList = new ArrayList<>();
        for (Box b : this.alBox) {
            ImageView img = new ImageView(new Image(this.alImage.get(7).getUrl(), this.tailleH / nbColTabChar, this.tailleV / nbLigneTabChar, false, false));
            img.prefWidth(this.tailleH / (float) nbCol);
            img.prefHeight(this.tailleV / (float) nbLigne);
            img.setX(b.getPosX());
            img.setY(b.getPosY());
            arrayList.add(img);
        }
        return arrayList;
    }

    public ArrayList<ImageView> chooseAndPrefImage(char c, int nbCol, int nbLigne, boolean firstTime) {
        ArrayList<ImageView> alImgView = new ArrayList<>();
        ImageView imgView;
        for (ImageTuile img : alImage) {
            if (img.getSymbole() == c) {
                if (img.getSymbole() != '*' && img.getSymbole() != '|') {
                    imgView = new ImageView(new Image(this.alImage.get(0).getUrl(), this.tailleH / nbColTabChar, this.tailleV / nbLigneTabChar, false, false));
                    imgView.prefWidth(this.tailleH / (float) nbCol);
                    imgView.prefHeight(this.tailleV / (float) nbLigne);
                    imgView.setX(this.xImage);
                    imgView.setY(this.yImage);
                    alImgView.add(imgView);
                }
                if (img.getSymbole() == 'P' && firstTime) {
                    this.P = new Personnage(this.xImage, this.yImage);
                } else if (img.getSymbole() == 'b' && firstTime) {
                    this.alBox.add(new Box(this.xImage, this.yImage));
                } else if (img.getSymbole() != 'P' && img.getSymbole() != 'b') {
                    if (img.getSymbole() == 's' && firstTime) {
                        this.alSocle.add(new Socle(this.xImage, this.yImage));
                    }
                    imgView = new ImageView(new Image(img.getUrl(), this.tailleH / nbColTabChar, this.tailleV / nbLigneTabChar, false, false));
                    //imgView.prefWidth(this.tailleH / (float) nbCol);
                    //imgView.prefHeight(this.tailleV / (float) nbLigne);
                    imgView.setX(this.xImage);
                    imgView.setY(this.yImage);

                    alImgView.add(imgView);
                }
                if ((this.xImage + (this.tailleH / nbColTabChar) < this.tailleH))
                    this.xImage += this.tailleH / this.nbColTabChar;
                else {
                    this.xImage = 0;
                    this.yImage += this.tailleV / this.nbLigneTabChar;
                }
            }
        }
        return alImgView;
    }

    public Group majPerso() {
        Group groupMaj = setImageLevel(new Group(), false);
        return groupMaj;
    }

    public boolean moveNextCase(char direction, boolean isBox, boolean beforeBox) {
        char[][] tabChar = this.alLevel.get(lvlNumber).getTabChar();
        int posXTabChar = (int) ((tabChar[0].length / this.tailleH) * this.P.getPosX());
        int posYTabChar = (int) ((tabChar.length / tailleV) * this.P.getPosY());
        switch (direction) {
            case 'u':
                if (!isBox) {
                    if (tabChar[posYTabChar - 1][posXTabChar] != '*' &&
                            tabChar[posYTabChar - 1][posXTabChar] != '|' &&
                            (!in(posXTabChar, posYTabChar - 1))) {

                        return true;
                    } else if ((in(posXTabChar, posYTabChar - 1))) {
                        if (moveNextCase(direction, true, false)) {// && !boiteActive.getEstPlace()) {
                            boiteActive.monter(true, echelleV);
                            return true;
                        } else return false;
                    }
                    return false;
                } else {
                    if (tabChar[getBoiteActivePosYTab() - 1][getBoiteActivePosXTab()] != '*' &&
                            tabChar[getBoiteActivePosYTab() - 1][getBoiteActivePosXTab()] != '|') {
                        for (Box box : this.alBox) {
                            if (box.getPosX() == boiteActive.getPosX() && box.getPosY() == boiteActive.getPosY() - this.tailleV / nbLigneTabChar)
                                return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }

            case 'd':
                if (!isBox) {
                    if (tabChar[posYTabChar + 1][posXTabChar] != '*' &&
                            tabChar[posYTabChar + 1][posXTabChar] != '|' &&
                            !in(posXTabChar, posYTabChar + 1)) {
                        return true;
                    } else if ((in(posXTabChar, posYTabChar + 1))) {
                        if (moveNextCase(direction, true, false)) {// && !boiteActive.getEstPlace()) {
                            boiteActive.descendre(true, echelleV);
                            return true;
                        } else return false;
                    }
                    return false;
                } else {
                    if (tabChar[getBoiteActivePosYTab() + 1][getBoiteActivePosXTab()] != '*' &&
                            tabChar[getBoiteActivePosYTab() + 1][getBoiteActivePosXTab()] != '|') {
                        for (Box box : this.alBox) {
                            if (box.getPosX() == boiteActive.getPosX() && box.getPosY() == boiteActive.getPosY() + this.tailleV / nbLigneTabChar)
                                return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            case 'l':
                if (!isBox) {
                    if (tabChar[posYTabChar][posXTabChar - 1] != '*' &&
                            tabChar[posYTabChar][posXTabChar - 1] != '|' &&
                            !in(posXTabChar - 1, posYTabChar)) {
                        return true;
                    } else if ((in(posXTabChar - 1, posYTabChar))) {
                        if (moveNextCase(direction, true, false)) {// && !boiteActive.getEstPlace()) {
                            boiteActive.gauche(true, echelleH);
                            return true;
                        } else return false;
                    }
                    return false;
                } else {
                    if (tabChar[getBoiteActivePosYTab()][getBoiteActivePosXTab() - 1] != '*' &&
                            tabChar[getBoiteActivePosYTab()][getBoiteActivePosXTab() - 1] != '|') {
                        for (Box box : this.alBox) {
                            if (box.getPosX() == boiteActive.getPosX() - this.tailleH / nbColTabChar && box.getPosY() == boiteActive.getPosY())
                                return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }

            case 'r':
                if (!isBox) {
                    if (tabChar[posYTabChar][posXTabChar + 1] != '*' &&
                            tabChar[posYTabChar][posXTabChar + 1] != '|' &&
                            !in(posXTabChar + 1, posYTabChar)) {
                        return true;
                    } else if (in(posXTabChar + 1, posYTabChar)) {
                        if (moveNextCase(direction, true, false)) {// && !boiteActive.getEstPlace()) {
                            boiteActive.droite(true, echelleH);
                            return true;
                        } else return false;
                    }
                    return false;
                } else {
                    if (tabChar[getBoiteActivePosYTab()][getBoiteActivePosXTab() + 1] != '*' &&
                            tabChar[getBoiteActivePosYTab()][getBoiteActivePosXTab() + 1] != '|') {
                        for (Box box : this.alBox) {
                            if (box.getPosX() == boiteActive.getPosX() + this.tailleH / nbColTabChar && box.getPosY() == boiteActive.getPosY())
                                return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            default:
        }
        return false;
    }

    public void boitePlace() {
        for (Box b : alBox) {
            for (Socle socle : alSocle) {
                if (b.getPosX() == socle.getPosX() && b.getPosY() == socle.getPosY()) {
                    b.setEstPlace(true);
                    //socle.setEstUtilise(true);
                    break;
                } else {
                    if (b.getEstPlace())
                        b.setEstPlace(false);
                }

            }
        }
    }

    public boolean in(double posXPerso, double posYPerso) {
        for (Box b : this.alBox) {
            int posXBoxTabCar = (int) ((nbColTabChar / this.tailleH) * b.getPosX());
            int posYBoxTabCar = (int) ((nbLigneTabChar / this.tailleV) * b.getPosY());

            if (posXBoxTabCar == posXPerso && posYBoxTabCar == posYPerso) {
                boiteActive = b;
                return true;
            }
        }
        return false;

    }

    public int getBoiteActivePosXTab() {
        return (int) (this.boiteActive.getPosX() / echelleH);
    }

    public int getBoiteActivePosYTab() {
        return (int) (boiteActive.getPosY() / echelleV);
    }

    public boolean estGagne() {
        for (Box box : alBox) {
            if (!box.getEstPlace())
                return false;
        }
        return true;
    }

    public void alertBoxDisplay(String title, String message) {
        Stage secondWindows = new Stage();
        secondWindows.initModality(Modality.APPLICATION_MODAL);
        secondWindows.setTitle(title);
        secondWindows.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        VBox vBox = new VBox();
        HBox hBox = new HBox();

        vBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(10);


        Button yes = new Button("Yes");
        Button no = new Button("No");

        hBox.getChildren().addAll(yes, no);
        vBox.getChildren().addAll(label, hBox);
        secondWindows.setScene(new Scene(vBox));
        yes.setOnAction(event -> {
            listePlayer.savePlayer();
            secondWindows.close();
        });

        no.setOnAction(event -> {
            secondWindows.close();
        });

        secondWindows.showAndWait();


    }

}