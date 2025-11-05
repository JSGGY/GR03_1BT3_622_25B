# Solución al Problema de Conexión de Base de Datos en Docker

## Problema Original
La aplicación no podía conectarse a la base de datos cuando se ejecutaba en Docker porque `persistence.xml` tenía hardcodeado `localhost:3306`, pero en Docker necesita conectarse a `db:3306`.

## Solución Implementada

### 1. **Modificación de `EntityManagerFactoryProvider.java`**
Se modificó para que lea las variables de entorno de Docker y sobrescriba dinámicamente la configuración de la base de datos:

```java
// Lee variables de entorno
String dbHost = System.getenv("DB_HOST");    // "db" en Docker
String dbPort = System.getenv("DB_PORT");    // "3306"
String dbName = System.getenv("DB_NAME");    // "adminscan"
String dbUser = System.getenv("DB_USER");    // "root"
String dbPassword = System.getenv("DB_PASSWORD");  // "root"

// Si existen, sobrescribe la configuración del persistence.xml
if (dbHost != null && dbPort != null && dbName != null) {
    String jdbcUrl = String.format(
        "jdbc:mysql://%s:%s/%s?useSSL=false&maxAllowedPacket=67108864",
        dbHost, dbPort, dbName
    );
    properties.put("jakarta.persistence.jdbc.url", jdbcUrl);
    properties.put("jakarta.persistence.jdbc.user", dbUser);
    properties.put("jakarta.persistence.jdbc.password", dbPassword);
}
```

### 2. **Variables de Entorno en `docker-compose.yaml`**
Ya estaban configuradas correctamente en tu archivo:

```yaml
app:
  environment:
    DB_HOST: db          # Nombre del servicio de base de datos
    DB_PORT: 3306
    DB_NAME: adminscan
    DB_USER: root
    DB_PASSWORD: root
```

### 3. **persistence.xml**
Se mantiene con valores por defecto para desarrollo local (sin Docker):
- `localhost:3306` para desarrollo local
- Las propiedades son sobrescritas automáticamente en Docker por `EntityManagerFactoryProvider`

## Cómo Funciona

### En Desarrollo Local (sin Docker):
1. No hay variables de entorno `DB_HOST`, `DB_PORT`, etc.
2. `EntityManagerFactoryProvider` usa la configuración por defecto de `persistence.xml`
3. Se conecta a `localhost:3306`

### En Docker:
1. Docker Compose inyecta las variables de entorno definidas en `docker-compose.yaml`
2. `EntityManagerFactoryProvider` detecta estas variables
3. Sobrescribe la URL de conexión para usar `db:3306` (el nombre del servicio)
4. Se conecta correctamente a la base de datos en el contenedor `db`

## Pasos para Probar

1. **Reconstruir la imagen Docker:**
   ```bash
   docker-compose down
   docker-compose build --no-cache
   ```

2. **Iniciar los contenedores:**
   ```bash
   docker-compose up -d
   ```

3. **Ver los logs de la aplicación:**
   ```bash
   docker logs adminscan_web
   ```
   
   Deberías ver:
   ```
   Using Docker environment variables for database connection
   Connecting to: jdbc:mysql://db:3306/adminscan?useSSL=false&maxAllowedPacket=67108864
   ```

4. **Verificar que la app funciona:**
   - Abre http://localhost:8080
   - La aplicación debería conectarse correctamente a la base de datos

## Verificación de Errores

Si aún hay problemas, verifica:

1. **¿La base de datos está lista?**
   ```bash
   docker logs mysql_adminscan
   ```
   Busca: `ready for connections`

2. **¿Las variables de entorno están configuradas?**
   ```bash
   docker exec adminscan_web env | grep DB_
   ```
   Deberías ver:
   ```
   DB_HOST=db
   DB_PORT=3306
   DB_NAME=adminscan
   DB_USER=root
   DB_PASSWORD=root
   ```

3. **¿Hay conectividad entre contenedores?**
   ```bash
   docker exec adminscan_web ping -c 3 db
   ```

## Ventajas de esta Solución

✅ **No rompe el desarrollo local**: Sigue funcionando con `localhost:3306`
✅ **Flexible**: Fácil cambiar credenciales modificando `docker-compose.yaml`
✅ **Estándar**: Usa variables de entorno, una práctica común en Docker
✅ **Sin duplicación**: Un solo `persistence.xml` para todos los ambientes
✅ **Seguro**: Las credenciales no están hardcodeadas en el código

## Archivos Modificados

1. ✅ `src/main/java/com/app/dao/EntityManagerFactoryProvider.java`
2. ✅ `src/main/resources/META-INF/persistence.xml` (comentarios agregados)
