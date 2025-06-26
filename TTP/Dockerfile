# Use OpenJDK 21 
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy Maven wrapper files first (for better caching)
COPY mvnw .
COPY .mvn .mvn/

# Copy pom.xml to download dependencies
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src src/

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Expose the port that the app runs on
EXPOSE 8080

# Command to run the application
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar target/*.jar"]