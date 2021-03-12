package domains.Search;

import backend.*;
import backend.common.OS.UnsupportedOSException;
import nlp.MatchedSequence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class SearchDomain extends Domain {

    public SearchDomain() {
        super(DomainNames.Search);

        // Define patterns for this domain in the constructor
        addPattern("<search> <...>");
        addPattern("<look up> <...>");
        addPattern("<open google> <...>");
        // Multiple patterns are supported
    }
    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        String str = sequence.getStringAt(1);

        return new Skill(this, outputChannel) {
            ArrayList<String> links;

            @Override
            public void run() {
                try {
                    if(sequence.getStringAt(0).toLowerCase().contains("search") || sequence.getStringAt(0).toLowerCase().contains("look up")){
                        links = Search.search(str);
                        for(String x: links){
                            pushMessage(x, MessageType.HYPER_LINK);
                        }
                        pushMessage("If you would like to open google for more choices, type 'Open Google'", MessageType.STRING);
                    }
                    if(sequence.getStringAt(0).toLowerCase().contains("open google") || sequence.getStringAt(0).toLowerCase().contains("look up")){
                        Search.googleSearch(Files.readAllLines(Paths.get("output.txt")).get(0));
                    }
                } catch (IOException | UnsupportedOSException e) {
                    e.printStackTrace();
                }

            }
        };
    }
}
