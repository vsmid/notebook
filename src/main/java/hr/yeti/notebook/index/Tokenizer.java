package hr.yeti.notebook.index;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class Tokenizer {

    public static Set<String> tokenize(String text) {
        Set<String> tokens = new HashSet<>();
        String lowercase = text.toLowerCase();
        String alphanumeric = lowercase.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", " ");
        StringTokenizer tokenizer = new StringTokenizer(alphanumeric);
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }
}
