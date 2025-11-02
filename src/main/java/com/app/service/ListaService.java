package com.app.service;

import java.util.List;

import com.app.dao.ListaDAO;
import com.app.dao.LectorDAO;
import com.app.dao.MangaDAO;
import com.app.model.Lista;
import com.app.model.Lector;
import com.app.model.Manga;

public class ListaService {
    private ListaDAO listaDAO = new ListaDAO();
    private LectorDAO lectorDAO = new LectorDAO();
    private MangaDAO mangaDAO = new MangaDAO();

    /**
     * Crea una nueva lista para un lector
     */
    public boolean crearLista(int lectorId, String nombre, String descripcion) {
        try {
            // Validar datos
            if (nombre == null || nombre.trim().isEmpty()) {
                System.out.println("ERROR: El nombre de la lista no puede estar vacío");
                return false;
            }

            // Verificar si ya existe una lista con ese nombre para el lector
            if (listaDAO.existeNombreEnLector(nombre.trim(), lectorId)) {
                System.out.println("ERROR: Ya existe una lista con ese nombre para este lector");
                return false;
            }

            // Obtener el lector
            Lector lector = lectorDAO.buscarPorId(lectorId);
            if (lector == null) {
                System.out.println("ERROR: Lector no encontrado");
                return false;
            }

            // Crear la lista
            Lista lista = new Lista();
            lista.setNombre(nombre.trim());
            lista.setDescripcion(descripcion != null ? descripcion.trim() : null);
            lista.setLector(lector);

            return listaDAO.guardar(lista);
        } catch (Exception e) {
            System.err.println("ERROR al crear lista: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las listas de un lector
     */
    public List<Lista> obtenerListasPorLector(int lectorId) {
        try {
            return listaDAO.buscarPorLectorId(lectorId);
        } catch (Exception e) {
            System.err.println("ERROR al obtener listas: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Obtiene una lista por su ID
     */
    public Lista obtenerListaPorId(int id) {
        try {
            return listaDAO.buscarPorId(id);
        } catch (Exception e) {
            System.err.println("ERROR al obtener lista: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Elimina una lista
     */
    public boolean eliminarLista(int listaId, int lectorId) {
        try {
            // Verificar que la lista pertenece al lector
            Lista lista = listaDAO.buscarPorId(listaId);
            if (lista == null || lista.getLector().getId() != lectorId) {
                System.out.println("ERROR: La lista no existe o no pertenece al lector");
                return false;
            }

            return listaDAO.eliminar(listaId);
        } catch (Exception e) {
            System.err.println("ERROR al eliminar lista: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Agrega un manga a una lista
     */
    public boolean agregarMangaALista(int listaId, int mangaId, int lectorId) {
        try {
            // Verificar que la lista pertenece al lector
            Lista lista = listaDAO.buscarPorId(listaId);
            if (lista == null || lista.getLector().getId() != lectorId) {
                System.out.println("ERROR: La lista no existe o no pertenece al lector");
                return false;
            }

            // Verificar que el manga existe
            Manga manga = mangaDAO.buscarPorId(mangaId);
            if (manga == null) {
                System.out.println("ERROR: El manga no existe");
                return false;
            }

            return listaDAO.agregarMangaALista(listaId, mangaId);
        } catch (Exception e) {
            System.err.println("ERROR al agregar manga a lista: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remueve un manga de una lista
     */
    public boolean removerMangaDeLista(int listaId, int mangaId, int lectorId) {
        try {
            // Verificar que la lista pertenece al lector
            Lista lista = listaDAO.buscarPorId(listaId);
            if (lista == null || lista.getLector().getId() != lectorId) {
                System.out.println("ERROR: La lista no existe o no pertenece al lector");
                return false;
            }

            return listaDAO.removerMangaDeLista(listaId, mangaId);
        } catch (Exception e) {
            System.err.println("ERROR al remover manga de lista: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

