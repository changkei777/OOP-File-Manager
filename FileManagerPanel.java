package FileManagerApp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class FileManagerPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Dependencies
    private IFileService fileService;
    private File currentDirectory;

    // UI Components
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JLabel pathLabel;
    private JLabel statusLabel;

    public FileManagerPanel() {
        // Initialize Logic
        this.fileService = new FileService();
        this.currentDirectory = new File(System.getProperty("user.home"));

        // --- IMPROVEMENT: Cleaner Layout & Fonts ---
        this.setLayout(new BorderLayout(15, 15)); // Add gaps between sections
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around edge
        this.setBackground(new Color(245, 245, 245)); // Light gray background

        setupTopBar();
        setupCenterList();
        setupBottomBar();
        
        refreshFileList();
    }

    private void setupTopBar() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false); // Make transparent to show background

        // Navigation Buttons
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        navButtons.setOpaque(false);
        
        JButton btnHome = createStyledButton("🏠 Home");
        JButton btnUp = createStyledButton("⬆ Up");

        btnHome.addActionListener(e -> navigateTo(new File(System.getProperty("user.home"))));
        btnUp.addActionListener(e -> navigateUp());

        navButtons.add(btnHome);
        navButtons.add(btnUp);

        // Path Label (Styled nicely)
        pathLabel = new JLabel();
        pathLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pathLabel.setForeground(new Color(50, 50, 50));
        pathLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        topPanel.add(navButtons, BorderLayout.WEST);
        topPanel.add(pathLabel, BorderLayout.CENTER);
        
        this.add(topPanel, BorderLayout.NORTH);
    }

    private void setupCenterList() {
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        
        // --- IMPROVEMENT: Better List Styling ---
        fileList.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Emoji-compatible font
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setFixedCellHeight(25); // Give items breathing room
        
        // Double Click Action
        fileList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openSelected();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void setupBottomBar() {
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton btnOpen = createStyledButton("📂 Open");
        JButton btnNew = createStyledButton("➕ New Folder");
        JButton btnDelete = createStyledButton("❌ Delete");

        btnOpen.addActionListener(e -> openSelected());
        btnNew.addActionListener(e -> createFolder());
        btnDelete.addActionListener(e -> deleteSelected());

        buttonPanel.add(btnOpen);
        buttonPanel.add(btnNew);
        buttonPanel.add(btnDelete);

        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- Helper to Create Nice Buttons ---
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(Color.WHITE);
        return btn;
    }

    // --- Logic Methods ---

    private void refreshFileList() {
        pathLabel.setText("📍 " + currentDirectory.getAbsolutePath());
        listModel.clear();
        
        File[] files = fileService.getFilesInDirectory(currentDirectory);
        if (files != null) {
            for (File f : files) {
                String icon = getIconForFile(f);
                listModel.addElement(icon + " " + f.getName());
            }
        }
        statusLabel.setText("Status: Loaded " + listModel.getSize() + " items.");
    }

    /**
     * Determines the emoji icon based on file type.
     */
    private String getIconForFile(File f) {
        if (f.isDirectory()) {
            return "📁";
        } 
        
        String name = f.getName().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif")) {
            return "🖼️";
        } else if (name.endsWith(".txt") || name.endsWith(".doc") || name.endsWith(".pdf")) {
            return "📄";
        } else if (name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".mov")) {
            return "🎬";
        } else if (name.endsWith(".mp3") || name.endsWith(".wav")) {
            return "🎵";
        } else if (name.endsWith(".zip") || name.endsWith(".rar")) {
            return "📦";
        } else if (name.endsWith(".java") || name.endsWith(".class")) {
            return "☕";
        }
        
        return "⬜"; // Default file icon
    }

    private void navigateTo(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            currentDirectory = dir;
            refreshFileList();
        }
    }

    private void navigateUp() {
        File parent = currentDirectory.getParentFile();
        if (parent != null) navigateTo(parent);
    }

    private void openSelected() {
        String selected = fileList.getSelectedValue();
        if (selected == null) return;

        // Remove the emoji and space (first 2 or 3 chars) to get name
        // Simple hack: find the first space and take everything after it
        int firstSpace = selected.indexOf(' ');
        String fileName = selected.substring(firstSpace + 1);
        
        File target = new File(currentDirectory, fileName);

        if (target.isDirectory()) {
            navigateTo(target);
        } else {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(target);
                    statusLabel.setText("Status: Opened " + fileName);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Could not open file.");
            }
        }
    }

    private void deleteSelected() {
        String selected = fileList.getSelectedValue();
        if (selected == null) return;

        int firstSpace = selected.indexOf(' ');
        String fileName = selected.substring(firstSpace + 1);
        File target = new File(currentDirectory, fileName);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Delete " + fileName + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (fileService.deleteFile(target)) {
                refreshFileList();
                statusLabel.setText("Status: Deleted " + fileName);
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not delete file.");
            }
        }
    }

    private void createFolder() {
        String name = JOptionPane.showInputDialog(this, "Enter folder name:");
        if (name != null && !name.trim().isEmpty()) {
            if (fileService.createDirectory(currentDirectory, name)) {
                refreshFileList();
                statusLabel.setText("Status: Created folder " + name);
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not create folder.");
            }
        }
    }
}