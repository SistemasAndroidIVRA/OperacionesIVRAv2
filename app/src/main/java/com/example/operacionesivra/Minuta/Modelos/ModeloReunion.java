package com.example.operacionesivra.Minuta.Modelos;

public class ModeloReunion {
    int reunionID;
    String empleado, lugar, fecha, horaInicio, horaFin;

    public ModeloReunion(int reunionID, String empleado, String lugar, String fecha, String horaInicio, String horaFin){
        this.reunionID = reunionID;
        this.empleado = empleado;
        this.lugar = lugar;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public void setReunionID(int reunionID){
        this.reunionID = reunionID;
    }

    public int getReunionID(){
        return reunionID;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }
}
