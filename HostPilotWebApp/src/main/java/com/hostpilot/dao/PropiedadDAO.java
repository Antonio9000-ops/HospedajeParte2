package com.hostpilot.dao;

import com.hostpilot.model.Propiedad;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el Acceso a Datos de la entidad Propiedad.
 * Define las operaciones CRUD y otras consultas a la base de datos.
 */
public interface PropiedadDAO {

    /**
     * Busca una propiedad por su ID.
     * @param id El ID de la propiedad a buscar.
     * @return un Optional conteniendo la propiedad si se encuentra, o un Optional vacío si no.
     * @throws DAOException si ocurre un error de base de datos.
     */
    Optional<Propiedad> buscarPorId(long id) throws DAOException;

    /**
     * Busca todas las propiedades en la base de datos.
     * @return una Lista de todas las propiedades.
     * @throws DAOException si ocurre un error de base de datos.
     */
    List<Propiedad> buscarTodas() throws DAOException;

    /**
     * Guarda una nueva propiedad en la base de datos.
     * @param propiedad La propiedad a crear.
     * @return el ID generado para la nueva propiedad.
     * @throws DAOException si ocurre un error de base de datos.
     */
    long crear(Propiedad propiedad) throws DAOException;
    
    // Aquí podrías añadir más métodos en el futuro, como:
    // void actualizar(Propiedad propiedad) throws DAOException;
    // void eliminar(long id) throws DAOException;
    // List<Propiedad> buscarPorCiudad(String ciudad) throws DAOException;
}