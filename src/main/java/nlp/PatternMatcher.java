
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

    public static List<AbstractMap.SimpleEntry<Set<String>, List<String>>> patternMatch(String pattern, String query) {
        List<AbstractMap.SimpleEntry<Set<String>, List<String>>> matchList = new ArrayList<AbstractMap.SimpleEntry<Set<String>, List<String>>>();
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

            matchList.add(new AbstractMap.SimpleEntry<Set<String>, List<String>>(p, matchedString));
            matchedString = null;
        }


        if(queryBuffer.length()!=0){
            for(Set p: patterns){
                if(p.contains("...")){
                    matchedString = Tokenizer.asTokenList((queryBuffer.toString()));
                    matchList.add(new AbstractMap.SimpleEntry<Set<String>, List<String>>(p, matchedString));
                    queryBuffer.replace(0,queryBuffer.length(),"");
                }
            }
        }

        System.out.println("Query Left  : "+queryBuffer);
        return matchList;
    }

    public static void main(String[] args) {
        String pattern = "<weather> <in> <...>";
        String query = "What is the weather in maastricht";
        List<AbstractMap.SimpleEntry<Set<String>, List<String>>> list = patternMatch(pattern , query);
        System.out.println("main");
        for (AbstractMap.SimpleEntry a : list) {
            System.out.println((a));
        }
    }

}
