package com.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

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
    
    @Column(name = "imagen_portada")
    private String imagenPortada;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoManga estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_id", nullable = false)
    private Scan scan;
    
    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Capitulo> capitulos = new ArrayList<>();
    

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
    
    public String getImagenPortada() { return imagenPortada; }
    public void setImagenPortada(String imagenPortada) { this.imagenPortada = imagenPortada; }
    
    public List<Capitulo> getCapitulos() { return capitulos; }
    public void setCapitulos(List<Capitulo> capitulos) { this.capitulos = capitulos; }
    
    public void cambiarEstado(EstadoManga nuevoEstado) {
        this.estado = nuevoEstado;
    }
    

    public int getTotalCapitulos() {
        try {
            return capitulos != null ? capitulos.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}