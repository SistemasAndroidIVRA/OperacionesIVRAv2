package com.example.operacionesivra.Vistas.Reportes.Inventario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.operacionesivra.Vistas.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
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
import java.util.Locale;

public class ReportesInventarioGeneral extends AppCompatActivity {
    BarChart inventario;
    public TextView fecha, total;
    ArrayList<ModeloItemsInventarioReporte> reporte = new ArrayList<>();
    ArrayList<BarEntry> sistema = new ArrayList<>();
    ArrayList<BarEntry> fisico  = new ArrayList<>();
    ArrayList<String> nombre = new ArrayList<>();
    public int loadinginventariogeneral=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportes_inventario_general);
        inventario = findViewById(R.id.inventarioGeneralInventario);
        inventario.getDescription().setEnabled(false);

        fecha = findViewById(R.id.fecharevisioninventariogeneral);
        total = findViewById(R.id.totaldeitemsinventariogeneral);

        escogerdia();

        findViewById(R.id.fechareportegeneral).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escogerdia();
            }
        });

        // Permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    1000);
        }
    }

    //Muestra los datos de la base de datos
    public void consulta(String fecha){
        this.fecha.setText( fecha);
        if(!reporte.isEmpty()){
            reporte.clear();
            sistema.clear();
            fisico.clear();
            nombre.clear();
        }
        int i=0;
        try {
            Conexion conexion = new Conexion(this);
            Statement es = conexion.conexiondbImplementacion().createStatement();
            ResultSet resultado = es.executeQuery("select sum(Total_registrado) as total" +
                    ", StockTotal, Material,Fecha,Folio,horainicio,horafin from Movil_Reporte where Total_registrado " +
                    "is not null and Fecha ='"+fecha+"' group by Fecha,StockTotal, Folio, Material,horainicio,horafin order by Fecha DESC");
            while (resultado.next()){
                reporte.add(new ModeloItemsInventarioReporte(resultado.getString("total")
                        ,resultado.getString("StockTotal"),resultado.getString("Material")
                        ,resultado.getString("Fecha"),resultado.getString("Folio")
                        ,resultado.getString("horainicio"),resultado.getString("horafin")));

                float stocktotal = Float.parseFloat(resultado.getString("StockTotal"));
                float total = Float.parseFloat(resultado.getString("total"));
                    sistema.add(new BarEntry(i,stocktotal,resultado.getString("Material")));
                    fisico.add(new BarEntry(i,total,resultado.getString("Material")));
                    nombre.add(i, resultado.getString("Material"));
                    i++;
            }

            llenar(sistema,fisico);

        }catch (SQLException e){
            System.out.println("Error: "+e);
        }
    }

    //llena la tabla con las entradas del sistema compararndo contra el fisico
    public void llenar(final ArrayList<BarEntry> sistema, ArrayList<BarEntry> fisico){
        //InterfazG

                float groupSpace = 0.32f;
                float barSpace = 0.04f;
                float barWidth = 0.8f;

                int groupCount = sistema.size() + 1;
                int startYear = 1980;
                int endYear = startYear + groupCount;


                BarDataSet set1, set2;
                set1 = new BarDataSet(fisico, "Fisico");
                set1.setColor(Color.GREEN);
                set2 = new BarDataSet(sistema, "Sistema");
                set2.setColor(Color.BLUE);

                set2.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getBarLabel(BarEntry barEntry) {
                        return barEntry.getData().toString() + "\n" + barEntry.getY();
                    }
                });

                set1.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getBarLabel(BarEntry barEntry) {
                        return barEntry.getData().toString() + "\n" + barEntry.getY();
                    }
                });


                inventario.getAxisRight().setEnabled(false);
                BarData data = new BarData(set2, set1);
                inventario.setData(data);
                inventario.getBarData().setBarWidth(barWidth);
                // restrict the x-axis range
                inventario.getXAxis().setAxisMinimum(startYear);
                inventario.getXAxis().setAxisMaximum(sistema.size() + 1);

                XAxis xAxis = inventario.getXAxis();
                xAxis.setGranularity(2f);
                xAxis.setCenterAxisLabels(true);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return "";
                    }
                });

                // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
                inventario.getXAxis().setAxisMaximum(startYear + inventario.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                inventario.groupBars(startYear, groupSpace, barSpace);

                inventario.invalidate();
                total.setText(sistema.size() + "");
    }


    public void crearPDF() {
        Document documento = new Document();
        try {
            String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte de: " +hora+ ".pdf");
            documento.setPageSize(PageSize.LEGAL);
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

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
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha:" , fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);


            PdfPTable table = new PdfPTable(4);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);


            //Encabezado
            PdfPTable encabezado = new PdfPTable(4);

            PdfPCell cellt = new PdfPCell(new Phrase("Fisico"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cell2t = new PdfPCell(new Phrase("Sistema"));
            cell2t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell2t);
            PdfPCell cell3t = new PdfPCell(new Phrase("Material"));
            cell3t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell3t);
            PdfPCell cell4t = new PdfPCell(new Phrase("Fecha"));
            cell4t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell4t);
            PdfPCell cell5t = new PdfPCell(new Phrase("Diferencia"));
            cell5t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell5t);

            for (int i = 0; i < reporte.size(); i++) {
                String faltante;
                PdfPCell cell = new PdfPCell(new Phrase(reporte.get(i).getTotal()));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                PdfPCell cel2 = new PdfPCell(new Phrase(reporte.get(i).getStocktotal()));
                cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel2);
                PdfPCell cel3 = new PdfPCell(new Phrase(reporte.get(i).getMaterial()));
                cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel3);
                PdfPCell cel4 = new PdfPCell(new Phrase(reporte.get(i).getFecha()));
                cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel4);
                float stocktotal = Float.parseFloat(reporte.get(i).getStocktotal());
                float total = Float.parseFloat(reporte.get(i).getTotal());
                if(stocktotal-total>0) {
                    faltante = stocktotal - total + "";
                }

            }

            documento.add(encabezado);
            documento.add(table);

        } catch (DocumentException e) {
            System.out.println(e+"1");
        } catch (IOException e) {
            System.out.println(e+"2");
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
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MisPDFs");

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

    public void escogerdia(){
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int mYear = Integer.parseInt(date.substring(0,4));
        int mMonth = Integer.parseInt(date.substring(5,7));
        int mDay = Integer.parseInt(date.substring(8,10));
        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                String fechapicker;
                selectedmonth = selectedmonth+1;
                if(selectedmonth<10){
                    fechapicker="" + selectedyear + "-0" + selectedmonth + "-" + selectedday;
                }
                else {
                    fechapicker="" + selectedyear + "-" + selectedmonth + "-" + selectedday;
                }
                fecha.setText(fechapicker);
                loadinginventariogeneral=1;
                loadinglauncher();
            }
        }, mYear, mMonth- 1, mDay);
        mDatePicker.setCancelable(false);
        mDatePicker.setTitle("Seleccione la fecha que desea consultar");
        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDatePicker.show();
    }

    public void cargardatos() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                consulta(fecha.getText().toString());
            }
        });
    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }



}