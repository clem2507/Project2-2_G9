package backend;

import nlp.NLPError;
import nlp.cfg.StringTokenizer;
import nlp.cfg.parsing.LiteralSymbol;
import nlp.cfg.parsing.ProductionRule;
import nlp.cfg.parsing.Symbol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * A class that handles custom skills that are added through a text file in a CFG format
 * Converts text to set of production rules and nonterminal - response pairs
 * Basic cases work; needs to be further extended for more advanced custom skills
 */
public class FallbackCFG implements FallbackInterpreter {

    private String path;
    private List<ProductionRule> rules;
    private List<AbstractMap.SimpleEntry<String, String>> responses;

    public FallbackCFG() {
        this.rules = new ArrayList<ProductionRule>();
        this.responses = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
    }

    @Override
    public Map.Entry<String, Double> processQuery(String query) {
        return new AbstractMap.SimpleEntry<String, Double>("todo", 3.1);
    }

    @Override
    public void notifyNewPath(String newPath) {
        this.path = newPath;
        this.rules = new ArrayList<ProductionRule>();
        this.responses = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
        readPath();
    }

    // Converts text file to production rules and responses
    public void readPath() {
        try {
            File txtFile = new File(path);
            Scanner myReader = new Scanner(txtFile);
            String line;
            while (myReader.hasNextLine()) {
                line = myReader.nextLine();

                String[] ruleaction = line.split("::");
                String[] split = ruleaction[1].split("->");
                switch (ruleaction[0].toLowerCase()) {
                    case "rule":
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
                        break;
                    case "action":
                        // Link NT to response
                        // In matchquery, we should find the nonterminal that matches with the query
                        // We should then match the nonterminal content with one of the strings
                        // in this mapping
                        responses.add(new AbstractMap.SimpleEntry<>(split[0], split[1]));
                        break;
                    default:
                        // should give an error to the bot, mistake in text file
                }
            }
            /* FOR TESTING PURPOSES

            System.out.println("Rules:");
            for (ProductionRule p : rules) {
                System.out.println(p);
            }
            System.out.println("----------------------");
            System.out.println("Actions");
            for (AbstractMap.SimpleEntry<String, String> a : responses) {
                System.out.println(a.getKey() + " to " + a.getValue());
            }*/
        }
        catch (FileNotFoundException | NLPError e) {
            System.out.println("File not found or problem with parsing");
        }

    }

}
