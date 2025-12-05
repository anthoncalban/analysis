import java.util.*;
import javax.swing.*;

public class Lexer {
    private final JTextArea codeArea;
    private final JTextArea resultArea;
    private final JButton lexBtn;
    private final JButton synBtn;

    private final List<String> tokenList = new ArrayList<>();
    private boolean success = false;

    public Lexer(JTextArea codeArea, JTextArea resultArea, JButton lexBtn, JButton synBtn) {
        this.codeArea = codeArea;
        this.resultArea = resultArea;
        this.lexBtn = lexBtn;
        this.synBtn = synBtn;
    }

    public void performLexical() {
        tokenList.clear();
        resultArea.setText("");
        success = true;

        String[] lines = codeArea.getText().split("\n");

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i].trim();
            if (raw.isEmpty() || raw.startsWith("//")) continue;

            String stmt = raw.endsWith(";") ? raw.substring(0, raw.length()-1).trim() : raw.trim();
            String[] parts = stmt.split("\\s*=\\s*", 2);

            if (parts.length != 2) {
                success = false;
                continue;
            }

            String left = parts[0].trim();
            String right = parts[1].trim();

            if (Utils.classifyValue(right) == null) {
                resultArea.append("Lexical Error (Line " + (i+1) + "): Invalid literal '" + right + "'\n");
                success = false;
            }

            if (left.contains(" ")) {
                String[] decl = left.split("\\s+");
                if (decl.length >= 2) {
                    tokenList.add("<data_type> " + decl[0]);
                    for (int j = 1; j < decl.length; j++) {
                        if (!Utils.isValidId(decl[j])) {
                            resultArea.append("Lexical Error (Line " + (i+1) + "): Invalid identifier '" + decl[j] + "'\n");
                            success = false;
                        } else {
                            tokenList.add("<identifier> " + decl[j]);
                        }
                    }
                }
            } else {
                if (!Utils.isValidId(left)) {
                    resultArea.append("Lexical Error (Line " + (i+1) + "): Invalid identifier '" + left + "'\n");
                    success = false;
                } else {
                    tokenList.add("<identifier> " + left);
                }
            }

            tokenList.add("<assignment_operator> =");
            tokenList.add("<value> " + right);
            tokenList.add("<delimiter> ;");
        }

        if (success) {
            resultArea.setText("Lexical Analysis Successful!\n\nUnique Tokens:\n\n");
            new LinkedHashSet<>(tokenList).forEach(t -> resultArea.append(t + "\n"));
            synBtn.setEnabled(true);
            lexBtn.setEnabled(false);
        } else {
            resultArea.append("\nLexical Analysis Failed.\n");
        }
    }

    public boolean isSuccess() { return success; }
    public void enableButton() { synBtn.setEnabled(true); }
    public void clear() { tokenList.clear(); }
}