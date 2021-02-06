package nlp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PatternMatcher {

    // Method that matches parts of the query with parts of the pattern
    // Returns list of strings where first part is the part of the pattern and the second is the part of the query that matches it
    public static Map<Set<String>, List<String>> patternMatch(String pattern, String query) {
        Map<Set<String>, List<String>> matchList = new HashMap<Set<String>, List<String>>();
        List<Set<String>> patterns = Pattern.parse(pattern);

        List<String> matchedString = null;
        int strIndex = 0;
        Pattern.ContentType currentType;
        for (Set p : patterns) {
            for (Object s : p) {
                if (Pattern.getContentType((String) s).equals(Pattern.ContentType.STRING)) {
                    if (query.contains((String) s)) {
                        matchedString = Tokenizer.asTokenList((String) s);
                        strIndex = query.indexOf((String) s);
                        // Removing matched part from the query
                        //query = query.substring(strIndex + ((String) s).length(), query.length());
                    }
                }
                else {
                    List<String> tokens = Tokenizer.asTokenList(query);
                    // TODO: should remove token from query
                    for (String token : tokens) {
                        if(Pattern.match((String) s, token)) {
                            matchedString.add(token);
                            break;
                        }
                    }
                }
                // TODO: 'empty' patterns (<...> or <#:3>)
            }
            matchList.put(p, matchedString);
            matchedString = null;
        }


        return matchList;
    }

    public static void main(String[] args) {
        Map<Set<String>, List<String>> strings = patternMatch("<test, word><this thing>", "test test this thing");
        System.out.println(strings);
    }

}
