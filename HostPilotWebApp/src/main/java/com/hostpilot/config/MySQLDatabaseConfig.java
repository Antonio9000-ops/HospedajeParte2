package com.hostpilot.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class MySQLDatabaseConfig implements DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(MySQLDatabaseConfig.class.getName());

    private final DataSource dataSource;

    /**
     * Constructor que crea el DataSource usando los valores de ConfigManager.
     */
    public MySQLDatabaseConfig() {
        this.dataSource = createDataSource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fallo al obtener conexión del pool de BD.", e);
            throw e;
        }
    }

    @Override
    public void close() {
        if (this.dataSource != null) {
            this.dataSource.close();
            LOGGER.info("Pool de conexiones cerrado.");
        }
    }

    private DataSource createDataSource() {
        try {
            PoolProperties p = new PoolProperties();
            
            // Construir la URL completa
            String fullUrl = ConfigManager.DB_URL + ConfigManager.DB_NAME + 
                             "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
            
            LOGGER.info("Configurando DataSource para URL: " + fullUrl);

            // Leer TODA la configuración desde ConfigManager
            p.setUrl(fullUrl);
            p.setDriverClassName("com.mysql.cj.jdbc.Driver");
            p.setUsername(ConfigManager.DB_USER);
            p.setPassword(ConfigManager.DB_PASSWORD);
            
            p.setInitialSize(ConfigManager.DB_POOL_INITIAL_SIZE);
            p.setMaxActive(ConfigManager.DB_POOL_MAX_ACTIVE);
            p.setMaxIdle(ConfigManager.DB_POOL_MAX_IDLE);
            p.setMinIdle(ConfigManager.DB_POOL_MIN_IDLE);
            p.setMaxWait(ConfigManager.DB_POOL_MAX_WAIT_MS);
            
            // Propiedades de validación y mantenimiento del pool
            p.setTestOnBorrow(true);
            p.setValidationQuery("SELECT 1");
            p.setValidationInterval(30000);
            p.setTimeBetweenEvictionRunsMillis(30000);
            p.setMinEvictableIdleTimeMillis(60000);
            p.setRemoveAbandoned(true);
            p.setRemoveAbandonedTimeout(60);
            
            DataSource tomcatDataSource = new DataSource();
            tomcatDataSource.setPoolProperties(p);

            LOGGER.info("DataSource (Tomcat JDBC) configurado exitosamente.");
            return tomcatDataSource;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ERROR CRÍTICO: No se pudo configurar el DataSource de la base de datos.", e);
            // Lanzar una RuntimeException aquí es correcto porque la aplicación no puede funcionar sin BD.
            throw new RuntimeException("Error fatal al configurar la base de datos.", e);
        }
    }
    
    // Los métodos isConnectionAvailable, getConfigInfo y la clase Builder se eliminan
    // porque ahora la configuración es estática y centralizada.
}