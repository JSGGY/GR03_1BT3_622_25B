<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Mis Listas</title>
  <link rel="stylesheet" type="text/css" href="styles.css">
  <style>
    body { background-color: #0d0d1a; font-family: Arial, sans-serif; color: #fff; margin: 0; padding: 20px; }
    .section-container { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
    .section-card { background: linear-gradient(135deg, #16213e, #1a1a2e); border: 1px solid #555588; border-radius: 10px; padding: 25px; }
    .section-card h3 { color: #00d4ff; margin-bottom: 15px; }
    .list-item { background-color: #1f1f3a; padding: 10px 15px; margin-bottom: 10px; border-radius: 5px; }
    .empty-state { color: #888; font-style: italic; text-align: center; padding: 20px; }
    @media (max-width: 768px) { .section-container { grid-template-columns: 1fr; } }
  </style>
</head>
<body>
<h1>📋 Mis Listas (quemadas)</h1>
<div class="section-container">
  <div class="section-card">
    <h3>Lista de Acción</h3>
    <div class="list-item">Attack on Titan</div>
    <div class="list-item">One Punch Man</div>
    <div class="list-item">My Hero Academia</div>
  </div>
  <div class="section-card">
    <h3>Lista de Comedia</h3>
    <div class="list-item">Gintama</div>
    <div class="list-item">KonoSuba</div>
    <div class="list-item">Saiki Kusuo</div>
  </div>
  <div class="section-card">
    <h3>Lista de Romance</h3>
    <div class="list-item">Toradora!</div>
    <div class="list-item">Kaguya-sama</div>
  </div>
  <div class="section-card">
    <h3>Lista de Fantasía</h3>
    <div class="list-item">Re:Zero</div>
    <div class="list-item">Overlord</div>
    <div class="list-item">Sword Art Online</div>
  </div>
</div>
</body>
</html>
