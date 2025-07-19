# Use official Maven image for build stage
FROM maven:3.9.4-amazoncorretto-17 as build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use lightweight JDK image to run the app
FROM amazoncorretto:17

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
