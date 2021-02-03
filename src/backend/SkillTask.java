package backend;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class SkillTask implements Runnable{
    private Skill parent;
    private List<String> queryTokens;
    private BlockingQueue<Result> outputChannel;

    SkillTask(Skill parent, List<String> tokens, BlockingQueue<Result> outputChannel){
        this.parent = parent;
        this.queryTokens = tokens;
        this.outputChannel = outputChannel;
    }

    /**
     * Put a message into the output queue
     * @param message
     */
    protected void pushMessage(String message){

        try {
            outputChannel.put(new Result(parent, message));
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Return the sequence of tokens given to this task
     * @return
     */
    protected List<String> getTokens(){
        return queryTokens;
    }

}
