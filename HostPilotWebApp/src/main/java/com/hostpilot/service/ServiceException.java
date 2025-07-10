package com.hostpilot.service;

/**
 * Excepción personalizada para la capa de servicio.
 * Se usa para encapsular errores de lógica de negocio o errores propagados desde la capa DAO.
 */
public class ServiceException extends Exception {

    /**
     * Constructor que toma un mensaje de error.
     * @param message El mensaje que describe el error.
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Constructor que toma un mensaje de error y la causa original (excepción anidada).
     * @param message El mensaje que describe el error.
     * @param cause La excepción original que causó este error (ej. una DAOException).
     */
public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Obtiene un mensaje amigable para el usuario.
     * Por ahora, simplemente devuelve el mensaje de la excepción, pero podría tener lógica
     * para traducir códigos de error en el futuro.
     * @return Un string con el mensaje de error.
     */
    public String getUserFriendlyMessage() {
        return this.getMessage();
    }
    
}