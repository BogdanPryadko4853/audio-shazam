# Этап сборки
FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Этап выполнения
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]