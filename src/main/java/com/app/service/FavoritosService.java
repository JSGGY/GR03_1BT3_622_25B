package com.app.service;

import java.util.List;
import com.app.dao.FavoritosDAO;
import com.app.model.Favoritos;
import com.app.model.Manga;
import com.app.model.Lector;

public class FavoritosService {

    private FavoritosDAO favoritosDAO;

    // Constructor por defecto (para producción)
    public FavoritosService() {
        this.favoritosDAO = new FavoritosDAO();
    }

    // Constructor para inyección de DAO (para tests unitarios con Mockito)
    public FavoritosService(FavoritosDAO favoritosDAO) {
        this.favoritosDAO = favoritosDAO;
    }

    public boolean agregarAFavoritos(Lector lector, Manga manga) {
        if (favoritosDAO.existeFavorito(lector, manga)) {
            return false; // ya está en favoritos
        }
        Favoritos favorito = new Favoritos(lector, manga);
        return favoritosDAO.guardar(favorito);
    }

    public boolean quitarDeFavoritos(Lector lector, Manga manga) {
        return favoritosDAO.eliminarFavorito(lector, manga);
    }

    public List<Manga> obtenerFavoritos(Lector lector) {
        return favoritosDAO.obtenerFavoritosPorLector(lector);
    }
}
