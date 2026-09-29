<div align="center">

# STUDENT GRADE TRACKER
### Academic Grade Management & Performance Analytics

[![Java](https://img.shields.io/badge/Java-8%2B%20%7C%2011%20%7C%2017%20%7C%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-0284C7?style=for-the-badge)]()
[![Internship](https://img.shields.io/badge/CodeAlpha-Task%201%20Completed-059669?style=for-the-badge)]()
[![Interface](https://img.shields.io/badge/Interface-Modern%20Swing%20GUI%20%2B%20CLI-0F172A?style=for-the-badge)]()
[![Dependencies](https://img.shields.io/badge/Dependencies-Zero%20(Pure%20Java%20SE)-success?style=for-the-badge)]()

<br/>

<p align="center">
  <b>A clean, beginner-friendly desktop application and CLI tool that enables teachers to enter student grades and instantly compute their average, highest, and lowest scores.</b>
</p>

</div>

---

## Project Overview

**Student Grade Tracker** is developed for the **CodeAlpha Java Programming Internship (Task 1)**. 

### Official Task Specification
> *"Develop a program that allows a teacher to enter students' grades and compute their average, highest, and lowest scores."*

Built in pure Java SE with modern Swing aesthetics, the application strictly adheres to the core project requirement: capturing student scores and computing the **Average**, **Highest**, and **Lowest** scores without confusing jargon, complex external dependencies, or unrequested letter grade curves.

---

## Key Features

- **Modern Minimalist Desktop GUI**:
  - Clean slate (`#F8FAFC`) and white card layout with subtle 1px borders (`#E2E8F0`).
  - Generous 44px Call-To-Action (CTA) buttons for comfortable clicking and touch interaction.
  - Header status indicator displaying `[SYSTEM ACTIVE]`.
- **Core Grade Analytics (Real-Time KPI Cards)**:
  - **Class Average**: Computes overall average score across all students.
  - **Highest Score**: Highlights the top score achieved in class (highlighted in emerald).
  - **Lowest Score**: Identifies the minimum score recorded (highlighted in crimson).
  - **Total Students**: Real-time counter of total students enrolled.
- **Direct Score Tracking (Zero Unneeded Jargon)**:
  - Focuses purely on numeric grades (0 to 100) as required by the assignment.
  - Removed confusing letter grade curves (A, B, C, D, F) so results remain strictly objective and aligned with the prompt.
- **Student Entry & Management**:
  - Add or update students with comma-separated grades (e.g., `85, 92, 78`).
  - Instant live search bar to filter student records by name.
  - Single-click row selection to edit or delete student records.
- **In-App Summary Report**:
  - Click **"View Summary Report"** to open a clean dialog showing class metrics and individual student breakdowns.
  - Optional **"Save Report to File"** button to export records directly to `reports/summary_report.txt`.
- **Zero-Setup File Persistence**:
  - Automatically saves all records to `data/students.csv` and reloads on startup.
- **Dual-Mode Execution**:
  - Default: Modern Swing Desktop GUI.
  - Console fallback: Run with `--console` for full terminal CLI mode.

---

## Project Structure

```
d:/studenttracker/
|-- Student.java              # Student data model (grades, average, highest, lowest)
|-- StudentStorage.java       # CSV file persistence engine with starter seed data
|-- StudentTrackerGUI.java    # Modern Swing Desktop GUI (cards, form, table, report)
|-- StudentGradeTracker.java  # Application entry point (launches GUI or CLI)
|-- run.bat                   # 1-click Windows compile & launch script
|-- data/
|   \-- students.csv          # Stored student records
\-- reports/
    \-- summary_report.txt    # Exported class performance summary reports
```

---

## How to Run

### Method 1: 1-Click Launch (Windows)
Double-click **`run.bat`** in the project directory.

### Method 2: Command Line (Windows, macOS, Linux)

1. **Compile all Java files:**
   ```bash
   javac *.java
   ```

2. **Launch Modern Desktop GUI (Default):**
   ```bash
   java StudentGradeTracker
   ```

3. **Launch Terminal CLI Mode (Optional):**
   ```bash
   java StudentGradeTracker --console
   ```

---

## Technical Standards & Quality

| Metric | Status | Details |
|---|---|---|
| **Compiler Lint** | **0 Warnings / 0 Errors** | Fully compliant with `javac -Xlint:all *.java` |
| **Dependencies** | **Zero** | 100% Pure Java SE standard library (`javax.swing`, `java.awt`, `java.io`, `java.util`) |
| **Persistence** | **CSV File** | Auto-saves to `data/students.csv` on every change |
| **Code Documentation** | **Beginner-Friendly** | Inline comments written in simple English explaining every function |
| **Input Validation** | **Strict** | Enforces range 0-100 for all scores, prevents blank student names |

---

## Author

Developed by **Bright Yankey** for the **CodeAlpha Java Programming Internship** (Task 1: Student Grade Tracker).