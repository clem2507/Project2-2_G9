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
        String str = sequence.getStringAt(1); // Matches with the empty slot are guaranteed to be String or null

        // Check if str is null inside of the task. Never return null. Once a domain is committed to generate an
        // output (i.e. at least one pattern matched with the query), it *must* dispatch a skill
        return new Skill(this, outputChannel) {
            @Override
            public void run() {

                if(!str.isEmpty()) {
                    pushMessage(str, MessageType.STRING);
                    System.out.println(str);
                }

                else{
                    pushMessage("You gave me nothing to say", MessageType.STRING);
                    System.out.println("You gave me nothing to say");
                }

            }
        };
    }
}
