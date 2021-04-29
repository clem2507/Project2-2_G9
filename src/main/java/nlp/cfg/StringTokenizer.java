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
    private static final char[] SYMBOLS = new char[]{
            '.', ',', '/', '\\', '!', '?', '+', '-', '*', '^', '(', ')', '[', ']', '{', '}', '=', ':'
    };

    private static final char[] ALPHABET = ("abcdefghijklmnopqrstuvwxyz".toUpperCase() + "abcdefghijklmnopqrstuvwxyz")
            .toCharArray();

    private static final char[] DIGITS = "0123456789".toCharArray();

    private static final char[] WHITESPACES = new char[]{
            ' ', '\t', '\n', '\r'
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

    /**
     * Determines the type of a character.
     * @param chr char to classify
     * @return CharType representing the class of a character
     * @throws NLPError if the character is unknown
     */
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
        StringBuilder output = new StringBuilder();

        while (!seq.isEmpty()) {
            char currentChar = seq.get(0);
            CharType type = getCharType(currentChar);

            if(!type.equals(CharType.WHITESPACE) && !type.equals(CharType.SEPARATOR)) {
                char chr = seq.get(0); seq.remove(0);
                output.append(chr);
                continue;
            }

            break;
        }

        assert output.length() > 0;
        return output.toString();
    }

    private static String getNextPunctuation(final List<Character> seq) throws NLPError {
        char currentChar = seq.get(0); seq.remove(0);
        CharType type = getCharType(currentChar);

        assert type.equals(CharType.SEPARATOR);
        return "" + currentChar;
    }

    private static String getNextNumber(final List<Character> seq) throws NLPError {
        StringBuilder output = new StringBuilder();

        while (!seq.isEmpty()) {
            char currentChar = seq.get(0);
            CharType type = getCharType(currentChar);

            if(type.equals(CharType.DIGIT)) {
                seq.remove(0);
                output.append(currentChar);
                continue;
            }

            if(currentChar == '.' && seq.size() >= 2 && getCharType(seq.get(1)).equals(CharType.DIGIT)) {
                seq.remove(0);
                output.append(currentChar);
                continue;
            }

            break;
        }

        assert output.length() > 0;
        return output.toString();
    }

    /**
     * Splits a string into tokens. Note that this tokenizer assumes the
     * string represents an expression in natural language.
     * @param string a string to split
     * @return a list of strings representing a sequence of tokens
     * @throws NLPError if the string could not be tokenized
     */
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

                default: throw new NLPError("Unforeseen character " + firstChar);
            }

        }

        return output;
    }

    public static void main(String[] args) {
        String input = "rule S = I want INT burgers, please.";

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
