<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Bienvenido - HostPilot</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilo4.css">
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; color: #333; }
        .navbar { background-color: #333; overflow: hidden; padding: 10px 20px; }
        .navbar a { float: left; display: block; color: white; text-align: center; padding: 14px 20px; text-decoration: none; }
        .navbar a:hover { background-color: #ddd; color: black; }
        .navbar a.logout { float: right; background-color: #dc3545; }
        .navbar a.logout:hover { background-color: #c82333; }
        .container { padding: 30px; max-width: 900px; margin: 20px auto; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        h1 { color: #007bff; }
        .user-info p { margin: 5px 0; }
        .user-info strong { color: #555; }
        .actions a {
            display: inline-block;
            margin: 10px 10px 10px 0;
            padding: 10px 15px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        .actions a:hover { background-color: #0056b3; }
        .actions a.admin-action { background-color: #28a745; }
        .actions a.admin-action:hover { background-color: #1e7e34; }
    </style>
</head>
<body>
    
    <c:if test="${empty sessionScope.userId}">
        <c:redirect url="/login">
            <c:param name="error" value="Por favor, inicie sesión para acceder a esta página."/>
        </c:redirect>
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
        <h1>¡Bienvenido a HostPilot!</h1>
        
        <c:if test="${not empty sessionScope.userEmail}">
            <div class="user-info">
                <p>Has iniciado sesión como: <strong><c:out value="${sessionScope.userEmail}"/></strong></p>
                <p>Tu rol es: <strong><c:out value="${sessionScope.userRole}"/></strong></p>
                <p>ID de Usuario: <strong><c:out value="${sessionScope.userId}"/></strong></p>
            </div>
        </c:if>

        <p>Desde aquí puedes gestionar tu cuenta y acceder a los servicios disponibles.</p>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/usuario?action=perfil">Ver/Editar mi Perfil</a>
         
            
            <c:if test="${sessionScope.userRole == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin?action=users" class="admin-action">Gestionar Usuarios (Admin)</a>
                <a href="${pageContext.request.contextPath}/ExportExcelServlet" class="admin-action">Exportar Usuarios (Admin)</a>
            </c:if>
        </div>

    </div>

</body>
</html>