# Multi-stage build for api-gateway

# Stage 1: Build
FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

# Copy and build api-gateway
WORKDIR /workspace/api-gateway
COPY ./api-gateway/pom.xml .
COPY ./api-gateway/src ./src

RUN apt-get update && apt-get install -y maven && mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built artifact from build stage
COPY --from=build /workspace/api-gateway/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
