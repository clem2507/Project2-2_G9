package backend;

import java.util.concurrent.BlockingQueue;

public abstract class Skill implements Runnable{
    private final Domain parent;
    private final BlockingQueue<AssistantMessage> outputChannel;

    public Skill(final Domain parent, final BlockingQueue<AssistantMessage> outputChannel){
        this.parent = parent;
        this.outputChannel = outputChannel;
    }

    protected void pushMessage(final String message, final MessageType messageType){

        try {
            outputChannel.put(new AssistantMessage(parent, message, messageType));
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}