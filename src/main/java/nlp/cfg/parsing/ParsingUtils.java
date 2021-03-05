package nlp.cfg.parsing;

import nlp.NLPError;
import nlp.cfg.StringTokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParsingUtils {

    public static List<Node> toNodes(final List<String> tokens) {
        return tokens.stream()
                .map(Node::new)
                .collect(Collectors.toList());
    }

    public static boolean compareTokenWithSymbol(String token, String symbol) {
        return token.equals(symbol);
    }

    /**
     * Parses a string according to the specified grammar rules.
     * @param tokens list of strings representing tokens
     * @param rules grammar rules
     * @return a Node object representing the root of the parse tree
     * @throws NLPError if parsing fails
     */
    public static Node parse(final List<String> tokens, final List<GrammarRule> rules) throws NLPError {
        List<Node> input = toNodes(tokens);

        // While the sequence is not reduced to a starting non-terminal symbol.
        outer_while : while (input.size() > 1) {

            // For each legal range of tokens we can analyse per iteration.
            // We start at 1 and finish at |input| (inclusive).
            // This is known as lookahead.
            for(int i = 1; i <= input.size(); i++) { // O(n)

                // For each legal starting position to analyze a sublist of tokens.
                // Imagine we slide a window [j, j + i] alongside the sequence of
                // tokens and we only analyse those that are inside of said range.
                 for(int j = 0; j < input.size() - i + 1; j++) { // O(n)
                     // We get the sublist of tokens [j, j + i].
                     final List<Node> subSeq = input.subList(j, j + i);
                     System.out.println("Working on subsequence: " + subSeq.toString());

                     // Now we try to apply the grammar rules.
                     for(GrammarRule rule : rules) { // O(m)

                         // If the rule can be applied to the sublist of tokens.
                         if(rule.isApplicable(subSeq)) {
                             // Keeps a reference to the old sequence.
                             final List<Node> oldInput = input;

                             // Get all tokens before the sublist.
                             List<Node> pre = new ArrayList<>(input.subList(0, j));
                             // Get the production after applying the rule.
                             Node modified = rule.apply(subSeq);
                             // Get All tokens after the sublist.
                             List<Node> pos = new ArrayList<>(input.subList(j + i, input.size()));

                             // Put them all together such that pre + (sublist becomes modified) + pos.
                             input = new ArrayList<>();
                             input.addAll(pre);
                             input.add(modified);
                             input.addAll(pos);

                             System.out.println(oldInput.toString() + " becomes " + input.toString());
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
        // get stuck forever, the time complexity is O(m*n^2). Where "m" is the number of
        // grammar rules and "n" is the number of tokens
    }

    public static void main(String[] args) {
        List<GrammarRule> rules = new ArrayList<>();
        rules.add(new GrammarRule("S", Collections.singletonList("::numeric::")));
        rules.add(new GrammarRule("S", Arrays.asList("S", "+", "S")));
        rules.add(new GrammarRule("S", Arrays.asList("(", "S", ")")));

        String input = "12 + (3 + 4)";

        try {
            Node output = parse(StringTokenizer.toTokenList(input), rules);

            if (output != null){
                System.out.println(output.flat());
            }

            else
                System.out.println("The sentence could not be parsed");

        } catch (NLPError nlpError) {
            nlpError.printStackTrace();
        }

    }

}
