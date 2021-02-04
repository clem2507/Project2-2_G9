package nlp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tokenizer {

    private static Stream<String> splitOn(String text, String delimiter){
        StringTokenizer tokenizer = new StringTokenizer(text, delimiter, true);
        List<String> temp = new ArrayList<>();

        while(tokenizer.hasMoreTokens()){
            temp.add(tokenizer.nextToken());
        }

        return temp.stream();
    }

    public static List<String> tokenize(String text){
        //TODO: This is horribly inefficient, needs to be improved.
        //  -Dennis
        List<String> tokens = Arrays.asList(text.split("\\s+"));
        return tokens.stream()
                .flatMap(t -> splitOn(t, "?"))
                .flatMap(t -> splitOn(t, "."))
                .flatMap(t -> splitOn(t, ","))
                .flatMap(t -> splitOn(t, "+"))
                .flatMap(t -> splitOn(t, "-"))
                .flatMap(t -> splitOn(t, "/"))
                .flatMap(t -> splitOn(t, "*"))
                .flatMap(t -> splitOn(t, "^"))
                .flatMap(t -> splitOn(t, "("))
                .flatMap(t -> splitOn(t, ")"))
                .collect(Collectors.toList());
    }

}
