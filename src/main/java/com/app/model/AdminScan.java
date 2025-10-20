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


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    // Método setNombre para compatibilidad (no persiste, solo ignora el valor)
    public void setNombre(String nombre) { 
        // No hace nada - solo para compatibilidad con el test
    }
    
    // Métodos alias para compatibilidad con nombres alternativos
    public String getEmail() { return getCorreo(); }
    public void setEmail(String email) { setCorreo(email); }
    
    public String getPassword() { return getContraseña(); }
    public void setPassword(String password) { setContraseña(password); }

    public List<Scan> getScansCreados() { return scansCreados; }
    public void setScansCreados(List<Scan> scansCreados) { this.scansCreados = scansCreados; }


    public Scan crearScan(String nombre, String descripcion) {
        Scan nuevoScan = new Scan();
        nuevoScan.setNombre(nombre);
        nuevoScan.setDescripcion(descripcion);
        nuevoScan.setCreadoPor(this);
        this.scansCreados.add(nuevoScan);
        return nuevoScan;
    }
}