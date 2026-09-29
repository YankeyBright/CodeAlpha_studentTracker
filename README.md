<div align="center">

# STUDENT GRADE TRACKER
### Academic Performance Management & Analytics System

[![Java](https://img.shields.io/badge/Java-8%2B%20%7C%2011%20%7C%2017%20%7C%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-0284C7?style=for-the-badge)]()
[![Internship](https://img.shields.io/badge/CodeAlpha-Task%201%20Completed-059669?style=for-the-badge)]()
[![Interface](https://img.shields.io/badge/Interface-Modern%20Swing%20GUI%20%2B%20CLI-0F172A?style=for-the-badge)]()
[![Dependencies](https://img.shields.io/badge/Dependencies-Zero%20(Pure%20Java%20SE)-success?style=for-the-badge)]()

<br/>
<p align="center">
  <b>A modern, minimalist desktop application and CLI suite for managing student academic records, tracking grade statistics, and generating official summary reports.</b>
</p>

</div>

---

## 📋 Project Overview

**Student Grade Tracker** is a professional academic management application developed for **CodeAlpha Java Programming Internship (Task 1)**. It provides educators with a centralized workspace to input, manage, analyze, and report student performance.

Engineered with a **minimalist, uncluttered design system** (Slate `#0F172A` & Clean Neutral `#F8FAFC`), it delivers real-time statistical analytics, letter grade grading curves, instant search filtering, persistent CSV storage, and official printable HTML summary reports.

---

## 🌟 Key Features

- **Modern Minimalist Desktop GUI**: Designed with flat white cards, subtle 1px dividers, anti-aliased button renderers, and zero visual clutter.
- **Real-Time Statistical KPI Cards**:
  - 📊 **Class Overall Average**: Calculates the true class-wide average grade across all student assignments.
  - 🏆 **Highest Score**: Highlights the top grade achieved in the cohort.
  - 📉 **Lowest Score**: Dynamically detects the minimum score recorded.
  - 👥 **Total Enrolled**: Live count of active students on the roster.
- **Letter Grade Computing Engine**: Automatically maps numeric averages to standard academic grading bands:
  - **A** (90.0% – 100.0%)
  - **B** (80.0% – 89.9%)
  - **C** (70.0% – 79.9%)
  - **D** (60.0% – 69.9%)
  - **F** (< 60.0%)
- **Dynamic Instant Search**: Filter student records in real time by typing a student name or letter grade (`A`, `B`, `C`...).
- **Official Printable HTML Reports**: Export an academic summary report (`reports/Student_Summary_Report.html`) with print-to-PDF support and browser preview with one click.
- **Zero-Setup File Persistence**: Automatically saves and restores student records to `data/students.csv` on every modification.
- **Dual Mode (GUI & CLI)**: Double-click launches the desktop interface by default; running with `--console` launches the classic terminal interface.

---

## 🏗️ Architecture & Component Design

```
d:/studenttracker/
├── Student.java           # Core domain entity (grades list, averages, min, max, letter grade, CSV serialization)
├── StudentStorage.java    # Persistence engine (CSV read/write with auto-healing seed data)
├── StudentTrackerGUI.java # Modern Swing Desktop GUI (KPI cards, input form, searchable table, report generator)
├── StudentGradeTracker.java # Bootstrap entry point with dual-mode launcher (GUI by default, CLI via --console)
├── run.bat                # 1-click Windows compile & launch script
├── data/                  # Auto-generated persistent data directory
│   └── students.csv       # Stored student records
└── reports/               # Generated printable academic reports
    └── Student_Summary_Report.html
```

---

## 🚀 How to Run

### Option 1: 1-Click Launch (Windows)
Double-click **`run.bat`** in the project folder to compile and open the Desktop GUI.

### Option 2: Command Line (Any OS)

1. **Compile all source files:**
   ```bash
   javac Student.java StudentStorage.java StudentTrackerGUI.java StudentGradeTracker.java
   ```

2. **Launch Desktop GUI:**
   ```bash
   java StudentGradeTracker
   ```

3. **Launch Terminal / Console Mode:**
   ```bash
   java StudentGradeTracker --console
   ```

---

## 🔒 Technical Standards & Best Practices

| Standard | Implementation |
|---|---|
| **Data Structures** | Uses `ArrayList<Double>` for flexible grade collections per student and `List<Student>` for the class roster |
| **Input Validation** | Rejects negative scores or values $> 100$; validates non-empty names and handles comma-separated grade strings |
| **Encapsulation** | Strict private fields with immutable list copies returned from getters |
| **Fault Tolerance** | Pre-populates sample class data if `students.csv` is missing or empty |
| **Zero Dependencies** | Built using 100% standard Java SE (`javax.swing`, `java.awt`, `java.io`, `java.util`) |

---

## 👨‍💻 Author & Credits

Developed by **Bright Yankey** for the **CodeAlpha Java Programming Internship** (Task 1: Student Grade Tracker).