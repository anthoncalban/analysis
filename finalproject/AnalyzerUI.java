import java.awt.*;
import javax.swing.*;

public class AnalyzerUI {

    private final JTextArea codeArea = new JTextArea(15, 50);
    private final JTextArea resultArea = new JTextArea(15, 50);

    private final JButton openBtn     = new JButton("Open File");
    private final JButton lexicalBtn  = new JButton("Lexical Analysis");
    private final JButton syntaxBtn   = new JButton("Syntax Analysis");
    private final JButton semanticBtn = new JButton("Semantic Analysis");
    private final JButton clearBtn    = new JButton("Clear");

    public void createAndShowGUI() {
        // Try to use Nimbus (looks best)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("COMPILER ANALYZER");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 750);
        frame.setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        main.setBackground(new Color(245, 247, 250));

        // === Left Panel - Buttons ===
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(Color.WHITE);
        left.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        left.setPreferredSize(new Dimension(220, 0));

        JLabel title = new JLabel("Analyzer Tools", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(50, 60, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        left.add(title);
        left.add(Box.createVerticalStrut(15));
        left.add(new JSeparator());
        left.add(Box.createVerticalStrut(30));

        Color gray = new Color(95, 105, 125);
        styleButton(openBtn,     gray);
        styleButton(lexicalBtn,  gray);
        styleButton(syntaxBtn,   gray);
        styleButton(semanticBtn, gray);
        styleButton(clearBtn,    gray);

        left.add(openBtn);     left.add(Box.createVerticalStrut(15));
        left.add(lexicalBtn);  left.add(Box.createVerticalStrut(15));
        left.add(syntaxBtn);   left.add(Box.createVerticalStrut(15));
        left.add(semanticBtn); left.add(Box.createVerticalStrut(15));
        left.add(clearBtn);    left.add(Box.createVerticalGlue());

        // === Right Panel - Text Areas ===
        JPanel right = new JPanel(new GridLayout(2, 1, 0, 20));
        right.setBackground(main.getBackground());
        right.add(createTextPanel("Code Text Area",  codeArea,  true));
        right.add(createTextPanel("Result Text Area", resultArea, false));

        main.add(left, BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);
        frame.add(main);
        frame.setVisible(true);

        // Connect controller (you already have this class)
        new AnalyzerController(codeArea, resultArea, openBtn, lexicalBtn, syntaxBtn, semanticBtn, clearBtn)
            .initListeners();
    }

    private JPanel createTextPanel(String title, JTextArea ta, boolean editable) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 215)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(40, 50, 70));

        ta.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        ta.setTabSize(4);
        ta.setEditable(editable);
        ta.setBackground(new Color(252, 254, 255));
        ta.setForeground(new Color(30, 30, 40));
        ta.setCaretColor(new Color(70, 130, 255));

        p.add(lbl, BorderLayout.NORTH);
        p.add(new JScrollPane(ta), BorderLayout.CENTER);
        return p;
    }

    private void styleButton(JButton b, Color bg) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setMaximumSize(new Dimension(190, 48));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(bg); }
        });
    }
}