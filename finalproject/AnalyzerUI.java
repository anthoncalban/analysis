import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class AnalyzerUI {
    private JTextArea codeArea = new JTextArea(20, 50);
    private JTextArea resultArea = new JTextArea(20, 50);
    private JButton openButton = new JButton("Open File");
    private JButton lexicalButton = new JButton("Lexical Analysis");
    private JButton syntaxButton = new JButton("Syntax Analysis");
    private JButton semanticButton = new JButton("Semantic Analysis");
    private JButton clearButton = new JButton("Clear");

    private AnalyzerController controller;

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Compiler Analyzer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);

        Font font = new Font("Consolas", Font.PLAIN, 14);
        codeArea.setFont(font);
        resultArea.setFont(font);
        codeArea.setLineWrap(true);
        resultArea.setLineWrap(true);

        // Scroll panes with titled borders
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(new TitledBorder("Source Code"));

        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(new TitledBorder("Result"));

        // Vertical button panel on the left
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(openButton);
        buttonPanel.add(lexicalButton);
        buttonPanel.add(syntaxButton);
        buttonPanel.add(semanticButton);
        buttonPanel.add(clearButton);

        // Right panel with code and result areas stacked
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        textPanel.add(codeScroll);
        textPanel.add(resultScroll);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(buttonPanel, BorderLayout.WEST);
        mainPanel.add(textPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);

        controller = new AnalyzerController(codeArea, resultArea, openButton, lexicalButton, syntaxButton, semanticButton, clearButton);
        controller.initListeners();
    }
}
