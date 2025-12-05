import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class SemanticAnalyzer {
    private final JTextArea codeArea;
    private final JTextArea resultArea;
    private final Map<String, String> symbolTable = new HashMap<>();

    private final JButton semBtn;

    public SemanticAnalyzer(JTextArea codeArea, JTextArea resultArea) {
        this.codeArea = codeArea;
        this.resultArea = resultArea;
        this.semBtn = null;
    }

    public void performSemantic() {
        symbolTable.clear();
        resultArea.setText("");
        boolean ok = true;

        String[] lines = codeArea.getText().split("\n");

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i].trim();
            if (raw.isEmpty() || raw.startsWith("//")) continue;

            String stmt = raw.endsWith(";") ? raw.substring(0, raw.length()-1).trim() : raw.trim();
            String[] parts = stmt.split("\\s*=\\s*", 2);
            if (parts.length != 2) continue;

            String left = parts[0].trim();
            String right = parts[1].trim();

            String valueType = Utils.classifyValue(right);
            if (valueType == null) continue;

            if (left.contains(" ")) {
                String[] d = left.split("\\s+");
                String type = d[0];
                String name = d[1];

                if (symbolTable.containsKey(name)) {
                    resultArea.append("Semantic Error (Line " + (i+1) + "): Redeclaration\n");
                    ok = false;
                    continue;
                }
                if (!Utils.isCompatible(type, valueType, right)) {
                    resultArea.append("Semantic Error (Line " + (i+1) + "): Type mismatch\n");
                    ok = false;
                } else {
                    symbolTable.put(name, type);
                }
            } else {
                String name = left;
                String type = symbolTable.get(name);
                if (type == null) {
                    resultArea.append("Semantic Error (Line " + (i+1) + "): Variable not declared\n");
                    ok = false;
                } else if (!Utils.isCompatible(type, valueType, right)) {
                    resultArea.append("Semantic Error (Line " + (i+1) + "): Type mismatch\n");
                    ok = false;
                }
            }
        }

        if (ok) {
            resultArea.setText("Semantic Analysis Successful!\n\nSymbol Table:\n\n");
            symbolTable.forEach((n, t) -> resultArea.append(String.format("  %-15s → %s\n", n, t)));
            resultArea.append("\nCompilation completed successfully!\n");
        } else {
            resultArea.append("\nSemantic Analysis Failed.\n");
        }
    }

    public void enableButton() { /* if you pass semBtn here, enable it */ }
    public void clear() { symbolTable.clear(); }
}