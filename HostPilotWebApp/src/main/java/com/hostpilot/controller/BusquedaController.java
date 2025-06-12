package com.hostpilot.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class BusquedaController extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(BusquedaController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String query = request.getParameter("q");
        
        LOGGER.info("Recibida solicitud de búsqueda para el término: " + query);
        
        request.setAttribute("terminoBusqueda", query);
        
        // Aquí iría la lógica futura para buscar en la base de datos
        // y pasar una lista de resultados a la vista.
        
        // CORRECCIÓN: Apuntar al JSP dentro de WEB-INF/jsp/
        request.getRequestDispatcher("/WEB-INF/jsp/resultadosBusqueda.jsp").forward(request, response);
    }
}