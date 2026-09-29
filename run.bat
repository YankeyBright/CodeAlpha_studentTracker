@echo off
title Student Grade Tracker
echo ========================================================
echo   Student Grade Tracker - CodeAlpha Task 1
echo ========================================================
echo.
echo Compiling project...
javac Student.java StudentStorage.java StudentTrackerGUI.java StudentGradeTracker.java
if %errorlevel% neq 0 (
    echo Compilation error occurred.
    pause
    exit /b %errorlevel%
)
echo Launching GUI Application...
start javaw StudentGradeTracker
exit