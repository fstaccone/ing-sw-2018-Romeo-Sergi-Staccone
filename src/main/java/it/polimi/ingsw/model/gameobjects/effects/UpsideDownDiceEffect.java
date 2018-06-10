package it.polimi.ingsw.model.gameobjects.effects;

import it.polimi.ingsw.model.gamelogic.MatchMultiplayer;
import it.polimi.ingsw.model.gameobjects.Dice;
import it.polimi.ingsw.model.gamelogic.Match;
import it.polimi.ingsw.model.gameobjects.Player;
import it.polimi.ingsw.model.gameobjects.PlayerMultiplayer;
import it.polimi.ingsw.socket.responses.Response;
import it.polimi.ingsw.socket.responses.ToolCardUsedByOthersResponse;

import java.rmi.RemoteException;

public class UpsideDownDiceEffect implements Effect {

    private int price;
    private MatchMultiplayer m;
    private PlayerMultiplayer p;

    public UpsideDownDiceEffect() {
        price = 1;
    }

    @Override
    public boolean applyEffect(Player player, Match match) {

        PlayerMultiplayer p = (PlayerMultiplayer) player;
        MatchMultiplayer m=(MatchMultiplayer) match;
        this.m=m;
        this.p=p;

        if(p.getNumFavorTokens() >= price) {
            if (player.getDice() < match.getBoard().getReserve().getDices().size()) {//CONTROLLO SU DICE!=NULL?

                int value = match.getBoard().getReserve().getDices().get(player.getDice()).getValue();
                switch (value) {
                    case 1:
                        match.getBoard().getReserve().getDices().get(player.getDice()).setValue(6);
                        p.setNumFavorTokens(p.getNumFavorTokens() - price);
                        price = 2;
                        notifytoOthers();
                        return true;
                    case 2:
                        match.getBoard().getReserve().getDices().get(player.getDice()).setValue(5);
                        p.setNumFavorTokens(p.getNumFavorTokens() - price);
                        price = 2;
                        notifytoOthers();
                        return true;
                    case 3:
                        match.getBoard().getReserve().getDices().get(player.getDice()).setValue(4);
                        p.setNumFavorTokens(p.getNumFavorTokens() - price);
                        price = 2;
                        notifytoOthers();
                        return true;
                    case 4:
                        match.getBoard().getReserve().getDices().get(player.getDice()).setValue(3);
                        p.setNumFavorTokens(p.getNumFavorTokens() - price);
                        price = 2;
                        notifytoOthers();
                        return true;
                    case 5:
                        match.getBoard().getReserve().getDices().get(player.getDice()).setValue(2);
                        p.setNumFavorTokens(p.getNumFavorTokens() - price);
                        price = 2;
                        notifytoOthers();
                        return true;
                    case 6:
                        match.getBoard().getReserve().getDices().get(player.getDice()).setValue(1);
                        p.setNumFavorTokens(p.getNumFavorTokens() - price);
                        price = 2;
                        notifytoOthers();
                        return true;
                    default:
                        return false;
                }
            } else return false;
        }else return false;
    }

    private void notifytoOthers(){
        //NOTIFY TO OTHERS
        Response response = new ToolCardUsedByOthersResponse( p.getName(),10);
        for (PlayerMultiplayer otherPlayer : (m.getPlayers())) {
            if (!otherPlayer.getName().equals(p.getName())) {
                if (m.getRemoteObservers().get(otherPlayer) != null) {
                    try {
                        m.getRemoteObservers().get(otherPlayer).onToolCardUsedByOthers( p.getName(),10);
                    } catch (RemoteException e) {
                        m.getLobby().disconnect(otherPlayer.getName());
                        System.out.println("Player " + p.getName() + " disconnected!");
                    }
                }
                m.notifyToSocketClient(otherPlayer, response);
            }
        }
    }

}
