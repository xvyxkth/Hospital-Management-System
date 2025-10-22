# Multi-stage build for billing-service

# Stage 1: Build
FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

# Copy common-lib first
COPY ./common-lib /workspace/common-lib
WORKDIR /workspace/common-lib
RUN apt-get update && apt-get install -y maven && mvn clean install -DskipTests

# Copy and build billing-service
WORKDIR /workspace/billing-service
COPY ./billing-service/pom.xml .
COPY ./billing-service/src ./src

RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built artifact from build stage
COPY --from=build /workspace/billing-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
