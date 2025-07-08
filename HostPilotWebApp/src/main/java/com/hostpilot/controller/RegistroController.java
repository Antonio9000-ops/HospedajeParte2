package com.hostpilot.controller;

import com.hostpilot.config.MetricsConfig; // <<< 1. AÑADIR IMPORT PARA MÉTRICAS
import com.hostpilot.exception.AppException;
import com.hostpilot.model.Usuario;
import com.hostpilot.service.UsuarioService;
import com.hostpilot.service.UsuarioServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet; // <<< 2. AÑADIR IMPORT PARA ANOTACIÓN
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para gestionar el registro de nuevos usuarios.
 */
@WebServlet("/registro") // <<< 3. AÑADIR ANOTACIÓN WEBSERVLET
public class RegistroController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RegistroController.class.getName());
    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        // No es necesario llamar a super.init() aquí
        this.usuarioService = new UsuarioServiceImpl();
        LOGGER.info("RegistroController inicializado.");
    }
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("GET /registro: Mostrando el formulario de registro.");
        request.getRequestDispatcher("/WEB-INF/jsp/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        LOGGER.info("POST /registro: Intentando registrar un nuevo usuario.");
        request.setCharacterEncoding("UTF-8");

        Usuario nuevoUsuario = mapRequestToUsuario(request);
        String confirmPassword = request.getParameter("confirmPassword");
        
        try {
            // Validación básica de contraseña
            if (nuevoUsuario.getPassword() == null || nuevoUsuario.getPassword().isEmpty() || !nuevoUsuario.getPassword().equals(confirmPassword)) {
                throw new AppException("Las contraseñas no coinciden o están vacías.");
            }
            
            // Lógica de registro
            usuarioService.registrarUsuario(nuevoUsuario);
            
            // =======================================================
            // MÉTRICA DE ÉXITO
            // =======================================================
            MetricsConfig.getRegistry().counter("usuarios_registrados_total", "status", "success").increment();
            // =======================================================

            LOGGER.info("Usuario registrado exitosamente: " + nuevoUsuario.getEmail());
            // Preparamos un mensaje de éxito para mostrar en la página de login después de redirigir.
            // Es mejor redirigir para evitar reenvíos del formulario si el usuario recarga la página.
            response.sendRedirect(request.getContextPath() + "/login?success=¡Registro+exitoso!+Ahora+puedes+iniciar+sesión.");
            
        } catch (AppException e) { 
            // =======================================================
            // MÉTRICA DE FALLO POR ERROR DE NEGOCIO (EJ. EMAIL DUPLICADO)
            // =======================================================
            MetricsConfig.getRegistry().counter("usuarios_registrados_total", "status", "failure_validation").increment();
            // =======================================================
            
            LOGGER.log(Level.WARNING, "Fallo en el registro para " + nuevoUsuario.getEmail() + ": " + e.getMessage());
            request.setAttribute("error", e.getMessage());
            request.setAttribute("formUsuario", nuevoUsuario); // Guardar datos para repoblar el formulario
            request.getRequestDispatcher("/WEB-INF/jsp/registro.jsp").forward(request, response);

        } catch (Exception e) {
            // =======================================================
            // MÉTRICA DE FALLO POR ERROR INESPERADO (EJ. ERROR DE BD)
            // =======================================================
            MetricsConfig.getRegistry().counter("usuarios_registrados_total", "status", "failure_unexpected").increment();
            // =======================================================
            
            LOGGER.log(Level.SEVERE, "Error inesperado durante el registro para " + nuevoUsuario.getEmail(), e);
            request.setAttribute("error", "Ocurrió un error inesperado. Por favor, inténtelo más tarde.");
            request.setAttribute("formUsuario", nuevoUsuario);
            request.getRequestDispatcher("/WEB-INF/jsp/registro.jsp").forward(request, response);
        }
    }

    private Usuario mapRequestToUsuario(HttpServletRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getParameter("nombre"));
        usuario.setApellido(request.getParameter("apellido"));
        usuario.setEmail(request.getParameter("email"));
        usuario.setPassword(request.getParameter("password"));
        usuario.setTelefono(request.getParameter("telefono"));
        usuario.setGenero(request.getParameter("genero"));

        String edadStr = request.getParameter("edad");
        if (edadStr != null && !edadStr.trim().isEmpty()) {
            try {
                usuario.setEdad(Integer.parseInt(edadStr.trim()));
            } catch (NumberFormatException e) {
                LOGGER.warning("Formato de edad inválido recibido: " + edadStr);
            }
        }
        return usuario;
    }
}