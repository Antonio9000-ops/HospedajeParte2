package com.hostpilot.service;

import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.PagoDAO;
import com.hostpilot.model.Pago;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PagoServiceImpl implements PagoService {
    private static final Logger LOGGER = Logger.getLogger(PagoServiceImpl.class.getName());
    private PagoDAO pagoDAO;

    public PagoServiceImpl(PagoDAO pagoDAO) {
        this.pagoDAO = pagoDAO;
    }

    @Override
    public Pago procesarPago(int reservaId, String metodo, String transaccionId, BigDecimal monto) throws ServiceException {
        if (reservaId <= 0 || metodo == null || metodo.trim().isEmpty() || monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Datos de pago inválidos.");
        }

        Pago pago = new Pago();
        pago.setReservaId(reservaId);
        pago.setMetodo(metodo);
        pago.setMonto(monto);
        pago.setTransaccionId(transaccionId);

        try {
            pagoDAO.crear(pago);
            LOGGER.info("Pago procesado exitosamente para reserva ID: " + reservaId);
            return pago;
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error al procesar pago para reserva ID: " + reservaId, e);
            throw new ServiceException("Error al registrar el pago: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pago> obtenerHistorialPagosUsuario(int idUsuario) throws ServiceException {
        try {
            return pagoDAO.buscarPorUsuario(idUsuario);
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener historial de pagos para usuario ID: " + idUsuario, e);
            throw new ServiceException("Error al obtener el historial de pagos: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarPagosDeReserva(int reservaId) throws ServiceException { // NUEVO
        try {
            pagoDAO.eliminarPorReserva(reservaId);
            LOGGER.info("Pagos eliminados para la reserva ID: " + reservaId);
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar pagos de la reserva ID: " + reservaId, e);
            throw new ServiceException("Error al eliminar los pagos asociados a la reserva.", e);
        }
    }
}