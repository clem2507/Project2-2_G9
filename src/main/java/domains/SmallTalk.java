package domains;

import backend.*;
import nlp.MatchedSequence;

import java.util.concurrent.BlockingQueue;

public class SmallTalk extends Domain {


    public SmallTalk() {
        super(DomainNames.SmallTalk);

        addPattern("<Hello>");
        addPattern("<Hi>");
        addPattern("<Hey>");
        addPattern("<Good Morning>");
        addPattern("<Good Afternoon>");
        addPattern("<Good Evening>");
        addPattern("<How are you>");
        addPattern("<fine>");
        addPattern("<good>");
        addPattern("<happy>");
        addPattern("<bad>");
        addPattern("<not good>");
        addPattern("<sick>");
    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        return new Skill(this, outputChannel) {
            @Override
            public void run() {
                if(sequence.getStringAt(0).toLowerCase().equals(("How are you").toLowerCase())){
                    pushMessage("I am good. How are you?", MessageType.STRING);
                } else if(sequence.getStringAt(0).toLowerCase().equals(("Good morning").toLowerCase())){
                    pushMessage("Good morning my friend!", MessageType.STRING);
                }else if(sequence.getStringAt(0).toLowerCase().equals(("bad").toLowerCase()) ||
                        sequence.getStringAt(0).toLowerCase().equals(("not good").toLowerCase()) ||
                        sequence.getStringAt(0).toLowerCase().equals(("sick").toLowerCase())){
                    pushMessage("I am sorry. I hope you get well soon!", MessageType.STRING);
                }else if(sequence.getStringAt(0).toLowerCase().equals(("good").toLowerCase()) ||
                        sequence.getStringAt(0).toLowerCase().equals(("fine").toLowerCase()) ||
                        sequence.getStringAt(0).toLowerCase().equals(("happy").toLowerCase())){
                    pushMessage("Glad to hear that! How can I help you?", MessageType.STRING);
                } else if(sequence.getStringAt(0).toLowerCase().equals(("Good morning").toLowerCase())){
                    pushMessage("Good morning my friend!", MessageType.STRING);
                } else if(sequence.getStringAt(0).toLowerCase().equals(("Good afternoon").toLowerCase())){
                    pushMessage("Good afternoon my friend!", MessageType.STRING);
                } else if(sequence.getStringAt(0).toLowerCase().equals(("Good evening").toLowerCase())){
                    pushMessage("Good evening my friend!", MessageType.STRING);
                }
                else{
                    pushMessage("Hello", MessageType.STRING);
                }
            }
        };
    }
}
