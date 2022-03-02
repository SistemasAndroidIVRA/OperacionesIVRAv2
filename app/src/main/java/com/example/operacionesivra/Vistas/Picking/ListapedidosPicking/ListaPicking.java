package com.example.operacionesivra.Vistas.Picking.ListapedidosPicking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.example.operacionesivra.Vistas.PantallasCargando.Loading;
import com.example.operacionesivra.Vistas.Picking.ListapedidosPicking.BuscarNota.BuscarPedido;
import com.example.operacionesivra.Vistas.Picking.ListapedidosPicking.BuscarNota.BuscarPedidoInterface;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaPicking extends AppCompatActivity implements BuscarPedidoInterface {
    private RecyclerView recycerpedidos;
    private AdapterListaPedidos adaptador;
    List<ModeloListaPicking> pedidos = new ArrayList<>();
    public int loadingpicking = 0;
    Context context;

    String fecha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picking_activity_picking);
        context = this;
        Toolbar toolbar = findViewById(R.id.toolbarpicking);
        setSupportActionBar(toolbar);
        recycerpedidos = findViewById(R.id.listapedidosview);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        this.setTitle("Picking");
        //loadingpicking=1;
        //loadinglauncher();
    }

    /*--------------------------------------ToolBar-----------------------------------------------*/

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.piking, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.fechapiking:
                escogerdia();
                break;
            case R.id.pedidopiking:
                escogerpedido();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Lanza un dialog
    public void escogerpedido() {
        BuscarPedido buscarPedido = new BuscarPedido();
        buscarPedido.show(getSupportFragmentManager(), null);
    }

    //Lanza un datepicker
    public void escogerdia() {
        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        int mYear = Integer.parseInt(date.substring(0, 4));
        int mMonth = Integer.parseInt(date.substring(5, 7));
        int mDay = Integer.parseInt(date.substring(8, 10));
        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth = selectedmonth + 1;
                System.out.println("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                if (selectedmonth < 10) {
                    fecha = "" + selectedyear + "-0" + selectedmonth + "-" + selectedday;
                    adaptador = new AdapterListaPedidos(cargardatosfecha(fecha));
                    recycerpedidos.setAdapter(adaptador);
                    //loadingListaDeReportes = 1;
                    //loadinglauncher();
                } else {
                    fecha = "" + selectedyear + "-" + selectedmonth + "-" + selectedday;
                    adaptador = new AdapterListaPedidos(cargardatosfecha(fecha));
                    recycerpedidos.setAdapter(adaptador);
                    //loadingListaDeReportes = 1;
                    //loadinglauncher();
                }
            }
        }, mYear, mMonth - 1, mDay);
        mDatePicker.setCancelable(false);
        mDatePicker.setTitle("Seleccione la fecha que desea consultar");
        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDatePicker.show();
    }

    /*--------------------------------------Funciones---------------------------------------------*/

    //Actualiza la lista segun el dia seleccionado
    public List<ModeloListaPicking> cargardatosfecha(String fecha) {
        if (!pedidos.isEmpty()) {
            pedidos.clear();
            recycerpedidos.getAdapter().notifyDataSetChanged();
        }
        Conexion conexion = new Conexion(this);
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        String idTemporal = null;
        try {
            Statement qu = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PedidosPorSurtir_fecha '" + fecha + "'");
            while (r.next()) {
                if (!r.getString(1).equals(idTemporal)) {
                    pedidos.add(new ModeloListaPicking(r.getString("Referencia")
                            , r.getString("Cliente"), r.getString("Fecha")
                            , r.getString("Numero"), r.getString("Serie")));
                }
                idTemporal = r.getString(1);
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Lo sentimos, error:\n" + e.toString() + "\nPor favor, reporte la falla con el area de sistemas")
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

    //Actualiza la lista segun el numero de pedido
    public List<ModeloListaPicking> cargardatospedido(String pedido, String serie) {
        if (!pedidos.isEmpty()) {
            pedidos.clear();
            recycerpedidos.getAdapter().notifyDataSetChanged();
        }
        Conexion conexion = new Conexion(this);
        String idTemporal = null;
        try {
            Statement qu = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PedidosPorSurtir_pedido '" + pedido + "','" + serie + "'");
            while (r.next()) {
                if (!r.getString(1).equals(idTemporal)) {
                    pedidos.add(new ModeloListaPicking(r.getString("Referencia")
                            , r.getString("Cliente"), r.getString("Fecha")
                            , r.getString("Numero"), r.getString("Serie")));
                }
                idTemporal = r.getString(1);
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Lo sentimos, error:\n" + e.toString() + "\nPor favor, reporte la falla con el area de sistemas")
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

    //Crea una lista que almacena los datos de la base de manera automatica (Implementacion)
    public List<ModeloListaPicking> obtenerpedidosdbImplementacion() {
        Conexion conexion = new Conexion(this);
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        String idTemporal = null;
        try {
            Statement qu = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PedidosPorSurtir");
            while (r.next()) {
                if (!r.getString(1).equals(idTemporal)) {
                    pedidos.add(new ModeloListaPicking(r.getString("Referencia")
                            , r.getString("Cliente"), r.getString("Fecha")
                            , r.getString("Numero"), r.getString("Serie")));
                }
                idTemporal = r.getString(1);
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Lo sentimos, error:\n" + e.toString() + "\nPor favor, reporte la falla con el area de sistemas")
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

    //Lanza un mensaje de cargando mientras realiza la obtencion de todos los pedidos dispobles para picking
    public void cargardatos() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adaptador = new AdapterListaPedidos(obtenerpedidosdbImplementacion());
                recycerpedidos.setAdapter(adaptador);
            }
        });
    }

    //Lanza mensaje de cargando
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    /*--------------------------------------Interface---------------------------------------------*/

    //Obtiene los datos del dialog usando una interface
    @Override
    public void obtenerDatos(String codigo, String serie) {
        adaptador = new AdapterListaPedidos(cargardatospedido(codigo, serie));
        recycerpedidos.setAdapter(adaptador);
    }
}