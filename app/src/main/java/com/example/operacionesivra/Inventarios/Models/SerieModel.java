package com.example.operacionesivra.Inventarios.Models;

public class SerieModel {
    String cajaID;
    String nombre;
    int Status;


    //Constructor vacío
    public SerieModel() {
    }

    //Constructor con sobrecarga
    public SerieModel(String cajaID, String nombre, int status) {
        this.cajaID = cajaID;
        this.nombre = nombre;
        Status = status;
    }

    //Metodos getter and setters
    public String getCajaID() {
        return cajaID;
    }

    public void setCajaID(String cajaID) {
        this.cajaID = cajaID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    @Override
    public String toString(){
        return getNombre();
    }
}
