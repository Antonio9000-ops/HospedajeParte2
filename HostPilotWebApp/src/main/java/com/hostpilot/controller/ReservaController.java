package com.hostpilot.controller;

import com.hostpilot.model.Propiedad;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class ReservaController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ReservaController.class.getName());

    /**
     * Maneja las solicitudes GET para mostrar la página de reservas.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        LOGGER.info("ReservaController (GET /reservas): Mostrando página de reservas.");
        
        String contextPath = request.getContextPath();
        List<Propiedad> propiedades = new ArrayList<>();

        propiedades.add(new Propiedad(1, "Loft moderno en Miraflores", -12.1215, -77.0307, "Lima", 240, 4.9, 25, contextPath + "/img/1.jpg"));
        propiedades.add(new Propiedad(2, "Depto con vista al mar", -4.1089, -81.1528, "Mancora", 320, 4.7, 5, contextPath + "/img/2.jpg"));
        propiedades.add(new Propiedad(3, "Ático panorámico en el Centro", -13.517, -71.978, "Cusco", 450, 4.8, 15, contextPath + "/img/3.jpg"));
        propiedades.add(new Propiedad(4, "Casa rústica y acogedora", -16.4090, -71.5375, "Arequipa", 280, 5.0, 30, contextPath + "/img/4.jpg"));
        propiedades.add(new Propiedad(5, "Bungalow ecológico en la selva", -6.4776, -76.3730, "Tarapoto", 310, 4.6, 18, contextPath + "/img/5.jpg"));
        propiedades.add(new Propiedad(6, "Cabaña de madera junto al lago", -15.8402, -70.0219, "Puno", 350, 4.7, 22, contextPath + "/img/6.jpg"));
        propiedades.add(new Propiedad(7, "Estudio moderno y céntrico", -8.1120, -79.0280, "Trujillo", 190, 4.5, 12, contextPath + "/img/7.jpg"));
        propiedades.add(new Propiedad(8, "Casa colonial restaurada", -7.1619, -78.5100, "Cajamarca", 300, 4.8, 19, contextPath + "/img/8.jpg"));
        
        request.setAttribute("listaPropiedades", propiedades);
        request.getRequestDispatcher("/WEB-INF/jsp/reservas.jsp").forward(request, response);
    }
    
    
}