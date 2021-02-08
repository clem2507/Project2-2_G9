package domains.Location;

import backend.AssistantMessage;
import backend.Domain;
import backend.DomainNames;
import backend.Skill;
import nlp.MatchedSequence;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class FindMe extends Domain {

    public FindMe() {
        super(DomainNames.FindMe);
        addPattern("<find me>");
        addPattern("<where am I>");
        addPattern("<what, which, where> <is my> <location, position, localization>");
    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        return new Skill(this, outputChannel) {
            @Override
            public void run() {
                String city = "UNKNOWN";

                try {
                    city = CurrentLocation.getLocation();
                } catch (IOException e) {

                    e.printStackTrace();
                }

                String message = "You are in " + city;
                pushMessage(message);
                System.out.println(message);
            }
        };
    }
}
