package nlp;

import java.util.*;

/**
 * Represents a sequence of slots from a given pattern matched with a given query.
 * Contains pairs of slots and lists of tokens, such that the first term is a slot
 * and the second term is a list of tokens (i.e. [slot, tokens]).
 */
public class MatchedSequence extends ArrayList<Map.Entry<Slot, List<String>>> {
    private String pattern, query;

    public MatchedSequence(String pattern, String query){
        super();
        this.pattern = pattern;
        this.query = query;
    }

    /**
     * Returns the list of tokens matched with the slot specified by
     * the index.
     * @param slotIndex index of the slot
     * @return list of tokens (i.e. list of strings)
     */
    public List<String> getMatchedTokensAt(int slotIndex){
        return get(slotIndex).getValue();
    }

    /**
     * Returns the set of contents in a slot specified by the index.
     * @param slotIndex index of the slot
     * @return set of strings representing the contents of the slot
     */
    public Slot getSlot(int slotIndex){
        return get(slotIndex).getKey();
    }

    /**
     * Returns an integer matched with a slot specified by the index.
     * @throws NLPError if the matched token is not a valid integer or if
     * more than one token was matched with the slot.
     * @param slotIndex index of the slot
     * @return an integer primitive
     */
    public int getIntAt(int slotIndex) throws NLPError {
        List<String> tokensAt = getMatchedTokensAt(slotIndex);

        if(tokensAt.size() != 1){
            throw new NLPError("Slot " + slotIndex + " matched with more than just one integer");
        }

        String token = tokensAt.stream().findFirst().orElseThrow();

        if(Pattern.isValidInt(token)){
            return Integer.parseInt(token);
        }

        throw new NLPError("Tried to return an invalid integer in slot " + slotIndex);
    }

    /**
     * Returns matched tokens in a slot specified by the index in the form of a string
     * @param slotIndex index of the slot
     * @return a string
     */
    public String getStringAt(int slotIndex){
        List<String> tokensAt = getMatchedTokensAt(slotIndex);
        return Tokenizer.asString(tokensAt);
    }

    /**
     * Returns a day of the week matched with the slot specified by the index in the form of a string
     * @throws NLPError if the matched token is not a valid day or if
     * more than one token was matched with the slot.
     * @param slotIndex index of the slot
     * @return a string
     */
    public String getDayAt(int slotIndex) throws NLPError {
        List<String> tokensAt = getMatchedTokensAt(slotIndex);

        if(tokensAt.size() != 1){
            throw new NLPError("Slot " + slotIndex + " matched with more than just one day");
        }

        String token = tokensAt.stream().findFirst().orElseThrow();

        if(Pattern.isValidDay(token)){
            return token;
        }

        throw new NLPError("Tried to return an invalid day of the week");
    }

    /**
     * Returns a real number between 0 and 1 (inclusive) representing the percentage
     * of the query matched with the pattern.
     * @return a double
     */
    public double useRatio(){
        return stream()
                .filter(s -> !Pattern.getSlotType(s.getKey()).equals(Pattern.SlotType.BLANK))
                .map(s -> (double)s.getValue().size())
                .reduce(Double::sum)
                .orElseThrow()/((double)(Tokenizer.asTokenList(query)).size());
    }

    /**
     * Returns a string representing the raw declaration of the pattern.
     * @return a string
     */
    public String getPattern(){
        return pattern;
    }

    /**
     * Returns a string representing the original content of the query
     * @return a string
     */
    public String getQuery(){
        return query;
    }

    /**
     * Returns the index of the slot with the specified tag
     * @param tag to match
     * @return Optional<Integer> representing the index of the slot
     */
    public Optional<Integer> getSlotIndex(String tag){
        Optional<Map.Entry<Slot, List<String>>> pair = stream().filter(p -> p.getKey().containsTag(tag)).findAny();
        return pair.map(this::indexOf);
    }

}
