#!/bin/bash

# Local development setup script

set -e

echo "🚀 Setting up RePlate for local development..."

# Check prerequisites
echo ""
echo "📋 Checking prerequisites..."

if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed"
    exit 1
fi

if ! command -v gradle &> /dev/null && ! command -v ./gradlew &> /dev/null; then
    echo "❌ Gradle is not installed"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=").*?(?=")' | head -n1)
echo "✅ Java: $JAVA_VERSION"

# Build all modules
echo ""
echo "📦 Building all modules..."
./gradlew clean build -x test

echo ""
echo "✅ Setup complete!"
echo ""
echo "📝 Available commands:"
echo "1. Run Auth Service:"
echo "   ./gradlew :auth-service:bootRun"
echo ""
echo "2. Run Listing Service:"
echo "   ./gradlew :listing-service:bootRun"
echo ""
echo "3. Run Filter Service:"
echo "   ./gradlew :filter-service:bootRun"
echo ""
echo "4. Run with Docker Compose:"
echo "   docker-compose up -d"
echo ""
echo "5. Run tests:"
echo "   ./gradlew test"

