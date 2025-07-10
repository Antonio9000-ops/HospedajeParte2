package com.hostpilot.controller;

import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.PropiedadDAO;
import com.hostpilot.dao.PropiedadDAOImpl;
import com.hostpilot.dao.UsuarioDAO;
import com.hostpilot.dao.UsuarioDAOImpl;
import com.hostpilot.model.Propiedad;
import com.hostpilot.model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/propiedades")
public class AdminPropiedadController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminPropiedadController.class.getName());
    private PropiedadDAO propiedadDAO;
    private UsuarioDAO usuarioDAO; // Necesario para el dropdown de anfitriones

    @Override
    public void init() throws ServletException {
        // Inicializamos ambos DAOs
        this.propiedadDAO = new PropiedadDAOImpl(new MySQLDatabaseConfig());
        this.usuarioDAO = new UsuarioDAOImpl(new MySQLDatabaseConfig());
    }

    // --- MÉTODO GET: Muestra formularios o realiza acciones simples como eliminar ---
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        switch (action) {
            case "editar":
                mostrarFormularioEdicion(request, response);
                break;
            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "eliminar": // <-- LÓGICA DE ELIMINACIÓN AÑADIDA AQUÍ
                eliminarPropiedad(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }

    // --- MÉTODO POST: Guarda o crea datos ---
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
             response.sendRedirect(request.getContextPath() + "/admin/dashboard");
             return;
        }

        switch(action) {
            case "guardar": // Actualiza una propiedad existente
                guardarPropiedad(request, response, false);
                break;
            case "crear": // Crea una nueva propiedad
                guardarPropiedad(request, response, true);
                break;
            default:
                 response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }
    
    // --- MÉTODOS PRIVADOS DE AYUDA ---

    private void mostrarFormularioEdicion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            Optional<Propiedad> propiedadOpt = propiedadDAO.buscarPorId(id);
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();

            if (propiedadOpt.isPresent()) {
                request.setAttribute("listaUsuarios", usuarios);
                request.setAttribute("propiedad", propiedadOpt.get());
                request.setAttribute("modo", "Editar");
                request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-propiedad.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=notfound");
            }
        } catch (Exception e) {
            throw new ServletException("Error al mostrar formulario de edición", e);
        }
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            request.setAttribute("listaUsuarios", usuarios);
            request.setAttribute("propiedad", new Propiedad()); 
            request.setAttribute("modo", "Crear");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-propiedad.jsp").forward(request, response);
        } catch (DAOException e) {
            throw new ServletException("Error al cargar datos para nuevo formulario", e);
        }
    }
    
    private void guardarPropiedad(HttpServletRequest request, HttpServletResponse response, boolean esNuevo) throws ServletException, IOException {
        try {
            Propiedad propiedad = new Propiedad();
            
            if (!esNuevo) {
                propiedad.setId(Integer.parseInt(request.getParameter("id")));
            }
            
            propiedad.setAnfitrionId(Integer.parseInt(request.getParameter("anfitrionId")));
            propiedad.setTitulo(request.getParameter("titulo"));
            propiedad.setDescripcion(request.getParameter("descripcion"));
            propiedad.setDireccion(request.getParameter("direccion"));
            propiedad.setCiudad(request.getParameter("ciudad"));
            propiedad.setPrecioPorNoche(Double.parseDouble(request.getParameter("precioPorNoche")));
            propiedad.setCapacidad(Integer.parseInt(request.getParameter("capacidad")));
            propiedad.setTipo(request.getParameter("tipo"));
            propiedad.setImgUrl(request.getParameter("imgUrl"));
            propiedad.setLat(Double.parseDouble(request.getParameter("lat")));
            propiedad.setLng(Double.parseDouble(request.getParameter("lng")));
            propiedad.setRating(Double.parseDouble(request.getParameter("rating")));
            propiedad.setReviews(Integer.parseInt(request.getParameter("reviews")));

            boolean exito;
            if (esNuevo) {
                long nuevoId = propiedadDAO.crear(propiedad);
                exito = (nuevoId > 0);
            } else {
                exito = propiedadDAO.actualizar(propiedad);
            }

            if (exito) {
                LOGGER.info("Operación de guardado/creación exitosa para propiedad: " + propiedad.getTitulo());
            } else {
                LOGGER.warning("Falló la operación de guardado/creación para propiedad: " + propiedad.getTitulo());
            }
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");

        } catch (Exception e) {
            throw new ServletException("Error al guardar la propiedad", e);
        }
    }

    // --- NUEVO MÉTODO PARA ELIMINAR ---
    private void eliminarPropiedad(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            boolean exito = propiedadDAO.eliminar(id);
            
            if (exito) {
                LOGGER.info("Propiedad con ID " + id + " eliminada exitosamente.");
                // Puedes añadir un mensaje de éxito a la sesión si lo deseas
                // request.getSession().setAttribute("success_message", "Propiedad eliminada con éxito.");
            } else {
                LOGGER.warning("No se pudo eliminar la propiedad con ID: " + id);
                // request.getSession().setAttribute("error_message", "No se pudo eliminar la propiedad.");
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Intento de eliminar propiedad con ID inválido.", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de propiedad inválido.");
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error de DAO al eliminar la propiedad. Posiblemente por restricciones de clave foránea.", e);
            // request.getSession().setAttribute("error_message", "Error: No se puede eliminar la propiedad, puede que tenga reservas asociadas.");
            // response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            throw new ServletException("Error al procesar la eliminación de la propiedad.", e);
        }
    }
}