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
          <h2>Inicia Sesi�n</h2>
          <input type="email" name="email" placeholder="Correo electr�nico" required>
          <input type="password" name="password" placeholder="Contrase�a" required>
          <button type="submit">Ingresar</button>
          <p class="toggle-form">�No tienes cuenta? <a href="#" onclick="toggleForms()">Reg�strate aqu�</a></p>
        </form>

        <form id="registerForm" method="post" action="RegistroServlet" style="display: none;">
          <h2>Reg�strate</h2>
          <input type="text" name="nombre" placeholder="Nombre completo" required>
          <input type="email" name="email" placeholder="Correo electr�nico" required>
          <input type="password" name="password" placeholder="Contrase�a" required>
          <button type="submit">Registrarse</button>
          <p class="toggle-form">�Ya tienes cuenta? <a href="#" onclick="toggleForms()">Inicia sesi�n aqu�</a></p>
        </form>
      </div>

      <script>
        // Funci�n para alternar entre formularios de login y registro
        function toggleForms() {
          const loginForm = document.getElementById('loginForm');
          const registerForm = document.getElementById('registerForm');
          // Alterna la propiedad display entre 'block' y 'none'
          loginForm.style.display = loginForm.style.display === 'none' ? 'block' : 'none';
          registerForm.style.display = registerForm.style.display === 'none' ? 'block' : 'none';

          // Opcional: Puedes a�adir l�gica para limpiar los campos al cambiar de formulario
          // loginForm.reset();
          // registerForm.reset();
        }

        // Opcional: Mostrar el formulario de login por defecto al cargar la p�gina
        // window.onload = function() {
        //    document.getElementById('loginForm').style.display = 'block';
        //    document.getElementById('registerForm').style.display = 'none';
        // };

      </script>
    </body>
    </html>
    