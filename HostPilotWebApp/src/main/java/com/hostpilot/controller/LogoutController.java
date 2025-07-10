package com.hostpilot.controller;

import com.hostpilot.security.SessionManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LogoutController.class.getName());

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
        LOGGER.info("LogoutController: Procesando logout.");
        
        String userEmail = SessionManager.getCurrentUserEmail(request);
        SessionManager.invalidateSession(request);

        LOGGER.info("Sesión invalidada para: " + (userEmail != null ? userEmail : "usuario desconocido") + ". Redirigiendo a login.");
        
        String successMessage = URLEncoder.encode("Has cerrado sesión exitosamente.", "UTF-8");
        response.sendRedirect(request.getContextPath() + "/login?message=" + successMessage);
    }

    @Override
    public String getServletInfo() {
        return "Controlador para cerrar la sesión del usuario";
    }
}