package com.example.operacionesivra.PantallaRecepcion;

public class ModeloVideos {
    String nombre, url;

    public ModeloVideos(String nombre, String url) {
        this.nombre = nombre;
        this.url = url;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrl() {
        return url;
    }
}
