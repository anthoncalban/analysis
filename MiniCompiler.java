import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MiniCompiler extends JFrame {

    private final JTextArea codeArea = new JTextArea();
    private final JTextArea resultArea = new JTextArea();

    private final JButton openBtn = new JButton("Open File");
    private final JButton lexBtn  = new JButton("Lexical Analysis");
    private final JButton synBtn  = new JButton("Syntax Analysis");
    private final JButton semBtn  = new JButton("Semantic Analysis");
    private final JButton clearBtn = new JButton("Clear");

    private final Map<String, String> symbolTable = new HashMap<>();
    private final java.util.List<String> tokenList = new java.util.ArrayList<>();

    private static final Set<String> VALID_TYPES = Set.of(
        "byte", "short", "int", "long", "float", "double", "char", "boolean", "String"
    );

    public MiniCompiler() {
        initUI();
    }

    private void initUI() {
        setTitle("Mini Compiler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        top.setBackground(new Color(44, 62, 80));
        top.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        styleButton(openBtn,  new Color(52, 152, 219));
        styleButton(lexBtn,   new Color(231, 76,  60));
        styleButton(synBtn,   new Color(241, 196, 15));
        styleButton(semBtn,   new Color(155, 89,  182));
        styleButton(clearBtn, new Color(127, 140, 141));

        top.add(openBtn); top.add(lexBtn); top.add(synBtn); top.add(semBtn); top.add(clearBtn);
        add(top, BorderLayout.NORTH);

        codeArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        codeArea.setTabSize(4);
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(createTitledBorder(" Code Text Area ", new Color(41, 128, 185)));

        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(250, 255, 250));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(createTitledBorder(" Result Text Area ", new Color(39, 174, 96)));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codeScroll, resultScroll);
        split.setDividerLocation(620);
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);

        disableAnalysisButtons();

        openBtn.addActionListener(e -> openFile());
        lexBtn.addActionListener(e -> performLexical());
        synBtn.addActionListener(e -> performSyntax());
        semBtn.addActionListener(e -> performSemantic());
        clearBtn.addActionListener(e -> clearAll());

        setVisible(true);
    }

    private void styleButton(JButton b, Color c) {
        b.setPreferredSize(new Dimension(190, 55));
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setForeground(Color.WHITE);
        b.setBackground(c);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private TitledBorder createTitledBorder(String title, Color color) {
        return new TitledBorder(
            BorderFactory.createLineBorder(color, 3),
            title,
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 18),
            color
        );
    }

    private void openFile() {
        JFileChooser fc = new JFileChooser(".");
        fc.setFileFilter(new FileNameExtensionFilter("Source files", "txt", "mini"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String content = Files.readString(fc.getSelectedFile().toPath());
                codeArea.setText(content.trim());
                resultArea.setText("File loaded: " + fc.getSelectedFile().getName() + "\nReady for analysis.\n");
                tokenList.clear();
                symbolTable.clear();
                enableAnalysisButtons();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Cannot read file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performLexical() {
        tokenList.clear();
        resultArea.setText("");
        boolean ok = true;

        String[] lines = codeArea.getText().split("\n");

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i].trim();
            if (raw.isEmpty() || raw.startsWith("//")) continue;

            String stmt = raw.endsWith(";") ? raw.substring(0, raw.length()-1).trim() : raw.trim();
            String[] parts = stmt.split("\\s*=\\s*", 2);

            if (parts.length != 2) {
                ok = false;
                continue;
            }

            String left = parts[0].trim();
            String right = parts[1].trim();

            if (classifyValue(right) == null) {
                resultArea.append("Lexical Error (Line " + (i+1) + "): Invalid literal '" + right + "'\n");
                ok = false;
            }

            if (left.contains(" ")) {
                String[] decl = left.split("\\s+");
                if (decl.length >= 2) {
                    tokenList.add("<data_type> " + decl[0]);
                    for (int j = 1; j < decl.length; j++) {
                        if (!isValidId(decl[j])) {
                            resultArea.append("Lexical Error (Line " + (i+1) + "): Invalid identifier '" + decl[j] + "'\n");
                            ok = false;
                        } else {
                            tokenList.add("<identifier> " + decl[j]);
                        }
                    }
                }
            } else {
                if (!isValidId(left)) {
                    resultArea.append("Lexical Error (Line " + (i+1) + "): Invalid identifier '" + left + "'\n");
                    ok = false;
                } else {
                    tokenList.add("<identifier> " + left);
                }
            }

            tokenList.add("<assignment_operator> =");
            tokenList.add("<value> " + right);
            tokenList.add("<delimiter> ;");
        }

        if (ok) {
            resultArea.setText("Lexical Analysis Successful!\n\nUnique Tokens:\n\n");
            new LinkedHashSet<>(tokenList).forEach(t -> resultArea.append(t + "\n"));
            synBtn.setEnabled(true);
            lexBtn.setEnabled(false);
        } else {
            resultArea.append("\nLexical Analysis Failed.\n");
            disableAnalysisButtons();
        }
    }

    private void performSyntax() {
        resultArea.setText("");
        boolean ok = true;

        String[] lines = codeArea.getText().split("\n");

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i].trim();
            if (raw.isEmpty() || raw.startsWith("//")) continue;

            if (!raw.endsWith(";")) {
                resultArea.append("Syntax Error (Line " + (i+1) + "): Missing semicolon\n");
                ok = false;
                continue;
            }

            String stmt = raw.substring(0, raw.length()-1).trim();
            if (!stmt.contains("=")) {
                resultArea.append("Syntax Error (Line " + (i+1) + "): Missing assignment operator\n");
                ok = false;
                continue;
            }

            String[] parts = stmt.split("\\s*=\\s*", 2);
            String left = parts[0].trim();

            if (left.contains(" ")) {
                String[] decl = left.split("\\s+");
                if (decl.length != 2) {
                    resultArea.append("Syntax Error (Line " + (i+1) + "): Invalid declaration format\n");
                    ok = false;
                    continue;
                }
                if (!VALID_TYPES.contains(decl[0])) {
                    resultArea.append("Syntax Error (Line " + (i+1) + "): Unknown data type\n");
                    ok = false;
                }
            }
        }

        if (ok) {
            resultArea.setText("Syntax Analysis Successful!\nAll statements are grammatically correct.\n");
            semBtn.setEnabled(true);
            synBtn.setEnabled(false);
        } else {
            resultArea.append("\nSyntax Analysis Failed.\n");
            disableAnalysisButtons();
        }
    }

    private void performSemantic() {
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

            String valueType = classifyValue(right);
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
                if (!isCompatible(type, valueType, right)) {
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
                } else if (!isCompatible(type, valueType, right)) {
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
        disableAnalysisButtons();
    }

    // FIXED: Correct order and regex
    private String classifyValue(String v) {
        v = v.trim();

        if (v.startsWith("\"") && v.endsWith("\"")) {
            String content = v.substring(1, v.length()-1);
            if (isValidStringContent(content)) return "String";
        }

        if (v.startsWith("'") && v.endsWith("'") && v.length() >= 2) {
            String content = v.substring(1, v.length()-1);
            if (content.length() == 1 || isValidEscape(content)) return "char";
        }

        if (v.equals("true") || v.equals("false")) return "boolean";

        // Long with L/l
        if (v.matches("-?\\d+[lL]")) return "long";

        // Float/Double with f/F/d/D or decimal point
        if (v.matches("-?\\d*\\.?\\d+[fFdD]") || v.matches("-?\\d+\\.\\d*[fFdD]?")) {
            String lower = v.toLowerCase();
            return lower.endsWith("f") ? "float" : "double";
        }

        // Pure decimal → double
        if (v.matches("-?\\d*\\.\\d+")) return "double";

        // Pure integer → int
        if (v.matches("-?\\d+")) return "int";

        return null;
    }

    private boolean isValidStringContent(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\\') {
                if (i + 1 >= s.length()) return false;
                char next = s.charAt(i+1);
                if ("btnfr\"'\\".indexOf(next) == -1) return false;
                i++;
            }
        }
        return true;
    }

    private boolean isValidEscape(String s) {
        return s.length() == 1 || (s.length() == 2 && "btnfr\"'\\".indexOf(s.charAt(1)) != -1);
    }

    private boolean isCompatible(String declared, String actual, String literal) {
        if (declared.equals(actual)) return true;
        if (declared.equals("String") || actual.equals("String")) return declared.equals(actual);
        if (declared.equals("char") || actual.equals("char")) return declared.equals(actual);
        if (declared.equals("boolean") || actual.equals("boolean")) return declared.equals(actual);

        if (actual.equals("int")) {
            try {
                long val = Long.parseLong(literal);
                return switch (declared) {
                    case "byte"  -> val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE;
                    case "short" -> val >= Short.MIN_VALUE && val <= Short.MAX_VALUE;
                    case "int", "long", "float", "double" -> true;
                    default -> false;
                };
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return (declared.equals("long")   && actual.equals("int")) ||
               (declared.equals("float")  && actual.equals("int")) ||
               (declared.equals("double") && (actual.equals("int") || actual.equals("long") || actual.equals("float")));
    }

    private void clearAll() {
        codeArea.setText("");
        resultArea.setText("");
        tokenList.clear();
        symbolTable.clear();
        disableAnalysisButtons();
    }

    private void enableAnalysisButtons() {
        lexBtn.setEnabled(true);
        synBtn.setEnabled(false);
        semBtn.setEnabled(false);
    }

    private void disableAnalysisButtons() {
        lexBtn.setEnabled(false);
        synBtn.setEnabled(false);
        semBtn.setEnabled(false);
    }

    private boolean isValidId(String s) {
        return s != null && s.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MiniCompiler());
    }
}