package com.example.operacionesivra.Minuta.Modelos;

public class ModeloTema {
    //Variables del módelo Tema
    int temaID;
    String tema;
    String tiempoEstimado;
    //Identificador para saber si esta seleccionado
    int reunionID;
    int isSelected;

    //Constructor cargado
    public ModeloTema(String tema, String tiempoEstimado, int isSelected){
        this.tema = tema;
        this.tiempoEstimado = tiempoEstimado;
        this.isSelected = isSelected;
    }

    //Constructor cargado completo
    public ModeloTema(int temaID, String tema, String tiempoEstimado, int reunionID, int isSelected){
        this.temaID = temaID;
        this.tema = tema;
        this.tiempoEstimado = tiempoEstimado;
        this.reunionID = reunionID;
        this.isSelected = isSelected;
    }

    //Getter and setter
    public int getTemaID() {
        return temaID;
    }

    public void setTemaID(int temaID) {
        this.temaID = temaID;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(String tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public int getReunionID() {
        return reunionID;
    }

    public void setReunionID(int reunionID) {
        this.reunionID = reunionID;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }
}
