package com.example.operacionesivra.Inventario;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.operacionesivra.Inventario.ConteosPausa.Pausa;
import com.example.operacionesivra.Inventario.InventariosCerrados.Inventarioscerrados;
import com.example.operacionesivra.MainActivity.MainActivity;
import com.example.operacionesivra.R;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.textfield.TextInputEditText;

import devliving.online.mvbarcodereader.MVBarcodeScanner;

import static android.app.Activity.RESULT_OK;

public class CustomDialogFragment extends DialogFragment {
    CustomDialogInterface customDialogInterface;
    TextInputEditText material;
    Spinner spinner;
    String almacen;
    ImageButton scanner;

    //Scanner
    public MVBarcodeScanner.ScanningMode modo_Escaneo;
    public String codigo = null;
    public int CODE_SCAN = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.inventario_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        material = view.findViewById(R.id.codigomaterial);
        spinner = view.findViewById(R.id.spinner);
        scanner = view.findViewById(R.id.escogermaterialscanner);
        String[] opciones = {"01 GAMMA", "02 GAMMA", "03 GAMMA", "04 ALMACEN ALPHA", "05 GAMMA MUESTRAS", "06 GAMMA SURTIDO", "07 KAPPA", "08 GAMMA ARETINA", "09 GAMMA BLOQUEADO", "10 GAMMA OP", "17 CORTES", "14 DELTA", "18 DELTA OP"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), R.layout.ajuste_inventario_spinnervisual, opciones);
        spinner.setAdapter(adapter);

        view.findViewById(R.id.aceptar_CD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String materialstring = material.getText().toString().trim();
                String seleccion = spinner.getSelectedItem().toString();
                almacen = seleccion;
                String almacenstring = almacen.trim();
                if (!materialstring.isEmpty() && !almacenstring.isEmpty()) {
                    customDialogInterface.datos(materialstring, almacenstring);
                    dismiss();

                }
            }
        });

        view.findViewById(R.id.atras_DF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.pausai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Pausa.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.cerrados_DF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Inventarioscerrados.class);
                startActivity(intent);
            }
        });

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanner();
            }
        });


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            customDialogInterface = (CustomDialogInterface) context;
        } catch (ClassCastException e) {
            System.out.println("Sorry");
        }
    }

    public void escanner() {
        modo_Escaneo = MVBarcodeScanner.ScanningMode.SINGLE_AUTO;

        new MVBarcodeScanner.Builder().setScanningMode(modo_Escaneo).setFormats(Barcode.ALL_FORMATS)
                .build()
                .launchScanner(this, CODE_SCAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_SCAN) {
            if (resultCode == RESULT_OK && data != null
                    && data.getExtras() != null) {
                if (data.getExtras().containsKey(MVBarcodeScanner.BarcodeObject)) {
                    Barcode mBarcode = data.getParcelableExtra(MVBarcodeScanner.BarcodeObject);
                    codigo = mBarcode.rawValue;
                    material.setText(codigo);
                }
            }
        }
    }
}