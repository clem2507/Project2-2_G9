package domains.Location;

import backend.*;
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

        // Now, if someone types "get my location" or "tell me my location" or something like that
        addPattern("<find, tell, get> <#:2> <location, position, localization>"); // This pattern will match
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
                pushMessage(message, MessageType.STRING);
                System.out.println(message);
            }
        };
    }
}
