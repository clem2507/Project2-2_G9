package nlp.cfg.parsing;

import nlp.NLPError;

/**
 * This is a technical class, at no point this class is intended to be used outside of the parser.
 * It represents the tokens provided by the sequence being parsed.
 */
public class LeafSymbol extends LiteralSymbol{

    public LeafSymbol(String content) {
        super(content);
    }

    @Override
    public boolean matchWith(String token) {
        return false;
    }
}
