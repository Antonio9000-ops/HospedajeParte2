package com.mycompany.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hostpilot_clean?serverTimezone=UTC&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678";


    public static Connection getConnection() throws SQLException {
        try {
            // Carga explícitamente el driver JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo cargar el driver JDBC de MySQL", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
