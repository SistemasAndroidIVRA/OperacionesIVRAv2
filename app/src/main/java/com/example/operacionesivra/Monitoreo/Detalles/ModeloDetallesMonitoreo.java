package com.example.operacionesivra.Monitoreo.Detalles;

public class ModeloDetallesMonitoreo {
    String idubicaion,idusuario,latitud,longitud,fechahora;

    public ModeloDetallesMonitoreo(String idubicaion, String idusuario, String latitud, String longitud, String fechahora) {
        this.idubicaion = idubicaion;
        this.idusuario = idusuario;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fechahora = fechahora;
    }

    public String getIdubicaion() {
        return idubicaion;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getFechahora() {
        return fechahora;
    }
}
