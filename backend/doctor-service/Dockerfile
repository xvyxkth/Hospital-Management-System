FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# First, copy and build common-lib
COPY ./common-lib /workspace/common-lib
WORKDIR /workspace/common-lib
RUN apt-get update && apt-get install -y maven && mvn clean install -DskipTests

# Then build doctor-service
WORKDIR /workspace/doctor-service
COPY ./doctor-service/pom.xml .
COPY ./doctor-service/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/doctor-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
