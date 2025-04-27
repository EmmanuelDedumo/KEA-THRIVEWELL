# Start from an official Java image
FROM openjdk:17-jdk-slim

# Set a working directory
WORKDIR /app

# Copy your jar file into the container
COPY target/*.jar app.jar

# Expose port (the one Spring Boot uses)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
