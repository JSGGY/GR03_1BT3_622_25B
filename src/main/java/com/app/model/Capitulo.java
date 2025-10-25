package com.app.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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

    @OneToMany(mappedBy = "capitulo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orden ASC")
    private List<CapituloImagen> imagenes = new ArrayList<>();

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

    public List<CapituloImagen> getImagenes() { return imagenes; }
    public void setImagenes(List<CapituloImagen> imagenes) { this.imagenes = imagenes; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    /**
     * Agrega una imagen al capítulo en el orden correcto
     */
    public void agregarImagen(CapituloImagen imagen) {
        if (imagen != null) {
            imagen.setCapitulo(this);
            imagen.setOrden(this.imagenes.size());
            this.imagenes.add(imagen);
        }
    }

    /**
     * Elimina una imagen del capítulo
     */
    public void eliminarImagen(CapituloImagen imagen) {
        this.imagenes.remove(imagen);
        // Reordenar las imágenes restantes
        for (int i = 0; i < imagenes.size(); i++) {
            imagenes.get(i).setOrden(i);
        }
    }

    /**
     * Retorna el total de páginas (imágenes) del capítulo
     */
    public int getTotalPaginas() {
        return imagenes != null ? imagenes.size() : 0;
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