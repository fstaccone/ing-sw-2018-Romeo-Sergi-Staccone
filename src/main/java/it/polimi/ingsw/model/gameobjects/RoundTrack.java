package it.polimi.ingsw.model.gameobjects;

import it.polimi.ingsw.model.gamelogic.Match;

import java.util.*;

public class RoundTrack {
    //dicesLeft is a list of (list of dices) since you may have more than one dice in the same position
    private List<List<Dice>> dicesLeft;

    public RoundTrack(){
        // It makes an ArrayList of 10 (constant declared in Match) Lists of Dices
        dicesLeft = new LinkedList<>();
        for(int i = 0; i < Match.getNumberOfRounds(); i++){
            dicesLeft.add(new ArrayList<>());
        }
    }

    public void putDices(List<Dice> dicesToPut, int position){
        dicesLeft.set(position,dicesToPut);
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
                        System.out.println("You've chose the dice: "+d.toString()+" so the color of the dices you can move is: "+d.getColor().toString());
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

    //Questa copia di getcolorofachosendice serve solo per il test finchè non troviamo un modo migliore
    //Ha lo scanner come parametro
    public Colors getColorOfAChosenDice(Scanner scan){
        showRoundTrack();
        System.out.println("Choose the number of the list from which you want to get a dice color");
        int listNumber = scan.nextInt();
        System.out.println(("Choose the dice id"));
        int diceId = scan.nextInt();
        int j=0;
        for(List<Dice> list : dicesLeft){
            if(j==listNumber){
                int i=0;
                for(Dice d : list){
                    if(i==diceId){
                        System.out.println("You've chose the dice: "+d.toString()+" so the color of the dices you can move is: "+d.getColor().toString());
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
                        Dice result = list.get(i);
                        list.set(i, diceToSwitch);
                        return result;
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
