package com.hostpilot.controller;

import com.hostpilot.exception.AppException; 
import com.hostpilot.model.Usuario;
import com.hostpilot.service.UsuarioService;
import com.hostpilot.service.UsuarioServiceImpl;
import com.hostpilot.security.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para las acciones del usuario una vez que ha iniciado sesión,
 * como ver su perfil.
 */
@WebServlet("/usuario")
public class UsuarioController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsuarioController.class.getName());
    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioServiceImpl();
    }

    /**
     * Usa 'service' para validar la sesión antes de delegar a doGet o doPost.
     * En este caso, todas las acciones se manejan aquí.
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // 1. Validar que exista una sesión activa
        if (!SessionManager.validateSession(request)) {
            String errorMessage = URLEncoder.encode("Necesitas iniciar sesión para acceder a esta página.", StandardCharsets.UTF_8.toString());
            response.sendRedirect(request.getContextPath() + "/login?error=" + errorMessage);
            return;
        }
        
        // 2. Obtener la acción del request
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            // Si no se especifica acción, por defecto mostramos el perfil.
            action = "perfil"; 
        }

        LOGGER.info("UsuarioController procesando acción: " + action);

        try {
            // 3. Delegar a la función correspondiente
            switch (action.trim().toLowerCase()) {
                case "perfil": // <-- CORRECCIÓN 1: Ahora coincide con el enlace del header
                    mostrarPerfil(request, response);
                    break;
                // case "editarperfil":
                //     mostrarFormularioEdicion(request, response);
                //     break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "La acción solicitada no existe.");
            }
        } catch (AppException e) {
            LOGGER.log(Level.SEVERE, "Error de negocio en UsuarioController (action: " + action + ")", e);
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * Obtiene los datos del usuario logueado y los muestra en la página de perfil.
     */
    private void mostrarPerfil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, AppException {
        
        Long userId = SessionManager.getCurrentUserId(request);
        if (userId == null) {
            // Esto no debería pasar si SessionManager.validateSession() funciona, pero es una buena defensa.
            throw new AppException("No se pudo encontrar el ID de usuario en la sesión.");
        }
        
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(userId);

        if (usuarioOpt.isPresent()) {
            // <-- CORRECCIÓN 2: Usamos el nombre "usuario" que espera la JSP
            request.setAttribute("usuario", usuarioOpt.get()); 
            request.getRequestDispatcher("/WEB-INF/jsp/usuario/perfil.jsp").forward(request, response);
        } else {
            LOGGER.warning("Usuario con ID " + userId + " en sesión no fue encontrado en la BD. Invalidando sesión.");
            SessionManager.invalidateSession(request);
            String errorMessage = URLEncoder.encode("Tu perfil de usuario no fue encontrado. Por favor, inicia sesión de nuevo.", StandardCharsets.UTF_8.toString());
            response.sendRedirect(request.getContextPath() + "/login?error=" + errorMessage);
        }
    }
}