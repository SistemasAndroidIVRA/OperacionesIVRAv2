package com.example.operacionesivra.Picking.ListapedidosPicking.BuscarNota;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Reportes.Inventario.InventarioActual.InventarioActualInterface;
import com.google.android.material.textfield.TextInputEditText;

public class BuscarPedido extends DialogFragment {
    BuscarPedidoInterface buscarPedidoInterface;
    TextInputEditText pedido;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.piking_buscar_codigo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pedido = view.findViewById(R.id.pedido_pbc);

        view.findViewById(R.id.aceptar_pbc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Spedido = pedido.getText().toString().trim().substring(1);
                String Sserie = pedido.getText().toString().trim().substring(0, 1);
                if (!Spedido.isEmpty() && !Sserie.isEmpty()) {
                    buscarPedidoInterface.obtenerDatos(Spedido, Sserie);
                    dismiss();

                }
            }
        });

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            buscarPedidoInterface = (BuscarPedidoInterface) context;
        } catch (ClassCastException e) {
            System.out.println("Sorry");
        }
    }
}
