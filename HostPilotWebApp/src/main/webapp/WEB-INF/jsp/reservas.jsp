<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Reservas - HostPilot</title>

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
        .modal { display: none; position: fixed; z-index: 1001; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.7); padding-top: 60px; }
        .modal-content { background-color: #fefefe; margin: 5% auto; padding: 20px; border: 1px solid #888; width: 80%; max-width: 700px; border-radius: 10px; position: relative; animation: fadeIn 0.5s; }
        @keyframes fadeIn { from {opacity: 0; transform: scale(0.95);} to {opacity: 1; transform: scale(1);} }
        .modal-close { color: #aaa; float: right; font-size: 28px; font-weight: bold; }
        .modal-image { width: 100%; height: 300px; object-fit: cover; border-radius: 8px; margin-bottom: 20px; }
        .reserva-form { margin-top: 20px; border-top: 1px solid #eee; padding-top: 20px; }
        .reserva-form .form-group { margin-bottom: 15px; }
        .reserva-form label { font-weight: 600; }
        .reserva-form input { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
        .btn-reservar { width: 100%; padding: 12px; font-size: 1.2rem; background-color: #28a745; color: white; border: none; border-radius: 5px; cursor: pointer; }
        .login-prompt { text-align: center; background-color: #fff3cd; padding: 15px; border-radius: 5px; }

        /* General layout/utility classes for forms and buttons */
        .mb-4 { margin-bottom: 1.5rem; }
        .text-center { text-align: center; }
        .d-flex { display: flex; }
        .justify-content-between { justify-content: space-between; }
        .align-items-center { align-items: center; }
        .btn { display: inline-block; padding: 0.5rem 1rem; border-radius: 0.25rem; font-weight: 400; text-align: center; vertical-align: middle; cursor: pointer; border: 1px solid transparent; transition: all 0.15s ease-in-out; }
        .btn-primary { color: #fff; background-color: #007bff; border-color: #007bff; }
        .btn-primary:hover { background-color: #0069d9; border-color: #0062cc; }
        .btn-success { color: #fff; background-color: #28a745; border-color: #28a745; }
        .btn-success:hover { background-color: #218838; border-color: #1e7e34; }
        .btn-sm { padding: 0.25rem 0.5rem; font-size: 0.875rem; border-radius: 0.2rem; }
        .mt-2 { margin-top: 0.5rem; }
        .row { display: flex; flex-wrap: wrap; margin-left: -0.75rem; margin-right: -0.75rem; }
        .row > * { padding-left: 0.75rem; padding-right: 0.75rem; }
        .col-md-6 { flex: 0 0 auto; width: 50%; }
        .col-md-3 { flex: 0 0 auto; width: 25%; }
        .text-muted { color: #6c757d !important; }
        .small { font-size: 0.875em !important; }
        .spinner-border { display: inline-block; width: 1rem; height: 1rem; vertical-align: text-bottom; border: 0.15em solid currentColor; border-right-color: transparent; border-radius: 50%; -webkit-animation: spinner-border .75s linear infinite; animation: spinner-border .75s linear infinite; }
        @keyframes spinner-border { to { transform: rotate(360deg); } }
        
        /* New styles for payment section in modal */
        .reserva-form fieldset { border: 1px solid #e0e0e0; border-radius: 8px; padding: 15px; margin-bottom: 20px; background-color: #fdfdfd; }
        .reserva-form legend { font-size: 1.1em; font-weight: bold; color: #001f54; padding: 0 5px; margin-left: -5px; background-color: #fff; border-radius: 3px; }
        .reserva-form .payment-section .form-group { margin-bottom: 10px; }
        .reserva-form .card-details-row { display: flex; gap: 10px; }
        .reserva-form .card-details-row > div { flex: 1; }

        @media (max-width: 992px) {
            .left-col { flex: 1 1 100%; } 
            .right-col { display: none; } 
            body { overflow-y: auto; }
            .modal-content { margin: 10% auto; }
        }
        @media (max-width: 768px) {
            .top-bar { flex-direction: column; gap: 10px; }
            .search-container { width: 100%; }
            .user-menu { width: 100%; justify-content: center; margin-left: 0; }
            nav { border-radius: 0; }
        }
    </style>
</head>
<body>
    <div class="page-wrapper">
        <header class="header-container">
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
                <a href="${pageContext.request.contextPath}/reservas" class="active">Explorar</a>
                <a href="${pageContext.request.contextPath}/mis-reservas">Mis Reservas</a>
                <a href="#">Zonas</a>
                <a href="${pageContext.request.contextPath}/anfitrion">Anfitrión</a>
            </nav>
        </header>

        <div class="page-layout">
            <div class="left-col">
                <div class="airbnb-wrapper">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2 class="text-center mb-0">Alojamientos Disponibles</h2>
                        <a href="${pageContext.request.contextPath}/exportar/alojamientos" class="btn btn-success">
                            Exportar a Excel
                        </a>
                    </div>
                    
                    <div class="ap-grid" id="apartmentGrid">
                        <c:forEach items="${listaPropiedades}" var="prop">
                            <div class="ap-card-wrapper" 
                                 data-id="${prop.id}" 
                                 data-nombre="<c:out value='${prop.titulo}'/>"
                                 data-ciudad="<c:out value='${prop.ciudad}'/>"
                                 data-precio="${prop.precioPorNoche}"
                                 data-rating="${prop.rating}"
                                 data-reviews="${prop.reviews}"
                                 data-descripcion="<c:out value='${prop.descripcion}'/>"
                                 data-img-url="${pageContext.request.contextPath}/${prop.imgUrl}">
                                 
                                <div class="ap-card">
                                    <img src="${pageContext.request.contextPath}/${prop.imgUrl}" alt="<c:out value='${prop.titulo}'/>">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-center mb-1">
                                            <strong class="ap-title"><c:out value="${prop.titulo}"/></strong>
                                            <div class="rating">★ <c:out value="${prop.rating}"/></div>
                                        </div>
                                        <div class="text-muted small"><c:out value="${prop.ciudad}"/>, Perú</div>
                                        <div>
                                            <span class="price">
                                                S/<fmt:formatNumber value="${prop.precioPorNoche}" type="number" minFractionDigits="0" maxFractionDigits="0"/>
                                            </span> noche
                                        </div>
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
                            <form id="formReserva">
                                <input type="hidden" id="propiedadId" name="propiedadId">
                                <div class="row">
                                    <div class="form-group col-md-6">
                                        <label for="checkin">Fecha de Check-in:</label>
                                        <input type="date" id="checkin" name="checkin" class="form-control" required>
                                    </div>
                                    <div class="form-group col-md-6">
                                        <label for="checkout">Fecha de Check-out:</label>
                                        <input type="date" id="checkout" name="checkout" class="form-control" required>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="form-group col-md-3">
                                        <label for="adultos">Adultos:</label>
                                        <input type="number" id="adultos" name="adultos" class="form-control" value="1" min="1" required>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <label for="ninos">Niños:</label>
                                        <input type="number" id="ninos" name="ninos" class="form-control" value="0" min="0">
                                    </div>
                                    <div class="form-group col-md-3">
                                        <label for="bebes">Bebés:</label>
                                        <input type="number" id="bebes" name="bebes" class="form-control" value="0" min="0">
                                    </div>
                                    <div class="form-group col-md-3">
                                        <label for="mascotas">Mascotas:</label>
                                        <input type="number" id="mascotas" name="mascotas" class="form-control" value="0" min="0">
                                    </div>
                                </div>
                                
                                <fieldset>
                                    <legend>Pago</legend>
                                    <div class="payment-section">
                                        <div class="form-group">
                                            <label for="titularTarjeta">Titular de la tarjeta:</label>
                                            <input type="text" id="titularTarjeta" name="titularTarjeta" class="form-control" value="" required>
                                        </div>
                                        <div class="form-group">
                                            <label for="numeroTarjeta">Número de tarjeta:</label>
                                            <input type="text" id="numeroTarjeta" name="numeroTarjeta" class="form-control" value="" required>
                                        </div>
                                        <div class="card-details-row">
                                            <div>
                                                <label for="vencimiento">Vencimiento (MM/AA):</label>
                                                <input type="text" id="vencimiento" name="vencimiento" class="form-control" placeholder="MM/AA" value="" required>
                                            </div>
                                            <div>
                                                <label for="cvv">CVV:</label>
                                                <input type="text" id="cvv" name="cvv" class="form-control" value="" required>
                                            </div>
                                        </div>
                                    </div>
                                </fieldset>

                                <button type="submit" class="btn-reservar mt-3">Reservar Ahora</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <div class="login-prompt alert alert-warning">
                                <h4>Para reservar, necesitas una cuenta</h4>
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Iniciar Sesión</a>
                                o
                                <a href="${pageContext.request.contextPath}/registro" class="btn btn-secondary">Registrarse</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                 <div id="reservaMessage" class="mt-3"></div>
            </div>
        </div>
    </div>

    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
   <script>
document.addEventListener('DOMContentLoaded', function() {
    const contextPath = "${pageContext.request.contextPath}";
    
    const apartmentData = [
        <c:forEach items="${listaPropiedades}" var="prop" varStatus="loop">
            { 
                id: ${prop.id}, 
                name: '<c:out value="${prop.titulo}"/>',
                lat: ${prop.lat}, 
                lng: ${prop.lng},
                ciudad: '<c:out value="${prop.ciudad}"/>',
                precio: ${prop.precioPorNoche},
                rating: ${prop.rating},
                reviews: ${prop.reviews},
                descripcion: '<c:out value="${prop.descripcion}"/>',
                imgUrl: '${pageContext.request.contextPath}/${prop.imgUrl}'
            }<c:if test="${not loop.last}">,</c:if>
        </c:forEach>
    ];

    const apartmentGrid = document.getElementById('apartmentGrid');
    const mapElement = document.getElementById('map');
    const modal = document.getElementById('propiedadModal');
    const modalCloseButton = modal.querySelector('.modal-close');
    const formReserva = document.getElementById('formReserva');
    const reservaMessageDiv = document.getElementById('reservaMessage');
    
    initMap();
    attachEventListeners();

    function initMap() {
        if (!mapElement) { console.error("Elemento del mapa no encontrado."); return; }
        try {
            const map = L.map(mapElement).setView([-9.19, -75.0152], 5);
            L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
                attribution: '© OpenStreetMap © CARTO'
            }).addTo(map);
            
            setTimeout(() => map.invalidateSize(), 200);

            apartmentData.forEach(ap => {
                const marker = L.marker([ap.lat, ap.lng]).addTo(map).bindPopup(`<strong>${ap.name}</strong>`);
                const cardWrapper = document.querySelector(`.ap-card-wrapper[data-id="${ap.id}"]`);
                if (cardWrapper) {
                    marker.on('click', () => cardWrapper.querySelector('.saber-mas-btn').click());
                    cardWrapper.addEventListener('mouseover', () => marker.openPopup());
                    cardWrapper.addEventListener('mouseout', () => marker.closePopup());
                }
            });
        } catch (e) {
            console.error("Error al inicializar el mapa Leaflet:", e);
            mapElement.innerHTML = '<div class="alert alert-danger">Error al cargar mapa.</div>';
        }
    }

    function attachEventListeners() {
        if (apartmentGrid) {
            apartmentGrid.addEventListener('click', function(event) {
                const detailsButton = event.target.closest('.saber-mas-btn');
                if (detailsButton) {
                    const cardWrapper = detailsButton.closest('.ap-card-wrapper');
                    if (cardWrapper) {
                        const propertyId = cardWrapper.dataset.id;
                        const propertyData = apartmentData.find(p => p.id == propertyId);
                        if (propertyData) {
                            openModalWithData(propertyData);
                        }
                    }
                }
            });
        }

        if (modalCloseButton) modalCloseButton.onclick = closeModal;
        window.onclick = (event) => { if (event.target === modal) closeModal(); };
        
        if (formReserva) {
            formReserva.addEventListener('submit', handleReservationSubmit);
        }
    }

    function openModalWithData(propiedad) {
        if (!modal || !propiedad) return;
        
        modal.querySelector('#modalImage').src = propiedad.imgUrl;
        modal.querySelector('#modalTitle').textContent = propiedad.name;
        modal.querySelector('#modalLocation').textContent = `${propiedad.ciudad}, Perú`;
        modal.querySelector('#modalPrice').textContent = propiedad.precio.toFixed(0);
        modal.querySelector('#modalRating').textContent = `${propiedad.rating} (${propiedad.reviews} reviews)`;
        modal.querySelector('#modalDescription').textContent = propiedad.descripcion;
        
        if (reservaMessageDiv) reservaMessageDiv.innerHTML = '';
        if (formReserva) {
            formReserva.reset();
            formReserva.querySelector('#propiedadId').value = propiedad.id;
            formReserva.querySelector('#adultos').value = 1;
            formReserva.querySelector('#ninos').value = 0;
            formReserva.querySelector('#bebes').value = 0;
            formReserva.querySelector('#mascotas').value = 0;
            
            // Vaciar campos de pago
            formReserva.querySelector('#titularTarjeta').value = '';
            formReserva.querySelector('#numeroTarjeta').value = '';
            formReserva.querySelector('#vencimiento').value = '';
            formReserva.querySelector('#cvv').value = '';

            const botonReservar = formReserva.querySelector('.btn-reservar');
            if(botonReservar) {
                botonReservar.disabled = false;
                botonReservar.textContent = 'Reservar Ahora';
            }
        }
        modal.style.display = 'block';
    }
    
    function closeModal() {
        if(modal) modal.style.display = 'none';
    }

    async function handleReservationSubmit(event) {
        event.preventDefault();
        const botonReservar = this.querySelector('.btn-reservar');
        const formData = new FormData(this);

        botonReservar.disabled = true;
        botonReservar.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Procesando...';
        reservaMessageDiv.className = 'alert mt-3';
        reservaMessageDiv.textContent = '';
        
        try {
            const response = await fetch('/HostPilotWebApp/realizar-reserva', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams(formData)
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || `Error del servidor: ${response.status}`);
            }

            if (data.status === 'success') {
                reservaMessageDiv.className = 'alert alert-success mt-3';
                reservaMessageDiv.textContent = data.message;
                botonReservar.textContent = '¡Reservado!';
                setTimeout(closeModal, 3000); 
            } else {
                throw new Error(data.message || 'No se pudo completar la reserva.');
            }
        } catch (error) {
            console.error('Error en la solicitud de reserva:', error);
            reservaMessageDiv.className = 'alert alert-danger mt-3';
            reservaMessageDiv.textContent = error.message;
            botonReservar.disabled = false;
            botonReservar.textContent = 'Reservar Ahora';
        }
    }
});
</script>
</body>
</html>