package com.app.constants;

/**
 * Clase que centraliza todas las constantes de la aplicación.
 *
 * Responsabilidad: Almacenar valores constantes usados en múltiples lugares.
 * Tipo de refactorización: Extract Constant (Replace Magic Strings)
 *
 * Beneficios:
 * - Cambios centralizados: modificar una ruta en un solo lugar
 * - Refactoring seguro: renombrar constante actualiza todos los usos
 * - Autocompletado del IDE
 * - Previene errores de tipeo
 */
public final class AppConstants {

    // Prevenir instanciación
    private AppConstants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes");
    }

    // ========== RUTAS ==========
    public static final String ROUTE_INDEX = "/index.jsp";
    public static final String ROUTE_DASHBOARD = "/dashboard";
    public static final String ROUTE_LOGIN = "/login";
    public static final String ROUTE_MANGA = "/manga";
    public static final String ROUTE_CAPITULO = "/capitulo";

    // ========== ATRIBUTOS DE SESIÓN ==========
    public static final String SESSION_ADMIN_SCAN = "adminScan";
    public static final String SESSION_LECTOR = "lector";

    // ========== PARÁMETROS DE REQUEST ==========
    public static final String PARAM_SCAN_ID = "scanId";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_MANGA_ID = "mangaId";
    public static final String PARAM_CAPITULO_ID = "capituloId";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_EMAIL = "email";

    // ========== ACCIONES ==========
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_VIEW = "view";
}