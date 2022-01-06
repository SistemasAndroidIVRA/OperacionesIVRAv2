package com.example.operacionesivra.Inventarios.Vistas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Inventarios.Models.InventariosDetalle;
import com.example.operacionesivra.Inventarios.Models.Ubicacion;
import com.example.operacionesivra.Inventarios.Models.UbicacionContenido;
import com.example.operacionesivra.Inventarios.Vistas.EnHistorico.DetallesHistorico.PdfDocumentAdapter;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
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
import java.util.UUID;

public class InventariosRegistro extends AppCompatActivity {
    //Status loading
    public int loading = 0;
    public int ajuste = 0;
    //Variables a nivel de clase
    String  material = "", unidadMedida = "", stockTotal = "", almacen ="", fecha = "", horaInicial = "", productoID = "";
    int estadoBloqueo = 0;
    Conexion con;
    //FileUri
    File fileUri;
    //Contexto contexto
    Context contexto = this;
    //Spinner registro ubicaciones
    Spinner spinnerUbicacionesRegistro;
    //Labels generales
    TextView lblInvTotalRegistros, lblInvTotalRegistrado, lblInvDiferencia;
    //EditText generales
    public EditText txtInvCodEscanear;
    //Array Detalles
    ArrayList<InventariosDetalle> inventariosDetalles = new ArrayList<>();
    AdapterDetalle adapterDetalle;
    RecyclerView recyclerInvDetalles;
    //Array ubicaciones
    ArrayList<UbicacionContenido> ubicacionContenidos = new ArrayList<>();
    //Ubicaciones y ids
    ArrayList<Ubicacion> ubicaciones;
    //Información usuario
    String usuario, pass;
    //EditText
    EditText txtUbicacionRegistro;
    //TextViews
    TextView lblInvFecha, lblInvAlmacen, lblInvMaterial, lblInvUniMedida, lblInvSistema, lblAjusteInventario;
    //Botones
    Button btnInvAgregar, btnInvEliminarItem, btnInvGuardar, btnInvBloqueado, btnInvRegresar;
    //UUID
    public String uuid = UUID.randomUUID().toString().replace("-", "");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Orientación dispositivo
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_a_registro);
        //Información usuario
        usuario = getIntent().getStringExtra("usuario");
        pass = getIntent().getStringExtra("pass");
        //Traer info intents
        getInfoIntents();
        //Traer info fecha hora
        startFechaHora();
        //Llenar array ubicaciones
        spinnerUbicacionesRegistro = findViewById(R.id.spinnerUbicacionesRegistro);
        ubicaciones = fullArrayUbicaciones();
        llenarSpinnerUbicacionesRegistro(ubicaciones);
        //EditText
        txtUbicacionRegistro = findViewById(R.id.txtUbicacionRegistro);
        //TextViews
        lblAjusteInventario = findViewById(R.id.lblAjusteInventario);
        if(ajuste == 1){
            lblAjusteInventario.setText("Complemento de inventario");
        }
        lblInvFecha = findViewById(R.id.lblInvFecha);
        lblInvFecha.setText(""+fecha);
        lblInvAlmacen = findViewById(R.id.lblInvAlmacen);
        lblInvAlmacen.setText(""+almacen);
        lblInvMaterial = findViewById(R.id.lblInvMaterial);
        lblInvMaterial.setText(""+material);
        lblInvUniMedida = findViewById(R.id.lblInvUniMedida);
        lblInvUniMedida.setText(""+unidadMedida);
        lblInvSistema = findViewById(R.id.lblInvSistema);
        lblInvSistema.setText(""+stockTotal);
        lblInvTotalRegistros = findViewById(R.id.lblInvTotalRegistros);
        lblInvTotalRegistrado = findViewById(R.id.lblInvTotalRegistrado);
        lblInvDiferencia = findViewById(R.id.lblInvDiferencia);
        txtInvCodEscanear = findViewById(R.id.txtInvCodEscanear);
        //Recycler
        adapterDetalle = new AdapterDetalle();
        recyclerInvDetalles = findViewById(R.id.recyclerInvDetalles);
        recyclerInvDetalles.setLayoutManager(new LinearLayoutManager(this));
        recyclerInvDetalles.setAdapter(adapterDetalle);
        //Buttons
        btnInvRegresar = findViewById(R.id.btnInvRegresar);
        btnInvGuardar = findViewById(R.id.btnInvGuardar);
        btnInvAgregar = findViewById(R.id.btnInvAgregar);
        btnInvEliminarItem = findViewById(R.id.btnInvEliminarItem);
        btnInvBloqueado = findViewById(R.id.btnInvBloqueado);
        //EditText acción
        txtUbicacionRegistro.setOnKeyListener((view, i, keyEvent) -> {
            if(txtUbicacionRegistro.getText().toString().equals("")){
                //Mostrar todas las ubicaciones
                llenarSpinnerUbicacionesRegistro(ubicaciones);
            }else{
                String filtro = txtUbicacionRegistro.getText().toString().toUpperCase();
                ArrayList<Ubicacion> ubicacionesFiltradas = searchUbicaciones(filtro);
                llenarSpinnerUbicacionesRegistro(ubicacionesFiltradas);
            }
            return false;
        });
        //Botón regresar
        btnInvRegresar.setOnClickListener(view -> {
            if(inventariosDetalles.size() > 0){
                new MaterialAlertDialogBuilder(contexto)
                        .setTitle("!Cuidado!")
                        .setIcon(R.drawable.confirmacion)
                        .setMessage("¿Qué deseas hacer?")
                        .setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //No hacer nada
                            }
                        })
                        .setNegativeButton("Salir y guardar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Guardar en la BD
                                guardarInventario();
                            }
                        })
                        .setNeutralButton("Salir sin guardar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir sin guardar
                                loading = 4;
                                loadinglauncher();
                                //despausarProducto();
                                finish();
                            }
                        })
                        .show();
            }else{
                //despausarProducto();
                loading = 4;
                loadinglauncher();
                finish();
            }
        });
        //Botón agregar
        btnInvAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Invocar método agregar
                String codigoMaterial = txtInvCodEscanear.getText().toString();
                if(codigoMaterial.equals("")){
                    txtInvCodEscanear.setHint("Campo requerido");
                    txtInvCodEscanear.setHintTextColor(Color.parseColor("#FF0000"));
                }else{
                    loading = 1;
                    loadinglauncher();
                    //addCodigoEscaneado(codigoMaterial);
                    txtInvCodEscanear.setText("");
                    txtInvCodEscanear.setHint("Leer código");
                    txtInvCodEscanear.setHintTextColor(Color.parseColor("#8a8a8a"));
                }
            }
        });

        btnInvGuardar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(ajuste == 1){
                    guardarInventario();
                }else{
                    guardarInventario();
                }
                return false;
            }
        });
        //TxtOnListener
        txtInvCodEscanear.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                char [] tamaño = txtInvCodEscanear.getText().toString().toCharArray();
                if(tamaño.length == 10){
                    loading = 1;
                    loadinglauncher();
                    //addCodigoEscaneado(codigoMaterial);
                    txtInvCodEscanear.setFocusable(true);
                    txtInvCodEscanear.requestFocus();
                    txtInvCodEscanear.setText("");
                    txtInvCodEscanear.setHint("Leer código");
                    txtInvCodEscanear.setHintTextColor(Color.parseColor("#8a8a8a"));
                }
                return false;
            }
        });
        btnInvEliminarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemInventario();
            }
        });

        btnInvBloqueado.setOnClickListener(view -> {
            if(estadoBloqueo == 1){
                loading = 4;
                loadinglauncher();
                //despausarProducto();
                btnInvBloqueado.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.icono_despausar, 0);
                btnInvBloqueado.setText("BLOQUEAR");
                estadoBloqueo = 0;
            }else{
                loading = 3;
                loadinglauncher();
                //pausarProducto();
                btnInvBloqueado.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.icono_pausar, 0);
                btnInvBloqueado.setText("DESBLOQUEAR");
                estadoBloqueo = 1;
            }
        });

        spinnerUbicacionesRegistro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Ubicacion ubicacion = (Ubicacion) spinnerUbicacionesRegistro.getSelectedItem();
                if(!ubicacion.toString().equals("")){
                    txtUbicacionRegistro.setText(""+ubicacion.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(contexto, "Acción no aplica para este proceso.", Toast.LENGTH_SHORT).show();
    }

    //Pantalla de cargando
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    //Método para guardar la información
    public void guardarInventario(){
        new MaterialAlertDialogBuilder(InventariosRegistro.this)
                .setTitle("¡Confirmación!")
                .setIcon(R.drawable.confirmacion)
                .setMessage("Seguro que desea guardar la información?")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Cancelar
                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Realizar transacciones
                        //Generar código nuevo
                        if(comprobarUbicaciones() == 1){
                            loading = 2;
                            loadinglauncher();
                            //generateEtiquetasViejas();
                            //subirtablaDB();
                        }else{
                            new MaterialAlertDialogBuilder(contexto)
                                    .setTitle("¡Error!")
                                    .setIcon(R.drawable.confirmacion)
                                    .setMessage("No podemos guardar la informacíón, asegurese de que cada material tenga asignada una ubicación.")
                                    .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //No hacer nada
                                        }
                                    })
                                    .show();
                        }
                    }
                })
                .show();
    }

    //Método que comprueba si los materiales tienen una ubicación guardada
    public int comprobarUbicaciones(){
        for(int i=0; i<inventariosDetalles.size(); i++){
            if(inventariosDetalles.get(i).getUbicacion().equals("") || inventariosDetalles.get(i).getUbicacion().equals(null)){
                return 0;
            }
        }
        return 1;
    }

    //Método para pausar el producto
    public void pausarProducto(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                con = new Conexion(contexto);
                try {
                    PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Producto_Pausar_UPDATE ?");
                    stmt.setString(1, productoID);
                    stmt.execute();
                    Toast.makeText(contexto, "¡Material bloqueado exitosamente!", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Método para despausar el producto
    public void despausarProducto(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                con = new Conexion(contexto);
                try {
                    PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Producto_Despausar_UPDATE ?");
                    stmt.setString(1, productoID);
                    stmt.execute();
                    Toast.makeText(contexto, "¡Producto desbloqueado exitosamente!", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Sube la lista a la base de datos
    public void subirtablaDB() {
        try {
            Conexion conexionService = new Conexion(this);
            //conexionService.conexiondbImplementacion().setAutoCommit(false);
            //comprobaciondeestado = 0;

            String horafin = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            for (int i = 0; i < inventariosDetalles.size(); i++) {
               PreparedStatement var = conexionService.conexiondbImplementacion().prepareCall("PMovil_Registrar_Inventario ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");

                        var.setString(1, inventariosDetalles.get(i).getCantidad());
                        var.setString(2, inventariosDetalles.get(i).getLongitud());
                        var.setString(3, ""+Float.parseFloat(inventariosDetalles.get(i).getCantidad()) * Float.parseFloat(inventariosDetalles.get(i).getLongitud()));
                        var.setString(4, inventariosDetalles.get(i).getUbicacion());
                        var.setString(5, inventariosDetalles.get(i).getEstado());
                        var.setString(6, usuario);
                        var.setString(7, uuid);
                        var.setString(8, material);
                        var.setString(9, fecha);
                        var.setString(10, stockTotal);
                        var.setString(11, almacen);
                        var.setString(12, inventariosDetalles.get(i).getIncidencia());
                        var.setString(13, horaInicial);
                        var.setString(14, horafin);
                        var.setString(15, inventariosDetalles.get(i).getProductoID());
                        var.setString(16, inventariosDetalles.get(i).getUbicacionId());
                        var.setInt(17, 0);
                        var.execute();
            }
            loading = 4;
            loadinglauncher();
            //despausarProducto();
            //conexionService.conexiondbImplementacion().commit();
            //comprobaciondeestado = 2;
            new MaterialAlertDialogBuilder(InventariosRegistro.this)
                    .setTitle("¡Éxito!")
                    .setIcon(R.drawable.correcto)
                    .setMessage("Se han guardado "+inventariosDetalles.size()+" registros, ahora puede consultarlos en el módulo 'Inventarios en proceso'.")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Cerrar intent
                            if(fileUri == null){
                                finish();
                            }
                        }
                    })
                    .show();
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(InventariosRegistro.this)
                    .setTitle("¡Error!")
                    .setMessage("Compruebe su conexión a internet "+e.getMessage())
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //
                        }
                    })
                    .show();
            //comprobaciondeestado = 1;
            //conexionService.conexiondbImplementacion().rollback();
        }
    }

    //Borra un registro
    public void deleteItemInventario(){
        ArrayList<InventariosDetalle> inventariosDetalelsTemp = new ArrayList<>();
        int hayEliminar = 0;
        if(inventariosDetalles.size() == 0){
            new MaterialAlertDialogBuilder(InventariosRegistro.this)
                    .setTitle("¡Error!")
                    .setIcon(R.drawable.confirmacion)
                    .setMessage("No hay registros aún, imposible realizar la acción.")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //No hacer nada
                        }
                    })
                    .show();
        }else{
            //for
            for(int i=0; i<inventariosDetalles.size(); i++){
                if(inventariosDetalles.get(i).getIsSelected() == 0){
                    inventariosDetalelsTemp.add(inventariosDetalles.get(i));
                }else{
                    hayEliminar = 1;
                }
            }
            if(hayEliminar == 1){
                inventariosDetalles = inventariosDetalelsTemp;
                adapterDetalle.notifyDataSetChanged();
                new MaterialAlertDialogBuilder(InventariosRegistro.this)
                        .setTitle("¡Éxito!")
                        .setIcon(R.drawable.correcto)
                        .setMessage("Acción realizada exitosamente.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //No hacer nada
                            }
                        })
                        .show();
            }else{
                new MaterialAlertDialogBuilder(InventariosRegistro.this)
                        .setTitle("¡Error!")
                        .setIcon(R.drawable.snakerojo)
                        .setMessage("No hay registros seleccionados para eliminar.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //No hacer nada
                            }
                        })
                        .show();
            }
        }
    }

    //Metodo para agregar un material escaneado
    public void addCodigoEscaneado(String codigoMaterial){
        Ubicacion ubicacionSeleccionada = (Ubicacion) spinnerUbicacionesRegistro.getSelectedItem();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Abrir conexión
                con = new Conexion(InventariosRegistro.this);
                //Try catch para cachar errores
                try {
                    //Consultar productoID
                    PreparedStatement stmtIni = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Registro_Codigo_Validate ?");
                    stmtIni.setString(1, codigoMaterial);
                    ResultSet r1 = stmtIni.executeQuery();
                    String productoBD = "";
                    if(r1.next()){
                        productoBD = r1.getString("ProductoID");
                    }
                    //Empezar comparaciones
                    if(productoBD.equals(productoID)){
                        //Empezar enunciadp
                        Statement stmt = con.conexiondbImplementacion().createStatement();
                        //Sintaxys SQL
                        String query = "SELECT DISTINCT U.Nombre as Ubicacion, U.UbicacionID as UbicacionID, CG.Longitud as Contenido, P.ProductoID as ProductoID FROM Ubicacion_Producto UP \n" +
                                "INNER JOIN Producto P ON (UP.ProductoID = P.ProductoID) \n" +
                                "INNER JOIN Ubicacion U ON(UP.UbicacionID = U.UbicacionID)\n" +
                                "INNER JOIN CodGeneral CG ON(P.ProductoID = CG.ProductoID) \n" +
                                "WHERE CG.Codigo = '"+codigoMaterial+"' AND U.Nombre LIKE 'G1%';";
                        //Ejecutar query
                        ResultSet r = stmt.executeQuery(query);
                        //Recorrer result set
                        while(r.next()){
                            ubicacionContenidos.add(new UbicacionContenido(r.getString("UbicacionID"), r.getString("Ubicacion"), r.getString("Contenido"), r.getString("ProductoID")));
                        }

                        if(ubicacionContenidos.size() >= 1){
                            if(ajuste == 1){
                                InventariosDetalle registro = new InventariosDetalle("", "1", ubicacionContenidos.get(0).getContenido(), ""+ubicacionSeleccionada.toString(), "Complemento de inventario", "0", ubicacionSeleccionada.getUbicacionId(), ubicacionContenidos.get(0).getProductoID(), "", "", 0, 0);
                                inventariosDetalles.add(registro);
                            }else{
                                InventariosDetalle registro = new InventariosDetalle("", "1", ubicacionContenidos.get(0).getContenido(), ""+ubicacionSeleccionada.toString(), "", "0", ubicacionSeleccionada.getUbicacionId(), ubicacionContenidos.get(0).getProductoID(), "", "", 0, 0);
                                inventariosDetalles.add(registro);
                            }
                            try {
                                adapterDetalle.notifyDataSetChanged();
                                Toast.makeText(InventariosRegistro.this, "¡Registrado exitosamente! ETIQUETA NORMAL", Toast.LENGTH_SHORT).show();
                                soundCorrecto();
                                ubicacionContenidos = new ArrayList<>();
                            }catch (Exception e){
                                Toast.makeText(InventariosRegistro.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }else if(ubicacionContenidos.size() == 0) {
                            //Empezar enunciadp
                            Statement stmtV1 = con.conexiondbImplementacion().createStatement();
                            //Sintaxys SQL
                            String queryV1 = "SELECT Longitud, ProductoID FROM CodGeneral WHERE Codigo = '" + codigoMaterial + "';";
                            //Ejecutar query
                            ResultSet rV1 = stmtV1.executeQuery(queryV1);
                            //Recorrer result set
                            while (rV1.next()) {
                                ubicacionContenidos.add(new UbicacionContenido("", "", rV1.getString("Longitud"), rV1.getString("ProductoID")));
                            }
                            if(ubicacionContenidos.size()==0){
                                //Buscar en viejas como ultima opción
                                //Empezar enunciadp
                                Statement stmtV = con.conexiondbImplementacion().createStatement();
                                //Sintaxys SQL
                                String queryV = "SELECT Codigo, Longitud, ProductoID FROM CodUnico WHERE Codigo = '"+codigoMaterial+"';";
                                //Ejecutar query
                                ResultSet rV = stmtV.executeQuery(queryV);
                                //Recorrer result set
                                while(rV.next()){
                                    ubicacionContenidos.add(new UbicacionContenido("", "", rV.getString("Longitud"), rV.getString("ProductoID"), rV.getString("Codigo"), ""));
                                }
                                //Buscar ubicaciones
                                if(ubicacionContenidos.size() > 0){
                                    if(ajuste == 1){
                                        InventariosDetalle registro = new InventariosDetalle("", "1", ubicacionContenidos.get(0).getContenido(), ""+ubicacionSeleccionada.toString(), "Complemento de inventario", "0", ubicacionSeleccionada.getUbicacionId(), ubicacionContenidos.get(0).getProductoID(), ubicacionContenidos.get(0).getCodigoViejo(), ubicacionContenidos.get(0).getCodigoNuevo(), 0, 1);
                                        inventariosDetalles.add(registro);
                                    }else{
                                        InventariosDetalle registro = new InventariosDetalle("", "1", ubicacionContenidos.get(0).getContenido(), ""+ubicacionSeleccionada.toString(), "", "0", ubicacionSeleccionada.getUbicacionId(), ubicacionContenidos.get(0).getProductoID(), ubicacionContenidos.get(0).getCodigoViejo(), ubicacionContenidos.get(0).getCodigoNuevo(), 0, 1);
                                        inventariosDetalles.add(registro);
                                    }
                                    try {
                                        adapterDetalle.notifyDataSetChanged();
                                        Toast.makeText(InventariosRegistro.this, "¡Registrado exitosamente! etiqueta vieja", Toast.LENGTH_SHORT).show();
                                        soundCorrecto();
                                        ubicacionContenidos = new ArrayList<>();
                                    }catch (Exception e){
                                        Toast.makeText(InventariosRegistro.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else{
                                ubicacionContenidos = new ArrayList<>();
                                //Empezar enunciadp
                                Statement stmtV = con.conexiondbImplementacion().createStatement();
                                //Sintaxys SQL
                                String queryV = "SELECT Codigo, Longitud, ProductoID FROM CodGeneral WHERE Codigo = '"+codigoMaterial+"';";
                                //Ejecutar query
                                ResultSet rV = stmtV.executeQuery(queryV);
                                //Recorrer result set
                                while(rV.next()){
                                    ubicacionContenidos.add(new UbicacionContenido("", "", rV.getString("Longitud"), rV.getString("ProductoID"), rV.getString("Codigo"), ""));
                                }
                                //InventariosDetalle registro = new InventariosDetalle("", "1", ubicacionContenidos.get(0).getContenido(), "" + ubicacionContenidos.get(0).getUbicacion(), "", "0", ubicacionContenidos.get(0).getUbicacionID(), ubicacionContenidos.get(0).getProductoID(), "", "", 0, 1);
                                if(ajuste == 1){
                                    InventariosDetalle registro = new InventariosDetalle("", "1", ubicacionContenidos.get(0).getContenido(), "" + ubicacionSeleccionada.toString(), "Complemento de inventario", "0", ubicacionSeleccionada.getUbicacionId(), ubicacionContenidos.get(0).getProductoID(), ubicacionContenidos.get(0).getCodigoViejo(), "", 0, 1);
                                    inventariosDetalles.add(registro);
                                }else{
                                    InventariosDetalle registro = new InventariosDetalle("", "1", ubicacionContenidos.get(0).getContenido(), "" + ubicacionSeleccionada.toString(), "", "0", ubicacionSeleccionada.getUbicacionId(), ubicacionContenidos.get(0).getProductoID(), ubicacionContenidos.get(0).getCodigoViejo(), "", 0, 1);
                                    inventariosDetalles.add(registro);
                                }
                                try {
                                    adapterDetalle.notifyDataSetChanged();
                                    soundCorrecto();
                                    Toast.makeText(InventariosRegistro.this, "¡Registrado exitosamente! ETIQUETA NORMAL sin ubicación"+ubicacionContenidos.get(0).getCodigoViejo(), Toast.LENGTH_SHORT).show();
                                    ubicacionContenidos = new ArrayList<>();
                                }catch (Exception e){
                                    Toast.makeText(InventariosRegistro.this, "Error al agregar: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }else{
                        txtInvCodEscanear.setText("");
                        txtInvCodEscanear.setHint("Leer código");
                        txtInvCodEscanear.setHintTextColor(Color.parseColor("#8a8a8a"));
                        new MaterialAlertDialogBuilder(contexto)
                                .setTitle("¡Error!")
                                .setIcon(R.drawable.confirmacion)
                                .setMessage("El material no coincide con el producto de origen o no existe, verifique lo con el administrador.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //No hacer nada
                                    }
                                })
                                .show();
                    }
                }catch (Exception e){
                    Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Generar etiquetas viejas o actualizar
    public void generateEtiquetasViejas(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Insertar etiquetas viejas
                //Nos ayuda a sabir si se encontarron etiquetas viejas
                int hayEtiquetasViejas = 0;
                con = new Conexion(contexto);
                try {
                    //Recorrer el arreglo y buscar etiquetas viejas
                    for(int i=0; i<inventariosDetalles.size(); i++){
                        if(!inventariosDetalles.get(i).getCodigoViejo().equals("")){
                            PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_EtiquetasViejas_VALIDATE ?,?,?;");
                            stmt.setString(1, inventariosDetalles.get(i).getCodigoViejo());
                            stmt.setFloat(2, Float.parseFloat(inventariosDetalles.get(i).getLongitud()));
                            stmt.setString(3, inventariosDetalles.get(i).getProductoID());
                            ResultSet r = stmt.executeQuery();
                            if(r.next()){
                                inventariosDetalles.get(i).setCodigoNuevo(r.getString("Codigo"));
                                //Etiquetas viejas ya tiene valor
                                hayEtiquetasViejas = 1;
                            }
                            System.out.println("dentro");
                        }else{
                            System.out.println("fuera");
                        }
                    }
                    //Terminamos de revisar y verificamos si se encontraron etiquetas viejas
                    if(hayEtiquetasViejas == 1){
                        //Si se encontraron viejas
                        //Se confirma que se encontrarón y se genera pdf
                        Toast.makeText(contexto, "Se encontraron etiquetas viejas, se generará documento.", Toast.LENGTH_SHORT).show();
                        //Generar pdf
                        crearPDF();
                        subirtablaDB();
                    }else{
                        if(ajuste == 1){
                            traerFolioNuevo();
                        }else{
                            //Mensaje de prueba
                            Toast.makeText(contexto, "No se encontraron viejas", Toast.LENGTH_SHORT).show();
                            subirtablaDB();
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //Método para terminar un inventario y mandar a histórico
    public void traerFolioNuevo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Prueba y error
                try {
                    //Creamos la conexión
                    con = new Conexion(contexto);
                    PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Folio_SELECT ?");
                    stmt.setString(1, productoID);
                    ResultSet r = stmt.executeQuery();
                    if(r.next()){
                        crearRegistrosAjuste(r.getString("Folio"));
                    }else{
                        Toast.makeText(contexto, ""+productoID, Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    //Llamamos mensaje de error
                    Toast.makeText(contexto, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void crearRegistrosAjuste(String folio){
        try {
            Conexion conexionService = new Conexion(this);
            //conexionService.conexiondbImplementacion().setAutoCommit(false);
            //comprobaciondeestado = 0;

            String horafin = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            for (int i = 0; i < inventariosDetalles.size(); i++) {
                PreparedStatement var = conexionService.conexiondbImplementacion().prepareCall("PMovil_Registrar_Inventario ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
                var.setString(1, inventariosDetalles.get(i).getCantidad());
                var.setString(2, inventariosDetalles.get(i).getLongitud());
                var.setString(3, ""+Float.parseFloat(inventariosDetalles.get(i).getCantidad()) * Float.parseFloat(inventariosDetalles.get(i).getLongitud()));
                var.setString(4, inventariosDetalles.get(i).getUbicacion());
                var.setString(5, "1");
                var.setString(6, usuario);
                var.setString(7, folio);
                var.setString(8, material);
                var.setString(9, fecha);
                var.setString(10, stockTotal);
                var.setString(11, almacen);
                var.setString(12, inventariosDetalles.get(i).getIncidencia());
                var.setString(13, horaInicial);
                var.setString(14, horafin);
                var.setString(15, inventariosDetalles.get(i).getProductoID());
                var.setString(16, inventariosDetalles.get(i).getUbicacionId());
                var.setInt(17, 1);
                var.execute();
            }
            loading = 4;
            loadinglauncher();

            //Terminar los inventarios y hacer los cambios
            inventariosDetalles = new ArrayList<>();
            Statement stmt1 = con.conexiondbImplementacion().createStatement();
            String query = "SELECT Ubicacion, SUM(Total_registrado) as Cantidad, ProductoID, UbicacionID FROM Movil_Reporte WHERE Folio = '" + folio + "' AND Status = 1 AND Estado = '1' and Fecha = '"+fecha+"' and horainicio = '"+horaInicial+"' and horafin = '"+horafin+"' GROUP BY Ubicacion, ProductoID, UbicacionID";
            ResultSet r = stmt1.executeQuery(query);
            int cont = 0;
            while (r.next()) {
                PreparedStatement statement2 = con.conexiondbImplementacion().prepareCall("PMovil_Ajuste_UPDATE ?,?,?,?");
                statement2.setString(1, r.getString("UbicacionID"));
                statement2.setString(2, r.getString("ProductoID"));
                statement2.setString(3, r.getString("Cantidad"));
                statement2.setInt(4, 1);
                statement2.execute();
                cont++;
            }

            //despausarProducto();
            //conexionService.conexiondbImplementacion().commit();
            //comprobaciondeestado = 2;
            new MaterialAlertDialogBuilder(InventariosRegistro.this)
                    .setTitle("¡Éxito!")
                    .setIcon(R.drawable.correcto)
                    .setMessage("Material ajustado con éxito. total de registros = "+cont)
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Cerrar intent
                            if(fileUri == null){
                                finish();
                            }
                        }
                    })
                    .show();
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(InventariosRegistro.this)
                    .setTitle("¡Error!")
                    .setMessage("Compruebe su conexión a internet "+e.getMessage())
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //
                        }
                    })
                    .show();
            //comprobaciondeestado = 1;
            //conexionService.conexiondbImplementacion().rollback();
        }
    }

    //Crea el PDF de las etiquetas viejas
    public void crearPDF() {
        Document documento = new Document();
        try {
            //Agregar hora de creación
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Etiquetas a imprimir  "+material.replace("/", "_").replace(":", "_").replace("-", "_")+".pdf");

            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            documento.setPageSize(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);


            HeaderFooter footer = new HeaderFooter(new Phrase("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tPágina:" ), new Phrase("\t\t\t|\t\t\tReporte realizado por: "+usuario));
            documento.setFooter(footer);


            documento.open();
            Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, String.valueOf(18), Font.BOLD, Color.BLACK);
            Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, String.valueOf(16), Font.BOLD, Color.BLACK);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.shimaco);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            PdfPTable a = new PdfPTable(3);
            a.setTotalWidth(1000);
            a.addCell(imagen);

            PdfPCell cellt1 = new PdfPCell(new Phrase("Reportes a imprimir\n\nFecha de elaboración: "+fecha+"\nURGENTE IMPRIMIR ETIQUETAS"));
            cellt1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellt1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            a.addCell(cellt1);
            PdfPCell celltUsuario = new PdfPCell(new Phrase("MATERIAL: "+material.toUpperCase()));
            celltUsuario.setHorizontalAlignment(Element.ALIGN_CENTER);
            celltUsuario.setVerticalAlignment(Element.ALIGN_MIDDLE);
            a.addCell(celltUsuario);

            documento.add(a);

            //Agregar temas
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tÉste reporte muestra el emparejamiento de las nuevas etiquetas y las etiquetas viejas."));
            documento.add(new Paragraph("\n"));

            PdfPTable etiquetas = new PdfPTable(4);
            etiquetas.setWidthPercentage(80);
            float[] widthsEtiquetas = new float[] {20f, 20f, 20f, 20f};
            etiquetas.setWidths(widthsEtiquetas);

            PdfPCell codViejo = new PdfPCell(new Phrase("Código viejo"));
            codViejo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            codViejo.setHorizontalAlignment(Element.ALIGN_CENTER);
            etiquetas.addCell(codViejo);

            PdfPCell codNuevo = new PdfPCell(new Phrase("Código nuevo"));
            codNuevo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            codNuevo.setHorizontalAlignment(Element.ALIGN_CENTER);
            etiquetas.addCell(codNuevo);

            PdfPCell cantidad = new PdfPCell(new Phrase("Cantidad"));
            cantidad.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            etiquetas.addCell(cantidad);

            PdfPCell metraje = new PdfPCell(new Phrase("Longitud"));
            metraje.setVerticalAlignment(Element.ALIGN_MIDDLE);
            metraje.setHorizontalAlignment(Element.ALIGN_CENTER);
            etiquetas.addCell(metraje);

            documento.add(etiquetas);

            PdfPTable etiquetasDatos = new PdfPTable(4);
            etiquetasDatos.setWidthPercentage(80);
            float[] widthsEtiquetasDatos = new float[] {20f, 20f, 20f, 20f};
            etiquetas.setWidths(widthsEtiquetasDatos);

            for(int i=0; i<inventariosDetalles.size(); i++){
                if(!inventariosDetalles.get(i).getCodigoNuevo().equals("")){
                    //Solo agregamos los que tengan el código nuevo
                    PdfPCell codViejoDato = new PdfPCell(new Phrase(inventariosDetalles.get(i).getCodigoViejo()));
                    codViejoDato.setHorizontalAlignment(Element.ALIGN_CENTER);
                    codViejoDato.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    etiquetasDatos.addCell(codViejoDato);

                    PdfPCell codNuevoDato = new PdfPCell(new Phrase(inventariosDetalles.get(i).getCodigoNuevo()));
                    codNuevoDato.setHorizontalAlignment(Element.ALIGN_CENTER);
                    codNuevoDato.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    etiquetasDatos.addCell(codNuevoDato);

                    PdfPCell cantidadDato = new PdfPCell(new Phrase(inventariosDetalles.get(i).getCantidad()));
                    cantidadDato.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cantidadDato.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    etiquetasDatos.addCell(cantidadDato);

                    PdfPCell metrajeDato = new PdfPCell(new Phrase(inventariosDetalles.get(i).getLongitud()));
                    metrajeDato.setHorizontalAlignment(Element.ALIGN_CENTER);
                    metrajeDato.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    etiquetasDatos.addCell(metrajeDato);
                }
            }
            documento.add(etiquetasDatos);
            fileUri = file;
        } catch (DocumentException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            documento.close();
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Necesario imprimir etiquetas!")
                    .setIcon(R.drawable.confirmacion)
                    .setCancelable(false)
                    .setMessage("No se permite continuar hasta que se imprima el documento de etiquetas.")
                    .setPositiveButton("Imprimir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            printPDF(fileUri);
                        }
                    })
                    .show();

        }
    }

    //Método de imprimir pdf
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
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Etiquetas");

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

    //Método para mostrar el total en metros que se ha registrado
    public void getTotalRegistrado(){
        //Variable para guardar el total
        float totalRegistrado = 0;
        //Recorremos el arreglo
        for(int i=0; i<inventariosDetalles.size(); i++){
            //Sumamos el total y guardamos en la variable
            totalRegistrado = totalRegistrado + (Float.parseFloat(inventariosDetalles.get(i).getLongitud()) * Float.parseFloat(inventariosDetalles.get(i).getCantidad()));
        }
        lblInvTotalRegistrado.setText(""+totalRegistrado);
        lblInvDiferencia.setText(""+(Float.parseFloat(lblInvSistema.getText().toString()) - Float.parseFloat(lblInvTotalRegistrado.getText().toString())));
    }

    //Trae la información de los intents
    public void getInfoIntents(){
        material = getIntent().getStringExtra("Material");
        unidadMedida = getIntent().getStringExtra("UnidadMedida");
        stockTotal = getIntent().getStringExtra("StockTotal");
        almacen = getIntent().getStringExtra("Almacen");
        productoID = getIntent().getStringExtra("ProductoID");
        estadoBloqueo = getIntent().getIntExtra("estadoBloqueo", 0);
        ajuste = getIntent().getIntExtra("Ajuste", 0);
    }

    //Inicia fecha y hora
    public void startFechaHora(){
        fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        horaInicial = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public ArrayList<Ubicacion> fullArrayUbicaciones(){
        ArrayList<Ubicacion> arrayUbicaciones = new ArrayList<Ubicacion>();
        con = new Conexion(InventariosRegistro.this);
        try {
            Statement stmt = con.conexiondbImplementacion().createStatement();
            String query = "SELECT UbicacionID, Nombre FROM Ubicacion WHERE status = 1;";
            ResultSet r = stmt.executeQuery(query);
            while(r.next()){
                arrayUbicaciones.add(new Ubicacion(r.getString("UbicacionID"), r.getString("Nombre")));
            }
        }catch (Exception e){

        }
        return arrayUbicaciones;
    }

    //Spinner ubicaciones
    public void llenarSpinnerUbicacionesRegistro(ArrayList<Ubicacion> ubicacionesList){
        ArrayAdapter<Ubicacion> adapter = new ArrayAdapter<Ubicacion>(InventariosRegistro.this, R.layout.support_simple_spinner_dropdown_item, ubicacionesList);
        spinnerUbicacionesRegistro.setAdapter(adapter);
        if(ubicacionesList.size() == 2){
            spinnerUbicacionesRegistro.setSelection(1);
        }
    }

    //Buscar ubicaciones
    public ArrayList<Ubicacion> searchUbicaciones(String filtro){
        ArrayList<Ubicacion> ubicacionesArray = new ArrayList<Ubicacion>();
        Ubicacion vacio = new Ubicacion("", "");
        ubicacionesArray.add(vacio);
        for(int i = 1; i<ubicaciones.size(); i++){
            if(ubicaciones.get(i).toString().contains(filtro)){
                ubicacionesArray.add(ubicaciones.get(i));
            }
        }
        return ubicacionesArray;
    }

    //Sonido de correcto
    public void soundCorrecto(){
        MediaPlayer mp = MediaPlayer.create(contexto, R.raw.definite);
        mp.start();
    }

    //Adapter de detalles
    public class AdapterDetalle extends RecyclerView.Adapter<AdapterDetalle.AdapterDetallesHolder>{
        @NonNull
        @Override
        public AdapterDetallesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterDetallesHolder(getLayoutInflater().inflate(R.layout.inventario_a_registro_detalles_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterDetallesHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            //Arreglo
            lblInvTotalRegistros.setText(""+inventariosDetalles.size());
            getTotalRegistrado();
            return inventariosDetalles.size();
        }

        //Adapter detalles holder
        class AdapterDetallesHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView lblInvItemCantidad, lblInvItemContenido, lblInvItemContTotal, lblInvItemUbicacion, lblInvItemIncidencia;
            TextView lblNumRegistro, lblMataterialRegistro;
            ImageView imgInvItemEstadoMercancia, imgInvItemEstadoEtiqueta;
            CardView cardViewInvItem;
            LinearLayout linearInvItem;
            CheckBox checkBoxInvRegistro;
            public AdapterDetallesHolder(@NonNull View itemView){
                super(itemView);
                lblNumRegistro = itemView.findViewById(R.id.lblNumRegistro);
                lblMataterialRegistro = itemView.findViewById(R.id.lblMaterialRegistro);
                lblInvItemCantidad = itemView.findViewById(R.id.lblInvItemCantidad);
                lblInvItemContenido = itemView.findViewById(R.id.lblInvItemContenido);
                lblInvItemContTotal = itemView.findViewById(R.id.lblInvItemContTotal);
                lblInvItemUbicacion = itemView.findViewById(R.id.lblInvItemUbicacion);
                lblInvItemIncidencia = itemView.findViewById(R.id.lblInvItemIncidencias);
                imgInvItemEstadoMercancia = itemView.findViewById(R.id.imgInvItemEstadoMercancia);
                imgInvItemEstadoEtiqueta = itemView.findViewById(R.id.imgInvItemEstadoEtiqueta);
                cardViewInvItem = itemView.findViewById(R.id.cardViewInvItem);
                linearInvItem = itemView.findViewById(R.id.linearInvItem);
                checkBoxInvRegistro = itemView.findViewById(R.id.checkBoxInvRegistro);

                //Checkbox para eliminar
                checkBoxInvRegistro.setOnClickListener(view -> {
                    if(checkBoxInvRegistro.isChecked()){
                        inventariosDetalles.get(getAdapterPosition()).setIsSelected(1);
                    }else{
                        inventariosDetalles.get(getAdapterPosition()).setIsSelected(0);
                    }
                    adapterDetalle.notifyDataSetChanged();
                });

                cardViewInvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(InventariosRegistro.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.inventario_a_registro_editar_item, null);

                        alert.setView(view);
                        AlertDialog dialog = alert.create();
                        dialog.show();
                        dialog.setCancelable(false);
                        EditText txtInvEditCantidad, txtInvEditContenido, txtInvEditUbicacion, txtInvEditIncidencia;
                        TextView lblInvEditTotal;
                        Button btnInvEditAtras, btnInvEditAceptar;
                        Spinner spinnerInvEditUbicacion;
                        txtInvEditCantidad = view.findViewById(R.id.txtInvEditCantidad);
                        txtInvEditContenido = view.findViewById(R.id.txtInvEditContenido);
                        txtInvEditUbicacion = view.findViewById(R.id.txtInvEditUbicacion);
                        txtInvEditIncidencia = view.findViewById(R.id.txtInvEditIncidencias);
                        lblInvEditTotal = view.findViewById(R.id.lblInvEditTotal);
                        spinnerInvEditUbicacion = view.findViewById(R.id.spinnerInvEditUbicacion);
                        btnInvEditAtras = view.findViewById(R.id.btnInvEditAtras2);
                        btnInvEditAceptar = view.findViewById(R.id.btnInvEditAceptar);
                        //Asignar valores
                        txtInvEditCantidad.setText(""+inventariosDetalles.get(getAdapterPosition()).getCantidad());
                        txtInvEditContenido.setText(""+inventariosDetalles.get(getAdapterPosition()).getLongitud());
                        txtInvEditUbicacion.setText(""+inventariosDetalles.get(getAdapterPosition()).getUbicacion());
                        txtInvEditIncidencia.setText(""+inventariosDetalles.get(getAdapterPosition()).getIncidencia());
                        lblInvEditTotal.setText(""+(Float.parseFloat(inventariosDetalles.get(getAdapterPosition()).getCantidad()))*(Float.parseFloat(inventariosDetalles.get(getAdapterPosition()).getLongitud())));
                        //Llenar array ubicaciones
                        fillSpinnerUbicaciones(spinnerInvEditUbicacion, searchUbicaciones(txtInvEditUbicacion.getText().toString()));
                        txtInvEditContenido.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                float total = 0;
                                if(txtInvEditCantidad.getText().toString().equals("") || txtInvEditContenido.getText().toString().equals("")){
                                    Toast.makeText(InventariosRegistro.this, "Favor de llenar la información correctamente.", Toast.LENGTH_SHORT).show();
                                }else{
                                    total = Float.parseFloat(txtInvEditCantidad.getText().toString()) * Float.parseFloat(txtInvEditContenido.getText().toString());
                                    lblInvEditTotal.setText(""+total);
                                }
                                return false;
                            }
                        });

                        txtInvEditCantidad.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                float total = 0;
                                if(txtInvEditCantidad.getText().toString().equals("") || txtInvEditContenido.getText().toString().equals("")){
                                    Toast.makeText(InventariosRegistro.this, "Favor de llenar la información correctamente.", Toast.LENGTH_SHORT).show();
                                }else{
                                    total = Float.parseFloat(txtInvEditCantidad.getText().toString()) * Float.parseFloat(txtInvEditContenido.getText().toString());
                                    lblInvEditTotal.setText(""+total);
                                }
                                return false;
                            }
                        });

                        txtInvEditUbicacion.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if(txtInvEditUbicacion.getText().toString().equals("")){
                                    //Mostrar todas las ubicaciones
                                    fillSpinnerUbicaciones(spinnerInvEditUbicacion, ubicaciones);
                                }else{
                                    String filtro = txtInvEditUbicacion.getText().toString().toUpperCase();
                                    ArrayList<Ubicacion> ubicacionesFiltradas = searchUbicaciones(filtro);
                                    fillSpinnerUbicaciones(spinnerInvEditUbicacion, ubicacionesFiltradas);
                                }
                                return false;
                            }
                        });

                        btnInvEditAtras.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        btnInvEditAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Validar ubicación seleccionada y campos
                                Ubicacion ubicacionSeleccionada = new Ubicacion();
                                if(txtInvEditCantidad.getText().toString().equals("") || txtInvEditContenido.getText().toString().equals("") || txtInvEditUbicacion.getText().toString().equals("")){
                                    new MaterialAlertDialogBuilder(InventariosRegistro.this)
                                            .setTitle("¡Error!")
                                            .setIcon(R.drawable.confirmacion)
                                            .setMessage("Por favor, verifique que la información sea correcta y completa, vuelvalo a intentar")
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //No hacer nada
                                                }
                                            })
                                            .show();
                                }else{
                                    ubicacionSeleccionada = (Ubicacion) spinnerInvEditUbicacion.getSelectedItem();
                                    inventariosDetalles.get(getAdapterPosition()).setCantidad(txtInvEditCantidad.getText().toString());
                                    inventariosDetalles.get(getAdapterPosition()).setLongitud(txtInvEditContenido.getText().toString());
                                    inventariosDetalles.get(getAdapterPosition()).setUbicacion(ubicacionSeleccionada.toString());
                                    inventariosDetalles.get(getAdapterPosition()).setUbicacionId(ubicacionSeleccionada.getUbicacionId());
                                    if(!txtInvEditIncidencia.getText().toString().equals("")){
                                        inventariosDetalles.get(getAdapterPosition()).setEstado("1");
                                    }else{
                                        inventariosDetalles.get(getAdapterPosition()).setEstado("0");
                                    }
                                    inventariosDetalles.get(getAdapterPosition()).setIncidencia(txtInvEditIncidencia.getText().toString());
                                    try {
                                        adapterDetalle.notifyDataSetChanged();
                                        dialog.dismiss();
                                        new MaterialAlertDialogBuilder(InventariosRegistro.this)
                                                .setTitle("¡Éxito")
                                                .setIcon(R.drawable.correcto)
                                                .setMessage("Registro actualizado exitosamente.")
                                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //No hacer nada
                                                    }
                                                }).show();
                                    }catch (Exception e){
                                        Toast.makeText(InventariosRegistro.this, "Error, consulte con el administrador del sistema.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                        spinnerInvEditUbicacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Ubicacion ubicacion = (Ubicacion) spinnerInvEditUbicacion.getSelectedItem();
                                if(ubicacion.toString().equals("")){

                                }else{
                                    txtInvEditUbicacion.setText(""+ubicacion.toString());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                //No hacer nada
                            }
                        });
                    }
                });
            }

            public void fillSpinnerUbicaciones(Spinner spinner, ArrayList<Ubicacion> ubicacionesArray){
                ArrayAdapter<Ubicacion> adapter = new ArrayAdapter<Ubicacion>(InventariosRegistro.this, R.layout.support_simple_spinner_dropdown_item, ubicacionesArray);
                spinner.setAdapter(adapter);
                if(ubicacionesArray.size() == 2){
                    spinner.setSelection(1);
                }
            }

            public ArrayList<Ubicacion> searchUbicaciones(String filtro){
                ArrayList<Ubicacion> ubicacionesArray = new ArrayList<Ubicacion>();
                Ubicacion vacio = new Ubicacion("", "");
                ubicacionesArray.add(vacio);
                for(int i = 1; i<ubicaciones.size(); i++){
                    if(ubicaciones.get(i).toString().contains(filtro)){
                        ubicacionesArray.add(ubicaciones.get(i));
                    }
                }
                return ubicacionesArray;
            }

            public void printAdapter(int position){
                //Rellenar los campos
                lblNumRegistro.setText(""+(position+1));
                lblMataterialRegistro.setText(material);
                if(inventariosDetalles.get(position).getIsSelected() == 1){
                    checkBoxInvRegistro.setChecked(true);
                }else{
                    checkBoxInvRegistro.setChecked(false);
                }
                lblInvItemCantidad.setText(""+inventariosDetalles.get(position).getCantidad());
                lblInvItemContenido.setText(""+inventariosDetalles.get(position).getLongitud());
                lblInvItemContTotal.setText(""+(Float.parseFloat(inventariosDetalles.get(position).getCantidad())*Float.parseFloat(inventariosDetalles.get(position).getLongitud())));
                lblInvItemUbicacion.setText(inventariosDetalles.get(position).getUbicacion());
                if(inventariosDetalles.get(position).getEstadoEtiqueta() == 1){
                    imgInvItemEstadoEtiqueta.setImageResource(R.drawable.snakerojo);
                }else{
                    imgInvItemEstadoEtiqueta.setImageResource(R.drawable.correcto);
                }
                if(Integer.parseInt(inventariosDetalles.get(position).getEstado()) == 1){
                    imgInvItemEstadoMercancia.setImageResource(R.drawable.snakerojo);
                    lblInvItemIncidencia.setText(inventariosDetalles.get(position).getIncidencia());
                    lblInvItemIncidencia.setVisibility(View.GONE);
                }else{
                    imgInvItemEstadoMercancia.setImageResource(R.drawable.correcto);
                    lblInvItemIncidencia.setText(inventariosDetalles.get(position).getIncidencia());
                    lblInvItemIncidencia.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onClick(View v){

            }
        }
    }
}