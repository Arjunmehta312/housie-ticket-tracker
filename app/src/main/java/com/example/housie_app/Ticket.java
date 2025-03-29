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
    
    // Basic winning patterns
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
    
    // Corner patterns
    public boolean isFourCorners() {
        boolean topLeft = (numbers[0][0] == null) || marked[0][0];
        boolean topRight = (numbers[0][COLS-1] == null) || marked[0][COLS-1];
        boolean bottomLeft = (numbers[ROWS-1][0] == null) || marked[ROWS-1][0];
        boolean bottomRight = (numbers[ROWS-1][COLS-1] == null) || marked[ROWS-1][COLS-1];
        
        return topLeft && topRight && bottomLeft && bottomRight;
    }
    
    public boolean isSixCorners() {
        // First and last number of each row
        for (int i = 0; i < ROWS; i++) {
            // Check first number in row
            if (hasUnmarkedNumberInPosition(i, 0)) {
                return false;
            }
            
            // Check last number in row
            if (hasUnmarkedNumberInPosition(i, COLS-1)) {
                return false;
            }
        }
        return true;
    }
    
    // Number range patterns
    public boolean isBreakfast() {
        // All numbers between 1 and 30 must be marked
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (numbers[i][j] != null && numbers[i][j] >= 1 && numbers[i][j] <= 30 && !marked[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isLunch() {
        // All numbers between 31 and 60 must be marked
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (numbers[i][j] != null && numbers[i][j] >= 31 && numbers[i][j] <= 60 && !marked[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isDinner() {
        // All numbers between 61 and 90 must be marked
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (numbers[i][j] != null && numbers[i][j] >= 61 && numbers[i][j] <= 90 && !marked[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isStraightBrunch() {
        // All numbers between 1 and 60 must be marked
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (numbers[i][j] != null && numbers[i][j] >= 1 && numbers[i][j] <= 60 && !marked[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isReverseBrunch() {
        // All numbers between 31 and 90 must be marked
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (numbers[i][j] != null && numbers[i][j] >= 31 && numbers[i][j] <= 90 && !marked[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // Position-based patterns
    public boolean isTwins() {
        // First two numbers in all three rows must be marked
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < 2; j++) {
                if (hasUnmarkedNumberInPosition(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isReverseTwins() {
        // Last two numbers in each row
        for (int i = 0; i < ROWS; i++) {
            int count = 0;
            for (int j = COLS - 1; j >= 0; j--) {
                if (numbers[i][j] != null) {
                    count++;
                    if (count <= 2 && !marked[i][j]) {
                        return false;
                    }
                    if (count > 2) break;
                }
            }
        }
        return true;
    }
    
    public boolean isTriplets() {
        // First three numbers in each row
        for (int i = 0; i < ROWS; i++) {
            int count = 0;
            for (int j = 0; j < COLS; j++) {
                if (numbers[i][j] != null) {
                    count++;
                    if (count <= 3 && !marked[i][j]) {
                        return false;
                    }
                    if (count > 3) break;
                }
            }
        }
        return true;
    }
    
    public boolean isReverseTriplets() {
        // Last three numbers in all three rows
        for (int i = 0; i < ROWS; i++) {
            int count = 0;
            for (int j = COLS - 1; j >= 0; j--) {
                if (numbers[i][j] != null) {
                    count++;
                    if (count <= 3 && !marked[i][j]) {
                        return false;
                    }
                    if (count > 3) break;
                }
            }
        }
        return true;
    }
    
    public boolean isLadder() {
        // First number in the top row, first two numbers in the middle row, and first three numbers in the bottom row
        int[] requiredCounts = {1, 2, 3}; // Numbers required in each row
        
        for (int i = 0; i < ROWS; i++) {
            int count = 0;
            for (int j = 0; j < COLS; j++) {
                if (numbers[i][j] != null) {
                    count++;
                    if (count <= requiredCounts[i] && !marked[i][j]) {
                        return false;
                    }
                    if (count > requiredCounts[i]) break;
                }
            }
            if (count < requiredCounts[i]) return false; // Not enough numbers in row
        }
        return true;
    }
    
    public boolean isReverseLadder() {
        // Last three numbers in the top row, last two numbers in the middle row, and last number in the bottom row
        int[] requiredCounts = {3, 2, 1}; // Numbers required in each row
        
        for (int i = 0; i < ROWS; i++) {
            int count = 0;
            for (int j = COLS - 1; j >= 0; j--) {
                if (numbers[i][j] != null) {
                    count++;
                    if (count <= requiredCounts[i] && !marked[i][j]) {
                        return false;
                    }
                    if (count > requiredCounts[i]) break;
                }
            }
            if (count < requiredCounts[i]) return false; // Not enough numbers in row
        }
        return true;
    }
    
    public boolean isLoversLane() {
        // First number in the top row, first number in the middle row, and entire bottom row
        // Top row - first number
        if (hasUnmarkedFirstNumberInRow(0)) return false;
        
        // Middle row - first number
        if (hasUnmarkedFirstNumberInRow(1)) return false;
        
        // Bottom row - all numbers
        return isRowComplete(2);
    }
    
    public boolean isReverseLoversLane() {
        // Entire top row, last number in the middle row, last number in the bottom row
        // Top row - all numbers
        if (!isRowComplete(0)) return false;
        
        // Middle row - last number
        if (hasUnmarkedLastNumberInRow(1)) return false;
        
        // Bottom row - last number
        return !hasUnmarkedLastNumberInRow(2);
    }
    
    public boolean isTennis() {
        // Middle number in the top and bottom rows, entire centre row
        // Top row - middle number
        if (hasUnmarkedMiddleNumberInRow(0)) return false;
        
        // Middle row - all numbers
        if (!isRowComplete(1)) return false;
        
        // Bottom row - middle number
        return !hasUnmarkedMiddleNumberInRow(2);
    }
    
    public boolean isStumps() {
        // Middle three numbers of each row
        for (int i = 0; i < ROWS; i++) {
            if (hasUnmarkedMiddleThreeNumbersInRow(i)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isRoundTheWorld() {
        // Entire top and bottom row plus first and last number in the middle row
        if (!isRowComplete(0)) return false;
        if (!isRowComplete(2)) return false;
        
        if (hasUnmarkedFirstNumberInRow(1)) return false;
        return !hasUnmarkedLastNumberInRow(1);
    }
    
    public boolean isLetterH() {
        // First and last numbers in top and bottom row, entire middle row
        // Top row - first and last number
        if (hasUnmarkedFirstNumberInRow(0) || hasUnmarkedLastNumberInRow(0)) return false;
        
        // Middle row - all numbers
        if (!isRowComplete(1)) return false;
        
        // Bottom row - first and last number
        return !(hasUnmarkedFirstNumberInRow(2) || hasUnmarkedLastNumberInRow(2));
    }
    
    // Quick patterns
    public boolean isQuick12() {
        // Any twelve numbers across the ticket must be marked
        return getMarkedNumbersCount() >= 12;
    }
    
    public boolean isQuick13() {
        // Any thirteen numbers across the ticket must be marked
        return getMarkedNumbersCount() >= 13;
    }
    
    public boolean isQuick14() {
        // Any fourteen numbers across the ticket must be marked
        return getMarkedNumbersCount() >= 14;
    }
    
    // Line combinations
    public boolean isTopAndCentreLine() {
        return isTopRowComplete() && isMiddleRowComplete();
    }
    
    public boolean isCentreAndBottomLine() {
        return isMiddleRowComplete() && isBottomRowComplete();
    }
    
    public boolean isTopAndBottomLine() {
        return isTopRowComplete() && isBottomRowComplete();
    }
    
    // Helper methods
    private boolean hasUnmarkedNumberInPosition(int row, int col) {
        return numbers[row][col] != null && !marked[row][col];
    }
    
    private boolean hasUnmarkedFirstNumberInRow(int row) {
        for (int j = 0; j < COLS; j++) {
            if (numbers[row][j] != null) {
                return !marked[row][j];
            }
        }
        return false; // No number found
    }
    
    private boolean hasUnmarkedLastNumberInRow(int row) {
        for (int j = COLS - 1; j >= 0; j--) {
            if (numbers[row][j] != null) {
                return !marked[row][j];
            }
        }
        return false; // No number found
    }
    
    private boolean hasUnmarkedMiddleNumberInRow(int row) {
        int count = 0;
        int totalNumbers = 0;
        
        // Count total numbers in the row
        for (int j = 0; j < COLS; j++) {
            if (numbers[row][j] != null) {
                totalNumbers++;
            }
        }
        
        // Find middle number
        int middle = (totalNumbers / 2) + 1;
        
        for (int j = 0; j < COLS; j++) {
            if (numbers[row][j] != null) {
                count++;
                if (count == middle) {
                    return !marked[row][j];
                }
            }
        }
        
        return false; // No middle number found
    }
    
    private boolean hasUnmarkedMiddleThreeNumbersInRow(int row) {
        int count = 0;
        int totalNumbers = 0;
        int[] middleIndices = new int[3];
        
        // Count total numbers in the row
        for (int j = 0; j < COLS; j++) {
            if (numbers[row][j] != null) {
                totalNumbers++;
            }
        }
        
        if (totalNumbers < 3) return false; // Not enough numbers
        
        // Find middle three indices
        int middle = totalNumbers / 2;
        middleIndices[0] = middle;
        middleIndices[1] = middle - 1;
        middleIndices[2] = middle + 1;
        
        // Check if the middle three are marked
        count = 0;
        for (int j = 0; j < COLS; j++) {
            if (numbers[row][j] != null) {
                if (count == middleIndices[0] || count == middleIndices[1] || count == middleIndices[2]) {
                    if (!marked[row][j]) {
                        return true; // Found unmarked middle number
                    }
                }
                count++;
            }
        }
        
        return false;
    }
    
    private int getMarkedNumbersCount() {
        int count = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (numbers[i][j] != null && marked[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }
    
    // Check if this ticket has won any prize based on the marked numbers
    public List<String> checkWinningPatterns() {
        List<String> winningPatterns = new ArrayList<>();
        
        // Basic patterns
        if (isTopRowComplete()) {
            winningPatterns.add("Top Line");
        }
        
        if (isMiddleRowComplete()) {
            winningPatterns.add("Centre Line");
        }
        
        if (isBottomRowComplete()) {
            winningPatterns.add("Bottom Line");
        }
        
        if (isFourCorners()) {
            winningPatterns.add("Four Corners");
        }
        
        if (isFullHouse()) {
            winningPatterns.add("Full House");
        }
        
        // Number range patterns
        if (isBreakfast()) {
            winningPatterns.add("Breakfast");
        }
        
        if (isLunch()) {
            winningPatterns.add("Lunch");
        }
        
        if (isDinner()) {
            winningPatterns.add("Dinner");
        }
        
        if (isStraightBrunch()) {
            winningPatterns.add("Straight Brunch");
        }
        
        if (isReverseBrunch()) {
            winningPatterns.add("Reverse Brunch");
        }
        
        // Position-based patterns
        if (isTwins()) {
            winningPatterns.add("Twins");
        }
        
        if (isReverseTwins()) {
            winningPatterns.add("Reverse Twins");
        }
        
        if (isTriplets()) {
            winningPatterns.add("Triplets");
        }
        
        if (isReverseTriplets()) {
            winningPatterns.add("Reverse Triplets");
        }
        
        if (isLadder()) {
            winningPatterns.add("Ladder");
        }
        
        if (isReverseLadder()) {
            winningPatterns.add("Reverse Ladder");
        }
        
        if (isLoversLane()) {
            winningPatterns.add("Lover's Lane");
        }
        
        if (isReverseLoversLane()) {
            winningPatterns.add("Reverse Lover's Lane");
        }
        
        if (isTennis()) {
            winningPatterns.add("Tennis");
        }
        
        if (isSixCorners()) {
            winningPatterns.add("Six Corners");
        }
        
        if (isStumps()) {
            winningPatterns.add("Stumps");
        }
        
        if (isRoundTheWorld()) {
            winningPatterns.add("Round the World");
        }
        
        if (isLetterH()) {
            winningPatterns.add("Letter H");
        }
        
        // Quick patterns
        if (isQuick12()) {
            winningPatterns.add("Quick-12");
        }
        
        if (isQuick13()) {
            winningPatterns.add("Quick-13");
        }
        
        if (isQuick14()) {
            winningPatterns.add("Quick-14");
        }
        
        // Line combinations
        if (isTopAndCentreLine()) {
            winningPatterns.add("Top & Centre Line");
        }
        
        if (isCentreAndBottomLine()) {
            winningPatterns.add("Centre & Bottom Line");
        }
        
        if (isTopAndBottomLine()) {
            winningPatterns.add("Top & Bottom Line");
        }
        
        return winningPatterns;
    }
} 