package com.mycompany.mavenproject1.mantenimiento;

public record Tecnico(String id, String nombre) {
    public Tecnico {
        if (id == null || id.isBlank() || nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El técnico requiere id y nombre");
    }
}
