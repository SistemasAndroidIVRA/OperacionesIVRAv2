package com.example.operacionesivra.Modelos;

public class Producto {
    String productoID;
    String nombre;
    String codigo;
    int status;
    int bloqueado;

    //Constructor vacío
    public Producto() {
    }

    //Constructor sobrecargado
    public Producto(String productoID, String nombre, String codigo, int status) {
        this.productoID = productoID;
        this.nombre = nombre;
        this.codigo = codigo;
        this.status = status;
    }

    //Métodos getter and setter
    public String getProductoID() {
        return productoID;
    }

    public void setProductoID(String productoID) {
        this.productoID = productoID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(int bloqueado) {
        this.bloqueado = bloqueado;
    }

    @Override
    public String toString(){
        return getNombre()+" - "+getCodigo();
    }
}
