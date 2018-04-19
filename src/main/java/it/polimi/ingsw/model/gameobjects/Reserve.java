package it.polimi.ingsw.model.gameobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Reserve {

    private ArrayList<Dice> dices;

    public Reserve(ArrayList<Dice> init){
        dices = new ArrayList<>();
        dices.addAll(init);
    }

    public void showReserve(){
        if(!(dices.isEmpty())){
            System.out.println("Dices in reserve:");
            int i=0;
            for(Dice nav : dices){
                System.out.println(nav.toString()+" id="+i);
                i++;
            }
        }
        else System.out.println("The reserve is empty.");
    }

    public Dice chooseDice() {
        if(!(dices.isEmpty())){
            System.out.println("Please choose a dice from the reserve by writing the corresponding id: ");
            showReserve();
            Scanner scan = new Scanner(System.in);
            int i = scan.nextInt();
            if(i<0 || i>dices.size()-1){
                while(i<0 || i>dices.size()-1) {
                    System.out.println("Please insert a correct value.");
                    i = scan.nextInt();
                }
            }
            Dice choice = dices.get(i);
            dices.remove(i);
            System.out.println("Chosen dice: "+choice.toString());
            return choice;
        }
        else {
            System.out.println("The reserve is empty."); //andrebbe fatto lanciando ExceptionEmptyReserve
            return null;
        }
    }
    public void endRound(){
        System.out.println("End of the round.");
        while(dices.size()>0){
            System.out.println("Dice removed from reserve: "+dices.get(0));
            dices.remove(0);
            //i dadi restanti nella riserva vanno rimossi e posti nella corrispondente casella del roundtrack
            this.showReserve();
        }
    }

    /*public static void main(String args[]){
        Bag bag = new Bag(2);
        ArrayList<Dice> pescata = bag.pesca(3);
        Reserve riserva = new Reserve(pescata);
        riserva.showReserve();
        Dice dado = riserva.chooseDice();
        riserva.endRound();
    }*/
}