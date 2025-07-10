package com.hostpilot.controller;

import com.hostpilot.dao.PropiedadDAO;
import com.hostpilot.dao.PropiedadDAOImpl; // Asumo que tienes esta clase
import com.hostpilot.model.Propiedad; // Asumo que tienes este modelo
import com.hostpilot.config.MySQLDatabaseConfig; // O tu clase de configuración
import com.hostpilot.dao.DAOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/dashboard")
public class AdminDashboardController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminDashboardController.class.getName());
    private PropiedadDAO propiedadDAO;

    @Override
    public void init() throws ServletException {
        // Inicializa el DAO. Asegúrate de que tu implementación no sea null.
        this.propiedadDAO = new PropiedadDAOImpl(new MySQLDatabaseConfig());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar si el usuario es ADMIN (esto ya lo debería hacer el SecurityFilter)
        // Pero una doble verificación nunca está de más.
        String userRole = (String) request.getSession().getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado.");
            return;
        }

        try {
            // 1. Obtener todas las propiedades desde el DAO
            List<Propiedad> listaPropiedades = propiedadDAO.obtenerTodas();

            // 2. Guardar la lista en el request para que la JSP pueda acceder a ella
            request.setAttribute("listaPropiedades", listaPropiedades);

            // 3. Redirigir la petición a la página JSP del dashboard
            request.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(request, response);

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener las propiedades para el panel de admin", e);
            // Puedes redirigir a una página de error más amigable
            request.setAttribute("error", "No se pudieron cargar los datos de las propiedades.");
            request.getRequestDispatcher("/WEB-INF/jsp/error/error.jsp").forward(request, response);
        }
    }
}