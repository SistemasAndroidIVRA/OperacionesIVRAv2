package com.example.operacionesivra.Minuta.Modelos;

public class ModeloAsistente {
    int personaID;
    String nombre;
    String correo;
    //Sirve para identificar el tipo
    String empleadoID;
    int asistencia;
    int isSelected;

    //Constructor cargado
    public ModeloAsistente(int personaID, String nombre, String correo, String empleadoID, int isSelected){
        this.personaID = personaID;
        this.nombre = nombre;
        this.correo = correo;
        this.empleadoID = empleadoID;
        this.isSelected = isSelected;
    }

    //Constructor cargado Completo
    public ModeloAsistente(int personaID, String nombre, String correo, String empleadoID, int asistencia, int isSelected){
        this.personaID = personaID;
        this.nombre = nombre;
        this.correo = correo;
        this.empleadoID = empleadoID;
        this.asistencia = asistencia;
        this.isSelected = isSelected;
    }

    //Getter and Setter
    public int getPersonaID() {
        return personaID;
    }

    public void setPersonaID(int personaID) {
        this.personaID = personaID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEmpleadoID() {
        return empleadoID;
    }

    public void setEmpleadoID(String empleadoID) {
        this.empleadoID = empleadoID;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }


    public int getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(int asistencia) {
        this.asistencia = asistencia;
    }


    @Override
    public String toString(){
        return this.nombre;
    }
}
