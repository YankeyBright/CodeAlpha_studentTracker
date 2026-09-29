import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern, minimalist, beginner-friendly Desktop GUI for Student Grade Tracker.
 * Allows teachers to enter student grades and automatically calculates average,
 * highest, and lowest scores.
 *
 * Built using pure Java Swing with zero external dependencies.
 */
@SuppressWarnings("serial")
public final class StudentTrackerGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    // Handles saving and loading student records from disk
    private final StudentStorage storage;

    // Current list of students displayed in the application
    private final List<Student> studentList = new ArrayList<>();

    // Color Palette: Clean slate, white cards, and clear accent colors
    public static final Color BG_APP = new Color(0xF8, 0xFA, 0xFC);
    public static final Color BG_CARD = Color.WHITE;
    public static final Color PRIMARY = new Color(0x0F, 0x17, 0x2A);
    public static final Color PRIMARY_HOVER = new Color(0x33, 0x41, 0x55);
    public static final Color TEXT_MAIN = new Color(0x0F, 0x17, 0x2A);
    public static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8B);
    public static final Color BORDER = new Color(0xE2, 0xE8, 0xF0);
    public static final Color SUCCESS = new Color(0x05, 0x96, 0x69);
    public static final Color DANGER = new Color(0xDC, 0x26, 0x26);
    public static final Color ACCENT = new Color(0x02, 0x84, 0xC7);

    // Top Metric Card Labels (updated live when grades change)
    private JLabel kpiAvgLabel;
    private JLabel kpiHighLabel;
    private JLabel kpiLowLabel;
    private JLabel kpiCountLabel;

    // Input fields for teacher entry
    private JTextField nameField;
    private JTextField gradesField;
    private JTextField searchField;

    // Table and Table Data Model
    private StudentTableModel tableModel;
    private JTable studentTable;

    // Main window setup
    @SuppressWarnings("this-escape")
    public StudentTrackerGUI() {
        super("Student Grade Tracker - CodeAlpha Task 1");
        this.storage = new StudentStorage();

        // Standard window settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(880, 580));
        setLocationRelativeTo(null); // center window on user screen

        // Load saved students from CSV
        studentList.addAll(storage.loadStudents());

        // Root container panel with clean slate background
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);
        setContentPane(root);

        // 1. Top Header Bar (App Title & Status Indicator)
        root.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Main Content Area (Cards + Form + Table)
        JPanel centerContainer = new JPanel(new BorderLayout(0, 16));
        centerContainer.setBackground(BG_APP);
        centerContainer.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Top Row: 4 Metric Cards for quick statistical overview
        centerContainer.add(createKpiBanner(), BorderLayout.NORTH);

        // Center Split: Left Input Form + Right Student Table
        JPanel workspace = new JPanel(new BorderLayout(16, 0));
        workspace.setOpaque(false);
        workspace.add(createFormSidebar(), BorderLayout.WEST);
        workspace.add(createTablePanel(), BorderLayout.CENTER);

        centerContainer.add(workspace, BorderLayout.CENTER);
        root.add(centerContainer, BorderLayout.CENTER);

        // 3. Bottom Status Bar (Shows file location and info)
        root.add(createStatusBar(), BorderLayout.SOUTH);

        // Refresh all table data and calculation cards
        refreshData();
    }

    // ==================== TOP HEADER BAR ====================

    // Creates the top navigation and title bar
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(14, 24, 14, 24)
        ));

        // Title and description on the left
        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setOpaque(false);

        JLabel brand = new JLabel("STUDENT GRADE TRACKER");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(PRIMARY);

        JLabel sub = new JLabel("Enter student grades and calculate average, highest, and lowest scores");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);

        leftBox.add(brand);
        leftBox.add(Box.createVerticalStrut(2));
        leftBox.add(sub);
        header.add(leftBox, BorderLayout.WEST);

        // Beginner-friendly system active status badge on the right
        JLabel status = new JLabel("\u25CF SYSTEM ACTIVE");
        status.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.setForeground(SUCCESS);
        header.add(status, BorderLayout.EAST);

        return header;
    }

    // ==================== TOP METRIC CARDS ====================

    // Creates the 4 real-time metric cards at the top
    private JPanel createKpiBanner() {
        JPanel banner = new JPanel(new GridLayout(1, 4, 14, 0));
        banner.setOpaque(false);

        kpiCountLabel = new JLabel("0 Students");
        kpiAvgLabel = new JLabel("0.0%");
        kpiHighLabel = new JLabel("0.0");
        kpiLowLabel = new JLabel("0.0");

        // Card 1: Total Students count
        banner.add(createKpiCard("TOTAL STUDENTS", kpiCountLabel, "Total students added", PRIMARY));

        // Card 2: Overall Class Average score
        banner.add(createKpiCard("CLASS AVERAGE", kpiAvgLabel, "Overall class average", ACCENT));

        // Card 3: Top score in class
        banner.add(createKpiCard("HIGHEST SCORE", kpiHighLabel, "Highest score in class", SUCCESS));

        // Card 4: Lowest score in class
        banner.add(createKpiCard("LOWEST SCORE", kpiLowLabel, "Lowest score in class", DANGER));

        return banner;
    }

    // Helper to build a clean individual metric card
    private JPanel createKpiCard(String title, JLabel valueLabel, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(createCardBorder());

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        s.setForeground(TEXT_MUTED);

        card.add(t, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(s, BorderLayout.SOUTH);

        return card;
    }

    // ==================== LEFT INPUT SIDEBAR (FORM) ====================

    // Creates the sidebar where teachers enter or edit student grades
    private JPanel createFormSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 16));
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(createCardBorder());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setOpaque(false);

        JLabel formTitle = new JLabel("STUDENT FORM");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formTitle.setForeground(PRIMARY);

        JLabel formSub = new JLabel("Enter or update student grades below");
        formSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        formSub.setForeground(TEXT_MUTED);

        // Input text fields (comfortable 42px height)
        nameField = createStyledTextField();
        gradesField = createStyledTextField();

        formContent.add(formTitle);
        formContent.add(Box.createVerticalStrut(2));
        formContent.add(formSub);
        formContent.add(Box.createVerticalStrut(18));

        // Student name input row
        formContent.add(createFormField("Student Name:", nameField));
        formContent.add(Box.createVerticalStrut(14));

        // Grades comma-separated input row
        formContent.add(createFormField("Grades (comma-separated, e.g. 85, 92, 78):", gradesField));
        formContent.add(Box.createVerticalStrut(20));

        // Action CTA Buttons with increased comfortable 44px height
        JButton saveBtn = createPrimaryButton("Save Student");
        saveBtn.addActionListener(e -> handleSaveStudent());

        JButton clearBtn = createSecondaryButton("Clear Form");
        clearBtn.addActionListener(e -> clearInputs());

        JButton deleteBtn = createDangerButton("Delete Student");
        deleteBtn.addActionListener(e -> handleDeleteStudent());

        // Container holding the 3 CTA buttons with 10px vertical spacing
        JPanel btnBox = new JPanel(new GridLayout(3, 1, 0, 10));
        btnBox.setOpaque(false);
        btnBox.setPreferredSize(new Dimension(280, 150)); // Generous CTA container height
        btnBox.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));
        btnBox.add(saveBtn);
        btnBox.add(clearBtn);
        btnBox.add(deleteBtn);

        formContent.add(btnBox);
        sidebar.add(formContent, BorderLayout.NORTH);

        return sidebar;
    }

    // Helper to package a label and text field together
    private JPanel createFormField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_MAIN);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    // ==================== RIGHT TABLE PANEL (STUDENT LIST) ====================

    // Creates the student table with real-time search toolbar
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // Search and Action Bar
        JPanel searchBar = new JPanel(new BorderLayout(12, 0));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(createCardBorder());

        // Search text field (matches 44px CTA height)
        searchField = createStyledTextField();
        searchField.setPreferredSize(new Dimension(250, 44));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(); }

            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(); }

            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        // Left side of toolbar: search icon & search input box
        JPanel searchLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        searchLeft.setOpaque(false);
        JLabel searchIcon = new JLabel("Search Student:");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchIcon.setForeground(TEXT_MAIN);
        searchLeft.add(searchIcon);
        searchLeft.add(searchField);

        // Right side of toolbar: View Summary Report CTA button (44px height)
        JButton summaryBtn = createSecondaryButton("View Summary Report");
        summaryBtn.setPreferredSize(new Dimension(190, 44));
        summaryBtn.addActionListener(e -> handleViewSummaryReport());

        searchBar.add(searchLeft, BorderLayout.WEST);
        searchBar.add(summaryBtn, BorderLayout.EAST);
        panel.add(searchBar, BorderLayout.NORTH);

        // Table setup
        tableModel = new StudentTableModel();
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(38); // generous row height for readability
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(new Color(0xF1, 0xF5, 0xF9));
        studentTable.getTableHeader().setForeground(TEXT_MAIN);
        studentTable.getTableHeader().setPreferredSize(new Dimension(0, 38));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setShowVerticalLines(false);
        studentTable.setShowHorizontalLines(true);
        studentTable.setGridColor(new Color(0xF1, 0xF5, 0xF9));
        studentTable.setSelectionBackground(new Color(0xE0, 0xF2, 0xFE));
        studentTable.setSelectionForeground(TEXT_MAIN);

        // When a student row is clicked, load their info into the left form
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = studentTable.getSelectedRow();
                if (row >= 0) {
                    Student s = tableModel.getStudentAt(row);
                    if (s != null) {
                        nameField.setText(s.getName());
                        gradesField.setText(s.getGradesAsString());
                    }
                }
            }
        });

        // Custom Cell Renderer: adds padding and colors highest (green) and lowest (red)
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSel, hasFoc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10)); // 10px internal cell padding

                if (!isSel) {
                    if (col == 3) {
                        // Average score in bold dark
                        setForeground(TEXT_MAIN);
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else if (col == 4) {
                        // Highest score in soft emerald green
                        setForeground(SUCCESS);
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else if (col == 5) {
                        // Lowest score in soft crimson red
                        setForeground(DANGER);
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        // Regular text for name and grades
                        setForeground(TEXT_MAIN);
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                }
                return c;
            }
        };

        // Apply cell renderer to all columns
        for (int i = 0; i < studentTable.getColumnCount(); i++) {
            studentTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Set optimal column widths
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(180); // Student Name
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(220); // Grades
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(90);  // Grade Count
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(110); // Average Score
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Highest Score
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Lowest Score

        // Put table inside a scroll pane
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(createCardBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ==================== BOTTOM STATUS BAR ====================

    // Creates the footer bar with persistence and system status
    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(Color.WHITE);
        status.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(8, 20, 8, 20)
        ));

        JLabel info = new JLabel("Student Grade Tracker \u2022 Auto-saved to data/students.csv");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(TEXT_MUTED);

        JLabel credit = new JLabel("CodeAlpha Internship Task 1 \u2022 Pure Java SE");
        credit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        credit.setForeground(TEXT_MUTED);

        status.add(info, BorderLayout.WEST);
        status.add(credit, BorderLayout.EAST);
        return status;
    }

    // ==================== EVENT HANDLERS & BUTTON ACTIONS ====================

    // Adds a new student or updates an existing student
    private void handleSaveStudent() {
        String name = nameField.getText().trim();
        String gradesText = gradesField.getText().trim();

        // Validate that name is not blank
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a student name.", "Missing Name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Parse comma-separated grades into numbers
        List<Double> gradesList = new ArrayList<>();
        if (!gradesText.isEmpty()) {
            String[] tokens = gradesText.split("[,\\s]+");
            for (String tok : tokens) {
                if (tok.trim().isEmpty()) continue;
                try {
                    double g = Double.parseDouble(tok.trim());
                    // Grades must be between 0 and 100
                    if (g < 0 || g > 100) {
                        JOptionPane.showMessageDialog(this, "Grade " + g + " is out of range. Grades must be between 0 and 100.", "Invalid Grade", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    gradesList.add(g);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid grade value: '" + tok + "'. Please enter numeric values.", "Parsing Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        // Check if student with this name already exists (update mode)
        Student existing = null;
        for (Student s : studentList) {
            if (s.getName().equalsIgnoreCase(name)) {
                existing = s;
                break;
            }
        }

        if (existing != null) {
            // Update existing student's grades
            existing.clearGrades();
            for (Double g : gradesList) {
                existing.addGrade(g);
            }
        } else {
            // Add as new student
            studentList.add(new Student(name, gradesList));
        }

        // Save immediately to CSV file on disk
        storage.saveStudents(studentList);
        refreshData();
        clearInputs();
        JOptionPane.showMessageDialog(this, "Student '" + name + "' saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    // Deletes the student currently selected in the table
    private void handleDeleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Student s = tableModel.getStudentAt(row);
        if (s == null) return;

        // Ask teacher for confirmation before deleting
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student: " + s.getName() + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            studentList.remove(s);
            storage.saveStudents(studentList);
            refreshData();
            clearInputs();
        }
    }

    // Clears the input form fields
    private void clearInputs() {
        nameField.setText("");
        gradesField.setText("");
        studentTable.clearSelection();
    }

    // Filters the table rows instantly as user types in the search box
    private void filterTable() {
        String q = searchField.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            tableModel.setStudents(studentList); // show all students
            return;
        }

        List<Student> filtered = new ArrayList<>();
        for (Student s : studentList) {
            // Match student name
            if (s.getName().toLowerCase().contains(q)) {
                filtered.add(s);
            }
        }
        tableModel.setStudents(filtered);
    }

    // Reloads table rows and recalculates the 4 top metric cards
    private void refreshData() {
        tableModel.setStudents(studentList);
        calculateKpis();
    }

    // Computes overall class average, highest score, and lowest score
    private void calculateKpis() {
        if (studentList.isEmpty()) {
            kpiCountLabel.setText("0 Students");
            kpiAvgLabel.setText("0.0%");
            kpiHighLabel.setText("0.0");
            kpiLowLabel.setText("0.0");
            return;
        }

        double totalSum = 0;
        int totalGrades = 0;
        double overallHigh = -1;
        double overallLow = 101;

        // Loop through all students and all grades
        for (Student s : studentList) {
            for (Double g : s.getGrades()) {
                totalSum += g;
                totalGrades++;
                if (g > overallHigh) {
                    overallHigh = g; // track highest grade
                }
                if (g < overallLow) {
                    overallLow = g; // track lowest grade
                }
            }
        }

        // Calculate class average
        double avg = totalGrades > 0 ? (totalSum / totalGrades) : 0.0;
        kpiCountLabel.setText(studentList.size() + (studentList.size() == 1 ? " Student" : " Students"));
        kpiAvgLabel.setText(String.format("%.1f%%", avg));
        kpiHighLabel.setText(overallHigh >= 0 ? String.format("%.1f", overallHigh) : "-");
        kpiLowLabel.setText(overallLow <= 100 ? String.format("%.1f", overallLow) : "-");
    }

    // ==================== SUMMARY REPORT MODAL DIALOG ====================

    // Displays the clean summary report dialog
    private void handleViewSummaryReport() {
        if (studentList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No students to summarize. Please add students first.",
                    "Summary Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int totalStudents = studentList.size();
        double classAvgSum = 0;
        double overallHighest = -1;
        double overallLowest = 101;
        String highestStudent = "-";
        String lowestStudent = "-";

        // Calculate statistics for the report
        for (Student s : studentList) {
            classAvgSum += s.getAverage();
            for (double grade : s.getGrades()) {
                if (grade > overallHighest) {
                    overallHighest = grade;
                    highestStudent = s.getName();
                }
                if (grade < overallLowest) {
                    overallLowest = grade;
                    lowestStudent = s.getName();
                }
            }
        }

        double classAverage = classAvgSum / totalStudents;

        // Build the dialog window
        JDialog dialog = new JDialog(this, "Class Summary Report", true);
        dialog.setSize(640, 530);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(BG_APP);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Header section of report
        JPanel top = new JPanel(new BorderLayout(0, 4));
        top.setOpaque(false);
        JLabel titleLbl = new JLabel("SUMMARY REPORT");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setForeground(PRIMARY);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        JLabel dateLbl = new JLabel("Generated on: " + LocalDateTime.now().format(dtf));
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLbl.setForeground(TEXT_MUTED);

        top.add(titleLbl, BorderLayout.NORTH);
        top.add(dateLbl, BorderLayout.SOUTH);
        content.add(top, BorderLayout.NORTH);

        // Grid showing key statistics (Total Students, Average, High, Low)
        JPanel statsCard = new JPanel(new GridLayout(2, 2, 12, 12));
        statsCard.setBackground(Color.WHITE);
        statsCard.setBorder(createCardBorder());

        statsCard.add(createSummaryItem("Total Students", String.valueOf(totalStudents)));
        statsCard.add(createSummaryItem("Class Average", String.format("%.2f%%", classAverage)));
        statsCard.add(createSummaryItem("Highest Score", String.format("%.1f (%s)", overallHighest, highestStudent)));
        statsCard.add(createSummaryItem("Lowest Score", String.format("%.1f (%s)", overallLowest, lowestStudent)));

        // Individual student breakdown list
        JPanel listPanel = new JPanel(new BorderLayout(0, 8));
        listPanel.setOpaque(false);
        JLabel listTitle = new JLabel("Individual Student Breakdown:");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        listTitle.setForeground(PRIMARY);
        listPanel.add(listTitle, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Student s : studentList) {
            listModel.addElement(String.format("  %-20s   |   Average: %6.1f%%   |   Highest: %5.1f   |   Lowest: %5.1f",
                    s.getName(), s.getAverage(), s.getHighestGrade(), s.getLowestGrade()));
        }

        JList<String> list = new JList<>(listModel);
        list.setFont(new Font("Consolas", Font.PLAIN, 12));
        list.setSelectionBackground(new Color(0xE0, 0xF2, 0xFE));
        list.setSelectionForeground(TEXT_MAIN);

        JScrollPane listScroll = new JScrollPane(list);
        listScroll.setBorder(createCardBorder());
        listScroll.getViewport().setBackground(Color.WHITE);
        listPanel.add(listScroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setOpaque(false);
        centerPanel.add(statsCard, BorderLayout.NORTH);
        centerPanel.add(listPanel, BorderLayout.CENTER);
        content.add(centerPanel, BorderLayout.CENTER);

        // Bottom CTA buttons (Save Report to File & Close) with 44px height
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomBar.setOpaque(false);

        JButton saveFileBtn = createSecondaryButton("Save Report to File");
        saveFileBtn.setPreferredSize(new Dimension(180, 44)); // Taller CTA box

        double finalClassAvg = classAverage;
        double finalHigh = overallHighest;
        double finalLow = overallLowest;
        String finalHighStudent = highestStudent;
        String finalLowStudent = lowestStudent;

        saveFileBtn.addActionListener(e -> {
            try {
                File dir = new File("reports");
                if (!dir.exists()) dir.mkdirs();
                File reportFile = new File(dir, "summary_report.txt");

                StringBuilder sb = new StringBuilder();
                sb.append("========================================\n");
                sb.append("         STUDENT GRADE SUMMARY REPORT\n");
                sb.append("========================================\n");
                sb.append("Generated Date : ").append(LocalDateTime.now().format(dtf)).append("\n");
                sb.append("Total Students : ").append(totalStudents).append("\n");
                sb.append("Class Average  : ").append(String.format("%.2f%%", finalClassAvg)).append("\n");
                sb.append("Highest Score  : ").append(String.format("%.1f (%s)", finalHigh, finalHighStudent)).append("\n");
                sb.append("Lowest Score   : ").append(String.format("%.1f (%s)", finalLow, finalLowStudent)).append("\n");
                sb.append("========================================\n\n");
                sb.append("INDIVIDUAL BREAKDOWN:\n");
                sb.append("----------------------------------------------------------------------\n");
                for (Student s : studentList) {
                    sb.append(String.format("%-22s | Avg: %6.1f%% | High: %5.1f | Low: %5.1f | Grades: %s\n",
                            s.getName(), s.getAverage(), s.getHighestGrade(), s.getLowestGrade(), s.getGradesAsString()));
                }
                sb.append("----------------------------------------------------------------------\n");

                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(reportFile), StandardCharsets.UTF_8)) {
                    writer.write(sb.toString());
                }

                JOptionPane.showMessageDialog(dialog,
                        "Report successfully saved to:\n" + reportFile.getAbsolutePath(),
                        "Report Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Could not save report: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeBtn = createPrimaryButton("Close");
        closeBtn.setPreferredSize(new Dimension(110, 44)); // Taller CTA box
        closeBtn.addActionListener(e -> dialog.dispose());

        bottomBar.add(saveFileBtn);
        bottomBar.add(closeBtn);
        content.add(bottomBar, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    // Helper to format a label and value inside the summary card
    private JPanel createSummaryItem(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 15));
        v.setForeground(PRIMARY);

        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    // ==================== UI STYLING & CTA BUTTON FACTORIES ====================

    // Helper for subtle 1px card borders
    private Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        );
    }

    // Creates a styled text field with comfortable 42px height
    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(TEXT_MAIN);
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(0, 42)); // Generous input box height
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    // Creates a primary dark CTA button (increased height to 44px)
    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(0, 44)); // Increased CTA height
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8); // Smooth rounded corners

                FontMetrics fm = g2.getFontMetrics(c.getFont());
                int x = (c.getWidth() - fm.stringWidth(btn.getText())) / 2;
                int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setFont(c.getFont());
                g2.setColor(Color.WHITE);
                g2.drawString(btn.getText(), x, y);
                g2.dispose();
            }
        });
        return btn;
    }

    // Creates a secondary outlined CTA button (increased height to 44px)
    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(TEXT_MAIN);
        btn.setPreferredSize(new Dimension(0, 44)); // Increased CTA height
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 8, 8);

                FontMetrics fm = g2.getFontMetrics(c.getFont());
                int x = (c.getWidth() - fm.stringWidth(btn.getText())) / 2;
                int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setFont(c.getFont());
                g2.setColor(TEXT_MAIN);
                g2.drawString(btn.getText(), x, y);
                g2.dispose();
            }
        });
        return btn;
    }

    // Creates a danger red CTA button (increased height to 44px)
    private JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(0, 44)); // Increased CTA height
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DANGER);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);

                FontMetrics fm = g2.getFontMetrics(c.getFont());
                int x = (c.getWidth() - fm.stringWidth(btn.getText())) / 2;
                int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setFont(c.getFont());
                g2.setColor(Color.WHITE);
                g2.drawString(btn.getText(), x, y);
                g2.dispose();
            }
        });
        return btn;
    }

    // ==================== TABLE DATA MODEL ====================

    // Provides student data to the Swing JTable
    @SuppressWarnings("serial")
    private static class StudentTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

        // Table column header names
        private final String[] columns = {
            "Student Name", "Recorded Grades", "Grade Count", "Average Score", "Highest Score", "Lowest Score"
        };

        // Table rows data
        private final List<Student> data = new ArrayList<>();

        // Updates table data and refreshes view
        public void setStudents(List<Student> list) {
            data.clear();
            if (list != null) {
                data.addAll(list);
            }
            fireTableDataChanged();
        }

        // Returns the student at a specific row
        public Student getStudentAt(int row) {
            if (row >= 0 && row < data.size()) {
                return data.get(row);
            }
            return null;
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }

        // Returns the text value for each cell in the table
        @Override
        public Object getValueAt(int row, int col) {
            Student s = data.get(row);
            switch (col) {
                case 0: return s.getName();
                case 1: return s.getGradesAsString();
                case 2: return s.getGradeCount();
                case 3: return String.format("%.1f%%", s.getAverage());
                case 4: return String.format("%.1f", s.getHighestGrade());
                case 5: return String.format("%.1f", s.getLowestGrade());
                default: return "";
            }
        }
    }
}