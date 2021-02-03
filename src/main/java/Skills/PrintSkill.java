package Skills;

import backend.Result;
import backend.SkillDispatcher;
import backend.Skill;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class PrintSkill extends SkillDispatcher {
    private String keyword = "print";

    protected PrintSkill(String name) {
        super(name);
    }

    @Override
    public double weight(List<String> tokens) {
        int weight = 0;
        if (tokens.get(0).toLowerCase().equals(keyword)){
            weight = 1;
            return weight;
        }
        return weight;
    }

    @Override
    //TODO: this method is incomplete
    public Skill createTask(List<String> tokens, BlockingQueue<Result> resultsQueue) {
        return null;
    }
}
