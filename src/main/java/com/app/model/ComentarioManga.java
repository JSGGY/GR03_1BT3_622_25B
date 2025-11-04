package com.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comentarios_manga")
public class ComentarioManga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manga_id", nullable = false)
    private Manga manga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lector_username", referencedColumnName = "username", nullable = false)
    private Lector lector;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_comentario")
    private LocalDateTime fechaComentario;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    // ========================
    // Constructores
    // ========================
    public ComentarioManga() {
        this.fechaComentario = LocalDateTime.now();
    }

    public ComentarioManga(Manga manga, Lector lector, String comentario) {
        this.manga = manga;
        this.lector = lector;
        this.comentario = comentario;
        this.fechaComentario = LocalDateTime.now();
    }

    // ========================
    // Getters y Setters
    // ========================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Manga getManga() {
        return manga;
    }

    public void setManga(Manga manga) {
        this.manga = manga;
    }

    public Lector getLector() {
        return lector;
    }

    public void setLector(Lector lector) {
        this.lector = lector;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
        this.fechaModificacion = LocalDateTime.now();
    }

    public LocalDateTime getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(LocalDateTime fechaComentario) {
        this.fechaComentario = fechaComentario;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    // ========================
    // Métodos de validación
    // ========================
    public boolean esComentarioValido() {
        return comentario != null && !comentario.trim().isEmpty();
    }

    public String obtenerNombreLector() {
        return lector != null ? lector.getUsername() : "Usuario desconocido";
    }
}

