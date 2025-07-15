package com.hostpilot.dao;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.model.Pago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;

public class PagoDAOImpl implements PagoDAO {
    private static final Logger LOGGER = Logger.getLogger(PagoDAOImpl.class.getName());
    private DatabaseConfig dbConfig;

    public PagoDAOImpl(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public void crear(Pago pago) throws DAOException {
        String sql = "INSERT INTO pagos (reserva_id, metodo, monto, transaccion_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pago.getReservaId());
            stmt.setString(2, pago.getMetodo());
            stmt.setBigDecimal(3, pago.getMonto());
            stmt.setString(4, pago.getTransaccionId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("La creación del pago falló, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pago.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al intentar crear el pago.", e);
            throw new DAOException("Error de base de datos al intentar crear el pago.", e);
        }
    }

    @Override
    public Optional<Pago> buscarPorId(int id) throws DAOException {
        String sql = "SELECT id, reserva_id, metodo, monto, transaccion_id, fecha_pago FROM pagos WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPago(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al buscar pago por ID.", e);
            throw new DAOException("Error al buscar el pago por ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Pago> buscarPorReservaId(int reservaId) throws DAOException {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT id, reserva_id, metodo, monto, transaccion_id, fecha_pago FROM pagos WHERE reserva_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pagos.add(mapResultSetToPago(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al buscar pagos por ID de reserva.", e);
            throw new DAOException("Error al buscar pagos por ID de reserva.", e);
        }
        return pagos;
    }

    @Override
    public List<Pago> buscarPorUsuario(int idUsuario) throws DAOException {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT p.id, p.reserva_id, p.metodo, p.monto, p.transaccion_id, p.fecha_pago " +
                     "FROM pagos p JOIN reservas r ON p.reserva_id = r.id " +
                     "WHERE r.id_usuario = ? ORDER BY p.fecha_pago DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pagos.add(mapResultSetToPago(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al buscar pagos por usuario.", e);
            throw new DAOException("Error al buscar pagos por usuario.", e);
        }
        return pagos;
    }

    @Override
    public void eliminarPorReserva(int reservaId) throws DAOException { // NUEVO MÉTODO
        String sql = "DELETE FROM pagos WHERE reserva_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar pagos de la reserva ID: " + reservaId, e);
            throw new DAOException("Error al eliminar pagos de la reserva.", e);
        }
    }

    private Pago mapResultSetToPago(ResultSet rs) throws SQLException {
        Pago pago = new Pago();
        pago.setId(rs.getInt("id"));
        pago.setReservaId(rs.getInt("reserva_id"));
        pago.setMetodo(rs.getString("metodo"));
        pago.setMonto(rs.getBigDecimal("monto"));
        pago.setTransaccionId(rs.getString("transaccion_id"));
        
        Timestamp timestamp = rs.getTimestamp("fecha_pago");
        if (timestamp != null) {
            pago.setFechaPago(timestamp.toLocalDateTime());
        } else {
            pago.setFechaPago(null);
        }
        return pago;
    }
}