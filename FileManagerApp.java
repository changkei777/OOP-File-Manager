package FileManagerApp;

// Final submission

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class FileManagerApp {

    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread (Best Practice)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Prof-Style OOP File Manager (Swing)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Add our Panel
            frame.add(new FileManagerPanel());
            
            // Set size and show
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }
}