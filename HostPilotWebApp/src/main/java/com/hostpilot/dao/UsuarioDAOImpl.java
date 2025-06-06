package com.hostpilot.dao;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.model.Usuario;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación del DAO para la entidad Usuario usando JDBC y MySQL.
 
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final Logger LOGGER = Logger.getLogger(UsuarioDAOImpl.class.getName());
    private final DatabaseConfig databaseConfig;

    //Constantes para SQL
    private static final String TABLE_NAME = "usuarios";
    private static final String ALL_COLUMNS = "id, nombre, apellido, email, password, rol, edad, genero, telefono, activo, fecha_creacion, fecha_modificacion, ultimo_acceso";
    
    private static final String INSERT_USUARIO = "INSERT INTO " + TABLE_NAME + " (nombre, apellido, email, password, rol, edad, genero, telefono, activo, fecha_creacion, fecha_modificacion, ultimo_acceso) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_USUARIO = "UPDATE " + TABLE_NAME + " SET nombre=?, apellido=?, email=?, rol=?, edad=?, genero=?, telefono=?, activo=?, fecha_modificacion=?, ultimo_acceso=? WHERE id=?";
    private static final String SELECT_BY_ID = "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE id = ?";
    private static final String SELECT_BY_EMAIL = "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE email = ?";
    private static final String SELECT_ALL = "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " ORDER BY apellido, nombre";
    private static final String SELECT_ACTIVE = "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE activo = true ORDER BY apellido, nombre";
    private static final String SELECT_BY_ROL = "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE rol = ? ORDER BY apellido, nombre";
    private static final String DEACTIVATE_BY_ID = "UPDATE " + TABLE_NAME + " SET activo = false, fecha_modificacion = ? WHERE id = ?";
    private static final String REACTIVATE_BY_ID = "UPDATE " + TABLE_NAME + " SET activo = true, fecha_modificacion = ? WHERE id = ?";
    
    public UsuarioDAOImpl(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Override
    public Usuario crear(Usuario usuario) throws DAOException {
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USUARIO, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellido());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getPassword());
            stmt.setString(5, usuario.getRol());
            stmt.setObject(6, usuario.getEdad(), Types.INTEGER);
            stmt.setString(7, usuario.getGenero());
            stmt.setString(8, usuario.getTelefono());
            stmt.setBoolean(9, usuario.isActivo());
            stmt.setTimestamp(10, Timestamp.valueOf(usuario.getFechaCreacion()));
            stmt.setTimestamp(11, Timestamp.valueOf(usuario.getFechaModificacion()));
            stmt.setTimestamp(12, (usuario.getUltimoAcceso() != null) ? Timestamp.valueOf(usuario.getUltimoAcceso()) : null);

            if (stmt.executeUpdate() == 0) {
                throw new DAOException("La creación del usuario falló, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                } else {
                    throw new DAOException("La creación del usuario falló, no se pudo obtener el ID generado.");
                }
            }
            
            LOGGER.info("Usuario creado exitosamente con ID: " + usuario.getId());
            return usuario;

        } catch (SQLException e) {
            throw new DAOException("Error de base de datos al crear el usuario.", e);
        }
    }

    @Override
    public Usuario actualizar(Usuario usuario) throws DAOException {
        // La contraseña no se actualiza aquí para evitar sobrescribirla accidentalmente
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USUARIO)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellido());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getRol());
            stmt.setObject(5, usuario.getEdad(), Types.INTEGER);
            stmt.setString(6, usuario.getGenero());
            stmt.setString(7, usuario.getTelefono());
            stmt.setBoolean(8, usuario.isActivo());
            stmt.setTimestamp(9, Timestamp.valueOf(usuario.getFechaModificacion()));
            stmt.setTimestamp(10, (usuario.getUltimoAcceso() != null) ? Timestamp.valueOf(usuario.getUltimoAcceso()) : null);
            stmt.setLong(11, usuario.getId());

            stmt.executeUpdate();
            LOGGER.info("Usuario actualizado exitosamente con ID: " + usuario.getId());
            return usuario;

        } catch (SQLException e) {
            throw new DAOException("Error de base de datos al actualizar el usuario con ID: " + usuario.getId(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) throws DAOException {
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error de base de datos al buscar usuario por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) throws DAOException {
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_EMAIL)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error de base de datos al buscar usuario por email: " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> obtenerTodos() throws DAOException {
        return findMany(SELECT_ALL);
    }
    
    @Override
    public List<Usuario> obtenerUsuariosActivos() throws DAOException {
        return findMany(SELECT_ACTIVE);
    }

    @Override
    public List<Usuario> buscarPorRol(String rol) throws DAOException {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ROL)) {
            stmt.setString(1, rol);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error de base de datos al buscar usuarios por rol: " + rol, e);
        }
        return usuarios;
    }

    @Override
    public boolean desactivar(Long id) throws DAOException {
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DEACTIVATE_BY_ID)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error de base de datos al desactivar usuario ID: " + id, e);
        }
    }

    @Override
    public boolean reactivar(Long id) throws DAOException {
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(REACTIVATE_BY_ID)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error de base de datos al reactivar usuario ID: " + id, e);
        }
    }

    /**
     * Método de ayuda genérico para consultas SELECT que devuelven una lista de usuarios.
   
     */
    private List<Usuario> findMany(String sql) throws DAOException {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error de base de datos al ejecutar consulta de lista.", e);
        }
        return usuarios;
    }

    /**
     * Convierte una fila de un ResultSet a un objeto Usuario.
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setRol(rs.getString("rol"));
        usuario.setEdad(rs.getObject("edad", Integer.class));
        usuario.setGenero(rs.getString("genero"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setActivo(rs.getBoolean("activo"));
        
        Timestamp tsCreacion = rs.getTimestamp("fecha_creacion");
        if (tsCreacion != null) {
            usuario.setFechaCreacion(tsCreacion.toLocalDateTime());
        }

        Timestamp tsModificacion = rs.getTimestamp("fecha_modificacion");
        if (tsModificacion != null) {
            usuario.setFechaModificacion(tsModificacion.toLocalDateTime());
        }

        Timestamp tsUltimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (tsUltimoAcceso != null) {
            usuario.setUltimoAcceso(tsUltimoAcceso.toLocalDateTime());
        }
        
        return usuario;
    }
}