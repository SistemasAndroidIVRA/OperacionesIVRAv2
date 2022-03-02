package com.example.operacionesivra.Vistas.PantallaRecepcion;

public class ModeloReporteMovimientos {
    String pedido, cliente, estado, referencia, horadelmovimiento1, horadelmovimiento2, horadelmovimiento3, horadelmovimiento4;

    public ModeloReporteMovimientos(String pedido, String cliente, String estado, String referencia, String horadelmovimiento1, String horadelmovimiento2, String horadelmovimiento3, String horadelmovimiento4) {
        this.pedido = pedido;
        this.cliente = cliente;
        this.estado = estado;
        this.referencia = referencia;
        this.horadelmovimiento1 = horadelmovimiento1;
        this.horadelmovimiento2 = horadelmovimiento2;
        this.horadelmovimiento3 = horadelmovimiento3;
        this.horadelmovimiento4 = horadelmovimiento4;
    }

    public String getPedido() {
        return pedido;
    }

    public String getCliente() {
        return cliente;
    }

    public String getEstado() {
        return estado;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getHoradelmovimiento1() {
        return horadelmovimiento1;
    }

    public String getHoradelmovimiento2() {
        return horadelmovimiento2;
    }

    public String getHoradelmovimiento3() {
        return horadelmovimiento3;
    }

    public String getHoradelmovimiento4() {
        return horadelmovimiento4;
    }
}
