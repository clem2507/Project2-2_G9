
package nlp;

import java.util.*;


public class PatternMatcher {

    /**
     Method that matches parts of the query with parts of the pattern
     @param: pattern from the domain
     @param: query input from user
     @return: map where each pair consists of the pattern slot and the tokens from the query that match it
     */

    /*
    TODO:
     - Complete 'empty' patterns (<...>),
     - Date Pattern,
     - Change data type of output returned,
     - Inspect ordering of sets (weird) [check longer strings first]
     */

    public static Map<Set<String>, List<String>> patternMatch(String pattern, String query) {
        Map<Set<String>, List<String>> matchList = new HashMap<Set<String>, List<String>>();
        List<Set<String>> patterns = Pattern.parse(pattern);
        StringBuffer queryBuffer = new StringBuffer(query.trim());

        List<String> matchedString = null;

        for (Set p : patterns) {
            for (Object s : p) {

                System.out.println(s);
                // STRING SLOT
                if (Pattern.getContentType((String) s).equals(Pattern.ContentType.STRING)) {

                    int objLength = ((String) s).length();
                    String substring = queryBuffer.substring(0, objLength);


                    if(patterns.indexOf(p)==0){
                        if(query.contains((String) s)){
                            int indexOf = query.indexOf((String) s) + objLength;
                            matchedString = Tokenizer.asTokenList((String) s);
                            queryBuffer.replace( 0 ,indexOf ,"");
                            String newQ = queryBuffer.toString().trim();
                            queryBuffer = new StringBuffer(newQ);
                            break;
                        }
                    }else{
                        if(s.equals(substring)){
                            matchedString = Tokenizer.asTokenList((String) s);
                            queryBuffer.replace( 0 ,objLength ,"");
                            String newQ = queryBuffer.toString().trim();
                            queryBuffer = new StringBuffer(newQ);
                            break;
                        }
                    }

                // INTEGER SLOT
                } else if(Pattern.getContentType((String) s).equals(Pattern.ContentType.PARAMETER_INT)){

                    int lengthOfInt = 0 ;

                    if(patterns.indexOf(p)==0){
                        int intStartIndex = 0;
                        for(int x = 0; x < query.length(); x++){
                            if(Character.isDigit(query.charAt(x))){
                                intStartIndex = x;
                                break;
                            }
                        }
                        for (int x = intStartIndex ; x < queryBuffer.length(); x++) {
                            if (!Pattern.isValidInt(Character.toString(queryBuffer.charAt(x)))) {
                                break;
                            }
                            lengthOfInt = x + 1;
                        }
                    }else{
                        for (int x = 0 ; x < queryBuffer.length(); x++){
                            if(!Pattern.isValidInt(Character.toString(queryBuffer.charAt(x)))){
                                break;
                            }
                            lengthOfInt = x+1;
                        }
                    }

                    int objLength = lengthOfInt;
                    if(objLength!=0){
                        matchedString = Tokenizer.asTokenList((String) s);
                        queryBuffer.replace( 0 ,objLength ,"");
                        String newQ = queryBuffer.toString().trim();
                        queryBuffer = new StringBuffer(newQ);
                        break;
                    }

                // DAY SLOT
                } else if(Pattern.getContentType((String) s).equals(Pattern.ContentType.PARAMETER_DAY)) {
                    String day = "";
                    for(String word: query.split(" ")){
                        if(Pattern.isValidDay(word)){
                            day = word;
                        }
                    }
                    int lengthOfString = day.length();
                    int indexOfString = query.indexOf(day) + lengthOfString;

                    if(!day.equals("")){
                        if (patterns.indexOf(p) == 0) {
                            matchedString = Tokenizer.asTokenList((String) s);
                            queryBuffer.replace( 0 ,indexOfString ,"");
                            String newQ = queryBuffer.toString().trim();
                            queryBuffer = new StringBuffer(newQ);
                            break;
                        }else{
                            matchedString = Tokenizer.asTokenList((String) s);
                            queryBuffer.replace( 0 ,lengthOfString ,"");
                            String newQ = queryBuffer.toString().trim();
                            queryBuffer = new StringBuffer(newQ);
                            break;
                        }
                    }
                }else if(Pattern.getContentType((String) s).equals(Pattern.ContentType.WHITESPACE)) {
                    //get the next set, find it in query, remove anything until that set is met
                    //if not met, remove until end of the sentence =)
                    int indexOfNextSet = patterns.indexOf(p);
                    Set nextSet = patterns.get(indexOfNextSet);
                    int lengthToBeReplaced = 0;
                    for (Object x : nextSet) {

                    }
                }else if(Pattern.getContentType((String) s).equals(Pattern.ContentType.WHITESPACE)) {

                }
            }
            matchList.put(p, matchedString);
            matchedString = null;
        }
        return matchList;
    }

    public static void main(String[] args) {
        Map<Set<String>, List<String>> strings = patternMatch("<remind me in, remind me> <param:day> <in, on> ", "remind me tuesday on");
        System.out.println(strings);
    }

}
//                else {
//                    List<String> tokens = Tokenizer.asTokenList(queryBuffer.toString());
//                    // Also doesn't work with multi-string parameters
//                    for (String token : tokens) {
//                        if(Pattern.match((String) s, token)) {
//                            matchedString.add(token);
//                            break;
//                        }
//                    }
//                }

//                    if (query.contains((String) s)) {
//                        matchedString = Tokenizer.asTokenList((String) s);
//                        strIndex = query.indexOf((String) s);
//                        // Removing matched part from the query
//                        //query = query.substring(strIndex + ((String) s).length(), query.length());
//                    }