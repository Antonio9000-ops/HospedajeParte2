package com.hostpilot.controller;

import com.hostpilot.exception.AppException; // <<< Import cambiado
import com.hostpilot.model.Usuario;
import com.hostpilot.service.UsuarioService;
import com.hostpilot.service.UsuarioServiceImpl;
import com.hostpilot.security.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para gestionar el proceso de inicio de sesión.

 */
public class LoginController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioServiceImpl();
    }

    /**
     * Muestra el formulario de inicio de sesión.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("GET /login: Mostrando formulario de inicio de sesión.");

        if (SessionManager.validateSession(request)) {
            LOGGER.info("Usuario ya autenticado. Redirigiendo a /bienvenido.jsp");
            response.sendRedirect(request.getContextPath() + "/bienvenido.jsp");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    /**
     * Procesa los datos del formulario para autenticar al usuario.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("POST /login: Procesando intento de autenticación.");

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        String forwardPageOnError = "/WEB-INF/jsp/login.jsp";

        try {
            Optional<Usuario> usuarioOpt = usuarioService.authenticate(email, password, request);

            if (usuarioOpt.isPresent()) {
                
                LOGGER.info("Autenticación exitosa para: " + usuarioOpt.get().getEmail());
                String redirectTo = getRedirectTarget(request);
                response.sendRedirect(redirectTo);
                
            } else {
                
                LOGGER.warning("Fallo en la autenticación para el email: " + email);
                request.setAttribute("error", "Email o contraseña incorrectos.");
                request.getRequestDispatcher(forwardPageOnError).forward(request, response);
            }

        } catch (AppException e) { // <<< Bloque catch actualizado
           
            LOGGER.log(Level.SEVERE, "Error durante la autenticación.", e);
            request.setAttribute("error", e.getMessage()); // Usamos el mensaje de nuestra AppException
            request.getRequestDispatcher(forwardPageOnError).forward(request, response);
        }
    }

    
    private String getRedirectTarget(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();
        String defaultTarget = contextPath + "/bienvenido.jsp";

        if (session != null) {
            String requestedURI = (String) session.getAttribute("requestedURI");
            if (requestedURI != null) {
                session.removeAttribute("requestedURI");
                
                // Evitar redirigir a la propia página de login/registro.
                if (!requestedURI.contains("/login") && !requestedURI.contains("/registro")) {
                    LOGGER.info("Redirigiendo a la URL guardada: " + requestedURI);
                    return requestedURI;
                }
            }
        }
        
        LOGGER.info("Redirigiendo al destino por defecto: " + defaultTarget);
        return defaultTarget;
    }
}