package com.example.operacionesivra.Modelos;

public class UnidadMedidaModel
{
    String unidadID;
    String Nombre;
    int status;

    //Constructor vacío
    public UnidadMedidaModel() {
    }

    //Constructor sobrecargado
    public UnidadMedidaModel(String unidadID, String nombre, int status) {
        this.unidadID = unidadID;
        Nombre = nombre;
        this.status = status;
    }

    //Métodos getter and setters
    public String getUnidadID() {
        return unidadID;
    }

    public void setUnidadID(String unidadID) {
        this.unidadID = unidadID;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
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
