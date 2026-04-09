# Use the official OpenJDK 21 image from Docker Hub
FROM eclipse-temurin:21-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file into the container
COPY target/*.jar  /app/user-mgmt-service.jar

# Expose the application's port (change if your application uses a different port)
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "user-mgmt-service.jar"]