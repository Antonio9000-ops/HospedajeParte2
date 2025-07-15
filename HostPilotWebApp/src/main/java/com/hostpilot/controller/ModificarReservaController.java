package com.hostpilot.controller;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.PropiedadDAO;
import com.hostpilot.dao.PropiedadDAOImpl;
import com.hostpilot.dao.ReservaDAO;
import com.hostpilot.dao.ReservaDAOImpl;
import com.hostpilot.dao.PagoDAO;
import com.hostpilot.dao.PagoDAOImpl;
import com.hostpilot.model.Reserva;
import com.hostpilot.model.Propiedad;
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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/modificar-reserva")
public class ModificarReservaController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ModificarReservaController.class.getName());
    private ReservaService reservaService;
    private PagoService pagoService;
    private PropiedadDAO propiedadDAO;

    @Override
    public void init() throws ServletException {
        LOGGER.info("########## INICIALIZANDO SERVLET: ModificarReservaController ##########");
        try {
            DatabaseConfig dbConfig = new MySQLDatabaseConfig();
            ReservaDAO reservaDAO = new ReservaDAOImpl(dbConfig);
            propiedadDAO = new PropiedadDAOImpl(dbConfig);
            PagoDAO pagoDAO = new PagoDAOImpl(dbConfig);
            this.pagoService = new PagoServiceImpl(pagoDAO); // Primero instanciar PagoService
            this.reservaService = new ReservaServiceImpl(reservaDAO, propiedadDAO, this.pagoService); // Instanciar ReservaService inyectando PagoService
            LOGGER.info("ModificarReservaController inicializado exitosamente.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fallo crítico al inicializar ModificarReservaController", e);
            throw new ServletException("No se pudo inicializar el servicio de modificación de reservas", e);
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

        String idReservaStr = request.getParameter("id");
        if (idReservaStr == null || idReservaStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/mis-reservas?error=no_id");
            return;
        }

        try {
            int idReserva = Integer.parseInt(idReservaStr);
            Reserva reserva = reservaService.buscarReservaPorId(idReserva);

            if (reserva == null || reserva.getIdUsuario() != idUsuarioLong.intValue()) {
                response.sendRedirect(request.getContextPath() + "/mis-reservas?error=reserva_no_encontrada");
                return;
            }
            
            // Verificar si la reserva ya está pagada al cargar el formulario
            if (reservaService.esReservaPagada(idReserva)) {
                request.setAttribute("errorMessage", "Esta reserva ya ha sido pagada y no se puede modificar.");
                request.getRequestDispatcher("/WEB-INF/jsp/usuario/misReservas.jsp").forward(request, response);
                return;
            }

            Propiedad propiedad = null;
            try {
                propiedad = propiedadDAO.buscarPorId((long)reserva.getIdPropiedad()).orElse(null);
            } catch (DAOException e) {
                LOGGER.log(Level.SEVERE, "Error DAO al buscar propiedad con ID " + reserva.getIdPropiedad() + " para modificación de reserva " + idReserva, e);
                throw new ServiceException("Error al cargar la propiedad asociada a la reserva.", e);
            }

            if (propiedad == null) {
                LOGGER.warning("Propiedad asociada a la reserva " + idReserva + " no encontrada.");
            }

            request.setAttribute("reserva", reserva);
            request.setAttribute("propiedad", propiedad);

            request.getRequestDispatcher("/WEB-INF/jsp/usuario/modificarReserva.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            LOGGER.warning("ID de reserva inválido: " + idReservaStr);
            response.sendRedirect(request.getContextPath() + "/mis-reservas?error=id_invalido");
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar reserva para modificación: " + idReservaStr, e);
            request.setAttribute("errorMessage", "Error al cargar los datos de la reserva: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/usuario/misReservas.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long idUsuarioLong = SessionManager.getCurrentUserId(request);
        if (idUsuarioLong == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idReservaStr = request.getParameter("idReserva");
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

        if (idReservaStr == null || idReservaStr.isEmpty() || checkinStr == null || checkinStr.isEmpty() ||
            checkoutStr == null || checkoutStr.isEmpty() || adultosStr == null || adultosStr.isEmpty()) {
            request.setAttribute("errorMessage", "Por favor, completa todos los campos requeridos.");
            try {
                int idReserva = Integer.parseInt(idReservaStr);
                Reserva resParaForm = reservaService.buscarReservaPorId(idReserva);
                request.setAttribute("reserva", resParaForm);
                if (resParaForm != null) {
                    request.setAttribute("propiedad", propiedadDAO.buscarPorId((long)resParaForm.getIdPropiedad()).orElse(null));
                }
            } catch (Exception e) { }
            request.getRequestDispatcher("/WEB-INF/jsp/usuario/modificarReserva.jsp").forward(request, response);
            return;
        }

        if (titularTarjeta == null || numeroTarjeta == null || vencimiento == null || cvv == null ||
            titularTarjeta.trim().isEmpty() || numeroTarjeta.trim().isEmpty() || vencimiento.trim().isEmpty() || cvv.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Por favor, complete todos los campos de pago.");
            request.setAttribute("reserva", createTempReservaFromParams(request));
            try {
                String propiedadIdParam = request.getParameter("idPropiedad");
                if (propiedadIdParam != null && !propiedadIdParam.isEmpty()) {
                    int propiedadId = Integer.parseInt(propiedadIdParam);
                    request.setAttribute("propiedad", propiedadDAO.buscarPorId((long)propiedadId).orElse(null));
                }
            } catch (Exception ex) { }
            request.getRequestDispatcher("/WEB-INF/jsp/usuario/modificarReserva.jsp").forward(request, response);
            return;
        }

        try {
            int idReserva = Integer.parseInt(idReservaStr);
            LocalDate checkin = LocalDate.parse(checkinStr);
            LocalDate checkout = LocalDate.parse(checkoutStr);
            int adultos = Integer.parseInt(adultosStr);
            int ninos = (ninosStr != null && !ninosStr.isEmpty()) ? Integer.parseInt(ninosStr) : 0;
            int bebes = (bebesStr != null && !bebesStr.isEmpty()) ? Integer.parseInt(bebesStr) : 0;
            int mascotas = (mascotasStr != null && !mascotasStr.isEmpty()) ? Integer.parseInt(mascotasStr) : 0;

            Reserva reservaOriginal = reservaService.buscarReservaPorId(idReserva);

            if (reservaOriginal == null || reservaOriginal.getIdUsuario() != idUsuarioLong.intValue()) {
                request.setAttribute("errorMessage", "No tienes permiso para modificar esta reserva o no existe.");
                response.sendRedirect(request.getContextPath() + "/mis-reservas");
                return;
            }
            
            // Verificar si la reserva ya está pagada ANTES de modificarla
            if (reservaService.esReservaPagada(idReserva)) {
                request.setAttribute("errorMessage", "Esta reserva ya ha sido pagada y no se puede modificar.");
                request.setAttribute("reserva", createTempReservaFromParams(request));
                try {
                    String propiedadIdParam = request.getParameter("idPropiedad");
                    if (propiedadIdParam != null && !propiedadIdParam.isEmpty()) {
                        int propiedadId = Integer.parseInt(propiedadIdParam);
                        request.setAttribute("propiedad", propiedadDAO.buscarPorId((long)propiedadId).orElse(null));
                    }
                } catch (Exception ex) { }
                request.getRequestDispatcher("/WEB-INF/jsp/usuario/modificarReserva.jsp").forward(request, response);
                return;
            }

            reservaOriginal.setFechaCheckin(checkin);
            reservaOriginal.setFechaCheckout(checkout);
            reservaOriginal.setNumeroAdultos(adultos);
            reservaOriginal.setNumeroNinos(ninos);
            reservaOriginal.setNumeroBebes(bebes);
            reservaOriginal.setNumeroMascotas(mascotas);

            reservaService.modificarReserva(reservaOriginal); // Esto ya recalcula el total

            // Procesar el pago después de modificar la reserva
            BigDecimal montoPago = BigDecimal.valueOf(reservaOriginal.getTotal());
            String metodoPago = "Tarjeta de Crédito";
            String transaccionId = "MOD_TXN_" + System.currentTimeMillis();

            pagoService.procesarPago(reservaOriginal.getId(), metodoPago, transaccionId, montoPago);
            
            // Actualizar estado de la reserva a "PAGADO" después de un pago exitoso
            reservaOriginal.setEstado("PAGADO");
            reservaService.modificarReserva(reservaOriginal); // Reutilizar modificarReserva para actualizar el estado en DB

            response.sendRedirect(request.getContextPath() + "/mis-reservas?status=modificado");

        } catch (NumberFormatException | DateTimeParseException | ServiceException e) {
            LOGGER.log(Level.WARNING, "Error al procesar modificación de reserva: " + e.getMessage(), e);
            String errorMessage = e instanceof NumberFormatException ? "Datos numéricos inválidos en el formulario." :
                                  e instanceof DateTimeParseException ? "El formato de las fechas debe ser AAAA-MM-DD." :
                                  e.getMessage();
            
            request.setAttribute("errorMessage", errorMessage);
            
            request.setAttribute("reserva", createTempReservaFromParams(request));
            try {
                String propiedadIdParam = request.getParameter("idPropiedad");
                if (propiedadIdParam != null && !propiedadIdParam.isEmpty()) {
                    int propiedadId = Integer.parseInt(propiedadIdParam);
                    request.setAttribute("propiedad", propiedadDAO.buscarPorId((long)propiedadId).orElse(null));
                }
            } catch (Exception ex) { }
            
            request.getRequestDispatcher("/WEB-INF/jsp/usuario/modificarReserva.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado en ModificarReservaController (POST)", e);
            request.setAttribute("errorMessage", "Ocurrió un error inesperado al intentar modificar tu reserva.");
            request.setAttribute("reserva", createTempReservaFromParams(request));
            try {
                String propiedadIdParam = request.getParameter("idPropiedad");
                if (propiedadIdParam != null && !propiedadIdParam.isEmpty()) {
                    int propiedadId = Integer.parseInt(propiedadIdParam);
                    request.setAttribute("propiedad", propiedadDAO.buscarPorId((long)propiedadId).orElse(null));
                }
            } catch (Exception ex) { }
            request.getRequestDispatcher("/WEB-INF/jsp/usuario/modificarReserva.jsp").forward(request, response);
        }
    }
    
    private Reserva createTempReservaFromParams(HttpServletRequest request) {
        Reserva tempReserva = new Reserva();
        try {
            if (request.getParameter("idReserva") != null && !request.getParameter("idReserva").isEmpty()) {
                tempReserva.setId(Integer.parseInt(request.getParameter("idReserva")));
            }
            if (request.getParameter("idPropiedad") != null && !request.getParameter("idPropiedad").isEmpty()) {
                tempReserva.setIdPropiedad(Integer.parseInt(request.getParameter("idPropiedad")));
            }
            if (request.getParameter("checkin") != null && !request.getParameter("checkin").isEmpty()) {
                tempReserva.setFechaCheckin(LocalDate.parse(request.getParameter("checkin")));
            }
            if (request.getParameter("checkout") != null && !request.getParameter("checkout").isEmpty()) {
                tempReserva.setFechaCheckout(LocalDate.parse(request.getParameter("checkout")));
            }
            tempReserva.setNumeroAdultos(parseIntOrDefault(request.getParameter("adultos"), 0));
            tempReserva.setNumeroNinos(parseIntOrDefault(request.getParameter("ninos"), 0));
            tempReserva.setNumeroBebes(parseIntOrDefault(request.getParameter("bebes"), 0));
            tempReserva.setNumeroMascotas(parseIntOrDefault(request.getParameter("mascotas"), 0));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al crear objeto Reserva temporal de parámetros: " + e.getMessage());
        }
        return tempReserva;
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}