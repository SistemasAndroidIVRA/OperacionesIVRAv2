package com.example.operacionesivra.Inventarios.Models;

public class Ubicacion {
    String ubicacionId;
    String nombre;

    public Ubicacion(){

    }

    public Ubicacion(String ubicacionId, String nombre){
        this.ubicacionId = ubicacionId;
        this.nombre = nombre;
    }

    public String getUbicacionId(){
        return ubicacionId;
    }

    public String toString(){
        return nombre;
    }

    public void setUbicacionId(String ubicacionId){
        this.ubicacionId = ubicacionId;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

}
