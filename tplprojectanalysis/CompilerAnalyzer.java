import java.awt.*;
import javax.swing.*;

public class CompilerAnalyzer extends JFrame {
    private final JTextArea codeArea = new JTextArea();
    private final JTextArea resultArea = new JTextArea();

    private final JButton openBtn = new JButton("Open File");
    private final JButton lexBtn  = new JButton("Lexical Analysis");
    private final JButton synBtn  = new JButton("Syntax Analysis");
    private final JButton semBtn  = new JButton("Semantic Analysis");
    private final JButton clearBtn = new JButton("Clear");

    private final Lexer lexer = new Lexer(codeArea, resultArea, lexBtn, synBtn);
    private final Parser parser = new Parser(codeArea, resultArea, synBtn, semBtn);
    private final SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(codeArea, resultArea);

    public CompilerAnalyzer() {
        initUI();
        setupActions();
        setVisible(true);
    }

    private void initUI() {
        setTitle("Compiler Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = UIComponents.createTopPanel(openBtn, lexBtn, synBtn, semBtn, clearBtn);
        add(top, BorderLayout.NORTH);

        JScrollPane codeScroll = UIComponents.createCodeScrollPane(codeArea);
        JScrollPane resultScroll = UIComponents.createResultScrollPane(resultArea);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codeScroll, resultScroll);
        split.setDividerLocation(620);
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);

        disableAnalysisButtons();
    }

    private void setupActions() {
        openBtn.addActionListener(e -> UIComponents.openFile(codeArea, resultArea, this::enableAnalysisButtons));
        lexBtn.addActionListener(e -> {
            lexer.performLexical();
            if (lexer.isSuccess()) {
                parser.enableButton();
                disableAnalysisButtonsExceptLex();
            }
        });
        synBtn.addActionListener(e -> {
            parser.performSyntax();
            if (parser.isSuccess()) {
                semanticAnalyzer.enableButton();
            }
        });
        semBtn.addActionListener(e -> semanticAnalyzer.performSemantic());
        clearBtn.addActionListener(e -> {
            codeArea.setText("");
            resultArea.setText("");
            lexer.clear();
            parser.clear();
            semanticAnalyzer.clear();
            disableAnalysisButtons();
        });
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

    private void disableAnalysisButtonsExceptLex() {
        lexBtn.setEnabled(false);
        synBtn.setEnabled(true);
        semBtn.setEnabled(false);
    }
}