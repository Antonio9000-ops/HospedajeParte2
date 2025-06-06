package com.hostpilot.config;

import java.util.logging.Logger;

/**
 * Gestor de configuración centralizado para la aplicación.
 */
public final class ConfigManager { 

    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());

    // --- Constructor Privado 
    private ConfigManager() {
        throw new IllegalStateException("Esta es una clase de utilidad y no debe ser instanciada.");
    }

    public static final String DB_URL = "jdbc:mysql://localhost:3306/";
    public static final String DB_NAME = "hostpilot_clean";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";
    
    // Parámetros para el Pool de Conexiones
    public static final int DB_POOL_INITIAL_SIZE = 5;
    public static final int DB_POOL_MAX_ACTIVE = 20;




    public static final int BCRYPT_LOG_ROUNDS = 12;

    public static final int SESSION_TIMEOUT_MINUTES = 30;


    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    public static boolean isDevelopmentMode(javax.servlet.ServletContext context) {
        if (context == null) {
            return false; // Por defecto, modo no-desarrollo si no hay contexto
        }
        String env = context.getInitParameter("environment");
        return "development".equalsIgnoreCase(env);
    }

    static {
        LOGGER.info("ConfigManager inicializado con valores estáticos.");
        // LOGGER.info("DB Name: " + DB_NAME);
    }
}