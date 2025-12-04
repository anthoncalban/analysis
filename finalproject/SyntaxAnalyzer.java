import java.util.ArrayList;

public class SyntaxAnalyzer {
    public static class VariableDeclaration {
        String type, name, value;

        VariableDeclaration(String type, String name, String value) {
            this.type = type;
            this.name = name;
            this.value = value;
        }

        public String toString() {
            return type + " " + name + " = " + value + ";";
        }
    }

    private ArrayList<VariableDeclaration> declarations = new ArrayList<>();

    public void analyze(ArrayList<LexicalAnalyzer.Token> tokens) {
        declarations.clear();

        // very simple check: TYPE IDENTIFIER ASSIGN IDENTIFIER SEMICOLON
        for (int i = 0; i < tokens.size() - 4; i++) {
            if (tokens.get(i).type.equals("TYPE") &&
                tokens.get(i+1).type.equals("IDENTIFIER") &&
                tokens.get(i+2).type.equals("ASSIGN") &&
                tokens.get(i+3).type.equals("IDENTIFIER") &&
                tokens.get(i+4).type.equals("SEMICOLON")) {

                VariableDeclaration decl = new VariableDeclaration(
                        tokens.get(i).value,
                        tokens.get(i+1).value,
                        tokens.get(i+3).value
                );
                declarations.add(decl);
            }
        }
    }

    public ArrayList<VariableDeclaration> getDeclarations() {
        return declarations;
    }
}
