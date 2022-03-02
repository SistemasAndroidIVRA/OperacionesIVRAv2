package com.example.operacionesivra.Vistas.Chequeo.ListadePedidos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.operacionesivra.Vistas.Reportes.Chequeo.ListaChequeoTerminado;
import com.example.operacionesivra.Vistas.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ListadeChequeo extends AppCompatActivity {
    Conexion conexionService = new Conexion(this);
    public RecyclerView recycerpedidos;
    public AdapterChequeo adaptador;
    List<ModeloListaChequeo> pedidosachecar = new ArrayList<>();
    public int loadingListaChequeo = 0;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chequeo_activity_listade_pedidosa_checar);
        recycerpedidos = findViewById(R.id.recyclerchequeo);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        context = this;
        loadingListaChequeo = 1;
        loadinglauncher();

        findViewById(R.id.reportes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(context, ListaChequeoTerminado.class);
                startActivity(a);
                finish();
            }
        });

    }

    //carga los datos en un hilo diferente
    public void cargardatosbackgroud() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adaptador = new AdapterChequeo(obtenerpedidosdbImplementacion());
                recycerpedidos.setAdapter(adaptador);
            }
        });
    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    //Obtiene los pedidos que ya han sido vendidos para su chequeo
    public List<ModeloListaChequeo> obtenerpedidosdbImplementacion() {
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PedidosParaChequeo 5");
            while (r.next()) {
                pedidosachecar.add(new ModeloListaChequeo(r.getString("Numero")
                        , r.getString("Cliente"), r.getString("Referencia")
                        , r.getString("Ruta"), r.getString("Fechalimite"), "Pendiente", r.getString("Serie")));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return pedidosachecar;
    }


}