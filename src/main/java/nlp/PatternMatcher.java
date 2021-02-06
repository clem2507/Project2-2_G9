package nlp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PatternMatcher {

    // Method that matches parts of the query with parts of the pattern
    // Returns list of strings where first part is the part of the pattern and the second is the part of the query that matches it
    public static Map<Set<String>, String> patternMatch(String pattern, String query) {
        Map<Set<String>, String> matchList = new HashMap<Set<String>, String>();
        List<Set<String>> patterns = Pattern.parse(pattern);

        String matchedString = null;
        for (Set p : patterns) {
            for (Object s : p) {
                if (query.contains((String) s)) {
                    matchedString = (String) s;
                }
                // TODO: param patterns and 'empty' patterns (<...> or <#:3>)
            }
            matchList.put(p, matchedString);
            matchedString = null;
        }


        return matchList;
    }

    public static void main(String[] args) {
        Map<Set<String>, String> strings = patternMatch("<test, word><this><thing>", "test this thing");
        System.out.println(strings);
    }

}
