package domains;

import backend.*;
import backend.common.CurrentDesktop;
import nlp.MatchedSequence;

import java.awt.*;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class OpenApplication extends Domain {

    public OpenApplication() {
        super(DomainNames.OpenApp);
        addPattern("<open, run, execute, exec, start> <@program_name, ...>");
    }

    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        Optional<Integer> programNameIndex = sequence.getSlotIndex("@program_name");
        Optional<String> programName = programNameIndex.map(sequence::getStringAt);

        return new Skill(this, outputChannel) {
            @Override
            public void run() {

                if(programName.isPresent()){
                    Optional<CurrentDesktop.LinkData> linkData = CurrentDesktop.getMostSimilarLink(
                            programName.get(),
                            CurrentDesktop.DEFAULT_MIN_THRESHOLD
                    );

                    if(linkData.isPresent()){
                        CurrentDesktop.runProgramFromLink(linkData.get());
                        pushMessage("Running " + programName.get(), MessageType.STRING);
                    }

                    else {
                        pushMessage("I could not find the program you specified", MessageType.STRING);
                    }

                }

                else{
                    pushMessage("You did not tell me which program to open", MessageType.STRING);
                }

            }
        };
    }

}
