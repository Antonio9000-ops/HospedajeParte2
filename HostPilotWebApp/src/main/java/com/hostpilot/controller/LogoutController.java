package com.hostpilot.controller;

import com.hostpilot.security.SessionManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutController.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }

    private void processLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        LOGGER.info("Procesando solicitud de logout.");
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userEmail = (String) session.getAttribute("userEmail"); // Asumo que guardas el email
            LOGGER.info("Cerrando sesión para el usuario: {}", (userEmail != null ? userEmail : "desconocido"));
            
            // Invalidar la sesión lo elimina todo (userId, userEmail, userRole, etc.)
            session.invalidate();
        } else {
            LOGGER.warn("Se intentó hacer logout sin una sesión activa.");
        }

        // Redirigir a la página de login con un mensaje de éxito.
        String successMessage = URLEncoder.encode("Has cerrado sesión exitosamente.", StandardCharsets.UTF_8.toString());
        response.sendRedirect(request.getContextPath() + "/login?message=" + successMessage);
    }

    @Override
    public String getServletInfo() {
        return "Controlador para cerrar la sesión del usuario";
    }
}