package com.mycompany.mavenproject1.mantenimiento;

import java.time.LocalDate;
import java.util.List;

/** Consulta de solo lectura de las órdenes registradas. */
public record Historial(List<OrdenMantenimiento> ordenes) {
    public Historial {
        ordenes = List.copyOf(ordenes);
    }
    public int size() { return ordenes.size(); }
    public List<OrdenMantenimiento> listar() { return ordenes; }
    public List<OrdenMantenimiento> proximosVencimientos(LocalDate desde, int dias) {
        if (desde == null) throw new IllegalArgumentException("La fecha inicial es obligatoria");
        if (dias < 0) throw new IllegalArgumentException("El número de días no puede ser negativo");
        LocalDate hasta = desde.plusDays(dias);
        return ordenes.stream().filter(o -> o.estaAbierta() && !o.fechaProgramada().isBefore(desde) && !o.fechaProgramada().isAfter(hasta)).toList();
    }
}
