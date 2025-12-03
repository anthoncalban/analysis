public class LexicalAnalysis {
    private String sourceCode;

    public LexicalAnalysis(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String analyze() {
        StringBuilder result = new StringBuilder();
        String[] tokens = sourceCode.split("\\s+");

        for (String token : tokens) {
            if (token.matches("[0-9]+")) result.append(token).append(" — NUMBER\n");
            else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) result.append(token).append(" — IDENTIFIER\n");
            else result.append(token).append(" — SYMBOL\n");
        }

        return result.toString();
    }
}
