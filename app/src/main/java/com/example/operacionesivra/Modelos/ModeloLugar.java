package com.example.operacionesivra.Modelos;

public class ModeloLugar {
    //Variables del módelo lugar
    int idLugar, estado;
    String lugar;

    //Constructor cargado
    public ModeloLugar(int idLugar, String lugar, int estado){
        this.idLugar = idLugar;
        this.lugar = lugar;
        this.estado = estado;
    }

    //Métodos get
    public int getIdLugar(){
        return idLugar;
    }

    public String getLugar(){
        return lugar;
    }

    public int getEstado(){
        return estado;
    }

    @Override
    public String toString(){
        return lugar;
    }
}
