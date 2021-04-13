package nlp.cfg.parsing;

import nlp.NLPError;
import nlp.cfg.StringTokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParsingUtils {

    public static List<ParsedNode> toNodes(final List<String> tokens) {
        return tokens.stream()
                .map(LeafSymbol::new)
                .map(ParsedNode::new)
                .collect(Collectors.toList());
    }

    /**
     * Parses a string according to the specified grammar rules.
     * @param tokens list of strings representing tokens
     * @param rules grammar rules
     * @return a Node object representing the root of the parse tree
     * @throws NLPError if parsing fails
     */
    public static ParsedNode parse(final List<String> tokens, final List<ProductionRule> rules) throws NLPError {
        List<ParsedNode> input = toNodes(tokens);
        System.out.println("Parsing " + input.toString());

        // While the sequence is not reduced to a starting non-terminal symbol.
        outer_while : while (input.size() > 1) { // O(n)

            // For each legal range of tokens we can analyse per iteration.
            // We start at 1 and finish at |input| (inclusive).
            // This is known as lookahead.
            for(int i = 1; i <= input.size(); i++) { // O(n)

                // For each legal starting position to analyze a sublist of tokens.
                // Imagine we slide a window [j, j + i] alongside the sequence of
                // tokens and we only analyse those that are inside of said range.
                 for(int j = 0; j < input.size() - i + 1; j++) { // O(n)
                     // We get the sublist of tokens [j, j + i].
                     final List<ParsedNode> subSeq = input.subList(j, j + i);
                     System.out.println("For " + subSeq.toString());

                     // Now we try to apply the grammar rules.
                     for(ProductionRule rule : rules) { // O(m)

                         // If the rule can be applied to the sublist of tokens.
                         if(rule.isApplicable(subSeq)) {
                             // Keeps a reference to the old sequence.
                             final List<ParsedNode> oldInput = input;

                             // Get all tokens before the sublist.
                             List<ParsedNode> pre = new ArrayList<>(input.subList(0, j));
                             // Get the production after applying the rule.
                             ParsedNode modified = rule.apply(subSeq);
                             // Get All tokens after the sublist.
                             List<ParsedNode> pos = new ArrayList<>(input.subList(j + i, input.size()));

                             // Put them all together such that pre + (sublist becomes modified) + pos.
                             input = new ArrayList<>();
                             input.addAll(pre);
                             input.add(modified);
                             input.addAll(pos);

                             System.out.println("\t\t\t" + oldInput.toString() + " becomes " + input.toString());
                             // Go back up and repeat.
                             continue outer_while;
                         }

                     }

                 }

            }

            throw new NLPError("Syntax error during parse operation: " + input.toString());
        }

        return input.stream().findFirst().orElseThrow();
        // NOTE: In the worst case, assuming that the grammar rules do not make the parser
        // get stuck forever, the time complexity is O(m*n^3). Where "m" is the number of
        // grammar rules and "n" is the number of tokens
    }

    public static void main(String[] args) {
        List<ProductionRule> rules = new ArrayList<>();

        Symbol S = new LiteralSymbol("S");
        Symbol N = new NumericSymbol();
        Symbol ADD = new LiteralSymbol("+");
        Symbol RPAR = new LiteralSymbol(")");
        Symbol LPAR = new LiteralSymbol("(");

        rules.add(new ProductionRule(S, Collections.singletonList(N)));
        rules.add(new ProductionRule(S, Arrays.asList(S, ADD, S)));
        rules.add(new ProductionRule(S, Arrays.asList(LPAR, S, RPAR)));

        String input = "12 + (3 + 4)";

        try {
            ParsedNode output = parse(StringTokenizer.toTokenList(input), rules);

            if (output != null){
                System.out.println(output.prettyString());
            }

            else
                System.out.println("The sentence could not be parsed.");

        } catch (NLPError nlpError) {
            nlpError.printStackTrace();
        }

    }

}
