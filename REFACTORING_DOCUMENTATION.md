# Documentación de Refactorización - AdminScan

---

## 🔧 Refactorización #1: Extract Method + Extract Superclass

### 📋 **Tipo de Refactorización:** Extract Method + Extract Superclass

### **Clase afectada:** `EditarScanServlet.java`

### **❌ ANTES de refactorizar:**

```java
package com.app.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/editar-scan")
public class EditarScanServlet extends HttpServlet {
    private final ScanService scanService = new ScanService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // CÓDIGO DUPLICADO #1: Validación de sesión (10 líneas)
        HttpSession session = request.getSession();
        AdminScan adminScan = (AdminScan) session.getAttribute("adminScan");

        if (adminScan == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String action = request.getParameter("action");
        String scanIdStr = request.getParameter("scanId");

        // CÓDIGO DUPLICADO #2: Validación de scanId (7 líneas)
        if (scanIdStr == null || scanIdStr.trim().isEmpty()) {
            System.out.println("ERROR: ID de scan no proporcionado");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            int scanId = Integer.parseInt(scanIdStr);

            if ("delete".equals(action)) {
                boolean eliminado = scanService.eliminarScan(scanId);
                // ... lógica
            } else if ("edit".equals(action)) {
                Scan scan = scanService.obtenerScanPorId(scanId);

                // CÓDIGO DUPLICADO #3: Validación de ownership (6 líneas)
                if (scan == null || scan.getCreadoPor().getId() != adminScan.getId()) {
                    System.out.println("ERROR: Scan no encontrado o no autorizado");
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }

                // ... más lógica inline
            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR: ID de scan inválido");
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
```

**Problemas:**
- 10 líneas de validación de sesión duplicadas en 4 servlets
- 7 líneas de validación de scanId duplicadas en 2 servlets
- 6 líneas de validación de ownership duplicadas en 3 servlets
- Método `doPost()` de 120 líneas

---

### **📝 Pasos para refactorizar:**

#### **Paso 1:** Crear clase base `BaseAuthenticatedServlet`
#### **Paso 2:** Cambiar herencia de `HttpServlet` a `BaseAuthenticatedServlet`
#### **Paso 3:** Reemplazar código duplicado con llamadas a métodos heredados
#### **Paso 4:** Extraer lógica de métodos largos en métodos privados

---

### **✅ DESPUÉS de refactorizar:**

```java
@WebServlet("/editar-scan")
public class EditarScanServlet extends BaseAuthenticatedServlet {  // ✅ Cambio
    private final ScanService scanService = new ScanService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Validación heredada (1 línea vs 10)
        AdminScan adminScan = validateSession(request, response);
        if (adminScan == null) return;

        String action = request.getParameter("action");
        String scanIdStr = request.getParameter("scanId");

        // ✅ Validación heredada (1 línea vs 7)
        if (!validateScanId(scanIdStr, request, response)) return;

        try {
            int scanId = Integer.parseInt(scanIdStr);

            // ✅ Métodos extraídos
            if ("delete".equals(action)) {
                handleDeleteAction(scanId);
            } else if ("edit".equals(action)) {
                handleEditAction(request, response, adminScan, scanId);
            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR: ID de scan inválido");
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    private void handleDeleteAction(int scanId) { /* ... */ }
    private void handleEditAction(...) { /* ... */ }
}
```

**Mejoras:**
- ✅ `doPost()` reducido de 120 a 40 líneas
- ✅ Validación de sesión: 10 líneas → 1 línea
- ✅ Validación de scanId: 7 líneas → 1 línea
- ✅ Validación de ownership: 6 líneas → 1 línea

---

### **📊 Clases refactorizadas de la misma manera:**
- `MangaServlet.java`
- `CrearScanServlet.java`
- `CapituloServlet.java`

**Resumen:** 4 servlets refactorizados, eliminando ~72 líneas duplicadas.

---
---

## 🔧 Refactorización #2: Inline Method (Eliminar duplicación)

### 📋 **Tipo de Refactorización:** Inline Method

### **Clase afectada:** `AdminScanDAO.java`

### **❌ ANTES de refactorizar:**

```java
package com.app.dao;

import com.app.model.AdminScan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class AdminScanDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");

    // MÉTODO DUPLICADO #1: guardar()
    public void guardar(AdminScan adminScan) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(adminScan);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // MÉTODO DUPLICADO #2: guardarCompleto() - HACE LO MISMO
    public AdminScan guardarCompleto(AdminScan adminScan) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(adminScan);  // ← MISMA LÓGICA
            em.getTransaction().commit();

            System.out.println("DEBUG: AdminScan guardado - ID: " + adminScan.getId());

            return adminScan;  // ← ÚNICA DIFERENCIA: retorna el objeto
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("ERROR guardando AdminScan: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    public AdminScan buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(AdminScan.class, id);
        } finally {
            em.close();
        }
    }

    // ... otros métodos
}
```

**Problemas:**
- `guardar()` y `guardarCompleto()` hacen lo mismo (persist + commit)
- Única diferencia: uno retorna void, otro retorna AdminScan
- `guardar()` NO se usa en el código (método muerto)

---

### **📝 Pasos para refactorizar:**

#### **Paso 1:** Verificar que `guardar()` no se usa en ningún lugar del código
```bash
# Buscar usos del método guardar()
grep -r "adminScanDAO.guardar(" src/
# Resultado: 0 ocurrencias
```

#### **Paso 2:** Eliminar el método `guardar()` (está muerto)

#### **Paso 3:** Renombrar `guardarCompleto()` → `guardar()` (nombre más simple)

---

### **✅ DESPUÉS de refactorizar:**

```java
package com.app.dao;

import com.app.model.AdminScan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class AdminScanDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");

    // ✅ MÉTODO ÚNICO: guardar() - retorna el objeto guardado
    public AdminScan guardar(AdminScan adminScan) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(adminScan);
            em.getTransaction().commit();

            System.out.println("DEBUG: AdminScan guardado - ID: " + adminScan.getId());

            return adminScan;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("ERROR guardando AdminScan: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    public AdminScan buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(AdminScan.class, id);
        } finally {
            em.close();
        }
    }

    // ... otros métodos
}
```

**Mejoras:**
- ✅ 1 método en lugar de 2 duplicados
- ✅ Código más simple y fácil de mantener
- ✅ Retorna el objeto guardado (más útil)

---

### **📝 Actualizar las llamadas en `LoginService.java`:**

```java
// ANTES
public AdminScan registrarAdminScan(String username, String email, String password) {
    AdminScan nuevoAdmin = new AdminScan();
    nuevoAdmin.setUsername(username);
    nuevoAdmin.setCorreo(email);
    nuevoAdmin.setContraseña(password);

    return adminScanDAO.guardarCompleto(nuevoAdmin);  // ← Método viejo
}

// DESPUÉS
public AdminScan registrarAdminScan(String username, String email, String password) {
    AdminScan nuevoAdmin = new AdminScan();
    nuevoAdmin.setUsername(username);
    nuevoAdmin.setCorreo(email);
    nuevoAdmin.setContraseña(password);

    return adminScanDAO.guardar(nuevoAdmin);  // ✅ Método único
}
```

---

### **📊 Clases refactorizadas de la misma manera:**
- `LectorDAO.java` (eliminar `guardar()` duplicado, dejar solo `guardarCompleto()` renombrado a `guardar()`)

**Resumen:** 2 DAOs refactorizados, eliminando 2 métodos duplicados.

---
---

## 🔧 Refactorización #3: Extract Constant

### 📋 **Tipo de Refactorización:** Extract Constant (Replace Magic Strings)

### **Clases afectadas:** `BaseAuthenticatedServlet.java`, `EditarScanServlet.java`, `MangaServlet.java`

### **❌ ANTES de refactorizar:**

```java
// BaseAuthenticatedServlet.java
protected AdminScan validateSession(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
    HttpSession session = request.getSession(false);

    if (session == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");  // ← String mágico
        return null;
    }

    AdminScan adminScan = (AdminScan) session.getAttribute("adminScan");  // ← String mágico

    if (adminScan == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");  // ← Duplicado
        return null;
    }

    return adminScan;
}

protected boolean validateScanId(String scanIdStr, HttpServletRequest request,
        HttpServletResponse response) throws IOException {
    if (scanIdStr == null || scanIdStr.trim().isEmpty()) {
        System.out.println("ERROR: ID de scan no proporcionado");
        response.sendRedirect(request.getContextPath() + "/dashboard");  // ← String mágico
        return false;
    }
    return true;
}

// EditarScanServlet.java
response.sendRedirect(request.getContextPath() + "/dashboard");  // ← String mágico duplicado

// MangaServlet.java
response.sendRedirect("index.jsp");  // ← String mágico sin contextPath
response.sendRedirect("dashboard");  // ← String mágico sin contextPath
```

**Problemas:**
- Strings hardcodeados repetidos en múltiples lugares
- `/index.jsp` aparece 2 veces en BaseAuthenticatedServlet
- `/dashboard` aparece en 6+ lugares
- `"adminScan"` aparece en 4+ lugares
- Inconsistencia: algunos usan contextPath, otros no

---

### **📝 Pasos para refactorizar:**

#### **Paso 1:** Crear clase `AppConstants.java` con constantes

```java
package com.app.constants;

public final class AppConstants {

    // Prevenir instanciación
    private AppConstants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes");
    }

    // ========== RUTAS ==========
    public static final String ROUTE_INDEX = "/index.jsp";
    public static final String ROUTE_DASHBOARD = "/dashboard";
    public static final String ROUTE_LOGIN = "/login";

    // ========== ATRIBUTOS DE SESIÓN ==========
    public static final String SESSION_ADMIN_SCAN = "adminScan";
    public static final String SESSION_LECTOR = "lector";

    // ========== PARÁMETROS DE REQUEST ==========
    public static final String PARAM_SCAN_ID = "scanId";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_MANGA_ID = "mangaId";
}
```

#### **Paso 2:** Importar constantes en las clases

```java
import static com.app.constants.AppConstants.*;
```

#### **Paso 3:** Reemplazar strings mágicos con constantes

---

### **✅ DESPUÉS de refactorizar:**

```java
// BaseAuthenticatedServlet.java
import static com.app.constants.AppConstants.*;

protected AdminScan validateSession(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
    HttpSession session = request.getSession(false);

    if (session == null) {
        response.sendRedirect(request.getContextPath() + ROUTE_INDEX);  // ✅ Constante
        return null;
    }

    AdminScan adminScan = (AdminScan) session.getAttribute(SESSION_ADMIN_SCAN);  // ✅ Constante

    if (adminScan == null) {
        response.sendRedirect(request.getContextPath() + ROUTE_INDEX);  // ✅ Constante
        return null;
    }

    return adminScan;
}

protected boolean validateScanId(String scanIdStr, HttpServletRequest request,
        HttpServletResponse response) throws IOException {
    if (scanIdStr == null || scanIdStr.trim().isEmpty()) {
        System.out.println("ERROR: ID de scan no proporcionado");
        response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);  // ✅ Constante
        return false;
    }
    return true;
}

// EditarScanServlet.java
import static com.app.constants.AppConstants.*;

response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);  // ✅ Constante

// MangaServlet.java
import static com.app.constants.AppConstants.*;

String action = request.getParameter(PARAM_ACTION);  // ✅ Constante
String scanIdParam = request.getParameter(PARAM_SCAN_ID);  // ✅ Constante
```

**Mejoras:**
- ✅ Strings mágicos → constantes con nombres descriptivos
- ✅ Cambio centralizado: modificar ruta en 1 lugar afecta todo el código
- ✅ Autocompletado del IDE (ctrl+espacio muestra las constantes)
- ✅ Refactoring seguro: renombrar constante actualiza todos los usos
- ✅ Consistencia garantizada: mismo valor en todos lados

---

### **📊 Clases refactorizadas de la misma manera:**

Todas las clases que usan estas rutas/parámetros:
- `BaseAuthenticatedServlet.java`
- `EditarScanServlet.java`
- `MangaServlet.java`
- `CrearScanServlet.java`
- `CapituloServlet.java`
- `LoginServlet.java`
- `DashboardServlet.java`

**Resumen:** 7+ clases refactorizadas, ~30 strings mágicos → constantes centralizadas.

---
---

## 🔧 Refactorización #4: Inline Temp

### 📋 **Tipo de Refactorización:** Inline Temp (Inline Variable)

### **Clase afectada:** `MangaDAO.java`

### **❌ ANTES de refactorizar:**

```java
package com.app.dao;

import com.app.model.Manga;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class MangaDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");

    // VARIABLE TEMPORAL #1: query usada solo una vez
    public List<Manga> obtenerTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Manga> query = em.createQuery("SELECT m FROM Manga m ORDER BY m.titulo", Manga.class);  // ← Temp innecesario
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // VARIABLE TEMPORAL #2: query usada solo para null check
    public boolean existeTituloEnScan(String titulo, int scanId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(  // ← Temp innecesario
                "SELECT COUNT(m) FROM Manga m WHERE m.titulo = :titulo AND m.scan.id = :scanId",
                Long.class
            );
            query.setParameter("titulo", titulo);
            query.setParameter("scanId", scanId);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}
```

**Problemas:**
- Variable `query` usada solo una vez sin agregar claridad
- Líneas extra que no aportan valor semántico
- Patrón repetido en múltiples métodos de DAOs

---

### **📝 Pasos para refactorizar:**

#### **Paso 1:** Identificar variables temporales usadas solo una vez
#### **Paso 2:** Reemplazar la variable con su expresión directamente
#### **Paso 3:** Usar method chaining para mantener legibilidad
#### **Paso 4:** Aplicar en todos los métodos con el mismo patrón

---

### **✅ DESPUÉS de refactorizar:**

```java
package com.app.dao;

import com.app.model.Manga;
import jakarta.persistence.EntityManager;

public class MangaDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");

    // ✅ Sin variable temporal innecesaria
    public List<Manga> obtenerTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT m FROM Manga m ORDER BY m.titulo", Manga.class)
                     .getResultList();  // ✅ Method chaining directo
        } finally {
            em.close();
        }
    }

    // ✅ Sin variable temporal innecesaria
    public boolean existeTituloEnScan(String titulo, int scanId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(m) FROM Manga m WHERE m.titulo = :titulo AND m.scan.id = :scanId",
                Long.class)
                .setParameter("titulo", titulo)
                .setParameter("scanId", scanId)
                .getSingleResult() > 0;  // ✅ Expresión inline
        } finally {
            em.close();
        }
    }
}
```

**Mejoras:**
- ✅ Código más conciso y directo
- ✅ Menos líneas sin perder legibilidad
- ✅ Method chaining hace obvio el flujo
- ✅ Elimina variables que no agregan valor semántico

---

### **📊 Clases y métodos refactorizados de la misma manera:**

**MangaDAO.java:**
- `obtenerTodos()` (línea 73)
- `existeTituloEnScan()` (línea 105)
- `existeTituloEnScanExceptoId()` (línea 119)

**CapituloDAO.java:**
- `listarTodos()` (línea 81)
- `listarPorManga()` (línea 91)

**ScanDAO.java:**
- `buscarPorAdminScan()` (línea 40)

**Resumen:** 3 DAOs refactorizados, 6 métodos simplificados, ~12 líneas eliminadas.

---
---

## 📊 **Resumen General de Refactorizaciones**

| # | Tipo | Clases afectadas | Líneas eliminadas | Beneficio |
|---|------|-----------------|-------------------|-----------|
| 1 | Extract Method + Superclass | 4 servlets | ~72 líneas | Reutilización de validaciones |
| 2 | Inline Method | 2 DAOs | 2 métodos duplicados | Simplificación |
| 3 | Extract Constant | 7+ clases | ~30 strings mágicos | Mantenibilidad |
| 4 | Inline Temp | 3 DAOs | ~12 líneas | Código más conciso |

**Total:** ~115 líneas de código eliminadas, mejor legibilidad y mantenibilidad.