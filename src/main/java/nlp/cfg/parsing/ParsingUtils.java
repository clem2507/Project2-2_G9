package nlp.cfg.parsing;

import nlp.NLPError;
import nlp.cfg.StringTokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParsingUtils {

    public static List<Node> toNodes(final List<String> tokens) {
        return tokens.stream()
                .map(Node::new)
                .collect(Collectors.toList());
    }

    public static boolean compareTokenWithSymbol(String token, String symbol) {
        return token.equals(symbol);
    }

    public static Node parse(final String sentence, final List<GrammarRule> rules) throws NLPError {
        List<Node> input = toNodes(StringTokenizer.toTokenList(sentence));

        outer_while : while (input.size() > 1) {

            for(int i = 1; i <= input.size(); i++) {

                 for(int j = 0; j < input.size() - i + 1; j++) {
                     final List<Node> subSeq = input.subList(j, j + i);
                     System.out.println("Working on subsequence: " + subSeq.toString());

                     for(GrammarRule rule : rules) {

                         if(rule.applicable(subSeq)) {
                             final List<Node> oldInput = input;

                             List<Node> pre = new ArrayList<>(input.subList(0, j));
                             Node modified = rule.transform(subSeq);
                             List<Node> pos = new ArrayList<>(input.subList(j + i, input.size()));

                             input = new ArrayList<>();
                             input.addAll(pre);
                             input.add(modified);
                             input.addAll(pos);

                             System.out.println(oldInput.toString() + " becomes " + input.toString());
                             continue outer_while;
                         }

                     }

                 }

            }

            return null;
        }

        return input.stream().findFirst().orElseThrow();
    }

    public static void main(String[] args) {
        List<GrammarRule> rules = new ArrayList<>();
        rules.add(new GrammarRule("S", Collections.singletonList("D")));
        rules.add(new GrammarRule("S", Arrays.asList("S", "+", "S")));
        rules.add(new GrammarRule("D", Collections.singletonList("a")));

        String input = "a + a + a";

        try {
            Node output = parse(input, rules);
            System.out.println(output.flat());
        } catch (NLPError nlpError) {
            nlpError.printStackTrace();
        }

    }

}
