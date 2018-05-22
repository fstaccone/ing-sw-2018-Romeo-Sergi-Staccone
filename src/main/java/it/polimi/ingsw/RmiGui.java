package it.polimi.ingsw;

import it.polimi.ingsw.control.RemoteController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RmiGui extends UnicastRemoteObject implements MatchObserver {

    private String username;
    private RemoteController controller;
    boolean myTurn;

    public RmiGui(String username, RemoteController controller) throws RemoteException {
        super();
        this.username = username;
        this.controller = controller;
        this.myTurn = false;
    }

    @Override
    public void onPlayers(List<String> playersNames) throws RemoteException {

    }

    @Override
    public void onYourTurn(boolean isMyTurn, String string) throws RemoteException {

    }


    @Override
    public void onReserve(String string) throws RemoteException {

    }

    public void launch(){

    }

    @Override
    public void onShowWindow(String window) throws RemoteException {

    }

    @Override
    public void onOtherTurn(String name) throws RemoteException {
    }
    
    @Override
    public void onShowToolCards(List<String> cards) throws RemoteException {
    }

    @Override
    public void onToolCards(String string) throws RemoteException {

    }

    @Override
    public void onWindowChoise(List<String> windows) throws RemoteException {

    }
}