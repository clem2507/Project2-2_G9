package nlp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tokenizer {

    public static Stream<String> splitOn(final String text, final String delimiter){
        StringTokenizer tokenizer = new StringTokenizer(text, delimiter, true);
        List<String> temp = new ArrayList<>();

        while(tokenizer.hasMoreTokens()){
            temp.add(tokenizer.nextToken());
        }

        return temp.stream();
    }

    public static List<String> asTokenList(final String text){
        //TODO: This is horribly inefficient, needs to be improved. (note to self)
        //  -Dennis
        List<String> tokens = Arrays.asList(text.split("\\s+"));
        return tokens.stream()
                .flatMap(t -> splitOn(t, "?"))
                .flatMap(t -> splitOn(t, "."))
                .flatMap(t -> splitOn(t, ","))
                .flatMap(t -> splitOn(t, "+"))
                .flatMap(t -> splitOn(t, "-"))
                .flatMap(t -> splitOn(t, "/"))
                .flatMap(t -> splitOn(t, "&"))
                .flatMap(t -> splitOn(t, "\\"))
                .flatMap(t -> splitOn(t, "*"))
                .flatMap(t -> splitOn(t, "^"))
                .flatMap(t -> splitOn(t, "("))
                .flatMap(t -> splitOn(t, ")"))
                .flatMap(t -> splitOn(t, "="))
                .flatMap(t -> splitOn(t, ">"))
                .flatMap(t -> splitOn(t, "<"))
                .flatMap(t -> splitOn(t, ":"))
                .flatMap(t -> splitOn(t, "["))
                .flatMap(t -> splitOn(t, "]"))
                .flatMap(t -> splitOn(t, "{"))
                .flatMap(t -> splitOn(t, "}"))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public static String asString(final List<String> tokens){
        String out = "";

        for(String t : tokens)
            out += t + " ";

        return out.trim();
    }

    public static String normalize(String text){
        return asString(asTokenList(text));
    }

}
