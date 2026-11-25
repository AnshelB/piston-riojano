package com.mycompany.parcial.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.parcial.modelo.Auto;
import com.mycompany.parcial.modelo.Cliente;
import com.mycompany.parcial.modelo.Moto;
import com.mycompany.parcial.modelo.TipoCombustible;
import com.mycompany.parcial.modelo.Vehiculo;


public class VehiculoDAO {

    // Trae también los datos del dueño para armar el objeto Cliente
    private static final String SELECT_BASE =
            "SELECT v.patente, v.marca, v.modelo, v.anio, v.tipo, "
            + "v.cantidad_puertas, v.tipo_combustible, v.cilindrada, "
            + "c.dni, c.nombre, c.apellido, c.telefono, c.email "
            + "FROM vehiculo v JOIN cliente c ON c.dni = v.dni_cliente ";

    public void insertar(Vehiculo v) throws SQLException {
        String sql = "INSERT INTO vehiculo (patente, marca, modelo, anio, dni_cliente, tipo, "
                + "cantidad_puertas, tipo_combustible, cilindrada) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPatente());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            ps.setString(5, v.getCliente().getDni());

            if (v instanceof Auto auto) {
                ps.setString(6, "AUTO");
                ps.setInt(7, auto.getCantidadPuertas());
                ps.setString(8, auto.getTipoCombustible().name());
                ps.setNull(9, Types.INTEGER);
            } else if (v instanceof Moto moto) {
                ps.setString(6, "MOTO");
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.VARCHAR);
                ps.setInt(9, moto.getCilindrada());
            } else {
                throw new IllegalArgumentException("Tipo de vehículo desconocido");
            }
            ps.executeUpdate();
        }
    }

    public Vehiculo buscarPorPatente(String patente) throws SQLException {
        String sql = SELECT_BASE + "WHERE v.patente = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return armarVehiculo(rs);
                }
                return null;
            }
        }
    }

    public List<Vehiculo> listarPorCliente(String dni) throws SQLException {
        String sql = SELECT_BASE + "WHERE v.dni_cliente = ? ORDER BY v.patente";
        List<Vehiculo> vehiculos = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(armarVehiculo(rs));
                }
            }
        }
        return vehiculos;
    }

    public List<Vehiculo> listarTodos() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY v.patente";
        List<Vehiculo> vehiculos = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vehiculos.add(armarVehiculo(rs));
            }
        }
        return vehiculos;
    }

    // Convierte la fila en un Auto o Moto según la columna "tipo"
    private Vehiculo armarVehiculo(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email"));

        String patente = rs.getString("patente");
        String marca = rs.getString("marca");
        String modelo = rs.getString("modelo");
        int anio = rs.getInt("anio");

        if ("AUTO".equals(rs.getString("tipo"))) {
            return new Auto(patente, marca, modelo, anio, cliente,
                    rs.getInt("cantidad_puertas"),
                    TipoCombustible.valueOf(rs.getString("tipo_combustible")));
        }
        return new Moto(patente, marca, modelo, anio, cliente, rs.getInt("cilindrada"));
    }
}
