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

        else if(Pattern.getContentType(content).equals(Pattern.ContentType.PARAMETER_INT)) { // If the content is of type INT

            for (int i = 0; i < tokens.size(); i++) { // Iterate over each token

                if (Pattern.isValidInt(tokens.get(i))) { // If the token is a valid integer
                    startsAt = i; // There is a match at i
                    break; // Stop iterating
                }

            }

        }

        else if(Pattern.getContentType(content).equals(Pattern.ContentType.PARAMETER_DAY)) { // If the content is of type DAY

            for (int i = 0; i < tokens.size(); i++) { // Iterate over each token

                if (Pattern.isValidDay(tokens.get(i))) { // If the token is a valid day of the week
                    startsAt = i; // There is a match at i
                    break; // Stop iterating
                }

            }

        }

        //TODO: Group both "else if" statements in one. (note to self) -Dennis

        endsAt = startsAt + Pattern.getContentLength(content); // Get the length of the matched sublist of tokens
        return startsAt != -1 && endsAt > startsAt? (new AbstractMap.SimpleEntry<>(startsAt, endsAt)):null; // Null if there is no match
    }

    public static Optional<Map.Entry<Integer, Integer>> matchSlotWithTokens(final Set<String> slot, final List<String> tokens){
        return slot.stream()
                .map(c -> matchContentWithTokens(c, tokens))
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(a -> a.getValue() - a.getKey())); // Get longest match in (slot, tokens[matchFrom:~])
    }

    public static List<Map.Entry<Set<String>, List<String>>> matchPatternWithString(final String pattern, final String query) throws NLPError {
        //TODO: This function is too long. There must be a way to split this algorithm into smaller parts
        List<String> tokens = Tokenizer.asTokenList(query); // Break query into tokens
        List<Set<String>> slots = Pattern.parse(pattern); // Parse pattern into slots (sets of strings)
        List<Map.Entry<Set<String>, List<String>>> output = new ArrayList<>(); // The sequence of (slot, tokens) that we will return
        int lastMatchBegins = 0, lastMatchEnds = 0; // Keep track of where we did the last match

        for(int slot_i = 0; slot_i < slots.size(); slot_i++){ // Iterate over the slots
            final int matchFrom = lastMatchEnds;
            Set<String> slot = slots.get(slot_i); // Current slot

            if(Pattern.getSlotType(slot).equals(Pattern.SlotType.SOLID)) { // If slot is SOLID
                Optional<Map.Entry<Integer, Integer>> range = matchSlotWithTokens(slot, tokens.subList(matchFrom, tokens.size()));
                // NOTE: Here we match the longest content in slot to prevent contents that are substrings of longer
                // contents from interfering. (i.e. "remind me to" is a super-string of "remind me")

                if (range.isPresent()) { // If such match exists
                    // We extract the matched indexes and apply the offset
                    int startsAt = range.get().getKey() + lastMatchEnds;
                    int endsAt = range.get().getValue() + lastMatchEnds;

                    // Then we check two cases:
                    //  a) It is the first slot, and thus no need to check if this slot's match is consecutive to the previous match
                    //  b) This match starts right where the previous one ends, and thus is consecutive
                    if (slot_i == 0 || (lastMatchEnds == startsAt)){
                        lastMatchBegins = startsAt; lastMatchEnds = endsAt; // Register where the last match occurred
                        Map.Entry<Set<String>, List<String>> element = new AbstractMap.SimpleEntry<>(
                                slot,
                                tokens.subList(startsAt, endsAt)
                        ); // Pack this match into a pair
                        output.add(element); // Push the pair into the output sequence
                        continue; // Move to the next slot
                    }

                }

                return null; // If a solid slot has no match, then stop and return null
            }

            else if(Pattern.getSlotType(slot).equals(Pattern.SlotType.BLANK)){ // If slot is BLANK
                int limit = Pattern.getBlankSlotParameter(slot); // Read the blank slot's parameter
                Set<String> nextSlot = slot_i < slots.size() - 1? slots.get(slot_i + 1):null; // Get the next slot

                if(nextSlot == null){ // If there is no next slot
                    Map.Entry<Set<String>, List<String>> element = new AbstractMap.SimpleEntry<>(
                            slot,
                            tokens.subList(
                                    matchFrom,
                                    limit==-1? tokens.size():Math.min(tokens.size(), matchFrom + limit)
                            )
                    ); // Get the remaining list of tokens up to limit, if limit != -1
                    output.add(element); // Add pair to the sequence
                }

                else{ // If there is next slot
                    assert !Pattern.getSlotType(nextSlot).equals(Pattern.SlotType.BLANK):"Can't have consecutive blank slots";
                    Optional<Map.Entry<Integer, Integer>> range = matchSlotWithTokens(
                            nextSlot,
                            tokens.subList(matchFrom, tokens.size())
                    ); // We match the next slot

                    if(range.isPresent()){ // If there is a match
                        // Just like above, we adjust to the offset
                        int startsAt = range.get().getKey() + lastMatchEnds;
                        int endsAt = range.get().getValue() + lastMatchEnds;

                        // Then we have to check if the next slot does not completely overlap the blank slot
                        // If the next slot matches right after the previous slot, then the blank slot is empty
                        if(startsAt - matchFrom > 0){

                            if(limit != -1 && startsAt - matchFrom > limit) {
                                // If limit is specified and the blank slot matches with more than -limit- tokens
                                // then stop and return null
                                return null;
                            }

                            // Now we add the chunk of tokens between the previous slot and the next slot as
                            // matches of the blank slot
                            Map.Entry<Set<String>, List<String>> element = new AbstractMap.SimpleEntry<>(
                                    slot,
                                    tokens.subList(
                                            matchFrom,
                                            startsAt
                                    )
                            );
                            output.add(element);
                            lastMatchBegins = matchFrom;
                            lastMatchEnds = startsAt;
                            continue;
                        }

                        else{ // We add the empty blank slot into the sequence
                            Map.Entry<Set<String>, List<String>> element = new AbstractMap.SimpleEntry<>(
                                    slot,
                                    null
                            );
                            output.add(element);
                            continue;
                        }

                    }

                    return null; // If there is no match for the next slot, we can early stop
                }

            }

            else{
                throw new AssertionError("Undefined slot type in position " + Integer.toString(slot_i));
            }

        }

        return output;
    }

    public static MatchedSequence compile(String pattern, String query){

        try {
            // First we get the matched sequence (i.e. pairs of (slot, tokens)
            List<Map.Entry<Set<String>, List<String>>> sequence = matchPatternWithString(pattern, query);

            // Since the matcher returns null when there is no match, we check for that
            if(sequence != null){
                // We translate the return type of the pattern matcher to our comfortable MatchedSequence
                MatchedSequence matchedSequence = new MatchedSequence(Tokenizer.asTokenList(query));
                matchedSequence.addAll(sequence);
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

}
