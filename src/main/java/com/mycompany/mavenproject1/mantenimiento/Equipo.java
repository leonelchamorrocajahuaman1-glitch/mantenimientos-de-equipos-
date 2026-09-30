package com.mycompany.mavenproject1.mantenimiento;

import java.util.Objects;

public record Equipo(String id, String nombre, String marca, String modelo) {
    public Equipo {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El id del equipo es obligatorio");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del equipo es obligatorio");
    }
    public Equipo(String id, String nombre) { this(id, nombre, "", ""); }
}
