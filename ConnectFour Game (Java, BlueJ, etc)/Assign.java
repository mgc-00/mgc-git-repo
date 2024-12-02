
/**
 * Assign
 * This class handles the creation of all moves in the game
 * @author M Currie 
 * @version 1.3
 */
public class Assign {
    private int col, row;//The row and column being assigned
    private ConnectFour game;//The game 
    Slot[][] moves;//2D Array to store the game's moves

    /**
     * Constructor for Assign class.
     * This gets the total number of moves and calls methods to determine the row that will be filled, and to set the state of the slot being assigned.
     * @param game - the game
     * @param col - the column the user has selected
     * @param player - a Boolean value that determines whether it is a player/computer move
     */
    public Assign(ConnectFour game, int col, boolean player) {
        this.game = game; // Sets the game instance variable to game object
        moves = game.getMoves(); // Initializes the 2D array moves with current game's moves
        this.col = col; // Sets the 'col' variable to the 'col' value
        this.row = calculateRow(col); // Calculate 'row' value based on provided 'col' 
        assignMove(player); // Performs an action that is based on the player parameter
        addToMoveHistory(); // Updates the game move history
    }

    /**
     * addToMoveHistory
     * This method records the history of moves in the stack, made by the player and computer 
     */    
    private void addToMoveHistory() {
        // Access the game's move history stack and push the current column (move) onto it
        game.getMoveHistory().push(col);
    }

    /**
     * calculateRow
     * Finds the lowest empty slot of the selected column
     * @param col - the selected column
     * @return the row value
     */
    public int calculateRow(int col) {

        // Initialize a boolean flag to track if the column is available
        Boolean columnAvailable = false;

        // Initialize a count variable to start from the lowest row
        int count = 5;

        // Use a do-while loop to search for the lowest empty slot in the column
        do {
            // Check if the state of the slot at the current row in the column is empty
            if (moves[col][count].getState().equals(ConnectFour.EMPTYSLOT)) {
                // Set the columnAvailable flag to true if an empty slot is found
                columnAvailable = true;
            } else {
                // Decrement the count to move to the row above
                count--;
            }

        } while (columnAvailable == false);

        // Return the row value of the lowest empty slot in the selected column
        return count;
    }

    /**
     * assignMove
     * This method assigns the move to the game
     * @param player a Boolean value to determine whether it is a computer/player move
     */
    public void assignMove(boolean player) {
        if (player == true) // Check if it's a player's move (true means player's move)
            moves[col][row].setState(ConnectFour.PLAYERMOVE); // Set the slot state to PLAYERMOVE
        else
            moves[col][row].setState(ConnectFour.COMPUTERMOVE); // Set the slot state to COMPUTERMOVE
    }

    /**
     * getRow
     * This method returns the current row value for this move. 
     * It allows another element of the program to access this move's current row.
     * @return the row value
     */
    public int getRow() {
        return row; // Returns the value of the 'row' variable, indicating the current row for this move.
    }

}//End of class Assign