import java.util.ArrayList;

public class SemanticAnalyzer {
    private ArrayList<String> errors = new ArrayList<>();

    public void analyze(ArrayList<SyntaxAnalyzer.VariableDeclaration> decls) {
        errors.clear();

        for (SyntaxAnalyzer.VariableDeclaration d : decls) {
            if (d.type.equals("int")) {
                if (!d.value.matches("\\d+")) {
                    errors.add("Error: " + d.name + " should be an int value.");
                }
            }
            if (d.type.equals("double")) {
                if (!d.value.matches("\\d+(\\.\\d+)?")) {
                    errors.add("Error: " + d.name + " should be a double value.");
                }
            }
            if (d.type.equals("boolean")) {
                if (!(d.value.equals("true") || d.value.equals("false"))) {
                    errors.add("Error: " + d.name + " should be true/false.");
                }
            }
            if (d.type.equals("String")) {
                if (!d.value.startsWith("\"") || !d.value.endsWith("\"")) {
                    errors.add("Error: " + d.name + " should be a String in quotes.");
                }
            }
        }

        if (errors.isEmpty()) {
            errors.add("No semantic errors found.");
        }
    }

    public ArrayList<String> getErrors() {
        return errors;
    }
}
