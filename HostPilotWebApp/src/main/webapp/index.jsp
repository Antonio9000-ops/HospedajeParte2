<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:choose>
    <c:when test="${not empty sessionScope.userId}">
        <% response.sendRedirect(request.getContextPath() + "/bienvenido.jsp"); %>
    </c:when>
    <c:otherwise>
        
        <% response.sendRedirect(request.getContextPath() + "/login"); %>
    </c:otherwise>
</c:choose>