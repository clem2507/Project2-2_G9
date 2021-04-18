package backend;

import nlp.NLPError;
import nlp.cfg.StringTokenizer;
import nlp.cfg.parsing.LiteralSymbol;
import nlp.cfg.parsing.ProductionRule;
import nlp.cfg.parsing.Symbol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FallbackCFG implements FallbackInterpreter {

    private String path;
    // Could just be combined, not sure
    private List<ProductionRule> rules;

    @Override
    public Map.Entry<String, Double> processQuery(String query) {
        return new AbstractMap.SimpleEntry<String, Double>("todo", 3.1);
    }

    @Override
    public void compileTemplate(String newPath) {
        this.path = newPath;
        this.rules = new ArrayList<ProductionRule>();
        readPath();
    }

    @Override
    public InterpreterNames getName() {
        return InterpreterNames.CONTEXT_FREE_GRAMMAR;
    }

    @Override
    public void reset() {
        // This is to tell the interpreter to forget all templates in memory.
    }

    // Converts text file to production rules and responses
    public void readPath() {
        try {
            File txtFile = new File(path);
            Scanner myReader = new Scanner(txtFile);
            String line;
            while (myReader.hasNextLine()) {
                line = myReader.nextLine();

                // Determine the NT of the production rules
                String[] split = line.split("->");
                LiteralSymbol NT = new LiteralSymbol(split[0]);

                // Determine the 'product(s)' of the production rule
                String[] products = split[1].split("\\|");

                List<Symbol> symbols = new ArrayList<Symbol>();
                // Convert to production rules
                for (String s : products) {
                    symbols.clear();
                    ArrayList<String> tokens = (ArrayList<String>) StringTokenizer.toTokenList(s);
                    // For now I'm converting every token to literal symbols
                    for (String token : tokens) {
                        symbols.add(new LiteralSymbol(token));
                    }
                    // Need to make a copy of the list
                    rules.add(new ProductionRule(NT, new ArrayList<Symbol>(symbols)));
                }

            }
        }
        catch (FileNotFoundException | NLPError e) {
            System.out.println("File not found or problem with parsing");
        }

    }

}
