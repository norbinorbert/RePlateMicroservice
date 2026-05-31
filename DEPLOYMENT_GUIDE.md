# RePlate Microservices - Deployment Guide

## Project Structure

The RePlate monolith has been refactored into a multi-module Gradle project with 3 microservices:

```
RePlate/
├── shared-dtos/           # Shared DTOs and exceptions across services
├── auth-service/          # Authentication & User Management Service
├── listing-service/       # Listings & Images Management Service
├── location-service/      # Locations & Categories Service
├── k8s/                   # Kubernetes deployment manifests
└── build.gradle           # Parent Gradle build configuration
```

## Microservices Overview

### 1. Auth Service (Port 8081)
**Responsibility:** User authentication, registration, and JWT token generation/validation

**Database:** `replate_auth`

**Endpoints:**
- `POST /auth/register` - User registration
- `POST /auth/login` - User login and token generation
- `GET /health` - Health check

**Internal Endpoints (for other services):**
- `GET /auth/validate-token` - Token validation (to be implemented for service-to-service auth)

### 2. Listing Service (Port 8082)
**Responsibility:** Listing management, image uploads, and user listings

**Database:** `replate_listings`

**Endpoints:**
- `POST /listings` - Create new listing (requires auth)
- `GET /listings/{id}` - Get listing details
- `GET /listings` - List all listings with pagination (public)
- `POST /listings/{id}/images` - Upload images for listing (requires auth)
- `GET /images/{id}` - Download image
- `GET /health` - Health check

**Dependencies:**
- Auth Service (for JWT validation)
- Location Service (for city and category lookups)

### 3. Location Service (Port 8083)
**Responsibility:** Geographic data and product categories (internal service)

**Database:** `replate_filters`

**Endpoints:**
- `GET /categories/top-level` - Top-level categories
- `GET /categories/{id}` - Category details
- `GET /categories/{id}/subcategories` - Subcategories
- `GET /locations/cities/{id}` - City details
- `GET /health` - Health check

**Access:** Internal only (ClusterIP, no external Ingress)

## Building Docker Images

### Build All Images

```bash
# Build auth-service image
cd RePlate
docker build -f auth-service/Dockerfile -t replate/auth-service:latest .

# Build listing-service image
docker build -f listing-service/Dockerfile -t replate/listing-service:latest .

# Build location-service image
docker build -f location-service/Dockerfile -t replate/location-service:latest .
```

### Push to Registry

```bash
# Replace with your registry
docker tag replate/auth-service:latest your-registry/replate/auth-service:latest
docker push your-registry/replate/auth-service:latest

# Repeat for other services
```

## Kubernetes Deployment

### Prerequisites

1. Kubernetes cluster (v1.20+)
2. kubectl configured with cluster access
3. Docker images built and available in registry
4. NGINX Ingress Controller installed (for external traffic routing)

### Deployment Steps

#### Step 1: Create Namespace and Common Resources

```bash
kubectl apply -f k8s/common-resources.yaml
```

This creates:
- Namespace `replate`
- Service Account and RBAC roles
- Ingress configuration
- NetworkPolicy

#### Step 2: Deploy MySQL Database

```bash
kubectl apply -f k8s/mysql.yaml
```

This creates:
- MySQL Deployment with persistent storage
- PersistentVolume and PersistentVolumeClaim
- MySQL Service (internal ClusterIP)

**Important:** Update MySQL credentials in `k8s/mysql.yaml` before deploying to production!

#### Step 3: Create Database Schemas

Wait for MySQL to be ready, then create databases:

```bash
kubectl exec -it deployment/mysql -n replate -- mysql -uroot -p$MYSQL_ROOT_PASSWORD

# In MySQL CLI:
CREATE DATABASE replate_auth;
CREATE DATABASE replate_listings;
CREATE DATABASE replate_filters;

# Grant permissions:
GRANT ALL PRIVILEGES ON replate_auth.* TO 'replate_user'@'%';
GRANT ALL PRIVILEGES ON replate_listings.* TO 'replate_user'@'%';
GRANT ALL PRIVILEGES ON replate_filters.* TO 'replate_user'@'%';
FLUSH PRIVILEGES;
```

#### Step 4: Update Service Configuration

Before deploying services, update the secrets in K8s manifests:

**auth-service.yaml:**
```yaml
stringData:
  DATABASE_PASSWORD: "your_actual_password"
  JWT_SECRET: "$(openssl rand -base64 32)"
```

**listing-service.yaml:**
```yaml
stringData:
  DATABASE_PASSWORD: "your_actual_password"
  JWT_SECRET: "your_jwt_secret_from_auth_service"
```

**location-service.yaml:**
```yaml
stringData:
  DATABASE_PASSWORD: "your_actual_password"
```

#### Step 5: Deploy Services

```bash
# Deploy Auth Service
kubectl apply -f k8s/auth-service.yaml

# Deploy Listing Service
kubectl apply -f k8s/listing-service.yaml

# Deploy Location Service
kubectl apply -f k8s/filter-service.yaml
```

#### Step 6: Verify Deployments

```bash
# Check all deployments
kubectl get deployments -n replate
kubectl get pods -n replate
kubectl get services -n replate

# View logs for a service
kubectl logs -n replate deployment/auth-service -f

# Check service health
kubectl exec -it pod/mysql-0 -n replate -- mysql -ureplate_user -p$MYSQL_PASSWORD -e "SELECT 1;"
```

## Configuration Management

### ConfigMaps (Public Configuration)

Each service has a ConfigMap containing non-sensitive configuration:

```yaml
DATABASE_HOST: "mysql-service"
DATABASE_PORT: "3306"
DATABASE_USER: "replate_user"
SPRING_PROFILES_ACTIVE: "prod"
```

Update via:
```bash
kubectl edit configmap auth-service-config -n replate
```

### Secrets (Sensitive Configuration)

Sensitive values are stored in Kubernetes Secrets:

```yaml
DATABASE_PASSWORD: "encrypted_password"
JWT_SECRET: "encrypted_secret"
```

**Security Best Practices:**
- Use Sealed Secrets or External Secrets Operator for production
- Rotate secrets regularly
- Never commit secrets to version control
- Use RBAC to limit secret access

Update via:
```bash
kubectl edit secret auth-service-secret -n replate
```

## Service-to-Service Communication

### Internal DNS

Services communicate via Kubernetes DNS:

- Auth Service: `http://auth-service` (or `http://auth-service.replate.svc.cluster.local`)
- Location Service: `http://location-service`
- Listing Service: `http://listing-service`

### Example: Listing Service calling Location Service

```java
@Configuration
public class RestClientConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// In service:
@Service
public class ListingServiceImpl {
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${location.service.url}")
    private String locationServiceUrl;
    
    public void validateCity(Long cityId) {
        try {
            String url = locationServiceUrl + "/locations/cities/" + cityId;
            ResponseEntity<CitySimpleOutDto> response = restTemplate.getForEntity(url, CitySimpleOutDto.class);
            // Handle response
        } catch (HttpClientErrorException e) {
            throw new BadRequestException("Invalid city ID");
        }
    }
}
```

## Ingress Routing

### External Routes (via api.replate.local)

- `api.replate.local/auth/*` → Auth Service (Port 80)
- `api.replate.local/listings/*` → Listing Service (Port 80)
- `api.replate.local/images/*` → Listing Service (Port 80)

### Internal Routes (via internal-api.replate.local)

- `internal-api.replate.local/locations/*` → Location Service (Port 80)
- `internal-api.replate.local/categories/*` → Location Service (Port 80)

### Update Hosts File

Add to `/etc/hosts` (Linux/Mac) or `C:\Windows\System32\drivers\etc\hosts` (Windows):

```
127.0.0.1  api.replate.local
127.0.0.1  internal-api.replate.local
```

## Scaling and Monitoring

### Horizontal Pod Autoscaling

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: auth-service-hpa
  namespace: replate
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: auth-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### Monitoring and Logging

- **Prometheus:** Collect metrics from services
- **Grafana:** Visualize metrics
- **ELK Stack:** Centralized logging
- **Jaeger:** Distributed tracing for inter-service calls

## Troubleshooting

### Service not starting

```bash
# Check pod logs
kubectl logs -n replate pod/auth-service-xyz-abc

# Describe pod for events
kubectl describe pod -n replate auth-service-xyz-abc
```

### Database connection issues

```bash
# Check MySQL service
kubectl get svc -n replate mysql-service

# Test connection from pod
kubectl exec -it pod/auth-service-xyz-abc -n replate -- \
  mysql -h mysql-service -u replate_user -p$DATABASE_PASSWORD -e "SELECT 1;"
```

### Service-to-service communication

```bash
# Test internal DNS resolution
kubectl exec -it pod/listing-service-xyz-abc -n replate -- \
  nslookup auth-service

# Test HTTP connectivity
kubectl exec -it pod/listing-service-xyz-abc -n replate -- \
  curl http://auth-service/health
```

## Database Initialization (Optional - DDL Auto)

For development/testing, you can enable automatic DDL:

```yaml
# In application-create.properties
spring.jpa.hibernate.ddl-auto=create
```

Then deploy with create profile:

```bash
kubectl set env deployment/auth-service -n replate \
  SPRING_PROFILES_ACTIVE=create
```

Wait for database setup, then switch back to validate mode:

```bash
kubectl set env deployment/auth-service -n replate \
  SPRING_PROFILES_ACTIVE=prod
```

## Cleanup

To remove all RePlate resources:

```bash
# Delete all services
kubectl delete -f k8s/auth-service.yaml
kubectl delete -f k8s/listing-service.yaml
kubectl delete -f k8s/filter-service.yaml
kubectl delete -f k8s/mysql.yaml
kubectl delete -f k8s/common-resources.yaml

# Delete namespace (removes everything in it)
kubectl delete namespace replate
```

## Further Enhancements

1. **API Gateway:** Add Kong or Spring Cloud Gateway for centralized API management
2. **Service Mesh:** Implement Istio for advanced networking, circuit breaking, and observability
3. **Event-Driven:** Use message broker (RabbitMQ/Kafka) for asynchronous communication
4. **Cache Layer:** Add Redis for caching and session management
5. **Security:** Implement OAuth2 with Spring Security for better auth
6. **CI/CD:** Set up GitHub Actions or GitLab CI for automated builds and deployments

