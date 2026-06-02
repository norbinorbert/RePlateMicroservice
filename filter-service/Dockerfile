# Multi-stage build for Filter Service
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
COPY . .
RUN chmod +x ./gradlew && ./gradlew :filter-service:build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/filter-service/build/libs/*.jar app.jar

RUN apk add --no-cache curl

EXPOSE 8083
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8083/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

