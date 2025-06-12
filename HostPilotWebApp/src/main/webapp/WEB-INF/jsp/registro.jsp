<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Usuario - HostPilot</title>

    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Inter', sans-serif; }
        body { background-color: #f4f4f7; color: #333; display: flex; justify-content: center; align-items: center; min-height: 100vh; padding: 40px 20px; }
        a { text-decoration: none; color: #007bff; }
        a:hover { text-decoration: underline; }
        
        .register-container { 
            background-color: #ffffff; 
            padding: 40px; 
            border-radius: 12px; 
            box-shadow: 0 10px 30px rgba(0,0,0,0.1); 
            width: 100%; 
            max-width: 500px; 
            text-align: center; 
        }
        .register-container h2 { 
            margin-bottom: 10px; 
            color: #001f54; 
            font-weight: 700;
        }
        .register-container .sub-heading {
            margin-bottom: 30px;
            color: #666;
        }

        .form-group { 
            margin-bottom: 15px; 
            text-align: left; 
        }
        .form-group label { 
            display: block; 
            margin-bottom: 8px; 
            color: #555; 
            font-weight: 600;
        }
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 1rem;
            transition: border-color 0.3s;
        }
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #007bff;
        }
        .form-group small {
            font-size: 0.8rem;
            color: #777;
            margin-top: 5px;
            display: block;
        }

        .btn-submit { 
            background-color: #28a745; 
            color: white; 
            padding: 12px 20px; 
            border: none; 
            border-radius: 8px; 
            cursor: pointer; 
            font-size: 1.1rem; 
            font-weight: 600;
            width: 100%; 
            transition: background-color 0.3s;
            margin-top: 10px;
        }
        .btn-submit:hover { background-color: #218838; }

        .message { 
            padding: 15px; 
            border-radius: 8px; 
            margin-bottom: 20px; 
            text-align: center;
            font-weight: 500;
        }
        .error-message { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .success-message { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .success-message a { font-weight: bold; }

        .login-link { 
            margin-top: 25px; 
            font-size: 0.95rem; 
        }
    </style>
    
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