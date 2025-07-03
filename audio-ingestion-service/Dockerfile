# Этап сборки (build stage)
FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Собираем приложение с профилем docker
RUN mvn clean package -DskipTests

# Этап выполнения (runtime stage)
FROM eclipse-temurin:17-jre-jammy

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=builder /app/target/*.jar app.jar

# Открываем порт, указанный в конфигурации
EXPOSE 8000

# Команда для запуска приложения с docker профилем
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]