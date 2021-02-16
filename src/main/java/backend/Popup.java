package backend;

import javax.swing.*;
import java.util.Optional;

public class Popup {
    private static final String DEFAULT_TITLE = "DKE Assistant";

    /**
     * Shows a popup with a custom message. Blocks the thread until the user
     * closes the popup window.
     * @param text String to display
     */
    public static void message(final String text){
        JOptionPane.showMessageDialog(null, text, DEFAULT_TITLE, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Shows a popup with a custom question and waits for user's response (yes/no). Blocks the thread
     * until the user responds or the window is closed.
     * @param text String to display
     * @return boolean representing the yes (true) or no (false)
     */
    public static boolean binaryQuestion(final String text){
        int answer = JOptionPane.showConfirmDialog(
                null,
                text,
                DEFAULT_TITLE,
                JOptionPane.YES_NO_OPTION
        );

        return answer == JOptionPane.YES_OPTION;
    }

    /**
     * Shows a popup with a custom message and waits for the user's input. Blocks the thread
     * until the user responds or the window is closed.
     * @param text message to show
     * @return Optional<String> representing the user's input
     */
    public static Optional<String> userInput(final String text){
        String input = JOptionPane.showInputDialog(null, text, DEFAULT_TITLE, JOptionPane.QUESTION_MESSAGE);
        return Optional.ofNullable((input != null && input.length() > 0)? input:null);
    }

    public static void main(String[] args){
        Optional<String> input = userInput("What is your name?");

        if(input.isPresent()){
            System.out.println(input.get());
        }

        else {
            System.out.println("N/A");
        }

        System.out.println(binaryQuestion("Are you satisfied with the service?"));

        message("Goodbye");
    }

}
