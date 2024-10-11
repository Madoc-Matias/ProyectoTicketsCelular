package com.example.proyectoticketstrabajo;

public class Ticket {

    private int id;
    private String titulo;
    private String descripcion;
    private String estado;
    private int fallos;
    private int tecnicoId;  // Nuevo campo para el ID del técnico asignado

    // Constructor por defecto
    public Ticket() {
    }

    // Constructor con parámetros
    public Ticket(int id, String titulo, String descripcion, String estado, int fallos, int tecnicoId) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fallos = fallos;
        this.tecnicoId = tecnicoId;  // Asignar ID del técnico
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

    // En la clase Ticket, asegurarse de que el método getTecnicoId esté bien implementado:
    public int getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(int tecnicoId) {
        this.tecnicoId = tecnicoId;
    }
}
