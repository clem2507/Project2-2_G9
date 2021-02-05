package backend;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class Domain {
    private final DomainNames uniqueName;

    public Domain(final DomainNames uniqueName) {
        this.uniqueName = uniqueName;
    }

    public abstract double weight(final List<String> tokens); //This will change soon!

    public abstract Skill dispatchSkill(List<String> tokens, BlockingQueue<AssistantMessage> resultsQueue);

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
