package com.hostpilot.service;

import com.hostpilot.model.Propiedad;
import java.util.List;

/**
 * Interfaz para la lógica de negocio relacionada con las propiedades.
 */
public interface PropiedadService {
    
    /**
     * Obtiene una lista de todas las propiedades disponibles.
     * @return Una lista de objetos Propiedad.
     * @throws ServiceException si ocurre un error durante la operación.
     */
    List<Propiedad> obtenerTodasLasPropiedades() throws ServiceException;
    
    // Aquí puedes añadir más métodos de negocio en el futuro
}