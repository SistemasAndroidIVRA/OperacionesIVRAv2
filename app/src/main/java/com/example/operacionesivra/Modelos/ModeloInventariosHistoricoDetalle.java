package com.example.operacionesivra.Modelos;

public class ModeloInventariosHistoricoDetalle {
    String usuario;
    String cantidad;
    String longitud;
    String ubicacion;
    String materialregistrado;
    String incidencias;
    String fechaInico;
    String fechaFinal;
    String horaInicio;
    String horaFin;

    public ModeloInventariosHistoricoDetalle(String usuario, String cantidad, String longitud, String ubicacion, String materialregistrado, String incidencias, String fechaInicio, String fechaFin, String horaInicio, String horaFin) {
        this.usuario = usuario;
        this.cantidad = cantidad;
        this.longitud = longitud;
        this.ubicacion = ubicacion;
        this.materialregistrado = materialregistrado;
        this.incidencias = incidencias;
        this.fechaInico = fechaInicio;
        this.fechaFinal = fechaFin;
        this.horaInicio= horaInicio;
        this.horaFin = horaFin;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFechaInico() {
        return fechaInico;
    }

    public void setFechaInico(String fechaInico) {
        this.fechaInico = fechaInico;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getMaterialregistrado() {
        return materialregistrado;
    }

    public void setMaterialregistrado(String materialregistrado) {
        this.materialregistrado = materialregistrado;
    }

    public String getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(String incidencias) {
        this.incidencias = incidencias;
    }
}
