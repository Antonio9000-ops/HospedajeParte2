<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Reservas - HostPilot</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Inter', sans-serif; }
        a { text-decoration: none; color: inherit; }
        header { background-color: #001f54; padding: 15px 20px; display: flex; flex-direction: column; align-items: center; box-shadow: 0 2px 5px rgba(0,0,0,0.2); }
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
        body { overflow: hidden; }
        .page-wrapper { display: flex; flex-direction: column; height: 100vh; }
        .header-container { flex-shrink: 0; position: sticky; top: 0; z-index: 100; }
        .page-layout { display: flex; flex-grow: 1; overflow: hidden; }
        .left-col { flex: 0 0 60%; height: 100%; overflow-y: auto; padding: 2rem; background-color: #f8f9fa; }
        .right-col { flex: 0 0 40%; height: 100%; }
        #map { height: 100%; width: 100%;  min-height: 500px; background-color: #e0e0e0; }
        .airbnb-wrapper { background: #fff; padding: 2rem; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .ap-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 1.5rem; }
        .ap-card { border: 2px solid transparent; border-radius: 12px; transition: all 0.2s ease-in-out; background-color: white; overflow: hidden; }
        .ap-card.highlight, .ap-link:hover .ap-card { transform: translateY(-3px); box-shadow: 0 6px 16px rgba(0,0,0,.12); border-color: #007bff; }
        .ap-card img { height: 200px; width: 100%; object-fit: cover; }
        .card-body { padding: 1rem; }
        .ap-title { font-size: 1.1rem; font-weight: 600; }
        .price { font-weight: 700; color: #333; }
        @media (max-width: 992px) { .left-col { flex: 1 1 100%; } .right-col { display: none; } body { overflow-y: auto; } }
        .modal { display: none; position: fixed; z-index: 1001; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.7); padding-top: 60px; }
        .modal-content { background-color: #fefefe; margin: 5% auto; padding: 20px; border: 1px solid #888; width: 80%; max-width: 700px; border-radius: 10px; position: relative; animation: fadeIn 0.5s; }
        .modal-close { color: #aaa; float: right; font-size: 28px; font-weight: bold; }
        .modal-image { width: 100%; height: 300px; object-fit: cover; border-radius: 8px; margin-bottom: 20px; }
        .reserva-form { margin-top: 20px; border-top: 1px solid #eee; padding-top: 20px; }
        .reserva-form .form-group { margin-bottom: 15px; }
        .reserva-form label { font-weight: 600; }
        .reserva-form input { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
        .btn-reservar { width: 100%; padding: 12px; font-size: 1.2rem; background-color: #28a745; color: white; border: none; border-radius: 5px; cursor: pointer; }
        .login-prompt { text-align: center; background-color: #fff3cd; padding: 15px; border-radius: 5px; }
    </style>
</head>
<body>
<div class="page-wrapper">
    <header class="header-container">
        <!-- INICIO: HTML del Header copiado aquí -->
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
            <a href="${pageContext.request.contextPath}/reservas" class="active">Reserva</a>
            <a href="#">Zonas</a>
             <a href="${pageContext.request.contextPath}/anfitrion">Anfitrión</a>
        </nav>
        <!-- FIN: HTML del Header -->
    </header>

 <div class="page-layout">
        <div class="left-col">
            <div class="airbnb-wrapper">
                <h2 class="text-center mb-4">Alojamientos Disponibles</h2>
                <div class="ap-grid" id="apartmentGrid">
                    
                    <!-- Las tarjetas ahora se generan con JSTL -->
                    <c:forEach items="${listaPropiedades}" var="prop">
                        <div class="ap-card-wrapper" 
                             data-id="${prop.id}" 
                             data-nombre="<c:out value='${prop.nombre}'/>"
                             data-ciudad="<c:out value='${prop.ciudad}'/>"
                             data-precio="<fmt:formatNumber value='${prop.precio}' type='number' minFractionDigits='0'/>"
                             data-rating="${prop.rating}"
                             data-reviews="${prop.reviews}"
                             data-img-url="${prop.imgUrl}">
                             
                            <div class="ap-card">
                                <img src="${prop.imgUrl}" alt="<c:out value='${prop.nombre}'/>">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-center mb-1">
                                        <strong class="ap-title"><c:out value="${prop.nombre}"/></strong>
                                        <div class="rating">★ <c:out value="${prop.rating}"/></div>
                                    </div>
                                    <div class="text-muted small"><c:out value="${prop.ciudad}"/>, Perú</div>
                                    <div><span class="price">S/<fmt:formatNumber value='${prop.precio}' type='number' minFractionDigits='0'/></span> noche</div>
                                    <button class="saber-mas-btn mt-2 btn btn-sm btn-primary">Ver Detalles</button>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    
                </div>
            </div>
        </div>
        <div class="right-col">
            <div id="map"></div>
        </div>
    </div>
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
            
            <div class="reserva-form">
                <c:choose>
                    <c:when test="${not empty sessionScope.userId}">
                        <!-- Formulario de reserva para usuarios logueados -->
                        <form id="formReserva">
                            <input type="hidden" id="propiedadId" name="propiedadId">
                            <div class="form-group">
                                <label for="checkin">Fecha de Check-in:</label>
                                <input type="date" id="checkin" name="checkin" required>
                            </div>
                            <div class="form-group">
                                <label for="checkout">Fecha de Check-out:</label>
                                <input type="date" id="checkout" name="checkout" required>
                            </div>
                            <button type="submit" class="btn-reservar">Reservar Ahora</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <!-- Mensaje para usuarios no logueados -->
                        <div class="login-prompt">
                            <h4>Para reservar, necesitas una cuenta</h4>
                            <p>
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Iniciar Sesión</a>
                                o
                                <a href="${pageContext.request.contextPath}/registro" class="btn btn-secondary">Registrarse</a>
                            </p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
             <div id="reservaMessage" class="mt-3"></div> <!-- Para mostrar mensajes de éxito/error -->
        </div>
    </div>
</div>
        

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    // 1. GENERAR LOS DATOS PARA JAVASCRIPT A PARTIR DEL MODELO DEL CONTROLADOR
    const apartmentData = [
        <c:forEach items="${listaPropiedades}" var="prop" varStatus="loop">
            { 
                id: ${prop.id}, 
                name: '<c:out value="${prop.nombre}"/>', 
                lat: ${prop.lat}, 
                lng: ${prop.lng}
            }<c:if test="${not loop.last}">,</c:if>
        </c:forEach>
    ];

    const mapElement = document.getElementById('map');
    const modal = document.getElementById('propiedadModal');
    const modalCloseButton = document.querySelector('.modal-close');
    const formReserva = document.getElementById('formReserva');
    const reservaMessageDiv = document.getElementById('reservaMessage');

    // 2. INICIALIZAR EL MAPA Y MARCADORES
    if (mapElement) {
        try {
            const map = L.map(mapElement).setView([-9.19, -75.0152], 5);
            L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png', {
                attribution: '© OpenStreetMap, © CartoDB'
            }).addTo(map);
            
            setTimeout(() => map.invalidateSize(), 100);

            const markers = {};
            apartmentData.forEach(ap => {
                const marker = L.marker([ap.lat, ap.lng]).addTo(map).bindPopup(`<strong>${ap.name}</strong>`);
                markers[ap.id] = marker;
                marker.on('click', () => openModalWithData(document.querySelector(`.ap-card-wrapper[data-id="${ap.id}"]`)));
            });

            // 3. AÑADIR EVENT LISTENERS A LAS TARJETAS GENERADAS POR JSTL
            document.querySelectorAll('.ap-card-wrapper').forEach(cardWrapper => {
                const id = cardWrapper.dataset.id;
                const marker = markers[id];

                // Abrir modal al hacer clic en el botón "Ver Detalles"
                cardWrapper.querySelector('.saber-mas-btn').addEventListener('click', (event) => {
                    event.preventDefault();
                    openModalWithData(cardWrapper);
                });

                // Interactividad con el mapa
                if (marker) {
                    cardWrapper.addEventListener('mouseover', () => { marker.openPopup(); cardWrapper.querySelector('.ap-card').classList.add('highlight'); });
                    cardWrapper.addEventListener('mouseout', () => { marker.closePopup(); cardWrapper.querySelector('.ap-card').classList.remove('highlight'); });
                }
            });

        } catch (e) {
            console.error("Error al inicializar el mapa:", e);
            mapElement.innerHTML = '<div class="alert alert-danger m-3">Error al cargar el mapa.</div>';
        }
    }

    // 4. LÓGICA DEL MODAL Y FORMULARIO
    function openModalWithData(cardWrapper) {
        if (!modal || !cardWrapper) return;
        
        // Obtener datos de los atributos data-* de la tarjeta
        document.getElementById('modalImage').src = cardWrapper.dataset.imgUrl;
        document.getElementById('modalTitle').textContent = cardWrapper.dataset.nombre;
        document.getElementById('modalLocation').textContent = `${cardWrapper.dataset.ciudad}, Perú`;
        document.getElementById('modalPrice').textContent = cardWrapper.dataset.precio;
        document.getElementById('modalRating').textContent = `${cardWrapper.dataset.rating} (${cardWrapper.dataset.reviews} reviews)`;
        document.getElementById('modalDescription').textContent = `Descubre este increíble alojamiento en ${cardWrapper.dataset.ciudad}.`;
        
        if (reservaMessageDiv) reservaMessageDiv.innerHTML = '';
        if (formReserva) document.getElementById('propiedadId').value = cardWrapper.dataset.id;

        modal.style.display = 'block';
    }

    if (modalCloseButton) {
        modalCloseButton.onclick = () => { modal.style.display = 'none'; };
    }
    window.onclick = (event) => {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    };
    
    if (formReserva) {
        // ... (el código del fetch para el formulario de reserva se mantiene igual) ...
    }
});
</script>
</body>
</html>