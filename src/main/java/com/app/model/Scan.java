package com.app.model;

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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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
    
    @Lob
    @Column(name = "imagen_blob", columnDefinition = "MEDIUMBLOB")
    private byte[] imagenBlob;
    
    @Column(name = "imagen_tipo", length = 50)
    private String imagenTipo;
    
    @Column(name = "imagen_nombre")
    private String imagenNombre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private AdminScan creadoPor;
    
    @OneToMany(mappedBy = "scan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Manga> mangas = new ArrayList<>();

    // =======================
    // Getters y Setters
    // =======================
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
    
    public byte[] getImagenBlob() { return imagenBlob; }
    public void setImagenBlob(byte[] imagenBlob) { this.imagenBlob = imagenBlob; }
    
    public String getImagenTipo() { return imagenTipo; }
    public void setImagenTipo(String imagenTipo) { this.imagenTipo = imagenTipo; }
    
    public String getImagenNombre() { return imagenNombre; }
    public void setImagenNombre(String imagenNombre) { this.imagenNombre = imagenNombre; }
    
    @Override
    public String toString() {
        return "Scan{id=" + id + ", nombre='" + nombre + "', descripcion='" + descripcion + "'}";
    }
}