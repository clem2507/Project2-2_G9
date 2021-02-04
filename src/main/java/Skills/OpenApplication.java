package Skills;

import backend.Result;
import backend.Skill;
import backend.SkillDispatcher;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class OpenApplication extends SkillDispatcher {
    private String keyword = "open";


    protected OpenApplication(String name) {
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
        Skill skill = new Skill(new PrintSkill(getName()),tokens, resultsQueue) {
            @Override
            public void run() {
                String application = null;
                for (int x = 1; x<tokens.size(); x++){
                    application = application + tokens.get(x) + " ";
                }

                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec( new String[] { "open" , "-a", application }) ;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        return skill;
    }
}
