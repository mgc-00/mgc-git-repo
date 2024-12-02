import java.util.Scanner;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.Stack;
import javax.swing.*;

/**
 * ConnectFourUI
 * Text-based user interface class for ConnectFour game
 * @author M Currie
 * @version 1.3
 */
public class ConnectFourUI {
    private Scanner consoleReader; //Reads inputs from the console
    private ConnectFour game; //The ConnectFour game
    private Slot moves[][];// 2D array of moves
    private String menuChoice = "x"; // user's selection from the menu, x by default
    private Stack<Assign>    stack  = null;
    private boolean gameOver;

    /**
     * Constructor for the ConnectFourUI class.
     * This method creates the game, initialises the scanner to read input, displays the initial game board and menu.
     */
    public ConnectFourUI() {
        game = new ConnectFour(); // Create a new instance of the ConnectFour game.
        consoleReader = new Scanner(System.in); // Initialize a scanner to read user input from the console.
        displayGame(); // Display the initial state of the game board.
        menu(); // Display the game menu for user interaction.
    }

    /**
     * Public static void main - the first method that runs when the project is run. 
     * This method initializes a new instance of the ConnectFour UI.
     * @param args 
     */
    public static void main(String args[]) {
        // Create an instance of the ConnectFourUI class
        ConnectFourUI ui = new ConnectFourUI();
    }

    /**
     * Menu
     * This method displays the menu that provides users with the options needed to play the game
     */
    public void menu() {
        // Continue displaying the menu until the user selects "Q" to quit.
        while (!menuChoice.equalsIgnoreCase("Q")) {
            // Display the menu options to the user.
            System.out.println("Please select an option: \n"
                + "       [M] make move\n"  // Option to make a move
                + "       [S] save game\n"  // Option to save the game
                + "       [L] load saved game\n"  // Option to load a saved game
                + "       [U] undo move\n"  // Option to undo the last move
                + "       [C] clear game\n"  // Option to clear the game
                + "       [Q] quit game\n");  // Option to quit the game
            // Get the user's choice and store it in the menuChoice variable.
            menuChoice = getChoice();
        }
    }

    /**
     * getChoice
     * This method processes the selection the user has made in the menu
     * @return the choice of the user
     */
    public String getChoice() {
        String choice = consoleReader.next(); // Reads the user's input
        if (choice.equalsIgnoreCase("M")) { // Checks if the input is "M" (Make Move)
            makeMove(); // Calls the method to make a move in the game
            checkWin(); // Checks if there's a winner after making the move
        } else if (choice.equalsIgnoreCase("S")) { // Checks if the input is "S" (Save Game)
            saveGame(); // Calls the method to save the game
        } else if (choice.equalsIgnoreCase("U")) { // Checks if the input is "U" (Undo Move)
            undoMove(); // Calls the method to undo the last move
        } else if (choice.equalsIgnoreCase("L")) { // Checks if the input is "L" (Load Game)
            loadGame(); // Calls the method to load a saved game
        } else if (choice.equalsIgnoreCase("C")) { // Checks if the input is "C" (Clear Game)
            clearGame(); // Calls the method to clear the game
        } else if (choice.equalsIgnoreCase("Q")) { // Checks if the input is "Q" (Quit Game)
            System.exit(0); // Exits the program
        }
        return choice; // Returns the user's choice
    }

    /**
     * saveGame
     * This method saves the current game state
     */
    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser(); // Create a file chooser dialog to select the save location

        int result = fileChooser.showSaveDialog(null); // Display the save dialog and capture the user's choice

        if (result == JFileChooser.APPROVE_OPTION) { // Check if the user selected the "Save" option in the dialog
            String fileName = fileChooser.getSelectedFile().getAbsolutePath(); // Get the absolute path of the selected file

            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName)); // Create an output stream to write the game data to a file
                outputStream.writeObject(game); // Serialize and write the game object to the file
                outputStream.close(); // Close the output stream
                JOptionPane.showMessageDialog(null, "Game saved successfully!"); // Display a success message
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error saving the game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Display an error message if an exception occurs
            }
        }
    }

    /**
     * loadGame
     * This method should load a previous saved game
     */
    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser(); // Create a file chooser dialog to select the saved game file

        int result = fileChooser.showOpenDialog(null); // Show the open file dialog and store the user's choice

        if (result == JFileChooser.APPROVE_OPTION) { // Check if the user selected a file
            String fileName = fileChooser.getSelectedFile().getAbsolutePath(); // Get the absolute file path of the selected file
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName)); // Create an ObjectInputStream to read the saved game file
                game = (ConnectFour) inputStream.readObject(); // Deserialize the saved game and assign it to the 'game' variable
                inputStream.close(); // Close the ObjectInputStream
                gameOver = false; // Reset the game over status
                displayGame(); // Update the GUI to display the loaded game state
                JOptionPane.showMessageDialog(null, "Game loaded successfully!"); // Show a success message dialog
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Error loading the game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Show an error message dialog if loading fails
            }
        }
    }

    /**
     * undoMove
     * This method undoes the previous move made in the game, along with the corresponding computer move
     */
    public void undoMove() {
        System.out.println("Move undone"); // Print a message to indicate that the move is being undone
        game.undoMove(); // Call the game's undoMove method to revert the previous move
        displayGame(); // Update the display after undoing the move
    }

    /**
     * clearGame
     * This method clears the game board and any record of moves, to reset the game
     */
    public void clearGame() {
        game = new ConnectFour(); // Create a new instance of the ConnectFour game, effectively resetting it
        displayGame(); // Display the cleared game board
    }

    /**
     * displayGame
     * This method displays the blank game board ready to play the game 
     */    

    public void displayGame() {
        Slot[][] moves = game.getMoves(); // Retrieve the game's moves
        System.out.println("*****************************\n"
            + "  Welcome to Mike's game of \n"
            + "   Connect Four, Good luck!\n"
            + "*****************************");

        for (int r = 0; r < 6; r++) { // Loop through each row of the game board
            for (int c = 0; c < 7; c++) { // Loop through each column of the game board
                String state = moves[c][r].getState(); // Get the state of the current slot
                System.out.print("| " + (state.equals(ConnectFour.EMPTYSLOT) ? " " : state) + " "); // Print the slot's state or a space if it's empty
            }
            System.out.print("|\n"); // Print the end of the row
            System.out.println(" - - - - - - - - - - - - - -"); // Print a row separator
        }

        System.out.println("  0   1   2   3   4   5   6"); // Print column numbers
    }

    /**
     * makeMove
     * This method displays the move the player has made, followed by the corresponding computer move
     * // modified to update the game board size //
     */
    public void makeMove() {
        System.out.println("Please enter the column you wish to select"); // Prompt the player to enter a column selection.
        int playerMove = consoleReader.nextInt(); // Read the player's chosen column.
        game.addMove(playerMove, true); // Add the player's move to the game board.
        game.generateComputerMove(); // Generate the computer's move.

        displayGame(); // Update the display after making a move

        checkWin(); // Check if there is a winner after the moves.
    }

    /**
     * checkWin
     * Displays game winner
     */
    public void checkWin() {
        // Check if the game has a winner by calling the checkWin method of the 'game' object.
        if (game.checkWin()!= null) {
            // If there is a winner, print the winner's name to the console.
            System.out.println("We have a WINNER!... " + game.checkWin());
            // Call the 'replay' method to offer the option to play again.
            replay();
        };
    }

    /**
     * replay
     * This method gives the user the option to play again, or quit, once the game is won
     */
    public void replay() {
        System.out.println("Would you like to play again? (Y/N)"); // Prompt the user to play again or quit
        String choice = consoleReader.next(); // Read the user's choice (Y/N)
        if (choice.equalsIgnoreCase("Y")) { // Check if the choice is "Y" (case-insensitive)
            game = new ConnectFour(); // Create a new ConnectFour game
            displayGame(); // Display the game board
            menu(); // Display the menu for the user to make a choice
        } else if (choice.equalsIgnoreCase("N")) { // Check if the choice is "N" (case-insensitive)
            System.exit(0); // Exit the program if the user chooses not to play again
        }
    }

}//End of class ConnectFourUI