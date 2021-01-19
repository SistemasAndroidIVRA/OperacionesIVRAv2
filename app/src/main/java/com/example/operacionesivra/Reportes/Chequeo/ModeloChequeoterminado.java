package com.example.operacionesivra.Reportes.Chequeo;

public class ModeloChequeoterminado {
    String pedido,serie, cliente,referencia,fecha,horainicio,horafin;

    public ModeloChequeoterminado(String pedido, String serie, String cliente, String referencia, String fecha, String horainicio, String horafin) {
        this.pedido = pedido;
        this.serie = serie;
        this.cliente = cliente;
        this.referencia = referencia;
        this.fecha = fecha;
        this.horainicio = horainicio;
        this.horafin = horafin;
    }

    public String getPedido() {
        return pedido;
    }

    public String getSerie() {
        return serie;
    }

    public String getCliente() {
        return cliente;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHorainicio() {
        return horainicio;
    }

    public String getHorafin() {
        return horafin;
    }
}
