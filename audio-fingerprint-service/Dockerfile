# Этап сборки
FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /workspace
# Копируем только нужные файлы для кэширования зависимостей
COPY pom.xml .
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Этап выполнения
FROM eclipse-temurin:17-jdk-jammy

# Устанавливаем chromaprint
RUN apt-get update && \
    apt-get install -y software-properties-common && \
    add-apt-repository ppa:spvkgn/ppa -y && \
    apt-get update && \
    apt-get install -y libchromaprint-tools ffmpeg && \
    rm -rf /var/lib/apt/lists/* && \
    ln -s /usr/lib/x86_64-linux-gnu/chromaprint/tools/fpcalc /usr/local/bin/fpcalc

WORKDIR /app
# Копируем JAR из этапа сборки
COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]