FROM maven:3.9.11-eclipse-temurin-17-jammy AS build
WORKDIR /workspace
COPY . .
RUN mvn -pl services/product-service -am -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/services/product-service/target/product-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]