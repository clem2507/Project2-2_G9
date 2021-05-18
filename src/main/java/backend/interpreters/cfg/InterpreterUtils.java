package backend.interpreters.cfg;

import nlp.NLPError;
import nlp.cfg.parsing.ParsedNode;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InterpreterUtils {
    //TODO: Replace strings "[", "]", "{", "}" with these constants
    private static final String OUTPUT_VALUE_OPEN = "[", OUTPUT_VALUE_CLOSE = "]";
    private static final String OUTPUT_TRANSFORM_OPEN = "{", OUTPUT_TRANSFORM_CLOSE = "}";

    private static boolean headMatch(final ParsedNode node, final OutputRule rule) {
        return rule.getHead().equals(node.toString());
    }

    private static boolean keyMatch(final ParsedNode node, final OutputRule rule) {

        if(rule.getKey().size() == node.getChildren().size()) {

            for(int i = 0; i < rule.getKey().size(); i++) {

                if(!rule.getKey().get(i).equals(node.getChildren().get(i).toString()))
                    return false;

            }

            return true;
        }

        return false;
    }

    public static List<String> applyOutputRules(final ParsedNode node, final List<OutputRule> rules) throws NLPError {

        for(OutputRule rule : rules) {

            if(headMatch(node, rule) && keyMatch(node, rule)) {
                final List<String> tokens = new LinkedList<>();

                for(int i = 0; i < rule.getBody().size(); i++) {
                    final String token = rule.getBody().get(i);

                    if(token.equals("]") || token.equals("}"))
                        throw new NLPError("Unexpected " + token);

                    if(token.equals("[")) {

                        if(rule.getBody().size() <= i + 1)
                            throw new NLPError("Unexpected " + token);

                        tokens.addAll(findValue(
                                node,
                                rule.getBody().subList(i + 1, rule.getBody().size()).stream()
                                .takeWhile(t -> !t.equals("]"))
                                .collect(Collectors.toList())
                        ));

                        i += 4;
                        continue;
                    }

                    if(token.equals("{")) {

                        if(rule.getBody().size() <= i + 1)
                            throw new NLPError("Unexpected " + token);

                        tokens.addAll(transform(
                                node,
                                rule.getBody().subList(i + 1, rule.getBody().size()).stream()
                                        .takeWhile(t -> !t.equals("}"))
                                        .collect(Collectors.toList()),
                                rules
                        ));

                        i += 4;
                        continue;
                    }

                    tokens.add(token);
                }

                return tokens;
            }

        }

        throw new NLPError("Insufficient rule set to handle tree\n" + node.prettyString());
    }

    private static List<String> findValue(final ParsedNode node, final List<String> params) throws NLPError {

        if(params.size() == 3) {
            final String symbol = params.get(0);
            final String indexStr = params.get(2);

            try{
                final int index = Integer.parseInt(indexStr);
                final List<ParsedNode> selection = node.getChildren().stream()
                        .filter(c -> c.toString().equals(symbol))
                        .collect(Collectors.toList());

                if(index >= 0 && index < selection.size()) {
                    final ParsedNode selected = selection.get(index);
                    return selected.getChildren().stream().map(ParsedNode::toString).collect(Collectors.toList());
                }

                throw new NLPError("Index in value retrieval of the form [<SYMBOL>, <INDEX>] is out of bounds " +
                        "or no match for " + symbol + " was found");

            } catch (NumberFormatException e) {
                throw new NLPError("Index in value retrieval of the form [<SYMBOL>, <INDEX>] must be an integer");
            }

        }

        throw new NLPError("Wrong number of parameters in value retrieval [<SYMBOL>, <INDEX>]");
    }

    private static List<String> transform(final ParsedNode node, final List<String> params, final List<OutputRule> rules) throws NLPError {

        if(params.size() == 3) {
            final String symbol = params.get(0);
            final String indexStr = params.get(2);

            try{
                final int index = Integer.parseInt(indexStr);
                final List<ParsedNode> selection = node.getChildren().stream()
                        .filter(c -> c.toString().equals(symbol))
                        .collect(Collectors.toList());

                if(index >= 0 && index < selection.size()) {
                    final ParsedNode selected = selection.get(index);
                    return applyOutputRules(selected, rules);
                }

                throw new NLPError("Index in transform of the form {<SYMBOL>, <INDEX>} is out of bounds " +
                        "or no match for " + symbol + " was found");

            } catch (NumberFormatException e) {
                throw new NLPError("Index in transform of the form {<SYMBOL>, <INDEX>} must be an integer");
            }

        }

        throw new NLPError("Wrong number of parameters in transform {SYMBOL>, <INDEX>}");
    }

}
