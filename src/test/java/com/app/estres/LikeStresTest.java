package com.app.estres;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.app.dao.MangaDAO;
import com.app.model.Manga;
import com.app.test.TestHelper;

public class LikeStresTest {

    @BeforeAll
    static void setUpDatabase() {
        // Configurar H2 en memoria para todos los tests
        TestHelper.setupTestDatabase();
    }

    @Test
    public void given_manga_when_1000_concurrent_likes_then_count_accurate() throws InterruptedException {
        Manga manga = new Manga();
        manga.setTitulo("Stress Test Manga");

        MangaDAO mangaDAO = new MangaDAO();
        mangaDAO.guardar(manga);

        int numThreads = 1000;
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                manga.agregarLike();
                latch.countDown();
            }).start();
        }

        latch.await();

        assertEquals(1000, manga.getTotalLikes(), "El total de likes debe ser 1000");
    }
}
