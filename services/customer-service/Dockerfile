# --- Stage 1: Build Phase ---
FROM maven:3.9.11-eclipse-temurin-17-jammy AS build
WORKDIR /workspace

# 1. Copy parent and necessary module configuration definitions first
COPY pom.xml .
COPY services/common-contracts/pom.xml ./services/common-contracts/
COPY services/customer-service/pom.xml ./services/customer-service/

# 2. Download and freeze dependency footprints into a cached layer
RUN mvn dependency:go-offline -B -pl services/customer-service -am

# 3. Pull in the remaining application source files
COPY . .

# 4. Compile and package the customer service executable artifact
RUN mvn -pl services/customer-service -am -DskipTests package

# --- Stage 2: Runtime Phase ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the compiled executable from the build stage container
COPY --from=build /workspace/services/customer-service/target/customer-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]