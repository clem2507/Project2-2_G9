package domains.Location;

import backend.AssistantMessage;
import backend.Domain;
import backend.DomainNames;
import backend.Skill;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class FindMe extends Domain {
    public FindMe() {
        super(DomainNames.FindMe);
    }

    @Override
    public double weight(List<String> tokens) {
        return 0;
    }

    @Override
    public Skill dispatchSkill(List<String> tokens, BlockingQueue<AssistantMessage> resultsQueue) {
        return new Skill(this, tokens, resultsQueue) {
            @Override
            public void run() {
                String city = "";
                try {
                    city = CurrentLocation.getLocation();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String message = "You are in the city of " + city;
                pushMessage(message);
                System.out.println(message);
            }
        };

    }
}
