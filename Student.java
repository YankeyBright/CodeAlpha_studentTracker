import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student with their registered grades and academic calculations.
 */
public class Student {
    private String name;
    private final ArrayList<Double> grades;

    public Student(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        this.name = name.trim();
        this.grades = new ArrayList<>();
    }

    public Student(String name, List<Double> grades) {
        this(name);
        if (grades != null) {
            for (Double g : grades) {
                addGrade(g);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        this.name = name.trim();
    }

    public List<Double> getGrades() {
        return new ArrayList<>(grades);
    }

    public int getGradeCount() {
        return grades.size();
    }

    public void addGrade(double grade) {
        if (grade < 0.0 || grade > 100.0) {
            throw new IllegalArgumentException("Grade must be between 0.0 and 100.0. Received: " + grade);
        }
        grades.add(grade);
    }

    public void clearGrades() {
        grades.clear();
    }

    public double getAverage() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0.0;
        for (double g : grades) {
            sum += g;
        }
        return sum / grades.size();
    }

    public double getHighestGrade() {
        if (grades.isEmpty()) return 0.0;
        double max = grades.get(0);
        for (double g : grades) {
            if (g > max) max = g;
        }
        return max;
    }

    public double getLowestGrade() {
        if (grades.isEmpty()) return 0.0;
        double min = grades.get(0);
        for (double g : grades) {
            if (g < min) min = g;
        }
        return min;
    }

    public String getLetterGrade() {
        if (grades.isEmpty()) return "N/A";
        double avg = getAverage();
        if (avg >= 90.0) return "A";
        if (avg >= 80.0) return "B";
        if (avg >= 70.0) return "C";
        if (avg >= 60.0) return "D";
        return "F";
    }

    public String getGradesAsString() {
        if (grades.isEmpty()) return "No grades recorded";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grades.size(); i++) {
            double g = grades.get(i);
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

    public String toCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(name.replace("\"", "\"\"")).append("\"");
        for (double g : grades) {
            sb.append(",").append(g);
        }
        return sb.toString();
    }

    public static Student fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        if (parts.length < 1) return null;

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

    @Override
    public String toString() {
        return String.format("%s | Avg: %.1f (%s) | High: %.1f | Low: %.1f | Grades: [%s]",
                name, getAverage(), getLetterGrade(), getHighestGrade(), getLowestGrade(), getGradesAsString());
    }
}