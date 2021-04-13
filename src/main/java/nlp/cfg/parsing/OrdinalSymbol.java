package nlp.cfg.parsing;

/**
 * Represents a symbol that seeks to match with ordinal numbers only. For instance: 1st, 2nd, 12th.
 */
public class OrdinalSymbol extends NumericSymbol{

    @Override
    public boolean matchWith(String token) {
        return super.matchWith(token) && (token.endsWith("th") || token.endsWith("nd") || token.endsWith("st"));
    }

}
