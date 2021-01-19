package com.example.operacionesivra.Reportes.Inventario;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.operacionesivra.Inventario.AdapterItemDetalle;
import com.example.operacionesivra.Inventario.ModeloDetallesItem;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Reportes.Inventario.InventarioActual.AdapterInventarioActual;
import com.example.operacionesivra.Reportes.Inventario.InventariosCerrados.ReporteInventariosCerrados;
import com.example.operacionesivra.Services.Conexion;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReporteInventarioTerminado extends AppCompatActivity {
    PieChart pieChart;
    private RecyclerView recycerpedidos;
    private AdapterItemDetalle adapterItemDetalle;
    List<ModeloDetallesItem> inventarioterminado = new ArrayList<>();
    TextView totalregistrado, totalrequerido,fecha,material;
    public int loadingReporteterminado=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportes_reporte_inventario_terminado);
        pieChart = findViewById(R.id.pieChart2);
        totalregistrado =findViewById(R.id.totalregistradoreporteinventario);
        totalrequerido= findViewById(R.id.totalrequeridoreporteinventario);
        material = findViewById(R.id.materialreporteinventario);
        fecha=findViewById(R.id.fechareporteinventario);
        recycerpedidos = findViewById(R.id.recyclerReporteInventario);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        loadingReporteterminado=1;
        loadinglauncher();

        findViewById(R.id.exportar_RIT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportar();
            }
        });
    }

    public void exportar(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Exportar")
                .setMessage("¿Quiere exportar este inventario?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        crearPDF();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    public void cargardatosbacgroud(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapterItemDetalle = new AdapterItemDetalle(consultarInventario(getIntent().getStringExtra("folio")));
                    recycerpedidos.setAdapter(adapterItemDetalle);
                }
            });

    }

    //Revisado
    public List<ModeloDetallesItem> consultarInventario(String folio) {
        Conexion conexionService = new Conexion(this);
        float total=0f;
        try {
            Statement es = conexionService.conexiondbImplementacion().createStatement();
            ResultSet resultado = es.executeQuery("select * from Movil_Reporte where Folio='" + folio + "'");

                while (resultado.next()) {
                    try {
                        String contenido = resultado.getString("Total_registrado");
                        total = total + Float.parseFloat(contenido);
                        inventarioterminado.add(new ModeloDetallesItem(resultado.getString("Codigo_unico")
                                ,resultado.getString("Cantidad"),resultado.getString("Total_registrado")
                                ,resultado.getString("Ubicacion"),resultado.getString("Estado")
                                ,R.drawable.correcto,resultado.getString("Observaciones")
                                ,resultado.getString("Folio")));
                        totalrequerido.setText(resultado.getString("StockTotal"));
                        fecha.setText(resultado.getString("Fecha"));
                        material.setText(resultado.getString("Material"));
                    } catch (Exception e) {
                        System.out.println("Error "+e);
                    }

                }

            totalregistrado.setText(total+"");
            pie();
        }catch (SQLException e){
            System.out.println(e);
        }
        return inventarioterminado;
    }

    public void pie() {
        try {
            if(Float.parseFloat(totalrequerido.getText().toString())<Float.parseFloat(totalregistrado.getText().toString())){
                ArrayList<PieEntry> NoOfEmp = new ArrayList();

                NoOfEmp.add(new PieEntry(Float.parseFloat(totalrequerido.getText().toString()) , "Sistema"));
                NoOfEmp.add(new PieEntry( Float.parseFloat(totalregistrado.getText().toString())- Float.parseFloat(totalrequerido.getText().toString()), "Sobrante"));

                PieDataSet dataSet = new PieDataSet(NoOfEmp, "(Material)");
                dataSet.setDrawIcons(false);
                dataSet.setSliceSpace(3f);
                dataSet.setIconsOffset(new MPPointF(0, 40));
                dataSet.setSelectionShift(5f);
                dataSet.setValueTextColor(Color.BLACK);
                pieChart.getLegend().setEnabled(false);

                pieChart.getDescription().setEnabled(false);

                PieData data = new PieData( dataSet);
                data.setValueTextColor(Color.BLACK);
                data.setValueTextSize(18);
                pieChart.setData(data);
                pieChart.setEntryLabelColor(Color.BLACK);
                pieChart.setCenterTextSize(18);
                pieChart.setEntryLabelTextSize(18);
                pieChart.setCenterText("Inventario");
                dataSet.setColors(new int[]{R.color.color1 , R.color.color4},this);
                pieChart.animateXY(2000, 2000);
            }
            else {
                ArrayList NoOfEmp = new ArrayList();

                NoOfEmp.add(new PieEntry(Float.parseFloat(totalrequerido.getText().toString()) - Float.parseFloat(totalregistrado.getText().toString()), "Faltante"));
                NoOfEmp.add(new PieEntry(Float.parseFloat(totalregistrado.getText().toString()),"Fisico"));

                PieDataSet dataSet = new PieDataSet(NoOfEmp, "(Material)");
                dataSet.setDrawIcons(false);
                dataSet.setSliceSpace(3f);
                dataSet.setIconsOffset(new MPPointF(0, 40));
                dataSet.setSelectionShift(5f);
                dataSet.setValueTextColor(Color.BLACK);
                pieChart.getLegend().setEnabled(false);

                pieChart.getDescription().setEnabled(false);


                PieData data = new PieData( dataSet);
                data.setValueTextSize(14f);
                data.setValueTextColor(Color.BLACK);
                pieChart.setData(data);
                pieChart.setEntryLabelColor(Color.BLACK);
                pieChart.setCenterTextSize(18);
                pieChart.setEntryLabelTextSize(18);
                pieChart.setCenterText("Inventario");
                dataSet.setColors(new int[]{R.color.colorAccent , R.color.positivo} , this);
                pieChart.animateXY(2000, 2000);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void crearPDF() {
        Document documento = new Document();
        try {
            String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte re impreso de: "+inventarioterminado.get(0).getMaterialregistrado().replace("/","-")+" con fecha de "+fecha.getText().toString()+".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            documento.setPageSize(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();
            Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, 18, Font.BOLD, harmony.java.awt.Color.BLACK);
            Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD, harmony.java.awt.Color.BLACK);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.shimaco);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            PdfPTable a = new PdfPTable(3);
            PdfPCell cellencabezado = new PdfPCell(new Phrase("\nReporte de Inventario", fuente));
            cellencabezado.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.setTotalWidth(1000);
            a.addCell(imagen);
            a.addCell(cellencabezado);
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha:" + fecha.getText().toString(), fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);


            PdfPTable table = new PdfPTable(4);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);

            String restanteString = "Cantidad Faltante: ";
            documento.add(a);
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tNombre del Material: " + inventarioterminado.get(0).getMaterialregistrado() + "\n\n"));
            Float restante = null;
            try {
                float total = Float.parseFloat(totalrequerido.getText().toString());
                float escaneado = Float.parseFloat(totalregistrado.getText().toString());
                restante = total - escaneado;
                if (restante < 0) {
                    restanteString = "Sobrante de: ";
                }
            } catch (Exception e) {
                documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tError al completar la creación del PDF, por favor acuda al area de desarrollo para mas información"));
            }


            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tCantidad en sistema: " + totalrequerido.getText().toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\tCantidad registrada: " + totalregistrado.getText().toString()));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + restanteString + "" + restante.toString().replace("-", " ")));

            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tRegistros Realizados:\n\n"));

            //Encabezado
            PdfPTable encabezado = new PdfPTable(4);

            PdfPCell cellt = new PdfPCell(new Phrase("Cantidad de rollos"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cell2t = new PdfPCell(new Phrase("Contenido"));
            cell2t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell2t);
            PdfPCell cell3t = new PdfPCell(new Phrase("Total"));
            cell3t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell3t);
            PdfPCell cell4t = new PdfPCell(new Phrase("Ubicación"));
            cell4t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell4t);

            for (int i = 0; i < inventarioterminado.size(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(inventarioterminado.get(i).getCantidad()));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                PdfPCell cel2 = new PdfPCell(new Phrase(inventarioterminado.get(i).getLongitud()));
                cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel2);
                PdfPCell cel3 = new PdfPCell(new Phrase(inventarioterminado.get(i).getMaterialregistrado()));
                cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel3);
                PdfPCell cel4 = new PdfPCell(new Phrase(inventarioterminado.get(i).getUbicacion()));
                cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel4);
            }

            documento.add(encabezado);
            documento.add(table);

            documento.add(new Paragraph("\n\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t(Inventario Reimpreso)\n"));
        } catch (DocumentException e) {
            System.out.println(e+"1");
        } catch (IOException e) {
            System.out.println(e+"2");
        } finally {
            documento.close();
            Toast.makeText(this, "¡PDF Creado!", Toast.LENGTH_SHORT).show();
            onBackPressed();
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
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Inventario");
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

    @Override
    public void onBackPressed() {
       Intent intent = new Intent(this, ReporteInventariosCerrados.class);
       startActivity(intent);
       finish();
    }
}