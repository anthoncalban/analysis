import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public enum TokenType {
        COMMENT("//.*|/\\*[\\s\\S]*?\\*/"),
        // Added String literal regex: matches anything between double quotes
        STRING("\"[^\"]*\""), 
        KEYWORD("\\b(package|import|class|public|static|private|protected|final|abstract|void|int|float|boolean|String|if|else|while|return)\\b"),
        NUMBER("\\b\\d+(\\.\\d+)?([fFdD])?\\b"), 
        BOOLEAN("\\b(true|false)\\b"),
        IDENTIFIER("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b"),
        OPERATOR("[+\\-*/=<>!&|]"),
        SEPARATOR("[;,.(){}\\[\\]]"),
        WHITESPACE("[ \\t\\f\\r\\n\\uFEFF]+");

        public final String pattern;
        TokenType(String pattern) { this.pattern = pattern; }
    }

    public static class Token {
        public final TokenType type;
        public final String value;
        public final int position;

        public Token(TokenType type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }
        @Override public String toString() { return String.format("%-12s | %s", type, value); }
    }

    private static final Pattern TOKEN_PATTERN;
    static {
        StringBuilder pb = new StringBuilder();
        for (TokenType type : TokenType.values()) pb.append(String.format("|(?<%s>%s)", type.name(), type.pattern));
        TOKEN_PATTERN = Pattern.compile(pb.substring(1));
    }

    public static List<Token> lex(String input) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(input);
        while (matcher.find()) {
            for (TokenType type : TokenType.values()) {
                if (matcher.group(type.name()) != null) {
                    if (type != TokenType.WHITESPACE && type != TokenType.COMMENT) {
                        tokens.add(new Token(type, matcher.group(type.name()), matcher.start()));
                    }
                    break;
                }
            }
        }
        return tokens;
    }
}