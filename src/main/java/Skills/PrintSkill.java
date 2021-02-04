package Skills;

import backend.Result;
import backend.SkillDispatcher;
import backend.Skill;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class PrintSkill extends SkillDispatcher {
    private String keyword = "print";

    public PrintSkill(String name) {
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
    public Skill createTask(List<String> tokens, BlockingQueue<Result> resultsQueue) {
        Skill skill = new Skill(new PrintSkill(getUniqueName()),tokens, resultsQueue) {
            @Override
            public void run() {
                for (int x = 1; x<tokens.size(); x++){
                    System.out.println(tokens.get(x) + " ");
                }
            }
        };
        return skill;
    }
}
