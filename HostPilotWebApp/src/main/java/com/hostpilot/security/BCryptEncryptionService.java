package com.hostpilot.security;

import com.hostpilot.config.ConfigManager;
import org.mindrot.jbcrypt.BCrypt;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación de EncryptionService que utiliza el algoritmo BCrypt.

 */
public class BCryptEncryptionService implements EncryptionService {

    private static final Logger LOGGER = Logger.getLogger(BCryptEncryptionService.class.getName());


    private static final int LOG_ROUNDS = ConfigManager.BCRYPT_LOG_ROUNDS;

    @Override
    public String encryptPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía.");
        }
       
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
    }

    @Override
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || plainPassword.isEmpty() || hashedPassword.isEmpty()) {
            return false;
        }
        try {
           
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
         
            LOGGER.log(Level.WARNING, "Se intentó verificar una contraseña contra un hash con formato inválido.", e);
            return false;
        }
    }
}