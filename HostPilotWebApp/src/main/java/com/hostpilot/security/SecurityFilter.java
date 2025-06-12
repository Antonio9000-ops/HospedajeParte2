package com.hostpilot.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecurityFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(SecurityFilter.class.getName());

   
    private static final Set<String> PUBLIC_URLS = new HashSet<>(Arrays.asList(
            "/",                // La raíz
            "/index.jsp",       // El welcome-file
            "/login",           // Servlet de Login
            "/registro",        // Servlet de Registro
            "/buscar",          // NUEVO: Servlet de Búsqueda
            "/propiedad",       // NUEVO: Servlet de Detalles de Propiedad
            "/css/",            // Recursos estáticos
            "/js/",
            "/images/",
            "/anfitrion", // AÑADIDO
            "/reservas",  // <<< AÑADIDO AQU
            "/favicon.ico",
            "/error/"           // Páginas de error 
            
    ));

    // URLs que requieren rol de administrador
    private static final Set<String> ADMIN_URLS = new HashSet<>(Arrays.asList("/admin/"));

    
    private Set<String> excludePatterns = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("Inicializando SecurityFilter...");
        String patterns = filterConfig.getInitParameter("excludePatterns");
        if (patterns != null && !patterns.isEmpty()) {
            for (String pattern : patterns.split(",")) {
                String trimmedPattern = pattern.trim();
                if (!trimmedPattern.isEmpty()) {
                    excludePatterns.add(trimmedPattern);
                    LOGGER.log(Level.INFO, "Patrón de exclusión (desde web.xml): {0}", trimmedPattern);
                }
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        addSecurityHeaders(httpResponse);

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        if (path.isEmpty()) {
            path = "/";
        }

        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        LOGGER.log(Level.INFO, "SecurityFilter procesando path: {0}", path);

        try {

            if (isExcludedOrPublicURL(path)) {
                LOGGER.log(Level.INFO, "Path público o excluido: {0}. Pasando al siguiente en la cadena.", path);
                chain.doFilter(request, response);
                return;
            }

            if (!SessionManager.validateSession(httpRequest)) {
                LOGGER.log(Level.INFO, "Sesión no válida para path protegido: {0}. Redirigiendo a login.", path);
                // Es correcto pasar el mensaje aquí, ya que se intentó acceder a un recurso protegido
                // y la sesión no era válida (o no existía y no era el acceso inicial a una página pública).
                redirectToLogin(httpRequest, httpResponse, "Su sesión ha expirado o no es válida. Por favor, inicie sesión.");
                return;
            }


            if (isAdminURL(path) && !SessionManager.hasRole(httpRequest, "ADMIN")) {
                LOGGER.log(Level.WARNING, "Acceso no autorizado a URL de admin ({0}) por usuario {1} (Rol: {2})",
                        new Object[]{path, SessionManager.getCurrentUserEmail(httpRequest), SessionManager.getCurrentUserRole(httpRequest)});
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene los permisos necesarios para acceder a este recurso.");
                return;
            }

            LOGGER.log(Level.INFO, "Acceso permitido para path: {0}", path);
            chain.doFilter(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado en SecurityFilter al procesar " + path, e);
            if (!httpResponse.isCommitted()) {

                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno procesando la solicitud.");
            }
        }
    }

    private boolean isExcludedOrPublicURL(String path) {
     
        for (String pattern : excludePatterns) {
            if (path.startsWith(pattern)) {
                LOGGER.log(Level.FINER, "Path {0} coincide con patrón de exclusión: {1}", new Object[]{path, pattern});
                return true;
            }
        }
 
        if (PUBLIC_URLS.contains(path)) {
            LOGGER.log(Level.FINER, "Path {0} está en PUBLIC_URLS.", path);
            return true;
        }
    
        for (String publicUrlPrefix : PUBLIC_URLS) {
            if (publicUrlPrefix.endsWith("/") && path.startsWith(publicUrlPrefix)) {
                LOGGER.log(Level.FINER, "Path {0} coincide con prefijo público: {1}", new Object[]{path, publicUrlPrefix});
                return true;
            }
        }
        LOGGER.log(Level.FINER, "Path {0} no es público ni excluido.", path);
        return false;
    }
    
    

    private boolean isAdminURL(String path) {
        if (ADMIN_URLS.contains(path)) return true;
        for (String adminUrlPrefix : ADMIN_URLS) {
            if (adminUrlPrefix.endsWith("/") && path.startsWith(adminUrlPrefix)) return true;
        }
        return false;
    }
    
    

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        HttpSession session = request.getSession(); 
        String currentPath = request.getRequestURI().substring(request.getContextPath().length());
         if (currentPath.isEmpty()) { // Normalizar raíz
            currentPath = "/";
        }

        if (!currentPath.equals("/login") && !currentPath.equals("/registro") && !currentPath.equals("/")) {
            String requestedURIWithQuery = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
            session.setAttribute("requestedURI", requestedURIWithQuery);
            LOGGER.log(Level.INFO, "Guardando URL solicitada ({0}) para redirección post-login.", requestedURIWithQuery);
        } else {
             LOGGER.log(Level.INFO, "No se guarda requestedURI para path: {0}", currentPath);
        }

        String redirectURL = request.getContextPath() + "/login";
        if (message != null && !message.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        }
        
        if (!response.isCommitted()) {
            LOGGER.log(Level.INFO, "Redirigiendo a: {0}", redirectURL);
            response.sendRedirect(redirectURL);
        } else {
            LOGGER.warning("La respuesta ya fue enviada (committed), no se puede redirigir a login.");
        }
    }
    
    

    private void addSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // --- INICIO DE LA CORRECCIÓN DE CSP ---
        // Política de Seguridad de Contenido (CSP) actualizada para permitir CDNs
        response.setHeader("Content-Security-Policy", 
             "default-src 'self'; " +  // Por defecto, solo permitir recursos del mismo origen

             // Permitir scripts de nuestro dominio, inline, y de las CDNs que usamos
             "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://unpkg.com; " + 
             
             // Permitir estilos de nuestro dominio, inline, y de las CDNs que usamos
             "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://unpkg.com https://fonts.googleapis.com; " +
             
             // Permitir fuentes de nuestro dominio y de Google Fonts
             "font-src 'self' https://fonts.gstatic.com; " + 
             
             // Permitir imágenes de nuestro dominio, data URIs, y de las URLs de ejemplo que usamos
             "img-src 'self' data: https://hips.hearstapps.com https://nexoinmobiliario.pe https://imagenes.20minutos.es https://content.elmueble.com https://images.unsplash.com https://dbdzm869oupei.cloudfront.net https://cf.bstatic.com https://i.pinimg.com https://encrypted-tbn0.gstatic.com https://a.tile.openstreetmap.org https://b.tile.openstreetmap.org https://c.tile.openstreetmap.org https://*.basemaps.cartocdn.com https://cdn-icons-png.flaticon.com; " +
             
                     
          
        // Añadimos https://unpkg.com para permitir las imágenes de los marcadores de Leaflet.
        "img-src 'self' data: https://hips.hearstapps.com https://nexoinmobiliario.pe https://imagenes.20minutos.es " +
        "https://content.elmueble.com https://images.unsplash.com https://dbdzm869oupei.cloudfront.net " +
        "https://cf.bstatic.com https://i.pinimg.com https://encrypted-tbn0.gstatic.com " +
        "https://*.basemaps.cartocdn.com https://cdn-icons-png.flaticon.com https://unpkg.com; " +
       
                     
             "form-action 'self'; " + // Permitir que los formularios envíen datos a nuestro dominio
             "frame-ancestors 'none';"); // Prevenir clickjacking
        // --- FIN DE LA CORRECCIÓN DE CSP ---
    }

    @Override
    public void destroy() {
        LOGGER.info("Destruyendo SecurityFilter...");
        excludePatterns.clear();
    }
    
    
}