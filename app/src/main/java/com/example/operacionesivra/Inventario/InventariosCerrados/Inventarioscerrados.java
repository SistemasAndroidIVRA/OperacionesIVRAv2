package com.example.operacionesivra.Inventario.InventariosCerrados;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.operacionesivra.Inventario.ConteosPausa.AdapterConteos_pausa;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.Services.Conexion;
import com.example.operacionesivra.MainActivity.MainActivity;
import com.example.operacionesivra.R;
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
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import harmony.java.awt.Color;

public class Inventarioscerrados extends AppCompatActivity {
    Conexion conexionService = new Conexion(this);
    private RecyclerView recycerpedidos;
    private AdapterInventariosCerrados adaptador;
    List<ModeloInventariosCerrados> inventariosterminados = new ArrayList<>();
    String date;
    Button  exportar;
    public int loadinginventarioscerrados=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_inventarioscerrados_inventarioscerrados);
        recycerpedidos = findViewById(R.id.recyclercerrados);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        loadinginventarioscerrados=1;
        loadinglauncher();

        exportar = findViewById(R.id.exportarinventarioscerrados);
        exportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPDF();
            }
        });
    }
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }


    public void cargardatos(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adaptador = new AdapterInventariosCerrados(obtenerpedidosdbImplementacion());
                recycerpedidos.setAdapter(adaptador);
                comprobarlista();
            }
        });
    }
    /*--------------------------------------Funciones---------------------------------------------*/

    /*
    //Crea una lista que almacena los datos de la base de manera automatica (Implementacion)
    public List<ModeloInventariosCerrados> obtenerpedidosdbImplementacion() {
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        String idTemporal=null;
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery(Execute PMovil_Item_Scaneados fecha");
            while (r.next()) {
                if(!r.getString(7).equals(idTemporal)){
                    inventariosterminados.add(new ModeloInventariosCerrados(r.getString("Fecha"),r.getString("Usuario"),r.getString("Material"),r.getString("total_registrado"),r.getString("Stock_Total"),r.getString("Folio"),r.getString("Almacen")));
                }
                idTemporal=r.getString(7);
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_MaterialComponents)
                    .setCancelable(false)
                    .setTitle("Error al conectar con el servidor...")
                    .setMessage("Por favor verifique que existe una conexión wi-fi y presione 'Reintentar'.\n Si esto no soluciona el problema cierre la aplicacción y reportelo en el área de desarrollo.\n"+e)

                    .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            obtenerpedidosdbImplementacion();
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
        return inventariosterminados;
    }

     */
    //Crea una lista que almacena los datos de la base de manera automatica (Implementacion)
    public List<ModeloInventariosCerrados> obtenerpedidosdbImplementacion() {
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("declare @Hoy datetime = cast(getdate() as date) ;" +
                    "select " +
                    "Fecha,Usuario,Material,sum(Total_registrado) as total_registrado,StockTotal,Folio,Almacen " +
                    "from Movil_Reporte " +
                    "where Fecha >= @Hoy -7 " +
                    "group by Fecha,Usuario,Material,StockTotal,folio,almacen " +
                    "order by Fecha desc");
            while (r.next()) {
                float fisico=Float.parseFloat (r.getString("total_registrado"));
                float sistema=Float.parseFloat (r.getString("StockTotal"));
                float diferencia=sistema-fisico;
                    inventariosterminados.add(new ModeloInventariosCerrados(r.getString("Fecha")
                            ,r.getString("Usuario"),r.getString("Material")
                            ,r.getString("total_registrado"),r.getString("StockTotal")
                            ,diferencia+"",r.getString("Folio"),r.getString("Almacen")));
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_MaterialComponents)
                    .setCancelable(false)
                    .setTitle("Error al conectar con el servidor...")
                    .setMessage("Por favor verifique que existe una conexión wi-fi y presione 'Reintentar'.\n Si esto no soluciona el problema cierre la aplicacción y reportelo en el área de desarrollo.\n"+e)

                    .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           obtenerpedidosdbImplementacion();
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
        return inventariosterminados;
    }


    /*--------------------------------------Botones---------------------------------------------*/
    public void atras(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void comprobarlista(){
        if(inventariosterminados.isEmpty()){
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Vacio")
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("Actualmente no existe algun inventario en pausa")
                    .setPositiveButton("Recargar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            obtenerpedidosdbImplementacion();
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

    //-------------------------------------------PDF------------------------------------------------
    public void crearPDF() {
        Document documento = new Document();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        try {
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte de: " +date+".pdf");
            documento.setPageSize(PageSize.LEGAL);
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();
            Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, 18, Font.BOLD, Color.BLACK);
            Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD, Color.BLACK);
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
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha:" + date, fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);
            documento.add(a);
            documento.add(new Paragraph("\n\n\n\n\n\n"));


            PdfPTable table = new PdfPTable(5);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);

            //Encabezado
            PdfPTable encabezado = new PdfPTable(5);

            PdfPCell cellt = new PdfPCell(new Phrase("Material"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cell2t = new PdfPCell(new Phrase("Físico"));
            cell2t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell2t);
            PdfPCell cell3t = new PdfPCell(new Phrase("Sistema"));
            cell3t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell3t);
            PdfPCell cell4t = new PdfPCell(new Phrase("Diferencia"));
            cell4t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell4t);
            PdfPCell cell5t = new PdfPCell(new Phrase("Fecha"));
            cell5t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell5t);

            for (int i = 0; i < inventariosterminados.size(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(inventariosterminados.get(i).getMaterial()));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                PdfPCell cel2 = new PdfPCell(new Phrase(inventariosterminados.get(i).getFisico()));
                cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel2);
                PdfPCell cel3 = new PdfPCell(new Phrase(inventariosterminados.get(i).getSistema()));
                cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel3);
                PdfPCell cel4 = new PdfPCell(new Phrase(inventariosterminados.get(i).getDiferencia()));
                cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel4);
                PdfPCell cel5 = new PdfPCell(new Phrase(inventariosterminados.get(i).getFecha()));
                cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel5);
            }

            documento.add(encabezado);
            documento.add(table);

        } catch (DocumentException e) {
            System.out.println(e+"1");
        } catch (IOException e) {
            System.out.println(e+"2");
        } finally {
            documento.close();
            Toast.makeText(this, "Reporte Creado con éxito", Toast.LENGTH_SHORT).show();
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


}