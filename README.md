# AdminScan - Sistema de Gestión de Scans

## 🚀 Descripción
AdminScan es una aplicación web moderna para la gestión de scans de manga, desarrollada con Java EE, JPA/Hibernate y MySQL.

## ✨ Funcionalidades Principales

### 🔐 Sistema de Autenticación
- **Registro de usuarios**: Crear nueva cuenta AdminScan
- **Login seguro**: Autenticación con username y contraseña
- **Validaciones robustas**: Email único, contraseñas seguras
- **Sesión automática**: Login automático después del registro

### 📊 Gestión de Scans
- **Crear scans**: Con nombre, descripción e imagen
- **Editar scans**: Modificar información y cambiar imagen
- **Eliminar scans**: Borrado seguro con confirmación
- **Subida de imágenes**: Soporte para JPG, PNG, GIF, WEBP

### 🎨 Interfaz Moderna
- **Dashboard responsive**: Se adapta a móviles y tablets
- **Scroll inteligente**: Grid con scroll independiente
- **Tema oscuro**: Diseño moderno AdminScan
- **Animaciones suaves**: Transiciones y efectos visuales

## 🛠️ Tecnologías Utilizadas

- **Backend**: Java 17, Jakarta EE 6.0
- **Persistencia**: JPA/Hibernate 6.2.7, MySQL 8.0
- **Frontend**: JSP, JSTL, JavaScript ES6, CSS3
- **Build**: Maven 3.6+
- **Servidor**: Apache Tomcat 10.1+

## 📋 Prerrequisitos

1. **Java Development Kit (JDK) 17+**
2. **Apache Maven 3.6+**
3. **MySQL 8.0+ / XAMPP**
4. **Apache Tomcat 10.1+**
5. **IDE** (IntelliJ IDEA, Eclipse, VS Code)

## ⚡ Instalación Rápida

### 1. Clonar el Repositorio
```bash
git clone [URL_DEL_REPOSITORIO]
cd GR03_1BT3_622_25B
```

### 2. Configurar Base de Datos
1. **Iniciar XAMPP/MySQL**
2. **Crear base de datos**:
```sql
CREATE DATABASE adminscan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar Conexión
Editar `src/main/resources/META-INF/persistence.xml` si es necesario:
```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/adminscan?useSSL=false"/>
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value=""/>
```

### 4. Compilar y Desplegar
```bash
mvn clean install
# Desplegar el archivo .war generado en Tomcat
```

### 5. Acceder a la Aplicación
1. **Iniciar Tomcat**
2. **Abrir navegador**: `http://localhost:8080/GR03_1BT3_622_25B`
3. **Registrarse**: Crear nueva cuenta o usar cuenta existente

## 👥 Uso de la Aplicación

### Registro de Usuario
1. **Hacer clic en**: "¿No tienes cuenta? Regístrate"
2. **Llenar formulario**:
   - Username: Entre 3-20 caracteres
   - Email: Dirección válida
   - Contraseña: Mínimo 6 caracteres
   - Confirmar contraseña
3. **Crear cuenta**: Te llevará automáticamente al dashboard

### Gestión de Scans
1. **Crear Scan**: Botón "+" en el dashboard
2. **Editar**: Botón "Editar" en cualquier card
3. **Eliminar**: Botón rojo "Eliminar" con confirmación
4. **Subir imágenes**: Opcional en crear/editar

## 🔧 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/app/
│   │   ├── controller/          # Servlets (Login, Registro, Dashboard, etc.)
│   │   ├── dao/                 # Data Access Objects
│   │   ├── model/               # Entidades JPA (Usuario, AdminScan, Scan, Manga)
│   │   └── service/             # Lógica de negocio
│   ├── resources/META-INF/
│   │   └── persistence.xml      # Configuración JPA
│   └── webapp/
│       ├── images/              # Imágenes (default + uploads)
│       ├── *.jsp                # Páginas JSP
│       ├── styles.css           # Estilos CSS
│       └── WEB-INF/web.xml      # Configuración web
└── pom.xml                      # Dependencias Maven
```

## 📊 Base de Datos

### Tablas Principales
- **usuarios**: Tabla padre (herencia)
- **admin_scan**: Administradores del sistema
- **scans**: Scans creados por admins
- **mangas**: Mangas asociados a scans

### Relaciones
- `AdminScan` hereda de `Usuario`
- `Scan` pertenece a `AdminScan` (FK: creado_por_id)
- `Manga` pertenece a `Scan` (FK: scan_id)

## 🎨 Características de Diseño

- **Tema Cyberpunk**: Colores azul neón (#00d4ff) sobre fondo oscuro
- **Responsive**: Mobile-first design
- **Animaciones**: Hover effects, transitions suaves
- **UX Moderna**: Modal dialogs, confirmaciones, feedback visual

## 🔐 Seguridad

- **Validación de sesión**: Verificación en cada request
- **Autorización**: Solo el creador puede editar/eliminar sus scans
- **Sanitización**: Validación de inputs y archivos
- **SQL Injection**: Protección via JPA/JPQL

## 📱 Responsive Design

- **Desktop**: Grid de múltiples columnas
- **Tablet**: Adaptación automática
- **Mobile**: Columna única, menús adaptados

## 🐛 Resolución de Problemas

### Base de Datos
- Verificar que MySQL esté ejecutándose
- Confirmar credenciales en `persistence.xml`
- Revisar que la BD `adminscan` exista

### Compilación
```bash
mvn clean compile
mvn clean install -X  # Modo verbose para debug
```

### Despliegue
- Verificar que Tomcat esté ejecutándose
- Confirmar que el .war se desplegó correctamente
- Revisar logs de Tomcat para errores

## 📈 Próximas Funcionalidades

- [ ] Sistema de roles (Admin/Editor/Viewer)
- [ ] Gestión completa de mangas
- [ ] API REST para integración
- [ ] Sistema de notificaciones
- [ ] Backup automático de imágenes

## 👨‍💻 Desarrollo

### Ejecutar en Modo Desarrollo
```bash
mvn clean compile
# Desplegar en Tomcat via IDE
```

### Base de Datos de Desarrollo
Las tablas se crean automáticamente con `hibernate.hbm2ddl.auto=update`

### Logs
- Backend: Consola del IDE/Tomcat
- Frontend: Developer Tools del navegador
- SQL: `hibernate.show_sql=true`

## 📄 Licencia

Este proyecto es para fines educativos - Universidad/Metodologías Ágiles.

---

**AdminScan v1.0** - Sistema moderno de gestión de scans