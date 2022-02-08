package com.example.operacionesivra.Modelos;

public class Venta {
    String comprasID;
    String empresaID;
    int documentoID;
    int etapa;
    String fecha;
    String hora;
    String numero;
    String clienteID;
    String cliente;
    String venedorID;
    String vendedor;
    String entregar_a;
    float total;
    String usuarioID;
    String enviarNombre;
    String referencia;
    String observaciones;

    //Constructor vacío
    public Venta() {
    }

    //Métodos getter and setter
    public String getComprasID() {
        return comprasID;
    }

    public void setComprasID(String comprasID) {
        this.comprasID = comprasID;
    }

    public String getEmpresaID() {
        return empresaID;
    }

    public void setEmpresaID(String empresaID) {
        this.empresaID = empresaID;
    }

    public int getDocumentoID() {
        return documentoID;
    }

    public void setDocumentoID(int documentoID) {
        this.documentoID = documentoID;
    }

    public int getEtapa() {
        return etapa;
    }

    public void setEtapa(int etapa) {
        this.etapa = etapa;
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getClienteID() {
        return clienteID;
    }

    public void setClienteID(String clienteID) {
        this.clienteID = clienteID;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getVenedorID() {
        return venedorID;
    }

    public void setVenedorID(String venedorID) {
        this.venedorID = venedorID;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getEntregar_a() {
        return entregar_a;
    }

    public void setEntregar_a(String entregar_a) {
        this.entregar_a = entregar_a;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public String getEnviarNombre() {
        return enviarNombre;
    }

    public void setEnviarNombre(String enviarNombre) {
        this.enviarNombre = enviarNombre;
    }

    public void setReferencia(String referencia){
        this.referencia = referencia;
    }

    public String getReferencia(){
        return referencia;
    }

    public void setObservaciones(String observaciones){
        this.observaciones = observaciones;
    }

    public String getObservaciones(){
        return this.observaciones;
    }
}
