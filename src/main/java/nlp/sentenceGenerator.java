package nlp;

import nlp.cfg.parsing.LiteralSymbol;
import nlp.cfg.parsing.NumericSymbol;
import nlp.cfg.parsing.ProductionRule;
import nlp.cfg.parsing.Symbol;

import java.util.*;

public class sentenceGenerator {


    private List<ProductionRule> parseRules;
    private int max;
    ArrayList<String> roots = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();
    Map<String,List<List<String>>> keyToBody = new HashMap<String, List<List<String>>>();
    ArrayList<List<String>> generatedSentences = new ArrayList<>();
    List<String> generatedSentAsString = new ArrayList<>();



    public sentenceGenerator(List<ProductionRule> parseRules, int max) {
        this.max = max;
        this.parseRules = parseRules;
        getRoots();
        System.out.println("roots = " +roots);
        System.out.println("keys = " +keys);
        System.out.println(keyToBody);

    }


    /**
     * function to extract the keys, bodies, and roots.
     * also a dictionary with keys as 'keys' and values as 'bodies' is created here
     */
    public void getRoots(){

        for (ProductionRule rule: parseRules){
            String key = rule.getNonTerminal().toString();
            List <Symbol> body = rule.getProduction();
            List <String> bodys = new ArrayList<>();
            List<List<String>> valueList =  new ArrayList<>();

            for (Symbol r: body){
                bodys.add(r.toString());
            }

            if (!this.keys.contains(key)){
                keys.add(key);
            }
            if (!roots.contains(key)){
                roots.add(key);
            }

            if(keyToBody.containsKey(key)){
                List<List<String>> curr =  keyToBody.get(key);
                curr.add(bodys);
                keyToBody.put(key,curr);
            }else{
                valueList.add(bodys);
                keyToBody.put(key,valueList);
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


    /**
     * This is the main function that generates sentences
     * @return generated sentences
     */
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

        int index = child.indexOf(key);
        List<List<String>> possibleBody = keyToBody.get(key);

        for (List<String> body : possibleBody){
            List<String> genString = possibleChild(body,child,index);
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


    /**
     * This function returns a sentence generated after replacement of a symbol
     * @param body ;  the body to be added
     * @param child ; text to be changed
     * @param index ;  index of the symbol to be removed
     * @return
     */
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


//        List<String> parseRules = new ArrayList<>();
//        parseRules.add("parse E as exams");
//        parseRules.add("parse E as lectures");
//        parseRules.add("parse S as When do I have E");
//        parseRules.add("parse S as S and E");
//        parseRules.add("parse X as S and training");

        /**
         *         These rules are parsed below
         */
        List<ProductionRule> rules = new ArrayList<>();
        Symbol S = new LiteralSymbol("S");
        Symbol X = new LiteralSymbol("X");
        Symbol E = new LiteralSymbol("E");
        Symbol exams = new LiteralSymbol("exams");
        Symbol lectures = new LiteralSymbol("lectures");
        Symbol when = new LiteralSymbol("when");
        Symbol doo = new LiteralSymbol("do");
        Symbol I = new LiteralSymbol("I");
        Symbol have = new LiteralSymbol("have");
        Symbol and = new LiteralSymbol("and");
        Symbol training = new LiteralSymbol("training");

        rules.add(new ProductionRule(E, Arrays.asList(exams)));
        rules.add(new ProductionRule(E, Arrays.asList(lectures)));
        rules.add(new ProductionRule(S, Arrays.asList(when, doo, I, have, E)));
        rules.add(new ProductionRule(S, Arrays.asList(S, and, E)));
        rules.add(new ProductionRule(X, Arrays.asList(S, and, training)));


        /**
         * Generate sentences: Simply create a function of the class and then call the function
         * 'generateSentences' which will return a
         * list of the generated strings
         */
        sentenceGenerator gg = new sentenceGenerator(rules, 9);
        List<String> x = gg.generateSentences();

        System.out.println(x);
        System.out.println(x.size());
    }
}
