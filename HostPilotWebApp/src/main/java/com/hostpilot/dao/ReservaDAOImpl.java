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
    public int crear(Reserva reserva) throws DAOException {
        String sql = "INSERT INTO reservas (id_usuario, id_propiedad, fecha_checkin, fecha_checkout, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reserva.getIdUsuario());
            stmt.setInt(2, reserva.getIdPropiedad());
            stmt.setDate(3, Date.valueOf(reserva.getFechaCheckin()));
            stmt.setDate(4, Date.valueOf(reserva.getFechaCheckout()));
            stmt.setString(5, reserva.getEstado());
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("La creación de la reserva falló, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("La creación de la reserva falló, no se obtuvo el ID.");
                }
            }
        } catch (SQLException e) {
            // Loggear el error específico de SQL puede ser útil
            // Logger.getLogger(ReservaDAOImpl.class.getName()).log(Level.SEVERE, "Error SQL al crear reserva", e);
            throw new DAOException("Error de base de datos al intentar crear la reserva.", e);
        }
    }
    
    @Override
    public List<Reserva> buscarPorUsuario(int idUsuario) throws DAOException {
        
        return new ArrayList<>();
    }
}