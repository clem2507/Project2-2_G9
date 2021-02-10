package nlp;

import java.util.*;

public class MatchedSequence extends ArrayList<Map.Entry<Set<String>, List<String>>> {
    private String pattern, query;

    public MatchedSequence(String pattern, String query){
        super();
        this.pattern = pattern;
        this.query = query;
    }

    public List<String> getMatchedTokensAt(int slotIndex){
        return get(slotIndex).getValue();
    }

    public Set<String> getSlot(int slotIndex){
        return get(slotIndex).getKey();
    }

    public int getIntAt(int slotIndex){
        List<String> tokensAt = getMatchedTokensAt(slotIndex);
        assert tokensAt.size() == 1:"<param:int> slots should only match with one token, an integer";
        String token = tokensAt.stream().findFirst().orElseThrow();

        if(Pattern.isValidInt(token)){
            return Integer.parseInt(token);
        }

        throw new AssertionError("Tried to return an invalid integer");
    }

    public String getStringAt(int slotIndex){
        List<String> tokensAt = getMatchedTokensAt(slotIndex);
        return Tokenizer.asString(tokensAt);
    }

    public String getDayAt(int slotIndex){
        List<String> tokensAt = getMatchedTokensAt(slotIndex);
        assert tokensAt.size() == 1:"<param:day> slots should only match with one token, an integer";
        String token = tokensAt.stream().findFirst().orElseThrow();

        if(Pattern.isValidDay(token)){
            return token;
        }

        throw new AssertionError("Tried to return an invalid day of the week");
    }

    public double useRatio(){
        return stream()
                .map(s -> (double)s.getValue().size())
                .reduce(Double::sum)
                .orElseThrow()/((double)(Tokenizer.asTokenList(query)).size());
    }

    public String getPattern(){
        return pattern;
    }

    public String getQuery(){
        return query;
    }

}
