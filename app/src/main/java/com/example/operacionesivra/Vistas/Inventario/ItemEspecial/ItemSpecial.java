package com.example.operacionesivra.Vistas.Inventario.ItemEspecial;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.operacionesivra.R;
import com.google.android.material.textfield.TextInputEditText;

public class ItemSpecial extends DialogFragment {
    TextInputEditText tag;
    ItemSpecialInterface itemSpecialInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.inventario_itemspecial, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tag = view.findViewById(R.id.tagespecial);

        view.findViewById(R.id.aceptarespecial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tags = tag.getText().toString();
                if (!tags.isEmpty()) {
                    itemSpecialInterface.datosincidenciareportada(tags);
                    tag.setText("");
                    dismiss();
                    Toast.makeText(getContext(), "Incidencia Agregada/Actualizada correctamente", Toast.LENGTH_SHORT).show();

                }
            }
        });

        view.findViewById(R.id.atrasespecial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            itemSpecialInterface = (ItemSpecialInterface) context;
        } catch (ClassCastException e) {
            System.out.println("Sorry");
        }
    }
}
