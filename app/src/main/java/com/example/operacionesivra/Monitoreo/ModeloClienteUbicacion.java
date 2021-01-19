package com.example.operacionesivra.Monitoreo;

import com.google.android.gms.maps.model.LatLng;

public class ModeloClienteUbicacion {
    String Nombre;
    LatLng ubicacion;

    public ModeloClienteUbicacion(String nombre, LatLng ubicacion) {
        Nombre = nombre;
        this.ubicacion = ubicacion;
    }

    public String getNombre() {
        return Nombre;
    }

    public LatLng getUbicacion() {
        return ubicacion;
    }
}
