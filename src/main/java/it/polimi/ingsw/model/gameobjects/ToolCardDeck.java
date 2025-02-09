package it.polimi.ingsw.model.gameobjects;

import java.util.Random;

public class ToolCardDeck extends Deck<ToolCard> {

    /**
     * Constructor for ToolCardDeck.
     *
     * @param numOfPlayers is the number of players in the match.
     * @param difficulty is the difficulty of the match (if single player).
     */
    public ToolCardDeck(int numOfPlayers, int difficulty) {
        super();

        for (int i = 1; i < 13; i++) {
            this.deck.add("tool" + i);
        }

        Random randomGenerator;
        if (numOfPlayers != 1) {
            for (int j = 0; j < 3; j++) {
                randomGenerator = new Random();
                int toolIndex = randomGenerator.nextInt(deck.size());
                String toolName = deck.get(toolIndex);
                switchOnToolName(toolName);
            }
        } else {
            for (int j = 0; j < difficulty; j++) {
                randomGenerator = new Random();
                int toolIndex = randomGenerator.nextInt(deck.size());
                String toolName = deck.get(toolIndex);
                switchOnToolName(toolName);
            }
        }

    }

    private void switchOnToolName(String toolName) {
        switch (toolName) {

            case "tool1":
                toolName = "Pinza Sgrossatrice";
                ToolCard card1 = new ToolCard(toolName, "tool1");
                this.pickedCards.add(card1);
                this.deck.remove("tool1");
                break;
            case "tool2":
                toolName = "Pennello per Eglomise";
                ToolCard card2 = new ToolCard(toolName, "tool2");
                this.pickedCards.add(card2);
                this.deck.remove("tool2");
                break;
            case "tool3":
                toolName = "Alesatore per Lamina di Rame";
                ToolCard card3 = new ToolCard(toolName, "tool3");
                this.pickedCards.add(card3);
                this.deck.remove("tool3");
                break;
            case "tool4":
                toolName = "Lathekin";
                ToolCard card4 = new ToolCard(toolName, "tool4");
                this.pickedCards.add(card4);
                this.deck.remove("tool4");
                break;
            case "tool5":
                toolName = "Taglierina Circolare";
                ToolCard card5 = new ToolCard(toolName, "tool5");
                this.pickedCards.add(card5);
                this.deck.remove("tool5");
                break;
            case "tool6":
                toolName = "Pennello per Pasta Salda";
                ToolCard card6 = new ToolCard(toolName, "tool6");
                this.pickedCards.add(card6);
                this.deck.remove("tool6");
                break;
            case "tool7":
                toolName = "Martelletto";
                ToolCard card7 = new ToolCard(toolName, "tool7");
                this.pickedCards.add(card7);
                this.deck.remove("tool7");
                break;
            case "tool8":
                toolName = "Tenaglia a Rotelle";
                ToolCard card8 = new ToolCard(toolName, "tool8");
                this.pickedCards.add(card8);
                this.deck.remove("tool8");
                break;
            case "tool9":
                toolName = "Riga in Sughero";
                ToolCard card9 = new ToolCard(toolName, "tool9");
                this.pickedCards.add(card9);
                this.deck.remove("tool9");
                break;
            case "tool10":
                toolName = "Tampone Diamantato";
                ToolCard card10 = new ToolCard(toolName, "tool10");
                this.pickedCards.add(card10);
                this.deck.remove("tool10");
                break;
            case "tool11":
                toolName = "Diluente per Pasta Salda";
                ToolCard card11 = new ToolCard(toolName, "tool11");
                this.pickedCards.add(card11);
                this.deck.remove("tool11");
                break;
            case "tool12":
                toolName = "Taglierina Manuale";
                ToolCard card12 = new ToolCard(toolName, "tool12");
                this.pickedCards.add(card12);
                this.deck.remove("tool12");
                break;
            default:
                break;
        }

    }
}
