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
            "/",
            "/index.jsp",
            "/login",
            "/registro",
            "/buscar",
            "/propiedad",
            "/reservas",
            "/anfitrion",
            "/css/",
            "/js/",
            "/images/",
            "/favicon.ico",
            "/realizar-reserva",
            "/error/"
            
    ));

    private static final Set<String> ADMIN_URLS = new HashSet<>(Arrays.asList("/admin/"));

    private static final Set<String> API_URLS = new HashSet<>(Arrays.asList(
            "/realizar-reserva"
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

        addSecurityHeaders(httpResponse);

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if (path.isEmpty()) {
            path = "/";
        }
        
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        try {
            if (isExcludedOrPublicURL(path)) {
                chain.doFilter(request, response);
                return;
            }

            if (!SessionManager.validateSession(httpRequest)) {
                if (isApiURL(path)) {
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpResponse.setContentType("application/json");
                    httpResponse.setCharacterEncoding("UTF-8");
                    httpResponse.getWriter().write("{\"status\":\"error\", \"message\":\"Debe iniciar sesión para realizar esta acción.\"}");
                } else {
                    redirectToLogin(httpRequest, httpResponse, "Su sesión ha expirado o no es válida.");
                }
                return;
            }

            if (isAdminURL(path) && !SessionManager.hasRole(httpRequest, "ADMIN")) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso Denegado.");
                return;
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado en SecurityFilter para path: " + path, e);
            if (!httpResponse.isCommitted()) {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor.");
            }
        }
    }

    private boolean isApiURL(String path) {
        return API_URLS.stream().anyMatch(path::startsWith);
    }
    
    private boolean isExcludedOrPublicURL(String path) {
        for (String pattern : excludePatterns) {
            if (path.startsWith(pattern)) return true;
        }
        if (PUBLIC_URLS.contains(path)) return true;
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
        HttpSession session = request.getSession(); 
        String currentPath = request.getRequestURI().substring(request.getContextPath().length());
        if (currentPath.isEmpty()) {
            currentPath = "/";
        }

        if (!currentPath.equals("/login") && !currentPath.equals("/registro") && !currentPath.equals("/")) {
            String requestedURIWithQuery = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
            session.setAttribute("requestedURI", requestedURIWithQuery);
        }

        String redirectURL = request.getContextPath() + "/login";
        if (message != null && !message.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        }
        
        if (!response.isCommitted()) {
            response.sendRedirect(redirectURL);
        }
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        response.setHeader("Content-Security-Policy", 
             "default-src 'self'; " +
             "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://unpkg.com; " + 
             "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://unpkg.com https://fonts.googleapis.com; " +
             "font-src 'self' https://fonts.gstatic.com; " + 
             "img-src 'self' data: https:; " + // 'https:' permite imágenes de cualquier fuente HTTPS
             "form-action 'self'; " +
             "frame-ancestors 'none';"
        );
    }

    @Override
    public void destroy() {
        LOGGER.info("Destruyendo SecurityFilter...");
    }
}