public class SemanticAnalysis {
    private String sourceCode;

    public SemanticAnalysis(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String analyze() {
        String[] lines = sourceCode.split("\n");
        String declared = "";   
        String errors = "";

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("int ") || line.startsWith("String ") ||
                line.startsWith("double ") || line.startsWith("boolean ")) {
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    String var = parts[1].replace(";", "");
                    declared += var + " "; 
                }
            }

            String[] words = line.split("[^a-zA-Z0-9_]");
            for (String w : words) {
                if (w.length() > 0) {
                    if (!declared.contains(w) && !isKeyword(w) && !isNumber(w)) {
                        errors += "Semantic Error: Variable '" + w + "' used without declaration.\n";
                    }
                }
            }
        }

        if (errors.equals("")) {
            return "✅ Semantic Analysis Passed: No semantic errors found.";
        } else {
            return errors;
        }
    }

    private boolean isKeyword(String word) {
        String[] keywords = {
            "int","String","double","boolean","if","else","for","while","return",
            "public","private","class","static","void","new","true","false","print"
        };
        for (String k : keywords) {
            if (word.equals(k)) return true;
        }
        return false;
    }

    private boolean isNumber(String word) {
        return word.matches("[0-9]+");
    }
}