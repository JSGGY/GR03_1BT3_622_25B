package com.app.service;

import java.util.List;

import com.app.dao.ComentarioMangaDAO;
import com.app.model.ComentarioManga;
import com.app.model.Lector;
import com.app.model.Manga;

public class ComentarioMangaService {

    private final ComentarioMangaDAO comentarioDAO;

    // Constructor por defecto (para producción)
    public ComentarioMangaService() {
        this.comentarioDAO = new ComentarioMangaDAO();
    }

    // Constructor para inyección de DAO (para tests unitarios con Mockito)
    public ComentarioMangaService(ComentarioMangaDAO comentarioDAO) {
        this.comentarioDAO = comentarioDAO;
    }

    /**
     * Publica un nuevo comentario en un manga
     * Valida que el comentario no esté vacío
     * 
     * @param lector Lector que publica el comentario
     * @param manga Manga al que se añade el comentario
     * @param textoComentario Texto del comentario
     * @return true si se publicó correctamente, false si está vacío o hubo error
     */
    public boolean publicarComentario(Lector lector, Manga manga, String textoComentario) {
        // Validar que el comentario no esté vacío
        if (textoComentario == null || textoComentario.trim().isEmpty()) {
            return false;
        }

        ComentarioManga comentario = new ComentarioManga(manga, lector, textoComentario.trim());
        
        // Validar usando el método del modelo
        if (!comentario.esComentarioValido()) {
            return false;
        }

        return comentarioDAO.guardar(comentario);
    }

    /**
     * Obtiene todos los comentarios de un manga
     * 
     * @param manga Manga del cual obtener comentarios
     * @return Lista de comentarios ordenados por fecha (más recientes primero)
     */
    public List<ComentarioManga> obtenerComentariosDeManga(Manga manga) {
        return comentarioDAO.obtenerComentariosPorManga(manga);
    }

    /**
     * Obtiene todos los comentarios de un manga por su ID
     * 
     * @param mangaId ID del manga
     * @return Lista de comentarios ordenados por fecha (más recientes primero)
     */
    public List<ComentarioManga> obtenerComentariosDeMangaPorId(int mangaId) {
        return comentarioDAO.obtenerComentariosPorMangaId(mangaId);
    }

    /**
     * Elimina un comentario
     * 
     * @param comentarioId ID del comentario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarComentario(int comentarioId) {
        return comentarioDAO.eliminar(comentarioId);
    }

    /**
     * Obtiene el número de comentarios de un manga
     * 
     * @param mangaId ID del manga
     * @return Número de comentarios
     */
    public long contarComentarios(int mangaId) {
        return comentarioDAO.contarComentariosPorManga(mangaId);
    }

    /**
     * Valida que un texto de comentario sea válido
     * 
     * @param textoComentario Texto a validar
     * @return true si es válido, false si está vacío o es null
     */
    public boolean validarTextoComentario(String textoComentario) {
        return textoComentario != null && !textoComentario.trim().isEmpty();
    }
}

