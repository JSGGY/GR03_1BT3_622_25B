package com.app.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lector")
@DiscriminatorValue("LECTOR")
public class Lector extends Usuario {

    @Column(nullable = false, unique = true)
    private String username;

    // ========================
    // Favoritos (lista de mangas)
    // ========================
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "mangas_favoritos",
            joinColumns = @JoinColumn(name = "id_lector"),
            inverseJoinColumns = @JoinColumn(name = "id_manga")
    )
    private List<Manga> favoritos = new ArrayList<>();

    // ========================
    // Listas de mangas
    // ========================
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_lector") // Relación unidireccional
    private List<Lista> listas = new ArrayList<>();

    // ========================
    // Constructores
    // ========================
    public Lector() { }

    public Lector(String username, String correo, String contraseña) {
        this.username = username;
        this.setCorreo(correo);
        this.setContraseña(contraseña);
    }

    // ========================
    // Getters y Setters
    // ========================
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<Lista> getListas() { return listas; }
    public void setListas(List<Lista> listas) { this.listas = listas; }

    public List<Manga> getFavoritos() { return favoritos; }
    public void setFavoritos(List<Manga> favoritos) { this.favoritos = favoritos; }

    // ========================
    // Métodos auxiliares Favoritos
    // ========================
    public void agregarFavorito(Manga manga) {
        if (!favoritos.contains(manga)) {
            favoritos.add(manga);
        }
    }

    public void quitarFavorito(Manga manga) {
        favoritos.remove(manga);
    }

    // ========================
    // Perfil Methods
    // ========================
    public String obtenerResumenPerfil() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("=== Perfil de Lector ===\n");
        resumen.append("ID: ").append(getId()).append("\n");
        resumen.append("Username: ").append(username).append("\n");
        resumen.append("Correo: ").append(getCorreo()).append("\n");
        resumen.append("Tipo de Usuario: Lector");
        return resumen.toString();
    }

    public boolean validarFormatoCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) return false;
        return correo.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public void validarDatosPerfil(String username, String correo, String contraseña) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username no puede estar vacío");

        if (!validarFormatoCorreo(correo))
            throw new IllegalArgumentException("Correo electrónico inválido");

        if (contraseña == null || contraseña.trim().isEmpty())
            throw new IllegalArgumentException("Contraseña no puede estar vacía");
    }

    public boolean actualizarPerfil(String nuevoUsername, String nuevoCorreo, String nuevaContraseña) {
        try {
            validarDatosPerfil(nuevoUsername, nuevoCorreo, nuevaContraseña);
        } catch (IllegalArgumentException e) {
            return false;
        }
        this.username = nuevoUsername;
        this.setCorreo(nuevoCorreo);
        this.setContraseña(nuevaContraseña);
        return true;
    }
}
