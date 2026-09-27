
import java.util.ArrayList;
import java.util.List;

// This class stores info about one student - their name and grades
public class Student {

    private String name;
    private ArrayList<Double> grades; // list to hold all grades for this student

    // constructor that takes just a name
    public Student(String name) {
        this.name = name.trim();
        this.grades = new ArrayList<>();
    }

    // constructor that takes name and grades
    public Student(String name, List<Double> grades) {
        this.name = name.trim();
        this.grades = new ArrayList<>(grades);
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public List<Double> getGrades() {
        return grades;
    }

    public void setGrades(List<Double> grades) {
        this.grades = new ArrayList<>(grades);
    }

    // add one grade to the student
    public void addGrade(double grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100. Got: " + grade);
        }
        grades.add(grade);
    }

    // calculate and return the average of all grades
    public double getAverage() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (double grade : grades) {
            sum += grade;
        }
        return sum / grades.size();
    }

    // find the highest grade
    public double getHighestGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double max = grades.get(0);
        for (double grade : grades) {
            if (grade > max) {
                max = grade;
            }
        }
        return max;
    }

    // find the lowest grade
    public double getLowestGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double min = grades.get(0);
        for (double grade : grades) {
            if (grade < min) {
                min = grade;
            }
        }
        return min;
    }

    // turn grades into a readable string like "85, 92, 78"
    public String getGradesAsString() {
        if (grades.isEmpty()) {
            return "No grades";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grades.size(); i++) {
            double g = grades.get(i);
            if (g == Math.floor(g)) { // if it's a whole number, don't show decimal
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

    @Override
    public String toString() {
        return "Student: " + name + " | Grades: " + getGradesAsString() + " | Avg: " + String.format("%.2f", getAverage());
    }
}
