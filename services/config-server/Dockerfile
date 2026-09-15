# --- Stage 1: Build Phase ---
FROM maven:3.9.11-eclipse-temurin-17-jammy AS build
WORKDIR /workspace

# 1. Copy parent and config-server configurations first
COPY pom.xml .
COPY services/config-server/pom.xml ./services/config-server/

# 2. Freeze and cache Maven dependency footprints 
RUN mvn dependency:go-offline -B -pl services/config-server -am

# 3. Pull down the remaining module codebase source strings
COPY . .

# 4. Compile and package the isolated configuration server execution jar
RUN mvn -pl services/config-server -am -DskipTests package

# --- Stage 2: Runtime Isolation ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy target executable artifact from build pipeline container
COPY --from=build /workspace/services/config-server/target/config-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]