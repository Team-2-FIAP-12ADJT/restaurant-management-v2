FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
RUN chown -R appuser:appgroup /app
USER appuser

# Actual port is set at runtime via APP_PORT env var (default 8080)
ENV APP_PORT=8080
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD bash -c ">/dev/tcp/localhost/${APP_PORT:-8080}" || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
