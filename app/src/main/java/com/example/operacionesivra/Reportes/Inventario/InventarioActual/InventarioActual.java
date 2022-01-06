package com.example.operacionesivra.Reportes.Inventario.InventarioActual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.operacionesivra.Inventario.InventariosCerrados.AdapterInventariosCerrados;
import com.example.operacionesivra.Inventario.InventariosCerrados.ModeloInventariosCerrados;
import com.example.operacionesivra.PantallaRecepcion.ModeloVideos;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Reportes.Chequeo.AdapterChequeoTerminado;
import com.example.operacionesivra.Reportes.Inventario.InventariosCerrados.AdapterReporteInventariosCerrados;
import com.example.operacionesivra.Services.Conexion;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.security.AccessController;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InventarioActual extends AppCompatActivity implements InventarioActualInterface {
    private RecyclerView recycerpedidos;
    private AdapterInventarioActual adaptador;
    List<ModeloInventarioActual> inventariosterminados = new ArrayList<>();
    public int loadinginventarioactual=0;
    public String material;
    Context context;
    TextView nombredelmaterial,fisico,comprometido,disponible;

    //Seleccion de material
    List<ModeloInventarioActualBuscar> materiales = new ArrayList<>();
    String[] opciones;
    String seleccion;
    String codigoseleccionado=null;

    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportes_inventario_actual);
        Toolbar toolbar = findViewById(R.id.toolbarinventarioactual);
        setSupportActionBar(toolbar);
        context = this;
        recycerpedidos = findViewById(R.id.recyclerinventarioactual);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        nombredelmaterial = findViewById(R.id.nombrematerial_ia);

        barChart = findViewById(R.id.char_ia);

        barChart.getDescription().setEnabled(false);

        fisico = findViewById(R.id.fisicot);
        comprometido = findViewById(R.id.comprometidot);
        disponible = findViewById(R.id.disponiblet);

        barChart.setVisibility(View.GONE);
    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    public void lanzawhatsapp(){
        //Enviar texto
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_TEXT, "Probando texto");

        //Enviar imagen
        /*
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 12345);
                 Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.setPackage("com.whatsapp");
        View vistaa = new View(this);
        String path = MediaStore.Images.Media.insertImage(vistaa.getContext().getContentResolver(),bitmap,"HOla",null);
        Uri imagen = Uri.parse(path);
        intent.putExtra(Intent.EXTRA_STREAM,imagen);
         */
        try {
            this.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(context, "El dispositivo no tiene instalada la aplicación", Toast.LENGTH_SHORT).show();
        }

    }

    public List<ModeloInventarioActual> materialactual(String codigo) {
        Conexion conexionService = new Conexion(this);
        if(!inventariosterminados.isEmpty()){
            inventariosterminados.clear();
        }
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_Item_Inventario_Actual '"+codigo+"'");
            while(r.next()) {
                    inventariosterminados.add(new ModeloInventarioActual(material,r.getString(1),r.getString(2),r.getString(3),r.getString(4)));
            }
        } catch (Exception e) {
            Toast.makeText(context, "Imposible conectar con el servidor", Toast.LENGTH_SHORT).show();
        }
        if(inventariosterminados.isEmpty()){
            Toast.makeText(this, "Sin resultados...", Toast.LENGTH_SHORT).show();
        }
        return inventariosterminados;
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.filtros, menu);
        menu.findItem(R.id.materialfiltro).setVisible(false);
        menu.findItem(R.id.fechafiltro).setVisible(false);
        menu.findItem(R.id.exportarfiltro).setVisible(false);
        return  true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.materialactualfiltro:
                InventarioActualitem actualitem = new InventarioActualitem();
                actualitem.show(getSupportFragmentManager(), null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void datos(final String nombre) {
            material=nombre;
            loadinginventarioactual=1;
            loadinglauncher();
    }

    public void doinbackgroud(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultados();
            }
        });
    }

    //Crea una lista con los items correspondientes a una consulta
    public String[] buscarmaterial(){
        Conexion c = new Conexion(this);
        int contador=0;
        if(!materiales.isEmpty()){
            materiales.clear();
            nombredelmaterial.setText("");
            opciones=null;
        }
        try{
            Statement s = c.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("select nombre,productoid from Producto where nombre like '%"+material+"%'");
            while (r.next()){
                materiales.add(new ModeloInventarioActualBuscar(r.getString("nombre"),r.getString("productoid")) );
                contador++;
            }
            opciones = new String[contador];
            for(int i = 0;i<materiales.size();i++){
                opciones[i]=materiales.get(i).getNombre();
            }
        }catch (Exception e){
            System.out.println("Error: "+ e);
        }
        return opciones;
    }

    //Muestra la lista creada y envia la siguiente solicitud
    public void resultados(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Materiales encontrados")
                .setCancelable(false)
                .setSingleChoiceItems(buscarmaterial(), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccion = opciones[which];
                        for(int i =0;i<materiales.size();i++){
                            if(materiales.get(i).getNombre().equals(seleccion)) {
                                codigoseleccionado = materiales.get(i).getId();
                            }
                        }
                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adaptador = new AdapterInventarioActual(materialactual(codigoseleccionado));
                        recycerpedidos.setAdapter(adaptador);
                        sumartotales();
                        Toast.makeText(context, "Para ver los detalles selecciona un almacen", Toast.LENGTH_LONG).show();
                        for(int i =0;i<materiales.size();i++){
                            if(codigoseleccionado.equals(materiales.get(i).getId())){
                                nombredelmaterial.setText(materiales.get(i).getNombre());
                            }
                        }
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .show();
    }

    //obtiene los datos segun el almacen
    public ArrayList<BarEntry> enviardatos(String almacen){
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i =0;i<inventariosterminados.size();i++){
            if(almacen.equals(inventariosterminados.get(i).getAlmacen())){
                float fisico = Float.parseFloat(inventariosterminados.get(i).getFisico());
                float comprometido = Float.parseFloat(inventariosterminados.get(i).getComprometido());
                float disponible = Float.parseFloat(inventariosterminados.get(i).getDisponible());
                entries.add(new BarEntry(0,fisico,"Fisico"));
                entries.add(new BarEntry(1,comprometido,"Comprometido"));
                entries.add(new BarEntry(2,disponible,"Disponible"));
            }
        }
        return entries;
    }

    public void respuesta2(String nombre){
        BarDataSet bardataset;
        bardataset= new BarDataSet(enviardatos(nombre), "");
        bardataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                String valores = barEntry.getData().toString()+" ("+barEntry.getY()+")";
                return  valores.replace(".0","");
            }
        });
        bardataset.setValueTextSize(10);

        BarData data = new BarData(bardataset);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setEnabled(false);
        barChart.setData(data); // set the data and list of labels into chart
        bardataset.setColors(new int[]{R.color.color1 , R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10} , this);
        barChart.setTouchEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();

    }

    //Muestra la suma total de cada almacen
    public void sumartotales(){
        float fisicot=0;
        float comprometidot=0;
        float disponiblet=0;
        for(int i =0;i<inventariosterminados.size();i++){
            fisicot = Float.parseFloat(inventariosterminados.get(i).getFisico())+fisicot;
            comprometidot = Float.parseFloat(inventariosterminados.get(i).getComprometido())+comprometidot;
            disponiblet = Float.parseFloat(inventariosterminados.get(i).getDisponible())+disponiblet;
        }

        fisico.setText("Fisico: "+fisicot);
        comprometido.setText("Comprometido: "+comprometidot);
        disponible.setText("Disponible: "+disponiblet);
    }
}