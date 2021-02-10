package domains.Photo;

import backend.AssistantMessage;
import backend.Domain;
import backend.DomainNames;
import backend.Skill;
import nlp.MatchedSequence;

import java.util.concurrent.BlockingQueue;

public class Photo extends Domain {
    public Photo(){
        super(DomainNames.Photo);

        addPattern("<photo>");
        addPattern("<selfie>");

    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        System.out.println("SEQUENCE -->\t" + sequence);
        return new Skill(this, outputChannel) {
            @Override
            public void run() {

                try {

                String messageA = "3";
                pushMessage(messageA);
                System.out.println(messageA);

                Thread.sleep(900);

                String messageB = "2";
                pushMessage(messageB);
                System.out.println(messageB);

                Thread.sleep(900);

                String messageC = "1";
                pushMessage(messageC);
                System.out.println(messageC);

                Thread.sleep(900);

                String messageD = "Smile!";
                pushMessage(messageD);
                System.out.println(messageD);

                Screenshot screenshot = new Screenshot();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
