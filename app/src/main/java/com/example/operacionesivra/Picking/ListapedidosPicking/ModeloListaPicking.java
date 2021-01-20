package com.example.operacionesivra.Picking.ListapedidosPicking;

public class ModeloListaPicking {
    String fecha, cliente, referencia, numerodepedido, serie;

    public ModeloListaPicking(String referencia, String cliente, String fecha, String numerodepedido, String serie) {
        this.fecha = fecha;
        this.cliente = cliente;
        this.referencia = referencia;
        this.numerodepedido = numerodepedido;
        this.serie = serie;
    }

    public String getFecha() {
        return fecha;
    }

    public String getCliente() {
        return cliente;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getNumerodepedido() {
        return numerodepedido;
    }

    public String getSerie() {
        return serie;
    }


}

