package com.example.operacionesivra.Modelos;

public class ModeloAcuerdo {
    //Variables del módelo Acuerdo
    int acuerdoID;
    String acuerdo;
    String empleado;
    int personaID;
    String fechaCompromiso;
    //Identificador para saber si está seleccionado
    int reunionID;
    int isSelected;

    //Constructor para agregar in acuerdo
    public ModeloAcuerdo(String acuerdo, String empleado, int personaID, String fechaCompromiso, int isSelected) {
        this.acuerdo = acuerdo;
        this.empleado = empleado;
        this.personaID = personaID;
        this.fechaCompromiso = fechaCompromiso;
        this.isSelected = isSelected;
    }

    //Constructor acuerdo cargado para editar
    public ModeloAcuerdo(int acuerdoID, String acuerdo, String empleado, int personaID, String fechaCompromiso, int reunionID, int isSelected) {
        this.acuerdoID = acuerdoID;
        this.acuerdo = acuerdo;
        this.empleado = empleado;
        this.personaID = personaID;
        this.fechaCompromiso = fechaCompromiso;
        this.reunionID = reunionID;
        this.isSelected = isSelected;
    }

    public int getAcuerdoID() {
        return acuerdoID;
    }

    public void setAcuerdoID(int acuerdoID) {
        this.acuerdoID = acuerdoID;
    }

    public String getAcuerdo() {
        return acuerdo;
    }

    public void setAcuerdo(String acuerdo) {
        this.acuerdo = acuerdo;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public int getPersonaID() {
        return personaID;
    }

    public void setPersonaID(int personaID) {
        this.personaID = personaID;
    }

    public String getFechaCompromiso() {
        return fechaCompromiso;
    }

    public void setFechaCompromiso(String fechaCompromiso) {
        this.fechaCompromiso = fechaCompromiso;
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
