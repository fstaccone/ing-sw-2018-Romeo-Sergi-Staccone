package it.polimi.ingsw;

import it.polimi.ingsw.control.RemoteController;
import it.polimi.ingsw.model.gameobjects.WindowPatternCard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RmiGui extends UnicastRemoteObject implements MatchObserver {

    private String username;
    private RemoteController controller;
    private Stage windowStage;
    private boolean myTurn;
    private ChooseCardHandler chooseCardHandler;
    private GameBoardHandler gameBoardHandler;
    private List<String> toolCardsList = new ArrayList<>();
    private List<String> dicesList;
    private String privateCard;
    private List<String> publicCardsList;
    private Map<String, Integer> otherFavorTokensMap;
    private Map<String, WindowPatternCard> otherSchemeCardsMap;
    private boolean reconnection;
    private List<String> players;

    public RmiGui(Stage fromLogin, String username, RemoteController controller) throws RemoteException {
        super();
        this.username = username;
        this.controller = controller;
        myTurn = false;
        windowStage = fromLogin;
        otherFavorTokensMap = new HashMap<>();
        otherSchemeCardsMap = new HashMap<>();
        reconnection = false;
        players = new ArrayList<>();
    }

    public List<String> getPlayers() { return players; }

    @Override
    public void onPlayers(List<String> playersNames) {
        System.out.println("On players");
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.setLocation(new URL("File:./src/main/java/it/polimi/ingsw/resources/choose-card.fxml"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        chooseCardHandler = fxmlLoader.getController();
        Scene scene = new Scene(root);
        chooseCardHandler.init(windowStage, scene, controller, username);
        chooseCardHandler.setOpponents(playersNames);
    }

    @Override
    public void onYourTurn(boolean isMyTurn, String string) {
        System.out.println("On your turn");
        if (string != null)
            onReserve(string);
        this.myTurn = isMyTurn;
        if (myTurn) {
            //Solo per verifica
            String s = "Ora è il tuo turno!";
            if (gameBoardHandler != null) gameBoardHandler.setTextArea(s);
            else chooseCardHandler.setTextArea(s);
        } else
            //Solo per verifica
            System.out.println("Non è più il tuo turno!");
    }


    @Override
    public void onReserve(String string) {
        dicesList = new ArrayList<>();
        System.out.println("On reserve");
        String dicesString = string.substring(1, string.length() - 1);
        List<String> temp = Pattern.compile(", ")
                .splitAsStream(dicesString)
                .collect(Collectors.toList());
        for (String s : temp) {
            s = s.substring(1, s.length() - 1).toLowerCase().replaceAll(" ", "_");
            dicesList.add(s);
        }
        for (String dice : dicesList) System.out.println(dice);
        if (gameBoardHandler != null) {
            gameBoardHandler.setReserve(dicesList);
        }
    }

    public void launch() {
        System.out.println("Launch");
        try {
            controller.observeMatch(username, this, reconnection);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerReconnection(String name) {
        System.out.println("On player reconnection");
        if (gameBoardHandler != null)
            gameBoardHandler.setTextArea("Il giocatore " + name + " è entrato nella partita!");
        else chooseCardHandler.setTextArea("Il giocatore " + name + " è entrato nella partita!");
    }


    @Override
    public void onGameClosing() {

    }

    @Override
    public void onGameEnd(String winner, List<String> rankingNames, List<Integer> rankingValues) {

    }

    @Override
    public void onAfterReconnection(String toolcards, String publicCards, String privateCard, String reserve, String roundTrack, int myTokens, WindowPatternCard schemeCard, Map<String, Integer> otherTokens, Map<String, WindowPatternCard> otherSchemeCards, boolean schemeCardChosen) throws RemoteException {

    }

    @Override
    public void onRoundTrack(String track) throws RemoteException {

    }

    @Override
    public void onMyWindow(WindowPatternCard window) {
        if (gameBoardHandler != null) gameBoardHandler.setMyWindow(window);
        //AGGIORNAMENTO PROPRIA CARTA SCHEMA
    }

    @Override
    public void onMyFavorTokens(int value) {
        //AGGIORNAMENTO PROPRI SEGNALINI
        if (gameBoardHandler != null) gameBoardHandler.setFavourTokens(value);
    }

    @Override
    public void onOtherFavorTokens(int value, String name) {
        //PRIMA INIZIALIZZAZIONE E AGGIORNAMENTO SEGNALINI ALTRUI
        otherFavorTokensMap.put(name, value);
        if (gameBoardHandler != null) gameBoardHandler.onOtherFavorTokens(value, name);

    }

    @Override
    public void onOtherSchemeCards(WindowPatternCard window, String name) {
        otherSchemeCardsMap.put(name, window);
        if (gameBoardHandler != null) gameBoardHandler.onOtherSchemeCards(window, name);
    }

    @Override
    public void onOtherTurn(String name) {
        System.out.println("On other turn");
        String s = "Ora è il turno di " + name;
        if (gameBoardHandler != null) {
            gameBoardHandler.setTextArea(s);
        } else chooseCardHandler.setTextArea(s);
    }


    @Override
    public void onInitialization(String toolcards, String publicCards, String privateCard, List<String> players) {

        parseToolcards(toolcards);

        // todo: cosa fa?
        privateCard = privateCard.substring(7, privateCard.length() - 1).toLowerCase();
        this.privateCard = privateCard;

        chooseCardHandler.setPrivateCard(privateCard);
        parsePublicCards(publicCards);

        this.players = players;

    }

    private void parseToolcards(String toolcards) {
        String dicesString = toolcards.substring(1, toolcards.length() - 1);
        List<String> temp = Pattern.compile(", ")
                .splitAsStream(dicesString)
                .collect(Collectors.toList());
        for (String s : temp) {
            s = s.split(":", 2)[0];
            toolCardsList.add(s);
        }

    }

    private void parsePublicCards(String publicCards) {
        publicCardsList = new ArrayList<>();
        String cards = publicCards.substring(1, publicCards.length() - 1);
        List<String> temp = Pattern.compile(", ").splitAsStream(cards).collect(Collectors.toList());
        for (String s : temp) {
            s = s.substring(8).toLowerCase();
            s = s.split("\n", 2)[0];
            s = s.replaceAll(" - ", "_").replaceAll(" ", "_");
            publicCardsList.add(s);
        }
    }

    @Override
    public void onPlayerExit(String name) {
        System.out.println("On player exit");
        if (gameBoardHandler != null) gameBoardHandler.setTextArea("Player " + name + " has left the game!");
        else chooseCardHandler.setTextArea("Player " + name + " has left the game!");
    }

    @Override
    public void onWindowChoise(List<String> windows) {
        System.out.println("On windowStage choise");
        chooseCardHandler.setWindows(windows);
    }

    @Override
    public void onAfterWindowChoise(){
        System.out.println("On show windowStage");
        String imgUrl = chooseCardHandler.getImageUrl();
        FXMLLoader fx = new FXMLLoader();
        try {
            fx.setLocation(new URL("File:./src/main/java/it/polimi/ingsw/resources/game-board.fxml"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Parent root = null;
        try {
            root = fx.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        gameBoardHandler = fx.getController();
        gameBoardHandler.init(windowStage, scene, controller, username, this);
        gameBoardHandler.setWindowPatternCardImg(imgUrl);
        gameBoardHandler.setToolCards(toolCardsList);
        gameBoardHandler.setReserve(dicesList);
        gameBoardHandler.setTextArea("Now it's your turn!");
        gameBoardHandler.setPrivateCard(privateCard);
        gameBoardHandler.setPublicCards(publicCardsList);
        gameBoardHandler.createLabelsMap();
        gameBoardHandler.initializeLabels(players);
        gameBoardHandler.initializeFavorTokens(otherFavorTokensMap);
        gameBoardHandler.initializeSchemeCards(otherSchemeCardsMap);
    }

    public Boolean isMyTurn() {
        return myTurn;
    }

    public List<String> getDicesList() {
        return dicesList;
    }

    public Map<String, WindowPatternCard> getOtherSchemeCardsMap() {
        return otherSchemeCardsMap;
    }

}