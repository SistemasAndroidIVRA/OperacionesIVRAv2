package com.example.operacionesivra.Inventario.InventariosHistoricos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Inventario_InventariosHistoricos extends AppCompatActivity {
    Conexion conexion = new Conexion(this);

    public TextView txtTotal;
    Button btnAtrasH;
    private RecyclerView recyclerHistoricos;
    private AdapterHistoricoInventarios adapterHistoricos;
    private EditText txtFiltroHistoricos;

    List<ModeloInventariosHistoricos> inventariosHistoricos = new ArrayList();
    List<ModeloInventariosHistoricos> historicosFiltrados = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_inventarioshistoricos);
        //Declaración de elementos
        txtTotal = findViewById(R.id.txtTotalHistoricos);
        //Filtro
        txtFiltroHistoricos = findViewById(R.id.txtFiltroHistoricos);
        //Recycler
        recyclerHistoricos = findViewById(R.id.recyclerHistoricos);
        recyclerHistoricos.setLayoutManager(new LinearLayoutManager(this));
        cargarDatosHistoricos();
        comprobarlista();

        btnAtrasH = (Button) findViewById(R.id.btnAtrasH);

        btnAtrasH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        txtFiltroHistoricos.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                historicosFiltrados.clear();
                filtranHistoricos(txtFiltroHistoricos.getText().toString());
                return false;
            }
        });
         */

        txtFiltroHistoricos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                historicosFiltrados.clear();
                filtranHistoricos(txtFiltroHistoricos.getText().toString());
            }
        });
    }


    public void filtranHistoricos(String filtro){
        for(int i=0; i<inventariosHistoricos.size(); i++){
            if(inventariosHistoricos.get(i).getFecha().contains(filtro) || inventariosHistoricos.get(i).getMaterial().contains(filtro) || inventariosHistoricos.get(i).getUsuario().contains(filtro) || inventariosHistoricos.get(i).getAlmacen().contains(filtro) ){
                historicosFiltrados.add(inventariosHistoricos.get(i));
            }
        }
        adapterHistoricos = new AdapterHistoricoInventarios(historicosFiltrados);
        recyclerHistoricos.setAdapter(adapterHistoricos);
        //adapterHistoricos = new AdapterHistoricoInventarios(historicosFiltrados);
        //recyclerHistoricos.setAdapter(adapterHistoricos);
    }
    public void cargarDatosHistoricos(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterHistoricos = new AdapterHistoricoInventarios(getHistoricos());
                recyclerHistoricos.setAdapter(adapterHistoricos);
            }
        });
    }

    public List<ModeloInventariosHistoricos> getHistoricos(){
        try {
            Statement statement = conexion.conexiondbImplementacion().createStatement();
            String query = "SELECT Folio as folio, Usuario as usuario, Fecha as fecha, Material as material, Almacen as almacen, count(Id_registro) AS entradas, SUM(Total_registrado) as totalRegistrado, ROUND((StockTotal - SUM(  Total_registrado)), 2) as diferencia, CONVERT(VARCHAR, horainicio, 108) as horainicio,CONVERT(VARCHAR, horafin, 108) as horafin \n" +
                    "FROM Movil_Reporte " +
                    "WHERE historico = 1 " +
                    "GROUP BY Folio, Fecha, Usuario, Material, Almacen, horainicio, horafin, StockTotal " +
                    "ORDER BY Fecha DESC ";
            ResultSet r = statement.executeQuery(query);
            while(r.next()){


                inventariosHistoricos.add( new ModeloInventariosHistoricos(r.getString("usuario"), r.getString("fecha"), r.getString("material"),
                        r.getString("folio"), r.getString("almacen"), r.getString("totalRegistrado"), r.getString("diferencia"),
                        r.getString("horainicio"), r.getString("horafin"), r.getString("entradas")));
            }

        }catch (Exception e){
            new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_MaterialComponents)
                    .setCancelable(false)
                    .setTitle("Error al conectar con el servidor...")
                    .setMessage("Por favor verifique que existe una conexión wi-fi y presione 'Reintentar'.\n Si esto no soluciona el problema cierre la aplicacción y reportelo en el área de desarrollo.\n" + e)

                    .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getHistoricos();
                        }
                    })
                    .setIcon(R.drawable.snakerojo)
                    .setNegativeButton("Cerrar App", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
        return inventariosHistoricos;
    }

    //Comprueba que exista algún item dentro de la lista creada

    public void comprobarlista() {
        if (inventariosHistoricos.isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Vacio")
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("Actualmente no existe ningún histórico de inventario")
                    .setPositiveButton("Recargar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getHistoricos();
                            comprobarlista();
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

}
