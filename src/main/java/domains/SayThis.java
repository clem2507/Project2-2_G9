package domains;

import backend.AssistantMessage;
import backend.Domain;
import backend.Skill;
import backend.DomainNames;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class SayThis extends Domain {

    public SayThis() {
        super(DomainNames.Say);
    }

    @Override
    public double weight(List<String> tokens) {
        return tokens.get(0).toLowerCase().equals("print")? 1.0:0.0;
    }

    @Override
    public Skill dispatchSkill(List<String> tokens, BlockingQueue<AssistantMessage> resultsQueue) {
        return new Skill(this, tokens, resultsQueue) {
            @Override
            public void run() {
                System.out.println("Running task...");
                String output = "";
                for (int x = 1; x < tokens.size(); x++){
                    output += tokens.get(x) + " ";
                }
                pushMessage(output);
                System.out.println(output);
                System.out.println("End of task.");
            }
        };
    }
}
