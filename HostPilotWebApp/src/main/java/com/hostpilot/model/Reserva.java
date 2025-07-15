package com.hostpilot.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId; // NECESARIO para la conversión
import java.util.Date;   // NECESARIO para la conversión

public class Reserva {

    private int id;
    private int idUsuario;
    private int idPropiedad;
    private LocalDateTime fechaReserva;
    private LocalDate fechaCheckin;
    private LocalDate fechaCheckout;
    private String estado;
    private double total;
    private int numeroAdultos;
    private int numeroNinos;
    private int numeroBebes;
    private int numeroMascotas;

    // --- Getters y Setters existentes (mantener como están) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdPropiedad() { return idPropiedad; }
    public void setIdPropiedad(int idPropiedad) { this.idPropiedad = idPropiedad; }
    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }
    public LocalDate getFechaCheckin() { return fechaCheckin; }
    public void setFechaCheckin(LocalDate fechaCheckin) { this.fechaCheckin = fechaCheckin; }
    public LocalDate getFechaCheckout() { return fechaCheckout; }
    public void setFechaCheckout(LocalDate fechaCheckout) { this.fechaCheckout = fechaCheckout; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public int getNumeroAdultos() { return numeroAdultos; }
    public void setNumeroAdultos(int numeroAdultos) { this.numeroAdultos = numeroAdultos; }
    public int getNumeroNinos() { return numeroNinos; }
    public void setNumeroNinos(int numeroNinos) { this.numeroNinos = numeroNinos; }
    public int getNumeroBebes() { return numeroBebes; }
    public void setNumeroBebes(int numeroBebes) { this.numeroBebes = numeroBebes; }
    public int getNumeroMascotas() { return numeroMascotas; }
    public void setNumeroMascotas(int numeroMascotas) { this.numeroMascotas = numeroMascotas; }

    // --- NUEVOS MÉTODOS AUXILIARES PARA JSTL Fmt:formatDate ---
    // Convierten LocalDate a java.util.Date para que fmt:formatDate pueda usarlos
    public Date getFechaCheckinAsUtilDate() {
        if (this.fechaCheckin == null) {
            return null;
        }
        // Convierte LocalDate a Instant, luego a java.util.Date
        return Date.from(this.fechaCheckin.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date getFechaCheckoutAsUtilDate() {
        if (this.fechaCheckout == null) {
            return null;
        }
        // Convierte LocalDate a Instant, luego a java.util.Date
        return Date.from(this.fechaCheckout.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}