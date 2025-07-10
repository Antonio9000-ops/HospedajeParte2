package com.hostpilot.dao;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.model.Propiedad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PropiedadDAOImpl implements PropiedadDAO {
    
    private final DatabaseConfig dbConfig;

    public PropiedadDAOImpl(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
    
    public PropiedadDAOImpl() {
        this.dbConfig = new MySQLDatabaseConfig();
    }

    @Override
    public Optional<Propiedad> buscarPorId(long id) throws DAOException {
        // ... (este método no necesita cambios) ...
        String sql = "SELECT * FROM propiedades WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPropiedad(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar propiedad por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Propiedad> obtenerTodas() throws DAOException {
        List<Propiedad> propiedades = new ArrayList<>();
        // ====================== CORRECCIÓN EN LA CONSULTA SQL ======================
        String sql = "SELECT id, anfitrion_id, titulo, descripcion, direccion, ciudad, " +
                     "precio_por_noche, capacidad, tipo, img_url, lat, lng, rating, reviews " + // <-- Usamos 'reviews'
                     "FROM propiedades ORDER BY id ASC";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                propiedades.add(mapResultSetToPropiedad(rs));
            }
        } catch (SQLException e) {
            // Envolvemos la excepción original para no perder la causa raíz del error
            throw new DAOException("Error de base de datos al obtener todas las propiedades", e);
        }
        return propiedades;
    }
    
    @Override
public boolean eliminar(long id) throws DAOException {
    String sql = "DELETE FROM propiedades WHERE id = ?";
    
    try (Connection conn = dbConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setLong(1, id);
        
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0; // Devuelve true si se eliminó al menos 1 fila

    } catch (SQLException e) {
        // Podría fallar si, por ejemplo, hay reservas asociadas a esta propiedad
        // y tienes una restricción de clave foránea en la tabla de reservas.
        throw new DAOException("Error de base de datos al eliminar la propiedad con ID: " + id, e);
    }
}
    
    @Override
public boolean actualizar(Propiedad propiedad) throws DAOException {
    String sql = "UPDATE propiedades SET " +
                 "anfitrion_id = ?, titulo = ?, descripcion = ?, direccion = ?, ciudad = ?, " +
                 "precio_por_noche = ?, capacidad = ?, tipo = ?, img_url = ?, " +
                 "lat = ?, lng = ? " + // Los campos 'rating' y 'reviews' no suelen ser editables directamente
                 "WHERE id = ?";

    try (Connection conn = dbConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, propiedad.getAnfitrionId());
        stmt.setString(2, propiedad.getTitulo());
        stmt.setString(3, propiedad.getDescripcion());
        stmt.setString(4, propiedad.getDireccion());
        stmt.setString(5, propiedad.getCiudad());
        stmt.setDouble(6, propiedad.getPrecioPorNoche());
        stmt.setInt(7, propiedad.getCapacidad());
        stmt.setString(8, propiedad.getTipo());
        stmt.setString(9, propiedad.getImgUrl());
        stmt.setDouble(10, propiedad.getLat());
        stmt.setDouble(11, propiedad.getLng());
        stmt.setInt(12, propiedad.getId()); // ID para la cláusula WHERE

        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0; // Devuelve true si se actualizó al menos 1 fila

    } catch (SQLException e) {
        throw new DAOException("Error de base de datos al actualizar la propiedad con ID: " + propiedad.getId(), e);
    }
}

    @Override
    public long crear(Propiedad propiedad) throws DAOException {
        // ====================== CORRECCIÓN EN LA CONSULTA SQL ======================
        String sql = "INSERT INTO propiedades (anfitrion_id, titulo, descripcion, direccion, ciudad, precio_por_noche, " +
                     "capacidad, tipo, img_url, lat, lng, rating, reviews) " + // <-- Usamos 'reviews'
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, propiedad.getAnfitrionId());
            stmt.setString(2, propiedad.getTitulo());
            stmt.setString(3, propiedad.getDescripcion());
            stmt.setString(4, propiedad.getDireccion());
            stmt.setString(5, propiedad.getCiudad());
            stmt.setDouble(6, propiedad.getPrecioPorNoche());
            stmt.setInt(7, propiedad.getCapacidad());
            stmt.setString(8, propiedad.getTipo());
            stmt.setString(9, propiedad.getImgUrl());
            stmt.setDouble(10, propiedad.getLat());
            stmt.setDouble(11, propiedad.getLng());
            stmt.setDouble(12, propiedad.getRating());
            stmt.setInt(13, propiedad.getReviews()); // <-- Usamos getReviews()
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("La creación de la propiedad falló, no se afectaron filas.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new DAOException("La creación de la propiedad falló, no se obtuvo el ID.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error de base de datos al crear la propiedad", e);
        }
    }
    
    private Propiedad mapResultSetToPropiedad(ResultSet rs) throws SQLException {
        Propiedad prop = new Propiedad();
        prop.setId(rs.getInt("id"));
        prop.setAnfitrionId(rs.getInt("anfitrion_id"));
        prop.setTitulo(rs.getString("titulo"));
        prop.setDescripcion(rs.getString("descripcion"));
        prop.setDireccion(rs.getString("direccion"));
        prop.setCiudad(rs.getString("ciudad"));
        prop.setPrecioPorNoche(rs.getDouble("precio_por_noche"));
        prop.setCapacidad(rs.getInt("capacidad"));
        prop.setTipo(rs.getString("tipo"));
        prop.setImgUrl(rs.getString("img_url"));
        prop.setLat(rs.getDouble("lat"));
        prop.setLng(rs.getDouble("lng"));
        prop.setRating(rs.getDouble("rating"));
        // ====================== CORRECCIÓN EN EL MAPEO ======================
        prop.setReviews(rs.getInt("reviews")); // <-- Usamos setReviews() y leemos la columna 'reviews'

        return prop;
    }
}