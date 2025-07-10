package com.hostpilot.dao;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.model.Propiedad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de PropiedadDAO para una base de datos MySQL.
 */
public class PropiedadDAOImpl implements PropiedadDAO {
    
    private final DatabaseConfig dbConfig;

    // Constructor que recibe la configuración de la base de datos (para testing).
    public PropiedadDAOImpl(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
    
    // Constructor por defecto para facilitar la instanciación en los Servlets.
    public PropiedadDAOImpl() {
        this.dbConfig = new MySQLDatabaseConfig();
    }

    @Override
    public Optional<Propiedad> buscarPorId(long id) throws DAOException {
        // CORRECCIÓN: Usando los nombres de columna correctos de tu BD.
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
    public List<Propiedad> buscarTodas() throws DAOException {
        List<Propiedad> propiedades = new ArrayList<>();
        
        // --- CORRECCIÓN: Consulta SQL con TODOS los nombres de columna CORRECTOS de tu BD ---
        // Se asume que has añadido las columnas que faltaban a tu tabla.
        String sql = "SELECT id, anfitrion_id, titulo, descripcion, direccion, ciudad, " +
                     "precio_por_noche, capacidad, tipo, img_url, lat, lng, rating, reviews " +
                     "FROM propiedades";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                propiedades.add(mapResultSetToPropiedad(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error de base de datos al buscar todas las propiedades", e);
        }
        return propiedades;
    }

    @Override
    public long crear(Propiedad propiedad) throws DAOException {
        // CORRECCIÓN: Usando los nombres de columna y getters correctos
        String sql = "INSERT INTO propiedades (anfitrion_id, titulo, descripcion, direccion, ciudad, precio_por_noche, " +
                     "capacidad, tipo, img_url, lat, lng, rating, reviews) " +
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
            stmt.setInt(13, propiedad.getReviews());
            
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
    
    /**
     * Método de utilidad para mapear una fila del ResultSet a un objeto Propiedad.
     */
    private Propiedad mapResultSetToPropiedad(ResultSet rs) throws SQLException {
        Propiedad prop = new Propiedad();
        
        // --- CORRECCIÓN: Mapeo con los nombres de columna y setters CORRECTOS ---
        prop.setId(rs.getInt("id"));
        prop.setAnfitrionId(rs.getInt("anfitrion_id"));
        prop.setTitulo(rs.getString("titulo"));
        prop.setDescripcion(rs.getString("descripcion"));
        prop.setDireccion(rs.getString("direccion"));
        prop.setCiudad(rs.getString("ciudad"));
        prop.setPrecioPorNoche(rs.getDouble("precio_por_noche"));
        prop.setCapacidad(rs.getInt("capacidad"));
        prop.setTipo(rs.getString("tipo"));
        
        // Estos campos deben existir en tu tabla para que no falle el 'rs.get...'
        prop.setImgUrl(rs.getString("img_url"));
        prop.setLat(rs.getDouble("lat"));
        prop.setLng(rs.getDouble("lng"));
        prop.setRating(rs.getDouble("rating"));
        prop.setReviews(rs.getInt("reviews"));

        return prop;
    }
}