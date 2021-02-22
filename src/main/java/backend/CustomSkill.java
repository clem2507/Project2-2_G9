package backend;

import nlp.MatchedSequence;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class CustomSkill {

    private String pattern;
    private String response;
    private List<AbstractMap.SimpleEntry<String, String>> parameters;

    public CustomSkill(String pattern, String response){
        this.pattern = pattern;
        this.response = response;
    }

    public CustomSkill(String pattern, String response, String parameters){
        this.pattern = pattern;
        this.response = response;
        // Adjust parameter string to a list
        this.parameters = convertToList(parameters);
    }

    // Converts the given string into a mapping of parameter values paired with the string that needs to be inserted into the response
    // Form of string: [(key1, value1),(key2, value2)]
    public List<AbstractMap.SimpleEntry<String, String>> convertToList(String parameter) {
        List<AbstractMap.SimpleEntry<String, String>> pairs = new ArrayList<>();

        // Remove [( and )] from string for easier manipulation
        String strippedParameter = parameter.substring(2);
        strippedParameter = strippedParameter.substring(0, strippedParameter.length() - 2);

        // Split into pair separated by comma
        String[] tuples = parameter.split("[)],[(]");
        String[] tempPair;
        for (int i = 0; i < tuples.length; i++) {
            tempPair = tuples[i].split(",");
            pairs.add(new AbstractMap.SimpleEntry<String, String>(tempPair[0], tempPair[1]));
        }
        return pairs;
    }

    // Fills out the blank spaces corresponding to parameters in the query
    public String getParametricResponse(MatchedSequence match) {
        String newResponse = response;
        //TODO
        return newResponse;
    }

    public String getPattern() {
        return pattern;
    }

    public String getResponse(MatchedSequence match) {
        if (parameters == null) {
            return response;
        }
        else {
            return getParametricResponse(match);
        }
    }

    // This method is supposed to convert the variables of this object to a string which can be put in the text file
    public String convertToText() {
        String complete = pattern + ";" + response;
        // If there are parameters, they need to be added to the string
        if (parameters != null) {
            complete = complete + ";[";
            for (AbstractMap.SimpleEntry<String, String> a : parameters) {
                complete = complete + "(" + a.getKey() + "," + a.getValue() + "),";
            }
            // Remove last comma, add final square bracket
            complete = complete.substring(0, complete.length() - 1) + "]";
        }
        return complete;
    }
}
