package nlp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PatternMatcher {

    // Method that matches parts of the query with parts of the pattern
    // Returns map where each pair consists of the pattern slot and the tokens from the query that match it
    public static Map<Set<String>, List<String>> patternMatch(String pattern, String query) {
        Map<Set<String>, List<String>> matchList = new HashMap<Set<String>, List<String>>();
        List<Set<String>> patterns = Pattern.parse(pattern);
        StringBuffer queryBuffer = new StringBuffer(query);

        List<String> matchedString = null;
        int strIndex = 0;
        Pattern.ContentType currentType;
        for (Set p : patterns) {
            for (Object s : p) {
                // Since strings will most likely have multiple strings, worked with this
                if (Pattern.getContentType((String) s).equals(Pattern.ContentType.STRING)) {
                    //get the length of the object
                    int objLength = ((String) s).length();
                    System.out.println(s);
                    //get substring of the query starting from 0 until length of obj
                    String substring = queryBuffer.substring(0, objLength);
                    System.out.println(substring);
                    if(s.equals(substring)){
                        matchedString = Tokenizer.asTokenList((String) s);
                        queryBuffer.replace( 0 ,objLength ,"");
                        String newQ = queryBuffer.toString().trim();
                        queryBuffer = new StringBuffer(newQ);
                        break;
                    }
                }
                else {
                    List<String> tokens = Tokenizer.asTokenList(queryBuffer.toString());
                    // TODO: should remove token from query
                    // Also doesn't work with multi-string parameters
                    for (String token : tokens) {
                        if(Pattern.match((String) s, token)) {
                            matchedString.add(token);
                            break;
                        }
                    }
                }
                // TODO: 'empty' patterns (<...> or <#:3>)
                // TODO: if pattern is matched, move on to next pattern
                // (e.g. one pattern might be <in, at>. In gets matched, shouldn't look for "at" later in the sentence)
            }
            matchList.put(p, matchedString);
            matchedString = null;
        }
        return matchList;
    }

    public static void main(String[] args) {
        Map<Set<String>, List<String>> strings = patternMatch("<remind me to, remind me> <on ,in>", "in remind me to");
        System.out.println(strings);
    }

}


//                    if (query.contains((String) s)) {
//                        matchedString = Tokenizer.asTokenList((String) s);
//                        strIndex = query.indexOf((String) s);
//                        // Removing matched part from the query
//                        //query = query.substring(strIndex + ((String) s).length(), query.length());
//                    }
