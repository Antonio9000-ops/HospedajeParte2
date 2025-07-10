package com.hostpilot.controller;

import com.hostpilot.config.MetricsConfig;
import com.hostpilot.exception.AppException;
import com.hostpilot.model.Usuario;
import com.hostpilot.service.UsuarioService;
import com.hostpilot.service.UsuarioServiceImpl;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/registro")
public class RegistroController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistroController.class);
    private UsuarioService usuarioService;

    @Override
    public void init() {
        this.usuarioService = new UsuarioServiceImpl();
        LOGGER.info("RegistroController inicializado.");
    }
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.debug("Mostrando formulario de registro.");
        request.getRequestDispatcher("/WEB-INF/jsp/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        Usuario nuevoUsuario = mapRequestToUsuario(request);
        String confirmPassword = request.getParameter("confirmPassword");
        
        LOGGER.info("Intentando registrar nuevo usuario con email: {}", nuevoUsuario.getEmail());
        
        try {
            if (StringUtils.isBlank(nuevoUsuario.getPassword()) || !nuevoUsuario.getPassword().equals(confirmPassword)) {
                throw new AppException("Las contraseñas no coinciden o están vacías.");
            }
            
            usuarioService.registrarUsuario(nuevoUsuario);
            
            MetricsConfig.getRegistry().counter("usuarios_registrados_total", "status", "success").increment();
            LOGGER.info("Usuario registrado exitosamente: {}", nuevoUsuario.getEmail());

            response.sendRedirect(request.getContextPath() + "/login?success=¡Registro+exitoso!+Ahora+puedes+iniciar+sesión.");
            
        } catch (AppException e) { 
            MetricsConfig.getRegistry().counter("usuarios_registrados_total", "status", "failure_validation").increment();
            LOGGER.warn("Fallo en la validación del registro para {}: {}", nuevoUsuario.getEmail(), e.getMessage());
            request.setAttribute("error", e.getMessage());
            request.setAttribute("formUsuario", nuevoUsuario);
            request.getRequestDispatcher("/WEB-INF/jsp/registro.jsp").forward(request, response);

        } catch (Exception e) {
            MetricsConfig.getRegistry().counter("usuarios_registrados_total", "status", "failure_unexpected").increment();
            LOGGER.error("Error inesperado durante el registro para {}", nuevoUsuario.getEmail(), e);
            request.setAttribute("error", "Ocurrió un error inesperado. Por favor, inténtelo más tarde.");
            request.setAttribute("formUsuario", nuevoUsuario);
            request.getRequestDispatcher("/WEB-INF/jsp/registro.jsp").forward(request, response);
        }
    }

    private Usuario mapRequestToUsuario(HttpServletRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(StringUtils.trimToNull(request.getParameter("nombre")));
        usuario.setApellido(StringUtils.trimToNull(request.getParameter("apellido")));
        usuario.setEmail(StringUtils.trimToNull(request.getParameter("email")));
        usuario.setPassword(StringUtils.trimToNull(request.getParameter("password")));
        usuario.setTelefono(StringUtils.trimToNull(request.getParameter("telefono")));
        usuario.setGenero(StringUtils.trimToNull(request.getParameter("genero")));

        String edadStr = request.getParameter("edad");
        if (!Strings.isNullOrEmpty(edadStr)) {
            try {
                usuario.setEdad(Integer.parseInt(edadStr.trim()));
            } catch (NumberFormatException e) {
                LOGGER.warn("Formato de edad inválido: {}", edadStr);
            }
        }
        return usuario;
    }
}