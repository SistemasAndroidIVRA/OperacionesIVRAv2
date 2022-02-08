package com.example.operacionesivra.Vistas.Picking.SuirtirPicking.EditarEntrada;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.operacionesivra.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class EditarRegistroPicking extends DialogFragment {
    EditarRegistroPickingInterface editarregistro;
    String material;
    float contenido;
    TextView materialT;
    TextInputEditText cantidad;

    public EditarRegistroPicking(String material, float contenido) {
        this.material = material;
        this.contenido = contenido;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            editarregistro = (EditarRegistroPickingInterface) context;
        } catch (ClassCastException e) {
            System.out.println("Sorry");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.picking_editar_registro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        materialT = view.findViewById(R.id.editarmaterialpicking);
        cantidad = view.findViewById(R.id.editarcontenidopicking);


        materialT.setText(material);
        cantidad.setText(contenido + "");

        view.findViewById(R.id.editarAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Objects.requireNonNull(cantidad.getText()).toString().equals("")) {
                    editarregistro.editar(material, contenido);
                    dismiss();
                } else {
                    cantidad.setError("Complete los campos para continuar");
                }
            }
        });

        view.findViewById(R.id.editarCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


}
