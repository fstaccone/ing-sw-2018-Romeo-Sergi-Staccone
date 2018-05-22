package it.polimi.ingsw.model.gamelogic;

import it.polimi.ingsw.*;
import it.polimi.ingsw.model.gameobjects.Player;
import it.polimi.ingsw.model.gameobjects.PlayerMultiplayer;
import it.polimi.ingsw.model.gameobjects.WindowPatternCard;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

public class TurnManager implements Runnable {

    private Timer turnTimer;
    TurnTimer task;
    private int turnTime;
    private MatchMultiplayer match;

    private boolean expired; // it's used to avoid double canceling of timer

    public TurnManager(MatchMultiplayer match, int turnTime) {
        this.turnTime = turnTime;
        this.match = match;
    }

    @Override
    public void run() {
        try {
            drawWindowPatternCards();
            turnManager();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Remote exception from TurnManager");
        }
    }

    private void drawWindowPatternCards() throws InterruptedException, RemoteException {

        for (int i = 0; i < match.getPlayers().size(); i++) {
            PlayerMultiplayer player=match.getPlayers().get(i);
            match.initializeWindowsToBeProposed(i);
            match.setWindowChosen(false);

            if (player.getStatus() == ConnectionStatus.READY) {

                List<String> list = match.getWindowsProposed()
                        .stream()
                        .map(WindowPatternCard::toString)
                        .collect(Collectors.toList());

                //notification
                rmiObserverNotify(player).onYourTurn(true);
                for(PlayerMultiplayer playerNotInTurn: match.getPlayers())
                    if (playerNotInTurn!=player)
                        rmiObserverNotify(playerNotInTurn).onOtherTurn(player.getName());
                rmiObserverNotify(player).onWindowChoise(list);
                socketObserverNotify(player, new YourTurnResponse(true));
                socketObserverNotify(player, new ProposeWindowResponse(list));

                turnTimer = new Timer();
                task = new TurnTimer(match, player);
                turnTimer.schedule(task, turnTime);

                while (!match.isWindowChosen()) {
                    synchronized (match.getLock()) {
                        match.getLock().wait();
                    }
                }

                if (!expired) {
                    turnTimer.cancel();
                }

                //notification
                rmiObserverNotify(player).onYourTurn(false);
                socketObserverNotify(player,new YourTurnResponse(false));
            }
        }
    }

    private void turnManager() throws InterruptedException, RemoteException {

        TurnTimer task;

        System.out.println("Round " + (match.getCurrentRound() + 1));
        System.out.println("First player: " + match.getPlayers().get(0).getName());

        for (PlayerMultiplayer player : match.getPlayers()) {
            player.setTurnsLeft(2);
        }

        match.getBoard().getReserve().throwDices(match.getBag().pickDices(match.getPlayers().size()));
        
        // first turn
        for (int i=0; i<match.getPlayers().size(); i++) {

            // initialisation of flags to control the turn's flow
            match.setDiceAction(false);
            match.setToolAction(false);
            match.setEndsTurn(false);
            match.setSecondDiceAction(true); // this is useful only when a player can play two dices in the same turn
            PlayerMultiplayer player= match.getPlayers().get(i);

            // debug
            System.out.println("From match : Turn 1 - round " + (match.getCurrentRound() + 1) + " player: " + player.getName());

            if (player.getStatus() == ConnectionStatus.READY) {


                //notification
                rmiObserverNotify(player).onYourTurn(true);
                for(PlayerMultiplayer playerNotInTurn: match.getPlayers())
                    if (playerNotInTurn!=player)
                        rmiObserverNotify(playerNotInTurn).onOtherTurn(player.getName());
                socketObserverNotify(player,new YourTurnResponse(true));

                turnTimer = new Timer();
                task = new TurnTimer(match, player);
                turnTimer.schedule(task, turnTime);

                /**
                 * diceAction e toolAction vengono settati inizialmente a false, se l'azione corrispondente viene
                 * completata con successo viene settato a true il rispettivo flag. Quando saranno entrambi veri
                 * la condizione del ciclo sarà falsa
                 * I metodi utilizzati ed i flag appartengono a match, in modo che possano essere settati a true senza risvegliare
                 * TurnManager, sarà risvegliato solo dopo che una azione è stata completata con successo
                 */
                // wait for user action or for timer
                while (checkCondition()) {
                    synchronized (match.getLock()) {
                        match.getLock().wait();
                    }
                    System.out.println("TurnManager correttamente risvegliato");
                }

                //notification
                rmiObserverNotify(player).onYourTurn(false);
                socketObserverNotify(player,new YourTurnResponse(false));
            }

            if (!expired) {
                turnTimer.cancel();
            }
            player.setTurnsLeft(player.getTurnsLeft() - 1);
        }

        // second turn todo: controllare dopo aver verificato che funzioni
        for (int i = match.getPlayers().size() - 1; i >= 0; i--) {

            PlayerMultiplayer player= match.getPlayers().get(i);

            if (player.getTurnsLeft() > 0 && player.getStatus() == ConnectionStatus.READY) {
                System.out.println("From match : Turn 2 - round " + (match.getCurrentRound() + 1) + " player: " + player.getName());

                //notification
                rmiObserverNotify(player).onYourTurn(true);
                for(PlayerMultiplayer playerNotInTurn: match.getPlayers())
                    if (playerNotInTurn!=player)
                        rmiObserverNotify(playerNotInTurn).onOtherTurn(player.getName());
                socketObserverNotify(player,new YourTurnResponse(true));

                match.setDiceAction(false);
                match.setToolAction(false);
                match.setEndsTurn(false);
                match.setSecondDiceAction(true); // this is useful only when a player can play two dices in the same turn

                turnTimer = new Timer();
                task = new TurnTimer(match, player);
                turnTimer.schedule(task, turnTime);

                // wait for user action or for timer
                while (checkCondition()) {
                    synchronized (match.getLock()) {
                        match.getLock().wait();
                    }
                    // debug
                    System.out.println("TurnManager correttamente risvegliato");
                }

                //notification
                rmiObserverNotify(player).onYourTurn( false);
                socketObserverNotify(player, new YourTurnResponse(false));

            }
            if (!expired) {
                turnTimer.cancel();
            }
            // todo: non ha molto senso, potrebbe averne se controllassimo in fase di testing che dopo questo aggiornamento siano davvero 0
            player.setTurnsLeft(player.getTurnsLeft() - 1);
        }

        // rearrange match.getPlayers() to keep the right order in next round
        // following the idea that the first player in this round will be the last in the next round
        match.getPlayers().add(match.getPlayers().get(0));
        match.getPlayers().remove(0);
        nextRound();
    }

    private MatchObserver rmiObserverNotify(PlayerMultiplayer player){
        if (match.getRemoteObservers().size() != 0) {
            if (match.getRemoteObservers().get(player) != null) {
                return match.getRemoteObservers().get(player);
            }
            else return null;
        }
        else return null;
    }

    private void socketObserverNotify(PlayerMultiplayer player, Response response){
        if (match.getSocketObservers().size() != 0) {
            if (match.getSocketObservers().get(player) != null) {
                try {
                    match.getSocketObservers().get(player).writeObject(response);
                    match.getSocketObservers().get(player).reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // todo: da spiegare (se funziona)
    private boolean checkCondition() {
        return !((match.isToolAction() && match.isDiceAction() && match.isSecondDiceAction()) || match.isEndsTurn());
    }

    private void nextRound() throws InterruptedException, RemoteException {
        match.pushLeftDicesToRoundTrack();
        match.incrementRoundCounter();

        if (match.getCurrentRound() >= 10) {
            //match.calculateFinalScore(); // può stare anche in match
        } else {
            this.turnManager();
        }
    }

    public void setExpiredTrue() {
        this.expired = true;
    }
}
