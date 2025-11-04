# Etapa 1: Compilar con Maven
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY src ./src

# Compilar el proyecto (esto genera el WAR)
RUN mvn clean package -DskipTests

# Etapa 2: Desplegar en Tomcat
FROM tomcat:10.1-jdk17
WORKDIR /usr/local/tomcat

# Limpiar aplicaciones por defecto
RUN rm -rf webapps/*

# Copiar el WAR generado en la etapa anterior
COPY --from=builder /app/target/*.war webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]