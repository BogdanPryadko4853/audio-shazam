# Сборка приложения
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /workspace/app
COPY . .
RUN ./mvnw clean package -DskipTests

# Финальный образ
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]