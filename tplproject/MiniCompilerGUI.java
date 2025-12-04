import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
// Removed unused/ambiguous imports

public class MiniCompilerGUI extends JFrame {

    // GUI Components
    private JTextArea sourceArea;
    private JTextArea resultArea;
    private JLabel statusLabel;
    
    // State Management
    private String sourceCode = "";
    // Use fully qualified name to avoid collision with java.awt.List
    private java.util.List<Lexer.Token> tokens = null;
    private Parser.ASTNode astRoot = null;
    
    // Flags for Pipeline Enforcement
    private boolean isLexed = false;
    private boolean isParsed = false;

    public MiniCompilerGUI() {
        setTitle("Mini Compiler - Integrated Environment");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Left Control Panel ---
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(6, 1, 10, 10));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        controlPanel.setPreferredSize(new Dimension(200, 0));
        controlPanel.setBackground(new Color(230, 230, 230));

        JButton btnFile = createStyledButton("Choose File");
        JButton btnLex = createStyledButton("Lexical Analysis");
        JButton btnSyn = createStyledButton("Syntax Analysis");
        JButton btnSem = createStyledButton("Semantic Analysis");
        JButton btnClear = createStyledButton("Clear");

        controlPanel.add(btnFile);
        controlPanel.add(new JSeparator()); // Spacer
        controlPanel.add(btnLex);
        controlPanel.add(btnSyn);
        controlPanel.add(btnSem);
        controlPanel.add(btnClear);

        // --- Main Split Pane (Results Top / Source Bottom) ---
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBorder(BorderFactory.createTitledBorder("Analysis Results"));

        sourceArea = new JTextArea();
        sourceArea.setEditable(true);
        sourceArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        sourceArea.setBorder(BorderFactory.createTitledBorder("Source Code"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(resultArea), new JScrollPane(sourceArea));
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        // --- Status Bar ---
        statusLabel = new JLabel(" Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());

        add(controlPanel, BorderLayout.WEST);
        add(splitPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // --- Action Listeners ---

        btnFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    sourceCode = Files.readString(file.toPath());
                    sourceArea.setText(sourceCode);
                    resetState();
                    log("File loaded: " + file.getName());
                } catch (IOException ex) {
                    showError("Error reading file: " + ex.getMessage());
                }
            }
        });

        btnLex.addActionListener(e -> {
            updateSourceFromArea();
            if (sourceCode.trim().isEmpty()) {
                showError("No source code to analyze.");
                return;
            }
            try {
                tokens = Lexer.lex(sourceCode);
                StringBuilder sb = new StringBuilder("=== LEXICAL ANALYSIS SUCCESS ===\n\n");
                for (Lexer.Token t : tokens) sb.append(t).append("\n");
                resultArea.setText(sb.toString());
                isLexed = true;
                isParsed = false; // Invalidate subsequent steps
                astRoot = null;
                statusLabel.setText(" Lexical analysis complete.");
            } catch (Exception ex) {
                showError("Lexical Error: " + ex.getMessage());
            }
        });

        btnSyn.addActionListener(e -> {
            if (!isLexed) {
                showError("Please run Lexical Analysis first.");
                return;
            }
            try {
                Parser parser = new Parser(tokens);
                astRoot = parser.parse();
                resultArea.setText("=== SYNTAX ANALYSIS SUCCESS ===\n\nAST Generated Successfully.\nRoot Node: " + astRoot.type);
                isParsed = true;
                statusLabel.setText(" Syntax analysis complete.");
            } catch (Exception ex) {
                showError("Syntax Error: " + ex.getMessage());
            }
        });

        btnSem.addActionListener(e -> {
            if (!isParsed) {
                showError("Please run Syntax Analysis first.");
                return;
            }
            try {
                SemanticAnalyzer analyzer = new SemanticAnalyzer();
                analyzer.analyze(astRoot);
                resultArea.setText("=== SEMANTIC ANALYSIS SUCCESS ===\n\nNo semantic errors found.\nCode is valid and type-safe.");
                statusLabel.setText(" Semantic analysis complete.");
            } catch (Exception ex) {
                showError("Semantic Error: " + ex.getMessage());
            }
        });

        btnClear.addActionListener(e -> {
            sourceArea.setText("");
            resultArea.setText("");
            resetState();
            statusLabel.setText(" Cleared.");
        });
    }

    private void updateSourceFromArea() {
        sourceCode = sourceArea.getText();
    }

    private void resetState() {
        tokens = null;
        astRoot = null;
        isLexed = false;
        isParsed = false;
        sourceCode = "";
    }

    private void log(String msg) {
        resultArea.append(msg + "\n");
    }

    private void showError(String msg) {
        resultArea.setText("ERROR:\n" + msg);
        statusLabel.setText(" Error occurred.");
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MiniCompilerGUI().setVisible(true));
    }
}