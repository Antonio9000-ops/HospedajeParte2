package com.hostpilot.security;

import com.hostpilot.config.ConfigManager; // Importar nuestra clase de configuración
import com.hostpilot.model.Usuario;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor de sesiones seguras.

 */
public final class SessionManager {

    private static final Logger LOGGER = Logger.getLogger(SessionManager.class.getName());

   
    private static final long SESSION_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(ConfigManager.SESSION_TIMEOUT_MINUTES);
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = ConfigManager.MAX_FAILED_LOGIN_ATTEMPTS;
    private static final long LOCKOUT_DURATION_MS = TimeUnit.MINUTES.toMillis(15); // 15 minutos de bloqueo

   
    private static final ConcurrentHashMap<String, FailedAttempt> failedLoginAttempts = new ConcurrentHashMap<>();

    private SessionManager() {} // Clase de utilidad, no instanciable.

  
    public static void createUserSession(HttpSession session, Usuario usuario, HttpServletRequest request) {
        // Regenerar el ID de la sesión previene ataques de "session fixation".
        request.changeSessionId();

        // Guardar información clave y segura del usuario en la sesión.
        session.setAttribute("userId", usuario.getId());
        session.setAttribute("userEmail", usuario.getEmail());
        session.setAttribute("userRole", usuario.getRol());

        // Guardar metadatos de la sesión para validaciones de seguridad.
        session.setAttribute("loginTime", System.currentTimeMillis());
        session.setAttribute("clientIP", getClientIP(request));
        
        LOGGER.info("Sesión creada para usuario ID: " + usuario.getId());
    }

    /**
     * Valida si la sesión actual es válida.
   
     */
    public static boolean validateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // No crear una nueva sesión
        return session != null && session.getAttribute("userId") != null;
    }

    /**
     * Invalida la sesión actual, cerrando la sesión del usuario.
     
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            LOGGER.info("Invalidando sesión para usuario ID: " + session.getAttribute("userId"));
            session.invalidate();
        }
    }
    


    public static Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userIdObj = session.getAttribute("userId");
            if (userIdObj instanceof Long) {
                return (Long) userIdObj;
            }
        }
        return null;
    }
    
    public static String getCurrentUserEmail(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (String) session.getAttribute("userEmail") : null;
    }

    public static String getCurrentUserRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (String) session.getAttribute("userRole") : null;
    }

    public static boolean hasRole(HttpServletRequest request, String requiredRole) {
        String userRole = getCurrentUserRole(request);
        return userRole != null && userRole.equalsIgnoreCase(requiredRole);
    }
    
    
    private static class FailedAttempt {
        int attempts = 1;
        long lastAttemptTimestamp = System.currentTimeMillis();
    }

    public static boolean isIPBlocked(HttpServletRequest request) {
        String clientIP = getClientIP(request);
        FailedAttempt attempt = failedLoginAttempts.get(clientIP);
        if (attempt == null) return false;

        // Si el bloqueo ha expirado, limpiar y permitir el acceso.
        if (System.currentTimeMillis() - attempt.lastAttemptTimestamp > LOCKOUT_DURATION_MS) {
            failedLoginAttempts.remove(clientIP);
            return false;
        }
        return attempt.attempts >= MAX_FAILED_LOGIN_ATTEMPTS;
    }

    public static void registerFailedLogin(HttpServletRequest request) {
        String clientIP = getClientIP(request);
        failedLoginAttempts.compute(clientIP, (ip, attempt) -> {
            if (attempt == null || System.currentTimeMillis() - attempt.lastAttemptTimestamp > LOCKOUT_DURATION_MS) {
                return new FailedAttempt(); // Iniciar nuevo conteo
            }
            attempt.attempts++;
            attempt.lastAttemptTimestamp = System.currentTimeMillis();
            if (attempt.attempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
                LOGGER.warning("IP " + clientIP + " ha sido bloqueada temporalmente por demasiados intentos de login.");
            }
            return attempt;
        });
    }

    public static void clearFailedLogins(HttpServletRequest request) {
        failedLoginAttempts.remove(getClientIP(request));
    }

    public static String getClientIP(HttpServletRequest request) {
        if (request == null) return "unknown";
        // Considerar X-Forwarded-For para proxies
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}