package com.hostpilot.dao;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.model.Reserva;
import com.hostpilot.model.Pago;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservaDAOImpl implements ReservaDAO {
    private static final Logger LOGGER = Logger.getLogger(ReservaDAOImpl.class.getName());
    private DatabaseConfig dbConfig;
    
    public ReservaDAOImpl(DatabaseConfig dbConfig) { 
        this.dbConfig = dbConfig; 
    }

    @Override
    public int crear(Reserva reserva) throws DAOException {
        String sql = "INSERT INTO reservas (id_usuario, id_propiedad, fecha_checkin, fecha_checkout, estado, total, numero_adultos, numero_ninos, numero_bebes, numero_mascotas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reserva.getIdUsuario());
            stmt.setInt(2, reserva.getIdPropiedad());
            stmt.setDate(3, Date.valueOf(reserva.getFechaCheckin()));
            stmt.setDate(4, Date.valueOf(reserva.getFechaCheckout()));
            stmt.setString(5, reserva.getEstado());
            stmt.setDouble(6, reserva.getTotal());
            stmt.setInt(7, reserva.getNumeroAdultos());
            stmt.setInt(8, reserva.getNumeroNinos());
            stmt.setInt(9, reserva.getNumeroBebes());
            stmt.setInt(10, reserva.getNumeroMascotas());
            
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
            LOGGER.log(Level.SEVERE, "Error de base de datos al intentar crear la reserva.", e);
            throw new DAOException("Error de base de datos al intentar crear la reserva.", e);
        }
    }
    
    @Override
    public Reserva buscarPorId(int idReserva) throws DAOException {
        String sql = "SELECT id, id_usuario, id_propiedad, fecha_reserva, fecha_checkin, fecha_checkout, estado, total, numero_adultos, numero_ninos, numero_bebes, numero_mascotas FROM reservas WHERE id = ?";
        Reserva reserva = null;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idReserva);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reserva = new Reserva();
                    reserva.setId(rs.getInt("id"));
                    reserva.setIdUsuario(rs.getInt("id_usuario"));
                    reserva.setIdPropiedad(rs.getInt("id_propiedad"));
                    Timestamp timestamp = rs.getTimestamp("fecha_reserva");
                    if (timestamp != null) {
                        reserva.setFechaReserva(timestamp.toLocalDateTime());
                    }
                    reserva.setFechaCheckin(rs.getDate("fecha_checkin").toLocalDate());
                    reserva.setFechaCheckout(rs.getDate("fecha_checkout").toLocalDate());
                    reserva.setEstado(rs.getString("estado"));
                    reserva.setTotal(rs.getDouble("total"));
                    reserva.setNumeroAdultos(rs.getInt("numero_adultos"));
                    reserva.setNumeroNinos(rs.getInt("numero_ninos"));
                    reserva.setNumeroBebes(rs.getInt("numero_bebes"));
                    reserva.setNumeroMascotas(rs.getInt("numero_mascotas"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al buscar reserva por ID.", e);
            throw new DAOException("Error al buscar la reserva por ID.", e);
        }
        return reserva;
    }

    @Override
    public void actualizar(Reserva reserva) throws DAOException {
        String sql = "UPDATE reservas SET fecha_checkin = ?, fecha_checkout = ?, estado = ?, total = ?, numero_adultos = ?, numero_ninos = ?, numero_bebes = ?, numero_mascotas = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(reserva.getFechaCheckin()));
            stmt.setDate(2, Date.valueOf(reserva.getFechaCheckout()));
            stmt.setString(3, reserva.getEstado());
            stmt.setDouble(4, reserva.getTotal());
            stmt.setInt(5, reserva.getNumeroAdultos());
            stmt.setInt(6, reserva.getNumeroNinos());
            stmt.setInt(7, reserva.getNumeroBebes());
            stmt.setInt(8, reserva.getNumeroMascotas());
            stmt.setInt(9, reserva.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("La actualización de la reserva falló, ninguna fila afectada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al actualizar la reserva.", e);
            throw new DAOException("Error al actualizar la reserva.", e);
        }
    }
    
    @Override
    public List<Reserva> buscarPorUsuario(int idUsuario) throws DAOException {
        // CORREGIDO: Filtrar por estado != 'CANCELADA'
        String sql = "SELECT id, id_usuario, id_propiedad, fecha_reserva, fecha_checkin, fecha_checkout, estado, total, numero_adultos, numero_ninos, numero_bebes, numero_mascotas FROM reservas WHERE id_usuario = ? AND estado != 'CANCELADA' ORDER BY fecha_checkin DESC";
        List<Reserva> reservas = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva();
                    reserva.setId(rs.getInt("id"));
                    reserva.setIdUsuario(rs.getInt("id_usuario"));
                    reserva.setIdPropiedad(rs.getInt("id_propiedad"));
                    Timestamp timestamp = rs.getTimestamp("fecha_reserva");
                    if (timestamp != null) {
                        reserva.setFechaReserva(timestamp.toLocalDateTime());
                    }
                    reserva.setFechaCheckin(rs.getDate("fecha_checkin").toLocalDate());
                    reserva.setFechaCheckout(rs.getDate("fecha_checkout").toLocalDate());
                    reserva.setEstado(rs.getString("estado"));
                    reserva.setTotal(rs.getDouble("total"));
                    reserva.setNumeroAdultos(rs.getInt("numero_adultos"));
                    reserva.setNumeroNinos(rs.getInt("numero_ninos"));
                    reserva.setNumeroBebes(rs.getInt("numero_bebes"));
                    reserva.setNumeroMascotas(rs.getInt("numero_mascotas"));
                    reservas.add(reserva);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al buscar reservas por usuario.", e);
            throw new DAOException("Error al buscar reservas por usuario.", e);
        }
        return reservas;
    }

    @Override
    public List<Pago> obtenerPagosPorReserva(int reservaId) throws DAOException {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT id, reserva_id, metodo, monto, transaccion_id, fecha_pago FROM pagos WHERE reserva_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pago pago = new Pago();
                    pago.setId(rs.getInt("id"));
                    pago.setReservaId(rs.getInt("reserva_id"));
                    pago.setMetodo(rs.getString("metodo"));
                    pago.setMonto(rs.getBigDecimal("monto"));
                    pago.setTransaccionId(rs.getString("transaccion_id"));
                    Timestamp timestamp = rs.getTimestamp("fecha_pago");
                    if (timestamp != null) {
                        pago.setFechaPago(timestamp.toLocalDateTime());
                    }
                    pagos.add(pago);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al obtener pagos por reserva ID.", e);
            throw new DAOException("Error al obtener pagos por reserva ID.", e);
        }
        return pagos;
    }

    @Override
    public void eliminar(int reservaId) throws DAOException { // NUEVO
        String sql = "DELETE FROM reservas WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar la reserva ID: " + reservaId, e);
            throw new DAOException("Error al eliminar la reserva.", e);
        }
    }
}