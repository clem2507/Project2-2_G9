package nlp;

import java.util.HashSet;
import java.util.Set;

public class Slot extends HashSet<String> {
    private Set<String> tags;

    public Slot(Set<String> contents, Set<String> tags){
        super(contents);
        this.tags = new HashSet<>(tags);
    }

    public boolean containsTag(String tag){
        return tags.contains(tag);
    }

    @Override
    public String toString() {
        return "(" + super.toString() + " Tags" + tags.toString() + ")";
    }
}
