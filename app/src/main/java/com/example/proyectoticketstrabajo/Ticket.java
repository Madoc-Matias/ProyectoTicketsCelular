package com.example.proyectoticketstrabajo;

public class Ticket {

    private int id;
    private String titulo;
    private String descripcion;
    private String estado;
    private int fallos;  // Nuevo campo para almacenar los fallos

    // Constructor por defecto (vacío)
    public Ticket() {
    }

    // Constructor con parámetros
    public Ticket(int id, String titulo, String descripcion, String estado, int fallos) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fallos = fallos;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getFallos() {
        return fallos;
    }

    public void setFallos(int fallos) {
        this.fallos = fallos;
    }
}

