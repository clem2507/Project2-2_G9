package nlp.cfg.parsing;

/**
 * Represents a symbol that seeks to match with integers only.
 */
public class IntegerSymbol extends NumericSymbol{

    @Override
    public boolean matchWith(String token) {
        return super.matchWith(token) && !token.contains(".") && !token.contains(",");
    }

}
