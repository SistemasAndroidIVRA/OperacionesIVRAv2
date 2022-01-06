package com.example.operacionesivra.Reportes.Chequeo.TerminadosListaPedido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import com.example.operacionesivra.Reportes.Chequeo.ListaChequeoTerminado;
import com.example.operacionesivra.Chequeo.Surtir.ModeloDetallesChequeo;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CrearReporte extends AppCompatActivity {
    TextView pedido, tiempo, metrostotales, items;

    String serie;
    PieChart pieChart;
    List<ModeloDetallesChequeo> itemsdechequeo = new ArrayList<>();
    Conexion conexionService = new Conexion(this);

    //Recycler
    private RecyclerView recycerpedidos;
    private AdapterReporte adaptador;

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i = new Intent(this, ListaChequeoTerminado.class);
        setContentView(R.layout.reportes_crear_reporte);
        pieChart = findViewById(R.id.pieChart);
        items = findViewById(R.id.ContadorChequeoTerminado);
        pedido = findViewById(R.id.pedidoChequeoTerminado);
        pedido.setText(getIntent().getStringExtra("pedidoChequeo"));
        serie = getIntent().getStringExtra("serieChequeo");
        tiempo = findViewById(R.id.tiempochequeoterminado);
        metrostotales = findViewById(R.id.metroschequeoterminado);
        //Recycler
        recycerpedidos = findViewById(R.id.recyclerreportechequeo);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdapterReporte(llenardatos());
        recycerpedidos.setAdapter(adaptador);

        pie();
        tiemportranscurrido(getIntent().getStringExtra("horainicioChequeo"), getIntent().getStringExtra("horafinChequeo"));
    }

    public long tiemportranscurrido(String actuals, String bases) {
        long minutesr = 0;
        long segundos = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date actual;
            Date base;
            base = format.parse(bases);
            actual = format.parse(actuals);
            long diff = base.getTime() - actual.getTime();//as given
            long diffsegundos = base.getTime() - actual.getTime();
            final long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            final long seginfods = TimeUnit.MILLISECONDS.toSeconds(diffsegundos);
            segundos = seginfods;
            minutesr = minutes;
            if (minutesr == 0) {
                tiempo.setText(segundos + " Seg");
            } else {
                tiempo.setText(minutesr + " Min");
            }
        } catch (Exception e) {
            System.out.println("Error" + e);
        }

        return minutesr;
    }

    public List<ModeloDetallesChequeo> llenardatos() {
        float metrost = 0f;
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PedidosPorSurtir_Productos '" + pedido.getText() + "', N'" + serie + "'");
            while (r.next()) {
                itemsdechequeo.add(new ModeloDetallesChequeo(r.getString("Producto"), r.getString("Cantidad"), r.getString("Unidad"), R.drawable.correcto, false));
                float tem = Float.parseFloat(r.getString("Cantidad"));
                metrost = metrost + tem;
            }
        } catch (Exception e) {
            System.out.println(e + "a ver a ver");
        }
        metrostotales.setText(metrost + " Ml");
        items.setText(itemsdechequeo.size() + "");
        return itemsdechequeo;
    }

    /*public void barras(){
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(8f, 0));
        entries.add(new BarEntry(2f, 1));
        entries.add(new BarEntry(5f, 2));
        entries.add(new BarEntry(20f, 3));
        entries.add(new BarEntry(15f, 4));
        entries.add(new BarEntry(19f, 5));

        BarDataSet bardataset = new BarDataSet(entries, "Cells");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("2016");
        labels.add("2015");
        labels.add("2014");
        labels.add("2013");
        labels.add("2012");
        labels.add("2011");

        BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the data and list of labels into chart
        barChart.setDescription("Set Bar Chart Description Here");  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.animateY(5000);
    }
     */

    public void pie() {
        try {
            ArrayList<PieEntry> NoOfEmp = new ArrayList();
            for (int i = 0; i < itemsdechequeo.size(); i++) {
                float valor = Float.parseFloat(itemsdechequeo.get(i).getCantidad());
                NoOfEmp.add(new PieEntry(valor, itemsdechequeo.get(i).getMaterial()));
            }
            PieDataSet dataSet = new PieDataSet(NoOfEmp, "");
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            dataSet.setDrawIcons(false);

            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            /*
            Legend l = pieChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
            l.setTextSize(10f);
             */

            pieChart.getLegend().setEnabled(false);

            pieChart.getDescription().setEnabled(false);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            pieChart.setData(data);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setCenterText("Items del pedido");
            pieChart.setCenterTextSize(12f);
            pieChart.animateY(2000, Easing.EaseInOutQuad);

            pieChart.invalidate();
        } catch (Exception e) {

        }
    }

    public void crearreporte() {
        Document documento = new Document();
        try {
            File file = crearFichero("Reporte.pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());
            documento.setPageSize(PageSize.LEGAL.rotate());
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);
            documento.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.shimaco);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image imagen = Image.getInstance(stream.toByteArray());

            Bitmap as = pieChart.getChartBitmap();

            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            as.compress(Bitmap.CompressFormat.PNG, 100, stream2);
            Image imagen2 = Image.getInstance(stream2.toByteArray());
            documento.add(imagen);
            documento.add(new Paragraph("\n\n\n"));
            documento.add(imagen2);
            documento.add(new Paragraph("\n\n\n"));

        } catch (DocumentException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            documento.close();
        }

    }

    public File crearFichero(String nombreFichero) {
        File ruta = getRuta();
        File fichero = null;
        if (ruta != null) {
            fichero = new File(ruta, nombreFichero);
        }
        return fichero;
    }

    public File getRuta() {
        File ruta = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Pruebas");
            System.out.println(ruta);
            if (ruta != null) {
                if (!ruta.mkdirs()) {
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }
        }
        return ruta;
    }

    public void salir() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmación")
                .setIcon(R.drawable.confirmacion)
                .setCancelable(false)
                .setMessage("¿Quiere terminar la revisión?\nPuede revisar los detalles de este pedido en el momento que sea.")
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        salir();
    }
}