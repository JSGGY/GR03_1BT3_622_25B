# 🐳 AdminScan - Instrucciones de Docker Hub

## 📦 Imagen Docker disponible en Docker Hub

**Imagen:** `jsgg/adminscan:latest`

---

## 🚀 Instalación y Uso Rápido

### Opción 1: Con Docker Run (Sin Docker Compose)

#### 🪟 Para Windows (PowerShell):

```powershell
# 1. Descargar la imagen
docker pull jsgg/adminscan:latest

# 2. Crear red
docker network create adminscan_net

# 3. MySQL con datos iniciales
docker run -d `
  --name mysql_adminscan `
  --network adminscan_net `
  -e MYSQL_ROOT_PASSWORD=root `
  -e MYSQL_DATABASE=adminscan `
  -v "${PWD}/db-init/init.sql:/docker-entrypoint-initdb.d/init.sql" `
  -p 3307:3306 `
  mysql:8.0

# 4. Esperar a que MySQL esté listo (60 segundos)
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

# 6. Acceder a: http://localhost:8080
```

#### 🐧 Para Linux/Mac (Bash):

```bash
# 1. Descargar la imagen
docker pull jsgg/adminscan:latest

# 2. Crear red
docker network create adminscan_net

# 3. MySQL con datos iniciales
docker run -d \
  --name mysql_adminscan \
  --network adminscan_net \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=adminscan \
  -v "$(pwd)/db-init/init.sql:/docker-entrypoint-initdb.d/init.sql" \
  -p 3307:3306 \
  mysql:8.0

# 4. Esperar a que MySQL esté listo (60 segundos)
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

# 6. Acceder a: http://localhost:8080
```

**Nota:** Asegúrate de tener la carpeta `db-init/` con el archivo `init.sql` antes de ejecutar los comandos.

---

### Opción 2: Con Docker Compose (Recomendado)

### Requisitos Previos

- Docker instalado
- Docker Compose instalado
- Puerto 8080 disponible (aplicación)
- Puerto 3307 disponible (MySQL)

### Paso 1: Descargar el archivo de configuración

Descarga el archivo `docker-compose.hub.yaml` o crea un archivo con este contenido:

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
      - ./db-init/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - adminsan_net
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
      - adminsan_net

volumes:
  db_data:

networks:
  adminsan_net:
```

### Paso 2: Crear la carpeta con el script de inicialización de base de datos

```bash
mkdir -p db-init
```

Coloca el archivo `init.sql` dentro de la carpeta `db-init/` (este archivo debe contener la estructura de la base de datos).

### Paso 3: Iniciar la aplicación

```bash
docker-compose -f docker-compose.hub.yaml up -d
```

### Paso 4: Acceder a la aplicación

Abre tu navegador y ve a:

**http://localhost:8080**

---

## 📋 Comandos Útiles

### Para Docker Run (Opción 1)

#### Ver logs de la aplicación

```bash
docker logs -f adminscan_web
```

#### Ver logs de la base de datos

```bash
docker logs -f mysql_adminscan
```

#### Detener la aplicación

```bash
docker stop adminscan_web mysql_adminscan
```

#### Detener y eliminar contenedores

```bash
docker rm -f adminscan_web mysql_adminscan
docker network rm adminscan_net
```

#### Reiniciar la aplicación

```bash
docker restart adminscan_web
```

#### Ver contenedores en ejecución

```bash
docker ps
```

---

### Para Docker Compose (Opción 2)

#### Ver logs de la aplicación

```bash
docker-compose -f docker-compose.hub.yaml logs -f app
```

#### Ver logs de la base de datos

```bash
docker-compose -f docker-compose.hub.yaml logs -f db
```

#### Detener la aplicación

```bash
docker-compose -f docker-compose.hub.yaml down
```

#### Detener y eliminar volúmenes (borra la base de datos)

```bash
docker-compose -f docker-compose.hub.yaml down -v
```

#### Reiniciar la aplicación

```bash
docker-compose -f docker-compose.hub.yaml restart app
```

#### Ver contenedores en ejecución

```bash
docker ps
```

---

## 🔧 Configuración Avanzada

### Variables de Entorno

La aplicación soporta las siguientes variables de entorno:

| Variable      | Descripción                | Valor por defecto |
| ------------- | -------------------------- | ----------------- |
| `DB_HOST`     | Host de MySQL              | `db`              |
| `DB_PORT`     | Puerto de MySQL            | `3306`            |
| `DB_NAME`     | Nombre de la base de datos | `adminscan`       |
| `DB_USER`     | Usuario de MySQL           | `root`            |
| `DB_PASSWORD` | Contraseña de MySQL        | `root`            |

### Cambiar puerto de la aplicación

Para usar un puerto diferente al 8080, modifica la línea en el `docker-compose.hub.yaml`:

```yaml
ports:
  - "TU_PUERTO:8080" # Ejemplo: "9090:8080"
```

---

## 🛠️ Solución de Problemas

### La aplicación no inicia

1. Verifica que los puertos 8080 y 3307 estén disponibles:

```bash
netstat -ano | findstr :8080
netstat -ano | findstr :3307
```

2. Revisa los logs:

```bash
docker-compose -f docker-compose.hub.yaml logs
```

### Error de conexión a la base de datos

Asegúrate de que el contenedor de MySQL esté saludable antes de iniciar la aplicación:

```bash
docker ps
```

Deberías ver `(healthy)` en el contenedor `mysql_adminscan`.

### Reiniciar desde cero

```bash
docker-compose -f docker-compose.hub.yaml down -v
docker-compose -f docker-compose.hub.yaml up -d
```

---

## 📦 Estructura del Proyecto

```
.
├── docker-compose.hub.yaml    # Archivo de configuración Docker Compose
└── db-init/
    └── init.sql              # Script de inicialización de base de datos
```

---

## 👥 Créditos

**Desarrollado por:** GR03_1BT3_622_25B

**Imagen Docker Hub:** https://hub.docker.com/r/jsgg/adminscan

---

## 📝 Notas

- La primera vez que ejecutes la aplicación, Docker descargará las imágenes necesarias (puede tardar unos minutos).
- El archivo `init.sql` solo se ejecuta la primera vez que se crea la base de datos.
- Si modificas `init.sql`, necesitas eliminar el volumen: `docker-compose -f docker-compose.hub.yaml down -v`

---

## 🚀 Actualizar a la última versión

### Con Docker Run:

```bash
# 1. Descargar la nueva versión
docker pull jsgg/adminscan:latest

# 2. Detener y eliminar el contenedor viejo
docker rm -f adminscan_web

# 3. Crear nuevo contenedor con la imagen actualizada
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

### Con Docker Compose:

```bash
docker pull jsgg/adminscan:latest
docker-compose -f docker-compose.hub.yaml down
docker-compose -f docker-compose.hub.yaml up -d
```
