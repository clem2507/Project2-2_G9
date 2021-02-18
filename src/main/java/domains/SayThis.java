package domains;

import backend.*;
import nlp.MatchedSequence;

import java.util.concurrent.BlockingQueue;

public class SayThis extends Domain {

    public SayThis() {
        super(DomainNames.Say);

        // Define patterns for this domain in the constructor
        addPattern("<say> <...>");
        addPattern("<repeat> <...>");
        addPattern("<print> <...>");
        // Multiple patterns are supported
    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        // Now request the parts of the query matched with the <...> (i.e. empty slot)
        String str = sequence.getStringAt(1); // Matches with the empty slot are guaranteed to be String

        return new Skill(this, outputChannel) {
            @Override
            public void run() {

                if(!str.isEmpty()) {
                    pushMessage(str, MessageType.STRING);
                }

                else{
                    pushMessage("You gave me nothing to say", MessageType.STRING);
                }

            }
        };
    }
}
