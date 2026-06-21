#!/bin/bash
echo "======================================"
echo "  Personal Document Vault - Build"
echo "======================================"

# Create output dir
mkdir -p out

# Compile all Java files
echo "Compiling..."
find src -name "*.java" | xargs javac -d out -sourcepath src

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "Running application..."
    echo "======================================"
    cd out && java Main
else
    echo "Compilation failed. Check errors above."
fi
