# Start with a base image containing Java runtime
FROM openjdk:17-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the build directory contents into the container at /app
COPY ./server/build/ .

# Run the app
ENTRYPOINT ["java", "-jar", "libs/Let-Them-Cook.jar"]