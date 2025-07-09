# Dockerfile

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy and build
COPY . .

# Run the built jar
CMD ["java", "-jar", "target/api-gateway-service-0.0.1-SNAPSHOT.jar"]