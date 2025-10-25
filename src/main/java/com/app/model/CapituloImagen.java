package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "capitulo_imagenes")
public class CapituloImagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitulo_id", nullable = false)
    private Capitulo capitulo;
    
    @Lob
    @Column(name = "imagen_blob", columnDefinition = "MEDIUMBLOB")
    private byte[] imagenBlob;
    
    @Column(name = "imagen_tipo", length = 50)
    private String imagenTipo;
    
    @Column(name = "imagen_nombre")
    private String imagenNombre;
    
    @Column(name = "orden")
    private int orden;
    
    // =======================
    // Getters y Setters
    // =======================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Capitulo getCapitulo() { return capitulo; }
    public void setCapitulo(Capitulo capitulo) { this.capitulo = capitulo; }
    
    public byte[] getImagenBlob() { return imagenBlob; }
    public void setImagenBlob(byte[] imagenBlob) { this.imagenBlob = imagenBlob; }
    
    public String getImagenTipo() { return imagenTipo; }
    public void setImagenTipo(String imagenTipo) { this.imagenTipo = imagenTipo; }
    
    public String getImagenNombre() { return imagenNombre; }
    public void setImagenNombre(String imagenNombre) { this.imagenNombre = imagenNombre; }
    
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
    
    @Override
    public String toString() {
        return "CapituloImagen{id=" + id + ", orden=" + orden + ", nombre='" + imagenNombre + "'}";
    }
}

