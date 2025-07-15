package com.hostpilot.service;

import com.hostpilot.model.Reserva;
import java.time.LocalDate;
import java.util.List;

public interface ReservaService {
    Reserva crearReserva(int idUsuario, int idPropiedad, LocalDate checkin, LocalDate checkout, int adultos, int ninos, int bebes, int mascotas) throws ServiceException;
    Reserva buscarReservaPorId(int idReserva) throws ServiceException;
    List<Reserva> buscarReservasPorUsuario(int idUsuario) throws ServiceException;
    void modificarReserva(Reserva reserva) throws ServiceException;
    
    boolean esReservaPagada(int reservaId) throws ServiceException;
    
    void cancelarReserva(int reservaId, int idUsuario) throws ServiceException;
    void eliminarReserva(int reservaId, int idUsuario) throws ServiceException; // NUEVO
}