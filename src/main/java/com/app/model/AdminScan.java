package com.app.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_scan")
@DiscriminatorValue("ADMIN_SCAN")
public class AdminScan extends Usuario {
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @OneToMany(mappedBy = "creadoPor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Scan> scansCreados = new ArrayList<>();
    
    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public List<Scan> getScansCreados() { return scansCreados; }
    public void setScansCreados(List<Scan> scansCreados) { this.scansCreados = scansCreados; }
    
    // Métodos de negocio
    public Scan crearScan(String nombre, String descripcion) {
        Scan nuevoScan = new Scan();
        nuevoScan.setNombre(nombre);
        nuevoScan.setDescripcion(descripcion);
        nuevoScan.setCreadoPor(this);
        this.scansCreados.add(nuevoScan);
        return nuevoScan;
    }
}
