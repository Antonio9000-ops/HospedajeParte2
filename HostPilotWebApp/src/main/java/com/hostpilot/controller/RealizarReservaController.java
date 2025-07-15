package com.hostpilot.controller;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.config.MetricsConfig;
import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.PropiedadDAO;
import com.hostpilot.dao.PropiedadDAOImpl;
import com.hostpilot.dao.ReservaDAO;
import com.hostpilot.dao.ReservaDAOImpl;
import com.hostpilot.dao.PagoDAO;
import com.hostpilot.dao.PagoDAOImpl;
import com.hostpilot.model.Reserva;
import com.hostpilot.security.SessionManager;
import com.hostpilot.service.ReservaService;
import com.hostpilot.service.ReservaServiceImpl;
import com.hostpilot.service.PagoService;
import com.hostpilot.service.PagoServiceImpl;
import com.hostpilot.service.ServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/realizar-reserva")
public class RealizarReservaController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RealizarReservaController.class.getName());
    
    private ReservaService reservaService;
    private PagoService pagoService;

    @Override
    public void init() throws ServletException {
        LOGGER.info("########## INICIALIZANDO SERVLET: RealizarReservaController ##########");
        try {
            DatabaseConfig dbConfig = new MySQLDatabaseConfig();
            ReservaDAO reservaDAO = new ReservaDAOImpl(dbConfig);
            PropiedadDAO propiedadDAO = new PropiedadDAOImpl(dbConfig);
            PagoDAO pagoDAO = new PagoDAOImpl(dbConfig);
            this.pagoService = new PagoServiceImpl(pagoDAO); // Primero instanciar PagoService
            this.reservaService = new ReservaServiceImpl(reservaDAO, propiedadDAO, this.pagoService); // Luego instanciar ReservaService inyectando PagoService
            LOGGER.info("RealizarReservaController inicializado exitosamente con sus Servicios.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fallo crítico al inicializar RealizarReservaController", e);
            throw new ServletException("No se pudo inicializar el Servicio de reservas", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Long idUsuarioLong = SessionManager.getCurrentUserId(request);
        if (idUsuarioLong == null) {
            LOGGER.warning("Intento de reserva por usuario no autenticado.");
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión para reservar.");
            return;
        }
        
        String propiedadIdStr = request.getParameter("propiedadId");
        String checkinStr = request.getParameter("checkin");
        String checkoutStr = request.getParameter("checkout");
        String adultosStr = request.getParameter("adultos");
        String ninosStr = request.getParameter("ninos");
        String bebesStr = request.getParameter("bebes");
        String mascotasStr = request.getParameter("mascotas");

        String titularTarjeta = request.getParameter("titularTarjeta");
        String numeroTarjeta = request.getParameter("numeroTarjeta");
        String vencimiento = request.getParameter("vencimiento");
        String cvv = request.getParameter("cvv");

        if (propiedadIdStr == null || checkinStr == null || checkoutStr == null || adultosStr == null ||
            propiedadIdStr.trim().isEmpty() || checkinStr.trim().isEmpty() || checkoutStr.trim().isEmpty() || adultosStr.trim().isEmpty()) {
            LOGGER.warning("Datos de reserva incompletos recibidos.");
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Todos los campos son requeridos: propiedadId, checkin, checkout, adultos.");
            return;
        }
        
        if (titularTarjeta == null || numeroTarjeta == null || vencimiento == null || cvv == null ||
            titularTarjeta.trim().isEmpty() || numeroTarjeta.trim().isEmpty() || vencimiento.trim().isEmpty() || cvv.trim().isEmpty()) {
            LOGGER.warning("Datos de pago incompletos.");
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Por favor, complete todos los campos de pago.");
            return;
        }

        try {
            int idPropiedad = Integer.parseInt(propiedadIdStr);
            LocalDate checkin = LocalDate.parse(checkinStr);
            LocalDate checkout = LocalDate.parse(checkoutStr);
            int adultos = Integer.parseInt(adultosStr);
            int ninos = (ninosStr != null && !ninosStr.isEmpty()) ? Integer.parseInt(ninosStr) : 0;
            int bebes = (bebesStr != null && !bebesStr.isEmpty()) ? Integer.parseInt(bebesStr) : 0;
            int mascotas = (mascotasStr != null && !mascotasStr.isEmpty()) ? Integer.parseInt(mascotasStr) : 0;

            Reserva nuevaReserva = reservaService.crearReserva(idUsuarioLong.intValue(), idPropiedad, checkin, checkout, adultos, ninos, bebes, mascotas);
            
            BigDecimal montoPago = BigDecimal.valueOf(nuevaReserva.getTotal());
            String metodoPago = "Tarjeta de Crédito";
            String transaccionId = "TXN_" + System.currentTimeMillis();

            pagoService.procesarPago(nuevaReserva.getId(), metodoPago, transaccionId, montoPago);

            // Actualizar estado de la reserva a "PAGADO" después de un pago exitoso
            nuevaReserva.setEstado("PAGADO");
            reservaService.modificarReserva(nuevaReserva); // Reutilizar modificarReserva para actualizar el estado en DB

            MetricsConfig.getRegistry().counter("reservas_creadas_total", "status", "success").increment();
            LOGGER.info("Reserva creada y pago procesado exitosamente con ID: " + nuevaReserva.getId() + " para Usuario ID " + idUsuarioLong);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"status\":\"success\", \"message\":\"¡Alojamiento reservado exitosamente y pagado!\", \"reservaId\":" + nuevaReserva.getId() + "}");

        } catch (NumberFormatException e) {
            MetricsConfig.getRegistry().counter("reservas_creadas_total", "status", "error_formato_id").increment();
            LOGGER.warning("Error de formato numérico: " + e.getMessage());
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Datos numéricos inválidos.");
            
        } catch (DateTimeParseException e) {
            MetricsConfig.getRegistry().counter("reservas_creadas_total", "status", "error_formato_fecha").increment();
            LOGGER.warning("Formato de fecha inválido. Check-in: " + checkinStr + ", Check-out: " + checkoutStr);
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "El formato de las fechas debe ser AAAA-MM-DD.");
            
        } catch (ServiceException e) {
            MetricsConfig.getRegistry().counter("reservas_creadas_total", "status", "error_service").increment();
            LOGGER.log(Level.WARNING, "Error de servicio al crear reserva: " + e.getMessage(), e);
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            MetricsConfig.getRegistry().counter("reservas_creadas_total", "status", "error_inesperado").increment();
            LOGGER.log(Level.SEVERE, "Error inesperado en RealizarReservaController", e);
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado.");
        }
    }

    private void sendJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        String safeMessage = message.replace("\"", "\\\"");
        response.getWriter().write(String.format("{\"status\":\"error\", \"message\":\"%s\"}", safeMessage));
    }
}