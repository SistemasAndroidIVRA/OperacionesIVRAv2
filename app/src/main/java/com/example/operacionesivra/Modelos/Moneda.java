package com.example.operacionesivra.Modelos;

public class Moneda {
    String monedaID;
    String nombre;
    String abreviatura;
    int status;

    //Constructor vacío
    public Moneda(){

    }

    //Constructor lleno
    public Moneda(String monedaID, String nombre, String abreviatura, int status) {
        this.monedaID = monedaID;
        this.nombre = nombre;
        this.abreviatura = abreviatura;
        this.status = status;
    }

    //Métodos getter and setter
    public String getMonedaID() {
        return monedaID;
    }

    public void setMonedaID(String monedaID) {
        this.monedaID = monedaID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString(){
        return this.nombre+" - "+this.abreviatura;
    }
}
