package com.example.operacionesivra.Vistas.Reportes.Inventario.InventarioActual;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.operacionesivra.R;
import com.google.android.material.textfield.TextInputEditText;

public class InventarioActualitem extends DialogFragment {
    InventarioActualInterface customDialogInterface;
    TextInputEditText material;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.reportes_inventario_actual_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        material = view.findViewById(R.id.material_IAD);

        view.findViewById(R.id.aceptar_AID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String materialstring = material.getText().toString().trim();
                if (!materialstring.isEmpty() ) {
                    customDialogInterface.datos(materialstring);
                    dismiss();

                }
            }
        });

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            customDialogInterface = (InventarioActualInterface) context;
        } catch (ClassCastException e) {
            System.out.println("Sorry");
        }
    }
}
