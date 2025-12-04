public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            AnalyzerUI ui = new AnalyzerUI();
            ui.createAndShowGUI();
        });
    }
}
