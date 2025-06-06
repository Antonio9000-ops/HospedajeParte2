package com.hostpilot.dao;

import com.hostpilot.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {

    Usuario crear(Usuario usuario) throws DAOException; 

    Usuario actualizar(Usuario usuario) throws DAOException; 

    Optional<Usuario> buscarPorId(Long id) throws DAOException; 

    Optional<Usuario> buscarPorEmail(String email) throws DAOException; 

    List<Usuario> obtenerTodos() throws DAOException; 

    List<Usuario> obtenerUsuariosActivos() throws DAOException; 
    List<Usuario> buscarPorRol(String rol) throws DAOException; 

    boolean desactivar(Long id) throws DAOException;

    boolean reactivar(Long id) throws DAOException;

    
}