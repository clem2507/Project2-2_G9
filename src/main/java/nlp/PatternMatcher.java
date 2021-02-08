
package nlp;

import java.util.*;


public class PatternMatcher {

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

    public static Map<Set<String>, List<String>> patternMatch(String pattern, String query) {
        Map<Set<String>, List<String>> matchList = new HashMap<Set<String>, List<String>>();
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
                        if(query.contains((String) s)){
                            int indexOf = query.indexOf((String) s) + objLength;
                            matchedString = Tokenizer.asTokenList((String) s);
                            System.out.println("removed:   "+queryBuffer.substring(0 ,indexOf));
                            queryBuffer.replace( 0 ,indexOf ,"");
                            String newQ = queryBuffer.toString().trim();
                            queryBuffer = new StringBuffer(newQ);
                            break;
                        }
                    }else{
                        if(temp.contains((String) s)){

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

                // DATE SLOT
                }else if(Pattern.getContentType((String) s).equals(Pattern.ContentType.PARAMETER_DATE)) {

                }
            }
            System.out.println("Query Left  : "+queryBuffer);

            matchList.put(p, matchedString);
            matchedString = null;
        }


        if(queryBuffer.length()!=0){
            for(Set p: patterns){
                if(p.contains("...")){
                    matchedString = Tokenizer.asTokenList((queryBuffer.toString()));
                    matchList.put(p, matchedString);
                    queryBuffer.replace(0,queryBuffer.length(),"");
                }
            }
        }

        System.out.println("Query Left  : "+queryBuffer);
        return matchList;
    }

    public static void main(String[] args) {
        String pattern = "<remind me, set reminder> <param:int> <on, in> ";
        String query = "set reminder 5 to do groceries on";
        Map<Set<String>, List<String>> strings = patternMatch(pattern , query);
        System.out.println(strings);

    }

}
