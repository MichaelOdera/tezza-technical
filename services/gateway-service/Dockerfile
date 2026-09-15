# --- Stage 1: Build Phase ---
FROM maven:3.9.11-eclipse-temurin-17-jammy AS build
WORKDIR /workspace

# 1. Copy ONLY the parent configuration files first
COPY pom.xml .
COPY services/common-contracts/pom.xml ./services/common-contracts/
COPY services/gateway-service/pom.xml ./services/gateway-service/

# 2. Cache Maven dependency layers without code changes
RUN mvn dependency:go-offline -B -pl services/gateway-service -am

# 3. Now copy the rest of your source code
COPY . .

# 4. Compile and package the executable artifact
RUN mvn -pl services/gateway-service -am -DskipTests package

# --- Stage 2: Runtime Phase ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the compiled executable from the build stage
COPY --from=build /workspace/services/gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]