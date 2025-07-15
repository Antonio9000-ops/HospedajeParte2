package com.hostpilot.service;

import com.hostpilot.model.Pago;
import java.math.BigDecimal;
import java.util.List;

public interface PagoService {
    Pago procesarPago(int reservaId, String metodo, String transaccionId, BigDecimal monto) throws ServiceException;
    List<Pago> obtenerHistorialPagosUsuario(int idUsuario) throws ServiceException;
    void eliminarPagosDeReserva(int reservaId) throws ServiceException; // NUEVO
}