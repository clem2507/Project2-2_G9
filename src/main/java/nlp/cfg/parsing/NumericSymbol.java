package nlp.cfg.parsing;

/**
 * Represents a symbol that seeks to match with any real number.
 */
public class NumericSymbol implements Symbol{
    @Override
    public boolean matchWith(String token) {
        return token.matches("-?\\d+(\\.\\d+)?");
    }

    @Override
    public String toString() {
        return "NUMBER";
    }

}
