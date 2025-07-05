package com.hostpilot.config;

import java.util.logging.Logger;

/**
 * Gestor de configuración centralizado para la aplicación.
 * Proporciona todas las constantes de configuración en un solo lugar.
 */
public final class ConfigManager {

    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());

    private ConfigManager() {
        // Prevenir la instanciación de esta clase de utilidad.
        throw new IllegalStateException("Clase de utilidad");
    }

    // --- Configuración de la Base de Datos ---
    public static final String DB_URL = "jdbc:mysql://localhost:3306/";
    public static final String DB_NAME = "hostpilot_clean"; // El nombre correcto de tu BD
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = ""; // Dejar vacío si no tienes contraseña

    // --- Configuración del Pool de Conexiones ---
    public static final int DB_POOL_INITIAL_SIZE = 5;
    public static final int DB_POOL_MAX_ACTIVE = 50;
    public static final int DB_POOL_MAX_IDLE = 20;
    public static final int DB_POOL_MIN_IDLE = 5;
    public static final int DB_POOL_MAX_WAIT_MS = 10000;

    // --- Configuración de Seguridad ---
    public static final int BCRYPT_LOG_ROUNDS = 12;
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    /**
     * Verifica si la aplicación está en modo de desarrollo.
     * Lee el parámetro de contexto 'environment' del web.xml.
     */
    public static boolean isDevelopmentMode(javax.servlet.ServletContext context) {
        if (context == null) {
            return false;
        }
        String env = context.getInitParameter("environment");
        return "development".equalsIgnoreCase(env);
    }
    
    static {
        LOGGER.info("ConfigManager inicializado.");
    }
}