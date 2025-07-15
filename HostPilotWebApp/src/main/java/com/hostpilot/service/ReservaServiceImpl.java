package com.hostpilot.service;

import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.ReservaDAO;
import com.hostpilot.dao.PropiedadDAO;
import com.hostpilot.dao.PagoDAO; // Importar PagoDAO
import com.hostpilot.model.Reserva;
import com.hostpilot.model.Propiedad;
import com.hostpilot.model.Pago;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservaServiceImpl implements ReservaService {
    private static final Logger LOGGER = Logger.getLogger(ReservaServiceImpl.class.getName());
    private ReservaDAO reservaDAO;
    private PropiedadDAO propiedadDAO;
    private PagoService pagoService; // Inyectar PagoService

    // Constructor para inyección de dependencias
    public ReservaServiceImpl(ReservaDAO reservaDAO, PropiedadDAO propiedadDAO) { // El constructor existente
        this.reservaDAO = reservaDAO;
        this.propiedadDAO = propiedadDAO;
        // NOTA: PagoService se inyectará en los controladores, o se necesita otro constructor/setter
        // Forzar inyección si el controlador lo hace:
        // this.pagoService = new PagoServiceImpl(new PagoDAOImpl(new MySQLDatabaseConfig())); // Evitar esto en un entorno real.
    }

    // NUEVO: Constructor con inyección de PagoService
    public ReservaServiceImpl(ReservaDAO reservaDAO, PropiedadDAO propiedadDAO, PagoService pagoService) {
        this.reservaDAO = reservaDAO;
        this.propiedadDAO = propiedadDAO;
        this.pagoService = pagoService;
    }

    @Override
    public Reserva crearReserva(int idUsuario, int idPropiedad, LocalDate checkin, LocalDate checkout, int adultos, int ninos, int bebes, int mascotas) throws ServiceException {
        if (checkin == null || checkout == null || checkin.isAfter(checkout) || checkin.isEqual(checkout)) {
            throw new ServiceException("Fechas de check-in/check-out inválidas.");
        }
        if (adultos <= 0) {
            throw new ServiceException("Debe haber al menos un adulto.");
        }

        try {
            Optional<Propiedad> propiedadOpt = propiedadDAO.buscarPorId((long)idPropiedad); 
            Propiedad propiedad = propiedadOpt.orElse(null);

            if (propiedad == null) {
                throw new ServiceException("Propiedad no encontrada.");
            }

            long numNoches = ChronoUnit.DAYS.between(checkin, checkout);
            if (numNoches <= 0) {
                throw new ServiceException("Número de noches inválido.");
            }

            double precioPorNoche = propiedad.getPrecioPorNoche();
            double total = precioPorNoche * numNoches;

            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setIdUsuario(idUsuario);
            nuevaReserva.setIdPropiedad(idPropiedad);
            nuevaReserva.setFechaCheckin(checkin);
            nuevaReserva.setFechaCheckout(checkout);
            nuevaReserva.setEstado("PENDIENTE");
            nuevaReserva.setTotal(total);
            nuevaReserva.setNumeroAdultos(adultos);
            nuevaReserva.setNumeroNinos(ninos);
            nuevaReserva.setNumeroBebes(bebes);
            nuevaReserva.setNumeroMascotas(mascotas);
            
            int idGenerado = reservaDAO.crear(nuevaReserva);
            nuevaReserva.setId(idGenerado);
            return nuevaReserva;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error DAO al crear reserva", e);
            throw new ServiceException("Error al crear la reserva.", e);
        }
    }

    @Override
    public Reserva buscarReservaPorId(int idReserva) throws ServiceException {
        try {
            Reserva reserva = reservaDAO.buscarPorId(idReserva);
            if (reserva == null) {
                throw new ServiceException("Reserva no encontrada.");
            }
            // Actualizar el estado si está pagada
            if (esReservaPagada(reserva.getId())) {
                reserva.setEstado("PAGADO");
            }
            return reserva;
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error DAO al buscar reserva por ID", e);
            throw new ServiceException("Error al buscar la reserva.", e);
        }
    }

    @Override
    public List<Reserva> buscarReservasPorUsuario(int idUsuario) throws ServiceException {
        try {
            List<Reserva> reservas = reservaDAO.buscarPorUsuario(idUsuario); // Ya filtra CANCELADA en DAO
            for (Reserva reserva : reservas) {
                if (esReservaPagada(reserva.getId())) {
                    reserva.setEstado("PAGADO");
                }
            }
            return reservas;
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error DAO al buscar reservas por usuario", e);
            throw new ServiceException("Error al obtener las reservas del usuario.", e);
        }
    }

    @Override
    public void modificarReserva(Reserva reservaModificada) throws ServiceException {
        try {
            if (esReservaPagada(reservaModificada.getId())) {
                throw new ServiceException("No se puede modificar una reserva ya PAGADA. Por favor, cancélela y haga una nueva.");
            }

            if (reservaModificada.getFechaCheckin() == null || reservaModificada.getFechaCheckout() == null ||
                reservaModificada.getFechaCheckin().isAfter(reservaModificada.getFechaCheckout()) ||
                reservaModificada.getFechaCheckin().isEqual(reservaModificada.getFechaCheckout())) {
                throw new ServiceException("Fechas de check-in/check-out inválidas para la modificación.");
            }
            if (reservaModificada.getNumeroAdultos() <= 0) {
                throw new ServiceException("Debe haber al menos un adulto en la reserva.");
            }

            Optional<Propiedad> propiedadOpt = propiedadDAO.buscarPorId((long)reservaModificada.getIdPropiedad());
            Propiedad propiedad = propiedadOpt.orElse(null);

            if (propiedad == null) {
                throw new ServiceException("Propiedad de la reserva no encontrada.");
            }

            long numNoches = ChronoUnit.DAYS.between(reservaModificada.getFechaCheckin(), reservaModificada.getFechaCheckout());
            double precioPorNoche = propiedad.getPrecioPorNoche();
            double nuevoTotal = precioPorNoche * numNoches;
            reservaModificada.setTotal(nuevoTotal);

            reservaDAO.actualizar(reservaModificada);

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error DAO al modificar reserva", e);
            throw new ServiceException("Error al modificar la reserva.", e);
        }
    }

    @Override
    public boolean esReservaPagada(int reservaId) throws ServiceException {
        try {
            List<Pago> pagos = reservaDAO.obtenerPagosPorReserva(reservaId);
            return !pagos.isEmpty();
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error DAO al verificar si la reserva está pagada: " + reservaId, e);
            throw new ServiceException("Error al verificar el estado de pago de la reserva.", e);
        }
    }

    @Override
    public void cancelarReserva(int reservaId, int idUsuario) throws ServiceException {
        try {
            Reserva reserva = reservaDAO.buscarPorId(reservaId);
            if (reserva == null) {
                throw new ServiceException("Reserva no encontrada.");
            }
            if (reserva.getIdUsuario() != idUsuario) {
                throw new ServiceException("No tienes permiso para cancelar esta reserva.");
            }
            
            // Si está pagada, solo cambiar estado. Si no, se puede borrar directamente.
            if (esReservaPagada(reservaId)) {
                reserva.setEstado("CANCELADA"); // Mantener registro del pago
                reservaDAO.actualizar(reserva);
                LOGGER.info("Reserva " + reservaId + " (pagada) CANCELADA exitosamente por usuario " + idUsuario);
                // Aquí podrías implementar lógica de reembolso si fuera necesario.
            } else {
                // Si no está pagada, podemos eliminarla completamente
                pagoService.eliminarPagosDeReserva(reservaId); // Asegúrate de que PagoService esté inyectado
                reservaDAO.eliminar(reservaId); // Eliminar la reserva de la BD
                LOGGER.info("Reserva " + reservaId + " (pendiente) ELIMINADA exitosamente por usuario " + idUsuario);
            }
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error DAO al cancelar reserva " + reservaId, e);
            throw new ServiceException("Error al cancelar la reserva.", e);
        }
    }

    @Override
    public void eliminarReserva(int reservaId, int idUsuario) throws ServiceException {
        // Este método es una opción si quieres un "borrado total" distinto de cancelar.
        // Por ahora, se usa internamente en cancelarReserva si no hay pagos.
        // Si lo vas a usar externamente, deberías considerar si se permiten eliminar reservas pagadas, etc.
        try {
            Reserva reserva = reservaDAO.buscarPorId(reservaId);
            if (reserva == null) {
                throw new ServiceException("Reserva no encontrada.");
            }
            if (reserva.getIdUsuario() != idUsuario) {
                throw new ServiceException("No tienes permiso para eliminar esta reserva.");
            }
            
            pagoService.eliminarPagosDeReserva(reservaId);
            reservaDAO.eliminar(reservaId);
            LOGGER.info("Reserva " + reservaId + " ELIMINADA permanentemente por usuario " + idUsuario);
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error DAO al eliminar reserva " + reservaId, e);
            throw new ServiceException("Error al eliminar la reserva.", e);
        }
    }
}