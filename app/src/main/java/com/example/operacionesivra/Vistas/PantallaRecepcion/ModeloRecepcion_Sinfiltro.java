package com.example.operacionesivra.Vistas.PantallaRecepcion;

public class ModeloRecepcion_Sinfiltro {
    String pedido, estado, cliente, referencia;

    public ModeloRecepcion_Sinfiltro(String pedido, String estado, String cliente, String referencia) {
        this.pedido = pedido;
        this.estado = estado;
        this.cliente = cliente;
        this.referencia = referencia;
    }

    public String getPedido() {
        return pedido;
    }

    public String getEstado() {
        return estado;
    }

    public String getCliente() {
        return cliente;
    }

    public String getReferencia() {
        return referencia;
    }
}
