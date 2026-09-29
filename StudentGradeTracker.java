import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Student Grade Tracker application.
 * Supports dual-mode execution:
 *   - Modern Swing Desktop GUI (Default)
 *   - Command-Line Interface (CLI via --console flag)
 */
public class StudentGradeTracker {
    private static final StudentStorage storage = new StudentStorage();
    private static final List<Student> students = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Check for CLI flag
        boolean useConsole = false;
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if ("--console".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                    useConsole = true;
                    break;
                }
            }
        }

        if (useConsole) {
            runConsoleMode();
        } else {
            runGuiMode();
        }
    }

    private static void runGuiMode() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            StudentTrackerGUI gui = new StudentTrackerGUI();
            gui.setVisible(true);
        });
    }

    private static void runConsoleMode() {
        students.clear();
        students.addAll(storage.loadStudents());

        boolean running = true;
        System.out.println();
        System.out.println("  ============================================");
        System.out.println("     ACADEMIC STUDENT GRADE TRACKER (CLI)");
        System.out.println("     CodeAlpha Java Internship - Task 1");
        System.out.println("  ============================================");
        System.out.println();

        while (running) {
            printMenu();
            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewAllStudents();
                    break;
                case 3:
                    editStudent();
                    break;
                case 4:
                    deleteStudent();
                    break;
                case 5:
                    viewSummaryReport();
                    break;
                case 6:
                    running = false;
                    System.out.println("\n  All data saved. Goodbye!\n");
                    break;
                default:
                    System.out.println("\n  Invalid choice. Please enter 1-6.\n");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("  --------------------------------");
        System.out.println("           MAIN MENU");
        System.out.println("  --------------------------------");
        System.out.println("    1. Add Student");
        System.out.println("    2. View All Students");
        System.out.println("    3. Edit Student Grades");
        System.out.println("    4. Delete Student");
        System.out.println("    5. View Summary Report");
        System.out.println("    6. Exit");
        System.out.println("  --------------------------------");
        System.out.print("  Enter choice (1-6): ");
    }

    private static int getMenuChoice() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void addStudent() {
        System.out.print("\n  Enter student name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("  Name cannot be empty.\n");
            return;
        }

        Student s = new Student(name);
        System.out.println("  Enter grades (0-100), or enter 'done' to finish:");
        while (true) {
            System.out.print("  Grade: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) break;
            try {
                double g = Double.parseDouble(input);
                s.addGrade(g);
            } catch (Exception ex) {
                System.out.println("  Invalid input: " + ex.getMessage());
            }
        }

        students.add(s);
        storage.saveStudents(students);
        System.out.println("  Student added successfully.\n");
    }

    private static void viewAllStudents() {
        System.out.println("\n  --- All Registered Students ---");
        if (students.isEmpty()) {
            System.out.println("  No students found.\n");
            return;
        }
        for (int i = 0; i < students.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + students.get(i));
        }
        System.out.println();
    }

    private static void editStudent() {
        viewAllStudents();
        if (students.isEmpty()) return;

        System.out.print("  Enter student number to edit (1-" + students.size() + "): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= students.size()) {
                System.out.println("  Invalid selection.\n");
                return;
            }
            Student s = students.get(idx);
            s.clearGrades();
            System.out.println("  Enter new grades for " + s.getName() + " (type 'done' to finish):");
            while (true) {
                System.out.print("  Grade: ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("done")) break;
                try {
                    s.addGrade(Double.parseDouble(input));
                } catch (Exception ex) {
                    System.out.println("  Invalid input: " + ex.getMessage());
                }
            }
            storage.saveStudents(students);
            System.out.println("  Student updated successfully.\n");
        } catch (Exception e) {
            System.out.println("  Invalid input.\n");
        }
    }

    private static void deleteStudent() {
        viewAllStudents();
        if (students.isEmpty()) return;

        System.out.print("  Enter student number to delete (1-" + students.size() + "): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < students.size()) {
                Student removed = students.remove(idx);
                storage.saveStudents(students);
                System.out.println("  Deleted " + removed.getName() + ".\n");
            }
        } catch (Exception e) {
            System.out.println("  Invalid selection.\n");
        }
    }

    private static void viewSummaryReport() {
        System.out.println("\n  ==========================================");
        System.out.println("            ACADEMIC SUMMARY REPORT");
        System.out.println("  ==========================================");
        if (students.isEmpty()) {
            System.out.println("  No data available.\n");
            return;
        }

        double sum = 0;
        int count = 0;
        double high = -1;
        double low = 101;

        for (Student s : students) {
            for (Double g : s.getGrades()) {
                sum += g;
                count++;
                if (g > high) high = g;
                if (g < low) low = g;
            }
        }

        double classAvg = count > 0 ? (sum / count) : 0;
        System.out.printf("  Total Students Enrolled : %d%n", students.size());
        System.out.printf("  Total Grades Recorded   : %d%n", count);
        System.out.printf("  Class Overall Average   : %.2f%%%n", classAvg);
        System.out.printf("  Highest Grade Recorded  : %.2f%n", (high >= 0 ? high : 0.0));
        System.out.printf("  Lowest Grade Recorded   : %.2f%n", (low <= 100 ? low : 0.0));
        System.out.println("  ------------------------------------------\n");
    }
}