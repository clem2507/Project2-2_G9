package backend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public abstract class SkillDispatcher {
    private final String uniqueName;

    protected SkillDispatcher(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    /**
     * Measure how sure the skill is that it can resolve the query correctly
     * @param tokens list tokens in the query
     * @return a number between 0.0 and 1.0
     */
    public abstract double weight(List<String> tokens); //This will change soon!

    /**
     * Return a task to be queue for execution
     * @param tokens
     * @param resultsQueue
     * @return a skill to be executed
     */
    public abstract Skill createTask(List<String> tokens, BlockingQueue<Result> resultsQueue);

    public String getUniqueName(){
        return uniqueName;
    }

    @Override
    public int hashCode() {
        return uniqueName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if(this == obj)
            return true;

        if(obj instanceof SkillDispatcher)
            return uniqueName.equals(((SkillDispatcher) obj).getUniqueName());

        return false;
    }

}
