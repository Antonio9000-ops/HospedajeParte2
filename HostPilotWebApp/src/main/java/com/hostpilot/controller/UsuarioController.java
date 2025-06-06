package com.hostpilot.controller;

import com.hostpilot.exception.AppException; 
import com.hostpilot.model.Usuario;
import com.hostpilot.service.UsuarioService;
import com.hostpilot.service.UsuarioServiceImpl;
import com.hostpilot.security.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para las acciones del usuario una vez que ha iniciado sesión.

 */
public class UsuarioController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsuarioController.class.getName());
    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioServiceImpl();
    }

   
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        if (!SessionManager.validateSession(request)) {
            String errorMessage = URLEncoder.encode("Necesitas iniciar sesión para acceder a esta página.", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/login?error=" + errorMessage);
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "verperfil"; 
        }

        LOGGER.info("UsuarioController procesando acción: " + action);

        try {
            switch (action.trim().toLowerCase()) {
                case "verperfil":
                    mostrarPerfil(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "La acción solicitada no existe.");
            }
        } catch (AppException e) { // <<< Bloque catch actualizado
            LOGGER.log(Level.SEVERE, "Error de negocio en UsuarioController (action: " + action + ")", e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/error/500.jsp").forward(request, response); // O a una página de error genérica
        }
    }

    
    private void mostrarPerfil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, AppException { // <<< Lanza AppException
        
        Long userId = SessionManager.getCurrentUserId(request);
        if (userId == null) {
            throw new AppException("No se pudo encontrar el ID de usuario en la sesión.");
        }

        
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(userId);

        if (usuarioOpt.isPresent()) {
            request.setAttribute("usuarioPerfil", usuarioOpt.get());
            request.getRequestDispatcher("/WEB-INF/jsp/usuario/perfil.jsp").forward(request, response);
        } else {
            LOGGER.warning("Usuario con ID " + userId + " en sesión no fue encontrado en la BD. Invalidando sesión.");
            SessionManager.invalidateSession(request);
            String errorMessage = URLEncoder.encode("Tu perfil de usuario no fue encontrado. Por favor, inicia sesión de nuevo.", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/login?error=" + errorMessage);
        }
    }
}