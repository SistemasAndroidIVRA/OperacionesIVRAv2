package com.example.operacionesivra.Inventario.ConteosPausa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.operacionesivra.MainActivity.MainActivity;
import com.example.operacionesivra.Services.Conexion;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Pausa extends AppCompatActivity {
    Button atras, recargar;

    Conexion conexionService;
    Context context;
    public int loadingPausa = 0;
    //Variables del programa
    private RecyclerView recyceritems;
    private AdapterConteos_pausa adaptador;
    ArrayList<Modelo_conteos_pausa> conteosPausas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_pausa);
        context = getBaseContext();
        conexionService = new Conexion(context);
        recyceritems = findViewById(R.id.conteosrecycler);
        atras = findViewById(R.id.atras_pausa);
        recargar = findViewById(R.id.actualizar_pausa);
        recyceritems.setAdapter(adaptador);

        loadingPausa = 3;
        loadinglauncher();

        //Botones
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPausa = 1;
                loadinglauncher();
            }
        });


    }

    //Carga el proceso en un hilo distinto
    public void cargardatos() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyceritems.setLayoutManager(new LinearLayoutManager(context));
                adaptador = new AdapterConteos_pausa(crearlista());
                comprobarlista();
            }
        });
    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    //Carga los inventarios detenidos
    public ArrayList<Modelo_conteos_pausa> crearlista() {
        int contador = 1;
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("SELECT  DISTINCT Fecha,Bloqueado,Material,Folio, Usuario,StockTotal,Almacen FROM Movil_Reporte where Pausado='SI' order by(Folio)");
            while (r.next()) {
                conteosPausas.add(new Modelo_conteos_pausa(contador, r.getString(1), r.getString(2), r.getString(3), r.getString(4), r.getString(5), r.getString(6), r.getString(7)));

                contador++;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return conteosPausas;
    }

    //Comprueba que exista algún inventario detenido
    public void comprobarlista() {
        if (conteosPausas.isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Vacio")
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("Actualmente no existe algun inventario en pausa")
                    .setPositiveButton("Recargar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingPausa = 1;
                            loadinglauncher();
                        }
                    })
                    .setNegativeButton("Atras", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    public void activityMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}