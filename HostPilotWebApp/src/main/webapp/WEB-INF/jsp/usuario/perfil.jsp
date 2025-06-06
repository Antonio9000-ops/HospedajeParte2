<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Perfil - HostPilot</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilo4.css">
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; color: #333; }
        .navbar { background-color: #333; overflow: hidden; padding: 10px 20px; }
        .navbar a { float: left; display: block; color: white; text-align: center; padding: 14px 20px; text-decoration: none; }
        .navbar a:hover { background-color: #ddd; color: black; }
        .navbar a.logout { float: right; background-color: #dc3545; }
        .navbar a.logout:hover { background-color: #c82333; }
        .container { padding: 30px; max-width: 700px; margin: 20px auto; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        h1 { color: #007bff; border-bottom: 2px solid #eee; padding-bottom: 10px; }
        .profile-details { margin-top: 20px; }
        .profile-details p {
            font-size: 1.1em;
            line-height: 1.8;
            padding: 8px 0;
            border-bottom: 1px dashed #eee;
        }
        .profile-details p:last-child { border-bottom: none; }
        .profile-details strong {
            display: inline-block;
            width: 180px; /* Ajusta según necesidad */
            color: #555;
        }
        .btn-edit {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        .btn-edit:hover { background-color: #0056b3; }
    </style>
</head>
<body>
    <c:if test="${empty sessionScope.userId}">
        <c:redirect url="${pageContext.request.contextPath}/login?error=sesionExpirada"/>
    </c:if>

    <div class="navbar">
        <a href="${pageContext.request.contextPath}/bienvenido.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/usuario?action=perfil">Mi Perfil</a>
        <c:if test="${sessionScope.userRole == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/admin?action=dashboard">Panel Admin</a>
        </c:if>
        <a href="${pageContext.request.contextPath}/logout" class="logout">Cerrar Sesión</a>
    </div>

    <div class="container">
        <h1>Mi Perfil</h1>
        <c:if test="${not empty requestScope.usuarioPerfil}">
            <div class="profile-details">
                <p><strong>ID de Usuario:</strong> <c:out value="${usuarioPerfil.id}"/></p>
                <p><strong>Nombre Completo:</strong> <c:out value="${usuarioPerfil.nombre} ${usuarioPerfil.apellido}"/></p>
                <p><strong>Correo Electrónico:</strong> <c:out value="${usuarioPerfil.email}"/></p>
                <p><strong>Rol:</strong> <c:out value="${usuarioPerfil.rol}"/></p>
                <p><strong>Edad:</strong> <c:out value="${not empty usuarioPerfil.edad ? usuarioPerfil.edad : 'No especificada'}"/></p>
                <p><strong>Género:</strong> <c:out value="${not empty usuarioPerfil.genero ? usuarioPerfil.genero : 'No especificado'}"/></p>
                <p><strong>Teléfono:</strong> <c:out value="${not empty usuarioPerfil.telefono ? usuarioPerfil.telefono : 'No especificado'}"/></p>
                <p><strong>Usuario Activo:</strong> <c:out value="${usuarioPerfil.activo ? 'Sí' : 'No'}"/></p>
                <p><strong>Fecha de Creación:</strong> 
                    <fmt:formatDate value="${usuarioPerfil.fechaCreacion}" pattern="dd/MM/yyyy HH:mm:ss" type="both" timeZone="UTC"/>
                    (UTC)
                </p>
                <p><strong>Última Modificación:</strong> 
                    <c:if test="${not empty usuarioPerfil.fechaModificacion}">
                        <fmt:formatDate value="${usuarioPerfil.fechaModificacion}" pattern="dd/MM/yyyy HH:mm:ss" type="both" timeZone="UTC"/>
                        (UTC)
                    </c:if>
                    <c:if test="${empty usuarioPerfil.fechaModificacion}">
                        N/A
                    </c:if>
                </p>
                <p><strong>Último Acceso:</strong> 
                    <c:if test="${not empty usuarioPerfil.ultimoAcceso}">
                        <fmt:formatDate value="${usuarioPerfil.ultimoAcceso}" pattern="dd/MM/yyyy HH:mm:ss" type="both" timeZone="UTC"/>
                        (UTC)
                    </c:if>
                    <c:if test="${empty usuarioPerfil.ultimoAcceso}">
                        N/A
                    </c:if>
                </p>
            </div>
            <%-- <a href="${pageContext.request.contextPath}/usuario?action=editarperfil" class="btn-edit">Editar Perfil</a> --%>
        </c:if>
        <c:if test="${empty requestScope.usuarioPerfil}">
            <p class="error-message">No se pudo cargar la información del perfil.</p>
        </c:if>
    </div>
</body>
</html>