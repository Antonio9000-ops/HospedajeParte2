package com.hostpilot.controller;

import com.hostpilot.model.Usuario;
import com.hostpilot.service.AuthenticationService;
import com.hostpilot.service.ServiceException;
import com.hostpilot.security.SessionManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private AuthenticationService authService;

    @Override
    public void init() {
        this.authService = new AuthenticationService(); 
        LOGGER.info("LoginController inicializado.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (SessionManager.validateSession(request)) {
            LOGGER.info("Usuario ya autenticado. Redirigiendo a la página de inicio.");
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        LOGGER.debug("Mostrando formulario de inicio de sesión.");
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String forwardPageOnError = "/WEB-INF/jsp/login.jsp";

        LOGGER.info("Procesando intento de login para el email: {}", email);

        // Validación de entrada usando Apache Commons
        if (StringUtils.isAnyBlank(email, password)) {
            LOGGER.warn("Intento de login con email o contraseña vacíos.");
            request.setAttribute("error", "Email y contraseña son requeridos.");
            request.getRequestDispatcher(forwardPageOnError).forward(request, response);
            return;
        }
        
        email = email.trim().toLowerCase();

        try {
            Usuario usuarioAutenticado = authService.authenticate(email, password, request);

            if (usuarioAutenticado != null) {
                LOGGER.info("Autenticación exitosa para: {}. Rol: {}", email, usuarioAutenticado.getRol());
                String targetURL = getRedirectTarget(request);
                response.sendRedirect(targetURL);
            } else {
                LOGGER.warn("Autenticación fallida para: {}", email);
                request.setAttribute("error", "Credenciales inválidas o cuenta inactiva.");
                request.getRequestDispatcher(forwardPageOnError).forward(request, response);
            }
        } catch (ServiceException e) {
            LOGGER.warn("Error de servicio durante la autenticación para {}: {}", email, e.getMessage());
            request.setAttribute("error", "Error durante el login: " + e.getMessage());
            request.getRequestDispatcher(forwardPageOnError).forward(request, response);
        } catch (Exception e) {
            LOGGER.error("Error inesperado durante el login para {}", email, e);
            request.setAttribute("error", "Ocurrió un error inesperado. Por favor, intente de nuevo.");
            request.getRequestDispatcher(forwardPageOnError).forward(request, response);
        }
    }

    private String getRedirectTarget(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();
        String defaultTarget = contextPath + "/";

        if (session != null) {
            String requestedURI = (String) session.getAttribute("requestedURI");
            if (StringUtils.isNotBlank(requestedURI)) {
                session.removeAttribute("requestedURI");
                if (!requestedURI.contains("/login") && !requestedURI.contains("/registro")) {
                    LOGGER.debug("Redirigiendo a la URL guardada: {}", requestedURI);
                    return requestedURI;
                }
            }
        }
        
        LOGGER.debug("Redirigiendo al destino por defecto: {}", defaultTarget);
        return defaultTarget;
    }
}