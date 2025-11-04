package com.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "manga_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_manga", "id_lector"})
})
public class MangaLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_manga", nullable = false)
    private Manga manga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lector", nullable = false)
    private Lector lector;

    public MangaLike() {}

    public MangaLike(Manga manga, Lector lector) {
        this.manga = manga;
        this.lector = lector;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Manga getManga() { return manga; }
    public void setManga(Manga manga) { this.manga = manga; }

    public Lector getLector() { return lector; }
    public void setLector(Lector lector) { this.lector = lector; }
}