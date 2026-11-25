package com.mycompany.parcial.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.parcial.modelo.Especialidad;
import com.mycompany.parcial.modelo.Mecanico;

public class MecanicoDAO {

    public List<Mecanico> listar() throws SQLException {
        String sql = "SELECT legajo, nombre, apellido, especialidad, valor_hora "
                + "FROM mecanico ORDER BY apellido, nombre";
        List<Mecanico> mecanicos = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mecanicos.add(armarMecanico(rs));
            }
        }
        return mecanicos;
    }

    // Devuelve el mecánico, o null si no existe
    public Mecanico buscarPorLegajo(int legajo) throws SQLException {
        String sql = "SELECT legajo, nombre, apellido, especialidad, valor_hora "
                + "FROM mecanico WHERE legajo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, legajo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return armarMecanico(rs);
                }
                return null;
            }
        }
    }

    private Mecanico armarMecanico(ResultSet rs) throws SQLException {
        return new Mecanico(
                rs.getInt("legajo"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                Especialidad.valueOf(rs.getString("especialidad")),
                rs.getDouble("valor_hora"));
    }
}
