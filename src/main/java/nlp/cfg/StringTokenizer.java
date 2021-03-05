package nlp.cfg;

import nlp.NLPError;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a tokenizer for sentences in English.
 * NOTE: Our old tokenizer has some inconveniences, but
 * since other parts of our code already rely on these
 * 'inconveniences' I preferred to starts fresh for the
 * new context-free grammar parser.
 */
public class StringTokenizer {
    private static char[] SYMBOLS = new char[]{
            '.', ',', '/', '\\', '!', '?', '+', '-', '*', '^', '(', ')'
    };

    private static char[] ALPHABET = ("abcdefghijklmnopqrstuvwxyz".toUpperCase() + "abcdefghijklmnopqrstuvwxyz")
            .toCharArray();

    private static char[] DIGITS = "0123456789".toCharArray();

    private static char[] WHITESPACES = new char[]{
            ' ', '\t', '\n'
    };

    private enum CharType {
        DIGIT, LETTER, SEPARATOR, WHITESPACE
    }

    private static boolean existsIn(final char chr, final char[] set) {

        for(char symbol : set) {

            if(symbol == chr)
                return true;

        }

        return false;
    }

    private static CharType getCharType(final char chr) throws NLPError {

        if(existsIn(chr, WHITESPACES))
            return CharType.WHITESPACE;

        if(existsIn(chr, SYMBOLS))
            return CharType.SEPARATOR;

        if(existsIn(chr, DIGITS))
            return CharType.DIGIT;

        if(existsIn(chr, ALPHABET))
            return CharType.LETTER;

        throw new NLPError("Unknown character type: " + chr);
    }

    private static String getNextWord(final List<Character> seq) throws NLPError {
        String output = "";

        while (!seq.isEmpty()) {
            char currentChar = seq.get(0);
            CharType type = getCharType(currentChar);

            if(!type.equals(CharType.WHITESPACE) && !type.equals(CharType.SEPARATOR)) {
                char chr = seq.get(0); seq.remove(0);
                output += chr;
                continue;
            }

            break;
        }

        assert !output.isEmpty();
        return output;
    }

    private static String getNextPunctuation(final List<Character> seq) throws NLPError {
        char currentChar = seq.get(0); seq.remove(0);
        CharType type = getCharType(currentChar);

        assert type.equals(CharType.SEPARATOR);
        return "" + currentChar;
    }

    private static String getNextNumber(final List<Character> seq) throws NLPError {
        String output = "";
        boolean dotFound = false;

        while (!seq.isEmpty()) {
            char currentChar = seq.get(0);
            CharType type = getCharType(currentChar);

            if(type.equals(CharType.DIGIT)) {
                seq.remove(0);
                output += currentChar;
                continue;
            }

            if(currentChar == '.' && seq.size() >= 2 && getCharType(seq.get(1)).equals(CharType.DIGIT)) {
                seq.remove(0);
                output += currentChar;
                continue;
            }

            break;
        }

        assert !output.isEmpty();
        return output;
    }

    public static List<String> toTokenList(final String string) throws NLPError {
        // Convert the string into a linked list of characters.
        List<Character> charSequence = string
                .chars()
                .mapToObj(chr -> (char) chr)
                .collect(Collectors.toCollection(LinkedList::new));
        // Empty output list of tokens.
        List<String> output = new ArrayList<>();

        while (!charSequence.isEmpty()){
            char firstChar = charSequence.get(0);

            switch (getCharType(firstChar)) {

                case WHITESPACE:
                    charSequence.remove(0);
                    break;

                case LETTER:
                    output.add(getNextWord(charSequence));
                    break;

                case SEPARATOR:
                    output.add(getNextPunctuation(charSequence));
                    break;

                case DIGIT:
                    output.add(getNextNumber(charSequence));
                    break;

                default: throw new NLPError("Unforeseen character " + firstChar + " (this should be impossible)");
            }

        }

        return output;
    }

    public static void main(String[] args) {
        String input = "2.4 + 5*4^2**4";

        try {
            List<String> tokens = toTokenList(input);

            for(String token : tokens) {
                System.out.println(token);
            }

        } catch (NLPError nlpError) {
            nlpError.printStackTrace();
        }

    }

}
