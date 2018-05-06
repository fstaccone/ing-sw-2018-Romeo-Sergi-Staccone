package it.polimi.ingsw.model.gameobjects.effects;

import it.polimi.ingsw.Room;
import it.polimi.ingsw.model.gameobjects.*;
import it.polimi.ingsw.model.gameobjects.windowpatterncards.LuzCelestial;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ColoredDiagonalsTest {
    private LuzCelestial schemeCard;
    private PublicObjectiveCard publicCard;
    private Player player;
    private Match match;
    private Room room;
    @Before
    public void Before() {
        room = mock(Room.class);
        match = mock(Match.class);
        player = new PlayerMultiplayer("player", room);
        schemeCard = new LuzCelestial();
        player.setSchemeCard(schemeCard);

        Dice dice1 = new Dice(Colors.GREEN);
        dice1.setValue(4);


        Dice dice2 = new Dice(Colors.BLUE);
        dice2.setValue(2);

        Dice dice3 = new Dice(Colors.YELLOW);
        dice3.setValue(4);

        Dice dice4 = new Dice(Colors.RED);
        dice4.setValue(3);

        player.setPickedDice(dice1);
        player.getSchemeCard().putFirstDice(player.getPickedDice(), 0, 0);
        player.setPickedDice(dice2);
        player.getSchemeCard().putDice(player.getPickedDice(), 0, 1);
        player.setPickedDice(dice1);
        player.getSchemeCard().putDice(player.getPickedDice(), 1, 1);
        player.setPickedDice(dice2);
        player.getSchemeCard().putDice(player.getPickedDice(), 1, 2);
        player.setPickedDice(dice4);
        player.getSchemeCard().putDice(player.getPickedDice(), 2, 1);
        player.getSchemeCard().putDice(player.getPickedDice(), 0, 2);
        player.setPickedDice(dice3);
        player.getSchemeCard().putDice(player.getPickedDice(), 3, 1);
        player.setPickedDice(dice2);
        player.getSchemeCard().putDice(player.getPickedDice(), 3, 2);
        player.getSchemeCard().putDice(player.getPickedDice(), 2, 3);
        player.setPickedDice(dice3);
        player.getSchemeCard().putDice(player.getPickedDice(),2,2);
        player.setPickedDice(dice1);
        player.getSchemeCard().putDice(player.getPickedDice(), 2, 4);
        player.getSchemeCard().putDice(player.getPickedDice(), 3, 3);
        player.setPickedDice(dice2);
        player.getSchemeCard().putDice(player.getPickedDice(), 3, 4);

        publicCard = new PublicObjectiveCard("Diagonali colorate");
    }

    @Test
    public void checkPoints() {
        publicCard.useCard(player, match);
        System.out.println(schemeCard.toString());
        Assert.assertEquals(11,player.getPoints());
    }
}
