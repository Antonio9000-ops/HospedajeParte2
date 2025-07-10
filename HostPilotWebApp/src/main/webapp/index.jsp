<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Hostpilot - Tu Lugar Ideal</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">
    <style>
        
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Inter', sans-serif; }
        body { background-color: #f5f5f5; color: #333; }
        a { text-decoration: none; color: inherit; }
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
        nav a:hover { background-color: rgba(255, 255, 255, 0.1); }
        main { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        h1 { font-size: 48px; margin-bottom: 40px; text-align: center; color: #001f54; }
        .contenido { display: flex; flex-wrap: wrap; gap: 40px; justify-content: center; align-items: center; margin-bottom: 60px; }
        .foto-principal img { width: 100%; max-width: 450px; height: auto; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        .carrusel-container { flex-grow: 1; max-width: 650px; display: flex; align-items: center; gap: 10px; }
        .carrusel { display: flex; gap: 20px; overflow-x: auto; padding: 10px; scrollbar-width: none; -ms-overflow-style: none; }
        .carrusel::-webkit-scrollbar { display: none; }
        .carrusel-btn { background-color: #e0e0e0; border: none; font-size: 24px; padding: 10px 15px; cursor: pointer; border-radius: 50%; z-index: 10; }
        .tarjeta { background-color: white; padding: 15px; border-radius: 10px; width: 220px; box-shadow: 0 4px 10px rgba(0,0,0,0.08); text-align: center; flex-shrink: 0; }
        .tarjeta:hover { transform: translateY(-5px); box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15); }
        .tarjeta img { width: 100%; height: 150px; object-fit: cover; border-radius: 8px; margin-bottom: 10px; }
        .saber-mas { display: inline-block; text-decoration: none; background-color: #0077cc; color: white !important; border: none; padding: 8px 16px; border-radius: 5px; margin-bottom: 8px; cursor: pointer; font-size: 14px; }
        .tarjeta p { font-size: 14px; color: #555; margin-top: 5px; }
        .modal { display: none; position: fixed; z-index: 1001; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.7); padding-top: 60px; }
        .modal-content { background-color: #fefefe; margin: 5% auto; padding: 20px; border: 1px solid #888; width: 80%; max-width: 700px; border-radius: 10px; position: relative; animation: fadeIn 0.5s; }
        @keyframes fadeIn { from {opacity: 0; transform: scale(0.95);} to {opacity: 1; transform: scale(1);} }
        .modal-close { color: #aaa; float: right; font-size: 28px; font-weight: bold; }
        .modal-close:hover, .modal-close:focus { color: black; text-decoration: none; cursor: pointer; }
        .modal-body { padding: 10px 16px; }
        .modal-image { width: 100%; height: 300px; object-fit: cover; border-radius: 8px; margin-bottom: 20px; }
        .modal-title { font-size: 2rem; color: #001f54; margin-bottom: 10px; }
        .modal-location { font-size: 1.1rem; color: #555; margin-bottom: 20px; }
        .modal-details p { margin-bottom: 10px; line-height: 1.6; }
        .modal-details strong { color: #002d7a; }
    </style>
</head>
<body>
    <header>
        <div class="top-bar">
            <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/img/logo.png" alt="Hostpilot logo" class="logo"></a>
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
            <a href="${pageContext.request.contextPath}/anfitrion">Anfitrión</a>
            <c:if test="${sessionScope.userRole == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/admin/dashboard" style="color: #ffc107; font-weight: bold;">Modo ADMIN</a>
        </c:if>
        </nav>
    </header>

    <main>
        <h1>Bienvenidos a Hostpilot</h1>
        <div class="contenido">
            <div class="foto-principal">
                <!-- Imagen principal ahora es dinámica -->
                <c:if test="${not empty propiedadPrincipal}">
                    <img src="${pageContext.request.contextPath}/${propiedadPrincipal.imgUrl}" alt="<c:out value='${propiedadPrincipal.titulo}'/>">
                </c:if>
            </div>
            <div class="carrusel-container">
                <button class="carrusel-btn" onclick="scrollCarrusel('carrusel-1', -1)">◀</button>
                <div class="carrusel" id="carrusel-1">
                    <!-- Carrusel 1 generado con JSTL -->
                    <c:forEach items="${listaCarrusel1}" var="prop">
                        <div class="tarjeta" 
                             data-id="${prop.id}" 
                             data-nombre="<c:out value='${prop.titulo}'/>"
                             data-ciudad="<c:out value='${prop.ciudad}'/>"
                             data-precio="${prop.precioPorNoche}"
                             data-rating="${prop.rating}"
                             data-descripcion="<c:out value='${prop.descripcion}'/>"
                             data-img-url="${pageContext.request.contextPath}/${prop.imgUrl}">
                            
                            <img src="${pageContext.request.contextPath}/${prop.imgUrl}" alt="<c:out value='${prop.titulo}'/>">
                            <button class="saber-mas">Saber más</button>
                            <p><c:out value="${prop.titulo}"/></p>
                        </div>
                    </c:forEach>
                </div>
                <button class="carrusel-btn" onclick="scrollCarrusel('carrusel-1', 1)">▶</button>
            </div>
        </div>

        <hr style="border: 1px solid #ddd; margin: 60px 0;">

        <h1>Tu mejor lugar para relajarte</h1>
        <div class="contenido">
            <div class="carrusel-container">
                <button class="carrusel-btn" onclick="scrollCarrusel('carrusel-2', -1)">◀</button>
                <div class="carrusel" id="carrusel-2">
                    <!-- Carrusel 2 generado con JSTL -->
                    <c:forEach items="${listaCarrusel2}" var="prop">
                        <div class="tarjeta" 
                             data-id="${prop.id}" 
                             data-nombre="<c:out value='${prop.titulo}'/>"
                             data-ciudad="<c:out value='${prop.ciudad}'/>"
                             data-precio="${prop.precioPorNoche}"
                             data-rating="${prop.rating}"
                             data-descripcion="<c:out value='${prop.descripcion}'/>"
                             data-img-url="${pageContext.request.contextPath}/${prop.imgUrl}">
                            
                            <img src="${pageContext.request.contextPath}/${prop.imgUrl}" alt="<c:out value='${prop.titulo}'/>">
                            <button class="saber-mas">Saber más</button>
                            <p><c:out value="${prop.titulo}"/></p>
                        </div>
                    </c:forEach>
                </div>
                <button class="carrusel-btn" onclick="scrollCarrusel('carrusel-2', 1)">▶</button>
            </div>
        </div>
    </main>
    
    <div id="propiedadModal" class="modal">
        <div class="modal-content">
            <span class="modal-close">×</span>
            <div class="modal-body">
                <img id="modalImage" src="" alt="Imagen de la propiedad" class="modal-image">
                <h2 id="modalTitle" class="modal-title"></h2>
                <p id="modalLocation" class="modal-location"></p>
                <div id="modalDetails" class="modal-details">
                    <p><strong>Precio por noche:</strong> S/<span id="modalPrice"></span></p>
                    <p><strong>Rating:</strong> ★ <span id="modalRating"></span></p>
                    <p><strong>Descripción:</strong> <span id="modalDescription"></span></p>
                </div>
            </div>
        </div>
    </div>
    
    <script>
        function scrollCarrusel(carruselId, direccion) {
            const carrusel = document.getElementById(carruselId);
            carrusel.scrollBy({ left: 260, behavior: 'smooth' }); // Ajustado para un scroll fijo
        }
        document.addEventListener('DOMContentLoaded', function() {
            const modal = document.getElementById('propiedadModal');
            const modalCloseButton = document.querySelector('.modal-close');
            
            document.querySelectorAll('.saber-mas').forEach(button => {
                button.addEventListener('click', function() {
                    const tarjeta = this.closest('.tarjeta');
                    
                    document.getElementById('modalImage').src = tarjeta.dataset.imgUrl;
                    document.getElementById('modalTitle').textContent = tarjeta.dataset.nombre;
                    document.getElementById('modalLocation').textContent = tarjeta.dataset.ciudad + ", Perú";
                    document.getElementById('modalPrice').textContent = tarjeta.dataset.precio;
                    document.getElementById('modalRating').textContent = tarjeta.dataset.rating;
                    document.getElementById('modalDescription').textContent = tarjeta.dataset.descripcion;
                    
                    modal.style.display = 'block';
                });
            });

            function closeModal() {
                modal.style.display = 'none';
            }

            if (modalCloseButton) {
                modalCloseButton.onclick = closeModal;
            }
       
            window.onclick = function(event) {
                if (event.target == modal) {
                    closeModal();
                }
            };
        });
    </script>
    
    <script type="text/javascript">
      (function(d, t) {
        var v = d.createElement(t), s = d.getElementsByTagName(t)[0];
        v.onload = function() {
          window.voiceflow.chat.load({
            verify: { projectID: '685c870af59e7dd26c0a0d50' },
            url: 'https://general-runtime.voiceflow.com',
            versionID: 'production'
          });
        }
        v.src = "https://cdn.voiceflow.com/widget-next/bundle.mjs"; v.type = "text/javascript"; s.parentNode.insertBefore(v, s);
      })(document, 'script');
    </script>
</body>
</html>