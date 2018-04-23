package it.polimi.ingsw.model.gameobjects;

public class PlayerSingleplayer extends Player {

    public PlayerSingleplayer(String name, Room room){
        super(name, room);
        this.setColor(Colors.NONE);
    }

    @Override
    public void setSchemeCard(WindowPatternCard schemeCard) { this.schemeCard = schemeCard; }
}
