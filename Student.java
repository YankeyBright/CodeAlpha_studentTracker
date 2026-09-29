import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student with their registered grades and score calculations.
 * Designed to be simple, beginner-friendly, and easy to understand.
 */
public final class Student {
    // The student's name (for example: "Kwesi Mensah")
    private String name;

    // A list of numbers representing exam or quiz scores
    private final ArrayList<Double> grades;

    // Constructor to create a student with just a name
    public Student(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        this.name = name.trim();
        this.grades = new ArrayList<>();
    }

    // Constructor to create a student with a name and a list of starting grades
    public Student(String name, List<Double> initialGrades) {
        this(name);
        if (initialGrades != null) {
            for (Double g : initialGrades) {
                // Validate and add each grade directly
                if (g != null && g >= 0.0 && g <= 100.0) {
                    this.grades.add(g);
                }
            }
        }
    }

    // Returns the student's name
    public String getName() {
        return name;
    }

    // Updates the student's name
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        this.name = name.trim();
    }

    // Returns a copy of the list of grades
    public List<Double> getGrades() {
        return new ArrayList<>(grades);
    }

    // Returns how many grades this student has
    public int getGradeCount() {
        return grades.size();
    }

    // Adds a single grade score (must be between 0 and 100)
    public void addGrade(double grade) {
        if (grade < 0.0 || grade > 100.0) {
            throw new IllegalArgumentException("Grade must be between 0.0 and 100.0. Received: " + grade);
        }
        grades.add(grade);
    }

    // Clears all grades for this student
    public void clearGrades() {
        grades.clear();
    }

    // Calculates and returns the average score
    public double getAverage() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (double g : grades) {
            sum += g; // add all scores together
        }
        return sum / grades.size(); // divide total by the number of grades
    }

    // Finds and returns the highest score
    public double getHighestGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double highest = grades.get(0);
        for (double g : grades) {
            if (g > highest) {
                highest = g; // found a higher score
            }
        }
        return highest;
    }

    // Finds and returns the lowest score
    public double getLowestGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double lowest = grades.get(0);
        for (double g : grades) {
            if (g < lowest) {
                lowest = g; // found a lower score
            }
        }
        return lowest;
    }

    // Formats grades into a neat readable string (for example: "88, 92, 95")
    public String getGradesAsString() {
        if (grades.isEmpty()) {
            return "No grades recorded";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grades.size(); i++) {
            double g = grades.get(i);
            // If the grade is a whole number (e.g. 85.0), show it as 85
            if (g == Math.floor(g)) {
                sb.append(String.format("%.0f", g));
            } else {
                sb.append(String.format("%.1f", g));
            }
            if (i < grades.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    // Converts student information into a CSV line for saving to a file
    public String toCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(name.replace("\"", "\"\"")).append("\"");
        for (double g : grades) {
            sb.append(",").append(g);
        }
        return sb.toString();
    }

    // Reads a CSV line from a file and creates a Student object
    public static Student fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        // Split by comma outside quotes
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        if (parts.length < 1) {
            return null;
        }

        String name = parts[0].replace("\"", "").trim();
        Student s = new Student(name);
        for (int i = 1; i < parts.length; i++) {
            String p = parts[i].trim();
            if (!p.isEmpty()) {
                try {
                    s.addGrade(Double.parseDouble(p));
                } catch (NumberFormatException ignored) {}
            }
        }
        return s;
    }

    // Text representation of the student
    @Override
    public String toString() {
        return String.format("%s | Avg: %.1f | High: %.1f | Low: %.1f | Grades: [%s]",
                name, getAverage(), getHighestGrade(), getLowestGrade(), getGradesAsString());
    }
}