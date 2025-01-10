FROM openjdk:20-jdk-slim

WORKDIR /app

COPY target/tasksmanager-0.0.1.jar /app/pro_task_taskmanager.jar

EXPOSE 8081

CMD ["java", "-jar", "/app/pro_task_taskmanager.jar"]