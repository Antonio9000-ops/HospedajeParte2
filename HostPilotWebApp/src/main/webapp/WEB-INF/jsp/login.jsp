<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - HostPilot</title>
    <%-- Referencia a tu CSS. Asegúrate que esté en webapp/estilo4.css --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilo4.css">
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; padding: 20px; box-sizing: border-box;}
        .login-container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.1); width: 100%; max-width: 400px; text-align: center; }
        .login-container h2 { margin-bottom: 20px; color: #333; }
        .form-group { margin-bottom: 15px; text-align: left; }
        .form-group label { display: block; margin-bottom: 5px; color: #555; font-weight: bold; }
        .form-group input[type="email"], .form-group input[type="password"] { width: calc(100% - 22px); padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        .btn-submit { background-color: #007bff; color: white; padding: 12px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; width: 100%; transition: background-color 0.3s ease; }
        .btn-submit:hover { background-color: #0056b3; }
        .message { padding: 10px; border-radius: 4px; margin-bottom: 20px; text-align: center; }
        .error-message { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .success-message { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; } /* Para mensajes de logout o registro exitoso */
        .register-link { margin-top: 20px; font-size: 0.9em; }
        .register-link a { color: #007bff; text-decoration: none; }
        .register-link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="login-container">
        <h2>Iniciar Sesión en HostPilot</h2>

        <%-- Mensaje de error general del servlet (atributo 'error') --%>
        <c:if test="${not empty error}">
            <div class="message error-message">
                <c:out value="${error}" />
            </div>
        </c:if>
        
        <%-- Mensaje de error/info pasado como parámetro en la URL (param.error o param.message) --%>
        <c:if test="${not empty param.error}">
            <div class="message error-message">
                <c:out value="${param.error}" />
            </div>
        </c:if>
        <c:if test="${not empty param.message}"> <%-- Para mensajes como "logoutExitoso" --%>
            <div class="message success-message">
                <c:out value="${param.message}" />
            </div>
        </c:if>
         <c:if test="${not empty param.successMessage}"> <%-- Para mensajes como registro exitoso --%>
            <div class="message success-message">
                <c:out value="${param.successMessage}" />
            </div>
        </c:if>


        <form action="${pageContext.request.contextPath}/login" method="POST">
            <div class="form-group">
                <label for="email">Correo Electrónico:</label>
                <%-- Usar el atributo 'formEmail' si existe (para repoblar tras error), sino 'param.email' si es el primer GET con params --%>
                <input type="email" id="email" name="email" value="<c:out value='${not empty formEmail ? formEmail : param.email}'/>" required>
            </div>
            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn-submit">Ingresar</button>
        </form>
        <div class="register-link">
            ¿No tienes una cuenta? <a href="${pageContext.request.contextPath}/registro">Regístrate aquí</a>
        </div>
    </div>
</body>
</html>