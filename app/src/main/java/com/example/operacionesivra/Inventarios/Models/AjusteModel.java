package com.example.operacionesivra.Inventarios.Models;

public class AjusteModel {
    String compraID;
    String referencia;
    String empresaID;
    String documentoID;
    String fecha;
    String hora;
    String fechaHora;
    int numero;
    String almacenID;
    String almacen;
    float cantidad;
    String usuarioID;
    String elaboradoPor;
    String usuarioIDAutorizado;
    String fechaAceptacion;
    char status;
    String cajaID;
    String enviarNombre; //Obs 1
    String enviarDireccion; //Obs 2
    String hospitalID; //Cancelado por
    String obsGenerales;
    String entregar_a; //Identificador: Movil
    String obs1;
    String obs2;

    //Constructor vacío
    public AjusteModel() {
    }

    //Constructor sobrecargado para darlo de alta
    public AjusteModel(String compraID, String referencia, String empresaID, String documentoID, String fecha, String hora, String fechaHora, int numero, String almacenID, float cantidad, String usuarioID, String usuarioIDAutorizado, char status, String cajaID, String enviarNombre, String enviarDireccion, String hospitalID, String obsGenerales, String entregar_a) {
        this.compraID = compraID;
        this.referencia = referencia;
        this.empresaID = empresaID;
        this.documentoID = documentoID;
        this.fecha = fecha;
        this.hora = hora;
        this.fechaHora = fechaHora;
        this.numero = numero;
        this.almacenID = almacenID;
        this.cantidad = cantidad;
        this.usuarioID = usuarioID;
        this.usuarioIDAutorizado = usuarioIDAutorizado;
        this.status = status;
        this.cajaID = cajaID;
        this.enviarNombre = enviarNombre;
        this.enviarDireccion = enviarDireccion;
        this.hospitalID = hospitalID;
        this.obsGenerales = obsGenerales;
        this.entregar_a = entregar_a;
    }

    //Métodos getter and setter
    public String getCompraID() {
        return compraID;
    }

    public void setCompraID(String compraID) {
        this.compraID = compraID;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getEmpresaID() {
        return empresaID;
    }

    public void setEmpresaID(String empresaID) {
        this.empresaID = empresaID;
    }

    public String getDocumentoID() {
        return documentoID;
    }

    public void setDocumentoID(String documentoID) {
        this.documentoID = documentoID;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getAlmacenID() {
        return almacenID;
    }

    public void setAlmacenID(String almacenID) {
        this.almacenID = almacenID;
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public String getElaboradoPor() {
        return elaboradoPor;
    }

    public void setElaboradoPor(String elaboradoPor) {
        this.elaboradoPor = elaboradoPor;
    }

    public String getUsuarioIDAutorizado() {
        return usuarioIDAutorizado;
    }

    public void setUsuarioIDAutorizado(String usuarioIDAutorizado) {
        this.usuarioIDAutorizado = usuarioIDAutorizado;
    }

    public String getFechaAceptacion() {
        return fechaAceptacion;
    }

    public void setFechaAceptacion(String fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public String getCajaID() {
        return cajaID;
    }

    public void setCajaID(String cajaID) {
        this.cajaID = cajaID;
    }

    public String getEnviarNombre() {
        return enviarNombre;
    }

    public void setEnviarNombre(String enviarNombre) {
        this.enviarNombre = enviarNombre;
    }

    public String getEnviarDireccion() {
        return enviarDireccion;
    }

    public void setEnviarDireccion(String enviarDireccion) {
        this.enviarDireccion = enviarDireccion;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public String getObsGenerales() {
        return obsGenerales;
    }

    public void setObsGenerales(String obsGenerales) {
        this.obsGenerales = obsGenerales;
    }

    public String getEntregar_a() {
        return entregar_a;
    }

    public void setEntregar_a(String entregar_a) {
        this.entregar_a = entregar_a;
    }

    public String getObs1() {
        return obs1;
    }

    public void setObs1(String obs1) {
        this.obs1 = obs1;
    }

    public String getObs2() {
        return obs2;
    }

    public void setObs2(String obs2) {
        this.obs2 = obs2;
    }
}
