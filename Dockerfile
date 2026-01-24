FROM eclipse-temurin:21-jre

WORKDIR /app
COPY app.jar /app/app.jar

# Elastic Beanstalk forwards to port 5000 by default
EXPOSE 5000

# EB usually sets PORT=5000; fallback to 5000
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-5000} -jar /app/app.jar"]
