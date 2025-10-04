<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>AdminScan - Autenticación</title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body class="login-page">
    <div class="auth-container">
        <!-- FORMULARIO DE LOGIN -->
        <div id="login-form" class="auth-form">
            <h2>Iniciar Sesión</h2>
            <form action="login" method="post">
                <div class="form-group">
                    <label for="login-username">Usuario</label>
                    <input type="text" id="login-username" name="username" placeholder="Ingresa tu usuario" required>
                </div>
                <div class="form-group">
                    <label for="login-password">Contraseña</label>
                    <input type="password" id="login-password" name="password" placeholder="Ingresa tu contraseña" required>
                </div>
                <button type="submit" class="btn-primary">Iniciar Sesión</button>
            </form>
            
            <div class="auth-switch">
                <p>¿No tienes cuenta? <a href="#" class="switch-link" onclick="showRegistration()">Regístrate</a></p>
            </div>
        </div>

        <!-- FORMULARIO DE REGISTRO -->
        <div id="register-form" class="auth-form hidden">
            <h2>Registrarse</h2>
            <form action="registro" method="post">
                <div class="form-group">
                    <label for="register-username">Usuario</label>
                    <input type="text" id="register-username" name="username" placeholder="Elige un nombre de usuario" required>
                    <small>Mínimo 3 caracteres, solo letras, números y guiones</small>
                </div>
                <div class="form-group">
                    <label for="register-email">Correo Electrónico</label>
                    <input type="email" id="register-email" name="email" placeholder="tu@email.com" required>
                </div>
                <div class="form-group">
                    <label for="register-password">Contraseña</label>
                    <input type="password" id="register-password" name="password" placeholder="Mínimo 6 caracteres" required>
                    <small>Mínimo 6 caracteres</small>
                </div>
                <div class="form-group">
                    <label for="confirm-password">Confirmar Contraseña</label>
                    <input type="password" id="confirm-password" name="confirmPassword" placeholder="Repite tu contraseña" required>
                </div>
                <button type="submit" class="btn-primary">Crear Cuenta</button>
            </form>
            
            <div class="auth-switch">
                <p>¿Ya tienes cuenta? <a href="#" class="switch-link" onclick="showLogin()">Iniciar Sesión</a></p>
            </div>
        </div>

        <!-- MENSAJES DE ERROR -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
    </div>

    <script>
        function showRegistration() {
            document.getElementById('login-form').classList.add('hidden');
            document.getElementById('register-form').classList.remove('hidden');
        }

        function showLogin() {
            document.getElementById('register-form').classList.add('hidden');
            document.getElementById('login-form').classList.remove('hidden');
        }

        // Validación del formulario de registro
        document.getElementById('register-form').addEventListener('submit', function(e) {
            const password = document.getElementById('register-password').value;
            const confirmPassword = document.getElementById('confirm-password').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Las contraseñas no coinciden');
                return false;
            }
            
            if (password.length < 6) {
                e.preventDefault();
                alert('La contraseña debe tener al menos 6 caracteres');
                return false;
            }
        });

        // Funcionalidad adicional puede agregarse aquí
    </script>
</body>
</html>
