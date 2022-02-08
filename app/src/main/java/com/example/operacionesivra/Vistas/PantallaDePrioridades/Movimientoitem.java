package com.example.operacionesivra.Vistas.PantallaDePrioridades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.operacionesivra.R;

public class Movimientoitem extends DialogFragment {
    TextView pedido, cliente, hora, movimiento, referencia;
    String pedidos, clientes, horas, movimientos, referencias;
    ImageView imagenpedido;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.prioridades_pantalla_prioridades_movimiento_item, container, false);
    }

    public Movimientoitem(String pedidos, String clientes, String horas, String movimientos, String referencias) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.horas = horas;
        this.movimientos = movimientos;
        this.referencias = referencias;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pedido = view.findViewById(R.id.numeropedido);
        cliente = view.findViewById(R.id.clientepedido);
        hora = view.findViewById(R.id.horapedido);
        movimiento = view.findViewById(R.id.tipodemovimientopedido);
        referencia = view.findViewById(R.id.referenciapedido);
        imagenpedido = view.findViewById(R.id.imagenpedido);
        llenar();
    }

    public void llenar() {
        pedido.setText(pedidos);
        cliente.setText(clientes);
        hora.setText(horas);
        movimiento.setText(movimientos);
        referencia.setText(referencias);
        switch (movimientos) {
            case "Nuevo pedido":
                imagenpedido.setImageResource(R.drawable.nuevopedidoxml);
                break;
            case "Pedido surtido":
                imagenpedido.setImageResource(R.drawable.pedidoentregado);
                break;
        }
    }
}
