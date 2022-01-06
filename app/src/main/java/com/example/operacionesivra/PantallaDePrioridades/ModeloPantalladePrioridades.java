package com.example.operacionesivra.PantallaDePrioridades;

public class ModeloPantalladePrioridades {
    String cliente, pedido, entrega, referencia, fecha, estadotexto;
    int dia;

    public ModeloPantalladePrioridades(String cliente, String pedido, String entrega, String referencia, int dia, String fecha, String estadotexto) {
        this.cliente = cliente;
        this.pedido = pedido;
        this.entrega = entrega;
        this.referencia = referencia;
        this.dia = dia;
        this.fecha = fecha;
        this.estadotexto = estadotexto;
    }

    public String getCliente() {
        return cliente;
    }

    public String getPedido() {
        return pedido;
    }

    public String getEntrega() {
        return entrega;
    }

    public String getReferencia() {
        return referencia;
    }

    public int getDia() {
        return dia;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstadotexto() {
        return estadotexto;
    }
}
