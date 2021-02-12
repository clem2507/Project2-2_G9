package domains;

import backend.*;
import nlp.MatchedSequence;

import java.awt.*;
import java.io.File;
import java.util.concurrent.BlockingQueue;

public class OpenApplication extends Domain {

    public OpenApplication() {
        super(DomainNames.OpenApp);

        addPattern("<open><...>");
        addPattern("<run><...>");
    }

    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        return new Skill(this, outputChannel) {
            @Override
            public void run() {

                String path = sequence.getStringAt(1);
                try {
                    File file = new File(path);
                    Desktop dt = Desktop.getDesktop();
                    dt.open(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                pushMessage("Application opened", MessageType.STRING);
            }
        };
    }

}
