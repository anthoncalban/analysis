import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class UIComponents {

    public static JPanel createTopPanel(JButton... buttons) {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        top.setBackground(new Color(44, 62, 80));
        top.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        styleButton(buttons[0], new Color(52, 152, 219));
        styleButton(buttons[1], new Color(231, 76, 60));
        styleButton(buttons[2], new Color(241, 196, 15));
        styleButton(buttons[3], new Color(155, 89, 182));
        styleButton(buttons[4], new Color(127, 140, 141));

        for (JButton b : buttons) top.add(b);
        return top;
    }

    public static JScrollPane createCodeScrollPane(JTextArea area) {
        area.setFont(new Font("Consolas", Font.PLAIN, 16));
        area.setTabSize(4);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(createTitledBorder(" Code Text Area ", new Color(41, 128, 185)));
        return scroll;
    }

    public static JScrollPane createResultScrollPane(JTextArea area) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        area.setEditable(false);
        area.setBackground(new Color(250, 255, 250));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(createTitledBorder(" Result Text Area ", new Color(39, 174, 96)));
        return scroll;
    }

    public static void styleButton(JButton b, Color c) {
        b.setPreferredSize(new Dimension(190, 55));
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setForeground(Color.WHITE);
        b.setBackground(c);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static TitledBorder createTitledBorder(String title, Color color) {
        return new TitledBorder(
            BorderFactory.createLineBorder(color, 3),
            title, TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 18), color
        );
    }

    public static void openFile(JTextArea codeArea, JTextArea resultArea, Runnable onSuccess) {
        JFileChooser fc = new JFileChooser(".");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Source files", "txt", "mini"));
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                String content = Files.readString(fc.getSelectedFile().toPath());
                codeArea.setText(content.trim());
                resultArea.setText("File loaded: " + fc.getSelectedFile().getName() + "\nReady for analysis.\n");
                onSuccess.run();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Cannot read file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}