package com.hostpilot.service;

import com.hostpilot.model.Usuario;
import com.hostpilot.dao.UsuarioDAO;
import com.hostpilot.dao.UsuarioDAOImpl;
import com.hostpilot.dao.DAOException;
import com.hostpilot.security.EncryptionService;
import com.hostpilot.security.BCryptEncryptionService;
import com.hostpilot.security.InputValidator;
import com.hostpilot.security.SessionManager;
import com.hostpilot.config.MySQLDatabaseConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());

    private final UsuarioDAO usuarioDAO;
    private final EncryptionService encryptionService;
    // InputValidator tiene métodos estáticos, no necesitamos una instancia.

    public AuthenticationService() {
        this.usuarioDAO = new UsuarioDAOImpl(new MySQLDatabaseConfig());
        this.encryptionService = new BCryptEncryptionService();
    }
    
    // Constructor para testing
    public AuthenticationService(UsuarioDAO usuarioDAO, EncryptionService encryptionService) {
        this.usuarioDAO = usuarioDAO;
        this.encryptionService = encryptionService;
    }

    public Usuario authenticate(String email, String password, HttpServletRequest request) throws ServiceException {
        if (email == null || !InputValidator.isValidEmail(email.trim())) {
            throw new ServiceException("Formato de email inválido.");
        }
        if (password == null || password.isEmpty()) {
            throw new ServiceException("La contraseña no puede estar vacía.");
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorEmail(email.trim().toLowerCase());

            if (!usuarioOpt.isPresent() || !usuarioOpt.get().isActivo()) {
                LOGGER.warning("Intento de login fallido para " + email + " (usuario no encontrado o inactivo).");
                return null; // No dar pistas de si el usuario existe o no.
            }

            Usuario usuario = usuarioOpt.get();

            if (!encryptionService.verifyPassword(password, usuario.getPassword())) {
                LOGGER.warning("Contraseña incorrecta para usuario: " + email);
                return null;
            }

            // --- Autenticación Exitosa ---
            LOGGER.info("Autenticación exitosa para usuario: " + email);
            
            // Actualizar último acceso y persistir
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioDAO.actualizar(usuario);

            // Crear sesión segura
            HttpSession session = request.getSession(true);
            SessionManager.createUserSession(session, usuario, request);

            return usuario;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error de DAO durante la autenticación para " + email, e);
            throw new ServiceException("Error interno del sistema durante la autenticación.", e);
        }
    }
}