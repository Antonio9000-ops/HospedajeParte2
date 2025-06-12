package com.hostpilot.controller;

import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.ReservaDAO;
import com.hostpilot.dao.ReservaDAOImpl;
import com.hostpilot.model.Reserva;
import com.hostpilot.security.SessionManager;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/realizar-reserva")
public class RealizarReservaController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RealizarReservaController.class.getName());
    private ReservaDAO reservaDAO;

    @Override
    public void init() throws ServletException {
        this.reservaDAO = new ReservaDAOImpl(new MySQLDatabaseConfig());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Long idUsuarioLong = SessionManager.getCurrentUserId(request);
        if (idUsuarioLong == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Debe iniciar sesión para reservar.\"}");
            return;
        }
        int idUsuario = idUsuarioLong.intValue();
        
        try {
            int idPropiedad = Integer.parseInt(request.getParameter("propiedadId"));
            LocalDate checkin = LocalDate.parse(request.getParameter("checkin"));
            LocalDate checkout = LocalDate.parse(request.getParameter("checkout"));

            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setIdUsuario(idUsuario);
            nuevaReserva.setIdPropiedad(idPropiedad);
            nuevaReserva.setFechaCheckin(checkin);
            nuevaReserva.setFechaCheckout(checkout);

            reservaDAO.crear(nuevaReserva);

            LOGGER.info("Reserva creada: Usuario ID " + idUsuario + ", Propiedad ID " + idPropiedad);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\", \"message\":\"¡Alojamiento reservado exitosamente!\"}");

        } catch (NumberFormatException | NullPointerException e) {
            LOGGER.log(Level.WARNING, "Datos de reserva inválidos", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Datos de reserva inválidos.\"}");
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al crear reserva", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Error al procesar su reserva.\"}");
        }
    }
}