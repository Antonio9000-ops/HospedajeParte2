package com.hostpilot.security;


public interface EncryptionService {

    
    String encryptPassword(String password);

    
    boolean verifyPassword(String plainPassword, String hashedPassword);

    
}