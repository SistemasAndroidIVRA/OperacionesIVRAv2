package com.example.operacionesivra.Reportes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.operacionesivra.Reportes.Inventario.InventarioActual.InventarioActual;
import com.example.operacionesivra.Reportes.Chequeo.ListaChequeoTerminado;
import com.example.operacionesivra.Reportes.Encuesta.EncuestadeSatisfacionReporte;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Reportes.Inventario.InventariosCerrados.ReporteInventariosCerrados;
import com.example.operacionesivra.Reportes.Inventario.ReportesInventarioGeneral;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SelectordeReportes extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportes_reportes);
        context = this;

        findViewById(R.id.chequeoReportes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chequeoterminado();
            }
        });
        findViewById(R.id.inventarioreportes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inventarioterminado();
            }
        });

        findViewById(R.id.encuestareportes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encuestaterminada();
            }
        });

        findViewById(R.id.inventariogeneralreportes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inventariogeneral();
            }
        });

        findViewById(R.id.inventarioactualreporte).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inventarioactual();
            }
        });
    }

    public void chequeoterminado(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Chequeo")
                .setMessage("¿Ver los chequeos terminados?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, ListaChequeoTerminado.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(true)
                .show();
    }

    public void inventarioterminado(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Inventario")
                .setMessage("¿Ver los inventarios terminados?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, ReporteInventariosCerrados.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(true)
                .show();
    }

    public void encuestaterminada(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Encuesta")
                .setMessage("¿Ver los datos de la encuesta?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, EncuestadeSatisfacionReporte.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(true)
                .show();
    }

    public void inventariogeneral(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Inventario general")
                .setMessage("¿Ver los datos recabados por el equipo de inventarios?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, ReportesInventarioGeneral.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(true)
                .show();
    }

    public void inventarioactual(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Inventario actual")
                .setMessage("¿Ver inventario en tiempo real?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, InventarioActual.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(true)
                .show();
    }


}