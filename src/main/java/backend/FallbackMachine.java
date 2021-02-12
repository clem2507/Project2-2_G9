package backend;

import nlp.MatchedSequence;
import nlp.PatternMatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FallbackMachine implements FallbackInterpreter {

    private String path;
    // Key-Value pair of pattern and response
    final private List<AbstractMap.SimpleEntry<String, String>>  simpleSkills;

    public FallbackMachine() {
        simpleSkills = new ArrayList<>();
    }

    @Override
    public String processQuery(String query) {
        String notFound = "Sorry, I didn't understand what you meant by \"" + query + "\" ";
        if(simpleSkills.size() == 0) {
            return notFound;
        }
        MatchedSequence match = simpleSkills.stream()
                .map(entry -> PatternMatcher.compile(entry.getKey(), query))
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(MatchedSequence::useRatio))
                .orElse(null);
        // TODO: I feel like there should be an easier way to do this net part
        if (match != null) {
            for (AbstractMap.SimpleEntry<String, String> entry2: simpleSkills) {
                if (entry2.getKey().equals(match.getPattern())) {
                    return entry2.getValue();
                }
            }
        }
        return notFound;

    }

    @Override
    public void notifyNewPath(String newPath) {
        this.path = newPath;
        readPath();
    }

    private void readPath() {
        try {
            File txtFile = new File(path);
            Scanner myReader = new Scanner(txtFile);
            String line;
            while (myReader.hasNextLine()) {
                line = myReader.nextLine();
                String[] parts = line.split(";");
                if (parts.length == 1) {
                    // problem with the txt file, should always be at least a pattern and a response
                }
                else if (parts.length == 2) {
                    simpleSkills.add(new AbstractMap.SimpleEntry<>(parts[0], parts[1]));
                }
                else {
                    //TODO more complex queries with parameters with custom responses
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("The file could not be found");
            // Probably ask the user to enter a new path?
        }
    }

    // Mainly for testing
    public List<AbstractMap.SimpleEntry<String, String>> getSimpleSkills() {
        return simpleSkills;
    }

}
