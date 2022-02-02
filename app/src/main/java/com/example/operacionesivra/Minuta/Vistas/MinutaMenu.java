package com.example.operacionesivra.Minuta.Vistas;

import static com.example.operacionesivra.ComprobaciondeDispositivo.TabletOTelefono.esTablet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.operacionesivra.Minuta.Modelos.ModeloAsistente;
import com.example.operacionesivra.Minuta.Modelos.ModeloLugar;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MinutaMenu extends AppCompatActivity {
    Button btnMinutaNueva, btnMinutaConsultar, btnMinutaRegresar, btnSilenciar;
    String usuario, usuarioID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minuta_a_menu);
        //Si es tablet maneja todas las dimenciones, sino solo la vertical
        if(!esTablet(this)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        usuario = getIntent().getStringExtra("usuario");
        usuarioID = getIntent().getStringExtra("idusuario");
        //Rererenciar botones
        btnMinutaNueva = (Button) findViewById(R.id.btnMinutaNueva);
        btnMinutaConsultar = (Button) findViewById(R.id.btnMinutaConsultar);
        btnMinutaRegresar = (Button) findViewById(R.id.btnMinutaRegresar);
        btnSilenciar = (Button) findViewById(R.id.btnSilenciar);

        SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        getSonido(preferences);
        //Accioens botónes
        // Generar nueva minuta
        btnMinutaNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadinglauncher();
            }
        });
        //Consultar minutas
        btnMinutaConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MinutaConsultarMinutas.class);
                startActivity(intent);
            }
        });
        //Botón de regresar
        btnMinutaRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnSilenciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                silenciar(preferences);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    //Pantalla de cargando
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }


    //Método que abre el dialogo para iniciar una reunión
    public void openDialogReunion(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Preparar el AlertDialog
                AlertDialog.Builder alert = new AlertDialog.Builder(MinutaMenu.this);
                LayoutInflater inflater = getLayoutInflater();
                //Traer la vista que se va a abrir
                View view = inflater.inflate(R.layout.minuta_a_principal, null);
                alert.setView(view);
                //Abrir la vista
                final AlertDialog dialog = alert.create();
                dialog.show();
                dialog.setCancelable(false);

                //Autocomplete
                AutoCompleteTextView autoCompleteTextViewLugar;
                autoCompleteTextViewLugar = (AutoCompleteTextView) view.findViewById(R.id.aCtxtMinutaRegistroLugar);

                //Componentes de la otra vista
                Button btnCancelar = view.findViewById(R.id.btnCancelar);
                Button btnAceptar = view.findViewById(R.id.btnAceptar);
                Spinner spinnerConvoco = view.findViewById(R.id.spinnerConvoco);

                //Personales
                ArrayList<ModeloAsistente> personales = getPersonales();
                ArrayAdapter<ModeloAsistente> adaptadorPersonales = new ArrayAdapter<ModeloAsistente>(MinutaMenu.this, R.layout.minuta_a_spinner, personales);
                spinnerConvoco.setAdapter(adaptadorPersonales);

                //Añadir los lugares para el autoComplete
                ArrayList<ModeloLugar> lugares = getLugares();
                ArrayAdapter<ModeloLugar> adaptadorLugar = new ArrayAdapter<>(MinutaMenu.this,R.layout.minuta_a_spinner,lugares);
                autoCompleteTextViewLugar.setAdapter(adaptadorLugar);

                //Para obtener el usuario (Metodo a implementar para spinner de convoco)
                //SharedPreferences sharedPref = getSharedPreferences("credenciales",Context.MODE_PRIVATE);
                //String name = sharedPref.getString("iduser","null");
                //Toast.makeText(MinutaMenu.this, name+"", Toast.LENGTH_LONG).show();

                //Acción del btnCancelar
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //Accción del btnAceptar
                btnAceptar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(autoCompleteTextViewLugar.getText().toString().equals("")){
                            Toast.makeText(view.getContext(), "Completa todos los campos",Toast.LENGTH_LONG).show();
                        }else{
                            //Traer lugar
                            ModeloLugar modeloLugarSelected = (ModeloLugar) findLugar(autoCompleteTextViewLugar.getText().toString(),lugares);
                            String lugar = modeloLugarSelected.getLugar();
                            int lugarID = modeloLugarSelected.getIdLugar();
                            //Traer convocó
                            ModeloAsistente convoco = (ModeloAsistente) spinnerConvoco.getSelectedItem();
                            //Mandar lugar a travez de interfaz
                            Intent intent = new Intent(view.getContext(), MinutaReunionRegistro.class);
                            intent.putExtra("Lugar", lugar);
                            intent.putExtra("LugarID", lugarID);
                            intent.putExtra("Convoco", convoco.getNombre());
                            intent.putExtra("ConvocoID", convoco.getEmpleadoID());
                            intent.putExtra("usuario", usuario);
                            //Iniciamos la activity
                            view.getContext().startActivity(intent);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    //Método para traer los asistentes
    public ArrayList<ModeloAsistente> getPersonales(){
        ArrayList<ModeloAsistente> array = new ArrayList<>();
        try {
            Conexion con = new Conexion(MinutaMenu.this);
            PreparedStatement var = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_PersonalesInternos_SELECT");
            ResultSet r = var.executeQuery();
            while(r.next()){
                array.add(new ModeloAsistente(r.getInt("personaID"), r.getString("nombreCompleto"), r.getString("correo"), r.getString("empleadoID"), 0));
            }
            return array;
        }catch (Exception e){
            Toast.makeText(MinutaMenu.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //Método para llenar un Spinner
    public void fullSpinner(ArrayList<ModeloLugar> lugaresArray, View view, Spinner spinner){
        //Llenar spinner y setear adapter
        ArrayAdapter<ModeloLugar> arrayAdapter = new ArrayAdapter<ModeloLugar>(view.getContext(), R.layout.minuta_a_spinner, lugaresArray);
        spinner.setAdapter(arrayAdapter);
    }

    //Método para traer lugares
    public ArrayList<ModeloLugar> getLugares(){
        //Declarar arraylist de tipo modelo Lugar para almacenar los resulatdos de la BD
        ArrayList<ModeloLugar> modeloLugarArray = new ArrayList<>();
        //Abrir conexión
        Conexion con = new Conexion(this);
        //Cachar las exepciones
        try {

            //Se ejecuta el procedimiento almacenado en sql
            PreparedStatement var = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_Lugares_SELECT");
            //Ejecutamos el enunciado cargado con la sentencia
            ResultSet r = var.executeQuery();
            //Recorremos los resultados
            while(r.next()){
                //Creamos modelos de tipo lugar y se los agregamos al arraylist
                modeloLugarArray.add(new ModeloLugar(r.getInt("lugarID"), r.getString("lugar"), r.getInt("estado")));
            }
            //Atrapamos el error
        } catch (SQLException throwables) {
            //Mostramos el error
            throwables.printStackTrace();
        }
        //Retornamos el arreglo
        return modeloLugarArray;

    }
    //Para encontrar el lugar
    public ModeloLugar findLugar(String lugar, ArrayList<ModeloLugar> lugares){
        for (int i = 0; i < lugares.size(); i++) {
            if(lugares.get(i).getLugar().equals(lugar)){
                return lugares.get(i);
            }
        }
        Conexion con = new Conexion(this);
        //Cachar las exepciones
        try {
            //Se prepara el enunciado del procedimiento almacenado en sql
            PreparedStatement var = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_Lugar_INSERT '"+lugar+"'"+",1");
            //Se ejecuta el enunciado
            ResultSet r = var.executeQuery();
        } catch (SQLException throwables) {
            //Mostramos el error
            throwables.printStackTrace();
        }
        lugares = getLugares();
        return lugares.get(lugares.size()-1);
    }
    public void getSonido(SharedPreferences preferences){
        int sound = preferences.getInt("sonido",-1);
        if(1 == sound){
            btnSilenciar.setText("Con sonido");
        }else if (sound == -1){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("sound", 1);
            btnSilenciar.setText("Con sonido");
            editor.apply();
        }else{
            btnSilenciar.setText("Sin sonido");
        }
    }
    public void silenciar(SharedPreferences preferences){
        if(1 == preferences.getInt("sonido",-1)){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("sonido", 0);
            btnSilenciar.setText("Sin sonido");
            editor.apply();
        }else{
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("sonido", 1);
            btnSilenciar.setText("Con sonido");
            editor.apply();
        }
    }

}
