# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw -DskipTests package

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Koyeb daje PORT jako env, więc ustawiamy server.port dynamicznie:
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]