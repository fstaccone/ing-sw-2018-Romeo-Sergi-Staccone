package it.polimi.ingsw.model.gameobjects.effects;

import it.polimi.ingsw.Room;
import it.polimi.ingsw.model.gameobjects.*;
import it.polimi.ingsw.model.gameobjects.windowpatterncards.KaleidoscopicDream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.mock;

public class IncrDecrDiceValueTest {
    private KaleidoscopicDream schemeCard;
    private ToolCard toolCard;
    private Player player;
    private Match match;
    private Room room;
    @Before
    public void before() {
        room = mock(Room.class);
        match = mock(Match.class);
        player = new PlayerMultiplayer("player", room);
        schemeCard = new KaleidoscopicDream();
        player.setSchemeCard(schemeCard);
        Dice dice = new Dice(Colors.BLUE);
        dice.setValue(6);
        player.setPickedDice(dice);
        toolCard = new ToolCard("Pinza Sgrossatrice");
        ByteArrayInputStream in = new ByteArrayInputStream("+\n-".getBytes());
        System.setIn(in);
    }
    @Test
    public void checkDice(){
        toolCard.useCard(player, match);
        Assert.assertEquals(5,player.getPickedDice().getValue());
    }
}
