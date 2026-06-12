# Maven Build (bliver slettet når image er færdig bygget)
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
# Cache dependencies
RUN mvn dependency:go-offline -B
COPY src ./src
# Building snapshot
RUN mvn clean package -DskipTests -B

# Java Runtime (til at køre snapshot, den kode som køre når containeren starter)
FROM eclipse-temurin:25-jre
WORKDIR /app
# Tilføjer appuser til image (undgår brugen af root user som kan give sikkerheds ricisi)
# Installere curl og updates
RUN addgroup --system appgroup \
    && adduser --system --ingroup appgroup appuser \
    && apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Kopirere maven snapshot, og gemmer i mappen som app.jar
COPY --from=build /app/target/*.jar app.jar
# Giver appuser rettigheder til app.jar
RUN chown appuser:appgroup app.jar
USER appuser

# Åbner port 8080
EXPOSE 8080

# Springboot healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Fortæller at app.jar er en container, som giver den containerens ram istedet for systemet,
# hermed også kun 75% af containerens ram, så der er noget til overs til andre ting.
# Java.security bliver gemt i en anden mappe, som forkortere startup tid.
# -jar fortæller at container skal køre app.jar i java.
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]