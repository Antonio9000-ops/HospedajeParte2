package com.hostpilot.controller;

import com.hostpilot.model.Propiedad;
import com.hostpilot.service.PropiedadService;
import com.hostpilot.dao.PropiedadServiceImpl;
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
import java.util.Collections;
import java.util.List;
import java.util.Random;

@WebServlet("")
public class IndexController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
    private PropiedadService propiedadService;

    @Override
    public void init() {
        this.propiedadService = new PropiedadServiceImpl();
        LOGGER.info("IndexController inicializado.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        LOGGER.info("Preparando datos para la página de inicio.");
        
        try {
            // Obtener los datos REALES de la base de datos a través del servicio
            List<Propiedad> todasLasPropiedades = propiedadService.obtenerTodasLasPropiedades();

            if (!todasLasPropiedades.isEmpty()) {
                // Seleccionar una propiedad principal al azar
                Propiedad propiedadPrincipal = todasLasPropiedades.get(new Random().nextInt(todasLasPropiedades.size()));
                request.setAttribute("propiedadPrincipal", propiedadPrincipal);

                // Dividir para los dos carruseles
                List<Propiedad> carrusel1 = new ArrayList<>(todasLasPropiedades);
                Collections.shuffle(carrusel1); // Barajar para variedad
                request.setAttribute("listaCarrusel1", carrusel1.subList(0, Math.min(4, carrusel1.size())));

                List<Propiedad> carrusel2 = new ArrayList<>(todasLasPropiedades);
                Collections.shuffle(carrusel2);
                request.setAttribute("listaCarrusel2", carrusel2.subList(0, Math.min(4, carrusel2.size())));
            } else {
                LOGGER.warn("No se encontraron propiedades en la base de datos para mostrar en la página de inicio.");
                // Establecer listas vacías para evitar errores en el JSP
                request.setAttribute("propiedadPrincipal", null);
                request.setAttribute("listaCarrusel1", new ArrayList<>());
                request.setAttribute("listaCarrusel2", new ArrayList<>());
            }

        } catch (ServiceException e) {
            LOGGER.error("Error al obtener datos para la página de inicio.", e);
            request.setAttribute("error", "No se pudieron cargar los datos de las propiedades.");
        }
        
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}