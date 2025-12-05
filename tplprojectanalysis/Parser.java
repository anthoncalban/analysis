import java.util.Set;
import javax.swing.*;

public class Parser {
    private final JTextArea codeArea;
    private final JTextArea resultArea;
    private final JButton synBtn;
    private final JButton semBtn;

    private static final Set<String> VALID_TYPES = Set.of(
        "byte", "short", "int", "long", "float", "double", "char", "boolean", "String"
    );

    private boolean success = false;

    public Parser(JTextArea codeArea, JTextArea resultArea, JButton synBtn, JButton semBtn) {
        this.codeArea = codeArea;
        this.resultArea = resultArea;
        this.synBtn = synBtn;
        this.semBtn = semBtn;
    }

    public void performSyntax() {
        resultArea.setText("");
        success = true;

        String[] lines = codeArea.getText().split("\n");

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i].trim();
            if (raw.isEmpty() || raw.startsWith("//")) continue;

            if (!raw.endsWith(";")) {
                resultArea.append("Syntax Error (Line " + (i+1) + "): Missing semicolon\n");
                success = false;
                continue;
            }

            String stmt = raw.substring(0, raw.length()-1).trim();
            if (!stmt.contains("=")) {
                resultArea.append("Syntax Error (Line " + (i+1) + "): Missing assignment operator\n");
                success = false;
                continue;
            }

            String[] parts = stmt.split("\\s*=\\s*", 2);
            String left = parts[0].trim();

            if (left.contains(" ")) {
                String[] decl = left.split("\\s+");
                if (decl.length != 2) {
                    resultArea.append("Syntax Error (Line " + (i+1) + "): Invalid declaration format\n");
                    success = false;
                    continue;
                }
                if (!VALID_TYPES.contains(decl[0])) {
                    resultArea.append("Syntax Error (Line " + (i+1) + "): Unknown data type\n");
                    success = false;
                }
            }
        }

        if (success) {
            resultArea.setText("Syntax Analysis Successful!\nAll statements are grammatically correct.\n");
            semBtn.setEnabled(true);
            synBtn.setEnabled(false);
        } else {
            resultArea.append("\nSyntax Analysis Failed.\n");
        }
    }

    public boolean isSuccess() { return success; }
    public void enableButton() { semBtn.setEnabled(true); }
    public void clear() {}
}
