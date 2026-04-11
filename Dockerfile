# ==========================================
# Etapa 1: Construcción (Build)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiamos todo el código fuente de GitHub al contenedor
COPY . .

# Le damos permisos al wrapper de Maven y compilamos el proyecto
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ==========================================
# Etapa 2: Ejecución (Run)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiamos SOLO el .jar generado en la Etapa 1
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Arrancamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]