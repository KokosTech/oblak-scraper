FROM gradle:7.6.0-jdk19 AS builder

# Copy the build files to the container
COPY . /home/gradle/src

# Set the working directory
WORKDIR /home/gradle/src

# Build the project
RUN gradle build --no-daemon

# Use a slim JRE image as the base image

FROM openjdk:19-alpine

# Copy the built JAR file from the builder image
COPY --from=builder /home/gradle/src/build/libs/Oblak-scraper-1.0-SNAPSHOT.jar /app/myapp.jar

# Install necessary packages
RUN apk add --no-cache bash

# Copy the necessary libraries to the container
COPY --from=builder /home/gradle/src/build/libs/jsoup-1.15.4.jar /app/lib/
COPY --from=builder /home/gradle/src/build/libs/gson-2.8.9.jar /app/lib/

# Set the classpath
ENV CLASSPATH /app/lib/*

# Expose the port used by the application
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "/app/myapp.jar"]