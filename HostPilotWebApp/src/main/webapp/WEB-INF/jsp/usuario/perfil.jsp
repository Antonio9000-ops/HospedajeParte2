<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil - Hostpilot</title>
    <%-- Estilos CSS integrados para replicar el diseño de la captura --%>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #001f54; /* Fondo azul oscuro */
            color: #333;
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }
        .header {
            background-color: rgba(0, 0, 0, 0.2); /* Fondo de la barra de navegación semi-transparente */
            padding: 15px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid rgba(255, 255, 255, 0.2);
        }
        .nav-links a {
            color: white;
            text-decoration: none;
            margin: 0 15px;
            font-weight: bold;
            font-size: 16px;
            transition: color 0.3s;
        }
        .nav-links a:hover {
            color: #ffc107; /* Color amarillo al pasar el ratón */
        }
        .btn-logout {
            background-color: #dc3545;
            color: white;
            padding: 10px 18px;
            border-radius: 5px;
            text-decoration: none;
            font-weight: bold;
            transition: background-color 0.3s;
        }
        .btn-logout:hover {
            background-color: #c82333;
        }
        .profile-container {
            display: flex;
            justify-content: center;
            align-items: center;
            flex-grow: 1; /* Ocupa el espacio restante */
            padding: 20px;
        }
        .profile-card {
            background-color: white;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            width: 100%;
            max-width: 450px;
            animation: fadeIn 0.5s ease-in-out;
        }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .profile-card h1 {
            text-align: center;
            margin-top: 0;
            margin-bottom: 35px;
            color: #001f54;
            font-size: 28px;
        }
        .profile-info {
            display: grid;
            grid-template-columns: 150px 1fr; /* Columna de etiquetas más ancha */
            gap: 12px 20px;
            align-items: center;
        }
        .profile-info label {
            font-weight: bold;
            color: #6c757d; /* Gris para las etiquetas */
            text-align: right;
        }
        .profile-info span {
            background-color: #f8f9fa;
            padding: 10px;
            border-radius: 5px;
            border: 1px solid #dee2e6;
            font-size: 15px;
            word-wrap: break-word; /* Para correos largos */
        }
        .error-message {
            color: #dc3545;
            text-align: center;
            font-size: 1.2em;
        }
    </style>
</head>
<body>
    
    <%-- Redirección si la sesión no es válida. El SecurityFilter ya debería hacer esto,
         pero es una buena doble comprobación. --%>
    <c:if test="${empty sessionScope.userId}">
        <c:redirect url="${pageContext.request.contextPath}/login?error=sesionExpirada"/>
    </c:if>

    <header class="header">
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/">Inicio</a>
            <a href="${pageContext.request.contextPath}/usuario?action=perfil">Mi Perfil</a>
            
            <%-- Muestra "Panel Admin" solo si el usuario tiene el rol 'ADMIN' --%>
            <c:if test="${sessionScope.userRole == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Panel Admin</a>
            </c:if>
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Cerrar Sesión</a>
    </header>

    <main class="profile-container">
        
        <%-- Usamos el objeto "usuario" que nos envía el UsuarioController --%>
        <c:if test="${not empty requestScope.usuario}">
            <div class="profile-card">
                <h1>Mi Perfil</h1>
                <div class="profile-info">
                    <label>ID de Usuario:</label>
                    <span><c:out value="${usuario.id}"/></span>

                    <label>Nombre Completo:</label>
                    <span><c:out value="${usuario.nombre} ${usuario.apellido}"/></span>

                    <label>Correo Electrónico:</label>
                    <span><c:out value="${usuario.email}"/></span>

                    <label>Rol:</label>
                    <span><c:out value="${usuario.rol}"/></span>

                    <label>Edad:</label>
                    <span><c:out value="${not empty usuario.edad ? usuario.edad : 'No especificado'}"/></span>

                    <label>Género:</label>
                    <span><c:out value="${not empty usuario.genero ? usuario.genero : 'No especificado'}"/></span>

                    <label>Teléfono:</label>
                    <span><c:out value="${not empty usuario.telefono ? usuario.telefono : 'No especificado'}"/></span>
                </div>
            </div>
        </c:if>

        <%-- Mensaje de error si el objeto usuario no se pudo cargar --%>
        <c:if test="${empty requestScope.usuario}">
            <div class="profile-card">
                 <p class="error-message">No se pudo cargar la información del perfil. Por favor, intente de nuevo.</p>
            </div>
        </c:if>

    </main>

</body>
</html>