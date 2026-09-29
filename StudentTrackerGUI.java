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
 * Modern, minimalist, industry-standard Desktop GUI for Student Grade Tracker.
 * Built with pure Java Swing and zero external framework dependencies.
 */
public class StudentTrackerGUI extends JFrame {
    private final StudentStorage storage;
    private final List<Student> studentList = new ArrayList<>();

    // UI Palette
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

    // KPI Metric Labels
    private JLabel kpiAvgLabel;
    private JLabel kpiHighLabel;
    private JLabel kpiLowLabel;
    private JLabel kpiCountLabel;

    // Form Fields
    private JTextField nameField;
    private JTextField gradesField;
    private JTextField searchField;

    // Table
    private StudentTableModel tableModel;
    private JTable studentTable;

    public StudentTrackerGUI() {
        super("Academic Grade Tracker — CodeAlpha Task 1");
        this.storage = new StudentStorage();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 700);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);

        // Load persisted data
        studentList.addAll(storage.loadStudents());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);
        setContentPane(root);

        // 1. Top Header Bar
        root.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Center Workspace (KPI Cards + Split Content)
        JPanel centerContainer = new JPanel(new BorderLayout(0, 16));
        centerContainer.setBackground(BG_APP);
        centerContainer.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Metric KPI Cards Banner
        centerContainer.add(createKpiBanner(), BorderLayout.NORTH);

        // Split: Left Form + Right Table
        JPanel workspace = new JPanel(new BorderLayout(16, 0));
        workspace.setOpaque(false);
        workspace.add(createFormSidebar(), BorderLayout.WEST);
        workspace.add(createTablePanel(), BorderLayout.CENTER);

        centerContainer.add(workspace, BorderLayout.CENTER);
        root.add(centerContainer, BorderLayout.CENTER);

        // 3. Status Bar
        root.add(createStatusBar(), BorderLayout.SOUTH);

        refreshData();
    }

    // ==================== TOP HEADER ====================

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(14, 24, 14, 24)
        ));

        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setOpaque(false);

        JLabel brand = new JLabel("STUDENT GRADE TRACKER");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(PRIMARY);

        JLabel sub = new JLabel("Academic Performance Management System • CodeAlpha Task 1");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);

        leftBox.add(brand);
        leftBox.add(Box.createVerticalStrut(2));
        leftBox.add(sub);
        header.add(leftBox, BorderLayout.WEST);

        JLabel status = new JLabel("● SYSTEM ACTIVE");
        status.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.setForeground(SUCCESS);
        header.add(status, BorderLayout.EAST);

        return header;
    }

    // ==================== KPI STATS BANNER ====================

    private JPanel createKpiBanner() {
        JPanel banner = new JPanel(new GridLayout(1, 4, 14, 0));
        banner.setOpaque(false);

        kpiAvgLabel = new JLabel("0.0%");
        kpiHighLabel = new JLabel("0.0");
        kpiLowLabel = new JLabel("0.0");
        kpiCountLabel = new JLabel("0 Students");

        banner.add(createKpiCard("CLASS AVERAGE", kpiAvgLabel, "Overall mean score", ACCENT));
        banner.add(createKpiCard("TOP SCORE", kpiHighLabel, "Highest individual grade", SUCCESS));
        banner.add(createKpiCard("LOWEST SCORE", kpiLowLabel, "Minimum score recorded", DANGER));
        banner.add(createKpiCard("TOTAL ENROLLED", kpiCountLabel, "Active student roster", PRIMARY));

        return banner;
    }

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

    // ==================== LEFT INPUT SIDEBAR ====================

    private JPanel createFormSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 16));
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(createCardBorder());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setOpaque(false);

        JLabel formTitle = new JLabel("STUDENT ENTRY & EDIT");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formTitle.setForeground(PRIMARY);

        JLabel formSub = new JLabel("Enter student info and grades below");
        formSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        formSub.setForeground(TEXT_MUTED);

        nameField = createStyledTextField();
        gradesField = createStyledTextField();

        formContent.add(formTitle);
        formContent.add(Box.createVerticalStrut(2));
        formContent.add(formSub);
        formContent.add(Box.createVerticalStrut(18));

        formContent.add(createFormField("Student Full Name:", nameField));
        formContent.add(Box.createVerticalStrut(14));
        formContent.add(createFormField("Grades (e.g., 85, 92, 78):", gradesField));
        formContent.add(Box.createVerticalStrut(18));

        // Action Buttons
        JButton saveBtn = createPrimaryButton("Save / Update Student");
        saveBtn.addActionListener(e -> handleSaveStudent());

        JButton clearBtn = createSecondaryButton("Clear Inputs");
        clearBtn.addActionListener(e -> clearInputs());

        JButton deleteBtn = createDangerButton("Delete Selected");
        deleteBtn.addActionListener(e -> handleDeleteStudent());

        JPanel btnBox = new JPanel(new GridLayout(3, 1, 0, 8));
        btnBox.setOpaque(false);
        btnBox.add(saveBtn);
        btnBox.add(clearBtn);
        btnBox.add(deleteBtn);

        formContent.add(btnBox);
        sidebar.add(formContent, BorderLayout.NORTH);

        return sidebar;
    }

    private JPanel createFormField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_MAIN);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    // ==================== RIGHT TABLE PANEL ====================

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // Search & Filter Toolbar
        JPanel searchBar = new JPanel(new BorderLayout(12, 0));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(createCardBorder());

        searchField = createStyledTextField();
        searchField.setPreferredSize(new Dimension(240, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        JPanel searchLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchLeft.setOpaque(false);
        JLabel searchIcon = new JLabel("Search Roster:");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchIcon.setForeground(TEXT_MAIN);
        searchLeft.add(searchIcon);
        searchLeft.add(searchField);

        JButton exportBtn = createSecondaryButton("Export HTML Report");
        exportBtn.addActionListener(e -> handleExportReport());

        searchBar.add(searchLeft, BorderLayout.WEST);
        searchBar.add(exportBtn, BorderLayout.EAST);
        panel.add(searchBar, BorderLayout.NORTH);

        // Table
        tableModel = new StudentTableModel();
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(36);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(new Color(0xF1, 0xF5, 0xF9));
        studentTable.getTableHeader().setForeground(TEXT_MAIN);
        studentTable.getTableHeader().setPreferredSize(new Dimension(0, 36));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setShowVerticalLines(false);
        studentTable.setShowHorizontalLines(true);
        studentTable.setGridColor(new Color(0xF1, 0xF5, 0xF9));
        studentTable.setSelectionBackground(new Color(0xE0, 0xF2, 0xFE));
        studentTable.setSelectionForeground(TEXT_MAIN);

        // When row clicked, populate left form
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

        // Cell padding and Grade Letter Color Renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSel, hasFoc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));

                if (col == 4 && val != null && !isSel) { // Letter Grade
                    String grade = val.toString();
                    if ("A".equals(grade)) setForeground(SUCCESS);
                    else if ("B".equals(grade)) setForeground(ACCENT);
                    else if ("C".equals(grade)) setForeground(new Color(0xD9, 0x77, 0x06));
                    else setForeground(DANGER);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else if (!isSel) {
                    setForeground(TEXT_MAIN);
                }
                return c;
            }
        };

        for (int i = 0; i < studentTable.getColumnCount(); i++) {
            studentTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        studentTable.getColumnModel().getColumn(0).setPreferredWidth(160); // Name
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Grades
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Count
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Average
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Letter
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Highest
        studentTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Lowest

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(createCardBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ==================== STATUS BAR ====================

    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(Color.WHITE);
        status.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(8, 20, 8, 20)
        ));

        JLabel info = new JLabel("Student Grade Tracker v2.0 • Auto-Saved to data/students.csv");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(TEXT_MUTED);

        JLabel credit = new JLabel("CodeAlpha Internship Task 1");
        credit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        credit.setForeground(TEXT_MUTED);

        status.add(info, BorderLayout.WEST);
        status.add(credit, BorderLayout.EAST);
        return status;
    }

    // ==================== ACTIONS & EVENT HANDLERS ====================

    private void handleSaveStudent() {
        String name = nameField.getText().trim();
        String gradesText = gradesField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a student name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Double> gradesList = new ArrayList<>();
        if (!gradesText.isEmpty()) {
            String[] tokens = gradesText.split("[,\\s]+");
            for (String tok : tokens) {
                if (tok.trim().isEmpty()) continue;
                try {
                    double g = Double.parseDouble(tok.trim());
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

        // Check if student exists (update) or is new
        Student existing = null;
        for (Student s : studentList) {
            if (s.getName().equalsIgnoreCase(name)) {
                existing = s;
                break;
            }
        }

        if (existing != null) {
            existing.clearGrades();
            for (Double g : gradesList) existing.addGrade(g);
        } else {
            studentList.add(new Student(name, gradesList));
        }

        storage.saveStudents(studentList);
        refreshData();
        clearInputs();
        JOptionPane.showMessageDialog(this, "Student '" + name + "' saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDeleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Student s = tableModel.getStudentAt(row);
        if (s == null) return;

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

    private void clearInputs() {
        nameField.setText("");
        gradesField.setText("");
        studentTable.clearSelection();
    }

    private void filterTable() {
        String q = searchField.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            tableModel.setStudents(studentList);
            return;
        }

        List<Student> filtered = new ArrayList<>();
        for (Student s : studentList) {
            if (s.getName().toLowerCase().contains(q) ||
                s.getLetterGrade().equalsIgnoreCase(q)) {
                filtered.add(s);
            }
        }
        tableModel.setStudents(filtered);
    }

    private void refreshData() {
        tableModel.setStudents(studentList);
        calculateKpis();
    }

    private void calculateKpis() {
        if (studentList.isEmpty()) {
            kpiAvgLabel.setText("0.0%");
            kpiHighLabel.setText("0.0");
            kpiLowLabel.setText("0.0");
            kpiCountLabel.setText("0 Students");
            return;
        }

        double totalSum = 0;
        int totalGrades = 0;
        double overallHigh = -1;
        double overallLow = 101;
        String topStudent = "";
        String lowStudent = "";

        for (Student s : studentList) {
            for (Double g : s.getGrades()) {
                totalSum += g;
                totalGrades++;
                if (g > overallHigh) {
                    overallHigh = g;
                    topStudent = s.getName();
                }
                if (g < overallLow) {
                    overallLow = g;
                    lowStudent = s.getName();
                }
            }
        }

        double avg = totalGrades > 0 ? (totalSum / totalGrades) : 0.0;
        kpiAvgLabel.setText(String.format("%.1f%%", avg));
        kpiHighLabel.setText(overallHigh >= 0 ? String.format("%.1f", overallHigh) : "—");
        kpiLowLabel.setText(overallLow <= 100 ? String.format("%.1f", overallLow) : "—");
        kpiCountLabel.setText(studentList.size() + " Students");
    }

    private void handleExportReport() {
        File dir = new File("reports");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, "Student_Summary_Report.html");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

        calculateKpis();

        StringBuilder rows = new StringBuilder();
        for (Student s : studentList) {
            rows.append("<tr>")
                .append("<td><strong>").append(s.getName()).append("</strong></td>")
                .append("<td>").append(s.getGradesAsString()).append("</td>")
                .append("<td>").append(s.getGradeCount()).append("</td>")
                .append("<td><strong>").append(String.format("%.1f", s.getAverage())).append("%</strong></td>")
                .append("<td><span class=\"badge\">").append(s.getLetterGrade()).append("</span></td>")
                .append("<td>").append(String.format("%.1f", s.getHighestGrade())).append("</td>")
                .append("<td>").append(String.format("%.1f", s.getLowestGrade())).append("</td>")
                .append("</tr>\n");
        }

        String html = "<!DOCTYPE html>\n<html><head><meta charset=\"UTF-8\"><title>Academic Summary Report</title>" +
                "<style>" +
                "body { font-family: 'Segoe UI', -apple-system, sans-serif; background: #f8fafc; padding: 36px; color: #0f172a; }\n" +
                ".card { max-width: 820px; margin: auto; background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 36px; box-shadow: 0 4px 6px rgba(0,0,0,0.04); }\n" +
                ".header { border-bottom: 2px solid #0f172a; padding-bottom: 16px; margin-bottom: 24px; }\n" +
                "table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 13px; }\n" +
                "th { text-align: left; padding: 10px 12px; background: #f1f5f9; border-bottom: 1px solid #cbd5e1; }\n" +
                "td { padding: 12px; border-bottom: 1px solid #f1f5f9; }\n" +
                ".badge { background: #e0f2fe; color: #0284c7; padding: 2px 8px; border-radius: 4px; font-weight: bold; }\n" +
                ".btn { background: #0f172a; color: white; border: none; padding: 8px 18px; border-radius: 6px; cursor: pointer; float: right; font-weight: bold; }\n" +
                "@media print { .btn { display: none; } body { padding: 0; background: white; } .card { border: none; box-shadow: none; } }\n" +
                "</style></head><body>" +
                "<div class=\"card\">" +
                "<button class=\"btn\" onclick=\"window.print()\">Print / Save as PDF</button>" +
                "<div class=\"header\"><h2>STUDENT GRADE SUMMARY REPORT</h2>" +
                "<p style=\"color: #64748b; font-size: 13px;\">Generated: " + LocalDateTime.now().format(dtf) + " • CodeAlpha Java Programming</p></div>" +
                "<p><strong>Total Students:</strong> " + studentList.size() + " &nbsp;|&nbsp; <strong>Class Average:</strong> " + kpiAvgLabel.getText() + "</p>" +
                "<table><thead><tr><th>Student Name</th><th>Grades</th><th>Count</th><th>Average</th><th>Grade</th><th>Highest</th><th>Lowest</th></tr></thead>" +
                "<tbody>" + rows + "</tbody></table>" +
                "<p style=\"margin-top: 30px; text-align: center; color: #94a3b8; font-size: 12px;\">End of Official Academic Report</p>" +
                "</div></body></html>";

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(html);
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(file.toURI());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== UI STYLING HELPERS ====================

    private Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        );
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(TEXT_MAIN);
        tf.setBackground(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
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
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 6, 6);

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

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(TEXT_MAIN);
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
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 6, 6);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 6, 6);

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

    private JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
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
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 6, 6);

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

    // ==================== TABLE MODEL ====================

    private static class StudentTableModel extends AbstractTableModel {
        private final String[] columns = {"Student Name", "Recorded Grades", "Count", "Average", "Grade", "Highest", "Lowest"};
        private final List<Student> data = new ArrayList<>();

        public void setStudents(List<Student> list) {
            data.clear();
            if (list != null) data.addAll(list);
            fireTableDataChanged();
        }

        public Student getStudentAt(int row) {
            if (row >= 0 && row < data.size()) return data.get(row);
            return null;
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Student s = data.get(row);
            switch (col) {
                case 0: return s.getName();
                case 1: return s.getGradesAsString();
                case 2: return s.getGradeCount();
                case 3: return String.format("%.1f%%", s.getAverage());
                case 4: return s.getLetterGrade();
                case 5: return String.format("%.1f", s.getHighestGrade());
                case 6: return String.format("%.1f", s.getLowestGrade());
                default: return "";
            }
        }
    }
}