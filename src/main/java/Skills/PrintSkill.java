package Skills;

import backend.Result;
import backend.SkillDispatcher;
import backend.Skill;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class PrintSkill extends SkillDispatcher {

    public PrintSkill() {
        super("PrintSomething");
    }

    @Override
    public double weight(List<String> tokens) {
        return tokens.get(0).toLowerCase().equals("print")? 1.0:0.0;
    }

    @Override
    public Skill createTask(List<String> tokens, BlockingQueue<Result> resultsQueue) {
        return new Skill(this, tokens, resultsQueue) {
            @Override
            public void run() {
                String output = "";
                for (int x = 1; x < tokens.size(); x++){
                    output += tokens.get(x) + " ";
                }
                pushMessage(output);
            }
        };
    }
}
