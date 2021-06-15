package nlp;

import java.util.*;

public class sentenceGenerator {


    private List<String> parseRules;
    private int max;
    ArrayList<String> roots = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();
    Map<String,List<List<String>>> keyToBody = new HashMap<String, List<List<String>>>();
    ArrayList<List<String>> generatedSentences = new ArrayList<>();
    List<String> generatedSentAsString = new ArrayList<>();


    public sentenceGenerator(List<String> parseRules, int maxSentences) {
        this.max = maxSentences;
        this.parseRules = parseRules;
        getRoots();
        System.out.println("roots = " +roots);
        System.out.println("keys = " +keys);
        System.out.println(keyToBody);

    }


    public void getRoots(){
        for (String rule : parseRules) {

            String [] words = rule.split(" ");
            List<String> wordsList = Arrays.asList(words).subList(3,words.length);
            List<List<String>> valueList =  new ArrayList<>();

            if (keyToBody.containsKey(words[1])){
                List<List<String>> curr =  keyToBody.get(words[1]);
                curr.add(wordsList);
                keyToBody.put(words[1],curr);
            }else{
                valueList.add(wordsList);
                keyToBody.put(words[1],valueList);
            }
            if (!roots.contains(words[1])){
                roots.add(words[1]);
            }
        }

        ArrayList<String> temp = new ArrayList<>(roots);
        ArrayList<String> keysList = new ArrayList<>(roots);
        this.keys = keysList;

        Iterator it = keyToBody.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            List<List<String>> bodyList = keyToBody.get(pair.getKey());

            for (String root : roots){
                if (!root.equals(pair.getKey())){
                    for (List<String> list : bodyList ){
                        if (list.contains(root)) {
                            temp.remove(root);
                        }


                    }
                }
            }
        }
        this.roots = temp;
    }


    public List<String> generateSentences(){
        for (String root : roots){
            List<List<String>> currChildren = keyToBody.get(root);
            for (List<String> child : currChildren){
                for (String key: keys){
                    if (child.contains(key)){
                        ArrayList<List<String>> genStrings = replaceKey(child, key);
                        for(List<String> x : genStrings){
                            if (!generatedSentences.contains(x)){
                                generatedSentences.add(x);
                            }
                        }
                    }
                }
            }

        }
        List<String> genSent = new ArrayList<>();
        for (List<String> sent : generatedSentences){
            String tempSent = "";
            for (String word: sent){
                tempSent = tempSent+ word+ " ";
            }
            genSent.add(tempSent);
        }
        generatedSentAsString = genSent;
        return genSent;
    }

    /**
     * function that recursively replaces keys found in a sentence and returns all possible valid sentences
     * @param child ; sentence
     * @param key
     * @return
     */
    public ArrayList<List<String>> replaceKey(List<String> child, String key){
        ArrayList<List<String>> generatedSentencess = new ArrayList<>();

        //Find the position of the key
        //find the possible children of the key
        int index = child.indexOf(key);
        List<List<String>> possibleBody = keyToBody.get(key);

        for (List<String> body : possibleBody){
            List<String> genString = possibleChild(body,child,index);
            //check if it contains any key and still less than the max
            int count = 0;
            for (String k : keys){
                if (genString.contains(k) && genString.size()<=max){
                    ArrayList<List<String>> xx =  replaceKey(genString, k);
                    for (List<String> sent : xx){
                        if (!generatedSentencess.contains(sent)){
                            generatedSentencess.add(sent);
                        }
                    }
                }else if( genString.size()<=max) {
                    if (count == keys.size()-1 ){
                        if (containsKey(genString)){
                            //find which key
                            //if this key has any body without any keys in
                            for (String x : keys){
                                if(genString.contains(x)){
                                    List<List<String>> bodies = keyToBody.get(x);
                                    List<List<String>> keylessBods = new ArrayList<>();
                                    for (List<String> b : bodies){
                                        if(!containsKey(b)){
                                            keylessBods.add(b);
                                        }
                                    }
                                    if (keylessBods.size()>0){
                                        //then create function for this final replacement
                                        ArrayList<List<String>> xx = finalRep(genString, x, keylessBods);
                                        for(List<String> str: xx){
                                            if(!containsKey(str)){
                                                generatedSentencess.add(str);

                                            }
                                        }
                                    }
                                }
                            }
                        }else{
                            generatedSentencess.add(genString);
                        }

                    }
                }
                count += 1;
            }

        }
        return generatedSentencess;
    }


    /**
     * This method replaces keys with bodies that do not contain keys
     * @param child ; a sentence that contains keys
     * @param key ; the key found in a sentence
     * @param keylessBods
     * @return leaf node generated sentences
     */
    public ArrayList<List<String>> finalRep(List<String> child, String key, List<List<String>> keylessBods){
        ArrayList<List<String>> generatedSentencess = new ArrayList<>();
        int index  = child.indexOf(key);
        for (List<String> bod : keylessBods){
            List<String> x = possibleChild(bod,child,index);
            if(x.contains(key)){
                ArrayList<List<String>> xx =  finalRep(x, key, keylessBods);
                for (List<String> sent : xx){
                    generatedSentencess.add(sent);
                }
            }else{
                generatedSentencess.add(x);
            }
        }
        return generatedSentencess;
    }

    public List<String> possibleChild(List<String> body, List<String> child, int index) {
        List<String> temp = new ArrayList<>(child);

        temp.remove(index);

        for (String word : body){
            temp.add(index, word);
            index+=1;
        }
        return temp;
    }

    /**
     * @param x ;  a string that has to be checked if a key exists in it
     * @return boolean in which true is returned if a key is found anf false otherwise
     */
    public boolean containsKey(List<String> x){
        for (String key : keys){
            if (x.contains(key)){
                return true;
            }
        }
        return false;
    }


    public static void main (String[] args){

        List<String> parseRules = new ArrayList<>();
        parseRules.add("parse E as exams");
        parseRules.add("parse E as lectures");
        parseRules.add("parse S as When do I have E");
        parseRules.add("parse S as S and E");
        parseRules.add("parse X as S and training");

        sentenceGenerator gg = new sentenceGenerator(parseRules, 9);

        List<String> x = gg.generateSentences();
        System.out.println(x);
        System.out.println(x.size());





    }
}
