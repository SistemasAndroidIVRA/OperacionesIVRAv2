package com.example.operacionesivra.Vistas.Reportes.Inventario.InventarioActual;

public class ModeloInventarioActualBuscar {
    String nombre,id;

    public ModeloInventarioActualBuscar(String nombre, String id) {
        this.nombre = nombre;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getId() {
        return id;
    }
}
