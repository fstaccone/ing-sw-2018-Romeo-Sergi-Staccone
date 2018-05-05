package it.polimi.ingsw.model.gameobjects;

public class PlayerSingleplayer extends Player {

    public PlayerSingleplayer(String name){
        super(name);
        this.setColor(Colors.NONE);
    }

    @Override
    public void setSchemeCard(WindowPatternCard schemeCard) { this.schemeCard = schemeCard; }

    @Override
    public void useToolCard(ToolCard chosenToolCardToUse) {

    }
}
