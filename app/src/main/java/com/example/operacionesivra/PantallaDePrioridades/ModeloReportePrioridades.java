package com.example.operacionesivra.PantallaDePrioridades;

public class ModeloReportePrioridades {
    String cliente, pedido, entrega, referencia, fecha, horadeentrega, entregado, tiempotranscurrido;

    public ModeloReportePrioridades(String cliente, String pedido, String entrega, String referencia, String fecha, String horadeentrega, String entregado, String tiempotranscurrido) {
        this.cliente = cliente;
        this.pedido = pedido;
        this.entrega = entrega;
        this.referencia = referencia;
        this.fecha = fecha;
        this.horadeentrega = horadeentrega;
        this.entregado = entregado;
        this.tiempotranscurrido = tiempotranscurrido;
    }

    public String getCliente() {
        return cliente;
    }

    public String getTiempotranscurrido() {
        return tiempotranscurrido;
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

    public String getHoradeentrega() {
        return horadeentrega;
    }

    public String getEntregado() {
        return entregado;
    }

    public String getFecha() {
        return fecha;
    }

}
