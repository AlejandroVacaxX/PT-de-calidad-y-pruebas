# ETAPA 1: Compilación
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiamos archivos de configuración de Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Descargamos dependencias 
RUN ./mvnw dependency:go-offline

# Copiamos el código fuente y compilamos
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ETAPA 2: Ejecución
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiamos el JAR generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]