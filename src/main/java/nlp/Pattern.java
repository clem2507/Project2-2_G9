package nlp;

import java.util.*;
import java.util.stream.Collectors;

public class Pattern {

    public static List<String> breakPattern(String pattern){
        List<String> tokens = Arrays.asList(pattern.split("\\s+"));
        return tokens.stream()
                .flatMap(t -> Tokenizer.splitOn(t, "<"))
                .flatMap(t -> Tokenizer.splitOn(t, ">"))
                .flatMap(t -> Tokenizer.splitOn(t, ","))
                .collect(Collectors.toList());
    }

    public static List<String> groupByContent(List<String> pattern){
        List<String> out = new ArrayList<>();
        String accumulator = "";

        for(String t : pattern){

            if(Arrays.asList(new String[]{"<", ",", ">"}).contains(t)){

                if(!accumulator.isEmpty()){
                    out.add(accumulator.trim());
                    accumulator = "";
                }

                out.add(t);
            }

            else{
                accumulator += t + " ";
            }

        }

        return out;
    }

    public static List<Set<String>> groupInSlots(List<String> pattern){
        List<Set<String>> out = new ArrayList<>();
        Set<String> slot = null;

        for(String t : pattern){

            if(t.equals("<")){
                assert slot == null:"Misplaced '<'";
                slot = new HashSet<>();
            }

            else if(t.equals(">")){
                assert slot != null:"Misplaced '>'";
                out.add(slot);
                slot = null;
            }

            else if(!t.equals(",")){
                slot.add(t);
            }

        }

        return out;
    }

    public static List<Set<String>> parsePattern(String pattern){
        return groupInSlots(groupByContent(breakPattern(pattern)));
    }

}
