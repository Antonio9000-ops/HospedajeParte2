package com.hostpilot.dao;

import com.hostpilot.model.Propiedad;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el acceso a datos de la entidad Propiedad.
 * Define las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * que deben ser implementadas.
 */
public interface PropiedadDAO {

    /**
     * Busca una propiedad por su ID único.
     * @param id El ID de la propiedad a buscar.
     * @return un Optional conteniendo la Propiedad si se encuentra, o un Optional vacío.
     * @throws DAOException si ocurre un error en la base de datos.
     */
    Optional<Propiedad> buscarPorId(long id) throws DAOException;
    boolean actualizar(Propiedad propiedad) throws DAOException;
    boolean eliminar(long id) throws DAOException;

    /**
     * Obtiene una lista con todas las propiedades de la base de datos.
     * @return una Lista de objetos Propiedad.
     * @throws DAOException si ocurre un error en la base de datos.
     */
    List<Propiedad> obtenerTodas() throws DAOException; // <-- NOMBRE CORREGIDO AQUÍ

    /**
     * Crea una nueva propiedad en la base de datos.
     * @param propiedad El objeto Propiedad a crear.
     * @return el ID generado para la nueva propiedad.
     * @throws DAOException si ocurre un error en la base de datos.
     */
    long crear(Propiedad propiedad) throws DAOException;
    
    // (Opcional, pero necesario para el formulario de edición)
    // Deberías añadir estos métodos también para completar la funcionalidad
    // boolean actualizar(Propiedad propiedad) throws DAOException;
    // boolean eliminar(long id) throws DAOException;

}