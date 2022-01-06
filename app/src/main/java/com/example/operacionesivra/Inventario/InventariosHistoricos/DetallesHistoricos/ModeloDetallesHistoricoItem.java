package com.example.operacionesivra.Inventario.InventariosHistoricos.DetallesHistoricos;

public class ModeloDetallesHistoricoItem {
    String cantidad;
    String longitud;
    String ubicacion;
    String materialregistrado;
    String incidencias;

    public ModeloDetallesHistoricoItem(String cantidad, String longitud, String ubicacion, String materialregistrado, String incidencias) {
        this.cantidad = cantidad;
        this.longitud = longitud;
        this.ubicacion = ubicacion;
        this.materialregistrado = materialregistrado;
        this.incidencias = incidencias;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getMaterialregistrado() {
        return materialregistrado;
    }

    public String getIncidencias(){ return incidencias;}

}
