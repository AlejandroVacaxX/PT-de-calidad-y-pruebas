# 1. Usar una imagen oficial de Java
FROM eclipse-temurin:21-jdk-alpine

# 2. Copiar CUALQUIER archivo .jar que esté en target y renombrarlo a app.jar
COPY target/*.jar app.jar

# 3. Exponer el puerto
EXPOSE 8080

# 4. Comando para ejecutar tu app
ENTRYPOINT ["java","-jar","/app.jar"]