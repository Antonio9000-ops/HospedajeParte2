package com.hostpilot.controller;

import com.hostpilot.exception.AppException; // <<< Import cambiado
import com.hostpilot.model.Usuario;
import com.hostpilot.service.UsuarioService;
import com.hostpilot.service.UsuarioServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para gestionar el registro de nuevos usuarios.
 
 */
public class RegistroController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RegistroController.class.getName());
    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioServiceImpl();
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
            if (nuevoUsuario.getPassword() == null || !nuevoUsuario.getPassword().equals(confirmPassword)) {
                throw new AppException("Las contraseñas no coinciden.");
            }
            
           
            usuarioService.registrarUsuario(nuevoUsuario);
            
            
            LOGGER.info("Usuario registrado exitosamente: " + nuevoUsuario.getEmail());
            request.setAttribute("success", "¡Registro exitoso! Ya puedes iniciar sesión.");
            
        } catch (AppException e) { 
            // Error de negocio gmail existente
            LOGGER.log(Level.WARNING, "Fallo en el registro para " + nuevoUsuario.getEmail() + ": " + e.getMessage());
            request.setAttribute("error", e.getMessage());
            // Guardar datos para repoblar el formulario
            request.setAttribute("formUsuario", nuevoUsuario); 
        }

        // Reenviar al JSP para mostrar el resultado
        request.getRequestDispatcher("/WEB-INF/jsp/registro.jsp").forward(request, response);
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