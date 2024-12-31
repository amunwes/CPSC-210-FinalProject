package ui;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) throws Exception {
        // new ConsoleInterface();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainAppPanel().setVisible(true);
            }
        });
    }
}
