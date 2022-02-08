package com.example.operacionesivra.Vistas.PantallaRecepcion;

public class ModeloVideos {
    String videoID;
    String nombre, url;
    int isSelected;

    public ModeloVideos(String nombre, String url, int isSelected) {
        this.nombre = nombre;
        this.url = url;
        this.isSelected = isSelected;
    }

    //Constructor completo para el edit
    public ModeloVideos(String videoID, String nombre, String url, int isSelected) {
        this.videoID = videoID;
        this.nombre = nombre;
        this.url = url;
        this.isSelected = isSelected;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }
}
