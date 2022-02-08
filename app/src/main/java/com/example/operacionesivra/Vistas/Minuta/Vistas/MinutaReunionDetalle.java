package com.example.operacionesivra.Vistas.Minuta.Vistas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.operacionesivra.Modelos.ModeloAcuerdo;
import com.example.operacionesivra.Modelos.ModeloAsistente;
import com.example.operacionesivra.Modelos.ModeloTema;
import com.example.operacionesivra.Vistas.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MinutaReunionDetalle extends AppCompatActivity {
    //Estados botones desplegables
    final int[] estadoFechaHora = {0};
    final int[] estadoAsistentes = {0};
    final int[] estadoTemas = {0};
    final int[] estadoAcuerdos = {0};
    //IdReunion
    int reunionID;
    //Conexion
    Conexion con;
    //Contexto
    Context contexto = this;
    //ArrayList
    ArrayList<ModeloAsistente> arrayEmpleados = new ArrayList<>();
    ArrayList<ModeloTema> arrayTemas = new ArrayList<>();
    ArrayList<ModeloAcuerdo> arrayAcuerdos = new ArrayList<>();
    //Adapters
    AdapterEmpleadoDet adapterEmpleadoDet;
    AdapterTemaDet adapterTemaDet;
    AdapterAcuerdosDet adapterAcuerdosDet;
    //Recyclers
    RecyclerView recyclerAsistentesMinutaDet;
    RecyclerView recyclerTemasMinutaDet;
    RecyclerView recyclerAcuerdosMinutaDet;
    //Liners
    LinearLayout linearShowFechaHora;
    LinearLayout linearShowAsistentes;
    LinearLayout linearShowTemas;
    LinearLayout linearShowAcuerdos;
    //Buttons
    Button btnMinutaDetRegresar;
    //Buttons desplegar
    Button btnShowFechaHora, btnShowAsistentes, btnShowTemas, btnShowAcuerdos;
    //TextViews
    TextView lblMinutaDetFecha, lblMinutaDetHoraI, lblMinutaDetHoraF, lblMinutaDetElaboro, lblMinutaDetLugar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minuta_a_reunion_consultar_minutas_detalle);
        //Referenciar elementos
        //Recyclers
        recyclerAsistentesMinutaDet = (RecyclerView) findViewById(R.id.recyclerAsistentesMinutaDet);
        recyclerAsistentesMinutaDet.setLayoutManager(new LinearLayoutManager(contexto));
        recyclerTemasMinutaDet = (RecyclerView) findViewById(R.id.recyclerTemasMinutaDet);
        recyclerTemasMinutaDet.setLayoutManager(new LinearLayoutManager(contexto));
        recyclerAcuerdosMinutaDet = (RecyclerView) findViewById(R.id.recyclerAcuerdosMinutaDet);
        recyclerAcuerdosMinutaDet.setLayoutManager(new LinearLayoutManager(contexto));
        //TextViews
        lblMinutaDetElaboro = (TextView) findViewById(R.id.lblMinutaDetElaboro);
        lblMinutaDetLugar = (TextView) findViewById(R.id.lblMinutaDetLugar);
        lblMinutaDetFecha = (TextView) findViewById(R.id.lblMinutaDetFecha);
        lblMinutaDetHoraI = (TextView) findViewById(R.id.lblMinutaDetHoraI);
        lblMinutaDetHoraF = (TextView) findViewById(R.id.lblMinutaDetHoraF);
        //Buttons
        btnMinutaDetRegresar = (Button) findViewById(R.id.btnMinutaDetRegresar);
        //Buttons desplegables
        btnShowFechaHora = findViewById(R.id.btnShowFechaHora);
        btnShowAsistentes = findViewById(R.id.btnShowAsistentes);
        btnShowTemas = findViewById(R.id.btnShowTemas);
        btnShowAcuerdos = findViewById(R.id.btnShowAcuerdos);
        //Linear layouts desplegables
        linearShowFechaHora = findViewById(R.id.linearShowFechaHora);
        linearShowAsistentes = findViewById(R.id.linearShowAsistentes);
        linearShowTemas = findViewById(R.id.linearShowTemas);
        linearShowAcuerdos = findViewById(R.id.linearShowAcuerdos);
        //Iniciar Adapters
        adapterTemaDet = new AdapterTemaDet();
        adapterEmpleadoDet = new AdapterEmpleadoDet();
        adapterAcuerdosDet = new AdapterAcuerdosDet();

        //Acciones botones
        //Regresar
        btnMinutaDetRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Acciones despliegues
        btnShowFechaHora.setOnClickListener(view -> {
            clickFechaHora();
        });
        btnShowAsistentes.setOnClickListener(view -> {
            clickAsistentes();
        });
        btnShowTemas.setOnClickListener(view -> {
           clickTemas();
        });
        btnShowAcuerdos.setOnClickListener(view -> {
            clickAcuerdos();
        });

        //Iniciar info
        loadinInfo();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void loadinInfo(){
        Loading loading = new Loading(contexto);
        loading.execute();
    }

    //Método para desplegar o collapsar los asistentes
    public void clickFechaHora(){
        if(estadoFechaHora[0] == 0){
            linearShowFechaHora.setVisibility(View.VISIBLE);
            linearShowFechaHora.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.linear_animation));
            btnShowFechaHora.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icono_desplegado,0);
            estadoFechaHora[0] = 1;
        }else{
            linearShowFechaHora.setVisibility(View.GONE);
            btnShowFechaHora.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icono_desplegar,0);
            estadoFechaHora[0] = 0;
        }
    }

    //Método para desplegar o collapsar los asistentes
    public void clickAsistentes(){
        if(estadoAsistentes[0] == 0){
            linearShowAsistentes.setVisibility(View.VISIBLE);
            linearShowAsistentes.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.linear_animation));
            btnShowAsistentes.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icono_desplegado,0);
            estadoAsistentes[0] = 1;
        }else{
            linearShowAsistentes.setVisibility(View.GONE);
            linearShowAsistentes.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.linear_animation));
            btnShowAsistentes.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icono_desplegar,0);

            estadoAsistentes[0] = 0;
        }
    }

    //Método para desplegar o collapsar los temas
    public void clickTemas(){
        if(estadoTemas[0] == 0){
            linearShowTemas.setVisibility(View.VISIBLE);
            linearShowTemas.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.linear_animation));
            btnShowTemas.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icono_desplegado,0);
            estadoTemas[0] = 1;
        }else{
            linearShowTemas.setVisibility(View.GONE);
            linearShowTemas.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.linear_animation));
            btnShowTemas.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icono_desplegar,0);
            estadoTemas[0] = 0;
        }
    }

    //Método para desplegar o collapsar los acuerdos
    public void clickAcuerdos(){
        if(estadoAcuerdos[0] == 0){
            linearShowAcuerdos.setVisibility(View.VISIBLE);
            linearShowAcuerdos.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.linear_animation));
            btnShowAcuerdos.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icono_desplegado,0);
            estadoAcuerdos[0] = 1;
        }else{
            linearShowAcuerdos.setVisibility(View.GONE);
            linearShowAcuerdos.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.linear_animation));
            btnShowAcuerdos.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icono_desplegar,0);
            estadoAcuerdos[0] = 0;
        }
    }

    public void startGets(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getInfoPrincipal();
                arrayEmpleados = getAsistentes();
                recyclerAsistentesMinutaDet.setAdapter(adapterEmpleadoDet);
                btnShowAsistentes.setText("Total de asistentes registrados: "+arrayEmpleados.size());

                arrayTemas = getTemas();
                recyclerTemasMinutaDet.setAdapter(adapterTemaDet);
                btnShowTemas.setText("Total de temas registrados: "+arrayTemas.size());

                arrayAcuerdos = getAcuerdos();
                recyclerAcuerdosMinutaDet.setAdapter(adapterAcuerdosDet);
                btnShowAcuerdos.setText("Total de acuerdos registrados: "+arrayAcuerdos.size());
            }
        });
    }

    public void getInfoPrincipal(){
        reunionID = getIntent().getIntExtra("reunionID", 0);
        lblMinutaDetLugar.setText(getIntent().getStringExtra("Lugar"));
        lblMinutaDetElaboro.setText(getIntent().getStringExtra("Elaboro"));
        lblMinutaDetFecha.setText(getIntent().getStringExtra("Fecha"));
        lblMinutaDetHoraI.setText(getIntent().getStringExtra("HoraI"));
        lblMinutaDetHoraF.setText(getIntent().getStringExtra("HoraF"));
    }

    //Método para traer los asistentes
    public ArrayList<ModeloAsistente> getAsistentes(){
        ArrayList<ModeloAsistente> array = new ArrayList<>();
        con = new Conexion(contexto);
        try {
            PreparedStatement var = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_AsistentesReunion_SELECT ?");
            var.setInt(1, reunionID);
            ResultSet r = var.executeQuery();
            while(r.next()){
                //Arreglar lo del tipo
                int tipo = 1;
                array.add(new ModeloAsistente(r.getInt("personaID"), r.getString("nombreCompleto"), r.getString("correo"), r.getString("empleadoID"), r.getInt("asistencia"), 0));
            }
            return array;
        }catch (Exception e){
            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //Método para traer los temas
    public ArrayList<ModeloTema> getTemas(){
        ArrayList<ModeloTema> array = new ArrayList<>();
        con = new Conexion(contexto);
        try {
            //Preparar enunciado
            PreparedStatement var = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_Temas_SELECT ?");
            var.setInt(1, reunionID);
            //ResultSet para recorrer resultados
            ResultSet r = var.executeQuery();
            while(r.next()){
                array.add(new ModeloTema(r.getInt("temaID"), r.getString("tema"), r.getString("tiempoEstimado"), r.getInt("reunionID"), 0));
            }
            return array;
        }catch(Exception e){
            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //Método para traer los acuerdos
    public ArrayList<ModeloAcuerdo> getAcuerdos(){
        ArrayList<ModeloAcuerdo> array = new ArrayList<>();
        con = new Conexion(contexto);
        try {
            PreparedStatement var = con.conexiondbImplementacion().prepareCall("PMovil_Minuta_R_Acuerdos_SELECT ?");
            var.setInt(1, reunionID);
            ResultSet r = var.executeQuery();
            while(r.next()){
                array.add(new ModeloAcuerdo(r.getInt("acuerdoID"), r.getString("acuerdo"), r.getString("nombreCompleto"), r.getInt("personaID"), r.getString("fechaCompromiso"), r.getInt("reunionID"), 0));
            }
            return array;
        }catch (Exception e){
            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public class AdapterEmpleadoDet extends RecyclerView.Adapter<AdapterEmpleadoDet.AdapterEmpleadoDetHolder>{
        @Override
        @NonNull
        public AdapterEmpleadoDetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterEmpleadoDetHolder(getLayoutInflater().inflate(R.layout.minuta_a_reunion_asistente_item_det, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterEmpleadoDetHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return arrayEmpleados.size();
        }

        class AdapterEmpleadoDetHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            //TextViews
            TextView lblMinutaDetNombreItem, lblMinutaDetCorreoItem;
            //ImageView
            ImageView imgMinutaDetAsistenciaItem;
            public AdapterEmpleadoDetHolder(@NonNull View itemView){
                super(itemView);
                //Referenciar elementos
                //TextView
                lblMinutaDetNombreItem = (TextView) itemView.findViewById(R.id.lblMinutaDetNombreItem);
                lblMinutaDetCorreoItem = (TextView) itemView.findViewById(R.id.lblMinutaDetCorreoItem);
                //ImageView
                imgMinutaDetAsistenciaItem = (ImageView) itemView.findViewById(R.id.imgMinutaDetAsistenciaItem);
            }

            public void printAdapter(int position){
                lblMinutaDetNombreItem.setText(arrayEmpleados.get(position).getNombre());
                lblMinutaDetCorreoItem.setText(arrayEmpleados.get(position).getCorreo());
                if(arrayEmpleados.get(position).getAsistencia() == 0){
                    imgMinutaDetAsistenciaItem.setImageResource(R.drawable.snakerojo);
                }else{
                    imgMinutaDetAsistenciaItem.setImageResource(R.drawable.correcto);
                }
            }

            public void onClick(View view){
                //No hacer nada
            }
        }
    }

    public class AdapterTemaDet extends RecyclerView.Adapter<AdapterTemaDet.AdapterTemaDetHolder>{
        @Override
        @NonNull
        public AdapterTemaDetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterTemaDetHolder(getLayoutInflater().inflate(R.layout.minuta_a_reunion_tema_item_det, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterTemaDetHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return arrayTemas.size();
        }

        class AdapterTemaDetHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            //Referenciar elementos
            TextView lblMinutaDetTemaItem, lblMinutaDetTiempoEstItem;
            public AdapterTemaDetHolder(@NonNull View itemView){
                super(itemView);
                lblMinutaDetTemaItem = (TextView) itemView.findViewById(R.id.lblMinutaDetTemaItem);
                lblMinutaDetTiempoEstItem = (TextView) itemView.findViewById(R.id.lblMinutaDetTiempoEstItem);
            }

            public void printAdapter(int position){
                lblMinutaDetTemaItem.setText(arrayTemas.get(position).getTema());
                if(!arrayTemas.get(position).getTiempoEstimado().equals("")){
                    lblMinutaDetTiempoEstItem.setText(arrayTemas.get(position).getTiempoEstimado());
                }else{
                    lblMinutaDetTiempoEstItem.setText("");
                    lblMinutaDetTiempoEstItem.setHint("N/D");
                }
            }

            @Override
            public void onClick(View v){
                //No hace nada
            }

        }
    }

    public class AdapterAcuerdosDet extends RecyclerView.Adapter<AdapterAcuerdosDet.AdapterAcuerdosDetHolder>{
        @Override
        @NonNull
        public AdapterAcuerdosDetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterAcuerdosDetHolder(getLayoutInflater().inflate(R.layout.minuta_a_reunion_acuerdo_item_det, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterAcuerdosDetHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return arrayAcuerdos.size();
        }

        class AdapterAcuerdosDetHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            //TextViews
            TextView lblMinutaDetAcuerdoItem, lblMinutaDetResponsableItem, lblMinutaDetFechaCompItem;
            public AdapterAcuerdosDetHolder(@NonNull View itemView){
                super(itemView);
                //Referenciar elementos
                //TextViews
                lblMinutaDetAcuerdoItem = (TextView) itemView.findViewById(R.id.lblMinutaDetAcuerdoItem);
                lblMinutaDetResponsableItem = (TextView) itemView.findViewById(R.id.lblMinutaDetResponsableItem);
                lblMinutaDetFechaCompItem = (TextView) itemView.findViewById(R.id.lblMinutaDetFechaCompItem);
            }

            public void printAdapter(int position){
                lblMinutaDetAcuerdoItem.setText(arrayAcuerdos.get(position).getAcuerdo());
                lblMinutaDetResponsableItem.setText(arrayAcuerdos.get(position).getEmpleado());
                lblMinutaDetFechaCompItem.setText(arrayAcuerdos.get(position).getFechaCompromiso());
            }

            @Override
            public void onClick(@NonNull View view){
                Toast.makeText(contexto, "Id: "+getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}