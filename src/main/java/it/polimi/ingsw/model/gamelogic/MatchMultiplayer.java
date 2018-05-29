package it.polimi.ingsw.model.gamelogic;

import it.polimi.ingsw.ConnectionStatus;
import it.polimi.ingsw.Lobby;
import it.polimi.ingsw.MatchObserver;
import it.polimi.ingsw.model.gameobjects.*;
import it.polimi.ingsw.socket.responses.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class MatchMultiplayer extends Match implements Runnable {

    private Lobby lobby;

    public Lobby getLobby() {
        return lobby;
    }

    private Map<PlayerMultiplayer, MatchObserver> remoteObservers;
    private Map<PlayerMultiplayer, ObjectOutputStream> socketObservers;

    private List<PlayerMultiplayer> ranking;

    private Thread localThread;
    private boolean started;
    private int matchId;
    private TurnManager turnManager;
    private List<PlayerMultiplayer> players;
    private List<WindowPatternCard> windowsProposed;
    private PlayerMultiplayer winner;


    public MatchMultiplayer(int matchId, List<String> clients, int turnTime, Map<String, ObjectOutputStream> socketsOut, Lobby lobby) {

        super();
        this.lobby = lobby;
        this.matchId = matchId;
        started = false;

        players = new ArrayList<>();
        remoteObservers = new HashMap<>();
        socketObservers = new HashMap<>();
        turnManager = new TurnManager(this, turnTime);
        decksContainer = new DecksContainer(clients.size());
        board = new Board(this, decksContainer.getToolCardDeck().getPickedCards(), decksContainer.getPublicObjectiveCardDeck().getPickedCards());

        initializePlayers(clients, socketsOut);

        if (this.players.size() == this.socketObservers.size()) {
            localThread = new Thread(this);
            localThread.start();
            started = true;
        }

        // debug
        System.out.println("New multiplayer matchId: " + matchId);
    }

    private void initializePlayers(List<String> clients, Map<String, ObjectOutputStream> socketsOut) {
        clients.forEach(client -> {
            PlayerMultiplayer player = new PlayerMultiplayer(client, this);
            this.players.add(player);
            if (socketsOut.size() != 0) { // ha senso questo controllo?
                for (String name : socketsOut.keySet()) {
                    if (name.equals(client)) {
                        this.socketObservers.put(player, socketsOut.get(name));
                    }
                }
            }
        });
    }

    // getters
    public List<WindowPatternCard> getWindowsProposed() {
        return windowsProposed;
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public Map<PlayerMultiplayer, MatchObserver> getRemoteObservers() {
        return remoteObservers;
    }

    public Map<PlayerMultiplayer, ObjectOutputStream> getSocketObservers() {
        return socketObservers;
    }

    public List<PlayerMultiplayer> getPlayers() {
        return players;
    }

    public void windowsToBeProposed() {
        windowsProposed = decksContainer.getWindowPatternCardDeck().getPickedCards().subList(0, 4);
    }


    /**
     * It checks if a player is CONNECTED and
     *
     * @return the number of CONNECTED players
     */
    public int checkConnection() {
        return (int) players.stream().filter(p -> p.getStatus().equals(ConnectionStatus.CONNECTED)).count();
    }


    // game's initialisation
    @Override
    public void gameInit() {

        // todo: revision of the creation of this arraylist
        List<String> playersNames = new ArrayList<>();
        players.forEach(p -> playersNames.add(p.getName()));

        // notification to remote observers
        for (PlayerMultiplayer p : remoteObservers.keySet()) {
            try {
                remoteObservers.get(p).onPlayers(playersNames);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        //notification to sockets
        ActualPlayersResponse response = new ActualPlayersResponse(playersNames);
        for (PlayerMultiplayer p : socketObservers.keySet()) {
            try {
                socketObservers.get(p).writeObject(response);
                socketObservers.get(p).reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // actions to be performed once only
        this.roundCounter = 0;
        this.assignColors();

        // it shuffles players to determine the sequence flow of rounds. Furthermore the first player is always in the first position.
        Collections.shuffle(this.players);

        this.drawPrivateObjectiveCards();

        this.turnManager.run();
    }

    // Assegna il colore ai giocatori in modo casuale
    private void assignColors() {

        // Creation of a list of colors (without the special value NONE) to be assigned randomly to players
        Random rand = new Random();
        int index;
        List<Colors> colors = new ArrayList<>();

        // This block creates an ArrayList of colors. A color, once assigned, must be removed from the ArrayList
        for (Colors c : Colors.values()) {
            if (!c.equals(Colors.NONE)) {
                colors.add(c);
            }
        }

        for (Player p : players) {
            index = rand.nextInt(colors.size());
            p.setColor(colors.get(index));  // Da testare, non ne sono convinto
            colors.remove(index);
        }
    }


    @Override
    public void calculateFinalScore() {

        List<String> rankingNames;
        List<Integer> rankingValues;

        ranking = new ArrayList<>();
        rankingNames = new ArrayList<>();
        rankingValues = new ArrayList<>();

        for (PlayerMultiplayer p : players) {

            // points assigned by the private objective card
            p.getPrivateObjectiveCard().useCard(p);

            // points assigned by public objective cards
            for (int i = 0; i < board.getPickedPublicObjectiveCards().size(); i++) {
                board.getPickedPublicObjectiveCards().get(i).useCard(p, this);
            }

            // points due to free cells
            p.setPoints(p.getPoints() - p.getSchemeCard().countFreeSquares());

            // points due to remaining favor tokens
            p.setPoints(p.getPoints() + p.getNumFavorTokens());

            addInOrder(p);
        }

        winner = theWinnerIs();

        for (int i = 0; i < ranking.size(); i++) {
            rankingNames.add(i, ranking.get(i).getName());
            rankingValues.add(i, ranking.get(i).getPoints());
        }

        ranking.clear();

        for (PlayerMultiplayer p : players) {
            if (remoteObservers.get(p) != null) {
                try {
                    remoteObservers.get(p).onGameEnd(winner.getName(), rankingNames, rankingValues);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if (socketObservers.get(p) != null) {
                try {
                    socketObservers.get(p).writeObject(new GameEndResponse(winner.getName(), rankingNames, rankingValues));
                    socketObservers.get(p).reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addInOrder(PlayerMultiplayer p) {
        int i = 0;

        if (ranking.isEmpty()) {
            ranking.add(p);
        } else {
            while (i < ranking.size()) {
                if (p.getPoints() > ranking.get(i).getPoints()) {
                    break;
                }
                i++;
            }
            ranking.add(i, p);
        }
    }


    public void afterReconnection(String name) {
        String toolCards = decksContainer.getToolCardDeck().getPickedCards().toString();
        String publicCards = decksContainer.getPublicObjectiveCardDeck().getPickedCards().toString();

        if (remoteObservers.get(getPlayer(name)) != null) {
            try {
                remoteObservers.get(getPlayer(name)).onAfterReconnection(toolCards, publicCards, getPlayer(name).getPrivateObjectiveCard().toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (socketObservers.get(getPlayer(name)) != null) {
            try {
                socketObservers.get(getPlayer(name)).writeObject(new AfterReconnectionResponse(toolCards, publicCards, getPlayer(name).getPrivateObjectiveCard().toString()));
                socketObservers.get(getPlayer(name)).reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showTrack(String name) {
        if (remoteObservers.get(getPlayer(name)) != null) {
            try {
                remoteObservers.get(getPlayer(name)).onShowTrack(board.getRoundTrack().toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            if (socketObservers.get(getPlayer(name)) != null) {
                try {
                    socketObservers.get(getPlayer(name)).writeObject(new ShowTrackResponse(board.getRoundTrack().toString()));
                    socketObservers.get(getPlayer(name)).reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private PlayerMultiplayer theWinnerIs() {
        List<PlayerMultiplayer> firstPlayoff;
        List<PlayerMultiplayer> secondPlayoff;
        List<PlayerMultiplayer> thirdPlayoff;

        firstPlayoff = new ArrayList<>();

        firstPlayoff.add(ranking.get(0));

        // compare points
        for (int i = 0; i < ranking.size() - 1; i++) {
            if (ranking.get(i).getPoints() == ranking.get(i + 1).getPoints()) {
                firstPlayoff.add(ranking.get(i + 1));
            } else {
                break;
            }
        }

        if (firstPlayoff.size() == 1) {
            return firstPlayoff.get(0);
        } else {
            // compare points by private objective card
            secondPlayoff = new ArrayList<>();

            int maxPrivate;
            maxPrivate = firstPlayoff.get(0).getPointsByPrivateObjective();

            // find the max value
            for (int i = 1; i < firstPlayoff.size(); i++) {
                if (firstPlayoff.get(i).getPointsByPrivateObjective() > maxPrivate) {
                    maxPrivate = firstPlayoff.get(i).getPointsByPrivateObjective();
                }
            }

            // find players admitted to the second playoff
            for (PlayerMultiplayer pla : firstPlayoff) {
                if (pla.getPointsByPrivateObjective() == maxPrivate) {
                    secondPlayoff.add(pla);
                }
            }

            if (secondPlayoff.size() == 1) {
                return secondPlayoff.get(0);
            } else {
                thirdPlayoff = new ArrayList<>();

                int maxTokens;
                maxTokens = secondPlayoff.get(0).getNumFavorTokens();

                // find the max value
                for (int i = 1; i < firstPlayoff.size(); i++) {
                    if (secondPlayoff.get(i).getNumFavorTokens() > maxTokens) {
                        maxTokens = secondPlayoff.get(i).getNumFavorTokens();
                    }
                }

                // find players admitted to the third playoff
                for (PlayerMultiplayer p : secondPlayoff) {
                    if (p.getNumFavorTokens() == maxTokens) {
                        thirdPlayoff.add(p);
                    }
                }

                if (thirdPlayoff.size() == 1) {
                    return thirdPlayoff.get(0);
                } else {
                    return lastPlayoff(thirdPlayoff);
                }
            }
        }
    }

    /**
     * Determines the winner if there are players with the same points at the end of other playoffs
     *
     * @param playoff list of players with the same points by tokens
     * @return the player which is in playoff and played before other players in playoff in the last round
     */
    private PlayerMultiplayer lastPlayoff(List<PlayerMultiplayer> playoff) {
        for (PlayerMultiplayer p : players) {
            if (playoff.contains(p)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void drawPrivateObjectiveCards() {
        for (PlayerMultiplayer p : players) {
            p.setPrivateObjectiveCard(this.decksContainer.getPrivateObjectiveCardDeck().getPickedCards().remove(0));
        }
    }

    @Override
    public void run() {
        gameInit();
    }

    public void observeMatchRemote(MatchObserver observer, String username) {

        remoteObservers.put(getPlayer(username), observer);

        if (!started) {
            if (this.players.size() == this.remoteObservers.size() + this.socketObservers.size()) {
                localThread = new Thread(this);
                localThread.start();
                started = true;
            }
        }
    }

    public void showPlayers(String name) {
        // RMI
        if (remoteObservers.get(getPlayer(name)) != null) {
            try {
                remoteObservers.get(getPlayer(name)).onPlayers(players.stream().map(Player::getName).collect(Collectors.toList()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (socketObservers.get(getPlayer(name)) != null) {
            try {
                socketObservers.get(getPlayer(name)).writeObject(new ActualPlayersResponse(players.stream().map(Player::getName).collect(Collectors.toList())));
                socketObservers.get(getPlayer(name)).reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public PlayerMultiplayer getPlayer(String name) {

        for (PlayerMultiplayer p : players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void setWindowPatternCard(String name, int index) {
        getPlayer(name).setSchemeCard(windowsProposed.get(index));
        decksContainer.getWindowPatternCardDeck().getPickedCards().removeAll(windowsProposed);
        getPlayer(name).setSchemeCardSet(true);
        setWindowChosen(true);

        tokensToBeUpdated(true, name);
        schemeCardsToBeUpdated(true, name);

        if (remoteObservers.get(getPlayer(name)) != null) {
            try {
                remoteObservers.get(getPlayer(name)).onAfterWindowChoise();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (socketObservers.get(getPlayer(name)) != null) {
            try {
                socketObservers.get(getPlayer(name)).writeObject(new AfterWindowChoiseResponse());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        synchronized (getLock()) { // è più giusto mettere lock protected?
            getLock().notifyAll();
        }
    }

    @Override
    public boolean placeDice(String name, int index, int x, int y) {
        if (!isDiceAction()) {
            boolean result;
            result = getPlayer(name).getSchemeCard().putDice(board.getReserve().getDices().get(index), x, y);
            setDiceAction(result);

            if (result)
                board.getReserve().getDices().remove(index);
            reserveToBeUpdated(result);
            schemeCardsToBeUpdated(result, name);

            synchronized (getLock()) {
                getLock().notifyAll();
            }
            return result;
        }
        return false;
    }


    public boolean useToolCard1(int diceChosen, String incrOrDecr, String name) {
        if (!isToolAction()) {
            getPlayer(name).setDice(diceChosen);
            getPlayer(name).setChoise(incrOrDecr);
            boolean result = getBoard().findAndUseToolCard(1, getPlayer(name), this);
            tokensToBeUpdated(result, name);
            reserveToBeUpdated(result);
            setToolAction(result);
            return result;
        } else {
            return false;
        }
    }

    public boolean useToolCard2or3(int n, int startX, int startY, int finalX, int finalY, String name) {
        if (!isToolAction()) {
            getPlayer(name).setStartX1(startX);
            getPlayer(name).setStartY1(startY);
            getPlayer(name).setFinalX1(finalX);
            getPlayer(name).setFinalY1(finalY);
            boolean result = getBoard().findAndUseToolCard(n, getPlayer(name), this);
            tokensToBeUpdated(result, name);
            schemeCardsToBeUpdated(result, name);
            setToolAction(result);
            return result;
        } else {
            return false;
        }
    }

    public boolean useToolCard4(int startX1, int startY1, int finalX1, int finalY1, int startX2, int startY2, int finalX2, int finalY2, String name) {
        if (!isToolAction()) {
            getPlayer(name).setStartX1(startX1);
            getPlayer(name).setStartY1(startY1);
            getPlayer(name).setFinalX1(finalX1);
            getPlayer(name).setFinalY1(finalY1);
            getPlayer(name).setStartX2(startX2);
            getPlayer(name).setStartY2(startY2);
            getPlayer(name).setFinalX2(finalX2);
            getPlayer(name).setFinalY2(finalY2);
            boolean result = getBoard().findAndUseToolCard(4, getPlayer(name), this);
            tokensToBeUpdated(result, name);
            schemeCardsToBeUpdated(result, name);
            setToolAction(result);
            return result;
        } else {
            return false;
        }
    }

    public boolean useToolCard5(int diceChosen, int roundChosen, int diceChosenFromRound, String name) {
        if (!isToolAction()) {
            getPlayer(name).setDice(diceChosen);
            getPlayer(name).setRound(roundChosen);
            getPlayer(name).setDiceChosenFromRound(diceChosenFromRound);
            boolean result = getBoard().findAndUseToolCard(5, getPlayer(name), this);
            tokensToBeUpdated(result, name);
            reserveToBeUpdated(result);
            setToolAction(result);
            return result;
        } else {
            return false;
        }
    }

    public boolean useToolCard6(int diceChosen, String name) {
        if (!isToolAction()) {
            getPlayer(name).setDice(diceChosen);
            boolean result = getBoard().findAndUseToolCard(6, getPlayer(name), this);
            tokensToBeUpdated(result, name);
            reserveToBeUpdated(result);
            setToolAction(result);
            return result;
        } else {
            return false;
        }
    }

    public boolean useToolCard7(String name) {
        if (!isToolAction()) {
            boolean result;
            result = getBoard().findAndUseToolCard(7, getPlayer(name), this);
            tokensToBeUpdated(result, name);
            reserveToBeUpdated(result);
            setToolAction(result);
            return result;
        } else {
            return false;
        }
    }

    public boolean useToolCard8(String name) {
        if (!isToolAction()) {
            boolean result;
            result = getBoard().findAndUseToolCard(8, getPlayer(name), this);
            tokensToBeUpdated(result, name);
            setToolAction(result);
            return result;
        } else {
            return false;
        }
    }

    public boolean useToolCard9(int diceChosen, int finalX1, int finalY1, String name) {
        if (!isToolAction()) {
            getPlayer(name).setDice(diceChosen);
            getPlayer(name).setFinalX1(finalX1);
            getPlayer(name).setFinalY1(finalY1);
            boolean result = getBoard().findAndUseToolCard(9, getPlayer(name), this);
            tokensToBeUpdated(result, name);
            reserveToBeUpdated(result);
            schemeCardsToBeUpdated(result, name);
            setToolAction(result);
            return result;
        } else {
            return false;
        }
    }

    public boolean useToolCard10(int diceChosen, String name) {
        if (!isToolAction()) {
            getPlayer(name).setDice(diceChosen);
            boolean result = getBoard().findAndUseToolCard(10, getPlayer(name), this);
            tokensToBeUpdated(result, name);
            reserveToBeUpdated(result);
            setToolAction(result);
            return result;
        } else return false;
    }

    private void reserveToBeUpdated(boolean reserveToBeUpdated) {
        if (reserveToBeUpdated) {
            Response response = new UpdateReserveResponse(board.getReserve().getDices().toString());
            for (PlayerMultiplayer player : players) {
                if (remoteObservers.get(player) != null) {
                    try {
                        remoteObservers.get(player).onReserve(board.getReserve().getDices().toString());
                    } catch (RemoteException e) {
                        lobby.disconnect(player.getName());
                    }
                }
                if (socketObservers.get(player) != null) {
                    try {
                        socketObservers.get(player).writeObject(response);
                        socketObservers.get(player).reset();
                    } catch (IOException e) {
                        lobby.disconnect(player.getName());
                    }
                }
            }
        }
    }

    private void tokensToBeUpdated(boolean result, String name) {
        if (result) {
            if (remoteObservers.get(getPlayer(name)) != null) {
                try {
                    remoteObservers.get(getPlayer(name)).onMyFavorTokens(getPlayer(name).getNumFavorTokens());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if (socketObservers.get(getPlayer(name)) != null) {
                try {
                    socketObservers.get(getPlayer(name)).writeObject(new MyFavorTokensResponse(getPlayer(name).getNumFavorTokens()));
                    socketObservers.get(getPlayer(name)).reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Response response = new OtherFavorTokensResponse(getPlayer(name).getNumFavorTokens(), name);
            for (PlayerMultiplayer otherPlayer : players) {
                if (!otherPlayer.getName().equals(name)) {
                    if (remoteObservers.get(otherPlayer) != null) {
                        try {
                            remoteObservers.get(otherPlayer).onOtherFavorTokens(getPlayer(name).getNumFavorTokens(), name);
                        } catch (RemoteException e) {
                            lobby.disconnect(otherPlayer.getName());
                        }
                    }
                    if (socketObservers.get(otherPlayer) != null) {
                        try {
                            socketObservers.get(otherPlayer).writeObject(response);
                            socketObservers.get(otherPlayer).reset();
                        } catch (IOException e) {
                            lobby.disconnect(otherPlayer.getName());
                        }
                    }
                }
            }
        }
    }

    private void schemeCardsToBeUpdated(boolean result, String name) {
        if (result) {

            if (remoteObservers.get(getPlayer(name)) != null) {
                try {
                    remoteObservers.get(getPlayer(name)).onMyWindow(getPlayer(name).getSchemeCard().toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if (socketObservers.get(getPlayer(name)) != null) {
                try {
                    socketObservers.get(getPlayer(name)).writeObject(new MyWindowResponse(getPlayer(name).getSchemeCard().toString()));
                    socketObservers.get(getPlayer(name)).reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Response response = new OtherSchemeCardsResponse(getPlayer(name).getSchemeCard().toString(), name);
            for (PlayerMultiplayer otherPlayer : players) {
                if (!otherPlayer.getName().equals(name)) {
                    if (remoteObservers.get(otherPlayer) != null) {
                        try {
                            remoteObservers.get(otherPlayer).onOtherSchemeCards(getPlayer(name).getSchemeCard().toString(), name);
                        } catch (RemoteException e) {
                            lobby.disconnect(otherPlayer.getName());
                        }
                    }
                    if (socketObservers.get(otherPlayer) != null) {
                        try {
                            socketObservers.get(otherPlayer).writeObject(response);
                            socketObservers.get(otherPlayer).reset();
                        } catch (IOException e) {
                            lobby.disconnect(otherPlayer.getName());
                        }
                    }
                }
            }
        }
    }

}
