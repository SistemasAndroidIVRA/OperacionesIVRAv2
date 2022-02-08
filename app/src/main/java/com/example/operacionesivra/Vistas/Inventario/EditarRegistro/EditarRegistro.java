package com.example.operacionesivra.Vistas.Inventario.EditarRegistro;

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

import com.example.operacionesivra.Vistas.Inventario.NuevoRegistro.ModeloUbicacion;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class EditarRegistro extends DialogFragment {
    EditarRegistroInterface customDialogInterface;
    TextInputEditText tag, contenido, ubicacion;
    TextView totalregistrado;
    EditText txtEditIncidencia;
    public Context mContext;
    String incidencia, tagobtenida, contenidoobtenido, ubicaionobtenida, totalobtenido, posicionobtenida, seleccionado, ubicacionId;
    //Botones
    Button aceptarEditar;
    //Spinner
    Spinner spinnerUbiEdit;
    //Arrays
    private static List<ModeloUbicacion> ubicaciones = new ArrayList();
    private static ArrayList<String> listArray = new ArrayList();
    //Listas nombres e ides
    private static ArrayList<String> nombres = new ArrayList<>();
    private static ArrayList<String> ides = new ArrayList<>();

    public int vaciar = 0;

    public EditarRegistro(Context context, String tagobtenida, String contenidoobtenido, String ubicacionobtenida, String total, String posicionobtenida, String incidencia) {
        this.mContext = context;
        this.tagobtenida = tagobtenida;
        this.contenidoobtenido = contenidoobtenido;
        this.ubicaionobtenida = ubicacionobtenida;
        this.totalobtenido = total;
        this.posicionobtenida = posicionobtenida;
        this.incidencia = incidencia;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            customDialogInterface = (EditarRegistroInterface) context;
        } catch (ClassCastException e) {
            System.out.println("Error");
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
        //contenido.setEnabled(false);
        ubicacion = view.findViewById(R.id.ubicacionEditar);
        totalregistrado = view.findViewById(R.id.resultadoEditar);
        txtEditIncidencia = view.findViewById(R.id.txtEditIncidencia);
        //Botones
        aceptarEditar = view.findViewById(R.id.aceptarEditar);

        //Spinner
        spinnerUbiEdit = view.findViewById(R.id.spinnerUbiEdit);

        listArray = getUbicaciones();

        //Separar arraylist
        separarArrayList(listArray);
        //Llenar spinner con ides
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(), R.layout.ajuste_inventario_spinnervisual, nombres);
        spinnerUbiEdit.setAdapter(arrayAdapter);

        //Spinner OnItemSelectedListener
        spinnerUbiEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String [] datos = spinnerUbiEdit.getSelectedItem().toString().split("/");
                if(nombres.get(position).toString().equals("")){
                    //No hace nada
                    vaciar = 1;
                }else{
                    vaciar = 0;
                    ubicacion.setText(""+nombres.get(position).toString());
                    seleccionado = nombres.get(position);
                    ubicacionId = ides.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //No hace nada
            }
        });

        view.findViewById(R.id.atrasEditar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tag.setText(tagobtenida);
        contenido.setText(contenidoobtenido);
        ubicacion.setText(ubicaionobtenida);
        txtEditIncidencia.setText(incidencia);

        String filtro = ubicacion.getText().toString();
        ArrayList<String> listArrayFiltro = getUbicacionesFiltro(filtro);
        //Separar arrays de nuevo
        separarArrayList(listArrayFiltro);
        //Llenar spinner
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(view.getContext(), R.layout.ajuste_inventario_spinnervisual, nombres);
        spinnerUbiEdit.setAdapter(arrayAdapter2);

        //ubicasion setOnKeyListener
        ubicacion.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(ubicacion.getText().toString().equals("")){
                    spinnerUbiEdit.setAdapter(null);
                    aceptarEditar.setEnabled(false);
                    vaciar = 1;
                }else if(tag.getText().toString().equals("") && ubicacion.getText().toString().equals("")){
                    spinnerUbiEdit.setAdapter(null);
                    aceptarEditar.setEnabled(false);
                    vaciar = 1;
                }else if(tag.getText().toString().equals("") && !ubicacion.getText().toString().equals("")){
                    aceptarEditar.setEnabled(false);
                }else if(!tag.getText().toString().equals("") && ubicacion.getText().toString().equals("")){
                    spinnerUbiEdit.setAdapter(null);
                    aceptarEditar.setEnabled(false);
                    spinnerUbiEdit.setEnabled(false);
                    vaciar = 1;
                }else{
                    vaciar = 1;
                    spinnerUbiEdit.setAdapter(null);
                    String filtro = ubicacion.getText().toString();
                    ArrayList<String> listArrayFiltro = getUbicacionesFiltro(filtro);
                    //Separar arrays
                    separarArrayList(listArrayFiltro);
                    //Llenar spinner
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(), R.layout.ajuste_inventario_spinnervisual, nombres);
                    spinnerUbiEdit.setAdapter(arrayAdapter);
                    //Validar si hay registros obtenidos
                    if(spinnerUbiEdit.getCount() <= 0){
                        new MaterialAlertDialogBuilder(view.getContext())
                                .setTitle("¡Error!")
                                .setCancelable(false)
                                .setIcon(R.drawable.snakerojo)
                                .setMessage("La ubicación que desea guardar no existe, por favor teclee una nueva y seleccione la correcta.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //
                                    }
                                })
                                .show();
                        //Desactivamos el botón de aceptar para no permitir guardar
                        aceptarEditar.setEnabled(false);
                    }else{
                        if(ubicacion.getText().toString().equals("")){
                            //Desactivamos el botón
                            aceptarEditar.setEnabled(false);
                        }else{
                            //Activamos el botón
                            aceptarEditar.setEnabled(true);
                        }
                    }
                }
                return false;
            }
        });

        //Total item seleccionado
        float totalSeleccionado = Float.parseFloat(tag.getText().toString())*Float.parseFloat(contenido.getText().toString());
        totalregistrado.setText(totalSeleccionado+"");

        tag.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String texto = tag.getText().toString();
                if(texto.equals("")){
                    aceptarEditar.setEnabled(false);
                    totalregistrado.setText("0");
                }else{
                    totalregistrado.setText(""+(Float.parseFloat(tag.getText().toString()) * Float.parseFloat(contenido.getText().toString())));
                    if(ubicacion.getText().toString().equals("")){
                        aceptarEditar.setEnabled(false);
                    }else{
                        aceptarEditar.setEnabled(true);
                    }
                }
                return false;
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
                if (!tag.equals("") && !contenido.equals("")) {
                    try {
                        float resultadofloat = Integer.parseInt(tag.getText().toString()) * Float.parseFloat(contenido.getText().toString());
                        totalregistrado.setText(resultadofloat + "");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }else if(tag.equals("") || contenido.equals("")){
                    totalregistrado.setText("Complete los campos");
                }
            }

        });

        aceptarEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (tag.getText().toString().equals("") || contenido.getText().toString().equals("")) {
                        if (tag.getText().toString().equals("")) {
                            tag.setError("Completa el campo para continuar");
                        }
                    } else {
                        String tags = tag.getText().toString().trim();
                        String ubicaionstring = seleccionado;
                        String contenidos = contenido.getText().toString().trim();
                        String total = totalregistrado.getText().toString();
                        customDialogInterface.editar(tags, contenidos, seleccionado, total, posicionobtenida, ubicacionId, txtEditIncidencia.getText().toString());
                        dismiss();
                        ubicacion.setText("");
                        contenido.setText("");
                        txtEditIncidencia.setText("");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    public void separarArrayList(ArrayList<String> lista){
        if(vaciar == 0){
            nombres = new ArrayList<>();
            ides = new ArrayList<>();
            for(int i = 0; i<lista.size(); i++){
                String [] datos = lista.get(i).toString().trim().split("/");
                nombres.add(datos[0]+"");
                ides.add(datos[1]+"");
            }
        }else if(vaciar == 1){
            nombres = new ArrayList<>();
            ides = new ArrayList<>();
            nombres.add("");
            ides.add("");
            for(int i = 0; i<lista.size(); i++){
                String [] datos = lista.get(i).toString().trim().split("/");
                nombres.add(datos[0]+"");
                ides.add(datos[1]+"");
            }
        }
    }

    //Obtener las ubicaciones de la BD
    public ArrayList<String> getUbicaciones(){
        ArrayList<String> lista = new ArrayList<>();
        try {
            Conexion conexion = new Conexion(getContext());
            Statement stmt = conexion.conexiondbImplementacion().createStatement();
            String query = "SELECT UbicacionID, Nombre FROM Ubicacion WHERE status = 1";
            ResultSet r = stmt.executeQuery(query);
            while(r.next()){
                ubicaciones.add(new ModeloUbicacion(r.getString("UbicacionID"), r.getString("Nombre")));
            }
            for(int i=0; i<ubicaciones.size();i++){
                lista.add(ubicaciones.get(i).getNombre().toUpperCase()+" /"+ubicaciones.get(i).getUbicacionId());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lista;
    }

    //Método para filtrar las ubicaciones
    public ArrayList<String> getUbicacionesFiltro(String filtro){
        ArrayList<String> list = new ArrayList<>();
        for(int i=0; i<listArray.size(); i++){
            if(listArray.get(i).toLowerCase().toString().trim().split("/")[0].contains(filtro.toLowerCase())){
                list.add(listArray.get(i));
            }
        }
        return list;
    }

}
