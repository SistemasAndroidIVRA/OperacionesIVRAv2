package com.example.operacionesivra.Picking.SuirtirPicking;

public class ModeloContenidodelPedido {

    String nombredelmaterial, estado, cantidad, cantidadsolicitada, codpedido, coditem;
    int imagen;

    public ModeloContenidodelPedido(String cantidad, String nombredelmaterial, String cantidadsolicitada, String estado, String codpedido, int imagen, String coditem) {
        this.cantidad = cantidad;
        this.nombredelmaterial = nombredelmaterial;
        this.cantidadsolicitada = cantidadsolicitada;
        this.estado = estado;
        this.codpedido = codpedido;
        this.imagen = imagen;
        this.coditem = coditem;
    }

    public String getCodpedido() {
        return codpedido;
    }

    public String getEstado() {
        return estado;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getNombredelmaterial() {
        return nombredelmaterial;
    }

    public String getCantidadsolicitada() {
        return cantidadsolicitada;
    }

    public int getImagen() {
        return imagen;
    }

    public String getCoditem() {
        return coditem;
    }

    public void setNombredelmaterial(String nombredelmaterial) {
        this.nombredelmaterial = nombredelmaterial;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public void setCantidadsolicitada(String cantidadsolicitada) {
        this.cantidadsolicitada = cantidadsolicitada;
    }

    public void setCodpedido(String codpedido) {
        this.codpedido = codpedido;
    }
}
