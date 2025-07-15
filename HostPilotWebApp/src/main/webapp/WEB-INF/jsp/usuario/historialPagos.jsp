<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Historial de Pagos - Hostpilot</title>
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
        
        /* Estilos específicos para Historial de Pagos */
        .main-content { max-width: 900px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .pagos-table { width: 100%; border-collapse: collapse; margin-top: 30px; }
        .pagos-table th, .pagos-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; }
        .pagos-table th { background-color: #f8f8f8; color: #333; font-weight: 600; }
        .pagos-table tr:hover { background-color: #f0f0f0; }
        .pago-propiedad-img { width: 80px; height: 60px; object-fit: cover; border-radius: 4px; margin-right: 10px; vertical-align: middle; }
        
        /* General utility classes */
        .btn { display: inline-block; padding: 0.5rem 1rem; border-radius: 0.25rem; font-weight: 400; text-align: center; vertical-align: middle; cursor: pointer; border: 1px solid transparent; transition: all 0.15s ease-in-out; }
        .btn-primary { color: #fff; background-color: #007bff; border-color: #007bff; }
        .btn-primary:hover { background-color: #0069d9; border-color: #0062cc; }
        .btn-info { color: #fff; background-color: #17a2b8; border-color: #17a2b8; }
        .btn-info:hover { background-color: #138496; border-color: #117a8b; }


        @media (max-width: 768px) {
            .top-bar { flex-direction: column; gap: 10px; }
            .search-container { width: 100%; }
            .user-menu { width: 100%; justify-content: center; margin-left: 0; }
            nav { border-radius: 0; }
            .pagos-table th, .pagos-table td { padding: 8px 10px; font-size: 0.9em; }
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
                <a href="${pageContext.request.contextPath}/historial-pagos" class="active">Historial de Pagos</a>
                <a href="#">Zonas</a>
                <a href="${pageContext.request.contextPath}/anfitrion">Anfitrión</a>
            </nav>
        </header>

        <div class="main-content">
            <h1>Historial de Pagos</h1>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <c:choose>
                <c:when test="${empty historialPagos}">
                    <p>No tienes pagos registrados en este momento.</p>
                    <p><a href="${pageContext.request.contextPath}/mis-reservas" class="btn btn-primary">Ver mis reservas</a></p>
                </c:when>
                <c:otherwise>
                    <div style="text-align: right; margin-bottom: 20px;">
                        <a href="${pageContext.request.contextPath}/download-pdf?type=historial" class="btn btn-info">Descargar Historial en PDF</a>
                    </div>
                    <table class="pagos-table">
                        <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Propiedad</th>
                                <th>Reserva ID</th>
                                <th>Método</th>
                                <th>Monto</th>
                                <th>Transacción ID</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${historialPagos}" var="pago">
                                <c:set var="reservaAsociada" value="${reservasAsociadas[pago.reservaId]}"/>
                                <c:set var="propiedadAsociada" value="${not empty reservaAsociada ? propiedadesAsociadas[reservaAsociada.idPropiedad] : null}"/>
                                <tr>
                                    <td><fmt:formatDate value="${pago.fechaPagoAsUtilDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty propiedadAsociada}">
                                                <img src="${pageContext.request.contextPath}/${propiedadAsociada.imgUrl}" alt="${propiedadAsociada.titulo}" class="pago-propiedad-img">
                                                ${propiedadAsociada.titulo}
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${pago.reservaId}</td>
                                    <td>${pago.metodo}</td>
                                    <td>S/<fmt:formatNumber value="${pago.monto}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
                                    <td>${pago.transaccionId}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>