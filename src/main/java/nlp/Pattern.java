package nlp;

import java.util.*;
import java.util.stream.Collectors;

public class Pattern {

    private static List<String> breakPattern(String pattern){
        List<String> tokens = Arrays.asList(pattern.split("\\s+"));
        return tokens.stream()
                .flatMap(t -> Tokenizer.splitOn(t, "<"))
                .flatMap(t -> Tokenizer.splitOn(t, ">"))
                .flatMap(t -> Tokenizer.splitOn(t, ","))
                .collect(Collectors.toList());
    }

    private static List<String> groupByContent(List<String> pattern){
        List<String> contents = new ArrayList<>();
        String accumulator = "";

        for(String t : pattern){

            if(Arrays.asList(new String[]{"<", ",", ">"}).contains(t)){

                if(!accumulator.isEmpty()){
                    contents.add(accumulator.trim());
                    accumulator = "";
                }

                contents.add(t);
            }

            else{
                accumulator += t + " ";
            }

        }

        return contents;
    }

    private static List<Set<String>> groupInSlots(List<String> pattern){
        List<Set<String>> slots = new ArrayList<>();
        Set<String> slot = null;

        for(String t : pattern){

            if(t.equals("<")){
                assert slot == null:"Misplaced '<'";
                slot = new HashSet<>();
            }

            else if(t.equals(">")){
                assert slot != null:"Misplaced '>'";
                slots.add(slot);
                slot = null;
            }

            else if(!t.equals(",")){
                slot.add(t);
            }

        }

        return slots;
    }

    public static List<Set<String>> parse(String pattern){
        return groupInSlots(groupByContent(breakPattern(pattern)));
    }

    public static boolean isParameter(String slotContent){
        return false;
    }

    public static boolean isValidInt(String str){
        return str.matches("^[+-]?\\d+$");
    }

    public static boolean isValidDay(String str){
        return Arrays.asList(new String[]{
                "monday",
                "tuesday",
                "wednesday",
                "thursday",
                "friday",
                "saturday",
                "sunday"
        }).contains(str.toLowerCase());
    }

    public static boolean isValidDate(String str){
        return str.matches("^((?:19|20)[0-9][0-9])-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$");
    }

}
