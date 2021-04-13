package nlp.cfg.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParsedNode {
    protected Symbol definition;
    protected List<ParsedNode> children;

    public ParsedNode(final Symbol definition, final List<ParsedNode> children) {
        this.definition = definition;
        this.children = children != null? new ArrayList<>(children):Collections.emptyList();
    }

    public ParsedNode(final Symbol definition) {
        this(definition, null);
    }

    public List<ParsedNode> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof ParsedNode))
            return false;

        return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return definition.toString();
    }

    public String prettyString(int level, boolean isLastChild) {
        StringBuilder out = new StringBuilder();

        for(int i = 0; i < level; i++) {
            out.append(i < level - 1 ? "     ": "|--> ");
        }

        out.append(definition.toString()).append("\n");

        for(int j = 0; j < children.size(); j++) {
            ParsedNode child = children.get(j);
            out.append(child.prettyString(level + 1, j == children.size() - 1));
        }

        return out.toString();
    }

    public String prettyString() {
        return prettyString(0, true);
    }

}
