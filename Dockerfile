# ==========================================
# Etapa 1: Construcción (Build)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiamos los archivos de configuración de Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Descargamos las dependencias (esto ayuda a que los próximos builds sean rápidos)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copiamos el código fuente y compilamos el proyecto
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ==========================================
# Etapa 2: Ejecución (Run)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiamos el .jar generado en la etapa anterior (Etapa 1)
# IMPORTANTE: El nombre del JAR debe coincidir con el que genera tu pom.xml
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto que usa Render
EXPOSE 8080

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]