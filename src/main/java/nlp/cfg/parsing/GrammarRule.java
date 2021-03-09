package nlp.cfg.parsing;

import nlp.Pattern;

import java.util.ArrayList;
import java.util.List;

public class GrammarRule {
    protected String nonTerminal;
    protected List<String> production;

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public GrammarRule(final String nonTerminal, final List<String> production) {
        this.nonTerminal = nonTerminal;
        this.production = new ArrayList<>(production);
    }

    public boolean isApplicable(final List<Node> seq) {
        System.out.print("Testing rule applicability " + toString() + " on " + seq.toString() + " : ");

        if(seq.size() != production.size()) {
            System.out.println("Not applicable, sequence and production have different lengths");
            return false;
        }

        for(int i = 0; i < seq.size(); i++) {
            final String symbol = production.get(i);
            final String token = seq.get(i).toString();

            switch (symbol) {
                case "::numeric::":

                    if (!isNumeric(token)) {
                        System.out.println("Not applicable, " + token + " is not a number");
                        return false;
                    }

                    break;

                default:

                    if (!token.equals(symbol)) {
                        System.out.println("Not applicable, mismatch " + token + " =/= " + symbol);
                        return false;
                    }

                    break;
            }

        }

        return true;
    }

    public Node apply(final List<Node> seq) {
        assert isApplicable(seq);
        System.out.println("Applying production rule " + toString() + " on " + seq.toString());
        return new Node(nonTerminal, new ArrayList<>(seq));
    }

    @Override
    public String toString() {
        return nonTerminal + " -> " + production.stream().reduce((a, b) -> a + " " + b).orElseThrow();
    }
}