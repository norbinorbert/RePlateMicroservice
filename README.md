# RePlate Microservices Architecture

A refactored version of the RePlate monolith split into 3 independent microservices with complete Docker and Kubernetes support.

## 📋 Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    NGINX Ingress                        │
├─────────────────────────────────────────────────────────┤
│  api.replate.local                                      │
│  ├─ /auth → Auth Service (8081)                        │
│  ├─ /listings → Listing Service (8082)                 │
│  └─ /images → Listing Service (8082)                   │
└────────┬──────────────────────────────────────┬─────────┘
         │                                      │
    ┌────▼────┐                         ┌───────▼──────┐
    │   Auth   │                         │   Listing    │
    │ Service  │                         │   Service    │
    │  (8081)  │                         │   (8082)     │
    └────┬─────┘                         └───────┬──────┘
         │                                       │
         │  JWT Tokens                           │
         │                                       │ City/Category Lookups
         │                                       │
         └───────────────┬──────────────────────┘
                         │
                    ┌────▼─────────┐
                    │   Location   │
                    │   Service    │
                    │   (8083)     │
                    │  (Internal)  │
                    └──────────────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
    ┌────▼──┐       ┌────▼──┐      ┌────▼──┐
    │Auth   │       │Listing│      │Location│
    │  DB   │       │  DB   │      │  DB    │
    └───────┘       └───────┘      └────────┘
    (MySQL)         (MySQL)        (MySQL)
```

## 🏗️ Service Boundaries

### Auth Service
- User registration and login
- JWT token generation and validation
- User management
- **Database:** `replate_auth`
- **Port:** 8081

### Listing Service
- Create and manage product listings
- Image upload and management
- Depends on Auth Service for JWT validation
- Depends on Location Service for city/category lookups
- **Database:** `replate_listings`
- **Port:** 8082

### Location Service (Internal)
- Geographic hierarchy (countries → counties → cities)
- Product categories with parent-child relationships
- Accessed internally by Listing Service
- No external Ingress (ClusterIP only)
- **Database:** `replate_locations`
- **Port:** 8083

## 🚀 Quick Start

### Prerequisites
- Java 21
- Gradle 8.0+
- Docker & Docker Compose
- Kubernetes 1.20+ (for K8s deployment)

### Build All Modules

```bash
./gradlew clean build
```

### Run Services Locally

```bash
# Terminal 1: Auth Service
./gradlew :auth-service:bootRun

# Terminal 2: Location Service
./gradlew :location-service:bootRun

# Terminal 3: Listing Service
./gradlew :listing-service:bootRun
```

Services will be available at:
- Auth: http://localhost:8081
- Listing: http://localhost:8082
- Location: http://localhost:8083

### Docker Compose (Single Command)

```bash
docker-compose up -d
```

Then access:
- Auth API: http://localhost:8081
- Listing API: http://localhost:8082
- Location API: http://localhost:8083

## 📦 Building Docker Images

```bash
# Build all images
docker build -f auth-service/Dockerfile -t replate/auth-service:latest .
docker build -f listing-service/Dockerfile -t replate/listing-service:latest .
docker build -f location-service/Dockerfile -t replate/location-service:latest .

# Or use the provided script
./scripts/build-images.sh
```

## ☸️ Kubernetes Deployment

### Quick Deploy

```bash
# 1. Create namespace and common resources
kubectl apply -f k8s/common-resources.yaml

# 2. Deploy MySQL
kubectl apply -f k8s/mysql.yaml

# 3. Deploy all services
kubectl apply -f k8s/auth-service.yaml
kubectl apply -f k8s/listing-service.yaml
kubectl apply -f k8s/location-service.yaml
```

### Verify Deployment

```bash
kubectl get all -n replate
kubectl logs -n replate deployment/auth-service
```

### Access Services

```bash
# Port forward for local testing
kubectl port-forward -n replate svc/auth-service 8081:80
kubectl port-forward -n replate svc/listing-service 8082:80
kubectl port-forward -n replate svc/location-service 8083:80
```

### Cleanup

```bash
kubectl delete namespace replate
```

## 📡 API Endpoints

### Auth Service

```
POST   /auth/register        - Register new user
POST   /auth/login          - Login and get JWT token
GET    /auth/health         - Health check
```

### Listing Service

```
POST   /listings            - Create listing (authenticated)
GET    /listings/{id}       - Get listing details
GET    /listings            - List all listings (paginated)
POST   /listings/{id}/images - Upload images (authenticated)
GET    /images/{id}         - Download image
GET    /listings/health     - Health check
```

### Location Service

```
GET    /categories/top-level      - Top-level categories
GET    /categories/{id}           - Category details
GET    /categories/{id}/subcategories - Subcategories
GET    /locations/cities/{id}     - City details
GET    /locations/health          - Health check
```

## 🔐 Configuration Management

### Environment Variables

Each service reads configuration from environment variables and ConfigMaps:

**Auth Service:**
- `DATABASE_HOST` - MySQL host
- `DATABASE_PORT` - MySQL port
- `DATABASE_USER` - MySQL user
- `DATABASE_PASSWORD` - MySQL password (Secret)
- `JWT_SECRET` - JWT signing secret (Secret)
- `JWT_EXPIRATION` - Token expiration in ms

**Listing Service:**
- All of the above plus:
- `AUTH_SERVICE_URL` - URL to Auth Service
- `app.images.upload-dir` - Image upload directory

**Location Service:**
- `DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_USER`, `DATABASE_PASSWORD`

### Kubernetes Secrets

Sensitive values are managed via Kubernetes Secrets:

```bash
# Create/Update a secret
kubectl create secret generic auth-service-secret \
  --from-literal=DATABASE_PASSWORD='mypassword' \
  --from-literal=JWT_SECRET='mysecret' \
  -n replate --dry-run=client -o yaml | kubectl apply -f -

# View secrets
kubectl get secrets -n replate
kubectl describe secret auth-service-secret -n replate
```

## 📊 Database Schema

### Auth Database (replate_auth)
```
users
├── id (PK)
├── username (UNIQUE)
├── email (UNIQUE)
├── password
├── phone_number (UNIQUE)
├── join_date
└── roles (many-to-many)
```

### Listing Database (replate_listings)
```
listings
├── id (PK)
├── title
├── description
├── price
├── date_posted
├── city_id (FK to location-service)
├── category_id (FK to location-service)
├── owner_id (FK to auth-service)
└── location_details

images
├── id (PK)
├── listing_id (FK)
├── image_name
├── file_path
└── mime_type
```

### Location Database (replate_locations)
```
categories
├── id (PK)
├── name (UNIQUE)
└── parent_category_id (FK)

countries
├── id (PK)
└── name (UNIQUE)

counties
├── id (PK)
├── name
└── country_id (FK)

cities
├── id (PK)
├── name
└── county_id (FK)
```

## 🧪 Testing

```bash
# Run tests for all modules
./gradlew test

# Run tests for specific module
./gradlew :auth-service:test

# Run with coverage
./gradlew jacocoTestReport
```

## 📚 Project Structure

```
RePlate/
├── shared-dtos/                    # Shared DTOs and exceptions
│   ├── src/main/java/.../dto/
│   ├── src/main/java/.../exception/
│   └── src/main/java/.../model/
├── auth-service/                   # Authentication Service
│   ├── src/main/java/.../auth/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── mapper/
│   │   └── util/
│   ├── src/main/resources/
│   └── Dockerfile
├── listing-service/                # Listing Service
│   └── (Similar structure)
├── location-service/               # Location Service
│   └── (Similar structure)
├── k8s/                            # Kubernetes manifests
│   ├── common-resources.yaml       # Namespace, RBAC, Ingress
│   ├── auth-service.yaml
│   ├── listing-service.yaml
│   ├── location-service.yaml
│   └── mysql.yaml
├── scripts/                        # Build and deploy scripts
├── build.gradle                    # Parent Gradle config
├── settings.gradle                 # Module definitions
├── DEPLOYMENT_GUIDE.md             # Detailed deployment docs
└── README.md                       # This file
```

## 🔄 Service-to-Service Communication

Services communicate via HTTP with service discovery through Kubernetes DNS:

```java
// Example: Listing Service calling Location Service
@Service
public class LocationClient {
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${location.service.url}")
    private String locationServiceUrl;
    
    public City getCity(Long cityId) {
        String url = locationServiceUrl + "/locations/cities/" + cityId;
        return restTemplate.getForObject(url, City.class);
    }
}
```

## 🛡️ Security Features

- **JWT Authentication:** Stateless token-based auth
- **Password Encryption:** BCrypt for password hashing
- **Database per Service:** Data isolation and independent scaling
- **Network Policies:** Kubernetes NetworkPolicy restricts traffic
- **RBAC:** Role-based access control for K8s resources
- **Non-root Containers:** Services run as unprivileged user
- **Read-only Root FS:** Containers have read-only root filesystem
- **Resource Limits:** CPU and memory limits enforced
- **Health Checks:** Liveness and readiness probes

## 📈 Monitoring and Observability

### Health Checks

All services expose `/health` endpoint:

```bash
curl http://localhost:8081/health
curl http://localhost:8082/health
curl http://localhost:8083/health
```

### Logs

View logs from services:

```bash
# Local
tail -f logs/auth-service.log

# Kubernetes
kubectl logs -n replate deployment/auth-service -f
```

### Metrics (Future Enhancement)

Add Micrometer and Prometheus for metrics collection:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
```

## 🤝 Contributing

1. Create a feature branch
2. Make changes to relevant service(s)
3. Update tests
4. Submit PR

## 📝 License

This project is part of RePlate and follows the same license as the original project.

## 📖 Additional Resources

- [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) - Detailed deployment instructions
- [Kubernetes Documentation](https://kubernetes.io/docs)
- [Spring Boot Microservices](https://spring.io/microservices)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

## ❓ FAQ

**Q: Can I run services individually?**
A: Yes! Each service is independent and can be run separately.

**Q: How do I add a new service?**
A: Follow the pattern of existing services and add to `settings.gradle`.

**Q: What about data migration from the monolith?**
A: See migration scripts in `scripts/db-migration/`.

**Q: How do I scale services differently?**
A: Use HPA (HorizontalPodAutoscaler) per service in K8s.

**Q: Is there service-to-service authentication?**
A: Currently JWT validation. Consider OAuth2 for production.

