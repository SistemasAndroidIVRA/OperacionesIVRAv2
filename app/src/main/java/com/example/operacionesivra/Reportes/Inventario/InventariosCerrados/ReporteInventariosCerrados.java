package com.example.operacionesivra.Reportes.Inventario.InventariosCerrados;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.operacionesivra.Inventario.InventariosCerrados.ModeloInventariosCerrados;
import com.example.operacionesivra.MainActivity.MainActivity;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Reportes.SelectordeReportes;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class ReporteInventariosCerrados extends AppCompatActivity {
    Conexion conexionService = new Conexion(this);
    private RecyclerView recycerpedidos;
    private AdapterReporteInventariosCerrados adaptador;
    List<ModeloInventariosCerrados> inventariosterminados = new ArrayList<>();
    String date;
    public int loadingreportecerrados =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportes_reporte_inventarios_cerrados);
        Toolbar toolbar = findViewById(R.id.toolbarinventarioscerrados);
        setSupportActionBar(toolbar);
        recycerpedidos = findViewById(R.id.recyclercerrados);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        loadingreportecerrados =1;
        loadinglauncher();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.filtros, menu);
        menu.findItem(R.id.materialactualfiltro).setVisible(false);
        return  true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.fechafiltro:
                escogerdia();
                break;
            case R.id.materialfiltro:
                escogermaterial();
                break;
            case R.id.exportarfiltro:
                crearPDF();
                break;
            case R.id.materialactualfiltro:
                escogermaterialactual();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }


    public void cargardatos(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adaptador = new AdapterReporteInventariosCerrados(obtenerpedidosdbImplementacion());
                recycerpedidos.setAdapter(adaptador);
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
                    "where Fecha >= @Hoy -7 and Total_registrado is not null " +
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

    //Crea una lista que almacena los datos de la base de manera automatica (Implementacion)
    public List<ModeloInventariosCerrados> cargardatosfecha(String fecha) {
        if(!inventariosterminados.isEmpty()){
            inventariosterminados.clear();
        }
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("select " +
                    "Fecha,Usuario,Material,sum(Total_registrado) as total_registrado,StockTotal,Folio,Almacen " +
                    "from Movil_Reporte " +
                    "where Fecha = '"+fecha+ "' and Total_registrado is not null "  +
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

    public List<ModeloInventariosCerrados> cargardatosnombre(String material) {
        if(!inventariosterminados.isEmpty()){
            inventariosterminados.clear();
        }
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("select " +
                    "Fecha,Usuario,Material,sum(Total_registrado) as total_registrado,StockTotal,Folio,Almacen " +
                    "from Movil_Reporte " +
                    "where Material LIKE '%"+material+"%' and Total_registrado is not null " +
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

    public List<ModeloInventariosCerrados> materialactual(String nombre) {
        if(!inventariosterminados.isEmpty()){
            inventariosterminados.clear();
        }
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_BuscarItem '"+nombre+"', '01 GAMMA'");
            while (r.next()) {
                inventariosterminados.add(new ModeloInventariosCerrados(r.getString("Familia")
                        ,r.getString("Producto"),r.getString("Almacen")
                        ,r.getString("Unidad"),r.getString("Cantidad")
                        ,r.getString("CodProducto"),r.getString("Ubicacion"),r.getString("Almacen")));
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

    public void escogermaterial(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre Del Material");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adaptador = new AdapterReporteInventariosCerrados(cargardatosnombre(input.getText().toString()));
                recycerpedidos.setAdapter(adaptador);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void escogermaterialactual(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre Del Material");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adaptador = new AdapterReporteInventariosCerrados(materialactual(input.getText().toString()));
                recycerpedidos.setAdapter(adaptador);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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

                adaptador = new AdapterReporteInventariosCerrados(cargardatosfecha(fechapicker));
                recycerpedidos.setAdapter(adaptador);
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


    /*--------------------------------------Botones---------------------------------------------*/
    public void atras(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //-------------------------------------------PDF------------------------------------------------
    public void crearPDF() {
        Document documento = new Document();
        String horafin= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        try {
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte de inventario: " +date+" a las "+horafin+".pdf");
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
            documento.add(new Paragraph("\n\n"));


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

            Font fuentefalta = FontFactory.getFont(FontFactory.defaultEncoding, Font.DEFAULTSIZE, Color.RED);
            Font fuentesobra = FontFactory.getFont(FontFactory.defaultEncoding, Font.DEFAULTSIZE, Color.GREEN);
            Font fuenteok = FontFactory.getFont(FontFactory.defaultEncoding, Font.DEFAULTSIZE, Color.BLUE);
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

                float diferencia= Float.parseFloat(inventariosterminados.get(i).getDiferencia());
                if(diferencia==0f) {
                    PdfPCell cel4 = new PdfPCell(new Phrase(inventariosterminados.get(i).getDiferencia(),fuenteok));
                    cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel4);
                }else if(diferencia>0f){
                    PdfPCell cel4 = new PdfPCell(new Phrase("-"+inventariosterminados.get(i).getDiferencia(),fuentefalta));
                    cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel4);
                }else if(diferencia<0f){
                    PdfPCell cel4 = new PdfPCell(new Phrase(inventariosterminados.get(i).getDiferencia().replace("-","+"),fuentesobra));
                    cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel4);
                }

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
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Inventarios");

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
        Intent intent = new Intent(this, SelectordeReportes.class);
        startActivity(intent);
        finish();
    }
}