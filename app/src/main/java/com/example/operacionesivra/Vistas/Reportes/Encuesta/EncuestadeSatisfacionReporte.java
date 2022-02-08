package com.example.operacionesivra.Vistas.Reportes.Encuesta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
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
import java.util.Locale;

public class EncuestadeSatisfacionReporte extends AppCompatActivity {
    Conexion conexion;
    List<ModeloReporteEncuesta> itemsdechequeo = new ArrayList<>();
    PieChart r5p1, r5p2, r5p3, r5p4, r5p5, r5p6;
    BarChart barChartr1, barChartr2, barChartr3, barChartr4, barChartr7, barChartr8, barChartr9;
    private AdapterReporteEncuesta adaptador;
    private RecyclerView recycerpedidos;
    View encuestasindividual;
    boolean isUp;
    ScrollView layout;

    final ArrayList<BarEntry> entriesr1 = new ArrayList<>();
    final ArrayList<BarEntry> entriesr2 = new ArrayList<>();
    final ArrayList<BarEntry> entriesr3 = new ArrayList<>();
    final ArrayList<BarEntry> entriesr4 = new ArrayList<>();
    final ArrayList<BarEntry> entriesr7 = new ArrayList<>();
    final ArrayList<BarEntry> entriesr8 = new ArrayList<>();
    final ArrayList<BarEntry> entriesr9 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportes_activity_encuestade_satisfacion_reporte);
        conexion = new Conexion(this);
        this.setTitle("Resumen de encuesta");
        Toolbar toolbar = findViewById(R.id.toolbarencuesta);
        setSupportActionBar(toolbar);
        layout = findViewById(R.id.reporteencuestasatisfaccion_l);
        barChartr1 = findViewById(R.id.r1grafica);
        barChartr2 = findViewById(R.id.r2grafica);
        barChartr3 = findViewById(R.id.r3grafica);
        barChartr4 = findViewById(R.id.r4grafica);
        barChartr7 = findViewById(R.id.r7grafica);
        barChartr8 = findViewById(R.id.r8grafica);
        barChartr9 = findViewById(R.id.r9grafica);
        r5p1 = findViewById(R.id.r5p1grafica);
        r5p2 = findViewById(R.id.r5p2grafica);
        r5p3 = findViewById(R.id.r5p3grafica);
        r5p4 = findViewById(R.id.r5p4grafica);
        r5p5 = findViewById(R.id.r5p5grafica);
        r5p6 = findViewById(R.id.r5p6grafica);

        encuestasindividual = findViewById(R.id.encuestaindividual);
        encuestasindividual.setVisibility(View.INVISIBLE);
        obtenerdatos();
        encuestasindividual.setMinimumWidth(2);

        // Permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    1000);
        }


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filtros, menu);
        menu.findItem(R.id.materialfiltro).setVisible(false);
        menu.findItem(R.id.fechafiltro).setVisible(false);
        menu.findItem(R.id.materialactualfiltro).setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.exportarfiltro) {
            crearreporte(entriesr1, entriesr2, entriesr3, entriesr4, entriesr7, entriesr8, entriesr9);
        }
        return super.onOptionsItemSelected(item);
    }

    //Desplaza el contenido de manera automatica
    public void slideUp(View view) {
        recycerpedidos = findViewById(R.id.recyclerreporteencuesta);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdapterReporteEncuesta(obtenerfecha());
        recycerpedidos.setAdapter(adaptador);
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                view.getHeight(),
                0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    //Desplaza el contenido de manera automatica
    public void slideDown(View view) {
        view.setVisibility(View.INVISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        encuestasindividual.setVisibility(View.GONE);
        encuestasindividual.invalidate();
        view.invalidate();
    }

    public void onSlideViewButtonClick(View view) {
        if (isUp) {
            slideDown(encuestasindividual);
        } else {
            slideUp(encuestasindividual);
            layout.fullScroll(View.FOCUS_DOWN);
        }
        isUp = !isUp;
    }

    public List<ModeloReporteEncuesta> obtenerfecha() {
        try {
            Statement qu = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("select * from Movil_CalificacionesEncuesta order by fechayhora DESC  ");
            while (r.next()) {
                itemsdechequeo.add(new ModeloReporteEncuesta(r.getString("r1"), r.getString("r2"), r.getString("r3")
                        , r.getString("r4"), r.getString("r5cal1"), r.getString("r5cal2"), r.getString("r5cal3")
                        , r.getString("r5cal4"), r.getString("r5cal5"), r.getString("r5cal6"), r.getString("r7")
                        , r.getString("r8"), r.getString("r9"), r.getString("recomendacion"), r.getString("usuario")
                        , r.getString("empresa"), r.getString("fechayhora"), r.getString("idencuesta")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return itemsdechequeo;
    }

    //Obtiene la lectura de los datos
    public void obtenerdatos() {
        int contador = 0;
        int contador1 = 0;
        int contador2 = 0;
        int contador3 = 0;
        int contador4 = 0;
        ArrayList<PieEntry> entriesr51 = new ArrayList();
        ArrayList<PieEntry> entriesr52 = new ArrayList();
        ArrayList<PieEntry> entriesr53 = new ArrayList();
        ArrayList<PieEntry> entriesr54 = new ArrayList();
        ArrayList<PieEntry> entriesr55 = new ArrayList();
        ArrayList<PieEntry> entriesr56 = new ArrayList();
        int contador7 = 0;
        int contador8 = 0;
        int contador9 = 0;

        try {
            Statement qu = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_ReporteEncuesta");
            while (r.next()) {
                switch (r.getString("Tipo")) {
                    case "r1":
                        entriesr1.add(new BarEntry(contador1, Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        contador1++;
                        break;
                    case "r2":
                        entriesr2.add(new BarEntry(contador2, Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        contador2++;
                        break;
                    case "r3":
                        entriesr3.add(new BarEntry(contador3, Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        contador3++;
                        break;
                    case "r4":
                        entriesr4.add(new BarEntry(contador4, Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        contador4++;
                        break;
                    case "r5cal1":
                        entriesr51.add(new PieEntry(Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        break;
                    case "r5cal2":
                        entriesr52.add(new PieEntry(Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        break;
                    case "r5cal3":
                        entriesr53.add(new PieEntry(Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        break;
                    case "r5cal4":
                        entriesr54.add(new PieEntry(Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        break;
                    case "r5cal5":
                        entriesr55.add(new PieEntry(Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        break;
                    case "r5cal6":
                        entriesr56.add(new PieEntry(Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        break;
                    case "r7":
                        entriesr7.add(new BarEntry(contador7, Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        contador7++;
                        break;
                    case "r8":
                        entriesr8.add(new BarEntry(contador8, Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        contador8++;
                        break;
                    case "r9":
                        entriesr9.add(new BarEntry(contador9, Float.parseFloat(r.getString("Total")), r.getString("Respuesta")));
                        contador9++;
                        break;
                }

                contador++;
            }
            respuesta1(entriesr1, contador);
            respuesta2(entriesr2);
            respuesta3(entriesr3);
            respuesta4(entriesr4);
            respuesta5p1(entriesr51);
            respuesta5p2(entriesr52);
            respuesta5p3(entriesr53);
            respuesta5p4(entriesr54);
            respuesta5p5(entriesr55);
            respuesta5p6(entriesr56);
            respuesta7(entriesr7);
            respuesta8(entriesr8);
            respuesta9(entriesr9);

        } catch (Exception e) {
        }


    }

    //Cargan una grafica con los datos obtenidos de la base de datos
    public void respuesta1(ArrayList<BarEntry> entradas, int itemstotales) {
        BarDataSet bardataset;
        bardataset = new BarDataSet(entradas, "");

        System.out.println(entradas.get(0).getData().toString());
        bardataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                String valores = barEntry.getData().toString() + " (" + barEntry.getY() + ")";
                return valores.replace(".0", "");
            }
        });
        bardataset.setValueTextSize(10);

        BarData data = new BarData(bardataset);
        barChartr1.getAxisRight().setEnabled(false);
        barChartr1.getDescription().setEnabled(false);
        barChartr1.getXAxis().setEnabled(false);
        barChartr1.setData(data); // set the data and list of labels into chart
        bardataset.setColors(new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10}, this);
        barChartr1.setTouchEnabled(false);
        barChartr1.animateY(2500);
        barChartr1.invalidate();
 /*
        ArrayList NoOfEmp = new ArrayList();
        try {
            NoOfEmp.add(new Entry(Float.parseFloat(r1porcentaje.getText().toString().replace("%","") ), 0));
            NoOfEmp.add(new Entry( 100- Float.parseFloat(r1porcentaje.getText().toString().replace("%","") ), 1));
        }catch (Exception e){
            System.out.println(e);
        }

        PieDataSet dataSet = new PieDataSet(NoOfEmp, "(Material)");
        ArrayList year = new ArrayList();

        year.add("Sobrante");
        year.add("Fisico");
        pier1.setDescription("");

        PieData data2 = new PieData(year, dataSet);
        pier1.setData(data2);
        pier1.setCenterText("Items del pedido");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pier1.animateXY(2000, 2000);

  */

    }

    public void respuesta2(ArrayList<BarEntry> entradas) {
        BarDataSet bardataset;
        bardataset = new BarDataSet(entradas, "");

        System.out.println(entradas.get(0).getData().toString());
        bardataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                String valores = barEntry.getData().toString() + " (" + barEntry.getY() + ")";
                return valores.replace(".0", "");
            }
        });
        bardataset.setValueTextSize(10);

        BarData data = new BarData(bardataset);
        barChartr2.setDrawGridBackground(false);
        barChartr2.getAxisRight().setEnabled(false);
        barChartr2.getDescription().setEnabled(false);
        barChartr2.getXAxis().setEnabled(false);
        barChartr2.setData(data); // set the data and list of labels into chart
        bardataset.setColors(new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10}, this);
        barChartr2.setTouchEnabled(false);
        barChartr2.animateY(2500);
        barChartr2.invalidate();

    }

    public void respuesta3(ArrayList<BarEntry> entradas) {
        BarDataSet bardataset;
        bardataset = new BarDataSet(entradas, "");

        System.out.println(entradas.get(0).getData().toString());
        bardataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                String valores = barEntry.getData().toString() + " (" + barEntry.getY() + ")";
                return valores.replace(".0", "");
            }
        });
        bardataset.setValueTextSize(10);

        BarData data = new BarData(bardataset);
        barChartr3.setDrawGridBackground(false);
        barChartr3.getAxisRight().setEnabled(false);
        barChartr3.getDescription().setEnabled(false);
        barChartr3.getXAxis().setEnabled(false);
        barChartr3.setData(data); // set the data and list of labels into chart
        bardataset.setColors(new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10}, this);
        barChartr3.setTouchEnabled(false);
        barChartr3.animateY(2500);
        barChartr3.invalidate();

    }

    public void respuesta4(ArrayList<BarEntry> entradas) {
        BarDataSet bardataset;
        bardataset = new BarDataSet(entradas, "");

        bardataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                String valores = barEntry.getData().toString() + " (" + barEntry.getY() + ")";
                return valores.replace(".0", "");
            }
        });
        bardataset.setValueTextSize(10);

        BarData data = new BarData(bardataset);
        barChartr4.setDrawGridBackground(false);
        barChartr4.getAxisRight().setEnabled(false);
        barChartr4.getDescription().setEnabled(false);
        barChartr4.getXAxis().setEnabled(false);
        barChartr4.setData(data); // set the data and list of labels into chart
        bardataset.setColors(new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10}, this);
        barChartr4.setTouchEnabled(false);
        barChartr4.animateY(2500);
        barChartr4.invalidate();

    }

    public void respuesta5p1(ArrayList entradas) {
        try {
            PieDataSet dataSet = new PieDataSet(entradas, "");
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setColors(new int[]{R.color.color3, R.color.color4, R.color.color2, R.color.color4, R.color.color5, R.color.color6}, this);
            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            r5p1.getLegend().setEnabled(false);

            r5p1.getDescription().setEnabled(false);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            r5p1.setData(data);
            r5p1.setEntryLabelColor(Color.BLACK);
            r5p1.setCenterText("Profesionalismo");
            r5p1.setCenterTextSize(15f);
            r5p1.animateY(2000, Easing.EaseInOutQuad);

            r5p1.invalidate();
            //Coloca la legend en la parte superior derecha
            /*
            Legend l =r5p1.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
            l.setTextSize(10f);
            */
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void respuesta5p2(ArrayList entradas) {
        try {
            PieDataSet dataSet = new PieDataSet(entradas, "");
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setColors(new int[]{R.color.color3, R.color.color4, R.color.color2, R.color.color4, R.color.color5, R.color.color6}, this);
            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            r5p2.getLegend().setEnabled(false);

            r5p2.getDescription().setEnabled(false);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            r5p2.setData(data);
            r5p2.setEntryLabelColor(Color.BLACK);
            r5p2.setCenterText("Calidad en el servicio");
            r5p2.setCenterTextSize(15f);
            r5p2.animateY(2000, Easing.EaseInOutQuad);

            r5p2.invalidate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void respuesta5p3(ArrayList entradas) {
        try {

            PieDataSet dataSet = new PieDataSet(entradas, "");
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setColors(new int[]{R.color.color3, R.color.color4, R.color.color2, R.color.color4, R.color.color5, R.color.color6}, this);
            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            r5p3.getLegend().setEnabled(false);

            r5p3.getDescription().setEnabled(false);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            r5p3.setData(data);
            r5p3.setEntryLabelColor(Color.BLACK);
            r5p3.setCenterText("Atención del ejecutivo de vent6as");
            r5p3.setCenterTextSize(15f);
            r5p3.animateY(2000, Easing.EaseInOutQuad);

            r5p3.invalidate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void respuesta5p4(ArrayList entradas) {
        try {

            PieDataSet dataSet = new PieDataSet(entradas, "");
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setColors(new int[]{R.color.color3, R.color.color4, R.color.color2, R.color.color4, R.color.color5, R.color.color6}, this);
            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            r5p4.getLegend().setEnabled(false);

            r5p4.getDescription().setEnabled(false);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            r5p4.setData(data);
            r5p4.setEntryLabelColor(Color.BLACK);
            r5p4.setCenterText("Calidad del producto");
            r5p4.setCenterTextSize(15f);
            r5p4.animateY(2000, Easing.EaseInOutQuad);

            r5p4.invalidate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void respuesta5p5(ArrayList entradas) {
        try {

            PieDataSet dataSet = new PieDataSet(entradas, "");
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setColors(new int[]{R.color.color3, R.color.color4, R.color.color2, R.color.color4, R.color.color5, R.color.color6}, this);
            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            r5p5.getLegend().setEnabled(false);

            r5p5.getDescription().setEnabled(false);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            r5p5.setData(data);
            r5p5.setEntryLabelColor(Color.BLACK);
            r5p5.setCenterText("Tiempo de entrega");
            r5p5.setCenterTextSize(15f);
            r5p5.animateY(2000, Easing.EaseInOutQuad);

            r5p5.invalidate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void respuesta5p6(ArrayList entradas) {
        try {

            PieDataSet dataSet = new PieDataSet(entradas, "");
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setColors(new int[]{R.color.color3, R.color.color4, R.color.color2, R.color.color4, R.color.color5, R.color.color6}, this);
            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            r5p6.getLegend().setEnabled(false);

            r5p6.getDescription().setEnabled(false);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            r5p6.setData(data);
            r5p6.setEntryLabelColor(Color.BLACK);
            r5p6.setCenterText("Relación Calidad/Precio");
            r5p6.setCenterTextSize(15f);
            r5p6.animateY(2000, Easing.EaseInOutQuad);

            r5p6.invalidate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void respuesta7(ArrayList<BarEntry> entradas) {
        BarDataSet bardataset;
        bardataset = new BarDataSet(entradas, "");

        System.out.println(entradas.get(0).getData().toString());
        bardataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                String valores = barEntry.getData().toString() + " (" + barEntry.getY() + ")";
                return valores.replace(".0", "");
            }
        });
        bardataset.setValueTextSize(10);

        BarData data = new BarData(bardataset);
        barChartr7.setDrawGridBackground(false);
        barChartr7.getAxisRight().setEnabled(false);
        barChartr7.getDescription().setEnabled(false);
        barChartr7.getXAxis().setEnabled(false);
        barChartr7.setData(data); // set the data and list of labels into chart
        bardataset.setColors(new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10}, this);
        barChartr7.setTouchEnabled(false);
        barChartr7.animateY(2500);
        barChartr7.invalidate();

    }

    public void respuesta8(ArrayList<BarEntry> entradas) {
        BarDataSet bardataset;
        bardataset = new BarDataSet(entradas, "");
        bardataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                String valores = barEntry.getData().toString() + " (" + barEntry.getY() + ")";
                return valores.replace(".0", "");
            }
        });
        bardataset.setValueTextSize(10);

        BarData data = new BarData(bardataset);
        barChartr8.setDrawGridBackground(false);
        barChartr8.getAxisRight().setEnabled(false);
        barChartr8.getDescription().setEnabled(false);
        barChartr8.getXAxis().setEnabled(false);
        barChartr8.setData(data); // set the data and list of labels into chart
        bardataset.setColors(new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10}, this);
        barChartr8.setTouchEnabled(false);
        barChartr8.animateY(2500);
        barChartr8.invalidate();
    }

    public void respuesta9(ArrayList<BarEntry> entradas) {
        BarDataSet bardataset;
        bardataset = new BarDataSet(entradas, "");

        bardataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                String valores = barEntry.getData().toString() + " (" + barEntry.getY() + ")";
                return valores.replace(".0", "");
            }
        });
        bardataset.setValueTextSize(10);

        BarData data = new BarData(bardataset);
        barChartr9.setDrawGridBackground(false);
        barChartr9.getAxisRight().setEnabled(false);
        barChartr9.getDescription().setEnabled(false);
        barChartr9.getXAxis().setEnabled(false);
        barChartr9.setData(data); // set the data and list of labels into chart
        bardataset.setColors(new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10}, this);
        barChartr9.setTouchEnabled(false);
        barChartr9.animateY(2500);
        barChartr9.invalidate();

    }

    //Genera el pedf correspondiente
    public void crearreporte(ArrayList<BarEntry> p1, ArrayList<BarEntry> p2, ArrayList<BarEntry> p3, ArrayList<BarEntry> p4, ArrayList<BarEntry> p7, ArrayList<BarEntry> p8, ArrayList<BarEntry> p9) {
        Document documento = new Document();
        String horafin = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        try {
            File file = crearFichero("Reporte de encuesta: " + date + " a las " + horafin + ".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());
            documento.setPageSize(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);
            documento.setMargins(-50f, -50f, 5f, 5f);
            documento.open();
            Bitmap bar1 = barChartr1.getChartBitmap();
            Bitmap bar2 = barChartr2.getChartBitmap();
            Bitmap bar3 = barChartr3.getChartBitmap();
            Bitmap bar4 = barChartr4.getChartBitmap();

            Bitmap p5r1 = r5p1.getChartBitmap();
            Bitmap p5r2 = r5p2.getChartBitmap();
            Bitmap p5r3 = r5p3.getChartBitmap();
            Bitmap p5r4 = r5p4.getChartBitmap();
            Bitmap p5r5 = r5p5.getChartBitmap();
            Bitmap p5r6 = r5p6.getChartBitmap();

            Bitmap bar7 = barChartr7.getChartBitmap();
            Bitmap bar8 = barChartr8.getChartBitmap();
            Bitmap bar9 = barChartr9.getChartBitmap();

            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            bar1.compress(Bitmap.CompressFormat.PNG, 100, stream1);
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            bar2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
            bar3.compress(Bitmap.CompressFormat.PNG, 100, stream3);
            ByteArrayOutputStream stream4 = new ByteArrayOutputStream();
            bar4.compress(Bitmap.CompressFormat.PNG, 100, stream4);

            ByteArrayOutputStream streamp5r1 = new ByteArrayOutputStream();
            p5r1.compress(Bitmap.CompressFormat.PNG, 100, streamp5r1);
            ByteArrayOutputStream streamp5r2 = new ByteArrayOutputStream();
            p5r2.compress(Bitmap.CompressFormat.PNG, 100, streamp5r2);
            ByteArrayOutputStream streamp5r3 = new ByteArrayOutputStream();
            p5r3.compress(Bitmap.CompressFormat.PNG, 100, streamp5r3);
            ByteArrayOutputStream streamp5r4 = new ByteArrayOutputStream();
            p5r4.compress(Bitmap.CompressFormat.PNG, 100, streamp5r4);
            ByteArrayOutputStream streamp5r5 = new ByteArrayOutputStream();
            p5r5.compress(Bitmap.CompressFormat.PNG, 100, streamp5r5);
            ByteArrayOutputStream streamp5r6 = new ByteArrayOutputStream();
            p5r6.compress(Bitmap.CompressFormat.PNG, 100, streamp5r6);

            ByteArrayOutputStream stream7 = new ByteArrayOutputStream();
            bar7.compress(Bitmap.CompressFormat.PNG, 100, stream7);
            ByteArrayOutputStream stream8 = new ByteArrayOutputStream();
            bar8.compress(Bitmap.CompressFormat.PNG, 100, stream8);
            ByteArrayOutputStream stream9 = new ByteArrayOutputStream();
            bar9.compress(Bitmap.CompressFormat.PNG, 100, stream9);


            Image p1g = Image.getInstance(stream1.toByteArray());
            Image p2g = Image.getInstance(stream2.toByteArray());
            Image p3g = Image.getInstance(stream3.toByteArray());
            Image p4g = Image.getInstance(stream4.toByteArray());
            Image p5r1g = Image.getInstance(streamp5r1.toByteArray());
            Image p5r2g = Image.getInstance(streamp5r2.toByteArray());
            Image p5r3g = Image.getInstance(streamp5r3.toByteArray());
            Image p5r4g = Image.getInstance(streamp5r4.toByteArray());
            Image p5r5g = Image.getInstance(streamp5r5.toByteArray());
            Image p5r6g = Image.getInstance(streamp5r6.toByteArray());
            Image p7g = Image.getInstance(stream7.toByteArray());
            Image p8g = Image.getInstance(stream8.toByteArray());
            Image p9g = Image.getInstance(stream9.toByteArray());

            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tResultados de la encuesta de satisfacción"));
            documento.add(new Paragraph("\n\n\n"));

            PdfPTable tabla = new PdfPTable(2);
            PdfPTable tablatitulos = new PdfPTable(1);
            PdfPTable tabla2 = new PdfPTable(2);
            tabla2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            tablatitulos.addCell("¿Cuánto tiempo lleva utilizando nuestros productos?");
            for (int i = 0; i < p1.size(); i++) {
                tabla2.addCell(p1.get(i).getData() + "");
                tabla2.addCell(p1.get(i).getY() + "");
            }
            tabla.addCell(tabla2);
            tabla.addCell(p1g);
            documento.add(tablatitulos);
            documento.add(tabla);
            tabla.deleteBodyRows();
            tablatitulos.deleteBodyRows();

            PdfPTable tabla4 = new PdfPTable(2);
            tabla4.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            tablatitulos.addCell("¿Cómo conoció nuestra empresa?");
            for (int i = 0; i < p2.size(); i++) {
                tabla4.addCell(p2.get(i).getData() + "");
                tabla4.addCell(p2.get(i).getY() + "");
            }
            tabla.addCell(tabla4);
            tabla.addCell(p2g);
            documento.add(tablatitulos);
            documento.add(tabla);
            tabla.deleteBodyRows();
            tablatitulos.deleteBodyRows();

            PdfPTable tabla5 = new PdfPTable(2);
            tabla5.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            tablatitulos.addCell("¿Con qué frecuencia visita la empresa?");
            for (int i = 0; i < p3.size(); i++) {
                tabla5.addCell(p3.get(i).getData() + "");
                tabla5.addCell(p3.get(i).getY() + "");
            }
            tabla.addCell(tabla5);
            tabla.addCell(p3g);
            documento.add(tablatitulos);
            documento.add(tabla);
            tabla.deleteBodyRows();
            tablatitulos.deleteBodyRows();

            PdfPTable tabla6 = new PdfPTable(2);
            tabla6.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            tablatitulos.addCell("En comparación con la competencia, los productos IVRA son:");
            for (int i = 0; i < p4.size(); i++) {
                tabla6.addCell(p4.get(i).getData() + "");
                tabla6.addCell(p4.get(i).getY() + "");
            }
            tabla.addCell(tabla6);
            tabla.addCell(p4g);
            documento.add(tablatitulos);
            documento.add(tabla);
            tabla.deleteBodyRows();
            tablatitulos.deleteBodyRows();

            tablatitulos.addCell("Califique los siguientes aspectos de IVRA");
            PdfPTable grupo = new PdfPTable(3);
            grupo.addCell(p5r1g);
            grupo.addCell(p5r2g);
            grupo.addCell(p5r3g);
            grupo.addCell(p5r4g);
            grupo.addCell(p5r5g);
            grupo.addCell(p5r6g);

            documento.add(tablatitulos);
            documento.add(grupo);
            tabla.deleteBodyRows();
            tablatitulos.deleteBodyRows();

            PdfPTable tabla7 = new PdfPTable(2);
            tabla7.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            tablatitulos.addCell("¿Ha recomendado nuestra empresa a otras personas?");
            for (int i = 0; i < p7.size(); i++) {
                tabla7.addCell(p7.get(i).getData() + "");
                tabla7.addCell(p7.get(i).getY() + "");
            }
            tabla.addCell(tabla7);
            tabla.addCell(p7g);
            documento.add(tablatitulos);
            documento.add(tabla);
            tabla.deleteBodyRows();
            tablatitulos.deleteBodyRows();

            PdfPTable tabla8 = new PdfPTable(2);
            tabla8.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            tablatitulos.addCell("¿Recomendaría nuestros productos a otras personas?");
            for (int i = 0; i < p8.size(); i++) {
                tabla8.addCell(p8.get(i).getData() + "");
                tabla8.addCell(p8.get(i).getY() + "");
            }
            tabla.addCell(tabla8);
            tabla.addCell(p8g);
            documento.add(tablatitulos);
            documento.add(tabla);
            tabla.deleteBodyRows();
            tablatitulos.deleteBodyRows();

            PdfPTable tabla9 = new PdfPTable(2);
            tabla9.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            tablatitulos.addCell("¿Volvería a comprar nuestros productos?");
            for (int i = 0; i < p8.size(); i++) {
                tabla9.addCell(p9.get(i).getData() + "");
                tabla9.addCell(p9.get(i).getY() + "");
            }
            tabla.addCell(tabla9);
            tabla.addCell(p9g);
            documento.add(tablatitulos);
            documento.add(tabla);
            tabla.deleteBodyRows();
            tablatitulos.deleteBodyRows();

            documento.add(tabla);

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
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Encuesta de satisfacción");
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


}