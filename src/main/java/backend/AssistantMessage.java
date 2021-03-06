package backend;

import java.util.Optional;

/**
 * Represents an output message sent by the assistant or any of the
 * skills running in the background.
 * NOTE: Domains do not generate messages
 */
public class AssistantMessage {
    private final Domain sender;
    private final String message;
    private final MessageType messageType;

    public AssistantMessage(final Domain sender, final String message, final MessageType messageType){
        this.sender = sender;
        this.message = message;
        this.messageType = messageType;
    }

    /**
     * Returns a reference to the skill that generated the output. If the output
     * is not generated by a skill (which is guaranteed to belong to a domain), then
     * the sender is empty/non-existent, which inherently means that the output comes from
     * the assistant class.
     * @return reference to a skill
     */
    public Optional<Domain> getSender(){
        return Optional.ofNullable(sender);
    }

    /**
     * Returns the message
     * @return a string
     */
    public String getMessage(){
        return message;
    }

    /**
     * Returns the type of the message. This is used to tell the GUI what to do
     * with the string.
     *
     * For instance, if the type if IMAGE, then the string is a path to said image
     * and the GUI is expected to display it.
     * @return a MessageType enum
     */
    public MessageType getMessageType(){
        return messageType;
    }

    @Override
    public String toString() {
        return sender.toString() + ": " + message + " | TYPE " + messageType;
    }
}