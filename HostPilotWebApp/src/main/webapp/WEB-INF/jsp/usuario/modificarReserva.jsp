<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Modificar Reserva - Hostpilot</title>
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
        nav a:hover, nav a.active { background-color: rgba(255, 255, 255, 0.1); }
        main { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        h1 { font-size: 48px; margin-bottom: 40px; text-align: center; color: #001f54; }
        .alert { padding: 15px; margin-bottom: 20px; border-radius: 4px; font-size: 0.9em; }
        .alert-success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-danger { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }

        /* Estilos específicos para Modificar Reserva (basado en la imagen de diseño original) */
        .main-content { max-width: 700px; margin: 20px auto; padding: 30px; background-color: #fff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        .property-summary { display: flex; flex-direction: column; align-items: center; text-align: center; margin-bottom: 30px; }
        .property-summary .summary-image { width: 100%; max-width: 450px; height: 250px; object-fit: cover; border-radius: 8px; margin-bottom: 15px; }
        .property-summary .summary-info h2 { margin: 0 0 5px 0; font-size: 2em; color: #333; }
        .property-summary .summary-info p { margin: 0 0 10px 0; color: #666; font-size: 1.1em; }
        .property-summary .summary-info .price { font-weight: bold; color: #001f54; font-size: 1.2em; }

        .modification-form fieldset { border: 1px solid #e0e0e0; border-radius: 8px; padding: 25px; margin-bottom: 25px; background-color: #fdfdfd; }
        .modification-form legend { font-size: 1.4em; font-weight: bold; color: #001f54; padding: 0 10px; margin-left: -10px; background-color: #fff; border-radius: 5px; }
        .modification-form .form-group { margin-bottom: 20px; }
        .modification-form label { display: block; margin-bottom: 8px; font-weight: 600; color: #555; }
        .modification-form input.form-control { width: 100%; padding: 12px 15px; border: 1px solid #ccc; border-radius: 8px; font-size: 1rem; }
        
        .modification-form .guests-row { display: flex; flex-wrap: wrap; gap: 20px; margin-bottom: 10px; }
        .modification-form .guest-input-group { flex: 1 1 120px; max-width: 150px; }
        .modification-form .guest-input-group label { font-size: 0.9em; color: #777; margin-bottom: 5px; }
        .modification-form .guest-input-group input[type="number"] { width: 100%; text-align: center; }

        .modification-form .personal-details-row { display: flex; flex-wrap: wrap; gap: 20px; margin-top: 20px; }
        .modification-form .personal-details-row .form-group { flex: 1 1 calc(33.33% - 20px); min-width: 150px; }

        .modification-form .payment-summary p { margin-bottom: 10px; font-size: 1em; color: #555; }
        .modification-form .payment-summary strong { color: #333; }
        .modification-form .payment-summary span { font-weight: bold; color: #001f54; }

        .payment-section .form-group { margin-bottom: 15px; }
        .payment-section .card-details-row { display: flex; gap: 15px; }
        .payment-section .card-details-row > div { flex: 1; }
        .payment-section .card-details-row input { text-align: center; }

        .form-actions { display: flex; justify-content: flex-end; gap: 15px; margin-top: 30px; }
        .btn { display: inline-block; padding: 0.5rem 1rem; border-radius: 0.25rem; font-weight: 400; text-align: center; vertical-align: middle; cursor: pointer; border: 1px solid transparent; transition: all 0.15s ease-in-out; }
        .btn-primary { color: #fff; background-color: #007bff; border-color: #007bff; }
        .btn-primary:hover { background-color: #0069d9; border-color: #0062cc; }
        .btn-secondary { color: #fff; background-color: #6c757d; border-color: #6c757d; }
        .btn-secondary:hover { background-color: #5a6268; border-color: #545b62; }
        
        @media (max-width: 768px) {
            .top-bar { flex-direction: column; gap: 10px; }
            .search-container { width: 100%; }
            .user-menu { width: 100%; justify-content: center; margin-left: 0; }
            nav { border-radius: 0; }
            .main-content { padding: 15px; }
            .property-summary .summary-image { height: 180px; }
            .property-summary .summary-info h2 { font-size: 1.5em; }
            .modification-form .guest-input-group { flex: 1 1 45%; max-width: none; }
            .modification-form .personal-details-row .form-group { flex: 1 1 100%; }
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
                <a href="${pageContext.request.contextPath}/mis-reservas">Mis Reservas</a>
                <a href="${pageContext.request.contextPath}/historial-pagos">Historial de Pagos</a>
              
                <a href="${pageContext.request.contextPath}/anfitrion">Anfitrión</a>
            </nav>
        </header>

        <div class="main-content">
            <h1>Modifica los detalles de la reservación</h1>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <div class="property-summary">
                <c:if test="${not empty propiedad}">
                    <img src="${pageContext.request.contextPath}/${propiedad.imgUrl}" alt="${propiedad.titulo}" class="summary-image">
                    <div class="summary-info">
                        <h2>${propiedad.titulo}</h2>
                        <p>${propiedad.ciudad}, Perú</p>
                        <p class="price">S/<fmt:formatNumber value="${propiedad.precioPorNoche}" type="number" minFractionDigits="0" maxFractionDigits="0"/> por noche</p>
                    </div>
                </c:if>
                <c:if test="${empty propiedad}">
                    <p>No se pudo cargar la información de la propiedad.</p>
                </c:if>
            </div>

            <form action="${pageContext.request.contextPath}/modificar-reserva" method="POST" class="modification-form">
                <input type="hidden" name="idReserva" value="${reserva.id}">
                <input type="hidden" name="idPropiedad" value="${reserva.idPropiedad}">

                <fieldset>
                    <legend>Fechas</legend>
                    <div class="form-group">
                        <label for="checkin">Check-in:</label>
                        <input type="date" id="checkin" name="checkin" class="form-control"
                            value="${reserva.fechaCheckin}" required>
                    </div>
                    <div class="form-group">
                        <label for="checkout">Check-out:</label>
                        <input type="date" id="checkout" name="checkout" class="form-control"
                            value="${reserva.fechaCheckout}" required>
                    </div>
                </fieldset>

                <fieldset>
                    <legend>Huéspedes</legend>
                    <div class="guests-row">
                        <div class="form-group guest-input-group">
                            <label for="adultos">Adultos:</label>
                            <input type="number" id="adultos" name="adultos" class="form-control" min="1" value="${reserva.numeroAdultos}" required>
                        </div>
                        <div class="form-group guest-input-group">
                            <label for="ninos">Niños (Edades 2 a 12):</label>
                            <input type="number" id="ninos" name="ninos" class="form-control" min="0" value="${reserva.numeroNinos}">
                        </div>
                        <div class="form-group guest-input-group">
                            <label for="bebes">Bebés (Menos de 2 años):</label>
                            <input type="number" id="bebes" name="bebes" class="form-control" min="0" value="${reserva.numeroBebes}">
                        </div>
                        <div class="form-group guest-input-group">
                            <label for="mascotas">Mascotas:</label>
                            <input type="number" id="mascotas" name="mascotas" class="form-control" min="0" value="${reserva.numeroMascotas}">
                        </div>
                    </div>
                </fieldset>

                <fieldset class="payment-summary">
                    <legend>Resumen de pago</legend>
                    <p><strong>Precio por noche:</strong> S/<span id="precioNocheDisplay"><fmt:formatNumber value="${propiedad.precioPorNoche}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span></p>
                    <c:set var="numNochesCalculado" value="${(reserva.fechaCheckout.toEpochDay() - reserva.fechaCheckin.toEpochDay())}"/>
                    <p><strong>Número de noches:</strong> <span id="numNoches">${numNochesCalculado}</span></p>
                    <p><strong>Total estimado:</strong> S/<span id="totalEstimado"><fmt:formatNumber value="${reserva.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span></p>
                </fieldset>

                <fieldset>
                    <legend>Pago</legend>
                    <div class="payment-section">
                        <div class="form-group">
                            <label for="titularTarjeta">Titular de la tarjeta:</label>
                            <input type="text" id="titularTarjeta" name="titularTarjeta" class="form-control" value="">
                        </div>
                        <div class="form-group">
                            <label for="numeroTarjeta">Número de tarjeta:</label>
                            <input type="text" id="numeroTarjeta" name="numeroTarjeta" class="form-control" value="">
                        </div>
                        <div class="card-details-row">
                            <div>
                                <label for="vencimiento">Vencimiento (MM/AA):</label>
                                <input type="text" id="vencimiento" name="vencimiento" class="form-control" placeholder="MM/AA" value="">
                            </div>
                            <div>
                                <label for="cvv">CVV:</label>
                                <input type="text" id="cvv" name="cvv" class="form-control" value="">
                            </div>
                        </div>
                    </div>
                </fieldset>

                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/mis-reservas" class="btn btn-secondary">Cancelar</a>
                    <button type="submit" class="btn btn-primary">Confirmar y pagar</button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const checkinInput = document.getElementById('checkin');
            const checkoutInput = document.getElementById('checkout');
            const numNochesSpan = document.getElementById('numNoches');
            const totalEstimadoSpan = document.getElementById('totalEstimado');
            const precioNocheDisplay = document.getElementById('precioNocheDisplay');
            const precioPorNoche = parseFloat(precioNocheDisplay.textContent.replace('S/', '').replace(',', ''));

            function recalculateTotal() {
                const checkinDate = new Date(checkinInput.value);
                const checkoutDate = new Date(checkoutInput.value);

                if (checkinDate && checkoutDate && checkoutDate > checkinDate) {
                    const diffTime = Math.abs(checkoutDate - checkinDate);
                    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                    numNochesSpan.textContent = diffDays;
                    totalEstimadoSpan.textContent = (precioPorNoche * diffDays).toFixed(2);
                } else {
                    numNochesSpan.textContent = "0";
                    totalEstimadoSpan.textContent = "0.00";
                }
            }

            checkinInput.addEventListener('change', recalculateTotal);
            checkoutInput.addEventListener('change', recalculateTotal);
            recalculateTotal();
        });
    </script>
</body>
</html>