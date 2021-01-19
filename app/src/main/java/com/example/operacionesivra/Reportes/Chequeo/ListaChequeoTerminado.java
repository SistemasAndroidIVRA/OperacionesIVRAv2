package com.example.operacionesivra.Reportes.Chequeo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

import com.example.operacionesivra.MainActivity.MainActivity;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaChequeoTerminado extends AppCompatActivity {
    Conexion conexionService = new Conexion(this);
    public RecyclerView recycerpedidos;
    public AdapterChequeoTerminado adaptador;
    List<ModeloChequeoterminado> pedidosachecar = new ArrayList<>();
    public int loadingListaDeReportes=0;
    Context context;
    String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportes_chequeo_lista_terminados);

        recycerpedidos = findViewById(R.id.recyclerregistros);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        context = this;
        escogerdia();

    }

    public void escogerdia(){
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int mYear = Integer.parseInt(date.substring(0,4));
        int mMonth = Integer.parseInt(date.substring(5,7));
        int mDay = Integer.parseInt(date.substring(8,10));
        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                System.out.println("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                selectedmonth = selectedmonth+1;
                if(selectedmonth<10){
                    fecha="" + selectedyear + "-0" + selectedmonth + "-" + selectedday;
                    loadingListaDeReportes = 1;
                    loadinglauncher();
                }
                else {
                    fecha="" + selectedyear + "-" + selectedmonth + "-" + selectedday;
                    loadingListaDeReportes = 1;
                    loadinglauncher();
                }
            }
        }, mYear, mMonth- 1, mDay);
        mDatePicker.setCancelable(false);
        mDatePicker.setTitle("Seleccione la fecha que desea consultar");
        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onBackPressed();
            }
        });
        mDatePicker.show();
    }

    public void cargardatosbackgroud(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adaptador = new AdapterChequeoTerminado(obtenerpedidosdbImplementacion(fecha));
                recycerpedidos.setAdapter(adaptador);
            }
        });
    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    public List<ModeloChequeoterminado> obtenerpedidosdbImplementacion(String fecha) {
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("select * from Movil_Registros_Chequeo where fecha='"+fecha+"'");
            while (r.next()) {
                pedidosachecar.add(new ModeloChequeoterminado(r.getString("pedido")
                        , r.getString("serie"), r.getString("cliente")
                        , r.getString("referencia"), r.getString("fecha"),  r.getString("horainicio"),r.getString("horafin")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        if(pedidosachecar.isEmpty()){
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Atencion")
                    .setMessage("No hay ningún registro con esa fecha. ¿Quiere eleguir una fecha diferente?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            escogerdia();
                        }
                    })
                    .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            terminar();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
        return pedidosachecar;
    }

    public void terminar(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmación")
                .setIcon(R.drawable.confirmacion)
                .setMessage("¿Quiere regresar al menú principal?\nLa información estará disponible en cualquier momento.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        terminar();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }
}