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
                if(sequence.getStringAt(0).toLowerCase().contains(("How are you").toLowerCase())){
                    pushMessage("I am good. How are you?", MessageType.STRING);
                } else if(sequence.getStringAt(0).toLowerCase().contains(("Good morning").toLowerCase())){
                    pushMessage("Good morning my friend!", MessageType.STRING);
                }else if(sequence.getStringAt(0).toLowerCase().contains(("bad").toLowerCase()) ||
                        sequence.getStringAt(0).toLowerCase().contains(("not good").toLowerCase()) ||
                        sequence.getStringAt(0).toLowerCase().contains(("sick").toLowerCase())){
                    pushMessage("I am sorry. I hope you get well soon!", MessageType.STRING);
                }else if(sequence.getStringAt(0).toLowerCase().contains(("good").toLowerCase()) ||
                        sequence.getStringAt(0).toLowerCase().contains(("fine").toLowerCase()) ||
                        sequence.getStringAt(0).toLowerCase().contains(("happy").toLowerCase())){
                    pushMessage("Glad to hear that! How can I help you?", MessageType.STRING);
                } else if(sequence.getStringAt(0).toLowerCase().contains(("Good morning").toLowerCase())){
                    pushMessage("Good morning my friend!", MessageType.STRING);
                } else if(sequence.getStringAt(0).toLowerCase().contains(("Good afternoon").toLowerCase())){
                    pushMessage("Good afternoon my friend!", MessageType.STRING);
                } else if(sequence.getStringAt(0).toLowerCase().contains(("Good evening").toLowerCase())){
                    pushMessage("Good evening my friend!", MessageType.STRING);
                }
                else{
                    pushMessage("Hello", MessageType.STRING);
                }
            }
        };
    }
}
