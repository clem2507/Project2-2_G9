package domains.Search;

import backend.*;
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
        addPattern("<link number:> <param:int>");
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
                        int count = 0;
                        for(String x: links){

                            pushMessage(count +") "+x, MessageType.STRING);
                            count++;
                        }
                        pushMessage("Please specify which link you want to visit by typing-> link number: <number>", MessageType.STRING);
                    }

                    if(sequence.getStringAt(0).toLowerCase().contains("link number")){
                        String number =  sequence.getStringAt(1);
                        System.out.println(number);
                        int num = Integer.parseInt(number);
                        System.out.println(num);
                        String line = Files.readAllLines(Paths.get("output.txt")).get(num);
                        System.out.println(line);
                        Search.open(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }
}
