<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel de Administración - Hostpilot</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background-color: #f4f4f4; }
        .admin-container { padding: 20px; }
        .admin-header {
            background-color: #ffc107; /* Amarillo */
            color: #333;
            padding: 10px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .admin-header h1 { font-size: 1.2em; margin: 0; }
        .admin-header .actions a {
            text-decoration: none;
            color: white;
            background-color: #007bff; /* Azul */
            padding: 8px 15px;
            border-radius: 4px;
            margin-left: 10px;
            font-size: 0.9em;
        }
        .admin-table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .admin-table th, .admin-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        .admin-table thead th {
            background-color: #001f54; /* Azul oscuro */
            color: white;
            font-weight: bold;
        }
        .admin-table tbody tr:hover { background-color: #f1f1f1; }
        .admin-table .actions-links a {
            margin-right: 10px;
            color: #007bff;
            text-decoration: none;
        }
        .admin-table .actions-links a:hover { text-decoration: underline; }
    </style>
</head>
<body>

    <div class="admin-container">
        <div class="admin-header">
            <h1>Panel de Administración - Propiedades</h1>
            <div class="actions">
                <a href="${pageContext.request.contextPath}/">← Volver a vista USER</a>
                <a href="${pageContext.request.contextPath}/admin/propiedades?action=nuevo">+ Nueva propiedad</a>
            </div>
        </div>

        <table class="admin-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Título</th>
                    <th>Ciudad</th>
                    <th>Precio (S/.)</th>
                    <th>Rating</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="prop" items="${listaPropiedades}">
                    <tr>
                        <td><c:out value="${prop.id}"/></td>
                        <td><c:out value="${prop.titulo}"/></td>
                        <td><c:out value="${prop.ciudad}"/></td>
                        <td>S/ <fmt:formatNumber value="${prop.precioPorNoche}" type="number" minFractionDigits="1" maxFractionDigits="2"/></td>
                        <td><c:out value="${prop.rating}"/></td>
                        <td class="actions-links">
                            <a href="${pageContext.request.contextPath}/admin/propiedades?action=editar&id=${prop.id}">Editar</a>
                            <a href="${pageContext.request.contextPath}/admin/propiedades?action=eliminar&id=${prop.id}" onclick="return confirm('¿Estás seguro de que deseas eliminar esta propiedad?');">Eliminar</a>
                        </td>
                    </tr>
                </c:forEach>
                 <c:if test="${empty listaPropiedades}">
                    <tr>
                        <td colspan="6" style="text-align: center;">No se encontraron propiedades.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

</body>
</html>