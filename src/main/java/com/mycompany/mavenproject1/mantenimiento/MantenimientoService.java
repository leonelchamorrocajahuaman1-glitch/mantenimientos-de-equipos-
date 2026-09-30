package com.mycompany.mavenproject1.mantenimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** Casos de uso del módulo: equipos, órdenes, cierre, historial y vencimientos. */
public class MantenimientoService {
    private final Map<String, Equipo> equipos = new ConcurrentHashMap<>();
    private final Map<String, Tecnico> tecnicos = new ConcurrentHashMap<>();
    private final Map<String, OrdenMantenimiento> ordenes = new ConcurrentHashMap<>();

    public void registrarEquipo(Equipo equipo) { Objects.requireNonNull(equipo); if (equipos.putIfAbsent(equipo.id(), equipo) != null) throw new MantenimientoException("El equipo ya existe"); }
    public void registrarTecnico(Tecnico tecnico) { Objects.requireNonNull(tecnico); if (tecnicos.putIfAbsent(tecnico.id(), tecnico) != null) throw new MantenimientoException("El técnico ya existe"); }

    public synchronized OrdenMantenimiento programar(String idOrden, String idEquipo, String idTecnico, TipoMantenimiento tipo, LocalDate fecha) {
        if (idOrden == null || idOrden.isBlank()) throw new MantenimientoException("El id de la orden es obligatorio");
        if (ordenes.containsKey(idOrden)) throw new MantenimientoException("La orden ya existe");
        Equipo equipo = equipos.get(idEquipo); Tecnico tecnico = tecnicos.get(idTecnico);
        if (equipo == null) throw new MantenimientoException("Equipo no encontrado");
        if (tecnico == null) throw new MantenimientoException("Técnico no encontrado");
        if (fecha == null) throw new MantenimientoException("La fecha es obligatoria");
        if (tipo == TipoMantenimiento.PREVENTIVO && ordenes.values().stream().anyMatch(o -> o.estaAbierta() && o.equipo().id().equals(idEquipo) && o.tipo() == tipo && o.fechaProgramada().equals(fecha)))
            throw new MantenimientoException("Ya existe una orden preventiva abierta para el equipo y fecha indicados");
        OrdenMantenimiento orden = new OrdenMantenimiento(idOrden, equipo, tecnico, tipo, fecha);
        ordenes.put(idOrden, orden); return orden;
    }

    public void cerrar(String idOrden, LocalDate fecha, String descripcion) {
        OrdenMantenimiento orden = buscarOrden(idOrden);
        try { orden.cerrar(fecha, descripcion); } catch (IllegalArgumentException | IllegalStateException ex) { throw new MantenimientoException(ex.getMessage()); }
    }
    public OrdenMantenimiento buscarOrden(String id) { OrdenMantenimiento o = ordenes.get(id); if (o == null) throw new MantenimientoException("Orden no encontrada"); return o; }
    public List<Equipo> listarEquipos() { return new ArrayList<>(equipos.values()); }
    public Historial historial() { return new Historial(ordenes.values().stream().sorted(Comparator.comparing(OrdenMantenimiento::fechaProgramada)).toList()); }
    public List<OrdenMantenimiento> proximosVencimientos(LocalDate desde, int dias) {
        if (desde == null) throw new MantenimientoException("La fecha inicial es obligatoria");
        if (dias < 0) throw new MantenimientoException("El número de días no puede ser negativo");
        LocalDate hasta = desde.plusDays(dias);
        return ordenes.values().stream().filter(o -> o.estaAbierta() && !o.fechaProgramada().isBefore(desde) && !o.fechaProgramada().isAfter(hasta)).sorted(Comparator.comparing(OrdenMantenimiento::fechaProgramada)).toList();
    }
}
