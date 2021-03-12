package nlp.cfg.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    protected String definition;
    protected List<Node> children;

    public Node(final String definition, final List<Node> children) {
        this.definition = definition;
        this.children = children != null? new ArrayList<>(children):Collections.emptyList();
    }

    public Node(final String definition) {
        this(definition, null);
    }

    public List<Node> getChildren() {
        return new ArrayList<>(children);
    }

    public String flat() {
        return "(" + (definition + " " + children.stream()
                .map(n -> {
                    if (n.toString().equals("("))
                        return new Node("[", n.getChildren());

                    if (n.toString().equals(")"))
                        return new Node("]", n.getChildren());

                    return n;
                })
                .map(Node::flat).reduce((a, b) -> a + " " + b).orElse("")).trim() + ")";
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof Node))
            return false;

        return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return definition;
    }
}
