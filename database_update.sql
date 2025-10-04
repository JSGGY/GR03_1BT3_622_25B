-- ===============================================
-- SQL para AdminScan - Sistema completo con Mangas y Capítulos
-- ===============================================
-- Ejecutar este script en tu base de datos MySQL
-- 1. Agregar columna para imagen en la tabla scans (si no existe)
ALTER TABLE scans
ADD COLUMN IF NOT EXISTS imagen_url VARCHAR(255);
-- 2. Actualizar scans existentes con imagen por defecto
UPDATE scans
SET imagen_url = 'images/default-scan.svg'
WHERE imagen_url IS NULL;
-- 3. Agregar columna imagen_portada a la tabla mangas
ALTER TABLE mangas
ADD COLUMN IF NOT EXISTS imagen_portada VARCHAR(255);
-- 4. Crear tabla para capítulos
CREATE TABLE IF NOT EXISTS capitulos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    numero INT NOT NULL,
    descripcion TEXT,
    manga_id INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (manga_id) REFERENCES mangas(id) ON DELETE CASCADE,
    UNIQUE KEY unique_numero_per_manga (manga_id, numero),
    INDEX idx_capitulos_manga (manga_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
-- 5. Crear tabla para imágenes de capítulos
CREATE TABLE IF NOT EXISTS capitulo_imagenes (
    capitulo_id INT NOT NULL,
    imagen_url VARCHAR(255) NOT NULL,
    orden INT NOT NULL,
    PRIMARY KEY (capitulo_id, orden),
    FOREIGN KEY (capitulo_id) REFERENCES capitulos(id) ON DELETE CASCADE,
    INDEX idx_capitulo_imagenes_orden (capitulo_id, orden)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
-- 6. Agregar índices para mejor rendimiento
CREATE INDEX IF NOT EXISTS idx_scans_creado_por ON scans(creado_por_id);
CREATE INDEX IF NOT EXISTS idx_mangas_scan ON mangas(scan_id);
CREATE INDEX IF NOT EXISTS idx_mangas_estado ON mangas(estado);
-- 7. Verificar estructuras actualizadas
DESCRIBE scans;
DESCRIBE mangas;
DESCRIBE capitulos;
DESCRIBE capitulo_imagenes;
-- 8. Verificar datos
SELECT s.id,
    s.nombre,
    s.descripcion,
    s.imagen_url,
    s.creado_por_id,
    COUNT(m.id) as total_mangas
FROM scans s
    LEFT JOIN mangas m ON s.id = m.scan_id
GROUP BY s.id,
    s.nombre,
    s.descripcion,
    s.imagen_url,
    s.creado_por_id
ORDER BY s.id;
-- ===============================================
-- Notas importantes:
-- ===============================================
-- - Las imágenes de scans se guardan en: webapp/images/scans/
-- - Las imágenes de mangas se guardan en: webapp/images/mangas/
-- - Las imágenes de capítulos se guardan en: webapp/images/capitulos/
-- - Si imagen_url es NULL, se usará images/default-scan.svg
-- - Los capítulos pueden tener múltiples imágenes ordenadas
-- - El sistema soporta cascada de eliminación (eliminar scan → elimina mangas → elimina capítulos)
-- ===============================================