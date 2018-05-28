package it.polimi.ingsw;

import it.polimi.ingsw.socket.ClientController;
import java.io.*;
import java.util.List;


public class SocketCli implements Serializable, MatchObserver {

    private Cli cli;
    private String username;
    private transient ClientController clientController;

    public SocketCli(String username, ClientController clientController, boolean single) {
        this.cli=new Cli(username,null,clientController,single);
        clientController.setSocketCli(this);
        this.username = username;
        this.clientController = clientController;
        cli.printWelcome();
    }

    //todo
    public void reconnect() {
        //clientController.request(new ReconnectRequest(username));
    }

    @Override
    public void onPlayers(List<String> playersNames) {
       cli.onPlayers(playersNames);
    }


    @Override
    public void onYourTurn(boolean yourTurn, String string) {
        cli.onYourTurn(yourTurn,string);
    }

    @Override
    public void onReserve(String string) {
        cli.onReserve(string);
    }

    @Override
    public void onWindowChoise(List<String> windows) {
        cli.onWindowChoise(windows);
    }

    @Override
    public void onAfterWindowChoise() {
        cli.onAfterWindowChoise();
    }

    @Override
    public void onShowWindow(String window) {
        cli.onShowWindow(window);
    }

    @Override
    public void onOtherTurn(String name) {
        cli.onOtherTurn(name);
    }

    @Override //todo PAOLO
    public void onInitialization(String toolcards, String publicCards, String privateCard) {
        cli.onInitialization(toolcards,publicCards,privateCard);
    }


    @Override
    public void onPlayerExit(String name) {
        cli.onPlayerExit(name);
    }

    @Override
    public void onPlayerReconnection(String name) {
        cli.onPlayerReconnection(name);
    }

    @Override
    public void onShowTrack(String track) {
       cli.onShowTrack(track);
    }

    @Override
    public void onShowPrivateCard() {
       cli.onShowPrivateCard();
    }

    @Override
    public void onShowPublicCards() {
        cli.onShowPublicCards();
    }

    @Override
    public void onGameClosing() {
        cli.onGameClosing();
    }

    @Override
    public void onShowToolCards() {
        cli.onShowToolCards();
    }
}




