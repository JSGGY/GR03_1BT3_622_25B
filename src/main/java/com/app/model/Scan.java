package com.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "scans")
public class Scan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(name = "imagen_url")
    private String imagenUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private AdminScan creadoPor;
    
    @OneToMany(mappedBy = "scan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Manga> mangas = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public AdminScan getCreadoPor() { return creadoPor; }
    public void setCreadoPor(AdminScan creadoPor) { this.creadoPor = creadoPor; }
    
    public List<Manga> getMangas() { return mangas; }
    public void setMangas(List<Manga> mangas) { this.mangas = mangas; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    @Override
    public String toString() {
        return "Scan{id=" + id + ", nombre='" + nombre + "', descripcion='" + descripcion + "'}";
    }
}