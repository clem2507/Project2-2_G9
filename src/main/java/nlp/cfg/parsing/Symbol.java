package nlp.cfg.parsing;

import nlp.NLPError;

/**
 * Represents the base type for every symbol type.
 */
public interface Symbol {

    /**
     * Tests if this symbol matches with an arbitrary token.
     * @param token to match with.
     * @return true if there is a match and false otherwise.
     */
    boolean matchWith(String token);

}
