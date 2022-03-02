package com.example.operacionesivra.Vistas.Inventarios.Vistas.EnHistorico.DetallesHistorico;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Modelos.ModeloInventariosHistoricoDetalle;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import harmony.java.awt.Color;

public class InventarioAEnHistoricosDetalle extends AppCompatActivity {
    //Status loading
    public int loading = 0;
    //RecyclerView
    RecyclerView recyvlerInvHistDetalle;
    //FileUri
    File fileUri;
    //Contexto
    Context contexto = this;
    ArrayList<ModeloInventariosHistoricoDetalle> detalles = new ArrayList<ModeloInventariosHistoricoDetalle>();
    AdapterDetalleHistorico adapterDetalleHistorico = new AdapterDetalleHistorico();
    //Variables principales
    String Folio, Fecha, Almacen, Material, Usuario, Entradas, sistema, HInicio, HFinal;
    float Total = 0;
    //Labels
    TextView lblInvHistDetFecha, lblInvHistDetAlmacen, lblInvHistDetMaterial, lblInvHistDetEntradas, lblInvHistDetTotal, lblInvHistDetSistema, lblInvHistDetDiferencia;
    //Buttons
    Button btnInvHistDetRegresar, btnInvHistDetReporte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_a_en_historicos_detalle);
        //RecyclerView
        recyvlerInvHistDetalle = findViewById(R.id.recyclerInvHistDetalle);
        //TextView
        lblInvHistDetFecha = findViewById(R.id.lblInvHistDetFecha);
        lblInvHistDetAlmacen = findViewById(R.id.lblInvHistDetAlmacen);
        lblInvHistDetMaterial = findViewById(R.id.lblInvHistDetAlMaterial);
        //lblInvHistDetUsuario = findViewById(R.id.lblInvHistDetUsuarui);
        lblInvHistDetEntradas = findViewById(R.id.lblInvHistDetEntradas);
        lblInvHistDetTotal = findViewById(R.id.lblInvHistDetTotal);
        lblInvHistDetSistema = findViewById(R.id.lblInvHistDetSistema);
        lblInvHistDetDiferencia = findViewById(R.id.lblInvHistDetDiferencia);
        fullVariablesPrincipales();
        lblInvHistDetFecha.setText(Fecha);
        lblInvHistDetAlmacen.setText(Almacen);
        lblInvHistDetMaterial.setText(Material);
        lblInvHistDetEntradas.setText(Entradas);

        //lblInvHistDetUsuario.setText(Usuario);
        lblInvHistDetTotal.setText(""+0);

        getDetalles();
        getTotalRegistrado();

        //Buttons
        btnInvHistDetReporte = (Button) findViewById(R.id.btnInvHistDetReporte);
        btnInvHistDetRegresar = (Button) findViewById(R.id.btnInvHistDetRegresar);
        btnInvHistDetRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnInvHistDetReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogOpcionesReporte();
            }
        });
    }

    //Pantalla cargando
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    public void fullVariablesPrincipales(){
        Folio = getIntent().getStringExtra("Folio");
        Fecha = getIntent().getStringExtra("Fecha");
        Almacen = getIntent().getStringExtra("Almacen");
        Material = getIntent().getStringExtra("Material");
        Usuario = getIntent().getStringExtra("Usuario");
        Entradas = getIntent().getStringExtra("Entradas");
        HInicio = getIntent().getStringExtra("HInicio");
        HFinal = getIntent().getStringExtra("HFin");
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
                    .setMessage("¡Reporte creado con éxito! guardado en almacenamiento interno/documents/Reportes por Material/")
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

    public void getDetalles(){
                try {
                    Conexion con = new Conexion(contexto);
                    Statement statement = con.conexiondbImplementacion().createStatement();
                    String query = "SELECT Usuario, Codigo_unico, Cantidad, Ubicacion, Total_registrado, StockTotal, Observaciones, CONVERT(varchar,fecha,5) as fecha,CONVERT(varchar,Terminado,5) as Terminado, CONVERT(char(5), horainicio, 108) as horainicio, CONVERT(char(5), horafin, 108) as horafin FROM Movil_Reporte WHERE Folio = '"+Folio+"' AND historico = 1 AND Status = 1 ORDER BY horainicio ASC";
                    ResultSet r = statement.executeQuery(query);
                    //Recorremos nuestro resultset si es que trae información
                    while(r.next()){
                        sistema = r.getString("StockTotal");
                        detalles.add(new ModeloInventariosHistoricoDetalle(r.getString("Usuario"), r.getString("Codigo_unico"), r.getString("Cantidad"),
                                r.getString("Ubicacion"), r.getString("Total_registrado"), r.getString("Observaciones"), r.getString("fecha"), r.getString("Terminado"), r.getString("horainicio"), r.getString("horafin")));
                    }
                    recyvlerInvHistDetalle.setLayoutManager(new LinearLayoutManager(contexto));
                    recyvlerInvHistDetalle.setAdapter(adapterDetalleHistorico);
                } catch (SQLException throwables) {
                    new MaterialAlertDialogBuilder(contexto)
                            .setMessage(throwables.getMessage())
                            .show();
                }

    }

    public void generateReporte(int status){
                Document documento = new Document();
                Conexion conexion = new Conexion(contexto);
                String horainicio = "", horafin = "";
                try {
                    Statement statement = conexion.conexiondbImplementacion().createStatement();
                    String query = "SELECT CONVERT(VARCHAR, horainicio, 108) as horainicio, CONVERT(VARCHAR, horafin, 108) as horafin FROM Movil_Reporte WHERE Folio = '"+Folio+"' group by horainicio, horafin;";
                    ResultSet r = statement.executeQuery(query);
                    while (r.next()){
                        horainicio = r.getString("horainicio");
                        horafin = r.getString("horafin");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                try {
                    String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    documento.setMargins(-50f, -50f, 5f, 5f);
                    File file = crearFichero("Reporte de "+Material.replace("-", "_")+" "+Fecha.replace("/", " ")+".pdf");
                    FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

                    documento.setPageSize(PageSize.LEGAL);
                    PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

                    documento.open();
                    Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, 18, Font.BOLD, Color.BLACK);
                    Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD, Color.BLACK);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.shimaco);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image imagen = Image.getInstance(stream.toByteArray());
                    PdfPTable a = new PdfPTable(3);
                    PdfPCell cellencabezado = new PdfPCell(new Phrase("\nReporte de Inventario", fuente));
                    cellencabezado.setHorizontalAlignment(Element.ALIGN_CENTER);
                    a.setTotalWidth(1000);
                    a.addCell(imagen);
                    a.addCell(cellencabezado);
                    PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha:" +Fecha+"\n"+horainicio+" - "+horafin, fuentefecha));
                    cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
                    a.addCell(cellencabezadofecha);


                    PdfPTable table = new PdfPTable(5);
                    table.setHorizontalAlignment(Cell.ALIGN_CENTER);

                    String restanteString = "Cantidad Faltante: ";
                    documento.add(a);
                    documento.add(new Paragraph("\n"));
                    documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAlmacén: " +Almacen+"\n\n"));
                    documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tMaterial: " +Material+"\n\n"));
                    //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tComprometido en sistema: " + comprometido.getText().toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\tDisponible en sistema: " + disponible.getText().toString()));
                    documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tSistema (registrado): "+lblInvHistDetTotal.getText().toString()+"\n\n"));
                    documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistente: "+sistema+"\n\n"));
                    documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia "+lblInvHistDetDiferencia.getText().toString()+"\n\n"));
                    //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + restanteString + "" + restante.toString().replace("-", " ")));

                    documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tRegistros Realizados: "+Entradas+"\n\n"));

                    //Encabezado
                    PdfPTable encabezado = new PdfPTable(5);

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
                    PdfPCell cell5t = new PdfPCell(new Phrase("Incidencia"));
                    cell5t.setHorizontalAlignment(Element.ALIGN_CENTER);
                    encabezado.addCell(cell5t);


                    for (int i = 0; i < detalles.size(); i++) {
                        PdfPCell cell = new PdfPCell(new Phrase(detalles.get(i).getCantidad()));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                        PdfPCell cel2 = new PdfPCell(new Phrase(detalles.get(i).getLongitud()));
                        cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cel2);
                        PdfPCell cel3 = new PdfPCell(new Phrase(detalles.get(i).getMaterialregistrado()));
                        cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cel3);
                        PdfPCell cel4 = new PdfPCell(new Phrase(detalles.get(i).getUbicacion()));
                        cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cel4);
                        if(detalles.get(i).getIncidencias().equals("")){
                            PdfPCell cel5 = new PdfPCell(new Phrase("Sin incidencia"));
                            cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cel5);
                        }else{
                            PdfPCell cel5 = new PdfPCell(new Phrase(detalles.get(i).getIncidencias()));
                            cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(cel5);
                        }
                    }

                    documento.add(encabezado);
                    documento.add(table);

                    documento.add(new Paragraph("\n\n\n"));
                    documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t______________________________________\n"));
                    documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tInventario realizado por: " + Usuario));
                    fileUri = file;
                } catch (DocumentException e) {
                    Toast.makeText(InventarioAEnHistoricosDetalle.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(InventarioAEnHistoricosDetalle.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                } finally {
                    documento.close();
                    if(status == 1){

                    }else if(status == 2){
                        printPDF(fileUri);
                        Toast.makeText(contexto, "Abriendo vista previa del archivo...", Toast.LENGTH_SHORT).show();
                    }
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
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Reportes por Material");
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

    public void printPDF(File file){
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try{
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(contexto, file.getAbsolutePath());
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        }catch (Exception e){
            Toast.makeText(contexto, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getTotalRegistrado(){
        try {
            //Variable para guardar el total
            float totalRegistrado = 0;
            //Recorremos el arreglo
            for(int i=0; i<detalles.size(); i++){
                //Sumamos el total y guardamos en la variable
                totalRegistrado = totalRegistrado + (Float.parseFloat(detalles.get(i).getLongitud()) * Float.parseFloat(detalles.get(i).getCantidad()));
            }
            lblInvHistDetTotal.setText(""+totalRegistrado);
            lblInvHistDetSistema.setText(sistema);
            lblInvHistDetDiferencia.setText(""+(Float.parseFloat(lblInvHistDetSistema.getText().toString()) - Float.parseFloat(lblInvHistDetTotal.getText().toString())));
        }catch (Exception e){
            System.out.println("Error en total registrado: "+e.getMessage());
        }
    }

    public class AdapterDetalleHistorico extends RecyclerView.Adapter<AdapterDetalleHistorico.AdapterDetalleHistoricoHolder>{
        @NonNull
        @Override
        public AdapterDetalleHistoricoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterDetalleHistoricoHolder(getLayoutInflater().inflate(R.layout.inventario_a_en_historicos_detalle_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterDetalleHistoricoHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            //Retornas tamaño lista
            //getTotalRegistrado();
            return detalles.size();
        }

        class AdapterDetalleHistoricoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView lblInvHistItemUbicacion, lblInvHistItemCantidad, lblInvHistItemContenido, lblInvHisDetItemUsuario, lblInvHistItemIncidencias, lblInvHisDetItemConsecutivo;
            TextView lblInvHisDetItemFI, lblInvHisDetItemFF;
            ImageView imgInvHistDetHistorico;
            public AdapterDetalleHistoricoHolder(@NonNull View itemView) {
                super(itemView);
                lblInvHisDetItemConsecutivo = itemView.findViewById(R.id.lblInvHisDetItemConsecutivo);
                lblInvHistItemUbicacion = itemView.findViewById(R.id.lblInvHisDetItemUbicacion);
                lblInvHistItemContenido = itemView.findViewById(R.id.lblInvHisDetItemContenido);
                lblInvHistItemCantidad = itemView.findViewById(R.id.lblInvHisDetItemCantidad);
                lblInvHisDetItemUsuario = itemView.findViewById(R.id.lblInvHisDetItemUsuario);
                lblInvHistItemIncidencias = itemView.findViewById(R.id.lblInvHisDetItemIncidencias);
                lblInvHisDetItemFI = itemView.findViewById(R.id.lblInvHisDetItemFI);
                lblInvHisDetItemFF = itemView.findViewById(R.id.lblInvHisDetItemFF);
                imgInvHistDetHistorico = itemView.findViewById(R.id.imgInvHistDetHistorico);
            }

            public void printAdapter(int position){
                lblInvHisDetItemConsecutivo.setText(""+(position+1));
                lblInvHistItemUbicacion.setText(detalles.get(position).getUbicacion());
                lblInvHistItemContenido.setText(detalles.get(position).getLongitud());
                lblInvHistItemCantidad.setText(detalles.get(position).getCantidad());
                lblInvHisDetItemUsuario.setText(detalles.get(position).getUsuario());
                if(detalles.get(position).getIncidencias().equals("")){
                    lblInvHistItemIncidencias.setText("Sin incidenias");
                }else{
                    lblInvHistItemIncidencias.setText(detalles.get(position).getIncidencias());
                    imgInvHistDetHistorico.setImageResource(R.drawable.snakerojo);
                }
                lblInvHisDetItemFI.setText(detalles.get(position).getFechaInico()+" "+detalles.get(position).getHoraInicio());
                lblInvHisDetItemFF.setText(detalles.get(position).getFechaFinal()+" "+detalles.get(position).getHoraFin());
            }

            @Override
            public void onClick(View v){

            }
        }

    }

}