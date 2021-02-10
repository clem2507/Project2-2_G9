package backend;

import GUI.Main;

public class AssistantMessage {
    private final Domain sender;
    private final String message;

    public AssistantMessage(final Domain sender, final String message){
        this.sender = sender;
        this.message = message;
        if (message.length() > 0) {
            Main.botText.setText("Bot: " + message);
        }
        else {
            Main.botText.setText("Bot: query not understood");
        }
        Main.robotInteractionText.setText("Anything else?\nI'm free");
        Main.isAgentFree = true;
    }

    /**
     * Return a reference to the skill that generated the output
     * @return reference to a skill
     */
    public Domain getSender(){
        return sender;
    }

    /**
     * Return message
     * @return a string
     */
    public String getMessage(){
        return message;
    }

    @Override
    public String toString() {
        return "[" + sender.toString() + "]: " + message;
    }
}
