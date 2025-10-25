package com.app.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "capitulos")
public class Capitulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private int numero;

    @Column(length = 1000)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manga_id", nullable = false)
    private Manga manga;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "capitulo_imagenes", joinColumns = @JoinColumn(name = "capitulo_id"))
    @Column(name = "imagen_url")
    @OrderColumn(name = "orden")
    private List<String> imagenesUrls = new ArrayList<>();

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Manga getManga() { return manga; }
    public void setManga(Manga manga) { this.manga = manga; }

    public List<String> getImagenesUrls() { return imagenesUrls; }
    public void setImagenesUrls(List<String> imagenesUrls) { this.imagenesUrls = imagenesUrls; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }


    public void agregarImagen(String imagenUrl) {
        if (imagenUrl != null && !imagenUrl.trim().isEmpty()) {
            this.imagenesUrls.add(imagenUrl);
        }
    }

    public void eliminarImagen(String imagenUrl) {
        this.imagenesUrls.remove(imagenUrl);
    }

    public int getTotalPaginas() {
        return imagenesUrls != null ? imagenesUrls.size() : 0;
    }

    @Override
    public String toString() {
        return "Capitulo{id=" + id + ", titulo='" + titulo + "', numero=" + numero + ", totalPaginas=" + getTotalPaginas() + "}";
    }

    public boolean actualizarDatos(String titulo, String descripcion) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return false;
        }
        this.titulo = titulo;
        this.descripcion = descripcion;
        return true;
    }


    public boolean cambiarNumeroCapitulo(int nuevoNumero) {
        if (nuevoNumero <= 0) {
            return false;  // Número debe ser positivo
        }
        this.numero = nuevoNumero;
        return true;
    }
}