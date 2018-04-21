package it.polimi.ingsw.model.gameobjects.effects;

import it.polimi.ingsw.model.gameobjects.Match;
import it.polimi.ingsw.model.gameobjects.Player;

import java.io.Serializable;

public interface Effect extends Serializable{
    public void applyEffect(Player caller, Match match);
}
