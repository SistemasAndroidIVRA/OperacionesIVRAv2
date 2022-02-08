package com.example.operacionesivra.Vistas.Reportes.Inventario.InventarioActual;

public class ModeloInventarioActual {
    String nombre,almacen,fisico,comprometido,disponible;

    public ModeloInventarioActual(String nombre, String almacen, String fisico, String comprometido, String disponible) {
        this.nombre = nombre;
        this.almacen = almacen;
        this.fisico = fisico;
        this.comprometido = comprometido;
        this.disponible = disponible;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAlmacen() {
        return almacen;
    }

    public String getFisico() {
        return fisico;
    }

    public String getComprometido() {
        return comprometido;
    }

    public String getDisponible() {
        return disponible;
    }
}