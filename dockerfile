# First stage: Build the JAR file
FROM gradle:jdk17-jammy AS build

# Set the working directory
WORKDIR /app

# Copy build files
COPY build.gradle settings.gradle ./

# Copy source files
COPY src/ src/

# Run Gradle build task to create JAR file
RUN gradle clean build --no-daemon -x test

# Second stage: Create an image from the JAR file
FROM openjdk:17-oracle as run

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Start the app
CMD ["java", "-jar", "app.jar"]