package nlp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PatternMatcher {

    // Method that matches parts of the query with parts of the pattern
    // Returns list of strings where first part is the part of the pattern and the second is the part of the query that matches it
    public static List<Set<String>> patternMatch2(String pattern, String query) {
        List<Set<String>> matchList = new ArrayList<>();
        Set<String> match = new HashSet<>();
        List<Set<String>> patterns = Pattern.parse(pattern);

        String completeSlot = "";
        for (Set p : patterns) {
            completeSlot = "<";
            for (Object s : p) {
                completeSlot += (String) s + ", ";
                // check whether part of query matches the string in the pattern.
            }
            completeSlot = completeSlot.substring(0, completeSlot.length() - 2);
            completeSlot += ">";
            match.add(completeSlot);
            matchList.add(match);
            match = new HashSet<>();
        }

        // For testing
        /*for (Set e: matchList) {
            for (Object o : e) {
                System.out.println(o);
            }
        }*/

        return matchList;
    }

    public static void main(String[] args) {
        patternMatch2("<test, the words><param:day>", "hello");
    }

}
