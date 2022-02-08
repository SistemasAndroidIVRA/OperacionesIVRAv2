package com.example.operacionesivra.Vistas.Inventarios.Vistas.EnHistorico;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Modelos.ModeloInventariosHistorico;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.EnHistorico.DetallesHistorico.InventarioAEnHistoricosDetalle;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.EnHistorico.DetallesHistorico.PdfDocumentAdapter;
import com.example.operacionesivra.Vistas.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
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
import java.util.Locale;

import harmony.java.awt.Color;

public class InventariosEnHistorico extends AppCompatActivity {
    //Status para la pantalla loading
    public int loading = 0;
    RecyclerView recyclerInvHistoricos;
    //FileUri
    File fileUri;
    Conexion con;
    //Contexto
    Context contexto = this;
    ArrayList<ModeloInventariosHistorico> inventariosEnHistorico = new ArrayList<ModeloInventariosHistorico>();
    AdapterInventariosEnHistorico adapterInventariosEnHistorico;
    ArrayList<ModeloInventariosHistorico> inventariosEnHistoricoRespaldo = new ArrayList<ModeloInventariosHistorico>();
    //Fecha
    String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());;
    //EditTexts
    EditText txtInvHistFiltrador;
    //TextViews
    TextView txtInvHistFInicial, txtInvHistFFinal;
    //Buttons
    ImageButton btnInvHistFInicial, btnInvHistFFinal;
    Button btnInvHistFiltrador, btnInvHistReporteGeneral, btnEnHistoricoRegresar;
    //CheckBox
    CheckBox cbFechaHist;
    //Botones de ordenamiento
    Button btnOrdenarHistNum;
    //Status del ordenamiento
    int statusbtnOrdenarHistNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Bloquear teclado inicial
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_a_en_historico);
        //Recycler
        adapterInventariosEnHistorico = new AdapterInventariosEnHistorico();
        recyclerInvHistoricos =findViewById(R.id.recyclerInvHistoricos);
        //EditText
        txtInvHistFiltrador = findViewById(R.id.txtInvHistBuscador);
        txtInvHistFInicial = findViewById(R.id.txtInvHistFInicial);
        txtInvHistFInicial.setText(fecha);
        txtInvHistFFinal = findViewById(R.id.txtInvHistFFinal);
        txtInvHistFFinal.setText(fecha);
        //ImageButtons
        btnInvHistFInicial = findViewById(R.id.btnInvHistFInicial);
        btnInvHistFFinal = findViewById(R.id.btnInvHistFFinal);
        //Buttons ordenamiento
        btnOrdenarHistNum = findViewById(R.id.btnOrdenarHistNum);
        //Buttons
        btnEnHistoricoRegresar = findViewById(R.id.btnEnHistoricoRegresar);
        btnInvHistFiltrador = findViewById(R.id.btnInvHistFiltrador);
        btnInvHistReporteGeneral = findViewById(R.id.btnInvHistReporteGeneral);
        cbFechaHist = findViewById(R.id.cbFechaHist);
        //Acciones botones
        btnEnHistoricoRegresar.setOnClickListener(view -> {
            finish();
        });
        btnInvHistReporteGeneral.setOnClickListener(view -> {
            openDialogOpcionesReporte();
        });

        //Método on key pressed filtrador
        txtInvHistFiltrador.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                getInventarioKeyPressed(txtInvHistFiltrador.getText().toString());
                return false;
            }
        });

        //Métodos botones
        //Botón FInicial
        btnInvHistFInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFechaInicial(txtInvHistFInicial);
            }
        });

        //Botón FFinal
        btnInvHistFFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFechaFinal(txtInvHistFFinal);
            }
        });

        //Botón Filtrador
        btnInvHistFiltrador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getByFechas();
                loading = 1;
                loadinglauncher();
            }
        });

        //Acciones botones ordenamiento
        btnOrdenarHistNum.setOnClickListener(view -> {
            if(statusbtnOrdenarHistNum == 0){

                statusbtnOrdenarHistNum = 1;
            }else{
                statusbtnOrdenarHistNum = 0;
            }
        });
    }

    //Cerrar al presionar atrás
    @Override
    public void onBackPressed() {
        finish();
    }

    //Pantalla cargando
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    public void openDialogOpcionesReporte(){
        //Abrir menú
        AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.inventario_a_en_proceso_reporte_menu, null);
        alert.setView(view);

        AlertDialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.show();
        //Buttons
        Button btnReporteMGuardar, btnReporteMImprimir, btnReporteMCerrar;
        btnReporteMGuardar = view.findViewById(R.id.btnReporteMGuardar);
        btnReporteMImprimir = view.findViewById(R.id.btnReporteMImprimir);
        btnReporteMCerrar = view.findViewById(R.id.btnReporteMCerrar);
        btnReporteMCerrar.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        btnReporteMGuardar.setOnClickListener(view1 -> {
            loading = 2;
            loadinglauncher();
            //generateReporte(1);
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Éxito")
                    .setIcon(R.drawable.correcto)
                    .setMessage("¡Reporte creado con éxito! guardado en: Almacenamiento interno/documents/Reportes Generales/")
                    .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    })
                    .show();
            dialog.dismiss();
        });
        btnReporteMImprimir.setOnClickListener(view1 -> {
            loading = 3;
            loadinglauncher();
            //generateReporte(2);
            dialog.dismiss();
        });
    }

    public void generateReporte(int status){
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Document documento = new Document();
        Conexion conexion = new Conexion(this);
        try {
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte General del día "+date.replace("-", "_")+".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            documento.setPageSize(PageSize.LEGAL);
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

            //Encabezado
            PdfPTable encabezado = new PdfPTable(11);
            //Contenido
            PdfPTable table = new PdfPTable(11);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);

            PdfPCell cellt = new PdfPCell(new Phrase("Fecha"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cell2t = new PdfPCell(new Phrase("Usuario"));
            cell2t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell2t);
            PdfPCell cell3t = new PdfPCell(new Phrase("Almacén"));
            cell3t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell3t);
            PdfPCell cell4t = new PdfPCell(new Phrase("Material"));
            cell4t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell4t);
            PdfPCell cell5t = new PdfPCell(new Phrase("Entradas"));
            cell5t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell5t);
            PdfPCell cell6t = new PdfPCell(new Phrase("Fisico"));
            cell6t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell6t);
            PdfPCell cell7t = new PdfPCell(new Phrase("Sistema"));
            cell7t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell7t);
            PdfPCell cell8t = new PdfPCell(new Phrase("Diferencia"));
            cell8t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell8t);
            PdfPCell cell9t = new PdfPCell(new Phrase("H.inicio"));
            cell9t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell9t);
            PdfPCell cell10 = new PdfPCell(new Phrase("H.fin"));
            cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell10);
            PdfPCell cell11 = new PdfPCell(new Phrase("Incidencias"));
            cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell11);

            for (int i = 0; i < inventariosEnHistorico.size(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(inventariosEnHistorico.get(i).getFecha()));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                PdfPCell cel2 = new PdfPCell(new Phrase(inventariosEnHistorico.get(i).getUsuario()));
                cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel2);
                PdfPCell cel3 = new PdfPCell(new Phrase(inventariosEnHistorico.get(i).getAlmacen()));
                cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel3);
                PdfPCell cel4 = new PdfPCell(new Phrase(inventariosEnHistorico.get(i).getMaterial()));
                cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel4);
                PdfPCell cel5 = new PdfPCell(new Phrase(inventariosEnHistorico.get(i).getEntradas()));
                cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel5);
                PdfPCell cel6 = new PdfPCell(new Phrase(inventariosEnHistorico.get(i).getFisico()));
                cel6.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel6);
                PdfPCell cel7 = new PdfPCell(new Phrase(inventariosEnHistorico.get(i).getSistema()));
                cel7.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel7);
                PdfPCell cel8 = new PdfPCell(new Phrase(""+((Float.parseFloat(inventariosEnHistorico.get(i).getFisico())) - (Float.parseFloat(inventariosEnHistorico.get(i).getSistema())))));
                cel8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel8);
                PdfPCell cel9 = new PdfPCell(new Phrase(inventariosEnHistorico.get(i).getHoraInicio()));
                cel9.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel9);
                PdfPCell cel10= new PdfPCell(new Phrase(inventariosEnHistorico.get(i).getHoraFin()));
                cel10.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel10);
                //Incidencias
                //Incidencias
                String cadenaIncidencias = "";
                try {
                    Statement stmt = conexion.conexiondbImplementacion().createStatement();
                    String query = "SELECT Observaciones FROM Movil_Reporte WHERE Folio = '"+inventariosEnHistorico.get(i).getFolio()+"'";
                    ResultSet r = stmt.executeQuery(query);
                    while(r.next()){
                        if(r.getString("Observaciones").equals("")){
                            //
                            cadenaIncidencias = "Sin incidencias";
                        }else{
                            cadenaIncidencias = cadenaIncidencias+", "+r.getString("Observaciones");
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                PdfPCell cel11 = new PdfPCell(new Phrase(cadenaIncidencias));
                cel11.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel11);
            }
            documento.add(encabezado);
            documento.add(table);
            fileUri = file;
            Toast.makeText(InventariosEnHistorico.this, "¡Reporte creado exitosamente! ", Toast.LENGTH_SHORT).show();
        } catch (DocumentException e) {
            Toast.makeText(InventariosEnHistorico.this, "Error: " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(InventariosEnHistorico.this, "Error: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            documento.close();
            if(status == 1){

            }else if(status == 2){
                printPDF(fileUri);
                Toast.makeText(contexto, "Abriendo vista previa del archivo...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void printPDF(File file){
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try{
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(contexto, file.getAbsolutePath());
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        }catch (Exception e){
            Toast.makeText(contexto, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Reportes Generales");

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

    public void getFechaInicial(TextView txt){
        //Traer fecha del sistema en formato añi/mes/día
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //Definimos el valor para nuestras variables que definirán el día
        int mYear = Integer.parseInt(date.substring(0,4));
        int mMonth = Integer.parseInt(date.substring(5,7));
        int mDay = Integer.parseInt(date.substring(8,10));
        //Creamos un datepicker
        DatePickerDialog mDatePicker;
        //Iniciamos el nuevo objeto e indicamos el contexto donde se presentará el dialog
        mDatePicker = new DatePickerDialog(InventariosEnHistorico.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                //Variable donde se guarda la fecha seleccionada
                String fechapicker;
                selectedmonth = selectedmonth+1;
                //Guardamos el formato de la fecha
                if(selectedmonth<10){
                    fechapicker="" + selectedyear + "-0" + selectedmonth + "-" + selectedday;
                }
                else {
                    fechapicker="" + selectedyear + "-" + selectedmonth + "-" + selectedday;
                }
                txt.setText(""+fechapicker);
                //Toast.makeText(MinutaReunionLayout.this, "La fecha seleccionada es: "+txtFechaProximaReunion.getText(), Toast.LENGTH_SHORT).show();
            }
        }, mYear, mMonth- 1, mDay);
        mDatePicker.setCancelable(false);
        mDatePicker.setTitle("Ver inventarios en históricos a partir de la fecha.");
        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDatePicker.show();
    }

    public void getFechaFinal(TextView txt){
        //Traer fecha del sistema en formato añi/mes/día
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //Definimos el valor para nuestras variables que definirán el día
        int mYear = Integer.parseInt(date.substring(0,4));
        int mMonth = Integer.parseInt(date.substring(5,7));
        int mDay = Integer.parseInt(date.substring(8,10));
        //Creamos un datepicker
        DatePickerDialog mDatePicker;
        //Iniciamos el nuevo objeto e indicamos el contexto donde se presentará el dialog
        mDatePicker = new DatePickerDialog(InventariosEnHistorico.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                //Variable donde se guarda la fecha seleccionada
                String fechapicker;
                selectedmonth = selectedmonth+1;
                //Guardamos el formato de la fecha
                if(selectedmonth<10){
                    fechapicker="" + selectedyear + "-0" + selectedmonth + "-" + selectedday;
                }
                else {
                    fechapicker="" + selectedyear + "-" + selectedmonth + "-" + selectedday;
                }
                txt.setText(""+fechapicker);
                //Toast.makeText(MinutaReunionLayout.this, "La fecha seleccionada es: "+txtFechaProximaReunion.getText(), Toast.LENGTH_SHORT).show();
            }
        }, mYear, mMonth- 1, mDay);
        mDatePicker.setCancelable(false);
        mDatePicker.setTitle("Ver inventarios en históricos a partir de la fecha.");
        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDatePicker.show();
    }

    public void getByFechas(){
        inventariosEnHistoricoRespaldo = new ArrayList<>();
        inventariosEnHistorico = new ArrayList<>();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                con = new Conexion(InventariosEnHistorico.this);
                try {
                    PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMoviul_Inventarios_Hitorico_SBuscar_SELECT ?,?,?,?");
                    stmt.setString(1, txtInvHistFInicial.getText().toString());
                    stmt.setString(2, txtInvHistFFinal.getText().toString());
                    stmt.setString(3, txtInvHistFiltrador.getText().toString());
                    if(cbFechaHist.isChecked()){
                        stmt.setInt(4, 1);
                    }else{
                        stmt.setInt(4, 0);
                    }
                    ResultSet r = stmt.executeQuery();
                    while(r.next()){
                        inventariosEnHistorico.add(new ModeloInventariosHistorico(r.getString("Folio"), r.getString("FechaInicio"),
                                r.getString("FechaFin"), r.getString("Almacen"), r.getString("Material"), r.getString("entradas"),
                                "", "", r.getString("estadoMercancia"), "", r.getString("StockTotal"), r.getString("total_registrado")));
                    }
                    if(inventariosEnHistorico.size() > 0){
                        inventariosEnHistoricoRespaldo = inventariosEnHistorico;
                        recyclerInvHistoricos.setLayoutManager(new LinearLayoutManager(contexto));
                        recyclerInvHistoricos.setAdapter(adapterInventariosEnHistorico);
                        //Toast.makeText(InventariosEnHistorico.)
                    }else{
                        Toast.makeText(InventariosEnHistorico.this, "No hay registros disponibles para esa fecha.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(InventariosEnHistorico.this, "Tamaño registros: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getInventarioKeyPressed(String filtro){
        if(inventariosEnHistoricoRespaldo.size() > 0){
            //Recorremos la lista
            inventariosEnHistorico = new ArrayList<ModeloInventariosHistorico>();
            for(int i=0; i<inventariosEnHistoricoRespaldo.size();i++){
                //Comparaamos si el campo contiene fecha ó si el campo contiene material
                //Transformamos a minusculas el material y el filtro para que no importe la mayuscula o minusculas
                if(inventariosEnHistoricoRespaldo.get(i).getFecha().contains(filtro) || inventariosEnHistoricoRespaldo.get(i).getMaterial().toLowerCase().contains(filtro.toLowerCase()) || inventariosEnHistoricoRespaldo.get(i).getAlmacen().toLowerCase().contains(filtro.toLowerCase())){
                    //Agregamos el registro a nuestra lista duplicada
                    inventariosEnHistorico.add(inventariosEnHistoricoRespaldo.get(i));
                }
            }
            //Iniciamos un nuevo adaptador con la copia de la lista cargada
            adapterInventariosEnHistorico.notifyDataSetChanged();
        }
    }

    public class AdapterInventariosEnHistorico extends RecyclerView.Adapter<AdapterInventariosEnHistorico.AdapterInventariosEnHistoricoHolder>{
        @NonNull
        @Override
        public AdapterInventariosEnHistoricoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterInventariosEnHistoricoHolder(getLayoutInflater().inflate(R.layout.inventario_a_en_historico_items, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterInventariosEnHistoricoHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return inventariosEnHistorico.size();
        }

        class AdapterInventariosEnHistoricoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView lblInvHistFecha, lblInvHistFechaFin, lblInvHistAlmacen, lblInvHistMaterial, lblInvHistEntradas, lblInvHistConsecutivo;
            ImageView imgInvHistEstadoMercancia;
            CardView cardviewInvHistDetalles;
            public AdapterInventariosEnHistoricoHolder(@NonNull View itemView) {
                super(itemView);
                lblInvHistFecha = itemView.findViewById(R.id.lblInvHistFecha);
                lblInvHistFechaFin = itemView.findViewById(R.id.lblInvHistFechaFin);
                lblInvHistAlmacen = itemView.findViewById(R.id.lblInvHistAlmacen);
                lblInvHistMaterial = itemView.findViewById(R.id.lblInvHistMaterial);
                lblInvHistEntradas = itemView.findViewById(R.id.lblInvHistEntradas);
                imgInvHistEstadoMercancia = itemView.findViewById(R.id.imgInvHistEstadoMercancia);
                lblInvHistConsecutivo = itemView.findViewById(R.id.lblInvHistConsecutivo);
                cardviewInvHistDetalles = itemView.findViewById(R.id.cardviewInvHistDetalles);

                cardviewInvHistDetalles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(InventariosEnHistorico.this, InventarioAEnHistoricosDetalle.class);
                        intent.putExtra("Folio", inventariosEnHistorico.get(getAdapterPosition()).getFolio());
                        intent.putExtra("Almacen", inventariosEnHistorico.get(getAdapterPosition()).getAlmacen());
                        intent.putExtra("Material", inventariosEnHistorico.get(getAdapterPosition()).getMaterial());
                        intent.putExtra("Usuario", inventariosEnHistorico.get(getAdapterPosition()).getUsuario());
                        intent.putExtra("Entradas", inventariosEnHistorico.get(getAdapterPosition()).getEntradas());
                        intent.putExtra("Fecha", inventariosEnHistorico.get(getAdapterPosition()).getFecha());
                        startActivity(intent);
                    }
                });
            }

            public void printAdapter(int position){
                //lblInvHistFecha.setText(inventariosEnHistorico.get(position));
                lblInvHistConsecutivo.setText(""+(position+1));
                lblInvHistFecha.setText(inventariosEnHistorico.get(position).getFecha());
                lblInvHistAlmacen.setText(inventariosEnHistorico.get(position).getAlmacen());
                lblInvHistMaterial.setText(inventariosEnHistorico.get(position).getMaterial());
                lblInvHistEntradas.setText(inventariosEnHistorico.get(position).getEntradas());
                if(Integer.parseInt(inventariosEnHistorico.get(position).getEstadoMercancia()) == 0){
                    imgInvHistEstadoMercancia.setImageResource(R.drawable.correcto);
                }else{
                    imgInvHistEstadoMercancia.setImageResource(R.drawable.snakerojo);
                }
                lblInvHistFechaFin.setText(inventariosEnHistorico.get(position).getUsuario());
            }

            @Override
            public void onClick(View v){

            }
        }
    }

}