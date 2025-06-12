package com.hostpilot.dao;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.model.Reserva;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOImpl implements ReservaDAO {
    private DatabaseConfig dbConfig;
    public ReservaDAOImpl(DatabaseConfig dbConfig) { this.dbConfig = dbConfig; }

    @Override
    public void crear(Reserva reserva) throws DAOException {
        String sql = "INSERT INTO reservas (id_usuario, id_propiedad, fecha_checkin, fecha_checkout) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reserva.getIdUsuario());
            stmt.setInt(2, reserva.getIdPropiedad());
            stmt.setDate(3, Date.valueOf(reserva.getFechaCheckin()));
            stmt.setDate(4, Date.valueOf(reserva.getFechaCheckout()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al crear la reserva", e);
        }
    }
    
    @Override
    public List<Reserva> buscarPorUsuario(int idUsuario) throws DAOException {
        
        return new ArrayList<>();
    }
}