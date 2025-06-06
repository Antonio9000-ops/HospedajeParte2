package com.hostpilot.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interfaz para la configuración y acceso a la base de datos.
 */
public interface DatabaseConfig {

 
    Connection getConnection() throws SQLException;

    void close();


}