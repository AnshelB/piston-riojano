package com.mycompany.parcial.modelo;

public abstract class Vehiculo {

    private String patente;
    private String marca;
    private String modelo;
    private int anio;
    private Cliente cliente;   // dueño del vehículo

    protected Vehiculo(String patente, String marca, String modelo, int anio, Cliente cliente) {
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.cliente = cliente;
    }

    public String getPatente() {
        return patente;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getAnio() {
        return anio;
    }

    public Cliente getCliente() {
        return cliente;
    }

    // Cada tipo de vehículo sabe calcular su propio coeficiente
    public abstract double calcularCoeficiente();

    @Override
    public String toString() {
        return patente + " - " + marca + " " + modelo;
    }
}