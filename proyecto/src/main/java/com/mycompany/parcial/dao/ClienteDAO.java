package com.mycompany.parcial.dao;

import com.mycompany.parcial.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla cliente.
 * Todos los métodos usan PreparedStatement y cierran sus recursos solos
 * (try-with-resources). Los errores SQL se propagan: los atrapa la ventana
 * y se los muestra al usuario con un JOptionPane.
 */
public class ClienteDAO {

    public void insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (dni, nombre, apellido, telefono, email) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getDni());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getApellido());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getEmail());   // null se guarda como NULL
            ps.executeUpdate();
        }
    }

    // El DNI es la clave primaria: identifica a qué cliente se modifica y no se cambia
    public void modificar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombre = ?, apellido = ?, telefono = ?, email = ? "
                + "WHERE dni = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDni());
            ps.executeUpdate();
        }
    }

    // Si el cliente tiene vehículos, la FK de la base también rechaza el borrado
    public void eliminar(String dni) throws SQLException {
        String sql = "DELETE FROM cliente WHERE dni = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.executeUpdate();
        }
    }

    public boolean tieneVehiculos(String dni) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehiculo WHERE dni_cliente = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Devuelve el cliente, o null si no existe
    public Cliente buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT dni, nombre, apellido, telefono, email FROM cliente WHERE dni = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return armarCliente(rs);
                }
                return null;
            }
        }
    }

    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT dni, nombre, apellido, telefono, email "
                + "FROM cliente ORDER BY apellido, nombre";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(armarCliente(rs));
            }
        }
        return clientes;
    }

    // Convierte la fila actual del ResultSet en un objeto Cliente
    private Cliente armarCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email"));
    }
}