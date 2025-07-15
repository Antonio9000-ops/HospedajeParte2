package com.hostpilot.controller;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.PagoDAO;
import com.hostpilot.dao.PagoDAOImpl;
import com.hostpilot.model.Pago;
import com.hostpilot.model.Reserva; // Para obtener detalles de la reserva asociada
import com.hostpilot.dao.ReservaDAO; // Y su DAO
import com.hostpilot.dao.ReservaDAOImpl; // Y su implementación
import com.hostpilot.model.Propiedad; // Para detalles de la propiedad asociada
import com.hostpilot.dao.PropiedadDAO; // Y su DAO
import com.hostpilot.dao.PropiedadDAOImpl; // Y su implementación
import com.hostpilot.security.SessionManager;
import com.hostpilot.service.PagoService;
import com.hostpilot.service.PagoServiceImpl;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/historial-pagos")
public class HistorialPagosController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(HistorialPagosController.class.getName());
    private PagoService pagoService;
    private ReservaDAO reservaDAO; // Para obtener detalles de la reserva
    private PropiedadDAO propiedadDAO; // Para obtener detalles de la propiedad

    @Override
    public void init() throws ServletException {
        LOGGER.info("########## INICIALIZANDO SERVLET: HistorialPagosController ##########");
        try {
            DatabaseConfig dbConfig = new MySQLDatabaseConfig();
            PagoDAO pagoDAO = new PagoDAOImpl(dbConfig);
            reservaDAO = new ReservaDAOImpl(dbConfig); // Inicializar
            propiedadDAO = new PropiedadDAOImpl(dbConfig); // Inicializar
            this.pagoService = new PagoServiceImpl(pagoDAO);
            LOGGER.info("HistorialPagosController inicializado exitosamente.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fallo crítico al inicializar HistorialPagosController", e);
            throw new ServletException("No se pudo inicializar el servicio de historial de pagos", e);
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
            List<Pago> historialPagos = pagoService.obtenerHistorialPagosUsuario(idUsuarioLong.intValue());

            // Opcional: Obtener detalles de la reserva y propiedad para cada pago
            Map<Integer, Reserva> reservasAsociadas = new HashMap<>();
            Map<Integer, Propiedad> propiedadesAsociadas = new HashMap<>();

            for (Pago pago : historialPagos) {
                if (!reservasAsociadas.containsKey(pago.getReservaId())) {
                    Reserva reserva = reservaDAO.buscarPorId(pago.getReservaId());
                    if (reserva != null) {
                        reservasAsociadas.put(reserva.getId(), reserva);
                        if (!propiedadesAsociadas.containsKey(reserva.getIdPropiedad())) {
                            Propiedad propiedad = propiedadDAO.buscarPorId((long)reserva.getIdPropiedad()).orElse(null);
                            if (propiedad != null) {
                                propiedadesAsociadas.put(propiedad.getId(), propiedad);
                            }
                        }
                    }
                }
            }

            request.setAttribute("historialPagos", historialPagos);
            request.setAttribute("reservasAsociadas", reservasAsociadas);
            request.setAttribute("propiedadesAsociadas", propiedadesAsociadas);

            request.getRequestDispatcher("/WEB-INF/jsp/usuario/historialPagos.jsp").forward(request, response);

        } catch (ServiceException | DAOException e) { // Capturar ServiceException y DAOException
            LOGGER.log(Level.SEVERE, "Error al obtener historial de pagos del usuario " + idUsuarioLong, e);
            request.setAttribute("errorMessage", "No se pudo cargar tu historial de pagos: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
}