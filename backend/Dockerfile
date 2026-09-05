# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar todos os arquivos
COPY pom.xml ./
COPY src ./src

# Build da aplicação
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar apenas o JAR
COPY --from=build /app/target/*.jar app.jar

# Expor a porta
EXPOSE 8081

# Comando para rodar
ENTRYPOINT ["java", "-jar", "app.jar"]
