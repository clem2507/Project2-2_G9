package domains;

import backend.AssistantMessage;
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
    public Skill dispatchSkill(List<String> tokens, BlockingQueue<AssistantMessage> resultsQueue) {
        return new Skill(this, tokens, resultsQueue) {
            @Override
            public void run() {
                String application = "";
                for (int x = 1; x<tokens.size(); x++){
                    application = application + tokens.get(x) + " ";
                }

                // The user must say something like this: Open C:\\Users\\aysen\\AppData\\Roaming\\Spotify\\Spotify.exe
                // This path is found when you right click the app, Properties -> Copy "Target"
                try {
                    Process process = Runtime.getRuntime().exec(application);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
