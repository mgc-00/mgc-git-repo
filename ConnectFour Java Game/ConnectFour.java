import java.util.Stack;
import java.util.Random;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * ConnectFour 
 * This is the main controller class for the game of ConnectFour
 * @author M Currie 
 * @version 1.3
 */
public class ConnectFour implements Serializable { // Serializable implemented to enable the user to save/load game states
    private Slot[][] moves;// 2D array of the moves of the game
    public static final String EMPTYSLOT = "-"; //Blank slot game state
    public static final String PLAYERMOVE = "x";//Player slot game state
    public static final String COMPUTERMOVE = "o";// computer slot game state
    private Stack<Integer> moveHistory; // Stack has been implemented to keep history of player and computer moves

    /**
     * Constructor of the class ConnectFour
     * Initialises the slots of the game ready to play
     */
    public ConnectFour() {
        createGame();           // Calls the createGame() method to initialize the game board.
        moveHistory = new Stack<>();  // Creates a new stack to track move history.
    }

    /**
     * createGame
     * This method creates a new set of blank moves in preparation to play the game.
     */
    public void createGame() {
        // Create a 2D array of Slot objects with dimensions 7x6 to represent the game board.
        moves = new Slot[7][6];

        // Nested loops to initialize each Slot object in the array with their respective row and column indices.
        for (int c = 0; c < 6; c++) { // Loop through columns (0 to 5).
            for (int r = 0; r < 7; r++) { // Loop through rows (0 to 6).
                // Create a new Slot object and assign it to the corresponding position in the array.
                moves[r][c] = new Slot(r, c);
            }
        }
    }

    /**
     * getMoves
     * This method returns a 2D array with all of the current moves in the game
     * @return the moves in the game currently
     */
    public Slot[][] getMoves() {
        return moves; // Returns the 2D array representing the current state of moves in the game
    }

    /**
     * getMoveHistory
     * This method returns the stack which stores the move history in the game
     */
    public Stack<Integer> getMoveHistory() {
        return moveHistory; // Returns the stack containing the move history
    }

    /**
     * addMove
     * This method adds a move to the game
     * @param col - The column that the player/computer has selected
     * @param player - A Boolean value denoting whether the move is made by the player or the computer
     */
    public void addMove(int col, boolean player) {
        // Create an instance of the Assign class, which handles move assignment
        Assign move = new Assign(this, col, player);
    }

    /**
     * undoMove 
     * This method undoes the last moves made by the player and computer
     * Junctioned to the ConnectFourUI class and the ConnectFourGUI class****** //change later
     */
    public void undoMove() {
        if (!moveHistory.isEmpty()) { // Check if there are moves in the move history stack
            int col = moveHistory.pop(); // Retrieve the last column from the move history
            int row = findTopOccupiedRow(col); // Find the top occupied row in the selected column
            if (row >= 0) { // Check if the row is valid (not empty)
                moves[col][row].setState(EMPTYSLOT); // Reset the state of the slot to empty
                if (!moveHistory.isEmpty()) { // Check if there are more moves in the history
                    col = moveHistory.pop(); // Retrieve the column of the previous move
                    row = findTopOccupiedRow(col); // Find the top occupied row in the selected column
                    if (row >= 0) { // Check if the row is valid (not empty)
                        moves[col][row].setState(EMPTYSLOT); // Reset the state of the slot to empty
                    }
                }
            }
        }
    }

    /**
     * saveGame
     * Saves game to a file
     * Junctioned to the ConnectFourUI class and the ConnectFourGUI class
     */
    public void saveGame(String fileName) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

            // Serialize and save the game object
            objectOutputStream.writeObject(this); // Serialize the current instance of the game

            System.out.println("Game saved successfully to " + fileName); // Display success message
        } catch (IOException e) {
            System.err.println("Error while saving the game: " + e.getMessage()); // Handle and display save error
        }
    }

    /**
     * loadGame
     * This method loads a previous saved game of the player
     * Junctioned to the ConnectFourUI class and the ConnectFourGUI class
     */
    public boolean loadGame(String fileName) {
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            // Deserialize and load game
            ConnectFour loadedGame = (ConnectFour) objectInputStream.readObject();

            // Update current game using saved game file
            this.moves = loadedGame.moves;
            this.moveHistory = loadedGame.moveHistory;

            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while loading the game: " + e.getMessage());
            return false;
        }
    }



    /**
     * generateComputerMove
     * This method generates the computer's move using a random number generator
     * // Computer difficulty level has been raised to improve play experience //
     */
    public void generateComputerMove() {
        Random randomGen = new Random(); // Initialize a random number generator

        int col;

        // Firstly - computer checks for a good move in the game
        for (col = 0; col < 7; col++) {
            int row = findRow(col); // Find the lowest available row in the column
            if (row >= 0) {
                moves[col][row].setState(COMPUTERMOVE); // Set the slot as a potential computer move
                if (checkWin() != null) {
                    moves[col][row].setState(EMPTYSLOT); // Reset the slot for the next move
                    addMove(col, false); // Make the winning move
                    return;
                }
                moves[col][row].setState(EMPTYSLOT); // Reset the slot for the next move
            }
        }

        // Next - computer will look for the best move to make
        for (col = 0; col < 7; col++) { //FLAG
            int row = findRow(col); // Find the lowest available row in the column
            if (row >= 0) {
                moves[col][row].setState(PLAYERMOVE); // Set the slot as a potential player move
                if (checkWin() != null) {
                    moves[col][row].setState(EMPTYSLOT); // Reset the slot for the next move
                    addMove(col, false); // Blocks the player
                    return;
                }
                moves[col][row].setState(EMPTYSLOT); // Reset the slot for the next move
            }
        }

        // If computer cannot make a winning move, choose the next available option randomly
        do {
            col = randomGen.nextInt(7); // Generate a random column
        } while (findRow(col) == -1); // Repeat until a valid column is found

        addMove(col, false); // Make the selected move
    }

    /**
     * checkWin
     * This method checks whether there is a vertical, horizontal, or diagonal match of four slots, to determine who has won the game
     * @return This value is the winner of the game or null if no one has won yet
     */
    public String checkWin() {
        try {
            for (int c = 0; c < 6; c++) { // Loop through columns
                for (int r = 0; r < 6; r++) { // Loop through rows
                    if (moves[r][c].getState().equals(PLAYERMOVE) || moves[r][c].getState().equals(COMPUTERMOVE)) {
                        // Check for a horizontal win
                        if (c + 3 < 6) {
                            if (moves[r][c].getState().equals(moves[r][c + 1].getState()) &&
                            moves[r][c].getState().equals(moves[r][c + 2].getState()) &&
                            moves[r][c].getState().equals(moves[r][c + 3].getState())) {
                                if (moves[r][c].getState().equals(PLAYERMOVE)) {
                                    return "YOU have won the game with 4 in a row, congratulations!!";
                                } else {
                                    return "COMPUTER has won the game with 4 in a row, tough luck!!";
                                }
                            }
                        }
                        // Check for a vertical win
                        if (r + 3 < 7) {
                            if (moves[r][c].getState().equals(moves[r + 1][c].getState()) &&
                            moves[r][c].getState().equals(moves[r + 2][c].getState()) &&
                            moves[r][c].getState().equals(moves[r + 3][c].getState())) {
                                if (moves[r][c].getState().equals(PLAYERMOVE)) {
                                    return "YOU have won the game with 4 in a row, congratulations!!";
                                } else {
                                    return "COMPUTER has won the game with 4 in a row, tough luck!!";
                                }
                            }
                        }
                        // Check for a diagonal win, from top-left to bottom-right //FLAG
                        if (c + 3 < 6 && r + 3 < 7) {
                            if (moves[r][c].getState().equals(moves[r + 1][c + 1].getState()) &&
                            moves[r][c].getState().equals(moves[r + 2][c + 2].getState()) &&
                            moves[r][c].getState().equals(moves[r + 3][c + 3].getState())) {
                                if (moves[r][c].getState().equals(PLAYERMOVE)) {
                                    return "YOU have won the game with 4 in a row!!";
                                } else {
                                    return "COMPUTER has won the game with 4 in a row, tough luck!!";
                                }
                            }
                        }
                        // Check for a diagonal win, from top-right to bottom-left
                        if (c - 3 >= 0 && r + 3 < 7) { //FLAG
                            if (moves[r][c].getState().equals(moves[r + 1][c - 1].getState()) &&
                            moves[r][c].getState().equals(moves[r + 2][c - 2].getState()) &&
                            moves[r][c].getState().equals(moves[r + 3][c - 3].getState())) {
                                if (moves[r][c].getState().equals(PLAYERMOVE)) {
                                    return "PLAYER has won the game with 4 in a row, congratulations!!";
                                } else {
                                    return "COMPUTER has won the game with 4 in a row, tough luck!!";
                                }
                            }
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // As the move could begin in slot 5, this allows the user to continue playing the game when the check contains higher numbers than the size of the array
        }
        return null; // No winner yet
    }

    /**
     * findRow 
     * This method finds the lowest empty slot in the selected column, which will be assigned
     * @param col - the selected column
     * @return the row value
     */
    public int findRow(int col) {
        for (int row = 5; row >= 0; row--) { // Loop through rows from bottom to top
            if (moves[col][row].getState().equals(EMPTYSLOT)) { // Check if the slot at the current row is empty
                return row; // Return the row number of the first empty slot found
            }
        }
        return -1; // Return -1 if the column is full (no empty slots found)
    }

    /**
     * findTopOccupiedRow 
     * Finds the highest occupied slot in the selected column to assign
     * @param col - the selected column
     * @return the row value
     */
    public int findTopOccupiedRow(int col) {
        for (int row = 0; row < 6; row++) { // Loop through each row in the column.
            if (!moves[col][row].getState().equals(EMPTYSLOT)) { // Check if the slot is not empty.
                return row; // Return the row number of the highest occupied slot.
            }
        }
        return -1; // Return -1 if the column is completely empty.
    }

}//End of class ConnectFour
