    <!DOCTYPE html>
    <html lang="es">
    <head>
      <meta charset="UTF-8">
      <title>Registro y Login</title>
      <link rel="stylesheet" href="estilo4.css">
    </head>
    <body>
      <div class="login">
        <form id="loginForm" method="post" action="LoginServlet">
          <h2>Inicia Sesión</h2>
          <input type="email" name="email" placeholder="Correo electrónico" required>
          <input type="password" name="password" placeholder="Contraseña" required>
          <button type="submit">Ingresar</button>
          <p class="toggle-form">¿No tienes cuenta? <a href="#" onclick="toggleForms()">Regístrate aquí</a></p>
        </form>

        <form id="registerForm" method="post" action="RegistroServlet" style="display: none;">
          <h2>Regístrate</h2>
          <input type="text" name="nombre" placeholder="Nombre completo" required>
          <input type="email" name="email" placeholder="Correo electrónico" required>
          <input type="password" name="password" placeholder="Contraseña" required>
          <button type="submit">Registrarse</button>
          <p class="toggle-form">¿Ya tienes cuenta? <a href="#" onclick="toggleForms()">Inicia sesión aquí</a></p>
        </form>
      </div>

      <script>
        // Función para alternar entre formularios de login y registro
        function toggleForms() {
          const loginForm = document.getElementById('loginForm');
          const registerForm = document.getElementById('registerForm');
          // Alterna la propiedad display entre 'block' y 'none'
          loginForm.style.display = loginForm.style.display === 'none' ? 'block' : 'none';
          registerForm.style.display = registerForm.style.display === 'none' ? 'block' : 'none';

          // Opcional: Puedes añadir lógica para limpiar los campos al cambiar de formulario
          // loginForm.reset();
          // registerForm.reset();
        }

        // Opcional: Mostrar el formulario de login por defecto al cargar la página
        // window.onload = function() {
        //    document.getElementById('loginForm').style.display = 'block';
        //    document.getElementById('registerForm').style.display = 'none';
        // };

      </script>
    </body>
    </html>
    