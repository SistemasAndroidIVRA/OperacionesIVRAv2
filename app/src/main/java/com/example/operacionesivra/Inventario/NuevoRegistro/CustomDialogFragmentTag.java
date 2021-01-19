package com.example.operacionesivra.Inventario.NuevoRegistro;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class CustomDialogFragmentTag extends DialogFragment {
    CustomDialogInterfaceTag customDialogInterface;
    TextInputEditText cantidaddemetros,ubicacion, cantidadderollos;
    String codproducto;
    TextView resultadomultiplicacion;
    int okmetros,okcantidad;

    //Scanner

    public Context mContext;

    public CustomDialogFragmentTag(Context context) {
        this.mContext = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.inventario_dialog_fragment_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cantidadderollos = view.findViewById(R.id.cantidadderollos);
        cantidaddemetros = view.findViewById(R.id.cantidaddemetros);
        ubicacion = view.findViewById(R.id.ubicaciontag);
        resultadomultiplicacion = view.findViewById(R.id.resultadoDFT);

        cantidadderollos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                okcantidad=1;
                if(okmetros==1 && okcantidad==1){
                    if(!cantidadderollos.equals("") && !cantidaddemetros.equals("")) {
                        try {
                            float resultadofloat = Integer.parseInt(cantidadderollos.getText().toString()) * Float.parseFloat(cantidaddemetros.getText().toString());
                            resultadomultiplicacion.setText(resultadofloat + "");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                    else if(!cantidadderollos.equals("") || !cantidaddemetros.equals("")){
                        resultadomultiplicacion.setText("Complete los campos");
                    }
                }
                if(!cantidadderollos.equals("") || !cantidaddemetros.equals("")){
                    resultadomultiplicacion.setText("Complete los campos");
                }
            }
        });

        cantidaddemetros.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                okmetros=1;
                if(okmetros==1 && okcantidad==1){
                    if(!cantidadderollos.equals("") && !cantidaddemetros.equals("")) {
                        try {
                            float resultadofloat = Integer.parseInt(cantidadderollos.getText().toString()) * Float.parseFloat(cantidaddemetros.getText().toString());
                            resultadomultiplicacion.setText(resultadofloat + "");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                    else if(!cantidadderollos.equals("") || !cantidaddemetros.equals("")){
                        resultadomultiplicacion.setText("Complete los campos");
                    }
                }
            }
        });


        view.findViewById(R.id.aceptar_CDT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ubicacion.getText().toString().equals("") || cantidaddemetros.getText().toString().equals("") || cantidadderollos.getText().toString().equals("")) {
                        if(ubicacion.getText().toString().equals("")) {
                            ubicacion.setError("Completa el campo para continuar");
                        }
                        if(cantidaddemetros.getText().toString().equals("")) {
                            cantidaddemetros.setError("Completa el campo para continuar");
                        }
                        if(cantidadderollos.getText().toString().equals("")) {
                            cantidaddemetros.setError("Completa el campo para continuar");
                        }
                        new MaterialAlertDialogBuilder(getContext(), R.style.MaterialAlertDialog_MaterialComponents)
                                .setTitle("Error")
                                .setMessage("Completa los datos para guardar una tag")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();

                    } else {
                        String materialstring = cantidaddemetros.getText().toString().trim();
                        String ubicaionstring = ubicacion.getText().toString().trim();
                        String resultadostring = resultadomultiplicacion.getText().toString();
                        System.out.println(resultadostring);
                        customDialogInterface.datostag(cantidadderollos.getText().toString(), cantidaddemetros.getText().toString(), ubicaionstring,codproducto,resultadostring);
                        dismiss();
                        cantidaddemetros.setText("");
                        ubicacion.setText("");
                        cantidadderollos.setText("");
                        resultadomultiplicacion.setText("");
                    }
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        });

        view.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {
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
            customDialogInterface = (CustomDialogInterfaceTag) context;
        } catch (ClassCastException e) {
            System.out.println("Sorry");
        }
    }
}
