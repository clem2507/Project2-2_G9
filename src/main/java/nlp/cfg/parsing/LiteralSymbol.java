package nlp.cfg.parsing;

/**
 * Represents a symbol that seeks to match its content with
 * a token.
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
