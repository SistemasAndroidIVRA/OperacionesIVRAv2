package com.example.operacionesivra.Picking.ListapedidosPicking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.operacionesivra.MainActivity.MainActivity;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Picking extends AppCompatActivity {
    private RecyclerView recycerpedidos;
    private AdapterListaPedidos adaptador;
    List<ModeloListaPedido> pedidos = new ArrayList<>();
    public int loadingpicking=0;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picking_activity_picking);
        context = this;
        recycerpedidos = findViewById(R.id.listapedidosview);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        loadingpicking=1;
        loadinglauncher();
    }

    /*--------------------------------------Funciones---------------------------------------------*/

    //Crea una lista que almacena los datos de la base de manera automatica (Implementacion)
    public List<ModeloListaPedido> obtenerpedidosdbImplementacion() {
        Conexion conexion = new Conexion(this);
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        String idTemporal = null;
        try {
            Statement qu = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PedidosPorSurtir");
            while (r.next()) {
                if (!r.getString(1).equals(idTemporal)) {
                    pedidos.add(new ModeloListaPedido(r.getString(5), r.getString(4), r.getString(3), r.getString(1), r.getString(7), r.getString(6), r.getString(2)));
                }
                idTemporal = r.getString(1);
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Lo sentimos, error:\n"+e.toString()+"\nPor favor, reporte la falla con el area de sistemas")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    })
                    .show();
        }
        return pedidos;
    }

    public void cargardatos(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adaptador = new AdapterListaPedidos(obtenerpedidosdbImplementacion());
                recycerpedidos.setAdapter(adaptador);
            }
        });
    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    /*--------------------------------------Botones---------------------------------------------*/


    public void recargar(View vista) {
        obtenerpedidosdbImplementacion();
    }
    public void atras(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
