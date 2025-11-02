package com.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "lista_manga")
public class ListaManga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_id", nullable = false)
    private Lista lista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manga_id", nullable = false)
    private Manga manga;

    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;

    @PrePersist
    protected void onCreate() {
        fechaAgregado = LocalDateTime.now();
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

    public Lista getLista() {
        return lista;
    }

    public void setLista(Lista lista) {
        this.lista = lista;
    }

    public Manga getManga() {
        return manga;
    }

    public void setManga(Manga manga) {
        this.manga = manga;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
}

