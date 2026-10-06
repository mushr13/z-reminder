@echo off
title Push Z-Reminder to GitHub
cd /d "%~dp0"
echo ========================================================
echo   Pushing Z-Reminder to GitHub (mushr13/z-reminder)
echo ========================================================
echo.
git push -u origin master
echo.
if %errorlevel% equ 0 (
    echo [SUCCESS] Code successfully pushed to GitHub!
) else (
    echo [ERROR] Push failed. Make sure you created the empty repo 'z-reminder' on https://github.com/new first!
)
echo.
pause
