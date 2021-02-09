package nlp;


import java.util.*;
import java.util.List;

public class PatternMatcher {

    public static Map.Entry<Integer, Integer> matchContentWithTokens(String content, List<String> tokens){
        int startsAt = -1;
        int endsAt = -1; // We don't really need to initialize this, but I keep it for consistency with Oracle's
        // Java Code Conventions

        if(Pattern.getContentType(content).equals(Pattern.ContentType.STRING)) { // If the content is of type STRING
            startsAt = Collections.indexOfSubList(
                    tokens,
                    Tokenizer.asTokenList(content)
            ); // The content is expected to be a sublist of tokens, otherwise starts_at = -1
        }

        else{ // Any other type different from STRING is a parameter type (they are all of size 1)

            for (int i = 0; i < tokens.size(); i++) { // Iterate over each token

                // If the content is of type INT or DAY
                if(
                        (Pattern.getContentType(content).equals(Pattern.ContentType.PARAMETER_INT) && Pattern.isValidInt(tokens.get(i)))
                        || (Pattern.getContentType(content).equals(Pattern.ContentType.PARAMETER_DAY) && Pattern.isValidDay(tokens.get(i)))
                ){
                    startsAt = i; // There is a match at i
                    break; // Stop iterating
                }

            }

        }

        endsAt = startsAt + Pattern.getContentLength(content); // Get the length of the matched sublist of tokens
        return startsAt != -1 && endsAt > startsAt? (new AbstractMap.SimpleEntry<>(startsAt, endsAt)):null; // Null if there is no match
    }

    public static Optional<Map.Entry<Integer, Integer>> matchSlotWithTokens(final Set<String> slot, final List<String> tokens){
        return slot.stream()
                .map(c -> matchContentWithTokens(c, tokens))
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(a -> a.getValue() - a.getKey())); // Get longest match in (slot, tokens[matchFrom:~])
    }

    /**
     Method that matches parts of the query with parts of the pattern
     @param pattern from the domain
     @param query input from user
     @return map where each pair consists of the pattern slot and the tokens from the query that match it
     */

    /*
    TODO:
     - Date Pattern,
     - Change data type of output returned,
     - Inspect ordering of sets (weird) [check longer strings first]--> might be better
     */

    public static List<Map.Entry<Set<String>, List<String>>> patternMatch(String pattern, String query) throws NLPError {
        List<Map.Entry<Set<String>, List<String>>> matchList = new ArrayList<>();
        List<Set<String>> patterns = Pattern.parse(pattern);
        StringBuffer queryBuffer = new StringBuffer(query.trim());

        List<String> matchedString = null;

        for (Set p : patterns) {
            for (Object s : p) {

                System.out.println("being checked-->" + s);
                // STRING SLOT
                if (Pattern.getContentType((String) s).equals(Pattern.ContentType.STRING)) {

                    int objLength = ((String) s).length();
                    String substring = "";
                    if(queryBuffer.length() >= objLength){
                        substring = queryBuffer.substring(0, objLength);

                    }
                    String temp = queryBuffer.toString();



                    if(patterns.indexOf(p)==0){
                        if(query.toLowerCase().contains(((String) s).toLowerCase())){
                            int indexOf = query.indexOf((String) s) + objLength;
                            matchedString = Tokenizer.asTokenList((String) s);
                            System.out.println("removed:   "+queryBuffer.substring(0 ,indexOf));
                            queryBuffer.replace( 0 ,indexOf ,"");
                            String newQ = queryBuffer.toString().trim();
                            queryBuffer = new StringBuffer(newQ);
                            break;
                        }
                    }else{
                        if(temp.toLowerCase().contains(((String) s).toLowerCase())){

//                            if(patterns.indexOf(p) == 1){
//                                objLength = queryBuffer.length();
//                                System.out.println((objLength));
//                            }
                            matchedString = Tokenizer.asTokenList((String) s);
                            System.out.println("removed:   "+queryBuffer.substring(temp.indexOf((String) s) ,objLength+temp.indexOf((String) s)));
                            queryBuffer.replace(temp.indexOf((String) s) ,objLength+temp.indexOf((String) s) ,"");
                            String newQ = queryBuffer.toString().trim();
                            queryBuffer = new StringBuffer(newQ);
                            break;
                        }
                    }

                    // INTEGER SLOT
                } else if(Pattern.getContentType((String) s).equals(Pattern.ContentType.PARAMETER_INT)){

                    int lengthOfInt = 0 ;
                    int intStartIndex = 0;
                    if(patterns.indexOf(p)==0){

                        for(int x = 0; x < queryBuffer.length(); x++){
                            if(Character.isDigit(queryBuffer.charAt(x))){
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
                        for(int x = 0; x < queryBuffer.length(); x++){
                            if(Character.isDigit(queryBuffer.charAt(x))){
                                intStartIndex = x;
                                break;
                            }
                        }
                        StringBuilder sb = new StringBuilder();
                        for(char c : queryBuffer.toString().toCharArray()) {
                            if (Character.isDigit(c)) {
                                sb.append(c);
                                lengthOfInt++;
                            }
                        }
                    }

                    int objLength = lengthOfInt+intStartIndex;
                    if(objLength!=0){
                        if(patterns.indexOf(p) == patterns.size()){
                            objLength = queryBuffer.length();
                        }
                        matchedString = Tokenizer.asTokenList((String) s);
                        System.out.println("removed:   "+queryBuffer.substring(intStartIndex ,objLength));
                        queryBuffer.replace( intStartIndex ,objLength ,"");
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
                            System.out.println("removed:   "+queryBuffer.substring(0 ,indexOfString));
                            queryBuffer.replace( 0 ,indexOfString ,"");
                            String newQ = queryBuffer.toString().trim();
                            queryBuffer = new StringBuffer(newQ);
                            break;
                        }else{

                            if(patterns.indexOf(p) == patterns.size()-1){
                                lengthOfString = queryBuffer.length();
                            }

                            matchedString = Tokenizer.asTokenList((String) s);
                            String temp = queryBuffer.toString();
                            System.out.println("removed:   "+queryBuffer.substring(temp.indexOf(day) ,lengthOfString));
                            queryBuffer.replace(temp.indexOf(day) ,lengthOfString ," ");
                            String newQ = queryBuffer.toString().trim();
                            queryBuffer = new StringBuffer(newQ);

                            break;
                        }
                    }
                }
            }
            System.out.println("Query Left  : "+queryBuffer);

            Map.Entry<Set<String>, List<String>> pair = new AbstractMap.SimpleEntry<>((Set<String>)p, matchedString);
            matchList.add(pair);
            matchedString = null;
        }


        if(queryBuffer.length()!=0){
            for(Set p: patterns){
                if(p.contains("...")){
                    matchedString = Tokenizer.asTokenList((queryBuffer.toString()));
                    Map.Entry<Set<String>, List<String>> pair = new AbstractMap.SimpleEntry<>((Set<String>)p, matchedString);
                    matchList.add(pair);
                    queryBuffer.replace(0,queryBuffer.length(),"");
                }
            }
        }

        System.out.println("Query Left  : "+queryBuffer);
        return matchList;
    }

    public static MatchedSequence compile(String pattern, String query){

        try {
            // First we get the matched sequence (i.e. pairs of (slot, tokens)
            List<Map.Entry<Set<String>, List<String>>> sequence = patternMatch(pattern, query);

            // Since the matcher returns null when there is no match, we check for that
            if(sequence != null){
                // We translate the return type of the pattern matcher to our comfortable MatchedSequence
                MatchedSequence matchedSequence = new MatchedSequence(Tokenizer.asTokenList(query));
                matchedSequence.addAll(sequence);
                // NOTE: This part of the code does not follow Oracle's Java Coding Conventions standards, since
                // MatchedSequence is not entirely defined in the constructor. This will change in future updates
                return matchedSequence;
            }

            return null; // If no match, then null
        }

        // Since the matcher will throw a NLPError in case of a problem while parsing, we have to account for this
        catch (NLPError e){
            System.err.println(e.toString() + " in '" + pattern + "'");
            return null;
        }

    }

    public static void main(String[] args){
        String p = "<hello> <world>";
        String q = "asfsdf hello world sdfsdf";
        try {
            System.out.println(patternMatch(p, q));
        } catch (NLPError nlpError) {
            nlpError.printStackTrace();
        }
    }

}
