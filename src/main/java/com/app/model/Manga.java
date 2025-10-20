package com.app.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(name = "totalLikes")
    private int totalLikes = 0;

    public int getTotalLikes() {
        return totalLikes;
    }
    public void agregarLike() {
        this.totalLikes++;
    }
    
    /**
     * Verifica si el manga puede transicionar del estado actual a un nuevo estado.
     * 
     * Reglas de transición:
     * - EN_PROGRESO puede ir a: COMPLETADO, PAUSADO, CANCELADO
     * - PAUSADO puede ir a: EN_PROGRESO, COMPLETADO, CANCELADO
     * - COMPLETADO NO puede cambiar a ningún otro estado (es final)
     * - CANCELADO NO puede cambiar a ningún otro estado (es final)
     * 
     * @param nuevoEstado El estado al que se quiere transicionar
     * @return true si la transición es válida, false en caso contrario
     */
    public boolean puedeTransicionarA(EstadoManga nuevoEstado) {
        // Estados finales no pueden cambiar
        if (this.estado == EstadoManga.COMPLETADO || 
            this.estado == EstadoManga.CANCELADO) {
            return false;
        }
        
        // EN_PROGRESO y PAUSADO pueden transicionar a cualquier estado
        return (this.estado == EstadoManga.EN_PROGRESO || 
                this.estado == EstadoManga.PAUSADO);
    }
}