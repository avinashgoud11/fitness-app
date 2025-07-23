######################## 1) Build stage ########################
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven descriptor first so dependencies are cached
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B -DskipTests clean package     # creates target/*.jar

######################## 2) Runtime stage #####################
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR produced in the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

