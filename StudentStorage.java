import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles saving and loading student records to and from a CSV file.
 * Automatically creates sample data if no records exist yet.
 */
public final class StudentStorage {
    // The folder name where files are saved
    private static final String DATA_DIR = "data";

    // The complete file path to the students file (data/students.csv)
    private static final String FILE_PATH = DATA_DIR + File.separator + "students.csv";

    // Constructor creates the data folder if missing and checks for starter data
    public StudentStorage() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // create data directory
        }
        initDefaultStudentsIfEmpty();
    }

    // Loads all student records from the CSV file
    public List<Student> loadStudents() {
        List<Student> list = new ArrayList<>();
        File file = new File(FILE_PATH);

        // If file does not exist, initialize starter students
        if (!file.exists()) {
            initDefaultStudentsIfEmpty();
        }

        // Open and read the file line by line
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Skip empty lines or comment lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // Parse student from CSV format
                Student s = Student.fromCsv(line);
                if (s != null) {
                    list.add(s);
                }
            }
        } catch (Exception e) {
            System.err.println("Notice: error reading student records: " + e.getMessage());
        }

        // If the file was completely empty, restore the default starter records
        if (list.isEmpty()) {
            initDefaultStudentsIfEmpty();
            return loadStudents();
        }

        return list;
    }

    // Saves the full list of students into the CSV file
    public void saveStudents(List<Student> students) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FILE_PATH), StandardCharsets.UTF_8))) {
            for (Student s : students) {
                writer.println(s.toCsv()); // write each student as a CSV row
            }
        } catch (IOException e) {
            System.err.println("Notice: error saving student records: " + e.getMessage());
        }
    }

    // Populates starter sample students if the file is new or empty
    private void initDefaultStudentsIfEmpty() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            List<Student> defaults = new ArrayList<>();

            // 5 realistic starter student records for testing
            defaults.add(new Student("Kwesi Mensah", Arrays.asList(88.0, 92.0, 95.0)));
            defaults.add(new Student("Ama Darko", Arrays.asList(74.0, 82.0, 79.0)));
            defaults.add(new Student("Kofi Boateng", Arrays.asList(91.0, 89.0, 94.0)));
            defaults.add(new Student("Abena Osei", Arrays.asList(68.0, 72.0, 70.0)));
            defaults.add(new Student("Yaw Appiah", Arrays.asList(82.0, 85.0, 90.0)));

            saveStudents(defaults);
        }
    }
}