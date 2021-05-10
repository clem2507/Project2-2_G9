package backend.interpreters.cfg;

import backend.FallbackInterpreter;
import backend.InterpreterNames;
import nlp.NLPError;
import nlp.cfg.StringTokenizer;
import nlp.cfg.parsing.*;

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
    private List<ProductionRule> inputRules;
    private List<AbstractMap.SimpleEntry<ProductionRule, String>> outputRules;


    public FallbackCFG() {
        this.inputRules = new ArrayList<>();
        this.outputRules = new ArrayList<>();
    }

    @Override
    public Map.Entry<String, Double> processQuery(String query){
        // Parse query according to the grammar rules

        //TODO: Re-write this "if" statement elegantly. This is more of a patch to run and test the code.
        if(inputRules.isEmpty())
            return null;

        ParsedNode parsedQuery = null;
        try {
            parsedQuery = ParsingUtils.parse(StringTokenizer.toTokenList(query), inputRules);
        } catch (NLPError nlpError) {
            nlpError.printStackTrace();
        }

        // REWRITE: Use production rules to generate output

        AbstractMap.SimpleEntry<String, Double> match = null;
        if (parsedQuery != null) {
            
        }
        return match;
    }

    @Override
    public void compileTemplate(String newPath) {
        this.path = newPath;
        readPath();
    }

    @Override
    public InterpreterNames getName() {
        return InterpreterNames.CONTEXT_FREE_GRAMMAR;
    }

    @Override
    public void reset() {
        this.path = "";
        this.inputRules = new ArrayList<>();
        this.outputRules = new ArrayList<>();
    }

    // Converts text file to production rules and responses
    public void readPath() {
        try {
            File txtFile = new File(path);
            Scanner myReader = new Scanner(txtFile);
            String line;
            String conditional = "";
            while (myReader.hasNextLine()) {
                line = myReader.nextLine();

                String[] ruleAction = line.split("::");
                String[] ntRule = ruleAction[1].split("->");
                String[] ntCond = ntRule[0].split("\\*");
                if (ntCond.length > 1) {
                    conditional = ntCond[1].trim();
                }

                LiteralSymbol NT = new LiteralSymbol(ntCond[0].trim());

                // Determine the 'product(s)' of the production rule
                String[] products = ntRule[1].split("\\|");
                List<Symbol> symbols = new ArrayList<>();
                
                switch (ruleAction[0].toLowerCase()) {
                    case "rule":
                        // Convert to input rules
                        for (String s : products) {
                            symbols.clear();
                            ArrayList<String> tokens = (ArrayList<String>) StringTokenizer.toTokenList(s);
                            // For now I'm converting every token to literal symbols
                            for (String token : tokens) {
                                symbols.add(new LiteralSymbol(token));
                            }
                            // Need to make a copy of the list
                            inputRules.add(new ProductionRule(NT, new ArrayList<>(symbols)));
                        }
                        break;
                    case "action":
                        // Convert to output rules
                        for (String s : products) {
                            symbols.clear();
                            ArrayList<String> tokens = (ArrayList<String>) StringTokenizer.toTokenList(s);
                            // For now I'm converting every token to literal symbols
                            for (String token : tokens) {
                                symbols.add(new LiteralSymbol(token));
                            }
                            // Need to make a copy of the list
                            outputRules.add(new AbstractMap.SimpleEntry<>(new ProductionRule(NT, new ArrayList<>(symbols)), conditional));
                        }
                        break;
                    default:
                        // should give an error to the bot, mistake in text file
                }
            }
            /*FOR TESTING PURPOSES

            System.out.println("Rules:");
            for (ProductionRule p : inputRules) {
                System.out.println(p);
            }
            System.out.println("----------------------");
            System.out.println("Actions");
            for (AbstractMap.SimpleEntry<ProductionRule, String> a : outputRules) {
                System.out.println(a.getKey().toString() + " for " + a.getValue());
            }*/
        }
        catch (FileNotFoundException | NLPError e) {
            System.out.println("File not found or problem with parsing");
        }

    }

    //TESTING PURPOSES
    public static void main(String[] args) {
        FallbackCFG test = new FallbackCFG();
        test.compileTemplate("C:\\Users\\gebruiker\\Documents\\Project 2-2\\botcfg.txt");
        //System.out.println(test.processQuery("When do I have lectures?").getKey());
    }
    

}
