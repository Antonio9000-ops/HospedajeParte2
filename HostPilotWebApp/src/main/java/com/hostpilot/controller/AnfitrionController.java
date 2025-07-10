package com.hostpilot.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
@WebServlet("/anfitrion")
public class AnfitrionController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AnfitrionController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        LOGGER.info("AnfitrionController: Mostrando la página de anfitriones.");

        // En un futuro, aquí obtendrías la lista de reseñas/anfitriones desde la base de datos
        // y la pasarías al JSP.

        // Hacemos forward al JSP que está en una carpeta protegida.
        request.getRequestDispatcher("/WEB-INF/jsp/anfitrion.jsp").forward(request, response);
    }
}