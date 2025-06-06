<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Usuario - HostPilot</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilo4.css">
</head>
<body>
   
    <div class="register-container">
        <h2>Crear Nueva Cuenta en HostPilot</h2>

        <%-- Mostrar mensajes de error o éxito que vienen del controlador. --%>
        <c:if test="${not empty error}">
            <div class="message error-message">
                <p><c:out value="${error}" /></p>
            </div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="message success-message">
                <p><c:out value="${success}" /></p>
                <p><a href="${pageContext.request.contextPath}/login">Iniciar Sesión Ahora</a></p>
            </div>
        </c:if>

      
        <c:if test="${empty success}">
            <form action="${pageContext.request.contextPath}/registro" method="POST">
                
                
                <div class="form-group">
                    <label for="nombre">Nombre:</label>
                    <input type="text" id="nombre" name="nombre" value="<c:out value='${formUsuario.nombre}'/>" required>
                </div>

                <div class="form-group">
                    <label for="apellido">Apellido:</label>
                    <input type="text" id="apellido" name="apellido" value="<c:out value='${formUsuario.apellido}'/>" required>
                </div>

                <div class="form-group">
                    <label for="email">Correo Electrónico:</label>
                    <input type="email" id="email" name="email" value="<c:out value='${formUsuario.email}'/>" required>
                </div>

                <div class="form-group">
                    <label for="password">Contraseña:</label>
                    <input type="password" id="password" name="password" required>
                    <small>Debe cumplir con los requisitos de seguridad.</small>
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Confirmar Contraseña:</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                </div>

                <div class="form-group">
                    <label for="edad">Edad (Opcional):</label>
                    <input type="number" id="edad" name="edad" min="0" max="120" value="<c:out value='${formUsuario.edad}'/>">
                </div>

                <div class="form-group">
                    <label for="genero">Género (Opcional):</label>
                    <select id="genero" name="genero">
                        <option value="" ${empty formUsuario.genero ? 'selected' : ''}>Seleccionar...</option>
                        <option value="Masculino" ${formUsuario.genero == 'Masculino' ? 'selected' : ''}>Masculino</option>
                        <option value="Femenino" ${formUsuario.genero == 'Femenino' ? 'selected' : ''}>Femenino</option>
                        <option value="Otro" ${formUsuario.genero == 'Otro' ? 'selected' : ''}>Otro</option>
                        <option value="Prefiero no decirlo" ${formUsuario.genero == 'Prefiero no decirlo' ? 'selected' : ''}>Prefiero no decirlo</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="telefono">Teléfono (Opcional):</label>
                    <input type="text" id="telefono" name="telefono" value="<c:out value='${formUsuario.telefono}'/>">
                </div>
                
                <button type="submit" class="btn-submit">Registrarme</button>
            </form>
        </c:if>

        <div class="login-link">
            <p>¿Ya tienes una cuenta? <a href="${pageContext.request.contextPath}/login">Inicia Sesión</a></p>
        </div>
    </div>
</body>
</html>