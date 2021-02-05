package nlp;

import java.util.*;
import java.util.stream.Collectors;



public class Pattern {

    public enum SlotType{
        SOLID, BLANK, NONE
    }

    public enum ContentType{
        STRING, PARAMETER_INT, PARAMETER_DAY, PARAMETER_DATE, WHITESPACE
    }

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

    public static List<Set<String>> parse(String pattern){
        return groupInSlots(groupByContent(breakPattern(pattern)));
    }

    public static ContentType getContentType(String content){

        if(content.equals("param:int"))
            return ContentType.PARAMETER_INT;

        if(content.equals("param:day"))
            return ContentType.PARAMETER_DAY;

        if(content.equals("param:date"))
            return ContentType.PARAMETER_DATE;

        if(content.equals("...") || (content.startsWith("#:") && isValidInt(content.substring(2)))){
            return ContentType.WHITESPACE;
        }

        return ContentType.STRING;
    }

    public static SlotType getSlotType(Set<String> slot){

        if(!slot.isEmpty()) {

            if (slot.size() == 1 && slot.stream().allMatch(c -> getContentType(c).equals(ContentType.WHITESPACE))) {
                return SlotType.BLANK;
            }

            if(slot.stream().noneMatch(c -> getContentType(c).equals(ContentType.WHITESPACE))){
                return SlotType.SOLID;
            }

        }

        return SlotType.NONE;
    }

    public static boolean match(String content, String token){
        ContentType cType = getContentType(content);

        if(cType.equals(ContentType.PARAMETER_INT))
            return isValidInt(token);

        if(cType.equals(ContentType.PARAMETER_DAY))
            return isValidDay(token);

        if(cType.equals(ContentType.PARAMETER_DATE))
            return isValidDate(token);

        if(cType.equals(ContentType.STRING))
            return content.equalsIgnoreCase(token);

        throw new AssertionError("Content type undefined: " + content);
    }

}
