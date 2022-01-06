package com.example.operacionesivra.Inventario.InventariosHistoricos.DetallesHistoricos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.operacionesivra.Inventario.InventariosHistoricos.Inventario_InventariosHistoricos;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Detalles_Historicos extends AppCompatActivity {
    Conexion conexion = new Conexion(this);

    private String folio;
    TextView lblRegistrosTotalesHisDetalles, lblFechaDH, lblAlmacenDH, lblMaterialDH, lblUnidadMedidaDH, lblSistemaDH, lblTotalRegistradoDH;
    Button btnAtrasDH;

    private RecyclerView recyclerDetallesHistoricos;
    private AdapterDetallesHistoricos adapterDeHistoricos;
    List<ModeloDetallesHistoricoItem> detallesHistoricos = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles_historicos);
        //Traemos folio
        lblRegistrosTotalesHisDetalles = (TextView) findViewById(R.id.lblRegistrosTotalesHistDetalles);

        lblFechaDH = (TextView) findViewById(R.id.lblFechaDH);
        lblAlmacenDH = (TextView) findViewById(R.id.lblAlmacenDH);
        lblMaterialDH = (TextView) findViewById(R.id.lblMaterialDH);
        lblUnidadMedidaDH = (TextView) findViewById(R.id.lblUnidadMedidaDH);
        lblSistemaDH = (TextView) findViewById(R.id.lblSistemaDH);
        lblTotalRegistradoDH = (TextView) findViewById(R.id.lblTotalRegistradoDH);
        btnAtrasDH = (Button) findViewById(R.id.btnAtrasDH);


        folio = getIntent().getStringExtra("Folio");
        //lblDetalleHistorico.setText(""+folio);

        recyclerDetallesHistoricos = findViewById(R.id.rexyclerDetallesHistoricos);
        recyclerDetallesHistoricos.setLayoutManager(new LinearLayoutManager(this));
        //Comprobamos que haya información en la lista
        //comprobarLista();
        cargarDatosDetallesHistoricos();
        llenarInfo();

        btnAtrasDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void cargarDatosDetallesHistoricos(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterDeHistoricos = new AdapterDetallesHistoricos(getDetallesHistoricos());
                recyclerDetallesHistoricos.setAdapter(adapterDeHistoricos);
            }
        });
    }

    public void llenarInfo(){
        Conexion conexion = new Conexion(this);
        try {
            Statement stmt = conexion.conexiondbImplementacion().createStatement();
            String query = "SELECT CONVERT(VARCHAR, horainicio, 108) as horainicio, CONVERT(VARCHAR, horafin, 108) as horafin, Fecha as fecha, Almacen as almacen, Material as material, StockTotal as sistema, SUM(Total_registrado) as total FROM Movil_Reporte WHERE Folio = '"+folio+"' AND Historico = 1 GROUP BY Fecha, Almacen, Material, StockTotal, horainicio, horafin;";
            ResultSet r = stmt.executeQuery(query);
            while(r.next()){
                lblFechaDH.setText("Fecha: "+r.getString("fecha")+", "+r.getString("horainicio")+" - "+r.getString("horafin"));
                lblAlmacenDH.setText(r.getString("almacen"));
                lblMaterialDH.setText(r.getString("material"));
                lblUnidadMedidaDH.setText("METRO LINE");
                lblSistemaDH.setText(r.getString("sistema"));
                lblTotalRegistradoDH.setText(r.getString("total"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<ModeloDetallesHistoricoItem> getDetallesHistoricos(){
        try {
            Statement statement = conexion.conexiondbImplementacion().createStatement();
            String query = "SELECT Codigo_unico, Cantidad, Ubicacion, Total_registrado, Observaciones FROM Movil_Reporte WHERE Folio = '"+folio+"' AND historico = 1";
            ResultSet r = statement.executeQuery(query);
            //Recorremos nuestro resultset si es que trae información
            while(r.next()){
                detallesHistoricos.add(new ModeloDetallesHistoricoItem(r.getString("Codigo_unico"), r.getString("Cantidad"),
                        r.getString("Ubicacion"), r.getString("Total_registrado"), r.getString("Observaciones")));
            }
        } catch (SQLException throwables) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage(throwables.getMessage())
                    .show();
        }
        lblRegistrosTotalesHisDetalles.setText(""+detallesHistoricos.size());
        return detallesHistoricos;
    }

    //Método para comprobar si hay registros en la lista
    public void comprobarLista(){
        try {
            cargarDatosDetallesHistoricos();
        }catch (Exception e){
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Error")
                    .setMessage(e.getMessage()+"")
                    .show();
        }
        if(detallesHistoricos.size() <= 0){
            new MaterialAlertDialogBuilder(this)
                    .setTitle("¡Error!")
                    .setIcon(R.drawable.snakerojo)
                    .setCancelable(false)
                    .setMessage("No hay detalles de este inventario, se debe a un " +
                            "error de carga de información, por favor consultelo con el administrador de sistemas\n")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getBaseContext(), Inventario_InventariosHistoricos.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }
}