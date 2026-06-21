@echo off
echo ======================================
echo   Personal Document Vault - Build
echo ======================================

mkdir out 2>nul

echo Compiling...
for /r src %%f in (*.java) do (
    javac -d out -sourcepath src "%%f"
)

if %errorlevel% == 0 (
    echo Compilation successful!
    echo.
    echo Running application...
    echo ======================================
    cd out
    java Main
) else (
    echo Compilation failed. Check errors above.
)
pause
