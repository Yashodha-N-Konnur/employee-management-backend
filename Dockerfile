# ─────────────────────────────────────────────────────────────────────────────
# Multi-stage Dockerfile for Employee Management System
# Stage 1 – Build  : Maven + JDK 17
# Stage 2 – Runtime: Slim JRE 17 (smaller final image ~200 MB vs ~500 MB)
# ─────────────────────────────────────────────────────────────────────────────

# ── STAGE 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom.xml first – Docker cache layer reused if pom.xml unchanged
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build (skip tests in Docker build; run separately in CI)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── STAGE 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy the fat JAR from builder stage
COPY --from=builder /app/target/employee-management-system-*.jar app.jar

# Log directory
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# JVM tuning for containers
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
