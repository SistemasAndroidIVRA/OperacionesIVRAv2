package com.example.operacionesivra.Vistas.Inventario.InventariosCerrados;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.operacionesivra.Vistas.MainActivity.MainActivity;
import com.example.operacionesivra.Vistas.PantallasCargando.Loading;
import com.example.operacionesivra.Vistas.Services.Conexion;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import harmony.java.awt.Color;

public class Inventarioscerrados extends AppCompatActivity {
    //conexión
    Conexion conexionService = new Conexion(this);
    //Filtro
    private EditText txtFiltroITerminados;
    //RecyclerView (tabla)
    private RecyclerView recycerpedidos;
    private AdapterInventariosCerrados adaptador;

    List<ModeloInventariosCerrados> inventariosterminados = new ArrayList<>();
    List<ModeloInventariosCerrados> cerradosFiltrados = new ArrayList();

    String date;
    //Exportar
    Button exportar;
    public int loadinginventarioscerrados = 0;
    //Finalizar para mandar a historicos
    private Button btnFinalizar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_inventarioscerrados_inventarioscerrados);
        //Filtro
        txtFiltroITerminados = findViewById(R.id.txtFiltroITerminados);
        //Recycler
        recycerpedidos = findViewById(R.id.recyclercerrados);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        //Fecha
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        loadinginventarioscerrados = 1;
        loadinglauncher();

        //Exportar
        exportar = findViewById(R.id.exportarinventarioscerrados);
        exportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPDF();
            }
        });

        //Filtro con función al teclear
        /*
        txtFiltroITerminados.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String filtro = txtFiltroITerminados.getText().toString();
                //Invocar método clear para vaciar la lista
                cerradosFiltrados.clear();
                //Llamamos al método filtrar y mandamos el filtro
                filtroInventariosCerrados(filtro);
                return false;
            }
        });
         */
        txtFiltroITerminados.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String filtro = txtFiltroITerminados.getText().toString();
                //Invocar método clear para vaciar la lista
                cerradosFiltrados.clear();
                //Llamamos al método filtrar y mandamos el filtro
                filtroInventariosCerrados(filtro);
            }
        });

        //Finalizar para mandar a históricos
        btnFinalizar = findViewById(R.id.btnFinalizar);
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHistoricos();
            }
        });
    }

    //Invoca a la clase Loading y ejecuta un método segun el estado de variable loadinglauncher
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    //Carga los datos, invoca al metodo ordenarpedidosImplementacion que regresa una lista
    public void cargardatos() {
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
    //Método comentado
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

    /*
    public int comprobarIncidencia(){
        Conexion con = new Conexion(getBaseContext());
        int estadoIncidencia = 0;
        for(int i=0; i<inventariosterminados.size();i++) {
            try {
                Statement stmt = con.conexiondbImplementacion().createStatement();
                String query = "SELECT COUNT(Observaciones) estadoIncidencia FROM Movil_Reporte WHERE Folio = '" + inventariosterminados.get(i).getFolio() + "' AND historico = 0 AND Observaciones != ''";
                ResultSet r = stmt.executeQuery(query);
                while (r.next()) {
                    estadoIncidencia = r.getInt("estadoIncidencia");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return estadoIncidencia;
    }

     */
    
    //Método que actualiza el estado de los inventarios y los manda a históricos
    public void updateHistoricos(){
            new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_MaterialComponents)
                    .setTitle("¡Confirmación!")
                    .setCancelable(false)
                    .setIcon(R.drawable.confirmacion)
                    .setMessage("¿Seguro que desea terminar todos los inventarios?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Creamos la conexión
                            Conexion conexion = new Conexion(getBaseContext());
                            //Prueba y error try catch
                            try {
                                conexion.conexiondbImplementacion().setAutoCommit(false);
                                for(int i=0; i<inventariosterminados.size();i++){
                                    //Preparamos un statement y llamamos el procedimeinto almacenado
                                    PreparedStatement statement = conexion.conexiondbImplementacion().prepareCall("exec PMovil_Cambiar_Historicos '"+inventariosterminados.get(i).getFolio()+"'");
                                    //Ejecutamos el estatement
                                    statement.execute();
                                    //Llamamos mensaje de exito

                                    //Traer movil report items sobre folio
                                    Statement stmt1 = conexion.conexiondbImplementacion().createStatement();
                                    String query = "SELECT Ubicacion, SUM(Total_registrado) as Cantidad, ProductoID, UbicacionID FROM Movil_Reporte WHERE Folio = '"+inventariosterminados.get(i).getFolio()+"' GROUP BY Ubicacion, ProductoID, UbicacionID";
                                    ResultSet r = stmt1.executeQuery(query);
                                    int cont = 0;
                                    while(r.next()){
                                        //Actualizamos en Ubicación producto
                                        PreparedStatement statement2 = conexion.conexiondbImplementacion().prepareCall("exec PUbicacion_Producto_GENERAR ?,?,?,?");
                                        statement2.setString(1, r.getString("UbicacionID"));
                                        statement2.setString(2, r.getString("ProductoID"));
                                        statement2.setString(3, r.getString("Cantidad"));
                                        statement2.setInt(4, 1);
                                        statement2.execute();
                                        cont++;
                                    }
                                }
                                conexion.conexiondbImplementacion().commit();
                                }catch (SQLException throwables) {
                                    //Llamamos mensaje de error
                                    try {
                                        conexion.conexiondbImplementacion().rollback();
                                     mensajeError(throwables);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            Toast.makeText(getBaseContext(), "¡Inventarios terminados exitosamente!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Cerrar dialog
                        }
                    })
                    .show();
    }

    //Mensaje de proceso exitoso
    public void mensajeExitoso(){
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_MaterialComponents)
                .setCancelable(false)
                .setTitle("¡Cambio realizado exitosamente!")
                .setMessage("Se han finalizado los inventarios, podrá consultarlos en Históricos de Inventario.")
                .setIcon(R.drawable.correcto)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cargamos la nueva información
                        onBackPressed();
                        finish();
                    }
                })
                .show();
    }



    //Método para mensaje de error
    public void mensajeError(SQLException throwables){
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_MaterialComponents)
                .setCancelable(false)
                .setTitle("¡Error!")
                .setMessage("No se ha podido realizar la operación, verifique que tenga conexión a internet y vuelva a intentar.\n" +
                        "Descripción del error: "+throwables.getMessage())
                //Botón positivo para reintentar acción
                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateHistoricos();
                    }
                })
                //Botón negativo para salir
                .setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        finish();
                    }
                })
                .show();
    }

    //Método para filtrar optimizadabemte los inventarios en proceso
    public void filtroInventariosCerrados(String filtro){
        //Recorremos la lista
        for(int i=0; i<inventariosterminados.size();i++){
            //Comparaamos si el campo contiene fecha ó si el campo contiene material
            //Transformamos a minusculas el material y el filtro para que no importe la mayuscula o minusculas
            if(inventariosterminados.get(i).getFecha().contains(filtro) || inventariosterminados.get(i).getMaterial().toLowerCase().contains(filtro.toLowerCase()) || inventariosterminados.get(i).getAlmacen().toLowerCase().contains(filtro.toLowerCase())){
                //Agregamos el registro a nuestra lista duplicada
                cerradosFiltrados.add(inventariosterminados.get(i));
            }
        }
        //Iniciamos un nuevo adaptador con la copia de la lista cargada
        adaptador = new AdapterInventariosCerrados(cerradosFiltrados);
        //Agregamos el adaptador al cecycler
        recycerpedidos.setAdapter(adaptador);
    }

    //Crea una lista que almacena los datos de la base de manera automatica (Implementacion)
    public List<ModeloInventariosCerrados> obtenerpedidosdbImplementacion() {
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("declare @Hoy datetime = cast(getdate() as date) ;" +
                    "select " +
                    "Fecha,Usuario,Material,sum(Total_registrado) as total_registrado,StockTotal,Folio,Almacen " +
                    "from Movil_Reporte " +
                    "where Fecha > '2021-06-07' and historico = 0 " +
                    "group by Fecha,Usuario,Material,StockTotal,folio,almacen " +
                    "order by Fecha desc");
            while (r.next()) {
                int estadoIncidencia = 0;
                //Traer estado incidencia
                Statement statement2 = conexionService.conexiondbImplementacion().createStatement();
                String query2 = "SELECT COUNT(Observaciones) estadoIncidencia FROM Movil_Reporte WHERE Folio = '"+r.getString("folio")+"' AND historico = 0 AND Observaciones != ''";
                ResultSet r2 = statement2.executeQuery(query2);
                while(r2.next()){
                    estadoIncidencia = r2.getInt("estadoIncidencia");
                }

                float fisico = Float.parseFloat(r.getString("total_registrado"));
                float sistema = Float.parseFloat(r.getString("StockTotal"));
                float diferencia = sistema - fisico;
                inventariosterminados.add(new ModeloInventariosCerrados(r.getString("Fecha")
                        , r.getString("Usuario"), r.getString("Material")
                        , r.getString("total_registrado"), r.getString("StockTotal")
                        , diferencia + "", r.getString("Folio"), r.getString("Almacen"), estadoIncidencia));
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_MaterialComponents)
                    .setCancelable(false)
                    .setTitle("Error al conectar con el servidor...")
                    .setMessage("Por favor verifique que existe una conexión wi-fi y presione 'Reintentar'.\n Si esto no soluciona el problema cierre la aplicacción y reportelo en el área de desarrollo.\n" + e.getMessage())

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
    public void atras() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Comprueba que exista algún item dentro de la lista creada
    public void comprobarlista() {
        if (inventariosterminados.isEmpty()) {
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

    //Crea un pdf con la información recabada
    public void crearPDF() {
        Document documento = new Document();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        try {
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte de inventario GENERAL - " + date + ".pdf");
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
            PdfPCell cellencabezado = new PdfPCell(new Phrase("\nReporte de Inventario GENERAL", fuente));
            cellencabezado.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.setTotalWidth(1000);
            a.addCell(imagen);
            a.addCell(cellencabezado);
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha:" + date, fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);
            documento.add(a);
            documento.add(new Paragraph("\n\n"));


            PdfPTable table = new PdfPTable(8);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);

            //Encabezado
            PdfPTable encabezado = new PdfPTable(8);

            PdfPCell cellt = new PdfPCell(new Phrase("Almacén"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cellt1 = new PdfPCell(new Phrase("Material"));
            cellt1.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt1);
            PdfPCell celltUsuario = new PdfPCell(new Phrase("Elaborado por"));
            celltUsuario.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(celltUsuario);
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
            PdfPCell cellIncidenciast = new PdfPCell(new Phrase("Incidencias"));
            cellIncidenciast.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellIncidenciast);

            for (int i = 0; i < inventariosterminados.size(); i++) {
                PdfPCell cel0 = new PdfPCell(new Phrase(inventariosterminados.get(i).getAlmacen()));
                cel0.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel0);
                PdfPCell cell = new PdfPCell(new Phrase(inventariosterminados.get(i).getMaterial()));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                PdfPCell celUsuario = new PdfPCell(new Phrase(inventariosterminados.get(i).getUsuario()));
                celUsuario.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(celUsuario);
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
                //Incidencias
                String cadenaIncidencias = "";
                try {
                    Statement stmt = conexionService.conexiondbImplementacion().createStatement();
                    String query = "SELECT Observaciones FROM Movil_Reporte WHERE Folio = '"+inventariosterminados.get(i).getFolio()+"'";
                    ResultSet r = stmt.executeQuery(query);
                    while(r.next()){
                        if(r.getString("Observaciones").equals("")){
                            //
                        }else{
                            cadenaIncidencias = cadenaIncidencias+", "+r.getString("Observaciones");
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                PdfPCell celIncidencias = new PdfPCell(new Phrase(cadenaIncidencias));
                celIncidencias.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(celIncidencias);
            }

            documento.add(encabezado);
            documento.add(table);

        } catch (DocumentException e) {
            System.out.println(e + "1");
        } catch (IOException e) {
            System.out.println(e + "2");
        } finally {
            documento.close();
            Toast.makeText(this, "Reporte Creado con éxito", Toast.LENGTH_SHORT).show();
        }

    }

    //Crea el fichero para el pedf
    public File crearFichero(String nombreFichero) {
        File ruta = getRuta();

        File fichero = null;
        if (ruta != null) {
            fichero = new File(ruta, nombreFichero);
        }

        return fichero;
    }

    //Obtiene la ruta para guardar el documento el en dispositivo
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