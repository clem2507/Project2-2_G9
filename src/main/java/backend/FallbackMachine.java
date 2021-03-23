package backend;

import nlp.MatchedSequence;
import nlp.PatternMatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FallbackMachine implements FallbackInterpreter {

    private String path;
    // Could just be combined, not sure
    final private List<CustomSkill> listOfSkills;

    public FallbackMachine() {
        listOfSkills = new ArrayList<>();
    }

    @Override
    // Takes in a query and checks whether it matches any of the custom patterns
    // Returns either the response corresponding to the matched pattern or a default response if there is not matched pattern
    public String processQuery(String query) {
        // Rewrite for custom skills
        if(listOfSkills.size() == 0) {
            return null;
        }
        MatchedSequence match = listOfSkills.stream()
                .map(singleSkill -> PatternMatcher.compile(singleSkill.getPattern(), query))
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(MatchedSequence::useRatio))
                .orElse(null);
        if (match != null) {
            for (CustomSkill skill : listOfSkills) {
                if (match.getPattern().equals(skill.getPattern())) {
                    return skill.getResponse(match);
                }
            }
        }
        return null;

    }

    @Override
    // Gives the location of the text file with custom skills
    public void notifyNewPath(String newPath) {
        this.path = newPath;
        readPath();
    }

    // Parses the text file and puts the info into data structures to be used for matching queries
    private void readPath() {
        try {
            File txtFile = new File(path);
            Scanner myReader = new Scanner(txtFile);
            String line;
            CustomSkill newSkill;
            while (myReader.hasNextLine()) {
                line = myReader.nextLine();
                String[] parts = line.split(";");
                if (parts.length == 1) {
                    // problem with the txt file, should always be at least a pattern and a response
                    // Send message to bot
                }
                else if (parts.length == 2) {
                    newSkill = new CustomSkill(parts[0], parts[1]);
                    listOfSkills.add(newSkill);
                }
                else {
                    newSkill = new CustomSkill(parts[0], parts[1], parts[2]);
                    listOfSkills.add(newSkill);
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("The file could not be found");
            // Probably ask the user to enter a new path?
        }
    }

    // Mainly for testing
    public List<CustomSkill> getSkills() {
        return listOfSkills;
    }


}
