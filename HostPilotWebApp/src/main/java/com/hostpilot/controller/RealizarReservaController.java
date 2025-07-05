package com.hostpilot.controller;

import com.hostpilot.config.DatabaseConfig;
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
     * Inicializa el servlet y sus dependencias.
     * @throws ServletException si la inicialización falla.
     */
    @Override
    public void init() throws ServletException {
        // FIX: Bloque init() simplificado para eliminar código duplicado.
        LOGGER.info("Inicializando RealizarReservaController...");
        try {
            // Se recomienda instanciar la configuración una sola vez si es posible,
            // pero para un servlet, instanciarla aquí está bien.
            DatabaseConfig dbConfig = new MySQLDatabaseConfig();
            this.reservaDAO = new ReservaDAOImpl(dbConfig);
            LOGGER.info("RealizarReservaController inicializado exitosamente con su DAO.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fallo crítico al inicializar RealizarReservaController", e);
            // Lanzar ServletException impide que el servlet se ponga en servicio si la BD falla.
            throw new ServletException("No se pudo inicializar el DAO de reservas", e);
        }
         // AÑADE ESTA LÍNEA JUSTO AL PRINCIPIO
    System.out.println("########## CARGANDO SERVLET: RealizarReservaController ##########");

    LOGGER.info("Inicializando RealizarReservaController...");
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
            nuevaReserva.setIdUsuario(idUsuarioLong.intValue()); // Convertir Long a int
            nuevaReserva.setIdPropiedad(idPropiedad);
            nuevaReserva.setFechaCheckin(checkin);
            nuevaReserva.setFechaCheckout(checkout);
            nuevaReserva.setEstado("CONFIRMADA"); // Asignar estado por defecto

            // 4. Persistir la Reserva usando el DAO
            int nuevoId = reservaDAO.crear(nuevaReserva);

            // La lógica de verificar si nuevoId > 0 ya está implícita en el DAO,
            // que lanzará una excepción si falla.
            
            LOGGER.info("Reserva creada exitosamente con ID: " + nuevoId + " para Usuario ID " + idUsuarioLong);
                
            // 5. Enviar Respuesta de Éxito
            response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created es más apropiado para creación de recursos
            // FIX: La línea de respuesta JSON se completó correctamente.
            response.getWriter().write("{\"status\":\"success\", \"message\":\"¡Alojamiento reservado exitosamente!\", \"reservaId\":" + nuevoId + "}");

        // FIX: Se añadieron bloques catch para un manejo de errores completo y robusto.
        } catch (NumberFormatException e) {
            LOGGER.warning("ID de propiedad inválido: " + propiedadIdStr);
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "El ID de la propiedad debe ser un número válido.");
        } catch (DateTimeParseException e) {
            LOGGER.warning("Formato de fecha inválido. Check-in: " + checkinStr + ", Check-out: " + checkoutStr);
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "El formato de las fechas debe ser AAAA-MM-DD.");
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error al intentar crear la reserva en la base de datos", e);
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ocurrió un error en el servidor al procesar tu reserva. Inténtalo más tarde.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado en RealizarReservaController", e);
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado.");
        }
    }

    /**
     * Método de utilidad para enviar respuestas de error en formato JSON.
     */
    private void sendJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        // Escapar comillas dobles en el mensaje para evitar un JSON inválido
        String safeMessage = message.replace("\"", "\\\"");
        response.getWriter().write(String.format("{\"status\":\"error\", \"message\":\"%s\"}", safeMessage));
    }
}