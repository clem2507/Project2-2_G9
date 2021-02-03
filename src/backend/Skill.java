package backend;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class Skill {
    private final String name;

    protected Skill(String name) {
        this.name = name;
    }

    /**
     * Measure how sure the skill is that it can resolve the query correctly
     * @param tokens list tokens in the query
     * @return a number between 0.0 and 1.0
     */
    public abstract double weight(List<String> tokens);

    /**
     * Return a task to be queue for execution
     * @param tokens
     * @param resultsQueue
     * @return a skill task to be executed
     */
    public abstract SkillTask createTask(List<String> tokens, BlockingQueue<Result> resultsQueue);

    public String getName(){
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if(this == obj)
            return true;

        if(obj instanceof Skill)
            return name.equals(((Skill) obj).getName());

        return false;
    }

}
