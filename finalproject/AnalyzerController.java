import java.io.*;
import javax.swing.*;

public class AnalyzerController {
    private JTextArea codeArea, resultArea;
    private JButton openButton, lexicalButton, syntaxButton, semanticButton, clearButton;

    private LexicalAnalyzer lexical = new LexicalAnalyzer();
    private SyntaxAnalyzer syntax = new SyntaxAnalyzer();
    private SemanticAnalyzer semantic = new SemanticAnalyzer();

    public AnalyzerController(JTextArea codeArea, JTextArea resultArea,
                              JButton openButton, JButton lexicalButton,
                              JButton syntaxButton, JButton semanticButton,
                              JButton clearButton) {
        this.codeArea = codeArea;
        this.resultArea = resultArea;
        this.openButton = openButton;
        this.lexicalButton = lexicalButton;
        this.syntaxButton = syntaxButton;
        this.semanticButton = semanticButton;
        this.clearButton = clearButton;
    }

    public void initListeners() {
        lexicalButton.setEnabled(false);
        syntaxButton.setEnabled(false);
        semanticButton.setEnabled(false);

        openButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = chooser.getSelectedFile();
                    String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                    codeArea.setText(content);
                    resultArea.setText("");
                    lexicalButton.setEnabled(true);
                    syntaxButton.setEnabled(false);
                    semanticButton.setEnabled(false);
                } catch (IOException ex) {
                    resultArea.setText("Error reading file.");
                }
            }
        });

        lexicalButton.addActionListener(e -> {
            lexical.analyze(codeArea.getText());
            resultArea.setText("Tokens:\n");
            for (LexicalAnalyzer.Token token : lexical.getTokens()) {
                resultArea.append(token.toString() + "\n");
            }
            lexicalButton.setEnabled(false);
            syntaxButton.setEnabled(true);
        });

        syntaxButton.addActionListener(e -> {
            syntax.analyze(lexical.getTokens());
            resultArea.setText("Declarations:\n");
            for (SyntaxAnalyzer.VariableDeclaration decl : syntax.getDeclarations()) {
                resultArea.append(decl.toString() + "\n");
            }
            syntaxButton.setEnabled(false);
            semanticButton.setEnabled(true);
        });

        semanticButton.addActionListener(e -> {
            semantic.analyze(syntax.getDeclarations());
            resultArea.setText("Semantic Check:\n");
            for (String error : semantic.getErrors()) {
                resultArea.append(error + "\n");
            }
            semanticButton.setEnabled(false);
        });

        clearButton.addActionListener(e -> {
            codeArea.setText("");
            resultArea.setText("");
            lexicalButton.setEnabled(false);
            syntaxButton.setEnabled(false);
            semanticButton.setEnabled(false);
        });
    }
}
