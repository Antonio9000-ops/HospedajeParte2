package com.hostpilot.service;

import com.hostpilot.dao.UsuarioDAO;
import com.hostpilot.model.Usuario;
import com.hostpilot.security.EncryptionService;
import com.hostpilot.security.SessionManager; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase UsuarioServiceImpl.
 
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioDAO usuarioDAO; // Mock para la capa de acceso a datos.

    @Mock
    private EncryptionService encryptionService; // Mock para la encriptación de contraseñas.


    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;
    
  
    private Usuario usuarioDePrueba;


    @BeforeEach
    void setUp() {
        // Creamos un usuario de prueba estándar.
        usuarioDePrueba = new Usuario();
        usuarioDePrueba.setId(1L);
        usuarioDePrueba.setEmail("test@example.com");
        usuarioDePrueba.setPassword("hashedPassword123"); // Representa una contraseña ya encriptada en la BD.
        usuarioDePrueba.setRol("USER");
        usuarioDePrueba.setActivo(true);
    }



    @Test
    @DisplayName("Autenticación exitosa con credenciales correctas")
    void authenticate_shouldReturnUser_whenCredentialsAreCorrect() throws Exception {
       
        String emailCorrecto = "test@example.com";
        String passwordPlanoCorrecto = "password123";

        when(usuarioDAO.buscarPorEmail(emailCorrecto)).thenReturn(Optional.of(usuarioDePrueba));

        when(encryptionService.verifyPassword(passwordPlanoCorrecto, "hashedPassword123")).thenReturn(true);
  
        when(request.getSession(true)).thenReturn(session);

  
        Optional<Usuario> resultado = usuarioService.authenticate(emailCorrecto, passwordPlanoCorrecto, request);


        assertTrue(resultado.isPresent(), "El resultado debería contener un usuario.");
        assertEquals(emailCorrecto, resultado.get().getEmail(), "El email del usuario autenticado debe ser el correcto.");
        

        verify(usuarioDAO).actualizar(any(Usuario.class)); 
        verify(session).setAttribute(eq("userId"), eq(1L)); 
    }

    @Test
    @DisplayName("Fallo de autenticación si el usuario no existe")
    void authenticate_shouldReturnEmpty_whenUserNotFound() throws Exception {

        String emailInexistente = "noexiste@example.com";
 
        when(usuarioDAO.buscarPorEmail(emailInexistente)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.authenticate(emailInexistente, "cualquierpass", request);


        assertFalse(resultado.isPresent(), "El resultado debería ser un Optional vacío.");
  
        verify(encryptionService, never()).verifyPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("Fallo de autenticación si la contraseña es incorrecta")
    void authenticate_shouldReturnEmpty_whenPasswordIsIncorrect() throws Exception {
    
        String emailCorrecto = "test@example.com";
        String passwordIncorrecto = "wrongpassword";
   
        when(usuarioDAO.buscarPorEmail(emailCorrecto)).thenReturn(Optional.of(usuarioDePrueba));
        when(encryptionService.verifyPassword(passwordIncorrecto, "hashedPassword123")).thenReturn(false);


        Optional<Usuario> resultado = usuarioService.authenticate(emailCorrecto, passwordIncorrecto, request);


        assertFalse(resultado.isPresent(), "El resultado debería ser un Optional vacío.");
 
        verify(request, never()).getSession(anyBoolean());
    }
    
    @Test
    @DisplayName("Fallo de autenticación si el usuario no está activo")
    void authenticate_shouldReturnEmpty_whenUserIsNotActive() throws Exception {

        usuarioDePrueba.setActivo(false); // Marcamos a nuestro usuario de prueba como inactivo.
        when(usuarioDAO.buscarPorEmail("test@example.com")).thenReturn(Optional.of(usuarioDePrueba));


        Optional<Usuario> resultado = usuarioService.authenticate("test@example.com", "password123", request);


        assertFalse(resultado.isPresent(), "El resultado debería ser un Optional vacío para un usuario inactivo.");

        verify(encryptionService, never()).verifyPassword(anyString(), anyString());
    }

    
}