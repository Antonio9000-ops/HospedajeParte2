package com.hostpilot.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
@WebServlet("/propiedad")
public class PropiedadController extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(PropiedadController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        
        LOGGER.info("Recibida solicitud para ver detalles de la propiedad ID: " + id);
        
        request.setAttribute("propiedadId", id);
        
        // Aquí iría la lógica futura para buscar la propiedad por ID en la base de datos
        // y pasar el objeto Propiedad a la vista.
        
        // CORRECCIÓN: Apuntar al JSP dentro de WEB-INF/jsp/
        request.getRequestDispatcher("/WEB-INF/jsp/detallePropiedad.jsp").forward(request, response);
    }
}