# 🎓 AdminScan - Instrucciones para el Profesor

## 📦 Imagen en Docker Hub

**Imagen**: `jsgg/adminscan:latest`  
**URL**: https://hub.docker.com/r/jsgg/adminscan

---

## 🚀 Método 1: Docker Compose (RECOMENDADO - 1 comando)

### Archivos necesarios:

- `docker-compose.hub.yaml`
- Carpeta `db-init/` con `init.sql`

### Ejecutar:

```bash
# 1. Renombrar el archivo (solo la primera vez)
mv docker-compose.hub.yaml docker-compose.yaml

# 2. Ejecutar
docker-compose up -d

# 3. Esperar 30-60 segundos

# 4. Acceder a: http://localhost:8080
```

### Detener:

```bash
docker-compose down
```

### Eliminar todo (incluyendo datos):

```bash
docker-compose down -v
```

---

## 🔧 Método 2: Docker Run (Comandos individuales)

### Linux/Mac:

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

# 4. Esperar a que MySQL inicie
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

### Windows PowerShell:

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

# 4. Esperar
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

---

## 📊 Información del Sistema

| Componente     | Puerto | Acceso                                  |
| -------------- | ------ | --------------------------------------- |
| Aplicación Web | 8080   | http://localhost:8080                   |
| MySQL          | 3307   | localhost:3307 (user: root, pass: root) |

---

## 🔍 Comandos Útiles

### Ver contenedores corriendo

```bash
docker ps
```

### Ver logs

```bash
# Aplicación
docker logs adminscan_web -f

# Base de datos
docker logs mysql_adminscan -f
```

### Reiniciar

```bash
docker restart adminscan_web
docker restart mysql_adminscan
```

### Detener

```bash
docker stop adminscan_web mysql_adminscan
```

### Eliminar todo

```bash
# Con docker-compose
docker-compose down -v

# Con docker run
docker stop adminscan_web mysql_adminscan
docker rm adminscan_web mysql_adminscan
docker network rm adminscan_net
```

---

## 🐛 Solución de Problemas

### La aplicación no conecta a la BD

```bash
# Esperar más tiempo (MySQL tarda en iniciar)
sleep 30

# Reiniciar la app
docker restart adminscan_web
```

### Puerto ocupado

Cambiar el puerto en el comando:

```bash
# En lugar de -p 8080:8080 usar:
-p 9090:8080
```

### BD vacía (sin datos)

```bash
# Cargar el script manualmente
docker exec -i mysql_adminscan mysql -uroot -proot adminscan < db-init/init.sql
```

### Empezar desde cero

```bash
docker-compose down -v  # Elimina todo
docker-compose up -d    # Vuelve a crear
```

---

## ✅ Verificación

Para verificar que todo funciona:

```bash
# Ver contenedores
docker ps

# Probar la app
curl http://localhost:8080

# O abrir en navegador:
# http://localhost:8080
```

---

## 🎯 Arquitectura

```
Docker Hub
    │
    │ docker pull jsgg/adminscan:latest
    │
    ▼
┌─────────────────────────────────┐
│      Máquina Local              │
│  ┌──────────┐    ┌───────────┐ │
│  │ adminscan│───▶│  mysql    │ │
│  │   :8080  │    │  :3307    │ │
│  └──────────┘    └───────────┘ │
│       adminscan_net (red)       │
└─────────────────────────────────┘
         │                │
    localhost:8080   localhost:3307
```

---

## 📚 Información del Proyecto

- **Nombre**: AdminScan
- **Equipo**: GR03_1BT3_622_25B
- **Tecnologías**: Java 17, Jakarta EE 10, MySQL 8, Tomcat 10
- **Imagen Docker**: jsgg/adminscan:latest
- **Repositorio**: https://hub.docker.com/r/jsgg/adminscan

---

## 🎉 ¡Listo para Usar!

**Método más simple:**

```bash
docker-compose up -d
```

**Acceder:** http://localhost:8080
