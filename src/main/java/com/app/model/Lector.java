package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "lector")
@DiscriminatorValue("LECTOR")
public class Lector extends Usuario {

    @Column(nullable = false, unique = true)
    private String username;


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    /**
     * Obtiene un resumen formateado del perfil del lector para mostrar en la UI.
     * Útil para mostrar información del usuario de forma legible.
     * 
     * @return String con el resumen del perfil formateado
     */
    public String obtenerResumenPerfil() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("=== Perfil de Lector ===\n");
        resumen.append("ID: ").append(getId()).append("\n");
        resumen.append("Username: ").append(username).append("\n");
        resumen.append("Correo: ").append(getCorreo()).append("\n");
        resumen.append("Tipo de Usuario: Lector");
        return resumen.toString();
    }

    /**
     * Valida que un correo electrónico tenga formato válido.
     * Útil para validaciones en formularios y antes de actualizar.
     * 
     * @param correo Correo electrónico a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public boolean validarFormatoCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        // Validar formato de email (debe contener @ y un dominio)
        return correo.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Valida los datos de perfil sin realizar cambios.
     * Lanza excepciones específicas si los datos son inválidos.
     * Útil para validar antes de actualizar y dar feedback al usuario.
     * 
     * @param username Nombre de usuario a validar
     * @param correo Correo electrónico a validar
     * @param contraseña Contraseña a validar
     * @throws IllegalArgumentException si alguno de los datos es inválido
     */
    public void validarDatosPerfil(String username, String correo, String contraseña) {
        // Validar username no vacío
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username no puede estar vacío");
        }

        // Validar correo con formato válido (validarFormatoCorreo ya valida null y vacío)
        if (!validarFormatoCorreo(correo)) {
            throw new IllegalArgumentException("Correo electrónico inválido");
        }

        // Validar contraseña no vacía
        if (contraseña == null || contraseña.trim().isEmpty()) {
            throw new IllegalArgumentException("Contraseña no puede estar vacía");
        }
    }

    /**
     * Actualiza el perfil del lector con nuevos datos.
     * Valida que los campos no sean nulos/vacíos y que el correo tenga formato válido.
     * 
     * @param nuevoUsername Nuevo nombre de usuario
     * @param nuevoCorreo Nuevo correo electrónico
     * @param nuevaContraseña Nueva contraseña
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarPerfil(String nuevoUsername, String nuevoCorreo, String nuevaContraseña) {
        // Usar el método de validación (captura excepciones para retornar false)
        try {
            validarDatosPerfil(nuevoUsername, nuevoCorreo, nuevaContraseña);
        } catch (IllegalArgumentException e) {
            return false;
        }
        
        // Actualizar campos
        this.username = nuevoUsername;
        this.setCorreo(nuevoCorreo);
        this.setContraseña(nuevaContraseña);
        
        return true;
    }
}

