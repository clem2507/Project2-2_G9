package backend.interpreters.cfg;

import backend.FallbackInterpreter;
import backend.InterpreterNames;
import com.android.dx.util.Output;
import nlp.NLPError;
import nlp.cfg.StringTokenizer;
import nlp.cfg.parsing.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CFGInterpreter implements FallbackInterpreter {
    private static final String PARSE_RULE_START = "parse";
    private static final String OUTPUT_RULE_START = "output";
    private static final String CONNECTOR = "as";
    private static final String OUTPUT_KEY_OPEN = "(", OUTPUT_KEY_CLOSE = ")";
    private static final String NUMERIC_TOKEN = "NUM", INTEGER_TOKEN = "INT", ORDINAL_TOKEN = "ORD";
    private final List<OutputRule> outputRules;
    private final List<ProductionRule> parseRules;

    public CFGInterpreter() {
        outputRules = new ArrayList<>();
        parseRules = new ArrayList<>();
    }

    @Override
    public Map.Entry<String, Double> processQuery(final String query) {

        try {
            final ParsedNode root = ParsingUtils.parse(StringTokenizer.toTokenList(query), parseRules);
            final List<String> outputTokens = InterpreterUtils.applyOutputRules(root, outputRules);

            final String output = outputTokens.stream().reduce((a, b) -> a + " " + b).orElseThrow();

            return new AbstractMap.SimpleEntry<>(output, 1.0);
        } catch (NLPError nlpError) {
            nlpError.printStackTrace();
        }

        return null;
    }

    @Override
    public void compileTemplate(final String newPath) {

        try {
            String fileContent = Files.readString(Path.of(newPath));
            String[] lines = fileContent.split("\\R");

            for(String line : lines) {
                List<String> tokens = StringTokenizer.toTokenList(line);
                compileTokens(tokens);
            }

        } catch (IOException | NLPError e) {
            e.printStackTrace();
        }

    }

    private void compileTokens(final List<String> tokens) throws NLPError {

        if(!tokens.isEmpty()) {

            if (tokens.get(0).equals(OUTPUT_RULE_START)) {
                compileOutputRule(tokens);
                return;
            }

            if (tokens.get(0).equals(PARSE_RULE_START)) {
                compileParseRule(tokens);
                return;
            }

            throw new NLPError("Unexpected token: " + tokens.get(0));
        }

    }

    private void compileParseRule(final List<String> tokens) throws NLPError {

        if(tokens.size() < 4)
            throw new NLPError("Production rule is too short.");

        String head = tokens.get(1);

        if(tokens.get(2).equals(CONNECTOR)) {
            List<String> body = tokens.subList(3, tokens.size());

            Symbol S = new LiteralSymbol(head);
            List<Symbol> production = body.stream().map(t -> {

                if(t.equals(NUMERIC_TOKEN))
                    return new NumericSymbol();

                if(t.equals(INTEGER_TOKEN))
                    return new IntegerSymbol();

                if(t.equals(ORDINAL_TOKEN))
                    return new OrdinalSymbol();

                return new LiteralSymbol(t);
            }).collect(Collectors.toList());

            ProductionRule newRule = new ProductionRule(S, production);
            parseRules.add(newRule);

            return;
        }

        throw new NLPError("Connector " + CONNECTOR + " not found. Instead got " + tokens.get(2));
    }

    private void compileOutputRule(final List<String> tokens) throws NLPError {

        if(tokens.size() < 7)
            throw new NLPError("output rule is too short.");

        String head = tokens.get(1);

        if(tokens.get(2).equals(OUTPUT_KEY_OPEN)) {
            List<String> key = tokens.subList(3, tokens.size()).stream()
                    .takeWhile(t -> !t.equals(OUTPUT_KEY_CLOSE))
                    .collect(Collectors.toList());


            if(tokens.get(key.size() + 4).equals(CONNECTOR)) {
                List<String> body = tokens.subList(key.size() + 5, tokens.size());

                OutputRule newRule = new OutputRule(head, key, body);
                outputRules.add(newRule);

                return;
            }

            throw new NLPError("Connector " + CONNECTOR + " not found. Instead got " + tokens.get(key.size() + 2));
        }

        throw new NLPError("Output rule key opening symbol " + OUTPUT_KEY_OPEN + " not found. Instead got " + tokens.get(2));
    }

    @Override
    public InterpreterNames getName() {
        return InterpreterNames.CFG;
    }

    @Override
    public void reset() {
        outputRules.clear();
        parseRules.clear();
    }

    public static void main(String[] args) {
        List<String> tokens = Arrays.asList("a", "b", "(", "c", "d", "e", ")");
        List<String> key = tokens.subList(3, tokens.size()).stream()
                .takeWhile(t -> !t.equals(")"))
                .collect(Collectors.toList());
        System.out.println(key);
    }

}
