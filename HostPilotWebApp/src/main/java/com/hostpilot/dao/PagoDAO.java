package com.hostpilot.dao;

import com.hostpilot.model.Pago;
import java.util.List;
import java.util.Optional;

public interface PagoDAO {
    void crear(Pago pago) throws DAOException;
    Optional<Pago> buscarPorId(int id) throws DAOException;
    List<Pago> buscarPorReservaId(int reservaId) throws DAOException;
    List<Pago> buscarPorUsuario(int idUsuario) throws DAOException;
    void eliminarPorReserva(int reservaId) throws DAOException; // NUEVO
}