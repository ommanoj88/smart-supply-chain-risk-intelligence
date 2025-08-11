#!/bin/bash

echo "=== Java Environment Diagnosis ==="
echo "Java version:"
java -version 2>&1

echo -e "\nJAVA_HOME:"
echo $JAVA_HOME

echo -e "\nJava location:"
which java

echo -e "\nMaven version:"
mvn -version 2>&1

echo -e "\nMaven location:"
which mvn

echo -e "\n=== Attempting Build ==="
echo "Starting Maven clean compile..."
mvn clean compile -X 2>&1 | head -50
