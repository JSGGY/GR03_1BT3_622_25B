package com.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mangas_favoritos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_lector", "id_manga"})
})
public class Favoritos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Relación con el lector
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lector", nullable = false)
    private Lector lector;

    // Relación con el manga
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_manga", nullable = false)
    private Manga manga;

    // Constructor vacío (requerido por JPA)
    public Favoritos() {}

    // Constructor conveniente
    public Favoritos(Lector lector, Manga manga) {
        this.lector = lector;
        this.manga = manga;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Lector getLector() {
        return lector;
    }

    public void setLector(Lector lector) {
        this.lector = lector;
    }

    public Manga getManga() {
        return manga;
    }

    public void setManga(Manga manga) {
        this.manga = manga;
    }

}

