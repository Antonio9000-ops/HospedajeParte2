<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Mis Reservas - Hostpilot</title>
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

        .main-content { max-width: 900px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .reservas-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 30px; margin-top: 30px; }
        .reserva-card { background-color: #fff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow: hidden; display: flex; flex-direction: column; }
        .reserva-card-image { width: 100%; height: 200px; overflow: hidden; }
        .reserva-card-image img { width: 100%; height: 100%; object-fit: cover; }
        .reserva-card-details { padding: 20px; display: flex; flex-direction: column; flex-grow: 1; }
        .reserva-card-details h3 { margin-top: 0; margin-bottom: 10px; color: #333; font-size: 1.3em; }
        .reserva-card-details p { margin-bottom: 8px; color: #555; font-size: 0.9em; line-height: 1.4; }
        .reserva-card-details strong { color: #333; }
        .reserva-estado { font-weight: bold; padding: 4px 8px; border-radius: 4px; display: inline-block; font-size: 0.8em; }
        .reserva-estado.confirmada { background-color: #e6ffed; color: #28a745; }
        .reserva-estado.pendiente { background-color: #fff3cd; color: #ffc107; }
        .reserva-estado.cancelada { background-color: #fbecec; color: #dc3545; }
        .reserva-estado.pagado { background-color: #d1ecf1; color: #0c5460; }
        .reserva-actions { margin-top: 15px; display: flex; justify-content: flex-end; flex-wrap: wrap; gap: 10px;}
        .reserva-actions .btn { margin-left: 0; }

        .btn { display: inline-block; padding: 0.5rem 1rem; border-radius: 0.25rem; font-weight: 400; text-align: center; vertical-align: middle; cursor: pointer; border: 1px solid transparent; transition: all 0.15s ease-in-out; }
        .btn-primary { color: #fff; background-color: #007bff; border-color: #007bff; }
        .btn-primary:hover { background-color: #0069d9; border-color: #0062cc; }
        .btn-secondary { color: #fff; background-color: #6c757d; border-color: #6c757d; }
        .btn-secondary:hover { background-color: #5a6268; border-color: #545b62; }
        .btn-danger { color: #fff; background-color: #dc3545; border-color: #dc3545; }
        .btn-danger:hover { background-color: #c82333; border-color: #bd2130; }
        .btn-info { color: #fff; background-color: #17a2b8; border-color: #17a2b8; }
        .btn-info:hover { background-color: #138496; border-color: #117a8b; }

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
                <a href="${pageContext.request.contextPath}/mis-reservas" class="active">Mis Reservas</a>
                <a href="${pageContext.request.contextPath}/historial-pagos">Historial de Pagos</a>
                <a href="#">Zonas</a>
                <a href="${pageContext.request.contextPath}/anfitrion">Anfitrión</a>
            </nav>
        </header>

        <div class="main-content">
            <h1>Mis Reservas</h1>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">${successMessage}</div>
            </c:if>
            <c:if test="${param.status eq 'modificado'}">
                <div class="alert alert-success">¡Reserva modificada exitosamente!</div>
            </c:if>
            <c:if test="${param.error eq 'reserva_no_encontrada' || param.error eq 'id_invalido'}">
                <div class="alert alert-danger">No se pudo encontrar la reserva o no tienes permiso para verla.</div>
            </c:if>

            <c:choose>
                <c:when test="${empty misReservas}">
                    <p>No tienes reservas activas en este momento.</p>
                    <p><a href="${pageContext.request.contextPath}/reservas">Explora alojamientos para reservar</a></p>
                </c:when>
                <c:otherwise>
                    <div class="reservas-grid">
                        <c:forEach items="${misReservas}" var="reserva">
                            <c:set var="propiedadAsociada" value="${propiedadesReservadas[reserva.idPropiedad]}"/>
                            <div class="reserva-card">
                                <div class="reserva-card-image">
                                    <c:if test="${not empty propiedadAsociada}">
                                        <img src="${pageContext.request.contextPath}/${propiedadAsociada.imgUrl}" alt="${propiedadAsociada.titulo}">
                                    </c:if>
                                </div>
                                <div class="reserva-card-details">
                                    <h3>
                                        <c:choose>
                                            <c:when test="${not empty propiedadAsociada}">${propiedadAsociada.titulo}</c:when>
                                            <c:otherwise>Propiedad Desconocida (ID: ${reserva.idPropiedad})</c:otherwise>
                                        </c:choose>
                                    </h3>
                                    <p><strong>Ubicación:</strong>
                                        <c:choose>
                                            <c:when test="${not empty propiedadAsociada}">${propiedadAsociada.ciudad}, Perú</c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </p>
                                    <p><strong>Check-in:</strong> <fmt:formatDate value="${reserva.fechaCheckinAsUtilDate}" pattern="dd/MM/yyyy"/></p>
                                    <p><strong>Check-out:</strong> <fmt:formatDate value="${reserva.fechaCheckoutAsUtilDate}" pattern="dd/MM/yyyy"/></p>
                                    <p><strong>Huéspedes:</strong> ${reserva.numeroAdultos} Adultos <c:if test="${reserva.numeroNinos > 0}">, ${reserva.numeroNinos} Niños</c:if><c:if test="${reserva.numeroBebes > 0}">, ${reserva.numeroBebes} Bebés</c:if><c:if test="${reserva.numeroMascotas > 0}">, ${reserva.numeroMascotas} Mascotas</c:if></p>
                                    <p><strong>Estado:</strong> <span class="reserva-estado ${reserva.estado.toLowerCase()}">${reserva.estado}</span></p>
                                    <p><strong>Total:</strong> S/<fmt:formatNumber value="${reserva.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/></p>

                                    <div class="reserva-actions">
                                        <c:if test="${reserva.estado eq 'PENDIENTE'}">
                                            <a href="${pageContext.request.contextPath}/modificar-reserva?id=${reserva.id}" class="btn btn-primary">Modificar</a>
                                            <a href="${pageContext.request.contextPath}/mis-reservas?action=cancelar&id=${reserva.id}" class="btn btn-danger" onclick="return confirm('¿Estás seguro de que quieres cancelar esta reserva?');">Cancelar</a>
                                            <a href="${pageContext.request.contextPath}/download-pdf?type=reserva&id=${reserva.id}" class="btn btn-info">Descargar PDF</a>
                                        </c:if>
                                        <c:if test="${reserva.estado eq 'PAGADO'}">
                                            <span class="btn btn-secondary" style="cursor: default;">Pagado</span>
                                            <a href="${pageContext.request.contextPath}/mis-reservas?action=cancelar&id=${reserva.id}" class="btn btn-danger" onclick="return confirm('¿Estás seguro de que quieres cancelar esta reserva? (Puede aplicar cargos)');">Cancelar</a>
                                            <a href="${pageContext.request.contextPath}/download-pdf?type=reserva&id=${reserva.id}" class="btn btn-info">Descargar PDF</a>
                                        </c:if>
                                        <c:if test="${reserva.estado eq 'CANCELADA'}">
                                            <span class="btn btn-secondary" style="cursor: default;">Cancelada</span>
                                            <a href="${pageContext.request.contextPath}/download-pdf?type=reserva&id=${reserva.id}" class="btn btn-info">Descargar PDF</a>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>