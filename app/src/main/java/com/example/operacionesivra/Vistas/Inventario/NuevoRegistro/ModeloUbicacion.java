package com.example.operacionesivra.Vistas.Inventario.NuevoRegistro;

public class ModeloUbicacion {
    String ubicacionId;
    String nombre;

    public ModeloUbicacion(String ubicacionId, String nombre) {
        this.ubicacionId = ubicacionId;
        this.nombre = nombre;
    }

    public String getUbicacionId() {
        return ubicacionId;
    }

    public String getNombre() {
        return nombre;
    }
}
