# Этап сборки
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /workspace/app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Этап выполнения
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]