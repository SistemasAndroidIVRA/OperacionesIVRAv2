package com.example.operacionesivra.Vistas.PantallaRecepcion;

public class ModeloRecepcion {
    String pedido, estado, cliente;

    public ModeloRecepcion(String pedido, String estado, String cliente) {
        this.pedido = pedido;
        this.estado = estado;
        this.cliente = cliente;
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
}
