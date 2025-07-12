######################## 1) Build stage ########################
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom first so Maven can cache dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline               # download deps into cache

# Copy the source code and build the JAR
COPY src ./src
RUN mvn -B -DskipTests clean package           # produces target/*.jar

######################## 2) Runtime stage ######################
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
