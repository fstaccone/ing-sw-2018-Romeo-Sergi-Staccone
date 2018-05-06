package it.polimi.ingsw.model.gameobjects.effects;

import it.polimi.ingsw.Room;
import it.polimi.ingsw.model.gameobjects.*;
import it.polimi.ingsw.model.gameobjects.windowpatterncards.LuzCelestial;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MediumShadesTest {
    private LuzCelestial schemeCard;
    private PublicObjectiveCard publicCard;
    private Player player;
    private Match match;
    private Room room;
    @Before
    public void Before() {
        room = mock(Room.class);
        match = mock(Match.class);
        // modificato in seguito all'introduzione di Lobby
        player = new PlayerMultiplayer("player");
        schemeCard = new LuzCelestial();
        player.setSchemeCard(schemeCard);

        Dice dg = new Dice(Colors.GREEN);
        dg.setValue(4);

        Dice db = new Dice(Colors.BLUE);
        db.setValue(3);

        Dice dy = new Dice(Colors.YELLOW);
        dy.setValue(4);

        Dice dr = new Dice(Colors.RED);
        dr.setValue(3);

        player.getSchemeCard().putFirstDice(dg, 0, 0);
        player.getSchemeCard().putDice(db, 0, 1);
        player.getSchemeCard().putDice(dy, 1, 2);
        player.getSchemeCard().putDice(dr, 2, 2);
        player.getSchemeCard().putDice(dy, 2, 1);
        publicCard = new PublicObjectiveCard("Sfumature medie");
    }

    @Test
    public void checkPoints() {
        publicCard.useCard(player, match);
        System.out.println(player.getSchemeCard().toString());
        Assert.assertEquals(4,player.getPoints());
    }
}
