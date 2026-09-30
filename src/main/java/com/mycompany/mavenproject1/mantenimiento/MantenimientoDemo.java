package com.mycompany.mavenproject1.mantenimiento;

import java.time.LocalDate;

/** Prueba de aceptación ejecutable desde NetBeans con Run File. */
public class MantenimientoDemo {
    public static void main(String[] args) {
        MantenimientoService service = new MantenimientoService();
        service.registrarEquipo(new Equipo("EQ-01", "Compresor principal", "Atlas", "C-200"));
        service.registrarTecnico(new Tecnico("TEC-01", "Ana Pérez"));

        LocalDate fecha = LocalDate.now().plusDays(7);
        service.programar("OM-01", "EQ-01", "TEC-01", TipoMantenimiento.PREVENTIVO, fecha);
        if (service.proximosVencimientos(LocalDate.now(), 30).size() != 1)
            throw new AssertionError("La orden abierta debe aparecer como próximo vencimiento");
        System.out.println("OK: próximo vencimiento consultado");
        try {
            service.programar("OM-02", "EQ-01", "TEC-01", TipoMantenimiento.PREVENTIVO, fecha);
            throw new AssertionError("Debió rechazarse la preventiva duplicada");
        } catch (MantenimientoException esperado) {
            System.out.println("OK: duplicado preventivo rechazado");
        }

        try {
            service.cerrar("OM-01", LocalDate.now(), "   ");
            throw new AssertionError("El cierre sin descripción debe rechazarse");
        } catch (MantenimientoException esperado) {
            System.out.println("OK: cierre sin descripción rechazado");
        }

        try {
            service.cerrar("OM-01", LocalDate.now(), "Cambio de filtros y limpieza general");
            System.out.println("OK: orden cerrada");
            if (!service.proximosVencimientos(LocalDate.now(), 30).isEmpty())
                throw new AssertionError("Una orden cerrada no debe aparecer como vencimiento");
            service.cerrar("OM-01", LocalDate.now(), "Segundo cierre");
            throw new AssertionError("No se debe cerrar dos veces");
        } catch (MantenimientoException esperado) {
            System.out.println("OK: segundo cierre rechazado");
        }
        System.out.println("Órdenes en historial: " + service.historial().size());
        System.out.println("Próximos vencimientos: " + service.proximosVencimientos(LocalDate.now(), 30).size());
    }
}
