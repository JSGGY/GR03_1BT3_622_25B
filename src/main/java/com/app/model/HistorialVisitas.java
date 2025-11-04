package com.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_visitas",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_manga", "id_lector"})
        })
public class HistorialVisitas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_manga", nullable = false,
            foreignKey = @ForeignKey(name = "fk_historial_manga"))
    private Manga manga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lector", nullable = false,
            foreignKey = @ForeignKey(name = "fk_historial_lector"))
    private Lector lector;

    @Column(name = "fecha_visita", nullable = false)
    private LocalDateTime fechaVisita = LocalDateTime.now();

    // =====================
    // Constructores
    // =====================
    public HistorialVisitas() {
    }

    public HistorialVisitas(Manga manga, Lector lector) {
        this.manga = manga;
        this.lector = lector;
        this.fechaVisita = LocalDateTime.now();
    }

    // =====================
    // Getters y Setters
    // =====================

    public int getId() {
        return id;
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

    public LocalDateTime getFechaVisita() {
        return fechaVisita;
    }

    public void setFechaVisita(LocalDateTime fechaVisita) {
        this.fechaVisita = fechaVisita;
    }
}

