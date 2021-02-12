package domains;

import backend.*;
import nlp.MatchedSequence;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Leave extends Domain {
    public Leave() {
        super(DomainNames.Leave);
        addPattern("<Bye>");
        addPattern("<Good Bye>");
        addPattern("<Exit>");
        addPattern("<Leave>");
    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        return new Skill(this, outputChannel) {
            @Override
            public void run() {
                pushMessage("Good Bye. Have a nice day!", MessageType.STRING);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        };
    }
}
