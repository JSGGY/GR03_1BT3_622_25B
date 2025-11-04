package com.app.service;

import java.util.Comparator;
import java.util.List;

import com.app.dao.MangaDAO;
import com.app.dao.MangaLikeDAO;
import com.app.model.Lector;
import com.app.model.Manga;

/**
 * Servicio para gestionar likes en mangas.
 * Proporciona funcionalidad para obtener, agregar y ordenar mangas por likes.
 */
public class MangaLikeService {

    private final MangaDAO mangaDAO;
    private final MangaLikeDAO mangaLikeDAO;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param mangaDAO DAO para operaciones de persistencia de Manga
     */
    public MangaLikeService(MangaDAO mangaDAO, MangaLikeDAO mangaLikeDAO) {
        this.mangaDAO = mangaDAO;
        this.mangaLikeDAO = mangaLikeDAO;
    }

    /**
     * Obtiene el total de likes de un manga.
     *
     * @param mangaId ID del manga
     * @return Cantidad total de likes del manga
     */
    public int obtenerTotalLikes(int mangaId) {
        return mangaLikeDAO.contarLikes(mangaId);
    }

    /**
     * Agrega un like a un manga.
     *
     * @param mangaId ID del manga
     * @return true si se agrega exitosamente, false en caso contrario
     */
    public boolean agregarLike(int mangaId, Lector lector) {

        // Verificar si el usuario ya dio like
        if (mangaLikeDAO.existeLike(mangaId, lector.getId())) {
            return false;
        }

        // Obtener el manga
        Manga manga = mangaDAO.buscarPorId(mangaId);
        // Guardar el like
        boolean guardado = mangaLikeDAO.guardarLike(manga, lector);
        if (guardado) {
            manga.agregarLike();
            mangaDAO.guardar(manga);
        }
        return guardado;
    }

    /**
     * Obtiene los mangas de un scan ordenados por cantidad de likes (descendente).
     *
     * @param scanId ID del scan
     * @return Lista de mangas ordenados de mayor a menor likes
     */
    public List<Manga> obtenerMangasOrdenadosPorLikes(int scanId) {
        List<Manga> mangas = mangaDAO.buscarPorScanId(scanId);

        // Actualizar el total de likes para cada manga
        for (Manga manga : mangas) {
            manga.setTotalLikes(mangaLikeDAO.contarLikes(manga.getId()));
        }

        // Ordenar por total de likes descendente
        mangas.sort(Comparator.comparingInt(Manga::getTotalLikes).reversed());

        return mangas;
    }

    /**
     * Verifica si un usuario ya dio like a un manga.
     *
     * @param mangaId ID del manga
     * @param lectorId ID del lector
     * @return true si el usuario ya dio like, false en caso contrario
     */
    public boolean usuarioYaDioLike(int mangaId, int lectorId) {
        return mangaLikeDAO.existeLike(mangaId, lectorId);
    }
}