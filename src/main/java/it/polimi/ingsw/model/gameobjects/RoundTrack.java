package it.polimi.ingsw.model.gameobjects;

import java.util.*;

public class RoundTrack {
    //dicesLeft is a list of (list of dices) since you may have more than one dice in the same position
    private Map<Integer, List<Dice>> dicesLeft;

    public RoundTrack(){
        // It makes an ArrayList of 10 (constant declared in Match) Lists of Dices
        dicesLeft = new HashMap<>(Match.getNumberOfRounds());
        for(Integer i = 0; i < Match.getNumberOfRounds(); i++){
            dicesLeft.get(i) = new ArrayList<>();
        }
    }

    public void putDices(List<Dice> dicesToPut, int position){
        dicesLeft.get(position) = new ArrayList<>();
        dicesLeft[position].add(dicesToPut);
    }

    public void showRoundTrack(){
        int j=0;
        for(List<Dice> list : dicesLeft){
            System.out.println("List number: "+j);
            int i=0;
            for(Dice d : list){
                System.out.println(d.toString()+"id="+i);
                i++;
            }
            j++;
        }
    }

    public Dice getDice(){
        showRoundTrack();
        System.out.println("Choose the number of the list from which you want to get a dice");
        Scanner scan = new Scanner(System.in);
        int listNumber = scan.nextInt();
        System.out.println(("Choose the dice id"));
        int diceId = scan.nextInt();
        int j=0;
        for(List<Dice> list : dicesLeft){
            if(j==listNumber){
                int i=0;
                for(Dice d : list){
                    if(i==diceId){
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

    public Colors getColorOfAChosenDice(){
        showRoundTrack();
        System.out.println("Choose the number of the list from which you want to get a dice color");
        Scanner scan = new Scanner(System.in);
        int listNumber = scan.nextInt();
        System.out.println(("Choose the dice id"));
        int diceId = scan.nextInt();
        int j=0;
        for(List<Dice> list : dicesLeft){
            if(j==listNumber){
                int i=0;
                for(Dice d : list){
                    if(i==diceId){
                        return d.getColor();
                    }
                    i++;
                }
            }
            j++;
        }
        System.out.println("Error: wrong parameters.");
        return null;
    }

    //switchDice is similar to getDice but it switches the chosen dice with a new one
    public Dice switchDice(Dice diceToSwitch){
        showRoundTrack();
        System.out.println("Choose the number of the list from which you want to get a dice");
        Scanner scan = new Scanner(System.in);
        int listNumber = scan.nextInt();
        System.out.println(("Choose the dice id"));
        int diceId = scan.nextInt();
        int j=0;
        for(List<Dice> list : dicesLeft){
            if(j==listNumber){
                int i=0;
                for(Dice d : list){
                    if(i==diceId){
                        list.remove(diceId);
                        list.add(diceToSwitch);
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
}
