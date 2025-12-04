import java.util.ArrayList;

public class LexicalAnalyzer {
    public static class Token {
        String type;
        String value;

        Token(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public String toString() {
            return type + " : " + value;
        }
    }

    private ArrayList<Token> tokens = new ArrayList<>();

    public void analyze(String code) {
        tokens.clear();
        String[] words = code.split("\\s+"); // split by spaces

        for (String w : words) {
            if (w.equals("int") || w.equals("double") || w.equals("String") || w.equals("boolean")) {
                tokens.add(new Token("TYPE", w));
            } else if (w.equals("=")) {
                tokens.add(new Token("ASSIGN", w));
            } else if (w.endsWith(";")) {
                String name = w.substring(0, w.length() - 1);
                tokens.add(new Token("IDENTIFIER", name));
                tokens.add(new Token("SEMICOLON", ";"));
            } else {
                tokens.add(new Token("IDENTIFIER", w));
            }
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
