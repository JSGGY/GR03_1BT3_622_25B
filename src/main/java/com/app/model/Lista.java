package com.app.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listas")
public class Lista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "lista_mangas",
            joinColumns = @JoinColumn(name = "lista_id"),
            inverseJoinColumns = @JoinColumn(name = "manga_id")
    )
    private List<Manga> mangas = new ArrayList<>();

    // Constructor vacío requerido por JPA
    public Lista() {}

    public Lista(String nombre) {
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Manga> getMangas() { return mangas; }

    public void agregarManga(Manga manga) {
        if (!mangas.contains(manga)) {
            mangas.add(manga);
        }
    }

    public void eliminarManga(Manga manga) {
        mangas.remove(manga);
    }
}

