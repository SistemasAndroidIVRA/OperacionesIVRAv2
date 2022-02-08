package com.example.operacionesivra.Modelos;

public class VentaDetalle {
    String Compras_DetailsID;
    String ComprasID;
    int renglon;
    String Codigo;
    String ProductoID;
    String producto;
    float precio;
    int cantidad;
    String unidadID;
    String unidad;
    String FCaptura;

    //Constructor vacío
    public VentaDetalle(){
    }

    //Métodos getter and setter
    public String getCompras_DetailsID() {
        return Compras_DetailsID;
    }

    public void setCompras_DetailsID(String compras_DetailsID) {
        Compras_DetailsID = compras_DetailsID;
    }

    public String getComprasID() {
        return ComprasID;
    }

    public void setComprasID(String comprasID) {
        ComprasID = comprasID;
    }

    public int getRenglon() {
        return renglon;
    }

    public void setRenglon(int renglon) {
        this.renglon = renglon;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getProductoID() {
        return ProductoID;
    }

    public void setProductoID(String productoID) {
        ProductoID = productoID;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadID() {
        return unidadID;
    }

    public void setUnidadID(String unidadID) {
        this.unidadID = unidadID;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getFCaptura() {
        return FCaptura;
    }

    public void setFCaptura(String FCaptura) {
        this.FCaptura = FCaptura;
    }
}
