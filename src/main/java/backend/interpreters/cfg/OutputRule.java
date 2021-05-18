package backend.interpreters.cfg;

import java.util.List;

public class OutputRule {
    private final String symbol;
    private final List<String> key, rule;

    public OutputRule(String symbol, List<String> key, List<String> rule) {
        this.symbol = symbol;
        this.key = key;
        this.rule = rule;

        System.out.println(this.symbol);
        System.out.println(this.key);
        System.out.println(this.rule);
    }

    public String getHead() {
        return symbol;
    }

    public List<String> getKey() {
        return key;
    }

    public List<String> getBody() {
        return rule;
    }

}
