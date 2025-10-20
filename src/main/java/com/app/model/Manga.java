package com.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.*;

@Entity
@Table(name = "mangas")
public class Manga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @Lob
    @Column(name = "portada_blob", columnDefinition = "MEDIUMBLOB")
    private byte[] portadaBlob;

    @Column(name = "portada_tipo", length = 50)
    private String portadaTipo;

    @Column(name = "portada_nombre")
    private String portadaNombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoManga estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_id", nullable = false)
    private Scan scan;

    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Capitulo> capitulos = new ArrayList<>();

    // =======================
    // Likes thread-safe
    // =======================
    @Transient
    private AtomicInteger totalLikes = new AtomicInteger(0);

    @Column(name = "totalLikes")
    private int totalLikesDB;

    // =======================
    // Getters y Setters
    // =======================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoManga getEstado() { return estado; }
    public void setEstado(EstadoManga estado) { this.estado = estado; }

    public Scan getScan() { return scan; }
    public void setScan(Scan scan) { this.scan = scan; }

    public byte[] getPortadaBlob() { return portadaBlob; }
    public void setPortadaBlob(byte[] portadaBlob) { this.portadaBlob = portadaBlob; }

    public String getPortadaTipo() { return portadaTipo; }
    public void setPortadaTipo(String portadaTipo) { this.portadaTipo = portadaTipo; }

    public String getPortadaNombre() { return portadaNombre; }
    public void setPortadaNombre(String portadaNombre) { this.portadaNombre = portadaNombre; }

    public List<Capitulo> getCapitulos() { return capitulos; }
    public void setCapitulos(List<Capitulo> capitulos) { this.capitulos = capitulos; }

    public int getTotalCapitulos() {
        return capitulos != null ? capitulos.size() : 0;
    }

    // =======================
    // Likes Methods
    // =======================
    public int getTotalLikes() {
        return totalLikes.get();
    }

    public void agregarLike() {
        totalLikes.incrementAndGet();
        totalLikesDB = totalLikes.get();
    }

    public void quitarLike() {
        totalLikes.updateAndGet(val -> val > 0 ? val - 1 : 0);
        totalLikesDB = totalLikes.get();
    }

    public void setTotalLikes(int likes) {
        totalLikes.set(likes);
        totalLikesDB = likes;
    }

    public int getTotalLikesDB() {
        return totalLikesDB;
    }

    public void setTotalLikesDB(int totalLikesDB) {
        this.totalLikesDB = totalLikesDB;
        this.totalLikes.set(totalLikesDB);
    }

    // =======================
    // Estado Methods
    // =======================
    public void cambiarEstado(EstadoManga nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public boolean puedeTransicionarA(EstadoManga nuevoEstado) {
        if (this.estado == EstadoManga.COMPLETADO || this.estado == EstadoManga.CANCELADO) {
            return false;
        }
        return this.estado == EstadoManga.EN_PROGRESO || this.estado == EstadoManga.PAUSADO;
    }
}
