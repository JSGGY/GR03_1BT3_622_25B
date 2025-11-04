package com.app.service;

import com.app.dao.HistorialVisitasDAO;
import com.app.model.HistorialVisitas;
import com.app.model.Lector;
import com.app.model.Manga;

import java.util.List;
import java.util.stream.Collectors;

public class HistorialVisitasService {

    private final HistorialVisitasDAO historialDAO;

    public HistorialVisitasService() {
        this.historialDAO = new HistorialVisitasDAO();
    }

    /**
     * Registra una nueva visita o actualiza la fecha de una existente.
     */
    public void registrarVisita(Lector lector, Manga manga) {
        historialDAO.registrarOActualizarVisita(lector, manga);
    }

    /**
     * Obtiene una lista de mangas visitados recientemente por el lector.
     */
    public List<Manga> obtenerMangasVisitadosRecientes(Lector lector) {
        List<HistorialVisitas> visitas = historialDAO.obtenerUltimasVisitas(lector);
        return visitas.stream()
                .map(HistorialVisitas::getManga)
                .collect(Collectors.toList());
    }
}

