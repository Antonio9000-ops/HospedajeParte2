package com.hostpilot.dao;

import com.hostpilot.model.Reserva;
import java.util.List;

public interface ReservaDAO {
    void crear(Reserva reserva) throws DAOException;
    List<Reserva> buscarPorUsuario(int idUsuario) throws DAOException;
}