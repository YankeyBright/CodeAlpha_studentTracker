import java.util.ArrayList;
import java.util.Scanner;

// Main class for the Student Grade Tracker program
public class StudentGradeTracker {

    // this arraylist stores all the students
    private static final ArrayList<Student> students = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        // print a welcome message when the program starts
        System.out.println();
        System.out.println("  ================================");
        System.out.println("     STUDENT GRADE TRACKER");
        System.out.println("     Java Programming - Task 1");
        System.out.println("  ================================");
        System.out.println();

        // keep showing the menu until the user picks exit
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
                    System.out.println("\n  Goodbye! Thanks for using Student Grade Tracker.\n");
                    break;
                default:
                    System.out.println("\n  Invalid choice. Please enter 1-6.\n");
            }
        }

        scanner.close();
    }

    // prints the main menu options
    private static void printMenu() {
        System.out.println("  --------------------------------");
        System.out.println("           MAIN MENU");
        System.out.println("  --------------------------------");
        System.out.println("    1. Add Student");
        System.out.println("    2. View All Students");
        System.out.println("    3. Edit Student");
        System.out.println("    4. Delete Student");
        System.out.println("    5. View Summary Report");
        System.out.println("    6. Exit");
        System.out.println("  --------------------------------");
    }

    // reads the user's menu choice and returns it
    private static int getMenuChoice() {
        System.out.print("  Enter your choice: ");
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // return -1 if input is not a number
        }
    }

    // ---- OPTION 1: ADD A NEW STUDENT ----
    private static void addStudent() {
        System.out.println("\n  -- Add New Student --\n");

        // ask for the student name, keep asking if empty
        String name = "";
        while (name.isEmpty()) {
            System.out.print("  Enter student name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("  Name cannot be empty. Try again.");
            }
        }

        // check if a student with the same name already exists
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) {
                System.out.println("  Warning: A student named \"" + s.getName() + "\" already exists.");
                System.out.print("  Continue adding? (y/n): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (!confirm.equals("y") && !confirm.equals("yes")) {
                    System.out.println("  Cancelled.\n");
                    return;
                }
                break;
            }
        }

        // ask how many grades to enter (must be at least 1)
        int numGrades = 0;
        while (numGrades < 1) {
            System.out.print("  How many grades to enter: ");
            String input = scanner.nextLine().trim();
            try {
                numGrades = Integer.parseInt(input);
                if (numGrades < 1) {
                    System.out.println("  Must enter at least 1 grade.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number.");
            }
        }

        // collect each grade one by one
        ArrayList<Double> grades = new ArrayList<>();
        for (int i = 1; i <= numGrades; i++) {
            boolean validGrade = false;
            while (!validGrade) {
                System.out.print("  Enter grade " + i + ": ");
                String input = scanner.nextLine().trim();
                try {
                    double grade = Double.parseDouble(input);
                    if (grade < 0 || grade > 100) {
                        System.out.println("  Grade must be between 0 and 100.");
                    } else {
                        grades.add(grade);
                        validGrade = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("  Please enter a valid number.");
                }
            }
        }

        // create the student and add to the list
        Student student = new Student(name, grades);
        students.add(student);

        System.out.println("\n  Student \"" + name + "\" added with " + numGrades + " grade(s).");
        System.out.println("  Average: " + String.format("%.2f", student.getAverage()));
        System.out.println();
    }

    // ---- OPTION 2: VIEW ALL STUDENTS ----
    private static void viewAllStudents() {
        System.out.println("\n  -- All Students --\n");

        // check if the list is empty
        if (students.isEmpty()) {
            System.out.println("  No students added yet.\n");
            return;
        }

        // print a simple table header
        System.out.printf("  %-4s %-20s %-25s %-10s%n", "#", "Name", "Grades", "Average");
        System.out.println("  " + "-".repeat(60));

        // print each student's info
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.printf("  %-4d %-20s %-25s %-10s%n",
                    (i + 1),
                    s.getName(),
                    s.getGradesAsString(),
                    String.format("%.2f", s.getAverage()));
        }

        System.out.println();
    }

    // ---- OPTION 3: EDIT A STUDENT ----
    private static void editStudent() {
        System.out.println("\n  -- Edit Student --\n");

        if (students.isEmpty()) {
            System.out.println("  No students to edit.\n");
            return;
        }

        // show the list of students so user can pick one
        printStudentListCompact();

        // ask which student to edit
        int index = getStudentSelection("edit");
        if (index == -1) return;

        Student student = students.get(index);

        // show current info
        System.out.println("\n  Current details:");
        System.out.println("    Name:    " + student.getName());
        System.out.println("    Grades:  " + student.getGradesAsString());
        System.out.println("    Average: " + String.format("%.2f", student.getAverage()));
        System.out.println();

        // ask for new name (press enter to keep current)
        System.out.print("  Enter new name (press Enter to keep \"" + student.getName() + "\"): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            student.setName(newName);
        }

        // ask if they want to re-enter grades
        System.out.print("  Re-enter grades? (y/n): ");
        String reenter = scanner.nextLine().trim().toLowerCase();
        if (reenter.equals("y") || reenter.equals("yes")) {

            // ask how many grades
            int numGrades = 0;
            while (numGrades < 1) {
                System.out.print("  How many new grades: ");
                String input = scanner.nextLine().trim();
                try {
                    numGrades = Integer.parseInt(input);
                    if (numGrades < 1) {
                        System.out.println("  Must enter at least 1 grade.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("  Please enter a valid number.");
                }
            }

            // collect the new grades
            ArrayList<Double> newGrades = new ArrayList<>();
            for (int i = 1; i <= numGrades; i++) {
                boolean validGrade = false;
                while (!validGrade) {
                    System.out.print("  Enter grade " + i + ": ");
                    String input = scanner.nextLine().trim();
                    try {
                        double grade = Double.parseDouble(input);
                        if (grade < 0 || grade > 100) {
                            System.out.println("  Grade must be between 0 and 100.");
                        } else {
                            newGrades.add(grade);
                            validGrade = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("  Please enter a valid number.");
                    }
                }
            }

            student.setGrades(newGrades);
        }

        // show updated info
        System.out.println("\n  Student updated!");
        System.out.println("    Name:    " + student.getName());
        System.out.println("    Grades:  " + student.getGradesAsString());
        System.out.println("    Average: " + String.format("%.2f", student.getAverage()));
        System.out.println();
    }

    // ---- OPTION 4: DELETE A STUDENT ----
    private static void deleteStudent() {
        System.out.println("\n  -- Delete Student --\n");

        if (students.isEmpty()) {
            System.out.println("  No students to delete.\n");
            return;
        }

        // show the list so user can pick one
        printStudentListCompact();

        int index = getStudentSelection("delete");
        if (index == -1) return;

        Student student = students.get(index);

        // show who will be deleted
        System.out.println("\n  Student to delete:");
        System.out.println("    Name:   " + student.getName());
        System.out.println("    Grades: " + student.getGradesAsString());

        // ask for confirmation before deleting
        System.out.print("\n  Are you sure? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            String removedName = student.getName();
            students.remove(index);
            System.out.println("\n  Student \"" + removedName + "\" deleted.\n");
        } else {
            System.out.println("\n  Deletion cancelled.\n");
        }
    }

    // ---- OPTION 5: VIEW SUMMARY REPORT ----
    private static void viewSummaryReport() {
        System.out.println();

        if (students.isEmpty()) {
            System.out.println("  No students to summarize. Add students first.\n");
            return;
        }

        int totalStudents = students.size();
        double classAvgSum = 0;

        // find the highest and lowest grade across all students
        double overallHighest = -1;
        double overallLowest = 101;
        String highestStudent = "";
        String lowestStudent = "";

        for (Student s : students) {
            classAvgSum += s.getAverage();

            // loop through each grade to find highest and lowest
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

        // print the summary
        System.out.println("  ================================");
        System.out.println("        SUMMARY REPORT");
        System.out.println("  ================================");
        System.out.println("  Total Students : " + totalStudents);
        System.out.println("  Class Average  : " + String.format("%.2f", classAverage));
        System.out.println("  Highest Score  : " + String.format("%.0f", overallHighest) + " (" + highestStudent + ")");
        System.out.println("  Lowest Score   : " + String.format("%.0f", overallLowest) + " (" + lowestStudent + ")");
        System.out.println("  ================================");

        // show each student's breakdown
        System.out.println("\n  Individual Breakdown:");
        System.out.println("  " + "-".repeat(50));

        for (Student s : students) {
            System.out.printf("  %-18s Avg: %6.2f  High: %5.1f  Low: %5.1f%n",
                    s.getName(), s.getAverage(), s.getHighestGrade(), s.getLowestGrade());
        }

        System.out.println();
    }

    // prints a short numbered list of students
    private static void printStudentListCompact() {
        System.out.println("  Students:");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.printf("    %d. %s (Avg: %.2f)%n", (i + 1), s.getName(), s.getAverage());
        }
        System.out.println();
    }

    // asks the user to pick a student by number, returns the index or -1 if cancelled
    private static int getStudentSelection(String action) {
        System.out.print("  Enter student number to " + action + " (0 to cancel): ");
        String input = scanner.nextLine().trim();

        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input.\n");
            return -1;
        }

        if (selection == 0) {
            System.out.println("  Cancelled.\n");
            return -1;
        }

        if (selection < 1 || selection > students.size()) {
            System.out.println("  Invalid student number. Enter 1-" + students.size() + ".\n");
            return -1;
        }

        return selection - 1; // convert to 0-based index
    }
}
