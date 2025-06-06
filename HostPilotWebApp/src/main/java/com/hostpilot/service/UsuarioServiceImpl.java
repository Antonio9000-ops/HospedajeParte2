package com.hostpilot.service;

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.UsuarioDAO;
import com.hostpilot.dao.UsuarioDAOImpl;
import com.hostpilot.exception.AppException; 
import com.hostpilot.model.Usuario;
import com.hostpilot.security.BCryptEncryptionService;
import com.hostpilot.security.EncryptionService;
import com.hostpilot.security.InputValidator;
import com.hostpilot.security.SessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación de la interfaz UsuarioService.
 
 */
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger LOGGER = Logger.getLogger(UsuarioServiceImpl.class.getName());

    private final UsuarioDAO usuarioDAO;
    private final EncryptionService encryptionService;

    public UsuarioServiceImpl() {
        DatabaseConfig dbConfig = new MySQLDatabaseConfig();
        this.usuarioDAO = new UsuarioDAOImpl(dbConfig);
        this.encryptionService = new BCryptEncryptionService();
    }

    public UsuarioServiceImpl(UsuarioDAO usuarioDAO, EncryptionService encryptionService) {
        this.usuarioDAO = usuarioDAO;
        this.encryptionService = encryptionService;
    }



    @Override
    public Optional<Usuario> authenticate(String email, String password, HttpServletRequest request) throws AppException {
        if (!InputValidator.isValidEmail(email) || !InputValidator.isNotEmpty(password)) {
            LOGGER.warning("Intento de autenticación con email o contraseña inválidos.");
            return Optional.empty();
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorEmail(email.toLowerCase());
            if (!usuarioOpt.isPresent()) {
                LOGGER.warning("Intento de login para un usuario no existente: " + email);
                return Optional.empty();
            }

            Usuario usuario = usuarioOpt.get();
            if (usuario.isActivo() && encryptionService.verifyPassword(password, usuario.getPassword())) {
                LOGGER.info("Autenticación exitosa para: " + email);
                usuario.setUltimoAcceso(LocalDateTime.now());
                usuarioDAO.actualizar(usuario);
                
                HttpSession session = request.getSession(true);
                SessionManager.createUserSession(session, usuario, request);
                
                return Optional.of(usuario);
            } else {
                LOGGER.warning("Fallo de autenticación para: " + email + " (contraseña incorrecta o usuario inactivo)");
                return Optional.empty();
            }

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error de DAO durante la autenticación", e);
            throw new AppException("Error interno del sistema. Por favor, intente de nuevo.", e);
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        SessionManager.invalidateSession(request);
    }

    // Metodos de Gestion de Usuarios (CRUD) 

    @Override
    public Usuario registrarUsuario(Usuario usuario) throws AppException {
        validarDatosUsuario(usuario);
        try {
            if (usuarioDAO.buscarPorEmail(usuario.getEmail().toLowerCase()).isPresent()) {
                throw new AppException("El email '" + usuario.getEmail() + "' ya está registrado.");
            }
            usuario.setPassword(encryptionService.encryptPassword(usuario.getPassword()));
            usuario.setEmail(usuario.getEmail().toLowerCase());
            usuario.setRol("USER");
            usuario.setActivo(true);
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setFechaModificacion(LocalDateTime.now());
            
            return usuarioDAO.crear(usuario);
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error de DAO al registrar usuario", e);
            throw new AppException("Error de base de datos al registrar el usuario.", e);
        }
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) throws AppException {
        if (usuario == null || usuario.getId() == null) {
            throw new AppException("Se requiere un usuario con ID para poder actualizar.");
        }
        
        try {
            usuario.setFechaModificacion(LocalDateTime.now());
            return usuarioDAO.actualizar(usuario);
        } catch (DAOException e) {
            throw new AppException("Error de base de datos al actualizar el usuario.", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) throws AppException {
        try {
            return usuarioDAO.buscarPorId(id);
        } catch (DAOException e) {
            throw new AppException("Error de base de datos al buscar usuario por ID.", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) throws AppException {
        if (!InputValidator.isValidEmail(email)) {
            return Optional.empty();
        }
        try {
            return usuarioDAO.buscarPorEmail(email.toLowerCase());
        } catch (DAOException e) {
            throw new AppException("Error de base de datos al buscar usuario por email.", e);
        }
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() throws AppException {
        try {
            return usuarioDAO.obtenerTodos();
        } catch (DAOException e) {
            throw new AppException("Error de base de datos al obtener la lista de usuarios.", e);
        }
    }
    
   

    /**
     * Valida los datos de un objeto Usuario antes de una operación de creación.
    
     */
    private void validarDatosUsuario(Usuario usuario) throws AppException {
        if (usuario == null) {
            throw new AppException("El objeto de usuario no puede ser nulo.");
        }
        if (!InputValidator.isNotEmpty(usuario.getNombre())) {
            throw new AppException("El nombre es obligatorio.");
        }
        if (!InputValidator.isNotEmpty(usuario.getApellido())) {
            throw new AppException("El apellido es obligatorio.");
        }
        if (!InputValidator.isValidEmail(usuario.getEmail())) {
            throw new AppException("El formato del email no es válido.");
        }
        if (usuario.getPassword() == null || !InputValidator.isValidPassword(usuario.getPassword())) {
            throw new AppException("La contraseña es obligatoria y debe cumplir los requisitos de seguridad (mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número).");
        }
    }
}