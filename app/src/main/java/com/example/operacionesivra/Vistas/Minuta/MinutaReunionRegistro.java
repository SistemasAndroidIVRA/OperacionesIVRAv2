package com.example.operacionesivra.Vistas.Minuta;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.BuildConfig;
import com.example.operacionesivra.Modelos.ModeloAcuerdo;
import com.example.operacionesivra.Modelos.ModeloAsistente;
import com.example.operacionesivra.Modelos.ModeloTema;
import com.example.operacionesivra.Vistas.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MinutaReunionRegistro extends AppCompatActivity {
    //public int
    public int loadingRegistrarMinuta = 0, sonido;
    //private int
    private  int hora, minutos;
    //Conexión
    Conexion con;
    //Contexto
    Context contexto = this;
    //File para guardar y abrir al instante
    File fileUri;
    //Variables principales
    String convoco, convocoID, lugar, fecha, horaInicio, horaFin, usuario, horaS, minutosS;
    int lugarID;
    //Recyclerviews
    RecyclerView recyclerViewRegistroAsistentes;
    RecyclerView recyclerViewRegistroTemas;
    RecyclerView recyclerViewRegistroAcuerdos;
    //Adapters
    AdapterAsistente adapterAsistente = new AdapterAsistente();
    AdapterTema adapterTema = new AdapterTema();
    AdapterAcuerdo adapterAcuerdo = new AdapterAcuerdo();
    //ArrayList
    ArrayList<ModeloAsistente> arrayAsistentesAutoCompletado = new ArrayList<>();
    ArrayList<ModeloAsistente> arrayAsistentes = new ArrayList<>();
    ArrayList<ModeloTema> arrayTemas = new ArrayList<>();
    ArrayList<ModeloAsistente> arrayResponsables = new ArrayList<>();
    ArrayList<ModeloAcuerdo> arrayAcuerdos = new ArrayList<>();
    //EditText
    EditText txtMinutaRegistroHora;
    //TextViews
    TextView lblMinutaRegistroFecha, lblMinutaRegistroLugar, lblMinutaRegistroConvoco;
    //Buttons
    Button btnMinutaRegistroPicker, btnMinutaRegistroAceptar, btnMinutaRegistroRegresar, btnMinutaRegistroCancelar;
    //Buttons asistentes
    Button btnMinutaRegistrarAsistente, btnMinutaEliminarAsistente;
    //Buttons temas
    Button btnMinutaRegistrarTema, btnMinutaEliminarTema;
    //Buttons acuerdos
    Button btnMinutaRegistrarAcuerdo, btnMinutaEliminarAcuerdo;
    //Button reloj
    ImageButton btnMinutaReloj;
    //ImageButton
    //Intent
    Intent intent;
    int encontrado = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minuta_a_reunion_registro);

        //Referenciar elementos
        //Spinners
        arrayAsistentesAutoCompletado = getAsistentesAutoCompletado();
        //Toast.makeText(contexto, "Cantidad: "+arrayAsistentesAutoCompletado.size(), Toast.LENGTH_SHORT).show();
        //Recyclerviews
        recyclerViewRegistroAsistentes = findViewById(R.id.recyclerViewRegistroAsistentes);
        recyclerViewRegistroAsistentes.setLayoutManager(new LinearLayoutManager(contexto));
        recyclerViewRegistroAsistentes.setAdapter(adapterAsistente);

        recyclerViewRegistroTemas = findViewById(R.id.recyclerViewRegistroTemas);
        recyclerViewRegistroTemas.setLayoutManager(new LinearLayoutManager(contexto));
        recyclerViewRegistroTemas.setAdapter(adapterTema);

        recyclerViewRegistroAcuerdos = findViewById(R.id.recyclerViewRegistroAcuerdos);
        recyclerViewRegistroAcuerdos.setLayoutManager(new LinearLayoutManager(contexto));
        recyclerViewRegistroAcuerdos.setAdapter(adapterAcuerdo);
        //EditText
        txtMinutaRegistroHora = findViewById(R.id.txtMinutaRegistroHora);
        //TextViews
        lblMinutaRegistroFecha = findViewById(R.id.lblMinutaRegistroFecha);
        lblMinutaRegistroLugar = findViewById(R.id.lblMinutaRegistroLugar);
        lblMinutaRegistroConvoco = findViewById(R.id.lblMinutaRegistroConvoco);
        //Buttons
        btnMinutaRegistroAceptar = findViewById(R.id.btnMinutaRegistroAceptar);
        btnMinutaRegistroRegresar = findViewById(R.id.btnMinutaRegistroRegresar);
        btnMinutaRegistroCancelar = findViewById(R.id.btnMinutaRegistroCancelar);
        //btnAsistentes
        btnMinutaRegistrarAsistente = findViewById(R.id.btnMinutaRegistrarAsistente);
        btnMinutaEliminarAsistente = findViewById(R.id.btnMinutaEliminarAsistente);
        //btnTemas
        btnMinutaRegistrarTema = findViewById(R.id.btnMinutaRegistrarTema);
        btnMinutaEliminarTema = findViewById(R.id.btnMinutaEliminarTema);
        //btnAcuerdos
        btnMinutaRegistrarAcuerdo = findViewById(R.id.btnMinutaRegistrarAcuerdo);
        btnMinutaEliminarAcuerdo = findViewById(R.id.btnMinutaEliminarAcuerdo);
        //ImageButton
        btnMinutaRegistroPicker = findViewById(R.id.btnMinutaRegistroPicker);
        btnMinutaReloj = findViewById(R.id.btnMinutaReloj);
        //getInfoPrincipal
        otorgarpermisos();
        startVariablesPrincipales();
        getInfoPrincipal();

        sonido = getSonido();
        //Acciones botonos
        btnMinutaRegistroAceptar.setOnClickListener(view -> {
            loadingRegistrarMinuta = 1;
            loadinglauncher();
        });
        btnMinutaRegistroRegresar.setOnClickListener(view -> {
            openMessageSalir();
        });
        btnMinutaRegistroCancelar.setOnClickListener(view -> {
            openMessageSalir();
        });
        btnMinutaRegistroPicker.setOnClickListener(view -> {
            openDialogPicker();
        });
        //Asistentes
        btnMinutaRegistrarAsistente.setOnClickListener(view -> {
            openDialogRegistrarAsistente();
        });
        btnMinutaEliminarAsistente.setOnClickListener(view -> {
            openDialogEliminarAsistente();
        });
        //Temas
        btnMinutaRegistrarTema.setOnClickListener(view -> {
           openDialogRegistrarTema();
        });
        btnMinutaEliminarTema.setOnClickListener(view -> {
            opendDialorEliminarTema();
        });
        //Acuerdos
        btnMinutaRegistrarAcuerdo.setOnClickListener(view -> {
            openDialogRegistrarAcuerdo();
        });
        btnMinutaEliminarAcuerdo.setOnClickListener(view -> {
            openDialogEliminarAcuerdo();
        });
        btnMinutaReloj.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();
            hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    if(i < 10){
                        horaS = "0"+i;
                    }else{
                        horaS = i+"";
                    }
                    if(i1 < 10){
                        minutosS = "0"+i1;
                    }else{
                        minutosS = i1+"";
                    }
                    txtMinutaRegistroHora.setText(horaS+":"+minutosS);
                }
            },hora, minutos, true);
            timePickerDialog.show();
        });

    }

    @Override
    public void onBackPressed() {
        openMessageSalir();
    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    //Método para solicitar los permisos en caso que no se tengasn
    public void otorgarpermisos() {
        // Permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    1000);
        }
    }

    //define las variables principales
    public void startVariablesPrincipales(){
        usuario = getIntent().getStringExtra("usuario");
        convoco = getIntent().getStringExtra("Convoco");
        convocoID = getIntent().getStringExtra("ConvocoID");
        lugar = getIntent().getStringExtra("Lugar");
        lugarID = getIntent().getIntExtra("LugarID", 0);
        fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        horaInicio = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    //Método que inicia la información principal
    public void getInfoPrincipal(){
        lblMinutaRegistroConvoco.setText(convoco);
        lblMinutaRegistroLugar.setText(lugar);
        lblMinutaRegistroFecha.setText(fecha);
        txtMinutaRegistroHora.setText(horaInicio);
    }

    //Método que abre el dialogo del picker para la fecha proxima
    public void openDialogPicker(){
        //Traer fecha del sistema en formato añi/mes/día
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //Definimos el valor para nuestras variables que definirán el día
        int mYear = Integer.parseInt(date.substring(0,4));
        int mMonth = Integer.parseInt(date.substring(5,7));
        int mDay = Integer.parseInt(date.substring(8,10));
        //Creamos un datepicker
        DatePickerDialog mDatePicker;
        //Iniciamos el nuevo objeto e indicamos el contexto donde se presentará el dialog
        mDatePicker = new DatePickerDialog(contexto, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                //Variable donde se guarda la fecha seleccionada
                String fechapicker;
                selectedmonth = selectedmonth+1;
                //Guardamos el formato de la fecha
                if(selectedmonth<10){
                    fechapicker="" + selectedyear + "-0" + selectedmonth + "-" + selectedday;
                }
                else {
                    fechapicker="" + selectedyear + "-" + selectedmonth + "-" + selectedday;
                }
                btnMinutaRegistroPicker.setText(fechapicker);
                //Toast.makeText(MinutaReunionLayout.this, "La fecha seleccionada es: "+txtFechaProximaReunion.getText(), Toast.LENGTH_SHORT).show();
            }
        }, mYear, mMonth- 1, mDay);
        mDatePicker.setCancelable(false);
        mDatePicker.setTitle("Fecha de compromiso");
        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDatePicker.show();
    }

    //Método para traer los asistentes
    public ArrayList<ModeloAsistente> getAsistentesAutoCompletado(){
        ArrayList<ModeloAsistente> array = new ArrayList<>();
        try {
            con = new Conexion(contexto);
            PreparedStatement var = con.conexiondbImplementacion().prepareCall("PMovil_R_Minuta_SELECT_Personas");
            ResultSet r = var.executeQuery();
            while(r.next()){
                array.add(new ModeloAsistente(r.getInt("personaID"), r.getString("nombreCompleto"), r.getString("correo"), r.getString("empleadoID"), 0));
            }
            return array;
        }catch (Exception e){
            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //Registrar asistente
    public void openDialogRegistrarAsistente(){
        //Preparar alert
        AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
        LayoutInflater inflater = getLayoutInflater();
        //Traer la vista que se va a llenar
        View view = inflater.inflate(R.layout.minuta_a_reunion_registro_asistentes, null);
        //setear la vista al alert
        alert.setView(view);
        //Abrir la vista
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setCancelable(false);
        //Elementos de la vista
        //EditTexts
        AutoCompleteTextView txtMinutaRegistroAsistenteNombre;
        EditText txtMinutaRegistroAsistenteCorreo;
        //RadioButtons
        RadioButton rbMinutaRegistroAsistenteAsistencia;
        //Buttons
        Button btnMinutaRegistroAsistenteAceptar, btnMinutaRegistroAsistenteCancelar;
        //Referenciar elementos
        txtMinutaRegistroAsistenteNombre = (AutoCompleteTextView) view.findViewById(R.id.txtMinutaRegistroAsistenteNombre);
        txtMinutaRegistroAsistenteCorreo = (EditText) view.findViewById(R.id.txtMinutaRegistroAsistenteCorreo);
        //RadioButtons
        rbMinutaRegistroAsistenteAsistencia = (RadioButton) view.findViewById(R.id.rbMinutaRegistroAsistenteAsistencia);
        //Botones
        btnMinutaRegistroAsistenteCancelar = view.findViewById(R.id.btnMinutaRegistroAsistenteCancelar);
        btnMinutaRegistroAsistenteAceptar = view.findViewById(R.id.btnMinutaRegistroAsistenteAceptar);
        //Llenar autocompeltado
        ArrayAdapter<ModeloAsistente> adaptador = new ArrayAdapter<ModeloAsistente>(contexto, R.layout.ajuste_inventario_spinnervisual, arrayAsistentesAutoCompletado);
        txtMinutaRegistroAsistenteNombre.setAdapter(adaptador);
        //Declarar modelo para guardar información
        final ModeloAsistente[] asistente = new ModeloAsistente[1];
        txtMinutaRegistroAsistenteNombre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Si se selecciona se guarda esta información
                asistente[0] = (ModeloAsistente) txtMinutaRegistroAsistenteNombre.getAdapter().getItem(i);
                txtMinutaRegistroAsistenteCorreo.setText(asistente[0].getCorreo());
            }
        });
        //Acciones botones
        //Registrar asistente
        btnMinutaRegistroAsistenteAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validar campos
                if(txtMinutaRegistroAsistenteNombre.getText().toString().equals("") || txtMinutaRegistroAsistenteCorreo.getText().toString().equals("")){
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Error!")
                            .setIcon(R.drawable.snakerojo)
                            .setMessage("Por favor, verifique que la información esté completa e intentelo nuevamente.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }else{
                    //Validar si el asistente fue seleccionado o está nulo
                    if(asistente[0] == null){
                        //Asistencia
                        int asistencia = 0;
                        if(rbMinutaRegistroAsistenteAsistencia.isChecked()){
                            asistencia = 1;
                        }else{
                            asistencia = 0;
                        }
                        //Agregar info
                        asistente[0] = new ModeloAsistente(0, txtMinutaRegistroAsistenteNombre.getText().toString().toUpperCase(), txtMinutaRegistroAsistenteCorreo.getText().toString().toLowerCase(), "", asistencia, 0);
                        arrayAsistentes.add(asistente[0]);
                    }else{
                        //Buscar si el asistente ya existe y es interno
                        int asistenteEstado = 0;
                        for(int j=0; j<arrayAsistentes.size();j++){
                            if(arrayAsistentes.get(j).getPersonaID() == asistente[0].getPersonaID()){
                                asistenteEstado = 1;
                            }
                        }
                        if(asistenteEstado == 1){
                            new MaterialAlertDialogBuilder(contexto)
                                    .setTitle("¡Error!")
                                    .setIcon(R.drawable.snakerojo)
                                    .setCancelable(false)
                                    .setMessage("Ya existe el registro, intentelo nuevamente.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialog.dismiss();
                                            return;
                                        }
                                    })
                                    .show();
                            return;
                        }else{
                            //Asistencia
                            if(rbMinutaRegistroAsistenteAsistencia.isChecked()){
                                asistente[0].setAsistencia(1);
                            }else{
                                asistente[0].setAsistencia(0);
                            }
                            //Sí el Id del empleado es nulo entonces no debería porque agregarse a responsables
                            ModeloAsistente m = new ModeloAsistente(asistente[0].getPersonaID(), txtMinutaRegistroAsistenteNombre.getText().toString().toUpperCase(), txtMinutaRegistroAsistenteCorreo.getText().toString().toLowerCase(), asistente[0].getEmpleadoID(), asistente[0].getAsistencia(), 0);
                            arrayAsistentes.add(m);
                            /*
                            if(m.getEmpleadoID() == null || m.getEmpleadoID().equals("")){
                            if(m.getEmpleadoID() == null || m.getEmpleadoID().equals("")){
                                arrayAsistentes.add(m);
                            }else{
                                arrayAsistentes.add(m);
                                arrayResponsables.add(m);
                            }
                            */
                        }
                    }
                    //Comprobar si se efectúa la acción en el adapter
                    try {
                        adapterAsistente.notifyDataSetChanged();
                        if(sonido == 1){
                            MediaPlayer mp = MediaPlayer.create(contexto, R.raw.definite);
                            mp.start();
                        }
                        dialog.dismiss();
                        new MaterialAlertDialogBuilder(contexto)
                                .setTitle("¡Éxito!")
                                .setIcon(R.drawable.correcto)
                                .setMessage("Registro realizado exitosamente.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }catch (Exception e){
                        dialog.dismiss();
                        Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //Cancelar
        btnMinutaRegistroAsistenteCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    //Eliminar asistentes
    public void openDialogEliminarAsistente(){
        if(arrayAsistentes.size() == 0){
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Error!")
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("No hay registros existentes, imposible realizar la acción")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    })
            .show();
        }else{
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Confirmación!")
                    .setIcon(R.drawable.confirmacion)
                    .setCancelable(false)
                    .setMessage("¿Seguro que desea eliminar los elementos seleccionados?")
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    })
                    .setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<ModeloAsistente> arrayAsistentesTemp = new ArrayList<>();
                            //arrayResponsables = arrayAsistentes;
                            int hayEliminar = 0;
                            for(int j=0; j<arrayAsistentes.size(); j++){
                                if(arrayAsistentes.get(j).getIsSelected() == 0){
                                    arrayAsistentesTemp.add(arrayAsistentes.get(j));
                                }else{
                                    hayEliminar = 1;
                                }
                            }
                            if(hayEliminar == 1){
                                arrayAsistentes = arrayAsistentesTemp;
                                try {
                                    adapterAsistente.notifyDataSetChanged();
                                    new MaterialAlertDialogBuilder(contexto)
                                            .setTitle("¡Éxito!")
                                            .setIcon(R.drawable.correcto)
                                            .setMessage("Se han eliminado los registros correctamente.")
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //No hacer nada
                                                }
                                            })
                                            .show();
                                }catch (Exception e){
                                    Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                new MaterialAlertDialogBuilder(contexto)
                                        .setTitle("¡Error!")
                                        .setIcon(R.drawable.snakerojo)
                                        .setMessage("No hay registros seleccionados, imposible realizar la acción")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //No hacer nada
                                            }
                                        })
                                        .show();
                            }
                        }
                    })
                    .show();
        }
    }

    //registrar temas
    public void openDialogRegistrarTema(){
        //Preparar el AlertDialog
        AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
        LayoutInflater inflater = getLayoutInflater();
        //Traer la vista que se va a abrir
        View view = inflater.inflate(R.layout.minuta_a_reunion_registro_temas, null);
        alert.setView(view);
        //Abrir la vista
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setCancelable(false);
        //Elementos vista
        //EditTexts
        EditText txtMinutaRegistroTemaTema, txtMinutaRegistroTemaTiempoEst;
        //Botónes
        Button btnMinutaRegistroTemaAceptar, btnMinutaRegistroTemaCancelar;
        //ImageButton
        ImageButton btnMinutaRelojTema;
        //Referenciar elementos
        //EditText
        txtMinutaRegistroTemaTema = (EditText) view.findViewById(R.id.txtMinutaRegistroTemaTema);
        txtMinutaRegistroTemaTiempoEst = (EditText) view.findViewById(R.id.txtMinutaRegistroTemaTiempoEst);
        //Buttons
        btnMinutaRegistroTemaAceptar = (Button) view.findViewById(R.id.btnMinutaRegistroTemaAceptar);
        btnMinutaRegistroTemaCancelar = (Button) view.findViewById(R.id.btnMinutaRegistroTemaCancelar);
        btnMinutaRelojTema = view.findViewById(R.id.btnMinutaRelojTema);
        //Acciones botones
        //Cancelar acción
        btnMinutaRegistroTemaCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnMinutaRelojTema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                hora = c.get(Calendar.HOUR_OF_DAY);
                minutos = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if(i < 10){horaS = "0"+i;}else{horaS = i+"";}
                        if(i1 < 10){minutosS = "0"+i1;}else{minutosS = i1+"";}
                        txtMinutaRegistroTemaTiempoEst.setText(horaS+":"+minutosS);
                    }
                },hora, minutos, true);
                timePickerDialog.show();
            }
        });

        //Agregar tema
        btnMinutaRegistroTemaAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validar campos
                if(txtMinutaRegistroTemaTema.getText().toString().equals("")){
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Error!")
                            .setIcon(R.drawable.snakerojo)
                            .setMessage("Por favor, verifique que la información esté completa e intentelo nuevamente.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }else{
                    //Crear tema
                    ModeloTema tema = new ModeloTema(txtMinutaRegistroTemaTema.getText().toString(), txtMinutaRegistroTemaTiempoEst.getText().toString(), 0);
                    arrayTemas.add(tema);
                    try {
                        adapterTema.notifyDataSetChanged();
                        if(sonido == 1){
                            MediaPlayer mp = MediaPlayer.create(contexto, R.raw.definite);
                            mp.start();
                        }
                        dialog.dismiss();
                        new MaterialAlertDialogBuilder(contexto)
                                .setTitle("¡Éxito!")
                                .setIcon(R.drawable.correcto)
                                .setMessage("Registro realizado exitosamente.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }catch (Exception e){
                        Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    //Notificar al adapter
                }
            }
        });
    }

    //Editar temas
    public void openDialogEditarTema(ModeloTema tema, int posicion){
        //Preparar el AlertDialog
        AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
        LayoutInflater inflater = getLayoutInflater();
        //Traer la vista que se va a abrir
        View view = inflater.inflate(R.layout.minuta_a_reunion_registro_temas_editar, null);
        alert.setView(view);
        //Abrir la vista
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setCancelable(false);
        //Elementos
        //EditText
        EditText txtMinutaRegistroTemaEditarTema, txtMinutaRegistroTemaEditarTiempoEst;
        //Buttons
        Button btnMinutaRegistroTemaEditarCancelar, btnMinutaRegistroTemaEditarAceptar;
        //Referenciar elementos
        txtMinutaRegistroTemaEditarTema = view.findViewById(R.id.txtMinutaRegistroTemaEditarTema);
        txtMinutaRegistroTemaEditarTema.setText(tema.getTema());
        txtMinutaRegistroTemaEditarTiempoEst = view.findViewById(R.id.txtMinutaRegistroTemaEditarTiempoEst);
        txtMinutaRegistroTemaEditarTiempoEst.setText(tema.getTiempoEstimado());
        btnMinutaRegistroTemaEditarCancelar = view.findViewById(R.id.btnMinutaRegistroTemaEditarCancelar);
        btnMinutaRegistroTemaEditarAceptar = view.findViewById(R.id.btnMinutaRegistroTemaEditarAceptar);

        //Acciones botones
        btnMinutaRegistroTemaEditarCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnMinutaRegistroTemaEditarAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validar campos
                if(txtMinutaRegistroTemaEditarTema.getText().toString().equals("")){
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Error!")
                            .setIcon(R.drawable.snakerojo)
                            .setMessage("Por favor, verifique que la información esté completa e intentelo nuevamente.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }else{
                    arrayTemas.get(posicion).setTema(txtMinutaRegistroTemaEditarTema.getText().toString());
                    arrayTemas.get(posicion).setTiempoEstimado(txtMinutaRegistroTemaEditarTiempoEst.getText().toString());
                    try {
                        adapterTema.notifyDataSetChanged();
                        if(sonido == 1){
                            MediaPlayer mp = MediaPlayer.create(contexto, R.raw.definite);
                            mp.start();
                        }
                        dialog.dismiss();
                        new MaterialAlertDialogBuilder(contexto)
                                .setTitle("¡Éxito!")
                                .setIcon(R.drawable.correcto)
                                .setMessage("Se ha actualizado el registro exitosamente")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //No hacer nada
                                    }
                                })
                                .show();
                    }catch (Exception e){
                        Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //Eliminar temas
    public void opendDialorEliminarTema(){
        //Validad si hay registros
        if(arrayTemas.size() == 0){
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Error!")
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("No hay registros existentes, imposible realizar la acción.")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    })
                    .show();
        }else {
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Confirmación!")
                    .setIcon(R.drawable.confirmacion)
                    .setCancelable(false)
                    .setMessage("¿Seguro que desea eliminar los elementos seleccionados?")
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    })
                    .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Eliminar elementos
                            //ArrayTemp
                            ArrayList<ModeloTema> arrayTemaTemp = new ArrayList<>();
                            //Validar si hay registros para eliminar
                            int hayEliminar = 0;
                            //Recorrer arreglo
                            for (int j = 0; j < arrayTemas.size(); j++) {
                                //Validar si está seleccionado
                                if (arrayTemas.get(j).getIsSelected() == 0) {
                                    //agregar los que no estén seleccionados al array temporal
                                    arrayTemaTemp.add(arrayTemas.get(j));
                                } else {
                                    //Si hay para eliminar
                                    hayEliminar = 1;
                                }
                            }
                            //Si si hay registros para eliminar se elimian sino no
                            if (hayEliminar == 1) {
                                arrayTemas = arrayTemaTemp;
                                try {
                                    adapterTema.notifyDataSetChanged();
                                    new MaterialAlertDialogBuilder(contexto)
                                            .setTitle("¡Éxito!")
                                            .setIcon(R.drawable.correcto)
                                            .setMessage("Se han eliminado los registros correctamente.")
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //No hacer nada
                                                }
                                            })
                                            .show();
                                } catch (Exception e) {
                                    Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                new MaterialAlertDialogBuilder(contexto)
                                        .setTitle("¡Error!")
                                        .setIcon(R.drawable.snakerojo)
                                        .setMessage("No hay registros seleccionados, imposible realizar la acción.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //No hacer nada
                                            }
                                        })
                                        .show();
                            }
                        }
                    })
                    .show();
        }
    }

    //Registrar acuerdo
    public void openDialogRegistrarAcuerdo(){
        AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.minuta_a_reunion_registro_acuerdos, null);
        alert.setView(view);
        final AlertDialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.show();
        //Elementos de la sub vista
        //Spinner
        Spinner spinnerMinutaRegistroAcuerdoResponsables;
        //EditTexts
        EditText txtMinutaRegistroAcuerdoAcuerdo;
        //Buttons
        Button btnMinutaRegistroAcuerdoCancelar, btnMinutaRegistroAcuerdoAceptar, btnMinutaRegistroAcuerdoPicker;
        //Referenciar elementos
        //spinner
        spinnerMinutaRegistroAcuerdoResponsables = view.findViewById(R.id.spinnerMinutaRegistroAcuerdoResponsable);
        ArrayAdapter<ModeloAsistente> adapter = new ArrayAdapter<ModeloAsistente>(contexto, R.layout.minuta_a_spinner, arrayResponsables);
        spinnerMinutaRegistroAcuerdoResponsables.setAdapter(adapter);
        //EditTexts
        txtMinutaRegistroAcuerdoAcuerdo = view.findViewById(R.id.txtMinutaRegistroAcuerdoAcuerdo);
        //Buttons
        btnMinutaRegistroAcuerdoPicker = view.findViewById(R.id.btnMinutaRegistroAcuerdoPicker);
        btnMinutaRegistroAcuerdoCancelar = view.findViewById(R.id.btnMinutaRegistroAcuerdoCancelar);
        btnMinutaRegistroAcuerdoAceptar = view.findViewById(R.id.btnMinutaRegistroAcuerdoAceptar);
        //Acciones botones
        //Picker fecha compromiso
        btnMinutaRegistroAcuerdoPicker.setOnClickListener(view1 -> {
            //Traer fecha del sistema en formato añi/mes/día
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            //Definimos el valor para nuestras variables que definirán el día
            int mYear = Integer.parseInt(date.substring(0,4));
            int mMonth = Integer.parseInt(date.substring(5,7));
            int mDay = Integer.parseInt(date.substring(8,10));
            //Creamos un datepicker
            DatePickerDialog mDatePicker;
            //Iniciamos el nuevo objeto e indicamos el contexto donde se presentará el dialog
            mDatePicker = new DatePickerDialog(contexto, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                    //Variable donde se guarda la fecha seleccionada
                    String fechapicker;
                    selectedmonth = selectedmonth+1;
                    //Guardamos el formato de la fecha
                    if(selectedmonth<10){
                        fechapicker="" + selectedyear + "-0" + selectedmonth + "-" + selectedday;
                    }
                    else {
                        fechapicker="" + selectedyear + "-" + selectedmonth + "-" + selectedday;
                    }
                    btnMinutaRegistroAcuerdoPicker.setText(fechapicker);
                    //Toast.makeText(MinutaReunionLayout.this, "La fecha seleccionada es: "+txtFechaProximaReunion.getText(), Toast.LENGTH_SHORT).show();
                }
            }, mYear, mMonth- 1, mDay);
            mDatePicker.setCancelable(false);
            mDatePicker.setTitle("Fecha de compromiso");
            mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            mDatePicker.show();
        });
        btnMinutaRegistroAcuerdoCancelar.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        btnMinutaRegistroAcuerdoAceptar.setOnClickListener(view1 -> {
            if(arrayResponsables.size() == 0){
                new MaterialAlertDialogBuilder(contexto)
                        .setTitle("¡Error!")
                        .setIcon(R.drawable.snakerojo)
                        .setMessage("No hay responsables a quienes se les pueda asignar un acuerdo.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }else{
                if(txtMinutaRegistroAcuerdoAcuerdo.getText().toString().equals("") || btnMinutaRegistroAcuerdoPicker.getText().toString().equals("SELECCIONAR")){
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Error!")
                            .setIcon(R.drawable.snakerojo)
                            .setMessage("Por favor, verifique que la información esté completa e intentelo nuevamente.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }else{
                    final ModeloAsistente asistente = (ModeloAsistente) spinnerMinutaRegistroAcuerdoResponsables.getSelectedItem();
                    String nombreAsistente = asistente.getNombre();
                    int personaID  = asistente.getPersonaID();
                    ModeloAcuerdo acuerdo = new ModeloAcuerdo(txtMinutaRegistroAcuerdoAcuerdo.getText().toString(), nombreAsistente, personaID, btnMinutaRegistroAcuerdoPicker.getText().toString(), 0);
                    arrayAcuerdos.add(acuerdo);
                    try {
                        adapterAcuerdo.notifyDataSetChanged();
                        if(sonido == 1){
                            MediaPlayer mp = MediaPlayer.create(contexto, R.raw.definite);
                            mp.start();
                        }
                        dialog.dismiss();
                        new MaterialAlertDialogBuilder(contexto)
                                .setTitle("¡Éxito!")
                                .setIcon(R.drawable.correcto)
                                .setMessage("Registro realizado exitosamente.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }catch (Exception e){
                        Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    //Editar acuerdo
    public void openDialogEditarAcuerdo(ModeloAcuerdo modelo, int posicion){
        AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.minuta_a_reunion_registro_acuerdos_editar, null);
        alert.setView(view);

        final AlertDialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.show();
        //Elementos de la sub activity
        //Spinner
        Spinner spinnerMinutaRegistroAcuerdoEditarResponsables;
        //EditText
        EditText txtMinutaRegistroAcuerdoEditarAcuerdo;
        //Buttons
        Button btnMinutaRegistroAcuerdoEditarCancelar, btnMinutaRegistroAcuerdoEditarAceptar, btnMinutaRegistroAcuerdoEditarPicker;
        //Referenciar elementos
        //Spinner
        spinnerMinutaRegistroAcuerdoEditarResponsables = view.findViewById(R.id.spinnerMinutaRegistroAcuerdoEditarResponsables);
        //EditText
        txtMinutaRegistroAcuerdoEditarAcuerdo = view.findViewById(R.id.txtMinutaRegistroAcuerdoEditarAcuerdo);
        //Buttons
        btnMinutaRegistroAcuerdoEditarCancelar = view.findViewById(R.id.btnMinutaRegistroAcuerdoEditarCancelar);
        btnMinutaRegistroAcuerdoEditarAceptar = view.findViewById(R.id.btnMinutaRegistroAcuerdoEditarAceptar);
        //ImageButton
        btnMinutaRegistroAcuerdoEditarPicker = view.findViewById(R.id.btnMinutaRegistroAcuerdoEditarPicker);
        //Información del item seleccionado
        txtMinutaRegistroAcuerdoEditarAcuerdo.setText(arrayAcuerdos.get(posicion).getAcuerdo());
        btnMinutaRegistroAcuerdoEditarPicker.setText(arrayAcuerdos.get(posicion).getFechaCompromiso());
        ArrayAdapter<ModeloAsistente> adaptador = new ArrayAdapter<ModeloAsistente>(contexto, R.layout.minuta_a_spinner, arrayResponsables);
        spinnerMinutaRegistroAcuerdoEditarResponsables.setAdapter(adaptador);
        //Buscar el id del responsablie
        int responsableIndex = 0;
        for(int i=0; i<arrayResponsables.size(); i++){
            if(arrayResponsables.get(i).getPersonaID() == modelo.getPersonaID()){
                responsableIndex = i;
                break;
            }
        }
        spinnerMinutaRegistroAcuerdoEditarResponsables.setSelection(responsableIndex);

        //Acciones botones
        btnMinutaRegistroAcuerdoEditarCancelar.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        btnMinutaRegistroAcuerdoEditarAceptar.setOnClickListener(view1 -> {
            if(txtMinutaRegistroAcuerdoEditarAcuerdo.getText().toString().equals("") || btnMinutaRegistroAcuerdoEditarPicker.getText().toString().equals("")){
                new MaterialAlertDialogBuilder(contexto)
                        .setTitle("¡Error!")
                        .setIcon(R.drawable.snakerojo)
                        .setMessage("Por favor, verifique que la información esté completa e intentelo nuevamente.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }else{
                arrayAcuerdos.get(posicion).setAcuerdo(txtMinutaRegistroAcuerdoEditarAcuerdo.getText().toString());
                arrayAcuerdos.get(posicion).setFechaCompromiso(btnMinutaRegistroAcuerdoEditarPicker.getText().toString());
                ModeloAsistente asistente = (ModeloAsistente) spinnerMinutaRegistroAcuerdoEditarResponsables.getSelectedItem();
                arrayAcuerdos.get(posicion).setEmpleado(asistente.getNombre());
                arrayAcuerdos.get(posicion).setPersonaID(asistente.getPersonaID());
                try {
                    adapterAcuerdo.notifyDataSetChanged();
                    if(sonido == 1){
                        MediaPlayer mp = MediaPlayer.create(contexto, R.raw.definite);
                        mp.start();
                    }
                    dialog.dismiss();
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Éxito!")
                            .setIcon(R.drawable.correcto)
                            .setMessage("Se ha actualizado el registro exitosamente")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //No hacer nada
                                }
                            })
                            .show();
                }catch (Exception e){
                    Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnMinutaRegistroAcuerdoEditarPicker.setOnClickListener(view1 -> {
            //Traer fecha del sistema en formato añi/mes/día
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            //Definimos el valor para nuestras variables que definirán el día
            int mYear = Integer.parseInt(date.substring(0,4));
            int mMonth = Integer.parseInt(date.substring(5,7));
            int mDay = Integer.parseInt(date.substring(8,10));
            //Creamos un datepicker
            DatePickerDialog mDatePicker;
            //Iniciamos el nuevo objeto e indicamos el contexto donde se presentará el dialog
            mDatePicker = new DatePickerDialog(contexto, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                    //Variable donde se guarda la fecha seleccionada
                    String fechapicker;
                    selectedmonth = selectedmonth+1;
                    //Guardamos el formato de la fecha
                    if(selectedmonth<10){
                        fechapicker="" + selectedyear + "-0" + selectedmonth + "-" + selectedday;
                    }
                    else {
                        fechapicker="" + selectedyear + "-" + selectedmonth + "-" + selectedday;
                    }
                    btnMinutaRegistroAcuerdoEditarPicker.setText(fechapicker);
                    //Toast.makeText(MinutaReunionLayout.this, "La fecha seleccionada es: "+txtFechaProximaReunion.getText(), Toast.LENGTH_SHORT).show();
                }
            }, mYear, mMonth- 1, mDay);
            mDatePicker.setCancelable(false);
            mDatePicker.setTitle("Fecha de compromiso");
            mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            mDatePicker.show();
        });
    }

    //Eliminar acuerdo
    public void openDialogEliminarAcuerdo(){
        if(arrayAcuerdos.size() == 0){
            new MaterialAlertDialogBuilder(contexto)
            .setTitle("¡Error!")
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("No hay registros existentes, imposible realizar la acción.")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    })
                    .show();
        }else{
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Confirmación!")
                    .setIcon(R.drawable.confirmacion)
                    .setCancelable(false)
                    .setMessage("¿Seguro que desea eliminar los elementos seleccionados?")
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    })
                    .setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<ModeloAcuerdo> arrayAcuerdosTemp = new ArrayList<>();
                            int hayEliminar = 0;
                            for(int j=0; j<arrayAcuerdos.size(); j++){
                                if(arrayAcuerdos.get(j).getIsSelected() == 0){
                                    arrayAcuerdosTemp.add(arrayAcuerdos.get(j));
                                }else{
                                    hayEliminar = 1;
                                }
                            }
                            if(hayEliminar == 1){
                                arrayAcuerdos = arrayAcuerdosTemp;
                                try {
                                    adapterAcuerdo.notifyDataSetChanged();
                                    new MaterialAlertDialogBuilder(contexto)
                                            .setTitle("¡Éxito!")
                                            .setIcon(R.drawable.correcto)
                                            .setMessage("Se han eliminado los registros correctamente.")
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //No hacer nada
                                                }
                                            })
                                            .show();
                                }catch (Exception e){
                                    Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                new MaterialAlertDialogBuilder(contexto)
                                        .setTitle("¡Error!")
                                        .setIcon(R.drawable.snakerojo)
                                        .setMessage("No hay registros seleccionados, imposible realizar la acción.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //No hacer nada
                                            }
                                        })
                                        .show();
                            }
                        }
                    })
                    .show();
        }
    }

    //Método para salir de la pantalla
    public void openMessageSalir(){
        new MaterialAlertDialogBuilder(contexto)
                .setTitle("¡Confirmación")
                .setIcon(R.drawable.confirmacion)
                .setCancelable(false)
                .setMessage("Si sale podría perder la información ¿Desea salir?")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    //Método para subir los cambios a la bd
    public void setMinutaBD(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                desableButtons();
                int reunionID = 0;
                Conexion con = new Conexion(contexto);
                horaFin = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                try {
                    //Insertar reunión
                    PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_Reunion_INSERT ?,?,?,?,?,?;");
                    stmt.setString(1, horaInicio);
                    stmt.setString(2, horaFin);
                    stmt.setString(3, fecha);
                    if(btnMinutaRegistroPicker.getText().toString().equals("Fecha proxima reunión") || btnMinutaRegistroPicker.getText().toString().equals("FECHA PROXIMA REUNIÓN")){
                        stmt.setString(4, null);
                    }else{
                        stmt.setString(4, btnMinutaRegistroPicker.getText().toString());
                    }
                    stmt.setInt(5, lugarID);
                    stmt.setString(6, convocoID);
                    //Guardar el valor Retornado (UltimoID insertado)
                    ResultSet r = stmt.executeQuery();
                    while(r.next()){
                        reunionID = r.getInt("reunionID");
                    }
                    //Insertar temas
                    for(int i=0; i<arrayTemas.size();i++){
                        PreparedStatement stmt2 = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_Tema_INSERT ?,?,?;");
                        stmt2.setString(1, arrayTemas.get(i).getTema());
                        stmt2.setString(2, arrayTemas.get(i).getTiempoEstimado());
                        stmt2.setInt(3, reunionID);
                        stmt2.execute();
                    }

                    //Insertar nuevas personas
                    for(int i=0; i<arrayAsistentes.size(); i++){
                        if(arrayAsistentes.get(i).getPersonaID() == 0){
                            int personaNuevaID = 0;
                            PreparedStatement stmt4 = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_NuevasPersonas_INSERT ?,?,?");
                            stmt4.setString(1, arrayAsistentes.get(i).getNombre());
                            stmt4.setString(2, arrayAsistentes.get(i).getCorreo());
                            stmt4.setString(3, null);
                            ResultSet r2 = stmt4.executeQuery();
                            while(r2.next()){
                                personaNuevaID = r2.getInt("personaID");
                            }
                            arrayAsistentes.get(i).setPersonaID(personaNuevaID);

                            //Insertar ReunionEmpelado
                            PreparedStatement stm5 = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_ReunionPersona_INSERT ?,?,?;");
                            stm5.setInt(1, arrayAsistentes.get(i).getAsistencia());
                            stm5.setInt(2, personaNuevaID);
                            stm5.setInt(3, reunionID);
                            stm5.execute();
                        }else{
                            //Insertar ReunionEmpelado
                            PreparedStatement stm5 = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_ReunionPersona_INSERT ?,?,?;");
                            stm5.setInt(1, arrayAsistentes.get(i).getAsistencia());
                            stm5.setInt(2, arrayAsistentes.get(i).getPersonaID());
                            stm5.setInt(3, reunionID);
                            stm5.execute();
                        }
                    }

                    //Asignar IDS
                    for(int i=0; i<arrayAcuerdos.size(); i++){
                        for(int j=0; j<arrayAsistentes.size(); j++){
                            if(arrayAcuerdos.get(i).getEmpleado().equals(arrayAsistentes.get(j).getNombre())){
                                arrayAcuerdos.get(i).setPersonaID(arrayAsistentes.get(j).getPersonaID());
                            }
                        }
                    }

                    //Insertar acuerdos
                    for(int i=0; i<arrayAcuerdos.size();i++){
                        PreparedStatement stmt3 = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_Acuerdo_INSERT ?,?,?,?;");
                        stmt3.setString(1, arrayAcuerdos.get(i).getAcuerdo());
                        stmt3.setString(2, arrayAcuerdos.get(i).getFechaCompromiso());
                        stmt3.setInt(3, arrayAcuerdos.get(i).getPersonaID());
                        stmt3.setInt(4, reunionID);
                        stmt3.execute();
                    }

                    crearPDF();
                    if(sonido == 1){
                        MediaPlayer mp = MediaPlayer.create(contexto, R.raw.definite);
                        mp.start();
                    }
                    //con.conexiondbImplementacion().commit();
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Éxito!")
                            .setIcon(R.drawable.correcto)
                            .setCancelable(false)
                            .setMessage("Minuta generada exitosamente.")
                            .setPositiveButton("Enviar email", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendEmail();
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //
                                    finish();
                                }
                            })
                            .show();
                }catch (Exception e){
                    if(sonido == 1){
                        MediaPlayer mp = MediaPlayer.create(contexto, R.raw.definite);
                        mp.start();
                    }
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Error fatal!")
                            .setIcon(R.drawable.snakerojo)
                            .setMessage("Error grave: "+e.getMessage())
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //No hacer nada
                                    enableButtons();
                                }
                            })
                            .show();
                    enableButtons();
                }
            }
        });
    }

    //Método para crear el respaldo PDF
    public void crearPDF() {
        Document documento = new Document();
        try {
            horaFin = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            documento.setMargins(-50f, -50f, 5f, 5f);
            //File file = crearFichero("Reporte de minuta "+lblFechaMinuta.getText().toString()+" "+txtHoraInicioMinuta.getText().toString()+" - "+horaFin+".pdf");
            File file = crearFichero("Reporte de minuta "+fecha+" HI "+horaInicio.replace(":","_")+" HF "+horaFin.replace(":", "_")+".pdf");
            //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "/Minutas_IVRA/"+"Reporte de minuta "+lblFechaMinuta.getText().toString()+" HI "+txtHoraInicioMinuta.getText().toString().replace(":","_")+" HF "+horaFin.replace(":", "_")+".pdf");

            fileUri = file;
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            documento.setPageSize(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);


            HeaderFooter footer = new HeaderFooter(new Phrase("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tPágina:" ), new Phrase("\t\t\t|\t\t\tReporte realizado por: "+usuario+"\t\t\t|\t\t\tFecha proxima reunión: "+btnMinutaRegistroPicker.getText().toString()));
            documento.setFooter(footer);


            documento.open();
            Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, String.valueOf(18), Font.BOLD, Color.BLACK);
            Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, String.valueOf(16), Font.BOLD, Color.BLACK);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.shimaco);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            PdfPTable a = new PdfPTable(3);
            a.setTotalWidth(1000);
            a.addCell(imagen);

            PdfPCell cellt1 = new PdfPCell(new Phrase("INFORMACIÓN DE LA MINUTA\n\nFecha: "+fecha+"\n\nHora inicio: "+horaInicio+"\n\nHora fin: "+horaFin));
            cellt1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellt1.setVerticalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellt1);
            PdfPCell celltUsuario = new PdfPCell(new Phrase("CONVOCÓ: \n"+convoco));
            celltUsuario.setHorizontalAlignment(Element.ALIGN_CENTER);
            celltUsuario.setVerticalAlignment(Element.ALIGN_MIDDLE);
            a.addCell(celltUsuario);

            documento.add(a);

            //Agregar asistentes
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tÉsta reunión se llevó a cabo en: "+lugar));
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAsistentes"));
            documento.add(new Paragraph("\n"));

            PdfPTable asistentes = new PdfPTable(4);
            asistentes.setWidthPercentage(80);
            float[] widthsAsistentes = new float[] {5f, 33f, 32f, 10f};
            asistentes.setWidths(widthsAsistentes);

            PdfPCell numero = new PdfPCell(new Phrase("Núm"));
            numero.setVerticalAlignment(Element.ALIGN_MIDDLE);
            numero.setHorizontalAlignment(Element.ALIGN_CENTER);
            asistentes.addCell(numero);
            PdfPCell nombre = new PdfPCell(new Phrase("Nombre"));
            nombre.setVerticalAlignment(Element.ALIGN_MIDDLE);
            nombre.setHorizontalAlignment(Element.ALIGN_CENTER);
            asistentes.addCell(nombre);
            PdfPCell email = new PdfPCell(new Phrase("Email"));
            email.setVerticalAlignment(Element.ALIGN_MIDDLE);
            email.setHorizontalAlignment(Element.ALIGN_CENTER);
            asistentes.addCell(email);
            PdfPCell asistencia = new PdfPCell(new Phrase("Asistencia"));
            asistencia.setVerticalAlignment(Element.ALIGN_MIDDLE);
            asistencia.setHorizontalAlignment(Element.ALIGN_CENTER);
            asistentes.addCell(asistencia);
            documento.add(asistentes);

            PdfPTable asistentesDatos = new PdfPTable(4);
            asistentesDatos.setWidthPercentage(80);
            float[] widthsAsistentesDatos = new float[] {5f, 33f, 32f, 10f};
            asistentesDatos.setWidths(widthsAsistentesDatos);
            for(int i=0; i<arrayAsistentes.size();i++){
                PdfPCell dato1 = new PdfPCell(new Phrase(""+(i+1)));
                dato1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dato1.setHorizontalAlignment(Element.ALIGN_CENTER);
                asistentesDatos.addCell(dato1);
                PdfPCell dato2 = new PdfPCell(new Phrase(arrayAsistentes.get(i).getNombre()));
                dato2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dato2.setHorizontalAlignment((Element.ALIGN_CENTER));
                asistentesDatos.addCell(dato2);
                PdfPCell dato4 = new PdfPCell(new Phrase(arrayAsistentes.get(i).getCorreo()));
                dato4.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dato4.setHorizontalAlignment(Element.ALIGN_CENTER);
                asistentesDatos.addCell(dato4);
                if(arrayAsistentes.get(i).getAsistencia() == 0){
                    PdfPCell dato5 = new PdfPCell(new Phrase("Faltó"));
                    dato5.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dato5.setHorizontalAlignment(Element.ALIGN_CENTER);
                    asistentesDatos.addCell(dato5);
                }else{
                    PdfPCell dato5 = new PdfPCell(new Phrase("Asistió"));
                    dato5.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dato5.setHorizontalAlignment(Element.ALIGN_CENTER);
                    asistentesDatos.addCell(dato5);
                }
            }
            documento.add(asistentesDatos);

            //Agregar temas
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tTemas vistos"));
            documento.add(new Paragraph("\n"));

            PdfPTable temas = new PdfPTable(3);
            temas.setWidthPercentage(80);
            float[] widthsTemas = new float[] {5f, 55f, 20f};
            temas.setWidths(widthsTemas);

            PdfPCell ntema = new PdfPCell(new Phrase("Núm"));
            ntema.setVerticalAlignment(Element.ALIGN_MIDDLE);
            ntema.setHorizontalAlignment(Element.ALIGN_CENTER);
            temas.addCell(ntema);
            PdfPCell tema = new PdfPCell(new Phrase("Tema"));
            tema.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tema.setHorizontalAlignment(Element.ALIGN_CENTER);
            temas.addCell(tema);
            PdfPCell tiempoEstimado = new PdfPCell(new Phrase("Tiempo estimado"));
            tiempoEstimado.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tiempoEstimado.setHorizontalAlignment(Element.ALIGN_CENTER);
            temas.addCell(tiempoEstimado);

            documento.add(temas);

            PdfPTable temasDatos = new PdfPTable(3);
            temasDatos.setWidthPercentage(80);
            float[] widthsTemasDatos = new float[] {5f, 55f, 20f};
            temasDatos.setWidths(widthsTemasDatos);
            for(int i=0; i<arrayTemas.size();i++){
                PdfPCell dato1 = new PdfPCell(new Phrase(""+(i+1)));
                dato1.setHorizontalAlignment(Element.ALIGN_CENTER);
                dato1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                temasDatos.addCell(dato1);
                PdfPCell dato2 = new PdfPCell(new Phrase(arrayTemas.get(i).getTema()));
                dato2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dato2.setHorizontalAlignment(Element.ALIGN_CENTER);
                temasDatos.addCell(dato2);
                PdfPCell dato3 = new PdfPCell(new Phrase(arrayTemas.get(i).getTiempoEstimado()));
                dato3.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dato3.setHorizontalAlignment(Element.ALIGN_CENTER);
                temasDatos.addCell(dato3);
            }
            documento.add(temasDatos);

            //Agregar acuerdos y responsables
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAcuerdos y responsables"));
            documento.add(new Paragraph("\n"));

            PdfPTable acuerdos = new PdfPTable(4);
            acuerdos.setWidthPercentage(80);
            float[] widthsAcuerdos = new float[] {5f, 40f, 25f, 10f};
            acuerdos.setWidths(widthsAcuerdos);
            PdfPCell nacuerdo = new PdfPCell(new Phrase("Núm"));
            nacuerdo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            nacuerdo.setHorizontalAlignment(Element.ALIGN_CENTER);
            acuerdos.addCell(nacuerdo);
            PdfPCell acuerdo = new PdfPCell(new Phrase("Acuerdo"));
            acuerdo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            acuerdo.setHorizontalAlignment(Element.ALIGN_CENTER);
            acuerdos.addCell(acuerdo);
            PdfPCell responsable = new PdfPCell(new Phrase("Responsable"));
            responsable.setVerticalAlignment(Element.ALIGN_MIDDLE);
            responsable.setHorizontalAlignment(Element.ALIGN_CENTER);
            acuerdos.addCell(responsable);
            PdfPCell fechaComp = new PdfPCell(new Phrase("Fecha entrega"));
            fechaComp.setVerticalAlignment(Element.ALIGN_MIDDLE);
            fechaComp.setHorizontalAlignment(Element.ALIGN_CENTER);
            acuerdos.addCell(fechaComp);

            documento.add(acuerdos);

            PdfPTable temasAcuerdos = new PdfPTable(4);
            temasAcuerdos.setWidthPercentage(80);
            float[] widthstemasAcuerdos = new float[] {5f, 40f, 25f, 10f};
            temasAcuerdos.setWidths(widthstemasAcuerdos);
            for(int i=0; i<arrayAcuerdos.size();i++){
                PdfPCell dato1 = new PdfPCell(new Phrase(""+(i+1)));
                dato1.setHorizontalAlignment(Element.ALIGN_CENTER);
                dato1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                temasAcuerdos.addCell(dato1);
                PdfPCell dato2 = new PdfPCell(new Phrase(arrayAcuerdos.get(i).getAcuerdo()));
                dato2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dato2.setHorizontalAlignment(Element.ALIGN_CENTER);
                temasAcuerdos.addCell(dato2);
                PdfPCell dato3 = new PdfPCell(new Phrase(arrayAcuerdos.get(i).getEmpleado()));
                dato3.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dato3.setHorizontalAlignment(Element.ALIGN_CENTER);
                temasAcuerdos.addCell(dato3);
                PdfPCell dato4 = new PdfPCell(new Phrase(arrayAcuerdos.get(i).getFechaCompromiso()));
                dato4.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dato4.setHorizontalAlignment(Element.ALIGN_CENTER);
                temasAcuerdos.addCell(dato4);
            }
            documento.add(temasAcuerdos);
            enableButtons();
        } catch (DocumentException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
            enableButtons();
        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
            enableButtons();
        } finally {
            documento.close();
            enableButtons();
        }
    }

    //Método para enviar el correo electrónoci
    //Pensar en dejar a la lic y al ingeniero cómo predeterminados
    public void sendEmail(){
        //File outPutFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "/Minutas_IVRA/12.pdf");
        File outPutFile = fileUri;
        Uri uriFile = Uri.fromFile(outPutFile);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uriFile = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", outPutFile);
        }

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent .setType("text/plain");
        emailIntent.setType("application/pdf");
        //Cargamos los destinataarios
        String to[] = new String[arrayAsistentes.size()];
        for(int i=0; i<to.length; i++){
            to[i] = arrayAsistentes.get(i).getCorreo()+",";
        }
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Minuta del día: "+fecha+" SHIMACO");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Buen día, adjunto la información de la reunión realizada en: "+lugar+"\nConvocada por: "+convoco+"\nEl día: "+fecha+"\nPara que cuenten con la información acordada.");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uriFile);
        startActivity(Intent.createChooser(emailIntent , "SELECCIONA EL SERVIDOR DE CORREO ELECTRÓNICO."));
        this.finish();
        //Toast.makeText(this, "¡Minuta generada con éxito!", Toast.LENGTH_SHORT).show();
    }

    //Crea el fichero para el pedf
    public File crearFichero(String nombreFichero) {
        File ruta = getRuta();

        File fichero = null;
        if (ruta != null) {
            fichero = new File(ruta, nombreFichero);
        }

        return fichero;
    }

    //Obtiene la ruta para guardar el documento el en dispositivo
    public File getRuta() {
        File ruta = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Minutas_IVRA");

            if (ruta != null) {
                if (!ruta.mkdirs()) {
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }

        }
        return ruta;
    }

    //Método para deshabilitar los botones
    public void desableButtons(){
        Toast.makeText(contexto, "Realizando la acción, por favor espere.", Toast.LENGTH_SHORT).show();
        btnMinutaRegistroAceptar.setEnabled(false);
        btnMinutaRegistroRegresar.setEnabled(false);
        btnMinutaRegistroCancelar.setEnabled(false);
        btnMinutaRegistroPicker.setEnabled(false);
        btnMinutaRegistrarAcuerdo.setEnabled(false);
        btnMinutaEliminarAcuerdo.setEnabled(false);
        btnMinutaRegistrarTema.setEnabled(false);
        btnMinutaEliminarTema.setEnabled(false);
        btnMinutaRegistrarAsistente.setEnabled(false);
        btnMinutaRegistrarAsistente.setEnabled(false);

    }

    //Método para habilitar los botones
    public void enableButtons(){
        btnMinutaRegistroAceptar.setEnabled(true);
        btnMinutaRegistroRegresar.setEnabled(true);
        btnMinutaRegistroCancelar.setEnabled(true);
        btnMinutaRegistroPicker.setEnabled(true);
        btnMinutaRegistrarAcuerdo.setEnabled(true);
        btnMinutaEliminarAcuerdo.setEnabled(true);
        btnMinutaRegistrarTema.setEnabled(true);
        btnMinutaEliminarTema.setEnabled(true);
        btnMinutaRegistrarAsistente.setEnabled(true);
        btnMinutaRegistrarAsistente.setEnabled(true);
    }

    //Adapters
    //Adapter asistente
    public class AdapterAsistente extends RecyclerView.Adapter<AdapterAsistente.AdapterAsistenteHolder>{
        @NonNull
        @Override
        public AdapterAsistenteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterAsistenteHolder(getLayoutInflater().inflate(R.layout.minuta_a_reunion_asistente_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterAsistenteHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            arrayResponsables = arrayAsistentes;
            return arrayAsistentes.size();
        }

        class AdapterAsistenteHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            ImageButton btnMinutaRegistroAsistenteItemQuitar;
            TextView txtMinutaRegistroAsistenteItemAsistente, txtMinutaRegistroAsistenteItemCorreo;
            ImageView imgMinutaRegistroAsistenteItemAsistencia;
            CardView cardViewMinutaRegistroAsistentesItem;
            LinearLayout linearMinutaRegistroAsistentesItem;
            public AdapterAsistenteHolder(@NonNull View itemView){
                super(itemView);
                btnMinutaRegistroAsistenteItemQuitar = itemView.findViewById(R.id.btnMinutaRegistroAsistenteItemQuitar);
                //TextViews
                txtMinutaRegistroAsistenteItemAsistente = itemView.findViewById(R.id.txtMinutaRegistroAsistenteItemAsistente);
                txtMinutaRegistroAsistenteItemCorreo = itemView.findViewById(R.id.txtMinutaRegistroAsistenteItemCorreo);
                imgMinutaRegistroAsistenteItemAsistencia = itemView.findViewById(R.id.imgMinutaRegistroAsistenteItemAsistencia);
                cardViewMinutaRegistroAsistentesItem = itemView.findViewById(R.id.cardViewMinutaRegistroAsistentesItem);
                linearMinutaRegistroAsistentesItem = itemView.findViewById(R.id.linearMinutaRegistroAsistentesItem);

                cardViewMinutaRegistroAsistentesItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(arrayAsistentes.get(getAdapterPosition()).getIsSelected() == 0){
                            arrayAsistentes.get(getAdapterPosition()).setIsSelected(1);
                        }else{
                            arrayAsistentes.get(getAdapterPosition()).setIsSelected(0);
                        }
                        adapterAsistente.notifyDataSetChanged();
                        return false;
                    }
                });

                btnMinutaRegistroAsistenteItemQuitar.setOnClickListener(view -> {
                    arrayAsistentes.remove(getAdapterPosition());
                    adapterAsistente.notifyDataSetChanged();
                });
            }

            //Mostrar la información
            public void printAdapter(int position){
                if(arrayAsistentes.get(position).getIsSelected() == 0){
                    linearMinutaRegistroAsistentesItem.setBackgroundColor(Color.TRANSPARENT);
                }else{
                    linearMinutaRegistroAsistentesItem.setBackgroundColor(Color.parseColor("#FF9A9A"));
                }
                txtMinutaRegistroAsistenteItemAsistente.setText(arrayAsistentes.get(position).getNombre());
                txtMinutaRegistroAsistenteItemCorreo.setText(arrayAsistentes.get(position).getCorreo());
                if(arrayAsistentes.get(position).getAsistencia() == 0){
                    imgMinutaRegistroAsistenteItemAsistencia.setImageResource(R.drawable.snakerojo);
                }else{
                    imgMinutaRegistroAsistenteItemAsistencia.setImageResource(R.drawable.correcto);
                }
            }

            @Override
            public void onClick(View view){
                //  No hacer nada
            }
        }
    }

    //AdapterTema
    public class AdapterTema extends RecyclerView.Adapter<AdapterTema.AdapterTemaHolder>{
        @NonNull
        @Override
        public AdapterTemaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterTemaHolder(getLayoutInflater().inflate(R.layout.minuta_a_reunion_tema_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterTemaHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return arrayTemas.size();
        }

        class AdapterTemaHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView lblTemaMinuta, lblTiempoEstimadoMinuta;
            ImageButton btnTemaQuitar;
            CardView cardViewTemas;
            LinearLayout linearLayoutTemas;
            public AdapterTemaHolder(@NonNull View itemView){
                super(itemView);
                btnTemaQuitar = itemView.findViewById(R.id.btnTemaQuitar);
                lblTemaMinuta = itemView.findViewById(R.id.lblTemaMinuta);
                lblTiempoEstimadoMinuta = itemView.findViewById(R.id.lblTiempoEstimadoMinuta);
                cardViewTemas = itemView.findViewById(R.id.cardviewTemas);
                linearLayoutTemas = itemView.findViewById(R.id.linearTemas);
                //Acciones on click
                cardViewTemas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openDialogEditarTema(arrayTemas.get(getAdapterPosition()), getAdapterPosition());
                    }
                });
                //LongClick
                cardViewTemas.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(arrayTemas.get(getAdapterPosition()).getIsSelected() == 0){
                            arrayTemas.get(getAdapterPosition()).setIsSelected(1);
                        }else{
                            arrayTemas.get(getAdapterPosition()).setIsSelected(0);
                        }
                        adapterTema.notifyDataSetChanged();
                        return false;
                    }
                });

                btnTemaQuitar.setOnClickListener(view -> {
                    arrayTemas.remove(getAdapterPosition());
                    adapterTema.notifyDataSetChanged();
                });
            }

            public void printAdapter(int position){
                lblTemaMinuta.setText(arrayTemas.get(position).getTema());
                if(!arrayTemas.get(position).getTiempoEstimado().equals("")){
                    lblTiempoEstimadoMinuta.setText(arrayTemas.get(position).getTiempoEstimado());
                }else{
                    lblTiempoEstimadoMinuta.setText("");
                    lblTiempoEstimadoMinuta.setHint("N/D");
                }
                if(arrayTemas.get(position).getIsSelected() == 0){
                    linearLayoutTemas.setBackgroundColor(Color.TRANSPARENT);
                }else{
                    linearLayoutTemas.setBackgroundColor(Color.parseColor("#FF9A9A"));
                }
            }

            @Override
            public void onClick(View v){
                //No hacer nada
            }
        }
    }

    //Adapter acuerdo
    public class AdapterAcuerdo extends RecyclerView.Adapter<AdapterAcuerdo.AdapterAcuerdoHolder>{
        @NonNull
        @Override
        public AdapterAcuerdoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterAcuerdoHolder(getLayoutInflater().inflate(R.layout.minuta_a_reunion_acuerdo_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterAcuerdoHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return arrayAcuerdos.size();
        }

        class AdapterAcuerdoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            //TextViews
            ImageButton btnMinutaRegistroAcuerdoQuitar;
            TextView lblMinutaRegistroAcuerdoAcuerdo, lblMinutaRegistroAcuerdoResponsable, lblMinutaRegistroAcuerdoFechaComp;
            CardView cardViewMinutaRegistroAcuerdos;
            LinearLayout linearMinutaRegistroAcuerdosItem;
            public AdapterAcuerdoHolder(@NonNull View itemView){
                super(itemView);
                //Referenciar elementos
                btnMinutaRegistroAcuerdoQuitar = itemView.findViewById(R.id.btnMinutaRegistroAcuerdoQuitar);
                //TextViews
                lblMinutaRegistroAcuerdoAcuerdo = itemView.findViewById(R.id.lblMinutaRegistroAcuerdoAcuerdo);
                lblMinutaRegistroAcuerdoResponsable = itemView.findViewById(R.id.lblMinutaRegistroAcuerdoResponsable);
                lblMinutaRegistroAcuerdoFechaComp = itemView.findViewById(R.id.lblMinutaRegistroAcuerdoFechaComp);
                linearMinutaRegistroAcuerdosItem = itemView.findViewById(R.id.linearMinutaRegistroAcuerdosItem);
                cardViewMinutaRegistroAcuerdos = itemView.findViewById(R.id.cardViewMinutaRegistroAcuerdos);

                cardViewMinutaRegistroAcuerdos.setOnClickListener(view -> {
                    openDialogEditarAcuerdo(arrayAcuerdos.get(getAdapterPosition()), getAdapterPosition());
                });

                cardViewMinutaRegistroAcuerdos.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(arrayAcuerdos.get(getAdapterPosition()).getIsSelected() == 0){
                            arrayAcuerdos.get(getAdapterPosition()).setIsSelected(1);
                        }else{
                            arrayAcuerdos.get(getAdapterPosition()).setIsSelected(0);
                        }
                        adapterAcuerdo.notifyDataSetChanged();
                        return false;
                    }
                });

                btnMinutaRegistroAcuerdoQuitar.setOnClickListener(view -> {
                    arrayAcuerdos.remove(getAdapterPosition());
                    adapterAcuerdo.notifyDataSetChanged();
                });
            }

            public void printAdapter(int position){
                if(arrayAcuerdos.get(getAdapterPosition()).getIsSelected() == 0){
                    linearMinutaRegistroAcuerdosItem.setBackgroundColor(Color.TRANSPARENT);
                }else{
                    linearMinutaRegistroAcuerdosItem.setBackgroundColor(Color.parseColor("#FF9A9A"));
                }
                lblMinutaRegistroAcuerdoAcuerdo.setText(arrayAcuerdos.get(position).getAcuerdo());
                lblMinutaRegistroAcuerdoResponsable.setText(arrayAcuerdos.get(position).getEmpleado());
                lblMinutaRegistroAcuerdoFechaComp.setText(arrayAcuerdos.get(position).getFechaCompromiso());
            }

            @Override
            public void onClick(@NonNull View view){
                //No hace nada
            }
        }
    }
    public int getSonido(){
        SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        int sound = preferences.getInt("sonido",1);
        return sound;
    }
}