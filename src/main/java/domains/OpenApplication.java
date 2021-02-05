package domains;

import backend.AssistantOutput;
import backend.Skill;
import backend.Domain;
import backend.DomainNames;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class OpenApplication extends Domain {

    public OpenApplication() {
        super(DomainNames.OpenApp);
    }

    @Override
    public double weight(List<String> tokens) {
        return tokens.get(0).toLowerCase().equals("open")? 1.0:0.0;
    }

    @Override
    public Skill createTask(List<String> tokens, BlockingQueue<AssistantOutput> resultsQueue) {
        return new Skill(this, tokens, resultsQueue) {
            @Override
            public void run() {
                String application = "";
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
    }
}
