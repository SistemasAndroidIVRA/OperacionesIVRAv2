package com.example.operacionesivra.Inventarios.Models;

public class AjusteDetailsModel {
    String comprasDetailsID;//Automatico
    String compraID;//Automatico
    int renglon;//Automatico
    String codigo;//Codigo material
    String productoID;//IdProducto
    float cantidad;//Cantidad a ajustar
    float contenido;//Cantidad a ajustar
    String nombre;//Nombre material
    UnidadMedidaModel unidad;//UnidadID
    float precio;//Precio
    String fCaptura;//Fechacaptura automatica

    //Constructor vacío
    public AjusteDetailsModel() {
    }

    //Contructos con sobrecarga
    public AjusteDetailsModel(String comprasDetailsID, String compraID, int renglon, String codigo, String productoID, float cantidad, String nombre, UnidadMedidaModel unidad, float precio, String fCaptura) {
        this.comprasDetailsID = comprasDetailsID;
        this.compraID = compraID;
        this.renglon = renglon;
        this.codigo = codigo;
        this.productoID = productoID;
        this.cantidad = cantidad;
        this.nombre = nombre;
        this.unidad = unidad;
        this.precio = precio;
        this.fCaptura = fCaptura;
    }

    //Métodos getter and setter
    public String getComprasDetailsID() {
        return comprasDetailsID;
    }

    public void setComprasDetailsID(String comprasDetailsID) {
        this.comprasDetailsID = comprasDetailsID;
    }

    public String getCompraID() {
        return compraID;
    }

    public void setCompraID(String compraID) {
        this.compraID = compraID;
    }

    public int getRenglon() {
        return renglon;
    }

    public void setRenglon(int renglon) {
        this.renglon = renglon;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getProductoID() {
        return productoID;
    }

    public void setProductoID(String productoID) {
        this.productoID = productoID;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public UnidadMedidaModel getUnidad() {
        return unidad;
    }

    public void setUnidad(UnidadMedidaModel unidad) {
        this.unidad = unidad;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getfCaptura() {
        return fCaptura;
    }

    public void setfCaptura(String fCaptura) {
        this.fCaptura = fCaptura;
    }

    public float getContenido() {
        return contenido;
    }

    public void setContenido(float contenido) {
        this.contenido = contenido;
    }
}
