package com.hostpilot.controller;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.config.MetricsConfig; // Importar la configuración de métricas
import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.ReservaDAO;
import com.hostpilot.dao.ReservaDAOImpl;
import com.hostpilot.model.Reserva;
import com.hostpilot.security.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/realizar-reserva")
public class RealizarReservaController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RealizarReservaController.class.getName());
    
    private ReservaDAO reservaDAO;

    /**
     * Inicializa el servlet, sus dependencias y registra las métricas.
     * @throws ServletException si la inicialización falla.
     */
    @Override
    public void init() throws ServletException {
        // --- CÓDIGO CORREGIDO Y LIMPIO ---
        // Se elimina el bloque duplicado y se añade un log claro.
        LOGGER.info("########## INICIALIZANDO SERVLET: RealizarReservaController ##########");
        try {
            DatabaseConfig dbConfig = new MySQLDatabaseConfig();
            this.reservaDAO = new ReservaDAOImpl(dbConfig);
            LOGGER.info("RealizarReservaController inicializado exitosamente con su DAO.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fallo crítico al inicializar RealizarReservaController", e);
            throw new ServletException("No se pudo inicializar el DAO de reservas", e);
        }
    }
    
    /**
     * Maneja las solicitudes POST para crear una nueva reserva.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 1. Verificar Autenticación del Usuario
        Long idUsuarioLong = SessionManager.getCurrentUserId(request);
        if (idUsuarioLong == null) {
            LOGGER.warning("Intento de reserva por usuario no autenticado.");
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión para reservar.");
            return;
        }
        
        // 2. Validar y Parsear los Parámetros de Entrada
        String propiedadIdStr = request.getParameter("propiedadId");
        String checkinStr = request.getParameter("checkin");
        String checkoutStr = request.getParameter("checkout");

        if (propiedadIdStr == null || checkinStr == null || checkoutStr == null || 
            propiedadIdStr.trim().isEmpty() || checkinStr.trim().isEmpty() || checkoutStr.trim().isEmpty()) {
            LOGGER.warning("Datos de reserva incompletos recibidos.");
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Todos los campos son requeridos: propiedadId, checkin, checkout.");
            return;
        }

        try {
            int idPropiedad = Integer.parseInt(propiedadIdStr);
            LocalDate checkin = LocalDate.parse(checkinStr);
            LocalDate checkout = LocalDate.parse(checkoutStr);

            if (checkout.isBefore(checkin) || checkout.isEqual(checkin)) {
                LOGGER.warning("Fecha de check-out inválida: " + checkout);
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "La fecha de Check-out debe ser posterior a la de Check-in.");
                return;
            }
            
            // 3. Crear el Objeto Reserva
            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setIdUsuario(idUsuarioLong.intValue());
            nuevaReserva.setIdPropiedad(idPropiedad);
            nuevaReserva.setFechaCheckin(checkin);
            nuevaReserva.setFechaCheckout(checkout);
            nuevaReserva.setEstado("CONFIRMADA");

            // 4. Persistir la Reserva usando el DAO
            int nuevoId = reservaDAO.crear(nuevaReserva);

            // --- INTEGRACIÓN CON PROMETHEUS ---
            // Incrementar el contador de reservas exitosas.
            MetricsConfig.getRegistry().counter("reservas_creadas_total", "status", "success").increment();
            // ------------------------------------
            
            LOGGER.info("Reserva creada exitosamente con ID: " + nuevoId + " para Usuario ID " + idUsuarioLong);
                
            // 5. Enviar Respuesta de Éxito
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"status\":\"success\", \"message\":\"¡Alojamiento reservado exitosamente!\", \"reservaId\":" + nuevoId + "}");

        } catch (NumberFormatException e) {
            MetricsConfig.getRegistry().counter("reservas_creadas_total", "status", "error_formato_id").increment();
            LOGGER.warning("ID de propiedad inválido: " + propiedadIdStr);
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "El ID de la propiedad debe ser un número válido.");
            
        } catch (DateTimeParseException e) {
            MetricsConfig.getRegistry().counter("reservas_creadas_total", "status", "error_formato_fecha").increment();
            LOGGER.warning("Formato de fecha inválido. Check-in: " + checkinStr + ", Check-out: " + checkoutStr);
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "El formato de las fechas debe ser AAAA-MM-DD.");
            
        } catch (DAOException e) {
            // --- INTEGRACIÓN CON PROMETHEUS ---
            // Incrementar el contador de reservas fallidas por error en la base de datos.
            MetricsConfig.getRegistry().counter("reservas_creadas_total", "status", "error_dao").increment();
            // ------------------------------------
            LOGGER.log(Level.SEVERE, "Error al intentar crear la reserva en la base de datos", e);
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ocurrió un error en el servidor al procesar tu reserva. Inténtalo más tarde.");

        } catch (Exception e) {
            MetricsConfig.getRegistry().counter("reservas_creadas_total", "status", "error_inesperado").increment();
            LOGGER.log(Level.SEVERE, "Error inesperado en RealizarReservaController", e);
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado.");
        }
    }

    /**
     * Método de utilidad para enviar respuestas de error en formato JSON.
     */
    private void sendJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        String safeMessage = message.replace("\"", "\\\"");
        response.getWriter().write(String.format("{\"status\":\"error\", \"message\":\"%s\"}", safeMessage));
    }
}