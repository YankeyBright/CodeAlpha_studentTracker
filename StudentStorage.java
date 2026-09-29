import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles persistent storage of student data to a CSV file.
 */
public class StudentStorage {
    private static final String DATA_DIR = "data";
    private static final String FILE_PATH = DATA_DIR + File.separator + "students.csv";

    public StudentStorage() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        initDefaultStudentsIfEmpty();
    }

    public List<Student> loadStudents() {
        List<Student> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            initDefaultStudentsIfEmpty();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                Student s = Student.fromCsv(line);
                if (s != null) {
                    list.add(s);
                }
            }
        } catch (Exception e) {
            System.err.println("Warning loading students: " + e.getMessage());
        }

        if (list.isEmpty()) {
            initDefaultStudentsIfEmpty();
            return loadStudents();
        }

        return list;
    }

    public void saveStudents(List<Student> students) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FILE_PATH), StandardCharsets.UTF_8))) {
            for (Student s : students) {
                writer.println(s.toCsv());
            }
        } catch (IOException e) {
            System.err.println("Error saving students: " + e.getMessage());
        }
    }

    private void initDefaultStudentsIfEmpty() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            List<Student> defaults = new ArrayList<>();

            Student s1 = new Student("Kwesi Mensah", Arrays.asList(88.0, 92.0, 95.0));
            Student s2 = new Student("Ama Darko", Arrays.asList(74.0, 82.0, 79.0));
            Student s3 = new Student("Kofi Boateng", Arrays.asList(91.0, 89.0, 94.0));
            Student s4 = new Student("Abena Osei", Arrays.asList(68.0, 72.0, 70.0));
            Student s5 = new Student("Yaw Appiah", Arrays.asList(82.0, 85.0, 90.0));

            defaults.add(s1);
            defaults.add(s2);
            defaults.add(s3);
            defaults.add(s4);
            defaults.add(s5);

            saveStudents(defaults);
        }
    }
}