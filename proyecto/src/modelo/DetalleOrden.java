package com.mycompany.parcial.modelo;

/**
 * Una línea de la orden: qué repuesto se usó, cuántos y a qué precio.
 * El precio se guarda aquí porque el precio actual del repuesto puede cambiar.
 */
public class DetalleOrden {

    private Repuesto repuesto;
    private int cantidad;
    private double precioAplicado;

    public DetalleOrden(Repuesto repuesto, int cantidad, double precioAplicado) {
        this.repuesto = repuesto;
        this.cantidad = cantidad;
        this.precioAplicado = precioAplicado;
    }

    public Repuesto getRepuesto() {
        return repuesto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioAplicado() {
        return precioAplicado;
    }

    // Se usa si el mismo repuesto se agrega dos veces a la misma orden
    public void sumarCantidad(int cantidadExtra) {
        this.cantidad += cantidadExtra;
    }

    public double calcularSubtotal() {
        return cantidad * precioAplicado;
    }
}