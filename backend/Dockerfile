FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY src ./src

# Build da aplicação
RUN ./mvnw package -DskipTests || mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar o JAR do target
COPY --from=build /app/target/*.jar app.jar

# Expor a porta
EXPOSE 8081

# Comando para rodar
ENTRYPOINT ["java", "-jar", "app.jar"]
