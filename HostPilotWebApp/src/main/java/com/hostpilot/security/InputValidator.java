package com.hostpilot.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger; // IMPORT AÑADIDO

/**
 * Clase para validar y sanitizar entradas de usuario.

 */
public class InputValidator {

    // Patrones de validación
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PHONE_PATTERN = "^[+]?[0-9]{10,15}$";
    private static final String NAME_PATTERN = "^[a-zA-ZáéíóúüÁÉÍÓÚÜñÑ'\\- ]{2,100}$";
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]{3,20}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,100}$";

    // Logger para advertencias internas de la clase
    private static final Logger LOGGER = Logger.getLogger(InputValidator.class.getName()); // CORREGIDO (Ahora el import existe)


    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.trim().toLowerCase().matches(EMAIL_PATTERN);
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; 
        }
        return phone.replaceAll("[\\s()-]+", "").matches(PHONE_PATTERN);
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.trim().matches(NAME_PATTERN);
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.matches(PASSWORD_PATTERN);
    }

    public static boolean isValidGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            return true; 
        }
        String genderLower = gender.trim().toLowerCase();
        Set<String> allowedGenders = new HashSet<>(Arrays.asList(
                "masculino", "femenino", "otro", "prefiero no decirlo"
        ));
        return allowedGenders.contains(genderLower);
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return username.trim().matches(USERNAME_PATTERN);
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Encadenamiento correcto de replace
        return input.replace("&", "&")
                    .replace("<", "<")
                    .replace(">", ">")
                    .replace("\"", "")  
                    .replace("'", "'")   
                    .replace("/", "/");
    }

  
    public static boolean isPotentiallyUnsafeForSQL(String input) {
        if (input == null) {
            return false; 
        }
        String lowerInput = input.toLowerCase();
        
        String[] commonSqlPatterns = {"'", "\"", ";", "--", " union ", " drop ", " delete ", " insert ", " update "};
        for (String pattern : commonSqlPatterns) {
            if (lowerInput.contains(pattern)) {
                LOGGER.warning("Entrada potencialmente insegura para SQL detectada: '" + input + "' contiene '" + pattern + "'");
                return true; 
            }
        }
        return false;
    }

   
    public static String sanitizeForSQL(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replace("'", "''"); 
    }

    public static boolean isValidLength(String input, int minLength, int maxLength) {
        if (input == null) {
            return minLength == 0; 
        }
        int length = input.trim().length();
        return length >= minLength && length <= maxLength;
    }

    public static boolean isAlphanumeric(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return input.matches("^[a-zA-Z0-9]+$");
    }

    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isValidIntRange(String value, int min, int max) {
        if (value == null || value.trim().isEmpty()) {
            return false; 
        }
        try {
            int intValue = Integer.parseInt(value.trim());
            return intValue >= min && intValue <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String cleanSpecialCharacters(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[^a-zA-Z0-9\\s]", "");
    }
}