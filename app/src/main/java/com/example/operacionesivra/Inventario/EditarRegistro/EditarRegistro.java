package com.example.operacionesivra.Inventario.EditarRegistro;

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


public class EditarRegistro extends DialogFragment {
    EditarRegistroInterface customDialogInterface;
    TextInputEditText tag, contenido,ubicacion;
    TextView totalregistrado;
    public Context mContext;
    String tagobtenida, contenidoobtenido, ubicaionobtenida, totalobtenido, posicionobtenida;
    int okmetros,okcantidad;

    public EditarRegistro(Context context,String tagobtenida, String contenidoobtenido, String ubicacionobtenida,String total, String posicionobtenida) {
        this.mContext = context;
        this.tagobtenida=tagobtenida;
        this.contenidoobtenido = contenidoobtenido;
        this.ubicaionobtenida = ubicacionobtenida;
        this.totalobtenido = total;
        this.posicionobtenida = posicionobtenida;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            customDialogInterface = (EditarRegistroInterface) context;
        } catch (ClassCastException e) {
            System.out.println("Sorry");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.inventario_editar_registro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tag = view.findViewById(R.id.tagEditar);
        contenido = view.findViewById(R.id.contenidoEditar);
        ubicacion = view.findViewById(R.id.ubicacionEditar);
        totalregistrado = view.findViewById(R.id.resultadoEditar);
        view.findViewById(R.id.atrasEditar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tag.setText(tagobtenida);
        contenido.setText(contenidoobtenido);
        ubicacion.setText(ubicaionobtenida);

        tag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float resultadofloat = Integer.parseInt(tag.getText().toString()) * Float.parseFloat(contenido.getText().toString());
                    totalregistrado.setText(resultadofloat + "");
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        });

        contenido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                        try {
                            float resultadofloat = Integer.parseInt(tag.getText().toString()) * Float.parseFloat(contenido.getText().toString());
                            totalregistrado.setText(resultadofloat + "");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                }

        });

        view.findViewById(R.id.aceptarEditar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ubicacion.getText().toString().equals("") || tag.getText().toString().equals("") || contenido.getText().toString().equals("")) {
                        if(ubicacion.getText().toString().equals("")) {
                            ubicacion.setError("Completa el campo para continuar");
                        }
                        if(tag.getText().toString().equals("")) {
                            tag.setError("Completa el campo para continuar");
                        }
                        if(contenido.getText().toString().equals("")) {
                            contenido.setError("Completa el campo para continuar");
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
                        String tags = tag.getText().toString().trim();
                        String ubicaionstring = ubicacion.getText().toString().trim();
                        String contenidos = contenido.getText().toString().trim();
                        String total = totalregistrado.getText().toString();
                        customDialogInterface.editar(tags,contenidos,ubicaionstring,total,posicionobtenida);
                        dismiss();
                        ubicacion.setText("");
                        contenido.setText("");
                    }
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        });
    }
}
