<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Determinamos la acción del formulario basado en el modo --%>
<c:set var="formAction" value="${modo == 'Crear' ? 'crear' : 'guardar'}" />

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>${modo} Propiedad - Hostpilot</title>
    <style>
        /* ... tu CSS no necesita cambios ... */
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }
        .form-container { max-width: 800px; margin: auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        .form-container h1 { text-align: center; color: #333; margin-bottom: 20px; }
        .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .form-group { display: flex; flex-direction: column; }
        .form-group.full-width { grid-column: 1 / -1; }
        .form-group label { margin-bottom: 5px; font-weight: bold; color: #555; }
        .form-group input, .form-group textarea, .form-group select { padding: 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 1em; }
        .form-group textarea { resize: vertical; min-height: 100px; }
        .form-buttons { grid-column: 1 / -1; display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
        .form-buttons button, .form-buttons a { padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 1em; font-weight: bold; text-decoration: none; display: inline-block; text-align: center; }
        .btn-primary { background-color: #007bff; color: white; }
        .btn-secondary { background-color: #6c757d; color: white; }
    </style>
</head>
<body>

    <div class="form-container">
        <%-- El título ahora es dinámico --%>
        <h1>${modo} propiedad<c:if test="${modo == 'Editar'}"> – ID ${propiedad.id}</c:if></h1>

        <form action="${pageContext.request.contextPath}/admin/propiedades" method="post">
            <%-- La acción del formulario y el ID oculto son dinámicos --%>
            <input type="hidden" name="action" value="${formAction}">
            <c:if test="${modo == 'Editar'}">
                <input type="hidden" name="id" value="<c:out value='${propiedad.id}'/>">
            </c:if>
            
            <div class="form-grid">
                <div class="form-group">
                    <label for="anfitrionId">ID Anfitrión *</label>
                    <input type="number" id="anfitrionId" name="anfitrionId" value="${propiedad.anfitrionId}" required>
                </div>
                <div class="form-group">
                    <label for="titulo">Título *</label>
                    <input type="text" id="titulo" name="titulo" value="<c:out value='${propiedad.titulo}'/>" required>
                </div>
                <div class="form-group full-width">
                    <label for="descripcion">Descripción</label>
                    <textarea id="descripcion" name="descripcion"><c:out value='${propiedad.descripcion}'/></textarea>
                </div>
                <div class="form-group">
                    <label for="direccion">Dirección *</label>
                    <input type="text" id="direccion" name="direccion" value="<c:out value='${propiedad.direccion}'/>" required>
                </div>
                <div class="form-group">
                    <label for="ciudad">Ciudad *</label>
                    <input type="text" id="ciudad" name="ciudad" value="<c:out value='${propiedad.ciudad}'/>" required>
                </div>
                <div class="form-group">
                    <label for="precioPorNoche">Precio por noche (S/.) *</label>
                    <input type="number" step="0.01" id="precioPorNoche" name="precioPorNoche" value="${propiedad.precioPorNoche}" required>
                </div>
                <div class="form-group">
                    <label for="capacidad">Capacidad *</label>
                    <input type="number" id="capacidad" name="capacidad" value="${propiedad.capacidad}" required>
                </div>
                <div class="form-group">
                    <label for="tipo">Tipo *</label>
                    <select id="tipo" name="tipo" required>
                        <option value="">Seleccione un tipo</option>
                        <option value="Casa" ${propiedad.tipo == 'Casa' ? 'selected' : ''}>Casa</option>
                        <option value="Departamento" ${propiedad.tipo == 'Departamento' ? 'selected' : ''}>Departamento</option>
                        <option value="Loft" ${propiedad.tipo == 'Loft' ? 'selected' : ''}>Loft</option>
                        <option value="Bungalow" ${propiedad.tipo == 'Bungalow' ? 'selected' : ''}>Bungalow</option>
                        <option value="Cabaña" ${propiedad.tipo == 'Cabaña' ? 'selected' : ''}>Cabaña</option>
                        <option value="Estudio" ${propiedad.tipo == 'Estudio' ? 'selected' : ''}>Estudio</option>
                        <option value="Ático" ${propiedad.tipo == 'Ático' ? 'selected' : ''}>Ático</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="imgUrl">URL de imagen *</label>
                    <input type="text" id="imgUrl" name="imgUrl" value="<c:out value='${propiedad.imgUrl}'/>" required>
                </div>
                <%-- Para 'Crear', estos campos no editables deben tener un valor por defecto --%>
                <div class="form-group">
                    <label for="lat">Latitud</label>
                    <input type="number" step="any" id="lat" name="lat" value="${not empty propiedad.lat ? propiedad.lat : 0.0}">
                </div>
                <div class="form-group">
                    <label for="lng">Longitud</label>
                    <input type="number" step="any" id="lng" name="lng" value="${not empty propiedad.lng ? propiedad.lng : 0.0}">
                </div>
                <div class="form-group">
                    <label for="rating">Rating</label>
                    <input type="number" step="0.1" id="rating" name="rating" value="${not empty propiedad.rating ? propiedad.rating : 0.0}">
                </div>
                <div class="form-group">
                    <label for="reviews"># Reviews</label>
                    <input type="number" id="reviews" name="reviews" value="${not empty propiedad.reviews ? propiedad.reviews : 0}">
                </div>

                <div class="form-buttons">
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn-secondary">Cancelar</a>
                    <%-- El texto del botón es dinámico --%>
                    <button type="submit" class="btn-primary">${modo == 'Crear' ? 'Crear propiedad' : 'Guardar cambios'}</button>
                </div>
            </div>
        </form>
    </div>

</body>
</html>