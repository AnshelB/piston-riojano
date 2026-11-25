package com.mycompany.parcial.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.parcial.modelo.Repuesto;

public class RepuestoDAO {

    public List<Repuesto> listar() throws SQLException {
        String sql = "SELECT codigo, descripcion, precio_unitario_actual, stock_disponible "
                + "FROM repuesto ORDER BY descripcion";
        List<Repuesto> repuestos = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                repuestos.add(armarRepuesto(rs));
            }
        }
        return repuestos;
    }

    public Repuesto buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT codigo, descripcion, precio_unitario_actual, stock_disponible "
                + "FROM repuesto WHERE codigo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return armarRepuesto(rs);
                }
                return null;
            }
        }
    }

    public void actualizar(Repuesto r) throws SQLException {
        String sql = "UPDATE repuesto SET precio_unitario_actual = ?, stock_disponible = ? "
                + "WHERE codigo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, r.getPrecioUnitarioActual());
            ps.setInt(2, r.getStockDisponible());
            ps.setString(3, r.getCodigo());
            ps.executeUpdate();
        }
    }

    private Repuesto armarRepuesto(ResultSet rs) throws SQLException {
        return new Repuesto(
                rs.getString("codigo"),
                rs.getString("descripcion"),
                rs.getDouble("precio_unitario_actual"),
                rs.getInt("stock_disponible"));
    }
}
