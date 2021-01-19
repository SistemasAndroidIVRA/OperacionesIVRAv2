package com.example.operacionesivra.Picking.ListapedidosPicking;

public class ModeloListaPedido {
    String fecha, cliente, referencia, numerodepedido, cantidad, hora, serie;

    public ModeloListaPedido(String referencia, String cliente, String fecha, String numerodepedido, String hora, String cantidad, String serie) {
        this.fecha = fecha;
        this.cliente = cliente;
        this.referencia = referencia;
        this.numerodepedido = numerodepedido;
        this.cantidad = cantidad;
        this.hora = hora;
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

    public String getCantidad() {
        return cantidad;
    }

    public String getHora() {
        return hora;
    }

    public String getSerie() {
        return serie;
    }


}

