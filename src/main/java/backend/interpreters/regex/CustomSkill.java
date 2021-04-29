package backend.interpreters.regex;

import nlp.MatchedSequence;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class CustomSkill {

    private final String pattern;
    private final String response;
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
        String[] tuples = strippedParameter.split("[)],[(]");
        String[] tempPair;
        for (String tuple : tuples) {
            tempPair = tuple.split(",");
            pairs.add(new AbstractMap.SimpleEntry<>(tempPair[0].toLowerCase(), tempPair[1]));
        }
        return pairs;
    }

    /** Fills out the blank spaces corresponding to parameters in the query
     *
     * @param match Mathced sequence of pattern
     * @return response with parameter value
     */
    public String getParametricResponse(MatchedSequence match) {
        String newResponse = response;
        // Find parameter value in the matched sequence by its index
        if (match.getSlotIndex("@1").isPresent()) {
            int index = match.getSlotIndex("@1").get();
            List<String> tokens = match.getMatchedTokensAt(index);
            StringBuilder builder = new StringBuilder();
            for (String s : tokens) {
                builder.append(s);
            }
            String parameterValue = builder.toString().toLowerCase();
            String parameterString = "";
            for (AbstractMap.SimpleEntry<String, String> e : parameters) {
                if (e.getKey().equals(parameterValue)) {
                    parameterString = e.getValue();
                    break;
                }
                /*if (e.getKey().equals("default")) {
                    parameterString = e.getValue();
                }*/
            }
            String placeholder = "@1";
            newResponse = newResponse.replace(placeholder, parameterString);
            return newResponse;
        }
        else {
            // In this case, there was no matching value for the parameter
            return null;
        }
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
