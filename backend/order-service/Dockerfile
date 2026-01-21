# ============ Build stage ============
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline

COPY src ./src

# Important: build a Spring Boot executable jar (repackage)
RUN mvn -q -B -DskipTests clean package

# ============ Runtime stage ============
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
