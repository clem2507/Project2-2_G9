package GUI;

import backend.MessageType;

public class ConsoleOutput {
    private final boolean isUser;
    private final String content;
    private final MessageType messageType;

    public ConsoleOutput(final String content, final boolean isUser, final MessageType messageType){
        this.isUser = isUser;
        this.content = content;
        this.messageType = messageType;
    }

    public String getContent(){
        return content;
    }

    public MessageType getMessageType(){
        return messageType;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isBot(){
        return !isUser;
    }

}
