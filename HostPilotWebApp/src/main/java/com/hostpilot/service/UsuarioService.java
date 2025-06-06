package com.hostpilot.service;

import com.hostpilot.exception.AppException; 
import com.hostpilot.model.Usuario;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    
    Optional<Usuario> authenticate(String email, String password, HttpServletRequest request) throws AppException;

    void logout(HttpServletRequest request);

    Usuario registrarUsuario(Usuario usuario) throws AppException;

    Usuario actualizarUsuario(Usuario usuario) throws AppException;

    Optional<Usuario> buscarPorId(Long id) throws AppException;

    Optional<Usuario> buscarPorEmail(String email) throws AppException;

    List<Usuario> obtenerTodosLosUsuarios() throws AppException;
}