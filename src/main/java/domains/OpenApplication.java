package domains;

import backend.*;
import backend.common.FileSystem;
import nlp.MatchedSequence;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class OpenApplication extends Domain {
    private final Set<FileSystem.LinkData> listedApps;

    public OpenApplication() {
        super(DomainNames.OpenApp);
        addPattern("<open, run, execute, exec, start> <@program_name, ...>");
        listedApps = new HashSet<>();
    }

    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        Optional<Integer> programNameIndex = sequence.getSlotIndex("@program_name");
        Optional<String> programName = programNameIndex.map(sequence::getStringAt);

        return new Skill(this, outputChannel) {

            private boolean askFurtherSearch(){
                assert programName.isPresent();
                return Popup.binaryQuestion(
                        "No match found for " + programName.get() +
                                ". Would you like do run a complete system search? (It may take a while)"
                );
            }

            private boolean searchApp(){
                assert programName.isPresent();
                Optional<FileSystem.LinkData> bestMatch = FileSystem.findClosestMatch(
                        listedApps,
                        programName.get(),
                        FileSystem.DEFAULT_MIN_THRESHOLD
                );

                if(bestMatch.isPresent()){
                    FileSystem.runProgramFromLink(bestMatch.get());
                    pushMessage("Running " + programName.get(), MessageType.STRING);
                    return true;
                }

                return false;
            }

            @Override
            public void run() {

                if(programName.isPresent()){

                    synchronized (listedApps) {

                        if(!searchApp()){

                            if(askFurtherSearch()){
                                listedApps.clear();

                                try {
                                    listedApps.addAll(FileSystem.listAllLinks());

                                    if(!searchApp()){
                                        pushMessage("I am sorry, " + programName.get() + " could not be found", MessageType.STRING);
                                    }

                                }

                                catch (FileSystem.UnsupportedOSException e) {
                                    e.printStackTrace();
                                    pushMessage("This skill is not supported on your operating system", MessageType.STRING);
                                }

                            }

                        }

                    }

                }

                else {
                    pushMessage("No program specified", MessageType.STRING);
                }

            }
        };
    }

}
