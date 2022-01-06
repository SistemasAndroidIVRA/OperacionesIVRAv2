package com.example.operacionesivra.Minuta.Vistas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.operacionesivra.Minuta.Modelos.ModeloReunion;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MinutaConsultarMinutas extends AppCompatActivity {
    public int loadingMinutaConsulta = 0;
    //Objeto de conexión
    Conexion con;
    //Contexto
    Context contexto = this;
    //Estados de los expandir
    int estadoFiltros = 0;
    //ArrayList reuniones
    ArrayList<ModeloReunion> arrayReuniones = new ArrayList<ModeloReunion>();
    ArrayList<ModeloReunion> arrayReunionesTemp = new ArrayList<ModeloReunion>();
    AdapterReunion adapterReunion = new AdapterReunion();
    //RecyclerView
    RecyclerView recyclerMinutasItem;
    //Linear layous expandir
    LinearLayout linearMinutaConsultarShowFiltros;
    //ImageButton
    ImageButton btnMinutaConsDateRange;
    //Buttons
    Button btnMinutaConsRegresar;
    //Buttons expandir
    Button btnMinutaConsultarShowFiltros;
    //EditText
    EditText txtMinutaConsDateRange, txtMinutaConsFiltrador;
    //TextViews
    TextView lblMinutaConsRegistros;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minuta_a_reunion_consultar_minutas);
        //RecyclerView
        recyclerMinutasItem = (RecyclerView) findViewById(R.id.recyclerMinutasItem);
        //Referenciar elementos
        //Linears expandir
        linearMinutaConsultarShowFiltros = findViewById(R.id.linearMinutaConsultarShowFiltros);
        //ImageButton
        btnMinutaConsDateRange = (ImageButton) findViewById(R.id.btnMinutaConsRangeDate);
        //Buttons
        btnMinutaConsRegresar = (Button) findViewById(R.id.btnMinutaConsRegresar);
        //Buttons expandir
        btnMinutaConsultarShowFiltros = findViewById(R.id.btnMinutaConsultarShowFiltros);
        //EditText
        txtMinutaConsDateRange = (EditText) findViewById(R.id.txtMinutaConsDateRange);
        txtMinutaConsDateRange.setEnabled(false);
        txtMinutaConsFiltrador = (EditText) findViewById(R.id.txtMinutaConsFiltrador);
        //TextViews
        lblMinutaConsRegistros = (TextView) findViewById(R.id.lblMinutaConsRegistros);

        //Acciones botones
        //botones expandir
        btnMinutaConsultarShowFiltros.setOnClickListener(view -> {
            expandirFiltros();
        });
        //Botón regresar
        btnMinutaConsRegresar.setOnClickListener(view -> {
            //Finalizar activity
            finish();
        });
        //Botón del rango de fechas
        btnMinutaConsDateRange.setOnClickListener(view -> {
            //Iniciar los dialogos de fechas
            getMinutasDateRange();
        });

        //Acción keyListener txt
        txtMinutaConsFiltrador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getMinutasKeyListener(txtMinutaConsFiltrador.getText().toString().trim().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        loadingMinutaConsulta = 1;
        loadinglauncher();

    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    public void expandirFiltros(){
        if(estadoFiltros == 0){
            linearMinutaConsultarShowFiltros.setVisibility(View.VISIBLE);
            linearMinutaConsultarShowFiltros.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.linear_animation));
            btnMinutaConsultarShowFiltros.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icono_desplegado,0);
            estadoFiltros = 1;
        }else{
            linearMinutaConsultarShowFiltros.setVisibility(View.GONE);
            btnMinutaConsultarShowFiltros.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icono_desplegar,0);
            estadoFiltros = 0;
        }
    }

    //Método que trae las minutas existentes
    //No recibe ningún parametro
    //Retorna un array
    public ArrayList<ModeloReunion> getMinutas(){
        ArrayList<ModeloReunion> array = new ArrayList<ModeloReunion>();
        try {
            con = new Conexion(contexto);
            //Agregar loading
            PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_Minutas_SELECT");
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                array.add(new ModeloReunion(r.getInt("reunionID"), r.getString("nombreCompleto"), r.getString("lugar"), r.getString("fecha"), r.getString("horaInicio"), r.getString("horaFin")));
            }
            if(array.size()<=0){
                Toast.makeText(contexto, "No se encontraron registros.", Toast.LENGTH_SHORT).show();
            }
            arrayReunionesTemp = array;
        }catch (Exception e){
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Error!")
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("Ocurrió un error: "+e.getMessage()+"\nConsultelo con el administrador del sistema.")
                    .setCancelable(false)
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    })
                    .show();
        }
        return array;
    }

    //Método que trae minutas por rango de fechas
    //Recibe cómo parametro String dateRange
    //Retorna ArrayList<Reunion>
    public void getMinutasPorFecha(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String dateRange = txtMinutaConsDateRange.getText().toString();
                if(dateRange.equals("")){
                    Toast.makeText(contexto, "No se han selecciodo fechas aún, inténtelo nuevamente.", Toast.LENGTH_LONG).show();
                }
                ArrayList<ModeloReunion> array = new ArrayList<ModeloReunion>();
                con = new Conexion(contexto);
                try {
                    PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_Minutas_DateRange_SELECT ?,?");
                    stmt.setString(1, dateRange.split("/")[0].toString().trim());
                    stmt.setString(2, dateRange.split("/")[1].toString().trim());
                    ResultSet r = stmt.executeQuery();
                    while(r.next()){
                        array.add(new ModeloReunion(r.getInt("reunionID"), r.getString("nombreCompleto"), r.getString("lugar"), r.getString("fecha"), r.getString("horaInicio"), r.getString("horaFin")));
                    }
                    if(array.size() == 0){
                        new MaterialAlertDialogBuilder(contexto)
                                .setTitle("¡Información!")
                                .setIcon(R.drawable.confirmacion)
                                .setMessage("No se han encontrado registros con las fechas especificadas, intentelo nuevamente.")
                                .setCancelable(false)
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //No hacer nada
                                    }
                                })
                                .show();
                    }else{
                        Toast.makeText(contexto, "¡Resultados encontrados!"+array.size(), Toast.LENGTH_SHORT).show();
                        arrayReunionesTemp = array;
                        arrayReuniones = array;
                        adapterReunion.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    txtMinutaConsFiltrador.setText("");
                    txtMinutaConsFiltrador.setHint("Rango de fechas");
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Error!")
                            .setIcon(R.drawable.snakerojo)
                            .setMessage("Ocurrió un error: "+e.getMessage()+"\nConsultelo con el administrador del sistema.")
                            .setCancelable(false)
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //No hacer nada
                                }
                            })
                            .show();
                }
            }
        });
    }

    public void getMinutasKeyListener(String cadena){
        ArrayList<ModeloReunion> array = new ArrayList<ModeloReunion>();
        if(cadena.equals("")){
            Toast.makeText(contexto, "No hay nada que buscar, mostrando registros precargados.", Toast.LENGTH_SHORT).show();
            arrayReuniones = arrayReunionesTemp;
            adapterReunion.notifyDataSetChanged();
        }else{
            for(int i=0; i<arrayReunionesTemp.size()-1;i++){
                if(arrayReunionesTemp.get(i).getEmpleado().toLowerCase().contains(cadena) || arrayReunionesTemp.get(i).getLugar().toLowerCase().contains(cadena) || arrayReunionesTemp.get(i).getFecha().contains(cadena)){
                    array.add(arrayReunionesTemp.get(i));
                }
            }
            arrayReuniones = array;
            adapterReunion.notifyDataSetChanged();
        }
    }

    //Método para llenar el recycle
    //Recibe RecyclerView recyclerView, AdapterReunion adapter
    public void fillRecyclerMinutas(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrayReuniones = getMinutas();
                recyclerMinutasItem.setLayoutManager(new LinearLayoutManager(contexto));
                recyclerMinutasItem.setAdapter(adapterReunion);
            }
            });
    }

    //Método para iniciar los date picker y traer las fechas
    public void getMinutasDateRange(){
        //Declaramos la fecha
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //Variables para componer la fecha
        int mYear = Integer.parseInt(date.substring(0,4));
        int mMonth = Integer.parseInt(date.substring(5,7));
        int mDay = Integer.parseInt(date.substring(8,10));
        //Declaramos objeto de dialogo picker
        DatePickerDialog mDatePicker;
        //Iniciamos el dialogo e indicamos el contexto donde se mostrará
        mDatePicker = new DatePickerDialog(contexto, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                //Variable para guardar la fecha señeccionada
                String fechapicker;
                selectedmonth = selectedmonth+1;
                //Guardamos la fecha
                if(selectedmonth<10){
                    fechapicker="" + selectedyear + "-0" + selectedmonth + "-" + selectedday;
                }
                else {
                    fechapicker="" + selectedyear + "-" + selectedmonth + "-" + selectedday;
                }
                //Mostramos la fecha
                txtMinutaConsDateRange.setText(""+fechapicker);
                if(!txtMinutaConsDateRange.getText().toString().equals("")){
                    //Declaramos la fecha
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    //Variables para componer la fecha
                    int mYear = Integer.parseInt(date.substring(0,4));
                    int mMonth = Integer.parseInt(date.substring(5,7));
                    int mDay = Integer.parseInt(date.substring(8,10));
                    //Declaramos objeto de dialogo picker
                    DatePickerDialog mDatePicker2;
                    //Iniciamos el dialogo e indicamos el contexto donde se mostrará
                    mDatePicker2 = new DatePickerDialog(contexto, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            //Variable para guardar la fecha señeccionada
                            String fechapicker;
                            selectedmonth = selectedmonth+1;
                            //Guardamos la fecha
                            if(selectedmonth<10){
                                fechapicker="" + selectedyear + "-0" + selectedmonth + "-" + selectedday;
                            }
                            else {
                                fechapicker="" + selectedyear + "-" + selectedmonth + "-" + selectedday;
                            }
                            //Mostramos la fecha
                            txtMinutaConsDateRange.setText(txtMinutaConsDateRange.getText().toString()+" / "+fechapicker);
                            //Invocar al método para traer las minutas
                            loadingMinutaConsulta = 2;
                            loadinglauncher();

                        }
                    }, mYear, mMonth- 1, mDay);
                    mDatePicker2.setCancelable(false);
                    mDatePicker2.setTitle("Seleccionar fecha fin");
                    mDatePicker2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            txtMinutaConsDateRange.setText("");
                            txtMinutaConsDateRange.setHint("Rango de fechas");
                            Toast.makeText(contexto, "No se seleccionaron fechas.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mDatePicker2.show();
                }
            }
        }, mYear, mMonth- 1, mDay);
        mDatePicker.setCancelable(false);
        mDatePicker.setTitle("Seleccionar fecha inicio");
        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                txtMinutaConsDateRange.setText("");
                txtMinutaConsDateRange.setHint("Rango de fechas");
                Toast.makeText(contexto, "No se seleccionaron fechas.", Toast.LENGTH_SHORT).show();
            }
        });
        mDatePicker.show();
    }

    //Adapter minutas
    public class AdapterReunion extends RecyclerView.Adapter<AdapterReunion.AdapterReunionHolder>{
        @NonNull
        @Override
        public AdapterReunionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AdapterReunionHolder(getLayoutInflater().inflate(R.layout.minuta_a_reunion_consultar_minutas_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterReunionHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            lblMinutaConsRegistros.setText(""+arrayReuniones.size());
            return arrayReuniones.size();
        }

        class AdapterReunionHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            //Elementos TextView
            TextView lblMinNumItem, lblMinNombreItem, lblMinLugarItem, lblMinFechaItem, lblMinHInicioItem, lblMinHFinItem;
            //Cardview
            CardView cardviewMinutas;
            public AdapterReunionHolder(@NonNull View itemView){
                super(itemView);
                //Referenciar elementos
                lblMinNumItem = (TextView) itemView.findViewById(R.id.lblMinNumItem);
                lblMinNombreItem = (TextView) itemView.findViewById(R.id.lblMinNombreItem);
                lblMinLugarItem = (TextView) itemView.findViewById(R.id.lblMinLugarItem);
                lblMinFechaItem = (TextView) itemView.findViewById(R.id.lblMinFechatem);
                lblMinHInicioItem = (TextView) itemView.findViewById(R.id.lblMinHInicioItem);
                lblMinHFinItem = (TextView) itemView.findViewById(R.id.lblMinHFinItem);
                //CardView
                cardviewMinutas = (CardView) itemView.findViewById(R.id.cardviewMinutas);

                //Click listener cardview
                cardviewMinutas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialAlertDialogBuilder(contexto)
                                .setTitle("¿Ver información de la minuta?")
                                .setIcon(R.drawable.confirmacion)
                                .setCancelable(false)
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //No hacer nada
                                    }
                                })
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Mandar ID
                                        Intent intent = new Intent(contexto, MinutaReunionDetalle.class);
                                        intent.putExtra("reunionID", arrayReuniones.get(getAdapterPosition()).getReunionID());
                                        intent.putExtra("Elaboro", arrayReuniones.get(getAdapterPosition()).getEmpleado());
                                        intent.putExtra("Lugar", arrayReuniones.get(getAdapterPosition()).getLugar());
                                        intent.putExtra("Fecha", arrayReuniones.get(getAdapterPosition()).getFecha());
                                        intent.putExtra("HoraI", arrayReuniones.get(getAdapterPosition()).getHoraInicio());
                                        intent.putExtra("HoraF", arrayReuniones.get(getAdapterPosition()).getHoraFin());
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                });
            }

            public void printAdapter(int position){
                lblMinNumItem.setText(""+arrayReuniones.get(position).getReunionID());
                lblMinNombreItem.setText(arrayReuniones.get(position).getEmpleado());
                lblMinLugarItem.setText(arrayReuniones.get(position).getLugar());
                lblMinFechaItem.setText(arrayReuniones.get(position).getFecha());
                lblMinHInicioItem.setText(arrayReuniones.get(position).getHoraInicio());
                lblMinHFinItem.setText(arrayReuniones.get(position).getHoraFin());
                //Agregar Size
            }

            //Método del onClick
            @Override
            public void onClick(View view){
            }
        }
    }
}