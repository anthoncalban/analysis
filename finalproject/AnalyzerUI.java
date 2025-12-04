import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AnalyzerUI {
    private JTextArea codeArea = new JTextArea(15, 50);
    private JTextArea resultArea = new JTextArea(15, 50);
    private JButton openButton = new JButton("Open File");
    private JButton lexicalButton = new JButton("Lexical Analysis");
    private JButton syntaxButton = new JButton("Syntax Analysis");
    private JButton semanticButton = new JButton("Semantic Analysis");
    private JButton clearButton = new JButton("Clear");

    private AnalyzerController controller;

    public void createAndShowGUI() {
        // Set Nimbus Look and Feel for modern appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback to default
        }

        JFrame frame = new JFrame("Variable Declaration Analyzer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 750);
        frame.setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 247, 250));

        // === LEFT PANEL: Buttons ===
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(255, 255, 255));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        leftPanel.setPreferredSize(new Dimension(220, 0));

        JLabel titleLabel = new JLabel("Analyzer Tools");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 60, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(180, 10));
        separator.setForeground(new Color(200, 200, 200));

        // Style buttons
        styleButton(openButton, new Color(70, 130, 255), Color.WHITE);     // Blue
        styleButton(lexicalButton, new Color(52, 199, 89), Color.WHITE);    // Green
        styleButton(syntaxButton, new Color(255, 149, 0), Color.WHITE);     // Orange
        styleButton(semanticButton, new Color(153, 51, 255), Color.WHITE);  // Purple
        styleButton(clearButton, new Color(255, 77, 77), Color.WHITE);      // Red

        // Add spacing
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(separator);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(openButton);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(lexicalButton);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(syntaxButton);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(semanticButton);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(clearButton);
        leftPanel.add(Box.createVerticalGlue());

        // === RIGHT PANEL: Code & Result Areas ===
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        rightPanel.setBackground(new Color(245, 247, 250));

        // Code Area Panel
        JPanel codePanel = createTextAreaPanel("Code Text Area", codeArea, true);
        JPanel resultPanel = createTextAreaPanel("Result Text Area", resultArea, false);

        rightPanel.add(codePanel);
        rightPanel.add(resultPanel);

        // Add panels to main
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);

        // Initialize controller after UI is built
        controller = new AnalyzerController(codeArea, resultArea, openButton, lexicalButton,
                syntaxButton, semanticButton, clearButton);
        controller.initListeners();
    }

    private JPanel createTextAreaPanel(String title, JTextArea textArea, boolean editable) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(40, 50, 70));

        textArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        textArea.setTabSize(4);
        textArea.setEditable(editable);
        textArea.setBackground(new Color(252, 254, 255));
        textArea.setForeground(new Color(30, 30, 40));
        textArea.setCaretColor(new Color(70, 130, 255));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(180, 45));
        button.setPreferredSize(new Dimension(180, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
}