package com.mycompany.mavenproject1.mantenimiento;

import java.time.LocalDate;
import java.util.Objects;

public final class OrdenMantenimiento {
    private final String id;
    private final Equipo equipo;
    private final Tecnico tecnico;
    private final TipoMantenimiento tipo;
    private final LocalDate fechaProgramada;
    private LocalDate fechaCierre;
    private String descripcion;
    private EstadoOrden estado;

    public OrdenMantenimiento(String id, Equipo equipo, Tecnico tecnico, TipoMantenimiento tipo, LocalDate fechaProgramada) {
        if (id == null || id.isBlank() || equipo == null || tecnico == null || tipo == null || fechaProgramada == null)
            throw new IllegalArgumentException("La orden requiere id, equipo, técnico, tipo y fecha");
        this.id = id; this.equipo = equipo; this.tecnico = tecnico; this.tipo = tipo;
        this.fechaProgramada = fechaProgramada; this.estado = EstadoOrden.ABIERTA;
    }
    public void cerrar(LocalDate fecha, String descripcion) {
        if (fecha == null) throw new IllegalArgumentException("La fecha de cierre es obligatoria");
        if (descripcion == null || descripcion.isBlank()) throw new IllegalArgumentException("La descripción de cierre es obligatoria");
        if (estado == EstadoOrden.CERRADA) throw new IllegalStateException("La orden ya está cerrada");
        this.fechaCierre = fecha; this.descripcion = descripcion.trim(); this.estado = EstadoOrden.CERRADA;
    }
    public String id() { return id; }
    public Equipo equipo() { return equipo; }
    public Tecnico tecnico() { return tecnico; }
    public TipoMantenimiento tipo() { return tipo; }
    public LocalDate fechaProgramada() { return fechaProgramada; }
    public LocalDate fechaCierre() { return fechaCierre; }
    public String descripcion() { return descripcion; }
    public EstadoOrden estado() { return estado; }
    public boolean estaAbierta() { return estado == EstadoOrden.ABIERTA; }
}
