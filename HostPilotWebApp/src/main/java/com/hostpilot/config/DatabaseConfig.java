package com.hostpilot.config;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConfig {
    
    /**
     * Obtiene una conexión del pool.
     */
    Connection getConnection() throws SQLException;
    
    /**
     * Cierra el pool de conexiones.
     */
    void close();
}