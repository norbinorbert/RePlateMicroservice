# Multi-stage build for Listing Service
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
COPY . .
RUN chmod +x ./gradlew && ./gradlew :listing-service:build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/listing-service/build/libs/*.jar app.jar

# Create non-root user
RUN addgroup -g 1000 appuser && adduser -D -u 1000 -G appuser appuser
USER appuser

EXPOSE 8082
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8082/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

