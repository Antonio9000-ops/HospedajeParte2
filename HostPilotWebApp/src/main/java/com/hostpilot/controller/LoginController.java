package com.hostpilot.controller;

import com.hostpilot.model.Usuario;
import com.hostpilot.service.AuthenticationService;
import com.hostpilot.service.ServiceException; // Import correcto
import com.hostpilot.security.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    
    // Declaración del servicio de autenticación
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Se instancia el servicio cuando el servlet se inicializa por primera vez
        this.authService = new AuthenticationService(); 
    }

    /**
     * Muestra el formulario de inicio de sesión si no hay una sesión activa.
     * Si ya hay una sesión, redirige a la página principal.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("GET /login: Mostrando formulario de inicio de sesión.");

        // Si el usuario ya tiene una sesión válida, no necesita ver el login.
        // Lo redirigimos a la página de inicio.
        if (SessionManager.validateSession(request)) {
            LOGGER.info("Usuario ya autenticado. Redirigiendo a la página de inicio (raíz).");
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // Si no hay sesión, mostramos el JSP de login.
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    /**
     * Procesa los datos del formulario de inicio de sesión.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("POST /login: Procesando intento de login.");

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String forwardPageOnError = "/WEB-INF/jsp/login.jsp";

        // Validación de entrada básica
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            LOGGER.warning("Email o contraseña vacíos en el formulario de login.");
            request.setAttribute("error", "Email y contraseña son requeridos.");
            request.getRequestDispatcher(forwardPageOnError).forward(request, response);
            return;
        }
        email = email.trim().toLowerCase();

        try {
            // Se usa la instancia de authService creada en init()
            Usuario usuarioAutenticado = authService.authenticate(email, password, request);

            if (usuarioAutenticado != null) {
                LOGGER.info("Autenticación exitosa para: " + email + ". Rol: " + usuarioAutenticado.getRol());
                
                // Lógica de redirección después del login
                String targetURL = getRedirectTarget(request);
                response.sendRedirect(targetURL);

            } else {
                LOGGER.warning("Autenticación fallida para: " + email);
                request.setAttribute("error", "Credenciales inválidas o cuenta inactiva.");
                request.getRequestDispatcher(forwardPageOnError).forward(request, response);
            }
        } catch (ServiceException e) { // Captura de la excepción de servicio
            LOGGER.log(Level.WARNING, "Error de servicio durante la autenticación: " + e.getMessage());
            request.setAttribute("error", "Error durante el login: " + e.getMessage());
            request.getRequestDispatcher(forwardPageOnError).forward(request, response);
        } catch (Exception e) { // Captura para cualquier otro error inesperado
            LOGGER.log(Level.SEVERE, "Error inesperado durante el login: " + e.getMessage(), e);
            request.setAttribute("error", "Ocurrió un error inesperado. Por favor, intente de nuevo.");
            request.getRequestDispatcher(forwardPageOnError).forward(request, response);
        }
    }

    /**
     * Determina a qué URL redirigir al usuario después de un login exitoso.
     * Si hay una URL guardada en sesión (porque el usuario intentó acceder a una página protegida),
     * lo redirige allí. Si no, lo envía a la página de inicio por defecto.
     * @param request La solicitud HTTP
     * @return La URL a la que se debe redirigir.
     */
    private String getRedirectTarget(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();
        String defaultTarget = contextPath + "/"; // Destino por defecto: la página de inicio

        if (session != null) {
            String requestedURI = (String) session.getAttribute("requestedURI");
            if (requestedURI != null && !requestedURI.isEmpty()) {
                session.removeAttribute("requestedURI"); // Limpiar para futuros logins
                
                // Evitar redirigir de nuevo a las páginas de login/registro
                if (!requestedURI.contains("/login") && !requestedURI.contains("/registro")) {
                    LOGGER.info("Redirigiendo a la URL guardada: " + requestedURI);
                    return requestedURI;
                }
            }
        }
        
        LOGGER.info("Redirigiendo al destino por defecto: " + defaultTarget);
        return defaultTarget;
    }

    @Override
    public String getServletInfo() {
        return "Controlador para gestionar el proceso de inicio de sesión de los usuarios.";
    }
}