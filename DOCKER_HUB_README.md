# AdminScan - Sistema de Gestión de Mangas

Sistema web completo para gestión de scans de manga con usuarios, favoritos, listas y comentarios.

## 🚀 Inicio Rápido

### Windows (PowerShell)

```powershell
# 1. Descargar la imagen
docker pull jsgg/adminscan:latest

# 2. Crear red
docker network create adminscan_net

# 3. MySQL
docker run -d `
  --name mysql_adminscan `
  --network adminscan_net `
  -e MYSQL_ROOT_PASSWORD=root `
  -e MYSQL_DATABASE=adminscan `
  -p 3307:3306 `
  mysql:8.0

# 4. Esperar 60 segundos
Start-Sleep -Seconds 60

# 5. Aplicación
docker run -d `
  --name adminscan_web `
  --network adminscan_net `
  -p 8080:8080 `
  -e DB_HOST=mysql_adminscan `
  -e DB_PORT=3306 `
  -e DB_NAME=adminscan `
  -e DB_USER=root `
  -e DB_PASSWORD=root `
  jsgg/adminscan:latest
```

### Linux/Mac (Bash)

```bash
# 1. Descargar la imagen
docker pull jsgg/adminscan:latest

# 2. Crear red
docker network create adminscan_net

# 3. MySQL
docker run -d \
  --name mysql_adminscan \
  --network adminscan_net \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=adminscan \
  -p 3307:3306 \
  mysql:8.0

# 4. Esperar 60 segundos
sleep 60

# 5. Aplicación
docker run -d \
  --name adminscan_web \
  --network adminscan_net \
  -p 8080:8080 \
  -e DB_HOST=mysql_adminscan \
  -e DB_PORT=3306 \
  -e DB_NAME=adminscan \
  -e DB_USER=root \
  -e DB_PASSWORD=root \
  jsgg/adminscan:latest
```

**Acceder a:** http://localhost:8080

## 📦 Con Docker Compose

Crea un archivo `docker-compose.yml`:

```yaml
services:
  db:
    image: mysql:8.0
    container_name: mysql_adminscan
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: adminscan
    ports:
      - "3307:3306"
    volumes:
      - db_data:/var/lib/mysql
    networks:
      - adminscan_net
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-uroot", "-proot"]
      timeout: 20s
      retries: 10

  app:
    image: jsgg/adminscan:latest
    container_name: adminscan_web
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "8080:8080"
    environment:
      DB_HOST: db
      DB_PORT: 3306
      DB_NAME: adminscan
      DB_USER: root
      DB_PASSWORD: root
    networks:
      - adminscan_net

volumes:
  db_data:

networks:
  adminscan_net:
```

Ejecutar:

```bash
docker-compose up -d
```

## 🔧 Variables de Entorno

| Variable      | Descripción                | Default     |
| ------------- | -------------------------- | ----------- |
| `DB_HOST`     | Host de MySQL              | `db`        |
| `DB_PORT`     | Puerto de MySQL            | `3306`      |
| `DB_NAME`     | Nombre de la base de datos | `adminscan` |
| `DB_USER`     | Usuario de MySQL           | `root`      |
| `DB_PASSWORD` | Contraseña de MySQL        | `root`      |

## 📋 Comandos Útiles

```bash
# Ver logs
docker logs -f adminscan_web

# Reiniciar
docker restart adminscan_web

# Detener
docker stop adminscan_web mysql_adminscan

# Eliminar
docker rm -f adminscan_web mysql_adminscan
docker network rm adminscan_net
```

## 🛠️ Tecnologías

- Java 17 + Jakarta EE
- Tomcat 10.1
- MySQL 8.0
- Hibernate/JPA
- JSP + JSTL

## 👥 Autor

**Grupo:** GR03_1BT3_622_25B

## 📄 Documentación Completa

Ver [DOCKER_HUB_INSTRUCTIONS.md](https://github.com/tuusuario/turrepo/blob/main/DOCKER_HUB_INSTRUCTIONS.md) para más detalles.
