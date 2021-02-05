package backend;

public class AssistantOutput {
    private final Domain sender;
    private final String message;

    public AssistantOutput(final Domain sender, final String message){
        this.sender = sender;
        this.message = message;
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
