package com.example.operacionesivra.Inventarios.Models;

public class AlmacenModel {
    String almacenId;
    String nombre;
    int status;

    //Constructor vacío
    public AlmacenModel() {
    }

    //Constructor con sobrecarga
    public AlmacenModel(String almacenId, String nombre, int status) {
        this.almacenId = almacenId;
        this.nombre = nombre;
        this.status = status;
    }

    //Métodos getter and setter
    public String getAlmacenId() {
        return almacenId;
    }

    public void setAlmacenId(String almacenId) {
        this.almacenId = almacenId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString(){
        return getNombre();
    }
}
