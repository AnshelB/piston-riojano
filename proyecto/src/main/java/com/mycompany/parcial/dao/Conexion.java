package com.mycompany.parcial.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestiona la conexion a la base de datos PostgreSQL.
 * Carga las credenciales de conexion desde db.properties en resources.
 */
public class Conexion {

    private static String url;
    private static String user;
    private static String pass;

    static {
        Properties prop = new Properties();
        try (InputStream input = Conexion.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                prop.load(input);
                url = prop.getProperty("db.url");
                user = prop.getProperty("db.user");
                pass = prop.getProperty("db.password");
            } else {
                System.err.println("Aviso: No se encontro el archivo db.properties en resources.");
            }
        } catch (IOException e) {
            System.err.println("Error al cargar db.properties: " + e.getMessage());
        }
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
