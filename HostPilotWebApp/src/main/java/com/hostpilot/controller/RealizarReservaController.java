package com.hostpilot.controller;

import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.ReservaDAO;
import com.hostpilot.dao.ReservaDAOImpl;
import com.hostpilot.model.Reserva;
import com.hostpilot.security.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RealizarReservaController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RealizarReservaController.class.getName());
    private ReservaDAO reservaDAO;

    @Override
    public void init() throws ServletException {
        // Inicializar el DAO
        this.reservaDAO = new ReservaDAOImpl(new MySQLDatabaseConfig());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Verificar si el usuario está logueado
        Long idUsuarioLong = SessionManager.getCurrentUserId(request);
        if (idUsuarioLong == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Debe iniciar sesión para reservar.\"}");
            return;
        }
        int idUsuario = idUsuarioLong.intValue();
        
        // 2. Obtener los datos de la reserva desde la solicitud
        try {
            int idPropiedad = Integer.parseInt(request.getParameter("propiedadId"));
            LocalDate checkin = LocalDate.parse(request.getParameter("checkin"));
            LocalDate checkout = LocalDate.parse(request.getParameter("checkout"));

            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setIdUsuario(idUsuario);
            nuevaReserva.setIdPropiedad(idPropiedad);
            nuevaReserva.setFechaCheckin(checkin);
            nuevaReserva.setFechaCheckout(checkout);

            // 3. Crear la reserva en la base de datos
            reservaDAO.crear(nuevaReserva);

            LOGGER.info("Reserva creada exitosamente para usuario ID: " + idUsuario + " y propiedad ID: " + idPropiedad);
            
            // 4. Enviar respuesta de éxito
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":\"success\", \"message\":\"¡Alojamiento reservado exitosamente!\"}");

        } catch (NumberFormatException | NullPointerException e) {
            LOGGER.log(Level.WARNING, "Datos de reserva inválidos", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Datos de reserva inválidos.\"}");
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al crear reserva", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Error al procesar su reserva.\"}");
        }
    }
}