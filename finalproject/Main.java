import java.awt.*;
import java.io.*;
import javax.swing.*;

public class Main extends JFrame {

    private JTextArea codeArea, resultArea;
    private JButton openFileBtn, lexicalBtn, syntaxBtn, semanticBtn, outputBtn, clearBtn;

    public Main() {
        setTitle("Mini Compiler - Lab04");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
        layoutComponents();
        attachListeners();

        setVisible(true);
    }

    private void initComponents() {
        Font monoFont = new Font("Monospaced", Font.PLAIN, 14);

        openFileBtn = new JButton("📂 Open File");
        lexicalBtn = new JButton("🔍 Lexical Analysis");
        syntaxBtn = new JButton("📝 Syntax Analysis");
        semanticBtn = new JButton("🧠 Semantic Analysis");
        outputBtn = new JButton("📤 Generate Output");
        clearBtn = new JButton("🧹 Clear");

        lexicalBtn.setEnabled(false);
        syntaxBtn.setEnabled(false);
        semanticBtn.setEnabled(false);
        outputBtn.setEnabled(false);

        codeArea = new JTextArea();
        codeArea.setEditable(false);
        codeArea.setFont(monoFont);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(monoFont);
        resultArea.setBackground(new Color(245, 245, 245));
    }

    private void layoutComponents() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(6, 1, 10, 10));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(openFileBtn);
        leftPanel.add(lexicalBtn);
        leftPanel.add(syntaxBtn);
        leftPanel.add(semanticBtn);
        leftPanel.add(outputBtn);
        leftPanel.add(clearBtn);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(resultArea),
                new JScrollPane(codeArea));
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.4);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(new JLabel("Result:"), BorderLayout.NORTH);
        rightPanel.add(splitPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private void attachListeners() {
        openFileBtn.addActionListener(e -> openFile());
        lexicalBtn.addActionListener(e -> runLexical());
        syntaxBtn.addActionListener(e -> runSyntax());
        semanticBtn.addActionListener(e -> runSemantic());
        outputBtn.addActionListener(e -> runOutput());
        clearBtn.addActionListener(e -> clearAll());
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
                codeArea.read(br, null);
                lexicalBtn.setEnabled(true);
                syntaxBtn.setEnabled(false);
                semanticBtn.setEnabled(false);
                outputBtn.setEnabled(false);
                resultArea.setText("✅ File Loaded. Ready for Lexical Analysis.");
            } catch (IOException ex) {
                resultArea.setText("❌ Error: Cannot open file.");
            }
        }
    }

    private void runLexical() {
        resultArea.setText("🔍 Running Lexical Analysis...\n\n");
        LexicalAnalysis analyzer = new LexicalAnalysis(codeArea.getText());
        resultArea.append(analyzer.analyze());
        syntaxBtn.setEnabled(true);
        resultArea.append("\n✅ Lexical Analysis Complete. Syntax Analysis enabled.");
    }

    private void runSyntax() {
        resultArea.append("\n\n📝 Syntax Analysis not implemented yet.");
        semanticBtn.setEnabled(true);
    }

    private void runSemantic() {
        resultArea.append("\n\n🧠 Running Semantic Analysis...\n\n");
        SemanticAnalysis analyzer = new SemanticAnalysis(codeArea.getText());
        resultArea.append(analyzer.analyze());
        outputBtn.setEnabled(true);
        semanticBtn.setEnabled(false);
    }

    private void runOutput() {
        resultArea.append("\n\n📤 Output Generator not implemented yet.");
    }

    private void clearAll() {
        codeArea.setText("");
        resultArea.setText("");
        lexicalBtn.setEnabled(false);
        syntaxBtn.setEnabled(false);
        semanticBtn.setEnabled(false);
        outputBtn.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
