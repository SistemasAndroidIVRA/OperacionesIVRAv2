package com.example.operacionesivra.Vistas.Picking.SuirtirPicking;

public class ModeloContenidodelPedido {

    String nombredelmaterial, estado, codproducto, codPedido, codimpreso;
    int imagen;
    float cantidad, cantidadsolicitada;

    public ModeloContenidodelPedido(float cantidad, String nombredelmaterial, float cantidadsolicitada, String estado, String codproducto, int imagen, String codPedido, String codimpreso) {
        this.cantidad = cantidad;
        this.nombredelmaterial = nombredelmaterial;
        this.cantidadsolicitada = cantidadsolicitada;
        this.estado = estado;
        this.codproducto = codproducto;
        this.imagen = imagen;
        this.codPedido = codPedido;
        this.codimpreso = codimpreso;
    }

    public String getCodproducto() {
        return codproducto;
    }

    public String getEstado() {
        return estado;
    }

    public float getCantidad() {
        return cantidad;
    }

    public String getNombredelmaterial() {
        return nombredelmaterial;
    }

    public float getCantidadsolicitada() {
        return cantidadsolicitada;
    }

    public int getImagen() {
        return imagen;
    }

    public String getCodPedido() {
        return codPedido;
    }

    public String getCodimpreso() {
        return codimpreso;
    }

}
