package com.mycompany.parcial.modelo;

public class Auto extends Vehiculo {

    private int cantidadPuertas;
    private TipoCombustible tipoCombustible;

    public Auto(String patente, String marca, String modelo, int anio, Cliente cliente,
                int cantidadPuertas, TipoCombustible tipoCombustible) {
        super(patente, marca, modelo, anio, cliente);
        this.cantidadPuertas = cantidadPuertas;
        this.tipoCombustible = tipoCombustible;
    }

    public int getCantidadPuertas() {
        return cantidadPuertas;
    }

    public TipoCombustible getTipoCombustible() {
        return tipoCombustible;
    }

    // Auto diésel: 1,20. Auto a nafta o GNC: 1,00
    @Override
    public double calcularCoeficiente() {
        if (tipoCombustible == TipoCombustible.DIESEL) {
            return 1.20;
        }
        return 1.00;
    }
}