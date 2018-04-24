package it.polimi.ingsw.model.gameobjects.effects;

import it.polimi.ingsw.model.gameobjects.*;
import it.polimi.ingsw.model.gameobjects.windowpatterncards.KaleidoscopicDream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DifferentColorsInAColumnTest {
    private KaleidoscopicDream schemeCard;
    private PublicObjectiveCard publicCard;
    private Player player;
    private Match match;
    private Room room;
    @Before
    public void before() {
        room = mock(Room.class);
        match=mock(Match.class);
        player = new PlayerMultiplayer("player", room);
        schemeCard = new KaleidoscopicDream();
        player.setSchemeCard(schemeCard);

        Dice dy= new Dice(Colors.YELLOW);
        dy.setValue(1);

        Dice dg= new Dice(Colors.GREEN);
        dg.setValue(2);

        Dice dr= new Dice(Colors.RED);
        dr.setValue(3);

        Dice db= new Dice(Colors.VIOLET);
        when(db.getValue()).thenReturn(4);
        when(db.getColor()).thenReturn(Colors.BLUE);

        Dice dv=mock(Dice.class);
        when(dv.getValue()).thenReturn(2);
        when(dv.getColor()).thenReturn(Colors.VIOLET);

        player.getSchemeCard().putFirstDice(dy,0,0);
        player.getSchemeCard().putDice(dg,1,0);
        player.getSchemeCard().putDice(dr,2,0);
        player.getSchemeCard().putDice(dv,3,0);

        publicCard = new PublicObjectiveCard("Colori diversi - Colonna");
    }

    @Test
    public void checkPoints() {
        publicCard.useCard(player, match);
        Assert.assertEquals(5,player.getPoints());
    }
}