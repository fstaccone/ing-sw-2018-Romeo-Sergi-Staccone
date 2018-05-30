package it.polimi.ingsw.model.gameobjects;

import java.io.Serializable;
import java.util.Arrays;

public class WindowPatternCard implements Serializable{

    private String name;
    private int difficulty; //difficulty is a value between 3 and 6
    private Square[][] window;
    private int rows;
    private int columns;
    private boolean empty;
    //constructor gives window a name and creates a double array of squares without constraints (for now)
    //it should be modified to allow constraints in specific squares
    public WindowPatternCard(String name, int rows, int columns) {
        this.name = name;
        this.empty = true;
        this.rows = rows;
        this.columns = columns;
        window = new Square[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++)
                window[i][j] = new Square();
        }
    }

    public int getRows() {
        return rows;
    }
    public int getColumns() { return columns;}
    public int getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    private boolean isEmpty() { return empty; }
    public String getName() {
        return name;
    }
    public Square[][] getWindow() {
        return window;
    }

    private void setEmpty(boolean empty) { this.empty = empty; }

    //method created by copying part of checkPos but it's useful to have both
    public boolean existsAdjacentDice(int row, int column){
        Dice northern;
        Dice eastern;
        Dice southern;
        Dice western;
        Dice northEastern = null;
        Dice southEastern = null;
        Dice southWestern = null;
        Dice northWestern = null;
        //If we are putting a dice in the upper border we don't have a northern square to check
        if (row == 0) {
            northern = null;
        } else {
            northern = window[row - 1][column].getDice();
            if (column < window[row].length - 1)
                northEastern = window[row - 1][column + 1].getDice();
            if (column > 0)
                northWestern = window[row - 1][column - 1].getDice();
        }
        //If we are putting a dice in the right border we don't have an eastern square to check
        if (column == window[row].length - 1) {
            eastern = null;
            northEastern = null;
        } else {
            eastern = window[row][column + 1].getDice();
            if(row<window.length-1)
                southEastern = window[row+1][column+1].getDice();
        }
        //If we are putting a dice in the bottom border we don't have an southern square to check
        if (row == window.length - 1) {
            southern = null;
            southEastern = null;
        } else {
            southern = window[row + 1][column].getDice();
            if(column > 0)
                southWestern = window[row+1][column-1].getDice();
        }
        //If we are putting a dice in the left border we don't have an western square to check
        if (column == 0) {
            western = null;
        } else {
            western = window[row][column - 1].getDice();
        }
        return (northern!=null || eastern!=null || southern!=null || western!=null || northEastern!=null || southEastern!=null || southWestern!=null || northWestern!=null);
    }
    // method used by checkPos
    public boolean existsAdjacentDice(Dice[] adjacentDices){
        for(Dice dice : adjacentDices){
            if(dice!=null) return true;
        }
        return false;
    }

    public boolean checkPos(Dice d, int row, int column) {
        //Declaration of the four adjacent dices to check
        //We also have to check that we have at least one adjacent dice
        Dice northern;
        Dice eastern;
        Dice southern;
        Dice western;
        Dice northEastern = null;
        Dice southEastern = null;
        Dice southWestern = null;
        Dice northWestern = null;
        //If we are putting a dice in the upper border we don't have a northern square to check
        if (row == 0) {
            northern = null;
            northEastern = null;
            northWestern = null;
        } else {
            northern = window[row - 1][column].getDice();
            if (column < window[row].length - 1)
                northEastern = window[row - 1][column + 1].getDice();
            if (column > 0)
                northWestern = window[row - 1][column - 1].getDice();
        }
        //If we are putting a dice in the right border we don't have an eastern square to check
        if (column == window[row].length - 1) {
            eastern = null;
            northEastern = null;
            southEastern = null;
        } else {
            eastern = window[row][column + 1].getDice();
            if(row<window.length-1)
                southEastern = window[row+1][column+1].getDice();
        }
        //If we are putting a dice in the bottom border we don't have an southern square to check
        if (row == window.length - 1) {
            southern = null;
            southEastern = null;
            southWestern = null;
        } else {
            southern = window[row + 1][column].getDice();
            if(column > 0)
                southWestern = window[row+1][column-1].getDice();
        }
        //If we are putting a dice in the left border we don't have an western square to check
        if (column == 0) {
            western = null;
            northWestern = null;
            southWestern = null;
        } else {
            western = window[row][column - 1].getDice();
        }
        Dice[] adjacentDices = {northern, eastern, southern, western, northEastern, southEastern, southWestern, northWestern};
        //check if we are putting the dice next to at least another one
        if (existsAdjacentDice(adjacentDices)) {
            //check on eventual dice in northern square with same color or value
            if (northern != null && (northern.getColor() == d.getColor() || northern.getValue() == d.getValue()))
                return false;
            //check on eventual dice in eastern square with same color or value
            if (eastern != null && (eastern.getColor() == d.getColor() || eastern.getValue() == d.getValue()))
                return false;
            //check on eventual dice in southern square with same color or value
            if (southern != null && (southern.getColor() == d.getColor() || southern.getValue() == d.getValue()))
                return false;
            //check on eventual dice in western square with same color or value
            if (western != null && (western.getColor() == d.getColor() || western.getValue() == d.getValue()))
                return false;
            return true;
        }
        return false;
    }

    //putDice calls checkPos, then putDice calls Square methods to check empty place and satisfied constrictions
    public boolean putDice(Dice d, int row, int column) {
        if(isEmpty()){
            boolean result = putFirstDice(d, row, column);
            setEmpty(!result);
            return result;
        }else{
            if (checkPos(d, row, column)) {
                return window[row][column].putDice(d);
            }
        }
        return false;
    }

    public void putDiceIgnoringValueConstraint(Dice d, int row, int column){
        if(checkPos(d, row, column))
            window[row][column].putDiceIgnoringValueConstraint(d);
    }

    public void putDiceIgnoringColorConstraint(Dice d, int row, int column){
        if(checkPos(d, row, column))
            window[row][column].putDiceIgnoringColorConstraint(d);
    }

    public void putDiceIgnoringAllConstraints(Dice d, int row, int column){
        if(checkPos(d, row, column))
            window[row][column]. putDiceIgnoringAllConstraints(d);
    }
    public boolean fullColumn(int z){
            boolean res = false;
            boolean ris = true;
            for(int i=0;i<4;i++){
                if (this.getWindow()[i][z].getDice()==null){
                        res = false;
                        ris = false;
                    }
                    else
                        res = true;
                }
            return ris;
        }

    public boolean putFirstDice(Dice d, int row, int column){
        if(row==0 || column==0 || column == window[row].length - 1 || row == window.length - 1) {
            return window[row][column].putDice(d);

        }
        return false;
    }

    public void putDiceWithoutCheckPos(Dice d, int row, int column){
        window[row][column].putDice(d);
    }

    public Dice removeDice(int row, int column) {
        return window[row][column].removeDice();
    }
    public Dice getDice(int row, int column) {
        return window[row][column].getDice();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newline = "\n";

        result.append(newline);
        result.append("Name: ");
        result.append(this.name);
        result.append(newline);
        result.append("Difficulty: ");
        result.append(this.difficulty);
        result.append(newline);
        result.append("--------------------------");
        result.append(newline);

        for (int i = 0; i < window.length; i++) {
            result.append(Arrays.toString(window[i]));
            result.append(newline);
        }
        result.append("--------------------------");

        return result.toString();
    }


    // it returns the number of free cells (to be used for points' calculation)
    public int countFreeSquares(){
       return (int) Arrays.stream(window)
                .flatMap(Arrays::stream)
                .filter(s -> s.getDice() == null)
                .count();
    }
}

