package com.example.housie_app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Housie ticket with a grid of numbers
 * Standard housie ticket has 3 rows and 9 columns
 * Each row has 5 numbers and 4 blank spaces
 */
public class Ticket implements Serializable {
    public static final int ROWS = 3;
    public static final int COLS = 9;
    
    private String ticketId;
    private Integer[][] numbers; // null represents empty cell
    private boolean[][] marked;  // tracks which numbers have been marked
    
    public Ticket(String ticketId) {
        this.ticketId = ticketId;
        this.numbers = new Integer[ROWS][COLS];
        this.marked = new boolean[ROWS][COLS];
        
        // Initialize marked array to false
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                marked[i][j] = false;
            }
        }
    }
    
    public String getTicketId() {
        return ticketId;
    }
    
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
    
    public Integer getNumber(int row, int col) {
        return numbers[row][col];
    }
    
    public void setNumber(int row, int col, Integer number) {
        numbers[row][col] = number;
    }
    
    public boolean isMarked(int row, int col) {
        return marked[row][col];
    }
    
    public void setMarked(int row, int col, boolean value) {
        marked[row][col] = value;
    }
    
    public void markNumber(int number) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (numbers[i][j] != null && numbers[i][j] == number) {
                    marked[i][j] = true;
                }
            }
        }
    }
    
    // Check winning patterns
    public boolean isTopRowComplete() {
        return isRowComplete(0);
    }
    
    public boolean isMiddleRowComplete() {
        return isRowComplete(1);
    }
    
    public boolean isBottomRowComplete() {
        return isRowComplete(2);
    }
    
    private boolean isRowComplete(int row) {
        for (int j = 0; j < COLS; j++) {
            if (numbers[row][j] != null && !marked[row][j]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isFullHouse() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (numbers[i][j] != null && !marked[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isFourCorners() {
        boolean topLeft = (numbers[0][0] == null) || marked[0][0];
        boolean topRight = (numbers[0][COLS-1] == null) || marked[0][COLS-1];
        boolean bottomLeft = (numbers[ROWS-1][0] == null) || marked[ROWS-1][0];
        boolean bottomRight = (numbers[ROWS-1][COLS-1] == null) || marked[ROWS-1][COLS-1];
        
        return topLeft && topRight && bottomLeft && bottomRight;
    }
    
    // Check if this ticket has won any prize based on the marked numbers
    public List<String> checkWinningPatterns() {
        List<String> winningPatterns = new ArrayList<>();
        
        if (isTopRowComplete()) {
            winningPatterns.add("Top Row");
        }
        
        if (isMiddleRowComplete()) {
            winningPatterns.add("Middle Row");
        }
        
        if (isBottomRowComplete()) {
            winningPatterns.add("Bottom Row");
        }
        
        if (isFourCorners()) {
            winningPatterns.add("Four Corners");
        }
        
        if (isFullHouse()) {
            winningPatterns.add("Full House");
        }
        
        return winningPatterns;
    }
} 