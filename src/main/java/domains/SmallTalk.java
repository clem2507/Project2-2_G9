package domains;

import backend.*;
import nlp.MatchedSequence;

import java.util.concurrent.BlockingQueue;

public class SmallTalk extends Domain {


    public SmallTalk() {
        super(DomainNames.SmallTalk);

        addPattern("<Hello>");
        addPattern("<Hi>");
        addPattern("<Good Morning>");
        addPattern("<Good Afternoon>");
        addPattern("<How are you>");
    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        return new Skill(this, outputChannel) {
            @Override
            public void run() {
                if(sequence.getStringAt(0).toLowerCase().equals(("How are you").toLowerCase())){
                    pushMessage("I am good. How can I help you?", MessageType.STRING);
                }else{
                    pushMessage("Hello", MessageType.STRING);
                }
            }
        };
    }
}
