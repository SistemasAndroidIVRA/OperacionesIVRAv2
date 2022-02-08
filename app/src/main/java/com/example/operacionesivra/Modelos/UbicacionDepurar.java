package com.example.operacionesivra.Modelos;

public class UbicacionDepurar {
    String UbicacionID;
    String Nombre;
    int isSelect;

    public UbicacionDepurar(String ubicacionID, String nombre, int isSelect) {
        UbicacionID = ubicacionID;
        Nombre = nombre;
        this.isSelect = isSelect;
    }

    public String getUbicacionID() {
        return UbicacionID;
    }

    public void setUbicacionID(String ubicacionID) {
        UbicacionID = ubicacionID;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(int isSelect) {
        this.isSelect = isSelect;
    }
}
