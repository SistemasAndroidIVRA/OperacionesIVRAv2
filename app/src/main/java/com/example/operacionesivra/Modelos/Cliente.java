package com.example.operacionesivra.Modelos;

public class Cliente {
    String clienteID;
    String nombre;

    //Constructor vacío
    public Cliente() {
    }

    //Métodos getter and setter
    public String getClienteID() {
        return clienteID;
    }

    public void setClienteID(String clienteID) {
        this.clienteID = clienteID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString(){
        return this.nombre;
    }
}
