package com.app.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "listas")
public class Lista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lector_id", nullable = false)
    private Lector lector;

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("fechaAgregado DESC")
    private List<ListaManga> listaMangas = new ArrayList<>();

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // =======================
    // Getters y Setters
    // =======================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Lector getLector() {
        return lector;
    }

    public void setLector(Lector lector) {
        this.lector = lector;
    }

    public List<ListaManga> getListaMangas() {
        return listaMangas;
    }

    public void setListaMangas(List<ListaManga> listaMangas) {
        this.listaMangas = listaMangas;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Obtiene la cantidad de mangas en la lista
     */
    public int getTotalMangas() {
        return listaMangas != null ? listaMangas.size() : 0;
    }

    /**
     * Verifica si un manga está en la lista
     */
    public boolean contieneManga(int mangaId) {
        if (listaMangas == null) {
            return false;
        }
        return listaMangas.stream()
                .anyMatch(lm -> lm.getManga() != null && lm.getManga().getId() == mangaId);
    }
}

