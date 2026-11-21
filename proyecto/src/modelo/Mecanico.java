package com.mycompany.parcial.modelo;

public class Mecanico {

    private int legajo;
    private String nombre;
    private String apellido;
    private Especialidad especialidad;
    private double valorHora;

    public Mecanico(int legajo, String nombre, String apellido,
                    Especialidad especialidad, double valorHora) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
        this.valorHora = valorHora;
    }

    public int getLegajo() {
        return legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public double getValorHora() {
        return valorHora;
    }

    // Es lo que se ve cuando el mecánico se muestra en un JComboBox
    @Override
    public String toString() {
        return apellido + ", " + nombre + " (" + especialidad + ")";
    }
}