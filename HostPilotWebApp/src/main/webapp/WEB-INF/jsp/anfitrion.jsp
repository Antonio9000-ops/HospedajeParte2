<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Conociendo a tu Anfitrión</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href="https://fonts.googleapis.com/css2?family=Segoe+UI:wght@400;500;700&display=swap" rel="stylesheet">
    
    <style>
    /* =============================================================== */
    /*  ESTILOS COMPLETOS PARA ANFITRION.JSP (Consistente con otras páginas) */
    /* =============================================================== */
    
    * { 
        margin: 0; 
        padding: 0; 
        box-sizing: border-box; 
    }
    
    body {
        font-family: 'Inter', 'Segoe UI', sans-serif;
        background-color: #F8F9FB;
        color: #333;
    }

    a {
        text-decoration: none;
        color: inherit;
    }
    ul {
        list-style: none;
    }

    /* --- Estilos del Header (igual que en index.jsp y reservas.jsp) --- */
    header { background-color: #001f54; padding: 15px 20px; display: flex; flex-direction: column; align-items: center; position: sticky; top: 0; z-index: 1000; box-shadow: 0 2px 5px rgba(0,0,0,0.2); }
    .top-bar { display: flex; align-items: center; justify-content: space-between; width: 100%; max-width: 1200px; margin-bottom: 10px; }
    .logo { height: 50px; margin-right: 20px; }
    .search-container { flex-grow: 1; display: flex; align-items: center; max-width: 600px; }
    .search-container input { width: 100%; padding: 10px 15px; border-radius: 20px 0 0 20px; border: 1px solid #ccc; font-size: 1rem; }
    .search-container button { padding: 10px 15px; border: none; background-color: #0077cc; color: white; border-radius: 0 20px 20px 0; cursor: pointer; font-size: 1.1rem; }
    .user-menu { margin-left: 20px; display: flex; align-items: center; gap: 15px; color: white; background-color: #002d7a; padding: 5px 15px; border-radius: 20px; cursor: pointer; }
    .user-menu span { font-size: 1.5rem; }
    .user-menu a { color: white; text-decoration: none; display: flex; align-items: center; }
    .user-menu .user-logout { font-size: 0.9rem; font-weight: 600; margin-left: -5px; }
    nav { background-color: #002d7a; width: 100%; display: flex; justify-content: center; padding: 10px 0; border-radius: 8px; }
    nav a { color: white; text-decoration: none; margin: 0 15px; padding: 10px 15px; border-radius: 5px; font-weight: 600; }
    nav a:hover, nav a.active { background-color: rgba(255, 255, 255, 0.1); }
    
    /* --- Estilos del Contenido Principal (Página Anfitrión) --- */
    .contenedor {
        text-align: center;
        padding: 3rem 1rem;
        max-width: 1200px;
        margin: 0 auto;
    }
    .contenedor h1 {
        color: #001f54;
        margin-bottom: 0.5rem;
        font-size: 2.5rem;
    }
    .subtitulo {
        font-size: 1.1rem;
        color: #555;
        margin-bottom: 2.5rem;
    }
    .tarjetas {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        gap: 2rem;
        justify-content: center;
    }
    .tarjeta {
        background-color: white;
        padding: 1.5rem;
        border-radius: 12px;
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.07);
        text-align: left;
        transition: transform 0.3s;
        cursor: pointer;
    }
    .tarjeta:hover { transform: translateY(-5px); }
    .foto-anfitrion { display: block; margin: 0 auto 1rem auto; width: 100px; height: 100px; border-radius: 50%; object-fit: cover; }
    .nombre-huesped { font-weight: bold; font-size: 1.2rem; color: #002147; }
    .ubicacion { color: #666; font-size: 0.95rem; margin-bottom: 0.5rem; }
    .comentario { font-style: italic; font-size: 1rem; color: #333; margin: 1rem 0; border-left: 3px solid #007BFF; padding-left: 10px; }
    .host-name { font-size: 0.95rem; color: #444; }
    .puntuacion { margin-top: 0.5rem; font-size: 0.95rem; color: #222; }
    .estrella { color: #FFD700; font-size: 1.2rem; }

    /* --- Estilos del Modal --- */
    .modal { display: none; position: fixed; z-index: 1001; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.6); }
    .modal-content { background-color: #fff; margin: 10% auto; padding: 2rem; border-radius: 10px; max-width: 600px; width: 90%; box-shadow: 0 10px 25px rgba(0,0,0,0.2); position: relative; }
    .close { position: absolute; right: 20px; top: 15px; font-size: 28px; font-weight: bold; color: #888; cursor: pointer; }
    .modal-info { display: flex; flex-direction: row; gap: 1.5rem; align-items: flex-start; }
    .modal-foto { width: 100px; height: 100px; border-radius: 50%; object-fit: cover; }
    .modal-texto h3 { margin-bottom: 0.2rem; font-size: 1.4rem; color: #002147; }
    .modal-texto p, .modal-texto ul { margin: 0.5rem 0; color: #444; font-size: 0.95rem; }
    .modal-texto ul { padding-left: 1.2rem; list-style-type: '✓ '; }
    .modal-texto hr { margin: 1rem 0; border: none; border-top: 1px solid #ddd; }
    .contactar-btn { margin-top: 1rem; padding: 0.7rem 1.2rem; background-color: #007BFF; border: none; color: white; border-radius: 8px; font-weight: bold; cursor: pointer; }
    .contactar-btn:hover { background-color: #0056b3; }
    @media (max-width: 600px) { .tarjetas { grid-template-columns: 1fr; } .modal-info { flex-direction: column; align-items: center; text-align: center; } }
</style>
</head>
<body>
    <header>
    <div class="top-bar">
        <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/img/logo.png" alt="Hostpilot Logo" class="logo"></a>
        <form action="${pageContext.request.contextPath}/buscar" method="GET" class="search-container">
            <input type="text" name="q" placeholder="Encuentra tu lugar ideal">
            <button type="submit">🔍</button>
        </form>
        <div class="user-menu">
            <span>☰</span>
            <c:choose>
                <c:when test="${not empty sessionScope.userId}">
                    <a href="${pageContext.request.contextPath}/usuario?action=perfil" title="Mi Perfil"><span>👤</span></a>
                    <a href="${pageContext.request.contextPath}/logout" class="user-logout" title="Cerrar Sesión">Salir</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" title="Iniciar Sesión / Registrarse"><span>👤</span></a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <nav>
        <a href="${pageContext.request.contextPath}/">Inicio</a>
        <a href="${pageContext.request.contextPath}/reservas">Reserva</a>
        <a href="#">Zonas</a>
        <a href="${pageContext.request.contextPath}/anfitrion" class="active">Anfitrión</a> 
    </nav>
</header>

    <div class="contenedor">
        <div class="typewriter-container">
            <h2 class="typewriter">Conociendo a tu anfitrión</h2>
        </div>
        <p class="subtitulo">Conoce la experiencia de otros huéspedes y lo que opinan de su estadía.</p>
        <div class="tarjetas">
            <!-- Las tarjetas de reseñas se generan con JavaScript -->
        </div>
    </div>

    <div id="modalHost" class="modal">
        <div class="modal-content">
            <span class="close">×</span>
            <div class="modal-info">
                <img src="" alt="Foto anfitrión" id="modalFoto" class="modal-foto">
                <div class="modal-texto">
                    <h3 id="modalNombre"></h3>
                    <p id="modalUbicacion" class="ubicacion"></p>
                    <h4>📜 Reglas del anfitrión:</h4>
                    <ul id="modalReglas"></ul>
                    <p><strong>🗣 Idiomas:</strong> <span id="modalIdioma"></span></p>
                    <p><strong>❌ Política de cancelación:</strong> <span id="modalPolitica"></span></p>
                    <hr>
                    <p><strong>📞 Teléfono:</strong> <span id="modalTelefono"></span></p>
                    <p><strong>📧 Correo:</strong> <span id="modalCorreo"></span></p>
                    <button id="contactarBtn" class="contactar-btn">Contactar anfitrión</button>
                </div>
            </div>
        </div>
    </div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const contextPath = "${pageContext.request.contextPath}";
    const anfitrionesData = [
      { nombre: "Laura G.", foto: "https://media.istockphoto.com/id/1368424494/es/foto/retrato-de-estudio-de-una-mujer-alegre.jpg?s=612x612&w=0&k=20&c=V6sLE6kK9t4_QJtnTJ5kp8c8poiWuqdgWdJh59zV14A=", ubicacion: "Lima, Miraflores", comentario: "“Muy amable, el lugar estaba impecable y la comunicación fue excelente.”", huesped: "Carlos M.", puntuacion: 4.5, reglas: ["No fumar", "No fiestas", "Respeta el descanso"], idioma: "Español, Inglés", politica: "Flexible", telefono: "+51 987 654 321", correo: "laura.g@example.com" },
      { nombre: "Javier T.", foto: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRGl69dXBknLvFKA0lSEOfgY6DrxOxvjxCJqw&s", ubicacion: "Cusco, Centro Histórico", comentario: "“Una experiencia maravillosa. Muy servicial y atento.”", huesped: "María R.", puntuacion: 5.0, reglas: ["No mascotas", "Check-in 3pm"], idioma: "Español", politica: "Moderada", telefono: "+51 912 345 678", correo: "javier.t@example.com" },
      { nombre: "Claudia M.", foto: "https://img.freepik.com/foto-gratis/chica-linda-posando_23-2147639420.jpg", ubicacion: "Arequipa, Yanahuara", comentario: "“Bonita casa, muy limpia y bien ubicada. Volvería sin dudar.”", huesped: "Eduardo L.", puntuacion: 4.8, reglas: ["No ruidos fuertes", "Check-out 11am"], idioma: "Español, Inglés", politica: "Estricta", telefono: "+51 998 877 665", correo: "claudia.m@example.com" }
    ];

    const tarjetasContainer = document.querySelector(".tarjetas");
    if (tarjetasContainer) {
        anfitrionesData.forEach((host, index) => {
            const tarjetaDiv = document.createElement('div');
            tarjetaDiv.className = 'tarjeta';
            tarjetaDiv.dataset.index = index;

            let estrellas = '★'.repeat(Math.floor(host.puntuacion)) + '☆'.repeat(5 - Math.floor(host.puntuacion));

            tarjetaDiv.innerHTML = `
                <img src="${host.foto}" alt="${host.nombre}" class="foto-anfitrion">
                <div class="contenido">
                    <div class="nombre-huesped">${host.nombre}</div>
                    <div class="ubicacion">${host.ubicacion}</div>
                    <p class="comentario">${host.comentario}</p>
                    <p class="host-name">Huésped: <strong>${host.huesped}</strong></p>
                    <p class="puntuacion">Puntuación: ${host.puntuacion} <span class="estrella">${estrellas}</span></p>
                </div>
            `;
            tarjetasContainer.appendChild(tarjetaDiv);
        });
    }

    const modal = document.getElementById("modalHost");
    const spanClose = document.querySelector(".close");
    
    document.querySelectorAll(".tarjeta").forEach(card => {
        card.addEventListener("click", () => {
            const hostIndex = card.dataset.index;
            const host = anfitrionesData[hostIndex];
            
            document.getElementById("modalFoto").src = host.foto;
            document.getElementById("modalNombre").textContent = host.nombre;
            document.getElementById("modalUbicacion").textContent = host.ubicacion;
            document.getElementById("modalIdioma").textContent = host.idioma;
            document.getElementById("modalPolitica").textContent = host.politica;
            document.getElementById("modalTelefono").textContent = host.telefono;
            document.getElementById("modalCorreo").textContent = host.correo;

            const reglasList = document.getElementById("modalReglas");
            reglasList.innerHTML = "";
            host.reglas.forEach(regla => {
                const li = document.createElement("li");
                li.textContent = regla;
                reglasList.appendChild(li);
            });

            modal.style.display = "block";
        });
    });

    if(spanClose) spanClose.onclick = () => modal.style.display = "none";
    window.onclick = (event) => { if (event.target === modal) modal.style.display = "none"; };
});
</script>
</body>
</html>