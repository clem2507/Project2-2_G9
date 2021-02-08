package nlp;

import java.util.*;
import java.util.stream.Collectors;



public class Pattern {

    public enum SlotType{
        SOLID, BLANK, NONE
    }

    public enum ContentType{
        STRING, PARAMETER_INT, PARAMETER_DAY, WHITESPACE
    }

    private static List<String> breakPattern(String pattern){
        List<String> tokens = Arrays.asList(pattern.split("\\s+"));
        return tokens.stream()
                .flatMap(t -> Tokenizer.splitOn(t, "<"))
                .flatMap(t -> Tokenizer.splitOn(t, ">"))
                .flatMap(t -> Tokenizer.splitOn(t, ","))
                .collect(Collectors.toList());
    }

    private static List<String> groupByContent(List<String> pattern) throws NLPError {
        List<String> contents = new ArrayList<>();
        String accumulator = "";
        int openSlots = 0; // This is to keep track of misplaced '<' and '>'

        for(String t : pattern){

            if(Arrays.asList(new String[]{"<", ",", ">"}).contains(t)){

                if(t.equals("<")){
                    openSlots++;
                }

                else if(t.equals(">")){
                    openSlots--;
                }

                if(!accumulator.isEmpty()){
                    contents.add(accumulator.trim().toLowerCase());
                    accumulator = "";
                }

                contents.add(t);
            }

            else{
                accumulator += t + " ";
            }

        }

        if(openSlots < 0){
            throw new NLPError("Missing '<'");
        }

        else if(openSlots > 0){
            throw new NLPError("Missing '>'");
        }

        return contents;
    }

    private static List<Set<String>> groupInSlots(List<String> pattern) throws NLPError {
        List<Set<String>> slots = new ArrayList<>();
        Set<String> slot = null;

        for(String t : pattern){

            if(t.equals("<")){

                if(slot != null) {
                    throw new NLPError("Misplaced '<'");
                }

                slot = new HashSet<>();
            }

            else if(t.equals(">")){

                if(slot == null) {
                    throw new NLPError("Misplaced '>'");
                }

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

    public static List<Set<String>> parse(String pattern) throws NLPError {
        return groupInSlots(groupByContent(breakPattern(pattern)));
    }

    public static ContentType getContentType(String content){

        if(content.equals("param:int"))
            return ContentType.PARAMETER_INT;

        if(content.equals("param:day"))
            return ContentType.PARAMETER_DAY;

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

    public static int getBlankSlotParameter(Set<String> slot) throws NLPError {

        if(!getSlotType(slot).equals(SlotType.BLANK)){
            throw new NLPError("Illegal operation on solid slot, solid slots contain no parameters");
        }

        String content = slot.stream().findFirst().orElseThrow();

        if(content.equals("..."))
            return -1;

        return Integer.parseInt(content.substring(2));
    }

    public static int getContentLength(String content){

        if(getContentType(content).equals(ContentType.STRING)){
            return Tokenizer.asTokenList(content).size();
        }

        if(getContentType(content).equals(ContentType.PARAMETER_INT)){
            return 1;
        }

        if(getContentType(content).equals(ContentType.PARAMETER_DAY)){
            return 1;
        }

        return -1;
    }

}
