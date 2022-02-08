package com.example.operacionesivra.Vistas.Chequeo.Surtir;

public class ModeloDetallesChequeo {
    String Material, Cantidad, Unidad;
    int estado;
    Boolean correcto;

    public ModeloDetallesChequeo(String material, String cantidad, String unidad, int estado, boolean correcto) {
        this.Material = material;
        this.Cantidad = cantidad;
        this.Unidad = unidad;
        this.estado = estado;
        this.correcto = correcto;
    }

    public String getMaterial() {
        return Material;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public String getUnidad() {
        return Unidad;
    }

    public int getEstado() {
        return estado;
    }

    public boolean getCorrecto() {
        return correcto;
    }
}
