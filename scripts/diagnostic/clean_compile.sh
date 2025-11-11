#!/bin/bash

# We only compile rather than run tests to avoid the need for an Open AI API key
echo "Compiling in a Docker container to make sure dependencies can be downloaded"

# Navigate to project root (two levels up from /scripts/diagnostic)
cd "$(dirname "$0")/../.." || exit 1

docker run --rm -v "$(pwd)":/app -w /app maven:3.9-eclipse-temurin-21 mvn clean compile