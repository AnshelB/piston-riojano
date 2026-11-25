package com.mycompany.parcial.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdenTrabajo {

    private int numero;
    private LocalDate fechaIngreso;
    private String descripcionProblema;
    private double horasManoDeObra;
    private EstadoOrden estado;
    private Vehiculo vehiculo;
    private Mecanico mecanico;
    private List<DetalleOrden> detalles = new ArrayList<>();

    public OrdenTrabajo(int numero, LocalDate fechaIngreso, String descripcionProblema,
                        double horasManoDeObra, EstadoOrden estado,
                        Vehiculo vehiculo, Mecanico mecanico) {
        this.numero = numero;
        this.fechaIngreso = fechaIngreso;
        this.descripcionProblema = descripcionProblema;
        this.horasManoDeObra = horasManoDeObra;
        this.estado = estado;
        this.vehiculo = vehiculo;
        this.mecanico = mecanico;
    }

    public OrdenTrabajo(Vehiculo vehiculo, Mecanico mecanico,
                        String descripcionProblema, double horasManoDeObra) {
        this(0, LocalDate.now(), descripcionProblema, horasManoDeObra,
                EstadoOrden.PENDIENTE, vehiculo, mecanico);
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public String getDescripcionProblema() {
        return descripcionProblema;
    }

    public double getHorasManoDeObra() {
        return horasManoDeObra;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public List<DetalleOrden> getDetalles() {
        return detalles;
    }

     // La orden solo puede avanzar al estado siguiente:
     //PENDIENTE -> EN_REPARACION -> FINALIZADA -> ENTREGADA.
    public void cambiarEstado(EstadoOrden nuevoEstado) {
        if (nuevoEstado.ordinal() != estado.ordinal() + 1) {
            throw new IllegalStateException(
                    "No se puede pasar de " + estado + " a " + nuevoEstado);
        }
        this.estado = nuevoEstado;
    }

    public void agregarRepuesto(Repuesto repuesto, int cantidad) throws StockInsuficienteException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        repuesto.descontarStock(cantidad);

        for (DetalleOrden d : detalles) {
            if (d.getRepuesto().getCodigo().equals(repuesto.getCodigo())) {
                d.sumarCantidad(cantidad);
                return;
            }
        }
        detalles.add(new DetalleOrden(repuesto, cantidad, repuesto.getPrecioUnitarioActual()));
    }

    // Mano de obra = horas * valor hora del mecánico * coeficiente del vehículo
    public double calcularManoDeObra() {
        return horasManoDeObra * mecanico.getValorHora() * vehiculo.calcularCoeficiente();
    }

    // Total = mano de obra + suma de los subtotales de los repuestos
    // Suma de los subtotales de todos los repuestos usados
    public double calcularTotalRepuestos() {
        double total = 0;
        for (DetalleOrden d : detalles) {
            total += d.calcularSubtotal();
        }
        return total;
    }

    // Total = mano de obra + repuestos
    public double calcularTotal() {
        return calcularManoDeObra() + calcularTotalRepuestos();
    }
}