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

/**
 * Filtro de seguridad que intercepta todas las peticiones para gestionar la autenticación y autorización.
 * - Protege las rutas que no son públicas.
 * - Redirige al login si el usuario no está autenticado.
 * - Devuelve respuestas JSON para peticiones de API no autorizadas.
 * - Añade cabeceras de seguridad a todas las respuestas.
 */
public class SecurityFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(SecurityFilter.class.getName());

    // --- Listas de URLs ---

    // URLs accesibles para CUALQUIER usuario (logueado o no).
    // Incluye páginas estáticas, de login/registro, y recursos públicos como CSS/JS.
    private static final Set<String> PUBLIC_URLS = new HashSet<>(Arrays.asList(
            "/",
            "/index.jsp",
            "/login",
            "/registro",
            "/buscar",
            "/propiedad",
            "/reservas", // La página para ver propiedades es pública, pero la acción de reservar no.
            "/anfitrion",
            "/css/",
            "/js/",
            "/images/",
            "/favicon.ico",
            // "/realizar-reserva", // <<< CORRECCIÓN: Se elimina de aquí. No es una acción pública.
            "/error/"
    ));

    // URLs que son parte de una API y deben devolver JSON en caso de error.
    private static final Set<String> API_URLS = new HashSet<>(Arrays.asList(
            "/realizar-reserva" // Esta es la acción de reservar, que es una API.
    ));
    
    // URLs restringidas solo para usuarios con rol de administrador.
    private static final Set<String> ADMIN_URLS = new HashSet<>(Arrays.asList(
            "/admin/"
    ));

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
                }
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Añadir cabeceras de seguridad a todas las respuestas.
        addSecurityHeaders(httpResponse);

        // Normalizar el path para facilitar las comparaciones.
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if (path.isEmpty()) {
            path = "/";
        }
        
        // Quitar la barra final si existe, para estandarizar (ej. /admin/ -> /admin)
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        try {
            // 1. Si la URL es pública, dejar pasar la petición sin más comprobaciones.
            if (isExcludedOrPublicURL(path)) {
                chain.doFilter(request, response);
                return;
            }

            // 2. Si la URL no es pública, verificar si hay una sesión válida.
            if (!SessionManager.validateSession(httpRequest)) {
                // Si no hay sesión, determinar cómo responder.
                if (isApiURL(path)) {
                    // Para una API, devolver un error 401 con JSON.
                    sendApiUnauthorizedError(httpResponse);
                } else {
                    // Para una página normal, redirigir al login.
                    redirectToLogin(httpRequest, httpResponse, "Su sesión ha expirado o no es válida. Por favor, inicie sesión.");
                }
                return; // Detener la cadena de filtros.
            }

            // 3. Si hay sesión, verificar roles para rutas de administrador.
            if (isAdminURL(path) && !SessionManager.hasRole(httpRequest, "ADMIN")) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso Denegado. No tiene permisos de administrador.");
                return; // Detener la cadena de filtros.
            }

            // 4. Si todas las comprobaciones pasan, permitir que la petición continúe.
            chain.doFilter(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado en SecurityFilter para el path: " + path, e);
            if (!httpResponse.isCommitted()) {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor.");
            }
        }
    }

    private boolean isApiURL(String path) {
        return API_URLS.stream().anyMatch(path::startsWith);
    }
    
    private boolean isExcludedOrPublicURL(String path) {
        // Comprobar patrones de exclusión (ej. /static/)
        if (excludePatterns.stream().anyMatch(path::startsWith)) {
            return true;
        }
        // Comprobar coincidencia exacta (ej. /login)
        if (PUBLIC_URLS.contains(path)) {
            return true;
        }
        // Comprobar si el path empieza con una URL pública que termina en / (ej. /css/style.css coincide con /css/)
        for (String publicUrlPrefix : PUBLIC_URLS) {
            if (publicUrlPrefix.endsWith("/") && path.startsWith(publicUrlPrefix)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isAdminURL(String path) {
        return ADMIN_URLS.stream().anyMatch(path::startsWith);
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        // Guardar la URL solicitada para redirigir al usuario después del login.
        String requestedURI = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        HttpSession session = request.getSession();
        session.setAttribute("requestedURI", requestedURI);

        String redirectURL = request.getContextPath() + "/login?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        
        if (!response.isCommitted()) {
            response.sendRedirect(redirectURL);
        }
    }
    
    private void sendApiUnauthorizedError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":\"error\", \"message\":\"Debe iniciar sesión para realizar esta acción.\"}");
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "DENY");
    response.setHeader("X-XSS-Protection", "1; mode=block");
    response.setHeader("X-Content-Type-Options", "nosniff");
    response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Política de Seguridad de Contenido (CSP): Define de dónde se pueden cargar los recursos.
         response.setHeader("Content-Security-Policy", 

        "default-src 'self'; " +
        
        // Scripts permitidos: del propio dominio, inline, y de los CDNs especificados.
        "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://unpkg.com https://cdn.voiceflow.com; " + 
        
        // === LÍNEA CORREGIDA ===
        // Hojas de estilo permitidas: se añade https://cdn.voiceflow.com.
        "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://unpkg.com https://fonts.googleapis.com https://cdn.voiceflow.com; " +
        
        // === LÍNEA CORREGIDA ===
        // Fuentes permitidas: se añade https://cdn.voiceflow.com.
        "font-src 'self' https://fonts.gstatic.com https://cdn.voiceflow.com; " + 
        
        // Conexiones de red (APIs): a los servidores de Voiceflow.
        "connect-src 'self' https://general-runtime.voiceflow.com https://runtime-api.voiceflow.com; " +
        
        // Imágenes permitidas: del propio dominio, data URIs, y de cualquier fuente HTTPS.
        "img-src 'self' data: https:; " + 
        
        // Dónde se pueden enviar formularios.
        "form-action 'self'; " +
        
        // No permitir que la página sea incrustada en un <iframe>.
        "frame-ancestors 'none';"
    );
    }

    @Override
    public void destroy() {
        LOGGER.info("Destruyendo SecurityFilter...");
    }
}