import java.io.Serializable; // Import for the Serializable interface
import java.util.Observable;


/**
 * Slot
 * This class handles the state of each slot in the game. Extending observable in preparation for the addition of a GUI.
 * @author M Currie 
 * @version 1.3
 */
public class Slot extends Observable implements Serializable {
    private String state; // The current state of the slot
    private int row, col; // The row and column number of the slot 

    // Constructor of the class Slot
    /**
     * Constructor of the class Slot
     * This creates the slot and denotes where it is placed in the game board.
     * 
     * @param col - the slot's column number
     * @param row - the slot's row number
     */
    public Slot(int col, int row) {
        this.row = row; // Set the row number of the slot
        this.col = col; // Set the column number of the slot
        this.state = ConnectFour.EMPTYSLOT; // Initialize the slot's state to be empty
    }

    /**
     * setState
     * This method sets the current state of the slot
     * @param newState - the new state of the slot
     */
    public void setState(String newState) {
        if (isValidState(newState)) { // Check if the new state is valid
            this.state = newState; // Set the current state to the new state
        }
    }

    /**
     * getState
     * This provides the current state of the slot
     * @return the current state of the slot
     */
    public String getState() {
        return state; // Return the current state of the slot
    }

    /**
     * isValidState 
     * This method checks whether the selected state is valid
     * @param state - the state that is being checked
     * @return a Boolean value showing whether the state is valid or not
     */
    public static boolean isValidState(String state) {
        // Check if the state is one of the valid states: COMPUTERMOVE, PLAYERMOVE, or EMPTYSLOT
        if (state.equals(ConnectFour.COMPUTERMOVE) || state.equals(ConnectFour.PLAYERMOVE) || state.equals(ConnectFour.EMPTYSLOT)) {
            return true; // The state is valid
        } else {
            return false; // The state is not valid
        }
    }  
}//End of class Slot
