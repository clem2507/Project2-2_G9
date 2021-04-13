package nlp.cfg.parsing;

import nlp.NLPError;

/**
 * Represents a symbol that seeks to match its content with
 * an arbitrary token. Effectively equivalent to matching two tokens hat
 * are the same string.
 */
public class LiteralSymbol implements Symbol {
    private final String content;

    public LiteralSymbol(String content) {
        this.content = content;
    }

    @Override
    public boolean matchWith(String token) {
        return content.equals(token);
    }

    @Override
    public String toString() {
        return content;
    }

}
