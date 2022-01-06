package com.example.operacionesivra.Inventarios.Vistas.EnProceso.DetallesProceso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.operacionesivra.Inventarios.Models.InventariosDetalle;
import com.example.operacionesivra.Inventarios.Models.Ubicacion;
import com.example.operacionesivra.Inventarios.Models.UbicacionContenido;
import com.example.operacionesivra.Inventarios.Vistas.EnHistorico.DetallesHistorico.PdfDocumentAdapter;
import com.example.operacionesivra.Inventarios.Vistas.InventariosRegistro;
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
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InventarioAEnProcesosDetalle extends AppCompatActivity {
    //Array list ubicaciones
    ArrayList<Ubicacion> ubicacionesFinal = new ArrayList<>();
    //Estado pantalla cargando
    public int loading = 0;
    //FileUri
    File fileUri;
    //Contexto
    Context contexto = this;
    //Conexión
    Conexion conexion = new Conexion(contexto);
    //Array liste inventariosDetalle
    ArrayList<InventariosDetalle> inventariosDetalles = new ArrayList<>();
    ArrayList<InventariosDetalle> inventariosEliminados = new ArrayList<>();
    //Array ubicaciones
    ArrayList<UbicacionContenido> ubicacionContenidos = new ArrayList<>();
    //Arrar list modoelo ubicaciones
    ArrayList<Ubicacion> ubicaciones;
    //Adapter
    AdaptadorDetalles adaptadorDetalles = new AdaptadorDetalles();
    //Variables principales
    String folio, fecha, almacen, material, sistema, horaInicio, horaFin, fechaCambio, usuario, stockTotal;
    String productoID = "";
    //Estado del bloqueo
    int estadoBloqueo = 0;
    //Spinenr ubicaciones general
    Spinner spinnerUbicacionesEditar;
    //Edit text
    EditText txtUbicacionEditar;
    //Recycler
    RecyclerView recyclerInvDetEdit;
    //TextViews
    TextView lblInvDetEditFecha, lblInvDetEditAlmacen, lblInvDetEditMaterial, lblInvDetEditMedida, lblInvDetEditSistema, lblInvDetEditTotalRegistrado, lblInvDetEditRegistros, lblInvDetEditDiferencia;
    //EditText
    EditText txtInvCodEscanearEdit;
    //Buttons
    Button btnInvDetEditGuardar, btnInvDetEditEliminar, btnInvDetEditBloqueado, btnInvDetEditAtras, btnInvDetEditAgregar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_a_editar);
        //Traer valores del intent y asignar  variables principales
        folio = getIntent().getStringExtra("Folio");
        fecha = getIntent().getStringExtra("Fecha");
        almacen = getIntent().getStringExtra("Almacen");
        material = getIntent().getStringExtra("Material");
        sistema = getIntent().getStringExtra("Sistema");
        stockTotal = getIntent().getStringExtra("StockTotal");
        usuario = getIntent().getStringExtra("usuario");
        //Spinner
        spinnerUbicacionesEditar = findViewById(R.id.spinnerUbicacionesEditar);
        //RecyclerView
        recyclerInvDetEdit = findViewById(R.id.recyclerInvDetEdit);
        recyclerInvDetEdit.setLayoutManager(new LinearLayoutManager(contexto));
        recyclerInvDetEdit.setAdapter(adaptadorDetalles);
        //EditText
        txtUbicacionEditar = findViewById(R.id.txtUbicacionEditar);
        txtInvCodEscanearEdit = findViewById(R.id.txtInvCodEscanearEdit);
        //TextViews
        lblInvDetEditFecha = findViewById(R.id.lblInvDetEditFecha);
        lblInvDetEditAlmacen = findViewById(R.id.lblInvDetEditAlmacen);
        lblInvDetEditMaterial = findViewById(R.id.lblInvDetEditMaterial);
        lblInvDetEditMedida = findViewById(R.id.lblInvDetEditMedida);
        lblInvDetEditSistema = findViewById(R.id.lblInvDetEditSistema);
        lblInvDetEditRegistros = findViewById(R.id.lblInvDetEditRegistros);
        lblInvDetEditTotalRegistrado = findViewById(R.id.lblInvDetEditTotalRegistrado);
        lblInvDetEditDiferencia = findViewById(R.id.lblInvDetEditDiferencia);
        //Buttons
        btnInvDetEditGuardar = findViewById(R.id.btnInvDetEditGuardar);
        btnInvDetEditEliminar = findViewById(R.id.btnInvDetEditEliminar);
        btnInvDetEditBloqueado = findViewById(R.id.btnInvDetEditBloqueado);
        btnInvDetEditAtras = findViewById(R.id.btnInvDetEditAtras);
        btnInvDetEditAgregar = findViewById(R.id.btnInvDetEditAgregar);

        //Invocamos al método para llenar los valores principales
        //Llenar array ubicaciones
        ubicaciones = fullArrayUbicaciones();
        fullVariablesPrincipales();
        fillSpinnerUbicaciones(ubicaciones);
        txtUbicacionEditar.setText("Hola");
        //Buttón regresar
        btnInvDetEditAtras.setOnClickListener(view -> {
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
                                loading = 2;
                                loadinglauncher();
                                //setEditInventario();
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
                loading = 4;
                loadinglauncher();
                //despausarProducto();
                finish();
            }
        });

        btnInvDetEditAgregar.setOnClickListener(view -> {
            String codigoMaterial = txtInvCodEscanearEdit.getText().toString();
            if(codigoMaterial.equals("")){
                txtInvCodEscanearEdit.setHint("Campo requerido");
                txtInvCodEscanearEdit.setHintTextColor(Color.parseColor("#FF0000"));
            }else{
                if(addCodigoEscaneado(codigoMaterial) == 1){
                    txtInvCodEscanearEdit.setText("");
                    txtInvCodEscanearEdit.setHint("Leer código");
                    txtInvCodEscanearEdit.setHintTextColor(Color.parseColor("#8a8a8a"));
                }
            }
        });

        txtInvCodEscanearEdit.setOnKeyListener((view, i, keyEvent) -> {
            escanearCodigo(txtInvCodEscanearEdit.getText().toString());
            return false;
        });

        txtUbicacionEditar.setOnKeyListener((view, i, keyEvent) -> {
            if(txtUbicacionEditar.getText().toString().equals("")){
                //Mostrar todas las ubicaciones
                fillSpinnerUbicaciones(ubicaciones);
            }else{
                String filtro = txtUbicacionEditar.getText().toString().toUpperCase();
                ArrayList<Ubicacion> ubicacionesFiltradas = searchUbicaciones(filtro);
                fillSpinnerUbicaciones(ubicacionesFiltradas);
            }
            return false;
        });

        spinnerUbicacionesEditar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Ubicacion ubicacion = (Ubicacion) spinnerUbicacionesEditar.getSelectedItem();
                if(!ubicacion.toString().equals("")){
                    txtUbicacionEditar.setText(""+ubicacion.toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //No hacer nada
            }
        });

        //Acciones botones
        btnInvDetEditGuardar.setOnClickListener(view -> {
            if(comprobarUbicaciones() == 0){
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
            }else{
                loading = 2;
                loadinglauncher();
                //setEditInventario();
            }
        });
        btnInvDetEditEliminar.setOnClickListener(view -> {
            deleteRegistros();
        });
        btnInvDetEditBloqueado.setOnClickListener(view -> {
            setStatusBtnBloqueado();
        });
    }

    public void loadinglauncher(){
        Loading loading = new Loading(this);
        loading.execute();
    }

    //Método para pausar/despausar el producto
    public void setStatusBtnBloqueado(){
        if(estadoBloqueo == 1){
            loading = 4;
            loadinglauncher();
            //despausarProducto();
            btnInvDetEditBloqueado.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.icono_despausar, 0);
            btnInvDetEditBloqueado.setText("BLOQUEAR");
            estadoBloqueo = 0;
        }else{
            loading = 3;
            loadinglauncher();
            //pausarProducto();
            btnInvDetEditBloqueado.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.icono_pausar, 0);
            btnInvDetEditBloqueado.setText("DESBLOQUEAR");
            estadoBloqueo = 1;
        }
        Toast.makeText(contexto, ""+ubicacionesFinal.get(ubicacionesFinal.size()-1).toString(), Toast.LENGTH_SHORT).show();
    }

    //Método para pausar el producto
    public void pausarProducto(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conexion = new Conexion(contexto);
                try {
                    PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Producto_Pausar_UPDATE ?");
                    stmt.setString(1, productoID);
                    stmt.execute();
                    Toast.makeText(contexto, "¡Material bloqueado exitosamente!"+ubicacionesFinal.get(ubicacionesFinal.size()-1).toString(), Toast.LENGTH_LONG);
                }catch (Exception e){
                    Toast.makeText(contexto, "Error al pausar: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Método para despausar el producto
    public void despausarProducto(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conexion = new Conexion(contexto);
                try {
                    PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Producto_Despausar_UPDATE ?");
                    stmt.setString(1, productoID);
                    stmt.execute();
                    Toast.makeText(contexto, "¡Producto desbloqueado exitosamente!", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(contexto, "Error al desbloquear: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(contexto, "Acción no permitida para este proceso.", Toast.LENGTH_SHORT).show();
    }

    //Método para guardar los cambios en la BD
    public void setEditInventario(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                horaFin = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                //Captura de errores
                //Actualizar el estado de los productos eliminados
                try {
                    //Agregar los eliminados para que puedan actualizarse
                    addEliminadosAGenerales();
                    for(int i = 0; i<inventariosDetalles.size(); i++){
                        //Preparar el procedimiento almacenado en un statement
                        PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Detalle_Proceso_UPDATE ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?;");
                        //Cargamos los valores de los parametros según su orden
                        stmt.setString(1, folio);
                        stmt.setString(2, inventariosDetalles.get(i).getRegistroID());
                        stmt.setString(3, inventariosDetalles.get(i).getCantidad());
                        stmt.setString(4, inventariosDetalles.get(i).getLongitud());
                        stmt.setString(5, ""+(Float.parseFloat(inventariosDetalles.get(i).getCantidad()) * Float.parseFloat(inventariosDetalles.get(i).getLongitud())));
                        stmt.setString(6, inventariosDetalles.get(i).getUbicacion());
                        stmt.setString(7, inventariosDetalles.get(i).getEstado());
                        stmt.setString(8, usuario);
                        stmt.setString(9, material);
                        stmt.setString(10, fecha);
                        stmt.setString(11, stockTotal);
                        stmt.setString(12, almacen);
                        stmt.setString(13, inventariosDetalles.get(i).getIncidencia());
                        stmt.setString(14, horaInicio);
                        stmt.setString(15, horaFin);
                        stmt.setString(16, inventariosDetalles.get(i).getProductoID());
                        stmt.setString(17, inventariosDetalles.get(i).getUbicacionId());
                        stmt.setInt(18, inventariosDetalles.get(i).getStatus());
                        stmt.execute();
                    }
                    loading = 4;
                    loadinglauncher();
                    //despausarProducto();
                    generateEtiquetasViejas();
                    //Regresamos que se realizó la acción
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Éxito!")
                            .setCancelable(false)
                            .setIcon(R.drawable.correcto)
                            .setMessage("Se ha realizado la acción exitosamente.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //No hacer nada
                                    if(fileUri == null){
                                        finish();
                                    }else{

                                    }
                                }
                            })
                            .show();
                }catch (Exception e){
                    //En caso que ocurra error, lo mostramos
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Error!")
                            .setCancelable(false)
                            .setIcon(R.drawable.snakerojo)
                            .setMessage("Error encontrado: "+e.getMessage()+", "+inventariosDetalles.get(1).getStatus())
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //No hacer nada
                                    finish();
                                }
                            })
                            .show();
                }
            }
        });
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

    //Método para agregar los inventarios eliminados a los inventarios generales
    public void addEliminadosAGenerales(){
        //Recorremos el arreglo de los eliminados
        for(int i=0; i<inventariosEliminados.size(); i++){
            //Agregamos a la lista general
            inventariosDetalles.add(inventariosEliminados.get(i));
        }
    }

    //Método para eliminar registros del inventario
    public void deleteRegistros(){
        //Declaramos un arreglo temporal
        ArrayList<InventariosDetalle> inventariosDetalelsTemp = new ArrayList<>();
        //Variable que nos permitirá saber si se encontraron registros para eliminar
        int hayEliminar = 0;
        //Verificamos si la lista que contiene todos los registros está vacía
        if(inventariosDetalles.size() == 0){
            new MaterialAlertDialogBuilder(contexto)
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
            //Recorremos la lista principal
            for(int i=0; i<inventariosDetalles.size(); i++){
                //Validamos si está seleccionado el registro para poderlo eliminar
                if(inventariosDetalles.get(i).getIsSelected() == 0){
                    //Agregamos el registro a la lista temporal, para depurar los que estaban seleccionados
                    inventariosDetalelsTemp.add(inventariosDetalles.get(i));
                }else{
                    //Asignamos que si se encontraron registros selecccionados
                    hayEliminar = 1;
                    //Validamos si el registro eliminado es existente o nuevo
                    if(!inventariosDetalles.get(i).getFolio().equals("")){
                        //Ponemos el status en 0 para que sea actualizable al guardar
                        inventariosDetalles.get(i).setStatus(0);
                        //Agregamos a la lista de los detalles eliminados que vienen de la BD
                        inventariosEliminados.add(inventariosDetalles.get(i));
                    }
                }
            }
            //Salimos del ciclo y validamos si se encontraron registros seleccionados para eliminar
            if(hayEliminar == 1){
                //Asignamos los item d ela lista temporal(nueva) en la lista de los item generales
                inventariosDetalles = inventariosDetalelsTemp;
                //Notificamos los cambios al adaptador
                adaptadorDetalles.notifyDataSetChanged();
                //Mensage de éxito
                new MaterialAlertDialogBuilder(contexto)
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
                //Sino se encontraon para eliminar, entonces solo mostramos mensaje de error
                new MaterialAlertDialogBuilder(contexto)
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

    //Método para validar el codigo escaneado
    public void escanearCodigo(String codigo){
        char [] tamaño = codigo.toCharArray();
        if(tamaño.length == 10){
            addCodigoEscaneado(codigo);
            txtInvCodEscanearEdit.requestFocus();
            txtInvCodEscanearEdit.setText("");
            txtInvCodEscanearEdit.setHint("Leer código");
            txtInvCodEscanearEdit.setHintTextColor(Color.parseColor("#8a8a8a"));
        }
    }

    //Método para agregar un producto
    public int addCodigoEscaneado(String codigoMaterial){
        //Abrir conexión
        Ubicacion ubicacionSeleccionada = (Ubicacion) spinnerUbicacionesEditar.getSelectedItem();
        conexion = new Conexion(contexto);
        //Try catch para cachar errores
        try {
            //Consultar productoID
            PreparedStatement stmtIni = conexion.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Registro_Codigo_Validate ?");
            stmtIni.setString(1, codigoMaterial);
            ResultSet r1 = stmtIni.executeQuery();
            String productoBD = "";
            if(r1.next()){
                productoBD = r1.getString("ProductoID");
            }
            //Empezar comparaciones
            if(productoBD.equals(productoID)){
                //Empezar enunciadp
                Statement stmt = conexion.conexiondbImplementacion().createStatement();
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
                //Si trae más de un registro
                //Si trae solo UNA UBICACIÓN
                if(ubicacionContenidos.size() >= 1){
                    InventariosDetalle registro = new InventariosDetalle("", "", "1", ubicacionContenidos.get(0).getContenido(), ""+ubicacionSeleccionada.toString(), "", "0", ubicacionSeleccionada.getUbicacionId(), ubicacionContenidos.get(0).getProductoID(), "", "", 1,  0, 0);
                    inventariosDetalles.add(registro);
                    try {
                        adaptadorDetalles.notifyDataSetChanged();
                        Toast.makeText(contexto, "¡Registrado exitosamente! ETIQUETA NORMAL", Toast.LENGTH_SHORT).show();
                        soundCorrecto();
                        ubicacionContenidos = new ArrayList<>();
                    }catch (Exception e){
                        Toast.makeText(contexto, "Error al agregar: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }//Buscar en codGeneral sin ubicaicon
                else if(ubicacionContenidos.size() == 0) {
                    ubicacionContenidos = new ArrayList<>();
                    //Empezar enunciadp
                    Statement stmtV1 = conexion.conexiondbImplementacion().createStatement();
                    //Sintaxys SQL
                    String queryV1 = "SELECT Longitud, ProductoID, Codigo FROM CodGeneral WHERE Codigo = '" + codigoMaterial + "';";
                    //Ejecutar query
                    ResultSet rV1 = stmtV1.executeQuery(queryV1);
                    //Recorrer result set
                    while (rV1.next()) {
                        ubicacionContenidos.add(new UbicacionContenido("", "", rV1.getString("Longitud"), rV1.getString("ProductoID")));
                    }
                    if(ubicacionContenidos.size()==0){
                        //Buscar en viejas como ultima opción
                        //Empezar enunciadp
                        Statement stmtV = conexion.conexiondbImplementacion().createStatement();
                        //Sintaxys SQL
                        String queryV = "SELECT Codigo, Longitud, ProductoID FROM CodUnico WHERE Codigo = '"+codigoMaterial+"';";
                        //Ejecutar query
                        ResultSet rV = stmtV.executeQuery(queryV);
                        //Recorrer result set
                        while(rV.next()){
                            ubicacionContenidos.add(new UbicacionContenido("", "", rV.getString("Longitud"), rV.getString("ProductoID"), rV.getString("Codigo"), ""));
                        }
                        if(ubicacionContenidos.size() > 0){
                            InventariosDetalle registro = new InventariosDetalle("", "", "1", ubicacionContenidos.get(0).getContenido(), ""+ubicacionSeleccionada.toString(), "", "0", ubicacionSeleccionada.getUbicacionId(), ubicacionContenidos.get(0).getProductoID(), ubicacionContenidos.get(0).getCodigoViejo(), ubicacionContenidos.get(0).getCodigoNuevo(), 1, 0, 1);
                            inventariosDetalles.add(registro);
                            try {
                                adaptadorDetalles.notifyDataSetChanged();
                                Toast.makeText(contexto, "¡Registrado exitosamente! etiqueta vieja", Toast.LENGTH_SHORT).show();
                                soundCorrecto();
                                ubicacionContenidos = new ArrayList<>();
                                return 1;
                            }catch (Exception e){
                                Toast.makeText(contexto, "Error al agregar: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        ubicacionContenidos = new ArrayList<>();
                        //Empezar enunciadp
                        Statement stmtV = conexion.conexiondbImplementacion().createStatement();
                        //Sintaxys SQL
                        String queryV = "SELECT Codigo, Longitud, ProductoID FROM CodGeneral WHERE Codigo = '"+codigoMaterial+"';";
                        //Ejecutar query
                        ResultSet rV = stmtV.executeQuery(queryV);
                        //Recorrer result set
                        while(rV.next()){
                            ubicacionContenidos.add(new UbicacionContenido("", "", rV.getString("Longitud"), rV.getString("ProductoID"), rV.getString("Codigo"), ""));
                        }
                        //InventariosDetalle registro = new InventariosDetalle("", "", "1", ubicacionContenidos.get(0).getContenido(), "" + ubicacionContenidos.get(0).getUbicacion(), "", "0", ubicacionContenidos.get(0).getUbicacionID(), ubicacionContenidos.get(0).getProductoID(), "", "", 1, 0, 0);
                        InventariosDetalle registro = new InventariosDetalle("", "1", ubicacionContenidos.get(0).getContenido(), "" + ubicacionContenidos.get(0).getUbicacion(), "", "0", ubicacionContenidos.get(0).getUbicacionID(), ubicacionContenidos.get(0).getProductoID(), ubicacionContenidos.get(0).getCodigoViejo(), "", 0, 1);
                        inventariosDetalles.add(registro);
                        try {
                            adaptadorDetalles.notifyDataSetChanged();
                            soundCorrecto();
                            Toast.makeText(contexto, "¡Registrado exitosamente! ETIQUETA NORMAL sin ubicación", Toast.LENGTH_SHORT).show();
                            ubicacionContenidos = new ArrayList<>();
                            return 1;
                        }catch (Exception e){
                            Toast.makeText(contexto, "Error al agregar: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }else{
                txtInvCodEscanearEdit.setText("");
                txtInvCodEscanearEdit.setHint("Leer código");
                txtInvCodEscanearEdit.setHintTextColor(Color.parseColor("#8a8a8a"));
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
            Toast.makeText(contexto, "Error al agregar: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //Traer la información del codigo escaneado

        return 0;
    }

    //Sonido de correcto
    public void soundCorrecto(){
        MediaPlayer mp = MediaPlayer.create(contexto, R.raw.definite);
        mp.start();
    }

    //Método que llena la información principal
    public void fullVariablesPrincipales(){
        String m = "METRO LINE";
        fechaCambio = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        horaInicio = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        lblInvDetEditFecha.setText(fecha);
        lblInvDetEditAlmacen.setText(almacen);
        lblInvDetEditMaterial.setText(material);
        lblInvDetEditMedida.setText(m);
        lblInvDetEditSistema.setText(stockTotal);
        lblInvDetEditTotalRegistrado.setText(sistema);
        loading = 1;
        loadinglauncher();
        //getRegistros();
        //setStatusBtnBloqueado();
    }

    //Método que muestra los registros encontrados con el folio
    public void getRegistros(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Captura de errores
                try {
                    //Preparamos el statement
                    PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Detalle_Proceso_SELECT ?");
                    //Agregarmos variable del procedimiento almacenado
                    stmt.setString(1, folio);
                    //Ejecutamos el query y almacenamos el resultado (SELECT)
                    ResultSet r = stmt.executeQuery();
                    //Recorremos los valores regresados por la BD
                    while(r.next()){
                        //Agregamos a la lista de inventarios detalle
                        inventariosDetalles.add(new InventariosDetalle(r.getString("Folio"), r.getString("Id_registro"), r.getString("Codigo_unico"), r.getString("Cantidad"), r.getString("Ubicacion"), r.getString("Observaciones"), "", r.getString("UbicacionID"), r.getString("ProductoID"), "", "", r.getInt("Status"), 0, 0));
                        ubicacionesFinal.add(new Ubicacion("", r.getString("Ubicacion")));
                    }
                    productoID = inventariosDetalles.get(0).getProductoID();
                    adaptadorDetalles.notifyDataSetChanged();
                    setStatusBtnBloqueado();
                }catch (Exception e){
                    Toast.makeText(contexto, "Error al traer registros: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Método para traer el total registrado
    public float getTotalRegistrado(){
        //Variable donde se guardará el total
        float total = 0;
        //Recorremos nuestro arreglo
        for(int i=0; i<inventariosDetalles.size(); i++){
            //Sumamos el total
            total = total +(Float.parseFloat(inventariosDetalles.get(i).getCantidad()) * Float.parseFloat(inventariosDetalles.get(i).getLongitud()));
        }
        //Scamos diferencia
        lblInvDetEditDiferencia.setText(""+(Float.parseFloat(stockTotal) - total));
        //Retornamos el total
        return total;
    }

    //Generar etiquetas viejas o actualizar
    public void generateEtiquetasViejas(){
        //Insertar etiquetas viejas
        //Nos ayuda a sabir si se encontarron etiquetas viejas
        int hayEtiquetasViejas = 0;
        conexion = new Conexion(contexto);
        try {
            //Recorrer el arreglo y buscar etiquetas viejas
            for(int i=0; i<inventariosDetalles.size(); i++){
                if(!inventariosDetalles.get(i).getCodigoViejo().equals("")){
                    PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("PMovil_Inventarios_EtiquetasViejas_VALIDATE ?,?,?;");
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
            }else{
                //Mensaje de prueba
                Toast.makeText(contexto, "No se encontraron viejas", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error al generar etiqueta vieja: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void crearPDF() {
        Document documento = new Document();
        try {
            //Agregar hora de creación
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Etiquetas a imprimir  "+material.replace("/", "_").replace(":", "_").replace("-", "_")+" editado.pdf");

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
            Toast.makeText(contexto, "Error al crear pdf: " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(contexto, "Error creando pdf: " + e, Toast.LENGTH_SHORT).show();
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

    //Método para imprimir
    public void printPDF(File file){
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try{
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(contexto, file.getAbsolutePath());
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        }catch (Exception e){
            Toast.makeText(contexto, "Error creando pdf "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    //Método para llenar el array de las ubicaciones del spinner en editar
    public ArrayList<Ubicacion> fullArrayUbicaciones(){
        ArrayList<Ubicacion> arrayUbicaciones = new ArrayList<Ubicacion>();
        conexion = new Conexion(contexto);
        try {
            Statement stmt = conexion.conexiondbImplementacion().createStatement();
            String query = "SELECT UbicacionID, Nombre FROM Ubicacion WHERE status = 1;";
            ResultSet r = stmt.executeQuery(query);
            while(r.next()){
                arrayUbicaciones.add(new Ubicacion(r.getString("UbicacionID"), r.getString("Nombre")));
            }
        }catch (Exception e){

        }
        return arrayUbicaciones;
    }

    //Método para llenar las ubicaciones
    public void fillSpinnerUbicaciones(ArrayList<Ubicacion> ubicacionesList){
        ArrayAdapter<Ubicacion> adapter = new ArrayAdapter<Ubicacion>(contexto, R.layout.support_simple_spinner_dropdown_item, ubicacionesList);
        spinnerUbicacionesEditar.setAdapter(adapter);
        if(ubicacionesList.size() == 2){
            spinnerUbicacionesEditar.setSelection(1);
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

    //Adaptador del recycler y el modelo inventarios detalle
    public class AdaptadorDetalles extends RecyclerView.Adapter<AdaptadorDetalles.AdaptadorDetallesHolder>{
        @NonNull
        @Override
        public AdaptadorDetallesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdaptadorDetallesHolder(getLayoutInflater().inflate(R.layout.inventario_a_registro_detalles_item, parent, false));
        }

        @Override
        public void onBindViewHolder(AdaptadorDetallesHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            //Cargamos el total de registros
            lblInvDetEditRegistros.setText(""+inventariosDetalles.size());
            //Cargamos el total registrado
            lblInvDetEditTotalRegistrado.setText(""+getTotalRegistrado());
            return inventariosDetalles.size();
        }

        class AdaptadorDetallesHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
            //TextView
            TextView lblInvItemCantidad, lblInvItemContenido, lblInvItemContTotal, lblInvItemUbicacion, lblInvItemIncidencia, lblNumRegistro, lblMaterialRegistro;
            ImageView imgInvItemEstadoMercancia, imgInvItemEstadoEtiqueta;
            CardView cardViewInvItem;
            CheckBox checkBoxInvRegistro;
            public AdaptadorDetallesHolder(View itemView) {
                super(itemView);
                lblNumRegistro = itemView.findViewById(R.id.lblNumRegistro);
                lblMaterialRegistro = itemView.findViewById(R.id.lblMaterialRegistro);
                lblInvItemCantidad = itemView.findViewById(R.id.lblInvItemCantidad);
                lblInvItemContenido = itemView.findViewById(R.id.lblInvItemContenido);
                lblInvItemContTotal = itemView.findViewById(R.id.lblInvItemContTotal);
                lblInvItemUbicacion = itemView.findViewById(R.id.lblInvItemUbicacion);
                lblInvItemIncidencia = itemView.findViewById(R.id.lblInvItemIncidencias);
                imgInvItemEstadoMercancia = itemView.findViewById(R.id.imgInvItemEstadoMercancia);
                imgInvItemEstadoEtiqueta = itemView.findViewById(R.id.imgInvItemEstadoEtiqueta);
                cardViewInvItem = itemView.findViewById(R.id.cardViewInvItem);
                checkBoxInvRegistro = itemView.findViewById(R.id.checkBoxInvRegistro);

                checkBoxInvRegistro.setOnClickListener(view -> {
                    if(inventariosDetalles.get(getAdapterPosition()).getIsSelected() == 0){
                        inventariosDetalles.get(getAdapterPosition()).setIsSelected(1);
                    }else{
                        inventariosDetalles.get(getAdapterPosition()).setIsSelected(0);
                    }
                    adaptadorDetalles.notifyDataSetChanged();
                });

                //CardView acciones
                cardViewInvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(contexto, "Estado etiqueta" + inventariosDetalles.get(getAdapterPosition()).getEstadoEtiqueta(), Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
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
                        txtInvEditCantidad.setText("" + inventariosDetalles.get(getAdapterPosition()).getCantidad());
                        txtInvEditContenido.setText("" + inventariosDetalles.get(getAdapterPosition()).getLongitud());
                        txtInvEditUbicacion.setText("" + inventariosDetalles.get(getAdapterPosition()).getUbicacion());
                        txtInvEditIncidencia.setText("" + inventariosDetalles.get(getAdapterPosition()).getIncidencia());
                        lblInvEditTotal.setText("" + (Float.parseFloat(inventariosDetalles.get(getAdapterPosition()).getCantidad())) * (Float.parseFloat(inventariosDetalles.get(getAdapterPosition()).getLongitud())));
                        //Llenar array ubicaciones
                        fillSpinnerUbicaciones(spinnerInvEditUbicacion, searchUbicaciones(txtInvEditUbicacion.getText().toString()));
                        txtInvEditContenido.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                float total = 0;
                                if (txtInvEditCantidad.getText().toString().equals("") || txtInvEditContenido.getText().toString().equals("")) {
                                    Toast.makeText(contexto, "Favor de llenar la información correctamente.", Toast.LENGTH_SHORT).show();
                                } else {
                                    total = Float.parseFloat(txtInvEditCantidad.getText().toString()) * Float.parseFloat(txtInvEditContenido.getText().toString());
                                    lblInvEditTotal.setText("" + total);
                                }
                                return false;
                            }
                        });

                        txtInvEditCantidad.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                float total = 0;
                                if (txtInvEditCantidad.getText().toString().equals("") || txtInvEditContenido.getText().toString().equals("")) {
                                    Toast.makeText(contexto, "Favor de llenar la información correctamente.", Toast.LENGTH_SHORT).show();
                                } else {
                                    total = Float.parseFloat(txtInvEditCantidad.getText().toString()) * Float.parseFloat(txtInvEditContenido.getText().toString());
                                    lblInvEditTotal.setText("" + total);
                                }
                                return false;
                            }
                        });

                        txtInvEditUbicacion.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if (txtInvEditUbicacion.getText().toString().equals("")) {
                                    //Mostrar todas las ubicaciones
                                    fillSpinnerUbicaciones(spinnerInvEditUbicacion, ubicaciones);
                                } else {
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
                                if (txtInvEditCantidad.getText().toString().equals("") || txtInvEditContenido.getText().toString().equals("") || txtInvEditUbicacion.getText().toString().equals("")) {
                                    new MaterialAlertDialogBuilder(contexto)
                                            .setTitle("¡Error de información!")
                                            .setIcon(R.drawable.confirmacion)
                                            .setMessage("Por favor, verifique que la información sea correcta y completa, vuelvalo a intentar")
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //No hacer nada
                                                }
                                            })
                                            .show();
                                } else {
                                    ubicacionSeleccionada = (Ubicacion) spinnerInvEditUbicacion.getSelectedItem();
                                    inventariosDetalles.get(getAdapterPosition()).setCantidad(txtInvEditCantidad.getText().toString());
                                    inventariosDetalles.get(getAdapterPosition()).setLongitud(txtInvEditContenido.getText().toString());
                                    inventariosDetalles.get(getAdapterPosition()).setUbicacion(ubicacionSeleccionada.toString());
                                    inventariosDetalles.get(getAdapterPosition()).setUbicacionId(ubicacionSeleccionada.getUbicacionId());
                                    if (!txtInvEditIncidencia.getText().toString().equals("")) {
                                        inventariosDetalles.get(getAdapterPosition()).setEstado("1");
                                    } else {
                                        inventariosDetalles.get(getAdapterPosition()).setEstado("0");
                                    }
                                    inventariosDetalles.get(getAdapterPosition()).setIncidencia(txtInvEditIncidencia.getText().toString());
                                    try {
                                        adaptadorDetalles.notifyDataSetChanged();
                                        dialog.dismiss();
                                        new MaterialAlertDialogBuilder(contexto)
                                                .setTitle("¡Éxito")
                                                .setIcon(R.drawable.correcto)
                                                .setMessage("Registro actualizado exitosamente.")
                                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //No hacer nada
                                                    }
                                                }).show();
                                    } catch (Exception e) {
                                        Toast.makeText(contexto, "Error, consulte con el administrador del sistema.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                        spinnerInvEditUbicacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Ubicacion ubicacion = (Ubicacion) spinnerInvEditUbicacion.getSelectedItem();
                                if (ubicacion.toString().equals("")) {

                                } else {
                                    txtInvEditUbicacion.setText("" + ubicacion.toString());
                                }
                                spinnerInvEditUbicacion.setSelection(5);
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
                ArrayAdapter<Ubicacion> adapter = new ArrayAdapter<Ubicacion>(contexto, R.layout.support_simple_spinner_dropdown_item, ubicacionesArray);
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
                //Mostrar los item de la lista
                lblNumRegistro.setText(""+(position+1));
                lblMaterialRegistro.setText(material);
                if(inventariosDetalles.get(position).getIsSelected() == 1){
                    checkBoxInvRegistro.setChecked(true);
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
                if(inventariosDetalles.get(position).getIncidencia().equals("") || inventariosDetalles.get(position).getIncidencia() == null){
                    imgInvItemEstadoMercancia.setImageResource(R.drawable.correcto);
                    lblInvItemIncidencia.setText(inventariosDetalles.get(position).getIncidencia());
                }else{
                    imgInvItemEstadoMercancia.setImageResource(R.drawable.snakerojo);
                    lblInvItemIncidencia.setText(inventariosDetalles.get(position).getIncidencia());
                }
            }

            @Override
            public void onClick(@NonNull View view){
                //No hace nada
            }
        }
    }

}