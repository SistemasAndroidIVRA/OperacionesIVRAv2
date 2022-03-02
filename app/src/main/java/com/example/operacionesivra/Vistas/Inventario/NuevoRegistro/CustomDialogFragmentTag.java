package com.example.operacionesivra.Vistas.Inventario.NuevoRegistro;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.example.operacionesivra.Modelos.VariablesGlobales.GlobalesInventario;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomDialogFragmentTag extends DialogFragment {
    CustomDialogInterfaceTag customDialogInterface;
    TextInputEditText cantidaddemetros, ubicacion, cantidadderollos;
    String codproducto, seleccionado, ubicacionId;
    TextView resultadomultiplicacion;
    int okmetros, okcantidad;
    Spinner spinnerUbicaciones;
    int estadoUbicacion = 1;
    Button btnAceptar;
    EditText txtIncidencia;


    private static List<ModeloUbicacion> ubicaciones = new ArrayList();
    private static ArrayList<String> listArray = new ArrayList<>();
    private static ArrayList<String> nombres = new ArrayList<>();
    private static ArrayList<String> ides = new ArrayList<>();

    public Context mContext;
    public Float lonjitud;

    public CustomDialogFragmentTag(Context context, Float lonjitudMat) {
        this.mContext = context;
        this.lonjitud = lonjitudMat;
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
        //Guardamos el valor global y desactivamos el txt
        cantidaddemetros.setText(""+ GlobalesInventario.globalContMaterial);
        //cantidaddemetros.setEnabled(false);
        ubicacion = view.findViewById(R.id.ubicaciontag);
        resultadomultiplicacion = view.findViewById(R.id.resultadoDFT);
        btnAceptar = view.findViewById(R.id.aceptar_CDT);
        txtIncidencia = view.findViewById(R.id.txtIncidencia);

        //Spinner
        spinnerUbicaciones = view.findViewById(R.id.spinnerUbicaciones);
        spinnerUbicaciones.setEnabled(false);

        //Traer las ubicasiones
        listArray = getUbicasiones();
        //Separar arrays
        separarArrays(listArray);

        //Llenar spinner con ides
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.ajuste_inventario_spinnervisual, nombres);
        spinnerUbicaciones.setAdapter(arrayAdapter);
        //Spinner on click
        spinnerUbicaciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(nombres.get(position).toString().equals("")){
                    //Nada
                }else{
                    ubicacion.setText(""+nombres.get(position));
                    seleccionado = nombres.get(position)+"";
                    ubicacionId = ides.get(position)+"";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //No hace nada
            }
        });

        if(ubicacion.getText().toString().trim().equals("")){
            btnAceptar.setEnabled(false);
        }

        //Ubicación on click listener
        ubicacion.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(ubicacion.getText().toString().equals("")){
                    spinnerUbicaciones.setAdapter(null);
                    spinnerUbicaciones.setEnabled(false);
                    btnAceptar.setEnabled(false);
                }else if(!cantidadderollos.getText().toString().equals("") && ubicacion.getText().toString().equals("")) {
                    btnAceptar.setEnabled(false);
                }else if(cantidadderollos.getText().toString().equals("") && !ubicacion.getText().toString().equals("")) {
                    btnAceptar.setEnabled(false);
                    spinnerUbicaciones.setEnabled(true);
                    spinnerUbicaciones.setAdapter(null);
                    String filtro = ubicacion.getText().toString();
                    ArrayList<String> listArrayFiltro = getUbicasionesFiltro(filtro);
                    //Llenar spinner
                    separarArrays(listArrayFiltro);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.ajuste_inventario_spinnervisual, nombres);
                    spinnerUbicaciones.setAdapter(arrayAdapter);
                    //Validar si hay registros obtenidos
                    if(spinnerUbicaciones.getCount() <= 0){
                        estadoUbicacion = 0;
                        new MaterialAlertDialogBuilder(getContext())
                                .setTitle("¡Error!")
                                .setIcon(R.drawable.snakerojo)
                                .setCancelable(false)
                                .setMessage("La ubicación que desea guardar no existe, por favor teclee una nueva y seleccione la correcta.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //
                                    }
                                })
                                .show();
                    }
                }else{
                    spinnerUbicaciones.setEnabled(true);
                    spinnerUbicaciones.setAdapter(null);
                    String filtro = ubicacion.getText().toString();
                    ArrayList<String> listArrayFiltro = getUbicasionesFiltro(filtro);
                    //Llenar spinner
                    separarArrays(listArrayFiltro);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.ajuste_inventario_spinnervisual, nombres);
                    spinnerUbicaciones.setAdapter(arrayAdapter);
                    //Validar si hay registros obtenidos
                    if(spinnerUbicaciones.getCount() <= 0){
                        estadoUbicacion = 0;
                        new MaterialAlertDialogBuilder(getContext())
                                .setTitle("¡Error!")
                                .setIcon(R.drawable.snakerojo)
                                .setCancelable(false)
                                .setMessage("La ubicación que desea guardar no existe, por favor teclee una nueva y seleccione la correcta.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //
                                    }
                                })
                                .show();
                        btnAceptar.setEnabled(false);
                    }
                    else{
                        estadoUbicacion = 1;
                        if(ubicacion.getText().toString().trim().equals("")){
                            btnAceptar.setEnabled(false);
                        }else{
                            btnAceptar.setEnabled(true);
                        }
                    }
                }
                return false;
            }
        });

        //ubicacion.setText(ubicaciones.get(0).getNombre()+"");
        cantidadderollos.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String texto = cantidadderollos.getText().toString();
                if(texto.equals("") || cantidaddemetros.getText().toString().equals("")){
                    btnAceptar.setEnabled(false);
                    resultadomultiplicacion.setText("0");
                }else{
                    resultadomultiplicacion.setText(""+(Float.parseFloat(cantidadderollos.getText().toString()) * Float.parseFloat(cantidaddemetros.getText().toString())));
                    if(ubicacion.getText().toString().equals("")){
                        btnAceptar.setEnabled(false);
                    }else{
                        btnAceptar.setEnabled(true);
                    }
                }
                return false;
            }
        });

        //Listener del metro
        cantidaddemetros.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(cantidaddemetros.getText().equals("")){
                    cantidaddemetros.setText("0");
                }
                if (!cantidadderollos.equals("") && !cantidaddemetros.equals("")) {
                    try {
                        float resultadofloat = Integer.parseInt(cantidadderollos.getText().toString()) * Float.parseFloat(cantidaddemetros.getText().toString());
                        resultadomultiplicacion.setText(resultadofloat + "");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } else if (cantidadderollos.equals("") || cantidaddemetros.equals("")) {
                    resultadomultiplicacion.setText("Complete los campos");
                }
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (cantidadderollos.getText().toString().equals("")) {
                            cantidadderollos.setError("Completa el campo para continuar");
                    } else {
                        if(cantidaddemetros.getText().toString().equals("") || cantidaddemetros.getText().toString().equals("0")){
                            ubicacion.setText("");
                            cantidadderollos.setText("0");
                            resultadomultiplicacion.setText("0");
                            txtIncidencia.setText("");
                            dismiss();
                        }else{
                            String ubicaionstring = "";
                            String materialstring = cantidaddemetros.getText().toString().trim();
                            if(spinnerUbicaciones.getSelectedItem() == null){
                                ubicaionstring = ubicacion.getText().toString().trim().toUpperCase();
                            }else{
                                ubicaionstring = seleccionado.trim().toUpperCase();
                            }
                            String resultadostring = resultadomultiplicacion.getText().toString();
                            System.out.println(resultadostring);
                            customDialogInterface.datostag(cantidadderollos.getText().toString(), cantidaddemetros.getText().toString(), ubicaionstring, codproducto, resultadostring, ubicacionId, txtIncidencia.getText().toString());
                            dismiss();
                            ubicacion.setText("");
                            cantidadderollos.setText("");
                            resultadomultiplicacion.setText("0");
                            txtIncidencia.setText("");
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

        view.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ubicacion.setText("");
                cantidadderollos.setText("");
                resultadomultiplicacion.setText("0");
                txtIncidencia.setText("");
            }
        });
    }

    public void separarArrays(ArrayList<String> list){
            nombres = new ArrayList<>();
            ides = new ArrayList<>();
            nombres.add("");
            ides.add("");
            for(int i=0; i<list.size();i++){
                String [] datos = list.get(i).toString().trim().split("/");
                nombres.add(datos[0]);
                ides.add(datos[1]);
            }
    }


    //Obtener las ubicaciones
    public ArrayList<String> getUbicasiones(){
        ArrayList<String> lista = new ArrayList<>();
        try {
            Conexion con = new Conexion(getContext());
            Statement statement = con.conexiondbImplementacion().createStatement();
            String query = "SELECT UbicacionID, Nombre FROM Ubicacion WHERE status = 1 ORDER BY Nombre DESC";
            ResultSet r = statement.executeQuery(query);
            while(r.next()){
                ubicaciones.add(new ModeloUbicacion(r.getString("UbicacionID"), r.getString("Nombre")));
            }
            for(int i=0; i<ubicaciones.size(); i++){
                lista.add(ubicaciones.get(i).getNombre().toUpperCase()+" /"+ubicaciones.get(i).getUbicacionId());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lista;
    }

    //Filtro para buscar ubicaciones
    public ArrayList<String> getUbicasionesFiltro(String filtro){
        ArrayList<String> lista = new ArrayList<>();
        for(int i=0; i<listArray.size(); i++){
            if(listArray.get(i).toLowerCase().toString().trim().split("/")[0].contains(filtro.toLowerCase())){
                lista.add(listArray.get(i));
            }
        }
        return lista;
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
