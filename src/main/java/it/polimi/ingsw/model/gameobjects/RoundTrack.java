package it.polimi.ingsw.model.gameobjects;

import it.polimi.ingsw.model.gamelogic.Match;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundTrack {
    //dicesLeft is a list of (list of dices) since you may have more than one dice in the same position
    private List<List<Dice>> dicesLeft;

    public RoundTrack() {
        // It makes an ArrayList of 10 (constant declared in Match) Lists of Dices
        dicesLeft = new LinkedList<>();
        for (int i = 0; i < Match.getNumberOfRounds(); i++) {
            dicesLeft.add(new ArrayList<>());
        }
    }

    public void putDices(List<Dice> dicesToPut, int position) {
        dicesLeft.set(position, dicesToPut);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        for (int i = 0; i < dicesLeft.size(); i++) {
            if(dicesLeft.get(i).size() != 0) {
                AtomicInteger j = new AtomicInteger();
                string.append("Round ");
                string.append(i + 1);
                string.append("\n");
                dicesLeft.get(i).forEach(e -> string.append(j.getAndIncrement() + ") " + e.toString() + "\t"));
                string.append("\n");
            }
        }
        return string.toString();
    }

    // todo: eliminare, anche gli utilizzi in testing
    public void showRoundTrack() {
        int j = 0;
        for (List<Dice> list : dicesLeft) {
            System.out.println("List number: " + j);
            int i = 0;
            for (Dice d : list) {
                System.out.println(d.toString() + "id=" + i);
                i++;
            }
            j++;
        }
    }

    public Dice getDice() {
        showRoundTrack();
        System.out.println("Choose the number of the list from which you want to get a dice");
        Scanner scan = new Scanner(System.in);
        int listNumber = scan.nextInt();
        System.out.println(("Choose the dice id"));
        int diceId = scan.nextInt();
        int j = 0;
        for (List<Dice> list : dicesLeft) {
            if (j == listNumber) {
                int i = 0;
                for (Dice d : list) {
                    if (i == diceId) {
                        list.remove(diceId);
                        return d;
                    }
                    i++;
                }
            }
            j++;
        }
        System.out.println("Error: wrong parameters.");
        return null;
    }


    public Colors getColorOfAChosenDice(int chosenRound, int chosenDiceFromRound) {
        Dice dice = dicesLeft.get(chosenRound - 1).get(chosenDiceFromRound);
        return dice.getColor();
    }

    public Dice switchDice(Dice diceToSwitch, int chosenRound, int chosenDiceFromRound) {
        if (chosenRound>0 && chosenRound<=dicesLeft.size()&&chosenDiceFromRound>=0 && chosenDiceFromRound <dicesLeft.get(chosenRound-1).size()) {
            Dice returnValue = dicesLeft.get(chosenRound - 1).remove(chosenDiceFromRound);
            dicesLeft.get(chosenRound - 1).add(chosenDiceFromRound, diceToSwitch);
            return returnValue;
        }
        else return null;
    }
}
