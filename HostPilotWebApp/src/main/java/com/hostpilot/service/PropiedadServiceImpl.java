package com.hostpilot.service; // <-- ¡El paquete correcto debe ser .service!

import com.hostpilot.config.DatabaseConfig;
import com.hostpilot.config.MySQLDatabaseConfig;
import com.hostpilot.dao.DAOException;
import com.hostpilot.dao.PropiedadDAO;
import com.hostpilot.dao.PropiedadDAOImpl;
import com.hostpilot.model.Propiedad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Implementación de la lógica de negocio para Propiedades.
 */
public class PropiedadServiceImpl implements PropiedadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropiedadServiceImpl.class);
    private final PropiedadDAO propiedadDAO;

    public PropiedadServiceImpl(PropiedadDAO propiedadDAO) {
        this.propiedadDAO = propiedadDAO;
    }

    public PropiedadServiceImpl() {
        DatabaseConfig dbConfig = new MySQLDatabaseConfig();
        this.propiedadDAO = new PropiedadDAOImpl(dbConfig);
        LOGGER.info("PropiedadServiceImpl instanciado con su propio DAO.");
    }

    @Override
    public List<Propiedad> obtenerTodasLasPropiedades() throws ServiceException {
        try {
            LOGGER.info("Obteniendo todas las propiedades desde la base de datos.");
            return propiedadDAO.obtenerTodas(); // <-- CORREGIDO
        } catch (DAOException e) {
            LOGGER.error("Error al obtener todas las propiedades desde el DAO.", e);
            throw new ServiceException("No se pudieron obtener las propiedades.", e);
        }
    }
}