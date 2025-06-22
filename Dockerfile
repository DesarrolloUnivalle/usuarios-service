# Imagen base con soporte para Java 21
FROM eclipse-temurin:21-jdk-alpine

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el jar generado desde el directorio target al contenedor
ARG JAR_FILE=target/usuarios-service-1.0.0.jar
COPY ${JAR_FILE} app.jar

# Expone el puerto en el que tu servicio escucha (ajusta si es distinto)
EXPOSE 8080

# Comando que se ejecutará al iniciar el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]