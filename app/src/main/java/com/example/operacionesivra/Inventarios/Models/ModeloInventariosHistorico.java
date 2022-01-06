package com.example.operacionesivra.Inventarios.Models;

public class ModeloInventariosHistorico {
    String folio, fecha, usuario, almacen, material, entradas, horaInicio, horaFin, estadoMercancia, incidencias;
    String fisico, sistema;

    public ModeloInventariosHistorico() {
    }

    public ModeloInventariosHistorico(String folio, String fecha, String usuario, String almacen, String material, String entradas, String horaInicio, String horaFin, String estadoMercancia, String incidencias, String fisico, String sistema) {
        this.folio = folio;
        this.fecha = fecha;
        this.usuario = usuario;
        this.almacen = almacen;
        this.material = material;
        this.entradas = entradas;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estadoMercancia = estadoMercancia;
        this.incidencias = incidencias;
        this.fisico = fisico;
        this.sistema = sistema;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getEntradas() {
        return entradas;
    }

    public void setEntradas(String entradas) {
        this.entradas = entradas;
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

    public String getEstadoMercancia() {
        return estadoMercancia;
    }

    public void setEstadoMercancia(String estadoMercancia) {
        this.estadoMercancia = estadoMercancia;
    }

    public String getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(String incidencias) {
        this.incidencias = incidencias;
    }

    public String getFisico() {
        return fisico;
    }

    public void setFisico(String fisico) {
        this.fisico = fisico;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }
}
