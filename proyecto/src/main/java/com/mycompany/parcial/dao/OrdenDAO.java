package com.mycompany.parcial.dao;

import com.mycompany.parcial.modelo.DetalleOrden;
import com.mycompany.parcial.modelo.EstadoOrden;
import com.mycompany.parcial.modelo.Mecanico;
import com.mycompany.parcial.modelo.OrdenTrabajo;
import com.mycompany.parcial.modelo.Repuesto;
import com.mycompany.parcial.modelo.StockInsuficienteException;
import com.mycompany.parcial.modelo.Vehiculo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de las órdenes de trabajo y sus detalles.
 */
public class OrdenDAO {

    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final MecanicoDAO mecanicoDAO = new MecanicoDAO();

    // ------------------------------------------------------------------
    //  RF4: guardar la orden, su detalle y el descuento de stock
    //  en UNA SOLA TRANSACCIÓN: o se graba todo, o no se graba nada.
    // ------------------------------------------------------------------
    public void guardar(OrdenTrabajo orden) throws SQLException, StockInsuficienteException {
        try (Connection con = Conexion.getConexion()) {
            con.setAutoCommit(false);   // desde acá, nada es definitivo hasta el commit
            try {
                insertarCabecera(con, orden);
                for (DetalleOrden d : orden.getDetalles()) {
                    descontarStock(con, d);
                    insertarDetalle(con, orden.getNumero(), d);
                }
                con.commit();           // todo salió bien: se confirma
            } catch (SQLException | StockInsuficienteException e) {
                con.rollback();         // algo falló: se deshace TODO lo anterior
                orden.setNumero(0);     // la orden no quedó guardada
                throw e;                // la ventana mostrará el mensaje al usuario
            }
        }
    }

    private void insertarCabecera(Connection con, OrdenTrabajo o) throws SQLException {
        String sql = "INSERT INTO orden_trabajo (fecha_ingreso, descripcion_problema, "
                + "horas_mano_obra, estado, patente, legajo_mecanico) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        // Se pide a la base el número generado (SERIAL) para usarlo en los detalles
        try (PreparedStatement ps = con.prepareStatement(sql, new String[]{"numero"})) {
            ps.setDate(1, Date.valueOf(o.getFechaIngreso()));
            ps.setString(2, o.getDescripcionProblema());
            ps.setDouble(3, o.getHorasManoDeObra());
            ps.setString(4, o.getEstado().name());
            ps.setString(5, o.getVehiculo().getPatente());
            ps.setInt(6, o.getMecanico().getLegajo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                o.setNumero(rs.getInt(1));
            }
        }
    }

    // El UPDATE solo se aplica si el stock alcanza; si no, no toca ninguna fila
    private void descontarStock(Connection con, DetalleOrden d)
            throws SQLException, StockInsuficienteException {
        String sql = "UPDATE repuesto SET stock_disponible = stock_disponible - ? "
                + "WHERE codigo = ? AND stock_disponible >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getCantidad());
            ps.setString(2, d.getRepuesto().getCodigo());
            ps.setInt(3, d.getCantidad());
            if (ps.executeUpdate() == 0) {
                throw new StockInsuficienteException("Stock insuficiente de \""
                        + d.getRepuesto().getDescripcion() + "\" al guardar la orden");
            }
        }
    }

    private void insertarDetalle(Connection con, int numeroOrden, DetalleOrden d)
            throws SQLException {
        String sql = "INSERT INTO detalle_orden (numero_orden, codigo_repuesto, cantidad, "
                + "precio_aplicado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, numeroOrden);
            ps.setString(2, d.getRepuesto().getCodigo());
            ps.setInt(3, d.getCantidad());
            ps.setDouble(4, d.getPrecioAplicado());
            ps.executeUpdate();
        }
    }

    // ------------------------------------------------------------------
    //  RF5: consulta de órdenes (estado == null devuelve todas)
    // ------------------------------------------------------------------
    public List<OrdenTrabajo> listar(EstadoOrden estado) throws SQLException {
        String sql = "SELECT numero, fecha_ingreso, descripcion_problema, horas_mano_obra, "
                + "estado, patente, legajo_mecanico FROM orden_trabajo ";
        if (estado != null) {
            sql += "WHERE estado = ? ";
        }
        sql += "ORDER BY numero";

        List<OrdenTrabajo> ordenes = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (estado != null) {
                ps.setString(1, estado.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehiculo vehiculo = vehiculoDAO.buscarPorPatente(rs.getString("patente"));
                    Mecanico mecanico = mecanicoDAO.buscarPorLegajo(rs.getInt("legajo_mecanico"));
                    OrdenTrabajo orden = new OrdenTrabajo(
                            rs.getInt("numero"),
                            rs.getDate("fecha_ingreso").toLocalDate(),
                            rs.getString("descripcion_problema"),
                            rs.getDouble("horas_mano_obra"),
                            EstadoOrden.valueOf(rs.getString("estado")),
                            vehiculo,
                            mecanico);
                    cargarDetalles(orden);
                    ordenes.add(orden);
                }
            }
        }
        return ordenes;
    }

    // Lee los repuestos usados, con el precio que se aplicó en su momento
    private void cargarDetalles(OrdenTrabajo orden) throws SQLException {
        String sql = "SELECT d.cantidad, d.precio_aplicado, r.codigo, r.descripcion, "
                + "r.precio_unitario_actual, r.stock_disponible "
                + "FROM detalle_orden d JOIN repuesto r ON r.codigo = d.codigo_repuesto "
                + "WHERE d.numero_orden = ? ORDER BY r.descripcion";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orden.getNumero());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Repuesto repuesto = new Repuesto(
                            rs.getString("codigo"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio_unitario_actual"),
                            rs.getInt("stock_disponible"));
                    // Se agrega directo a la lista (no con agregarRepuesto, que descontaría stock)
                    orden.getDetalles().add(new DetalleOrden(
                            repuesto, rs.getInt("cantidad"), rs.getDouble("precio_aplicado")));
                }
            }
        }
    }

    // Guarda el estado actual de la orden (RF5: cambiar el estado de la orden seleccionada)
    public void actualizarEstado(OrdenTrabajo orden) throws SQLException {
        String sql = "UPDATE orden_trabajo SET estado = ? WHERE numero = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, orden.getEstado().name());
            ps.setInt(2, orden.getNumero());
            ps.executeUpdate();
        }
    }
}