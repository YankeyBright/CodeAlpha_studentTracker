# Student Grade Tracker

> **CodeAlpha Java Programming Internship â€” Task 1**  
> An intuitive, beginner-friendly desktop application and CLI tool that allows teachers to enter students' grades and automatically compute their **average**, **highest**, and **lowest** scores.

---

## Task Requirements Met

- [x] **Enter Student Grades**: Add students and their exam/quiz scores via an easy comma-separated input field (e.g. `85, 92, 78`).
- [x] **Compute Average Score**: Automatically computes individual student averages and class-wide average score.
- [x] **Compute Highest Score**: Instantly identifies the highest score across all students.
- [x] **Compute Lowest Score**: Instantly identifies the lowest score across all students.
- [x] **Beginner-Friendly Interface**: Simple vocabulary with zero confusing jargon (no complicated grade curves, no confusing export steps).
- [x] **Class Summary Report**: Built-in summary dialog showing class averages, top score, lowest score, and student breakdown.
- [x] **Dual Execution Mode**: Modern desktop window by default, with a simple command-line interface (CLI) fallback.
- [x] **Zero Dependencies**: Pure Java SE standard library. Runs out of the box on any machine with Java installed.

---

## Key Features

1. **Clean Minimalist Design**:
   - Modern slate and clean white layout with soft borders.
   - Real-time indicator: `â— SYSTEM ACTIVE`.

2. **Top Metric Cards (Instant Stats)**:
   - **Total Students**: Total number of students added.
   - **Class Average**: Average grade percentage across the class.
   - **Highest Score**: Top score achieved in class.
   - **Lowest Score**: Lowest score recorded in class.

3. **Student Form**:
   - Enter student name and comma-separated scores.
   - Single-click **Save Student**, **Clear Form**, and **Delete Student**.

4. **Student List & Search**:
   - Clean table showing: **Student Name**, **Grades**, **Grade Count**, **Average Score**, **Highest Score**, and **Lowest Score**.
   - Search box to filter students quickly by name.

5. **View Summary Report**:
   - Click **View Summary Report** to open a clean summary report popup.
   - Includes a **Save Report to File** button to save `reports/summary_report.txt`.

6. **Automatic Saving (CSV)**:
   - All student records are automatically saved to `data/students.csv`.

---

## Project Structure

```
d:/studenttracker/
â”œâ”€â”€ Student.java              # Student data model (stores grades, calculates average, highest, lowest)
â”œâ”€â”€ StudentStorage.java       # Saves and loads students from data/students.csv
â”œâ”€â”€ StudentTrackerGUI.java    # Minimalist Desktop GUI (Swing)
â”œâ”€â”€ StudentGradeTracker.java  # Main program launcher (GUI by default, CLI via --console)
â”œâ”€â”€ run.bat                   # 1-click Windows runner
â”œâ”€â”€ data/
â”‚   â””â”€â”€ students.csv          # Stored student records
â””â”€â”€ reports/
    â””â”€â”€ summary_report.txt    # Saved summary reports
```

---

## How to Run

### Method 1: 1-Click Launch (Windows)
Double-click **`run.bat`** in the project folder.

### Method 2: Command Line (Windows / Mac / Linux)

1. Compile all Java files:
   ```bash
   javac *.java
   ```

2. Run the Desktop GUI:
   ```bash
   java StudentGradeTracker
   ```

3. Run in Console / CLI Mode (Optional):
   ```bash
   java StudentGradeTracker --console
   ```

---

## Author

Developed by **Bright Yankey** for the **CodeAlpha Java Programming Internship** (Task 1).