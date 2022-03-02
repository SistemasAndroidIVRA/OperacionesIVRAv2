package com.example.operacionesivra.Vistas.Chequeo.ListadePedidos;

public class ModeloListaChequeo {
    String pedido, cliente, referencia, ruta, fecha, estado, serie;

    public ModeloListaChequeo(String pedido, String cliente, String referencia, String ruta, String fecha, String estado, String serie) {
        this.pedido = pedido;
        this.cliente = cliente;
        this.referencia = referencia;
        this.ruta = ruta;
        this.fecha = fecha;
        this.estado = estado;
        this.serie = serie;
    }

    public String getPedido() {
        return pedido;
    }

    public String getCliente() {
        return cliente;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getRuta() {
        return ruta;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public String getSerie() {
        return serie;
    }
}
