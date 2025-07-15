package com.hostpilot.dao;

import com.hostpilot.model.Reserva;
import java.util.List;
import com.hostpilot.model.Pago;

public interface ReservaDAO {
    int crear(Reserva reserva) throws DAOException;
    List<Reserva> buscarPorUsuario(int idUsuario) throws DAOException;
    Reserva buscarPorId(int idReserva) throws DAOException;
    void actualizar(Reserva reserva) throws DAOException;
    
    List<Pago> obtenerPagosPorReserva(int reservaId) throws DAOException;
    void eliminar(int reservaId) throws DAOException; // NUEVO: Para eliminar la reserva completamente
}