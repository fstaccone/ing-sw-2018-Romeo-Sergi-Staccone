package it.polimi.ingsw.model.gameobjects;

import java.io.Serializable;

public class Player implements Serializable{
    private final String name;
    private WindowPatternCard schemeCard;
    private Dice pickedDice;
    private int points;

    public Player(String name){
        super();
        this.name = name;
    }

    // getter
    public String getName() {
        return name;
    }
    public Dice getPickedDice() {
        return pickedDice;
    }
    public WindowPatternCard getSchemeCard() {
        return schemeCard;
    }
    public int getPoints() {
        return points;
    }

    // setter
    public void setPickedDice(Dice pickedDice) {
        this.pickedDice = pickedDice;
    }
    public void setSchemeCard(WindowPatternCard schemeCard) {
        this.schemeCard = schemeCard;
    }
    public void setPoints(int points) {
        this.points = points;
    }



    public void useToolCard(ToolCard chosenToolCardToUse){//il controller fa player1.useToolCard(): può passare la carta scelta perchè il controller ha riferimento alla board e pertanto alle pickedToolCards(attributo di board) tra le quali fa scegliere al client quale usare(ammesso che possa-->va fatto un check)(si tratta di un'azione precedente)
        chosenToolCardToUse.useCard(this,this.schemeCard);
    }


}
