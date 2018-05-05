package it.polimi.ingsw.model.gameobjects;

import java.util.List;

public class MatchSinglePlayer extends Match{

    private int matchId;
    private int difficulty;
    PlayerSingleplayer player;

    public MatchSinglePlayer(int matchId, String playerName, int difficulty) {
        super();
        this.matchId = matchId;
        this.difficulty = difficulty;
        this.decksContainer = new DecksContainer(1);
        this.player = new PlayerSingleplayer(playerName);
    }

    public int getMatchId() { return matchId; }

    @Override
    public void gameInit() {

    }

    @Override
    public void drawPrivateObjectiveCards() {

    }

    @Override
    public void proposeWindowPatternCards() {

    }

    @Override
    public void drawPublicObjectiveCards() {

    }

    @Override
    public void drawToolCards() {

    }

    @Override
    public void nextRound() {

    }

    @Override
    public void calculateFinalScore() {

    }
}
