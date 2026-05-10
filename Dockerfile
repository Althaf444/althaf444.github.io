# Build stage — mint-app + budget-domain (other budget modules omitted from image context if unused)
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY budget-domain budget-domain
COPY budget-create-app budget-create-app
COPY budget-edit-app budget-edit-app
COPY budget-status-app budget-status-app
COPY budget-delete-app budget-delete-app
COPY mint-app mint-app

RUN mvn package -pl mint-app -am -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/mint-app/target/mint-app-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
