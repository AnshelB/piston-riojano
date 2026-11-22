package com.mycompany.parcial.modelo;

public class Repuesto {

    private String codigo;
    private String descripcion;
    private double precioUnitarioActual;
    private int stockDisponible;

    public Repuesto(String codigo, String descripcion, double precioUnitarioActual, int stockDisponible) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precioUnitarioActual = precioUnitarioActual;
        this.stockDisponible = stockDisponible;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecioUnitarioActual() {
        return precioUnitarioActual;
    }

    public void setPrecioUnitarioActual(double precioUnitarioActual) {
        this.precioUnitarioActual = precioUnitarioActual;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(int stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    /**
     * Descuenta stock. Si no alcanza, no modifica nada y lanza la excepción propia.
     */
   public boolean tieneStock(int cantidad) {
    return cantidad <= stockDisponible;
    }

    public void descontarStock(int cantidad) throws StockInsuficienteException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (!tieneStock(cantidad)) {
            throw new StockInsuficienteException("Stock insuficiente de \"" + descripcion
                    + "\": disponible " + stockDisponible + ", solicitado " + cantidad);
        }
        stockDisponible -= cantidad;
    }

    @Override
    public String toString() {
        return codigo + " - " + descripcion;
    }
}