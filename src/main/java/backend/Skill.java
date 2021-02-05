package backend;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class Skill implements Runnable{
    private final Domain parent;
    private final List<String> queryTokens;
    private final BlockingQueue<AssistantMessage> outputChannel;

    public Skill(final Domain parent, final List<String> tokens, final BlockingQueue<AssistantMessage> outputChannel){
        this.parent = parent;
        this.queryTokens = tokens;
        this.outputChannel = outputChannel;
    }

    protected void pushMessage(final String message){

        try {
            outputChannel.put(new AssistantMessage(parent, message));
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    protected List<String> getTokens(){
        return queryTokens;
    }

}