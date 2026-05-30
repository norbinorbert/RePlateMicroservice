#!/bin/bash

# Kubernetes deployment script for RePlate microservices

set -e

NAMESPACE="${1:-replate}"
ENVIRONMENT="${2:-dev}"

echo "☸️  Deploying RePlate to Kubernetes..."
echo "Namespace: $NAMESPACE"
echo "Environment: $ENVIRONMENT"

# Create namespace if it doesn't exist
if ! kubectl get namespace "$NAMESPACE" &> /dev/null; then
    echo "📍 Creating namespace $NAMESPACE..."
    kubectl create namespace "$NAMESPACE"
else
    echo "📍 Namespace $NAMESPACE already exists"
fi

# Label namespace for network policies
kubectl label namespace "$NAMESPACE" name="$NAMESPACE" --overwrite

# Apply common resources
echo ""
echo "📋 Applying common resources (RBAC, Ingress, NetworkPolicy)..."
kubectl apply -f k8s/common-resources.yaml

# Apply MySQL
echo ""
echo "🗄️  Deploying MySQL database..."
kubectl apply -f k8s/mysql.yaml

# Wait for MySQL to be ready
echo ""
echo "⏳ Waiting for MySQL to be ready..."
kubectl rollout status deployment/mysql -n "$NAMESPACE" --timeout=5m

# Apply services
echo ""
echo "🔐 Deploying Auth Service..."
kubectl apply -f k8s/auth-service.yaml

echo ""
echo "📝 Deploying Listing Service..."
kubectl apply -f k8s/listing-service.yaml

echo ""
echo "📍 Deploying Location Service..."
kubectl apply -f k8s/location-service.yaml

# Wait for all deployments
echo ""
echo "⏳ Waiting for all services to be ready..."
kubectl rollout status deployment/auth-service -n "$NAMESPACE" --timeout=5m
kubectl rollout status deployment/listing-service -n "$NAMESPACE" --timeout=5m
kubectl rollout status deployment/location-service -n "$NAMESPACE" --timeout=5m

# Show status
echo ""
echo "✅ Deployment complete!"
echo ""
echo "📊 Deployments:"
kubectl get deployments -n "$NAMESPACE"

echo ""
echo "🔗 Services:"
kubectl get services -n "$NAMESPACE"

echo ""
echo "🌐 Ingress:"
kubectl get ingress -n "$NAMESPACE"

echo ""
echo "📝 Next steps:"
echo "1. Update /etc/hosts with ingress IPs"
echo "2. Access services:"
echo "   - Auth: http://api.replate.local/auth"
echo "   - Listings: http://api.replate.local/listings"
echo "   - Locations: http://internal-api.replate.local/locations (internal only)"
echo ""
echo "3. View logs:"
echo "   kubectl logs -n $NAMESPACE deployment/auth-service -f"
echo ""
echo "4. Port forward for testing:"
echo "   kubectl port-forward -n $NAMESPACE svc/auth-service 8081:80"

