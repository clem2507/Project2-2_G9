package nlp;

import java.util.*;
import java.util.stream.Collectors;



public class Pattern {

    public enum SlotType{
        SOLID, BLANK, NONE
    }

    public enum ContentType{
        STRING, PARAMETER_INT, PARAMETER_DAY, WHITESPACE, SLOT_TAG
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
        List<String> contents = new ArrayList<>(); // List of tokens put together as either delimiters or contents
        StringBuilder accumulator = new StringBuilder(); // Current content
        int openSlots = 0; // This is to keep track of misplaced '<' and '>'

        for(String t : pattern){ // Iterate over tokens

            if(Arrays.asList(new String[]{"<", ",", ">"}).contains(t)){ // If the token is a delimiter

                if(t.equals("<")){
                    openSlots++;
                }

                else if(t.equals(">")){
                    openSlots--;
                }

                if(accumulator.length() > 0){ // If there is a content being parsed
                    contents.add(accumulator.toString().trim().toLowerCase()); // Add it to the list
                    accumulator = new StringBuilder(); // Then start over with the remaining set of tokens
                }

                contents.add(t); // Add delimiter to the list
            }

            else{
                // TODO: The accumulator could be replaced with a StringBuilder for better performance
                accumulator.append(t).append(" "); // Accumulate token
            }

        }

        if(openSlots < 0){ // If there are more closing clauses (i.e. '>') than opening clauses (i.e. '<')
            throw new NLPError("Missing '<'"); // Throw NLPError
        }

        else if(openSlots > 0){ // If there are more opening clauses (i.e. '>') than closing clauses (i.e. '<')
            throw new NLPError("Missing '>'"); // Throw NLPError
        }

        return contents; // Return grouped tokens
    }

    private static List<Set<String>> groupInSlots(List<String> pattern) throws NLPError {
        List<Set<String>> slots = new ArrayList<>(); // List of sets (i.e. slots)
        Set<String> slot = null; // Current slot we are working with

        for(String t : pattern){ // Iterate over the tokens

            if(t.equals("<")){ // If the token is an opening clause '<'

                if(slot != null) {
                    throw new NLPError("Misplaced '<'");
                }

                slot = new HashSet<>(); // Create a new working set
            }

            else if(t.equals(">")){ // If the token is a closing clause '>'

                if(slot == null) {
                    throw new NLPError("Misplaced '>'");
                }

                slots.add(slot); // Add the current working set to the list
                slot = null; // Start over for the next slot
            }

            else if(!t.equals(",")){ // If the token is a conjunction clause ','
                assert slot != null;
                slot.add(t); // Att token to the working set
            }

        }

        return slots; // Return list of slots
    }

    private static List<Slot> parseTags(List<Set<String>> pattern) throws NLPError {
        Set<String> usedTags = new HashSet<>(); // Empty set of tags, we will use this to keep track of used tags
        List<Slot> output = new ArrayList<>(pattern.size()); // The mapped pattern sequence

        for(Set<String> slot : pattern){ // For each slot in the pattern
            // Get the contents
            Set<String> contents = slot.stream().filter(c -> !c.startsWith("@")).collect(Collectors.toSet());

            // Get the tags
            Set<String> tags = slot.stream().filter(c -> c.startsWith("@")).collect(Collectors.toSet());

            for(String tag : tags){ // For each tag

                if(usedTags.contains(tag)){ // If it was already used
                    throw new NLPError("Duplicated tag " + tag); // Throw NLPError
                }

            }

            if(contents.isEmpty()){
                throw new NLPError("Illegal slot declaration, empty slot");
            }

            output.add(new Slot(contents, tags)); // Add mapped slot to output
            usedTags.addAll(tags); // Register tag for future reference
        }

        return output; // Return mapped pattern
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

    public static List<Slot> parse(String pattern) throws NLPError {
        return parseTags(groupInSlots(groupByContent(breakPattern(pattern))));
    }

    public static ContentType getContentType(String content){

        if(content.equals("param:int"))
            return ContentType.PARAMETER_INT;

        if(content.equals("param:day"))
            return ContentType.PARAMETER_DAY;

        if(content.equals("...") || (content.startsWith("#:") && isValidInt(content.substring(2)))){
            return ContentType.WHITESPACE;
        }

        if(content.startsWith("@")){
            return ContentType.SLOT_TAG;
        }

        return ContentType.STRING;
    }

    public static SlotType getSlotType(Slot slot){

        if(!slot.isEmpty()) {

            if (slot.size() <= 2 && slot.stream().allMatch(c -> getContentType(c).equals(ContentType.WHITESPACE))) {
                return SlotType.BLANK;
            }

            if(slot.stream().noneMatch(c -> getContentType(c).equals(ContentType.WHITESPACE))){
                return SlotType.SOLID;
            }

        }

        return SlotType.NONE;
    }

    public static int getBlankSlotParameter(Slot slot) throws NLPError {

        if(!getSlotType(slot).equals(SlotType.BLANK)){
            throw new NLPError("Illegal operation on solid slot, solid slots contain no parameters");
        }

        String content = slot.stream().findFirst().orElseThrow();

        if(content.equals("..."))
            return Integer.MAX_VALUE/2;

        int value = Integer.parseInt(content.substring(2));
        return value > 0? value:(Integer.MAX_VALUE/2);
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
