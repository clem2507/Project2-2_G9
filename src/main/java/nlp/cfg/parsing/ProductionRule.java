package nlp.cfg.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a production rule of the form S -> EXPR, where EXPR is an arbitrary
 * combination of non-terminal symbols.
 */
public class ProductionRule {
    protected Symbol nonTerminal;
    protected List<Symbol> production;

    public ProductionRule(final Symbol nonTerminal, final List<Symbol> production) {
        this.nonTerminal = nonTerminal;
        this.production = new ArrayList<>(production);
    }

    public boolean isApplicable(final List<ParsedNode> seq) {
        System.out.println("\tTesting production rule " + toString());

        if(seq.size() != production.size()) {
            System.out.println("\t\tFalse, sequence and production have different lengths");
            return false;
        }

        for(int i = 0; i < seq.size(); i++) {
            final Symbol symbol = production.get(i);
            final String token = seq.get(i).toString();

            if(!symbol.matchWith(token)) {
                System.out.println("\t\tFalse, " + symbol.toString() + " failed to match with " + token);
                return false;
            }

        }

        return true;
    }

    public ParsedNode apply(final List<ParsedNode> seq) {
        assert isApplicable(seq);
        System.out.println("\t\tApplying production rule " + toString());
        return new ParsedNode(nonTerminal, new ArrayList<>(seq));
    }

    @Override
    public String toString() {
        return nonTerminal.toString() + " -> " + production.stream()
                .map(Object::toString)
                .reduce((a, b) -> a + " " + b).orElseThrow();
    }

    public Symbol getNonTerminal() {
        return nonTerminal;
    }

    public List<Symbol> getProduction() {
        return production;
    }
}
