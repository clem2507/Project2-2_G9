package backend;

import nlp.MatchedSequence;
import nlp.Pattern;
import nlp.PatternMatcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public abstract class Domain {
    private final DomainNames uniqueName;
    private final List<String> patterns;

    public Domain(final DomainNames uniqueName) {
        this.patterns = new ArrayList<>();
        this.uniqueName = uniqueName;
    }

    public void addPattern(String newPattern){
        patterns.add(newPattern);
    }

    public List<String> getPatterns(){
        return new ArrayList<>(patterns);
    }

    public MatchedSequence matchQuery(String query){
        return patterns.stream()
                .map(p -> PatternMatcher.compile(p, query))
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(MatchedSequence::useRatio))
                .orElse(null);
    }

    public abstract Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel);

    public DomainNames getUniqueName(){
        return uniqueName;
    }

    @Override
    public int hashCode() {
        return uniqueName.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {

        if(this == obj)
            return true;

        if(obj instanceof Domain)
            return uniqueName.equals(((Domain) obj).getUniqueName());

        return false;
    }

    @Override
    public String toString() {
        return uniqueName.toString();
    }
}
