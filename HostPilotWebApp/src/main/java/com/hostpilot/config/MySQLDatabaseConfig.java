package com.hostpilot.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * Implementación de la configuración para la base de datos MySQL.

 */
public class MySQLDatabaseConfig implements DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(MySQLDatabaseConfig.class.getName());

    private final javax.sql.DataSource dataSource;

    /**
     * Constructor. Llama al método que crea el pool de conexiones.
     */
    public MySQLDatabaseConfig() {
        this.dataSource = createTomcatJdbcDataSource();
    }

    /**
     * Obtiene una conexión del pool.

     */
    @Override
    public Connection getConnection() throws SQLException {
        if (this.dataSource == null) {
            throw new SQLException("El DataSource no fue inicializado correctamente.");
        }
        return this.dataSource.getConnection();
    }


    @Override
    public void close() {
        if (this.dataSource instanceof DataSource) { // DataSource de org.apache.tomcat.jdbc.pool
            ((DataSource) this.dataSource).close();
            LOGGER.info("Pool de conexiones Tomcat JDBC cerrado.");
        }
    }

  
    private javax.sql.DataSource createTomcatJdbcDataSource() {
        try {
            //  Configurar las propiedades del pool usando las constantes de ConfigManager.
            PoolProperties props = new PoolProperties();
            
    
            String jdbcUrl = ConfigManager.DB_URL + ConfigManager.DB_NAME + 
                             "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
            
            props.setUrl(jdbcUrl);
            props.setDriverClassName("com.mysql.cj.jdbc.Driver");
            props.setUsername(ConfigManager.DB_USER);
            props.setPassword(ConfigManager.DB_PASSWORD);

          
            props.setInitialSize(ConfigManager.DB_POOL_INITIAL_SIZE);
            props.setMaxActive(ConfigManager.DB_POOL_MAX_ACTIVE);
            
        
            props.setTestOnBorrow(true);
            props.setValidationQuery("SELECT 1");
            props.setValidationInterval(30000); 

        
            DataSource tomcatDataSource = new DataSource();
            tomcatDataSource.setPoolProperties(props);

            LOGGER.info("DataSource (Tomcat JDBC) configurado exitosamente para: " + jdbcUrl);
            return tomcatDataSource;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ERROR CRÍTICO: No se pudo configurar el DataSource de la base de datos.", e);
         
          
            throw new RuntimeException("Fallo al inicializar la conexión con la base de datos.", e);
        }
    }
    
}