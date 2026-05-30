#!/bin/bash

# Build script for RePlate microservices Docker images

set -e

REGISTRY="${1:-replate}"
VERSION="${2:-latest}"

echo "🐳 Building RePlate Docker images..."
echo "Registry: $REGISTRY"
echo "Version: $VERSION"

# Build parent first
echo ""
echo "📦 Building Auth Service..."
docker build -f auth-service/Dockerfile -t "${REGISTRY}"/auth-service:"${VERSION}" .

echo ""
echo "📦 Building Listing Service..."
docker build -f listing-service/Dockerfile -t "${REGISTRY}"/listing-service:"${VERSION}" .

echo ""
echo "📦 Building Filter Service..."
docker build -f filter-service/Dockerfile -t "${REGISTRY}"/filter-service:"${VERSION}" .

echo ""
echo "✅ All images built successfully!"
echo ""
echo "Images created:"
docker images | grep replate

echo ""
echo "Next steps:"
echo "1. Tag images for your registry:"
echo "   docker tag ${REGISTRY}/auth-service:${VERSION} your-registry/replate/auth-service:${VERSION}"
echo "2. Push to registry:"
echo "   docker push your-registry/replate/auth-service:${VERSION}"
echo "3. Run with Docker Compose:"
echo "   docker-compose up -d"

