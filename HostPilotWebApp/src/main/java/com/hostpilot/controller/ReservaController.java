package com.hostpilot.controller;

import com.hostpilot.model.Propiedad;
import com.hostpilot.service.PropiedadService;
import com.hostpilot.service.PropiedadServiceImpl;
import com.hostpilot.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@WebServlet("/reservas")
public class ReservaController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservaController.class);
    private PropiedadService propiedadService;

    @Override
    public void init() {
        this.propiedadService = new PropiedadServiceImpl();
        LOGGER.info("ReservaController inicializado.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        LOGGER.info("Mostrando página de reservas.");
        
        try {
            // Obtener la lista de propiedades desde la base de datos
            List<Propiedad> propiedades = propiedadService.obtenerTodasLasPropiedades();
            request.setAttribute("listaPropiedades", propiedades);
            LOGGER.info("Se encontraron {} propiedades para mostrar.", propiedades.size());

        } catch (ServiceException e) {
            LOGGER.error("Error al obtener la lista de propiedades para la página de reservas.", e);
            request.setAttribute("error", "No se pudieron cargar los alojamientos disponibles.");
            // Enviar una lista vacía para que el JSP no falle
            request.setAttribute("listaPropiedades", new ArrayList<>());
        }
        
        request.getRequestDispatcher("/WEB-INF/jsp/reservas.jsp").forward(request, response);
    }
}