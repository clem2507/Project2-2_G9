package nlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TokenPattern {
    private final List<Set<String>> pattern;

    public TokenPattern(){
        pattern = new ArrayList<>();
    }

    public void addTokenSet(Set<String> set){
        pattern.add(set);
    }

    public void addGeneric(){
        pattern.add(Collections.EMPTY_SET);
    }

    public boolean isAny(String token, int index){
        return pattern.get(index).contains(token) || isGeneric(index);
    }

    public boolean isGeneric(int index){
        return pattern.get(index).isEmpty();
    }

}
