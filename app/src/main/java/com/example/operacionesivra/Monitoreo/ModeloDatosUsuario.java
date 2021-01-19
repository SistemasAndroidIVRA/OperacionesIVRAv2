package com.example.operacionesivra.Monitoreo;

public class ModeloDatosUsuario {
    String nombre,idusuario;
    float latitud,longitud;

    public ModeloDatosUsuario(String nombre,float latitud,float longitud,String idusuario){
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.idusuario = idusuario;
    }

    public String getNombre(){
        return nombre;
    }

    public float getLatitud(){
        return latitud;
    }

    public float getLongitud(){
        return longitud;
    }

    public String getIdusuario(){
        return idusuario;
    }
}
