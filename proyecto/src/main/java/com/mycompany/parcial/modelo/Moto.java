package com.mycompany.parcial.modelo;

public class Moto extends Vehiculo {

    private int cilindrada;

    public Moto(String patente, String marca, String modelo, int anio, Cliente cliente,
                int cilindrada) {
        super(patente, marca, modelo, anio, cliente);
        this.cilindrada = cilindrada;
    }

    public int getCilindrada() {
        return cilindrada;
    }

    // Moto de hasta 250 cc: 0,80. Moto de más de 250 cc: 1,00
    @Override
    public double calcularCoeficiente() {
        if (cilindrada <= 250) {
            return 0.80;
        }
        return 1.00;
    }
}