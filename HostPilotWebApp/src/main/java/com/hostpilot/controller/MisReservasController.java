package com.hostpilot.controller;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.PropiedadDAO;
import com.hostpilot.dao.PropiedadDAOImpl;
import com.hostpilot.dao.ReservaDAO;
import com.hostpilot.dao.ReservaDAOImpl;
import com.hostpilot.model.Reserva;
import com.hostpilot.model.Propiedad;
import com.hostpilot.security.SessionManager;
import com.hostpilot.service.ReservaService;
import com.hostpilot.service.ReservaServiceImpl;
import com.hostpilot.service.ServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/mis-reservas")
public class MisReservasController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MisReservasController.class.getName());
    private ReservaService reservaService;
    private PropiedadDAO propiedadDAO;

    @Override
    public void init() throws ServletException {
        LOGGER.info("########## INICIALIZANDO SERVLET: MisReservasController ##########");
        try {
            DatabaseConfig dbConfig = new MySQLDatabaseConfig();
            ReservaDAO reservaDAO = new ReservaDAOImpl(dbConfig);
            propiedadDAO = new PropiedadDAOImpl(dbConfig);
            this.reservaService = new ReservaServiceImpl(reservaDAO, propiedadDAO);
            LOGGER.info("MisReservasController inicializado exitosamente.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fallo crítico al inicializar MisReservasController", e);
            throw new ServletException("No se pudo inicializar el servicio de Mis Reservas", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long idUsuarioLong = SessionManager.getCurrentUserId(request);
        if (idUsuarioLong == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String action = request.getParameter("action");
            if ("cancelar".equals(action)) {
                int reservaId = Integer.parseInt(request.getParameter("id"));
                reservaService.cancelarReserva(reservaId, idUsuarioLong.intValue());
                request.setAttribute("successMessage", "Reserva cancelada exitosamente.");
            }

            List<Reserva> misReservas = reservaService.buscarReservasPorUsuario(idUsuarioLong.intValue());
            
            Map<Integer, Propiedad> propiedadesReservadas = new HashMap<>();
            for(Reserva r : misReservas) {
                if (!propiedadesReservadas.containsKey(r.getIdPropiedad())) {
                    Propiedad p = null;
                    try {
                        p = propiedadDAO.buscarPorId((long)r.getIdPropiedad()).orElse(null);
                    } catch (DAOException e) {
                        LOGGER.log(Level.SEVERE, "Error DAO al buscar propiedad con ID " + r.getIdPropiedad() + " para reserva " + r.getId(), e);
                        throw new ServiceException("Error al cargar detalles de una propiedad.", e);
                    }
                    if (p != null) {
                        propiedadesReservadas.put(r.getIdPropiedad(), p);
                    }
                }
            }

            request.setAttribute("misReservas", misReservas);
            request.setAttribute("propiedadesReservadas", propiedadesReservadas);

            request.getRequestDispatcher("/WEB-INF/jsp/usuario/misReservas.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID de reserva inválido para la acción.");
            request.getRequestDispatcher("/WEB-INF/jsp/usuario/misReservas.jsp").forward(request, response);
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener reservas del usuario " + idUsuarioLong + " o al cancelar.", e);
            request.setAttribute("errorMessage", "No se pudieron cargar tus reservas o hubo un error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
}