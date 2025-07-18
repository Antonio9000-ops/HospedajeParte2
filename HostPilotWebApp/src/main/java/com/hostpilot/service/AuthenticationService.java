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

    /**
     * Autentica a un usuario, actualiza su último acceso y crea una sesión segura.
     * @param email Email del usuario.
     * @param password Contraseña sin encriptar.
     * @param request El HttpServletRequest para obtener/crear la sesión.
     * @return El objeto Usuario si la autenticación es exitosa, de lo contrario null.
     * @throws ServiceException Si ocurre un error de validación o de sistema.
     */
    public Usuario authenticate(String email, String password, HttpServletRequest request) throws ServiceException {
        if (email == null || !InputValidator.isValidEmail(email.trim())) {
            LOGGER.warning("Intento de login con email inválido: " + email);
            throw new ServiceException("Formato de email inválido.");
        }
        if (password == null || password.isEmpty()) {
            throw new ServiceException("La contraseña no puede estar vacía.");
        }

        try {
            // Buscamos al usuario por su email
            Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorEmail(email.trim().toLowerCase());

            // Si el usuario no existe o no está activo, retornamos null
            if (!usuarioOpt.isPresent() || !usuarioOpt.get().isActivo()) {
                LOGGER.warning("Intento de login fallido para " + email + " (usuario no encontrado o inactivo).");
                return null; // No dar pistas de si el usuario existe o no.
            }

            Usuario usuario = usuarioOpt.get();

            // Verificamos la contraseña
            if (!encryptionService.verifyPassword(password, usuario.getPassword())) {
                LOGGER.warning("Contraseña incorrecta para usuario: " + email);
                return null;
            }

            // --- Autenticación Exitosa ---
            LOGGER.info("Autenticación exitosa para usuario: " + email + " con rol: " + usuario.getRol());
            
            // Actualizamos la fecha del último acceso en la base de datos
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioDAO.actualizar(usuario); // Nota: Asegúrate que este `actualizar` no requiera la contraseña.

            // --- CREACIÓN DE LA SESIÓN ---
            // Obtenemos la sesión (la crea si no existe)
            HttpSession session = request.getSession(true);
            
            // Guardamos los datos esenciales del usuario en la sesión.
            // Esta es la parte central que conecta el backend con el frontend (JSP).
            session.setAttribute("userId", usuario.getId());
            session.setAttribute("userEmail", usuario.getEmail());
            session.setAttribute("userName", usuario.getNombre());
            
            // ====================== ¡LÍNEA CLAVE! ======================
            // Guardamos el ROL del usuario en la sesión.
            session.setAttribute("userRole", usuario.getRol());
            // ==========================================================

            // (Opcional) Si tu SessionManager hace más cosas, puedes llamarlo aquí también.
            // SessionManager.createUserSession(session, usuario, request);

            return usuario;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error de DAO durante la autenticación para " + email, e);
            throw new ServiceException("Error interno del sistema durante la autenticación.", e);
        }
    }
}