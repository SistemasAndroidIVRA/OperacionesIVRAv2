package com.example.operacionesivra.Inventarios.Models;

public class ModeloInventariosProceso {
    String folio, fecha, almacen, material, sistema, fisico, estadoMercancia, bloqueado, incidencias, productoID;

    public ModeloInventariosProceso(){

    }

    public ModeloInventariosProceso(String folio, String fecha, String almacen, String material, String sistema, String fisico, String estadoMercancia, String bloqueado, String incidencias, String productoID) {
        this.folio = folio;
        this.fecha = fecha;
        this.almacen = almacen;
        this.material = material;
        this.sistema = sistema;
        this.fisico = fisico;
        this.estadoMercancia = estadoMercancia;
        this.bloqueado = bloqueado;
        this.incidencias = incidencias;
        this.productoID = productoID;
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

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getFisico() {
        return fisico;
    }

    public void setFisico(String fisico) {
        this.fisico = fisico;
    }

    public String getEstadoMercancia() {
        return estadoMercancia;
    }

    public void setEstadoMercancia(String estadoMercancia) {
        this.estadoMercancia = estadoMercancia;
    }

    public String getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(String bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(String incidencias) {
        this.incidencias = incidencias;
    }

    public String getProductoID() {
        return productoID;
    }

    public void setProductoID(String productoID) {
        this.productoID = productoID;
    }
}
