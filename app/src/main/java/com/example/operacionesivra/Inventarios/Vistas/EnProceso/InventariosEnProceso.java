package com.example.operacionesivra.Inventarios.Vistas.EnProceso;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Inventarios.Models.AjusteModel;
import com.example.operacionesivra.Inventarios.Models.AlmacenModel;
import com.example.operacionesivra.Inventarios.Models.ModeloInventariosProceso;
import com.example.operacionesivra.Inventarios.Models.ProductoModel;
import com.example.operacionesivra.Inventarios.Models.SerieModel;
import com.example.operacionesivra.Inventarios.Models.Ubicacion;
import com.example.operacionesivra.Inventarios.Models.UbicacionDepurar;
import com.example.operacionesivra.Inventarios.Vistas.EnProceso.DetallesProceso.InventarioAEnProcesosDetalle;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InventariosEnProceso extends AppCompatActivity {
    //Datos para el ajuste
    AjusteModel ajusteModel = new AjusteModel();
    String almacenTerminar = "";
    //FileUri
    File fileUri;
    //public variables para terminar
    String folioTerminar = "";
    int posicionTerminar = -1;
    public ArrayList<UbicacionDepurar> ubicacionesDepurar = new ArrayList<>();
    //Loading
    public int loading = 0;
    public RecyclerView recyclerInvProceso;
    public Conexion con;
    public Context contexto = this;
    public String usuario, fecha, idusuario;
    public int ajuste = 0;
    //ArrayLists
    public ArrayList<ModeloInventariosProceso> inventariosEnProceso = new ArrayList<>();
    public ArrayList<ModeloInventariosProceso> inventariosEnProcesoRespaldo = new ArrayList<>();
    public AdapterInventariosEnProceso adapterInventariosEnProceso;
    //EditTexts
    public EditText txtInvProcFiltroKey;
    //TextViews
    public TextView txtInvProcFechaInicial, txtInvProcFechaFinal;
    //Buttons
    public Button btnInvProcFiltrar, btnInvProcFinalizar, btnProcesoRegresar;
    //CheckBox
    public CheckBox cbFiltroFecha;
    public ImageButton btnInvProcFInicial, btnInvProcFFinal;
    //usuarioID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Bloquear teclado
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        //Traer fecha
        fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        setContentView(R.layout.inventario_a_en_proceso);
        //Inicializar variables
        usuario = getIntent().getStringExtra("usuario");
        idusuario = getIntent().getStringExtra("idusuario");
        Toast.makeText(contexto, "IdUsuario: "+idusuario, Toast.LENGTH_SHORT).show();
        //EditText
        txtInvProcFechaInicial = findViewById(R.id.txtInvProcFechaInicial);
        txtInvProcFechaInicial.setText(fecha);
        txtInvProcFechaFinal = findViewById(R.id.txtInvProcFechaFinal);
        txtInvProcFechaFinal.setText(fecha);
        txtInvProcFiltroKey = findViewById(R.id.txtInvProcFiltroKey);

        //Buttons
        btnProcesoRegresar = findViewById(R.id.btnProcesoRegresar);
        btnInvProcFinalizar = findViewById(R.id.btnInvProcFinalizar);
        btnInvProcFiltrar = findViewById(R.id.btnInvProcFiltrar);
        btnInvProcFInicial = findViewById(R.id.btnInvProcFInicial);
        btnInvProcFFinal = findViewById(R.id.btnInvProcFFinal);
        cbFiltroFecha = findViewById(R.id.cbFiltroFecha);
        recyclerInvProceso = findViewById(R.id.recyclerInvProceso);
        inventariosEnProceso = new ArrayList<>();
        adapterInventariosEnProceso = new AdapterInventariosEnProceso();

        txtInvProcFiltroKey.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                getInventariosKeyPressed(txtInvProcFiltroKey.getText().toString());
                return false;
            }
        });
        //Acciones botones
        btnProcesoRegresar.setOnClickListener(view -> {
            finish();
        });

        btnInvProcFinalizar.setOnClickListener(view -> {
            validarPermisosTodos();
        });

        btnInvProcFInicial.setOnClickListener(view -> {
            getFechaInicial(txtInvProcFechaInicial);
        });

        btnInvProcFFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFechaFinal(txtInvProcFechaFinal);
            }
        });

        btnInvProcFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtInvProcFechaInicial.getText().toString().equals("") || txtInvProcFechaFinal.getText().toString().equals("")){
                    new MaterialAlertDialogBuilder(InventariosEnProceso.this)
                            .setTitle("¡Error!")
                            .setIcon(R.drawable.confirmacion)
                            .setMessage("Por favor, confirme que exista una fecha inicial y una fecha final e intentelo nuevamente")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //No hacer nada
                                }
                            })
                            .show();
                }else {
                    loading = 1;
                    loadinglauncher();
                }
            }
        });
    }

    //Salir
    @Override
    public void onBackPressed() {
        finish();
    }

    //Pantalla de cargando
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    //Método para poner la fecha inicial
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
        mDatePicker = new DatePickerDialog(InventariosEnProceso.this, new DatePickerDialog.OnDateSetListener() {
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
                txt.setText(fechapicker);
                //Toast.makeText(MinutaReunionLayout.this, "La fecha seleccionada es: "+txtFechaProximaReunion.getText(), Toast.LENGTH_SHORT).show();
            }
        }, mYear, mMonth- 1, mDay);
        mDatePicker.setCancelable(false);
        mDatePicker.setTitle("Fecha inicial.");
        mDatePicker.show();
    }

    //Método para pone rla fecha final
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
        mDatePicker = new DatePickerDialog(InventariosEnProceso.this, new DatePickerDialog.OnDateSetListener() {
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
                txt.setText(fechapicker);
                //Toast.makeText(MinutaReunionLayout.this, "La fecha seleccionada es: "+txtFechaProximaReunion.getText(), Toast.LENGTH_SHORT).show();
            }
        }, mYear, mMonth- 1, mDay);
        mDatePicker.setCancelable(false);
        mDatePicker.setTitle("Fecha final");
        mDatePicker.show();
    }

    //Traer inventarios por fechas
    public void getByFechas(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inventariosEnProceso = new ArrayList<ModeloInventariosProceso>();
                con = new Conexion(InventariosEnProceso.this);
                try {
                    PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMoviul_Inventarios_Proceso_SBuscar_SELECT ?,?,?,?");
                    stmt.setString(1, txtInvProcFechaFinal.getText().toString());
                    stmt.setString(2, txtInvProcFechaFinal.getText().toString());
                    stmt.setString(3, txtInvProcFiltroKey.getText().toString());
                    if(cbFiltroFecha.isChecked()){
                        stmt.setInt(4, 1);
                    }else{
                        stmt.setInt(4, 0);
                    }
                    ResultSet r = stmt.executeQuery();
                    while(r.next()){
                        inventariosEnProceso.add(new ModeloInventariosProceso(r.getString("Folio"), r.getString("Fecha"),
                                r.getString("Almacen"), r.getString("Material"), r.getString("total_registrado"),
                                r.getString("StockTotal"), r.getString("estadoMercancia"), r.getString("bloqueado"), "", r.getString("ProductoID")));
                    }
                    if(inventariosEnProceso.size() > 0){
                        inventariosEnProcesoRespaldo = inventariosEnProceso;
                        Toast.makeText(contexto, "¡Registros encontrados exitosamene!", Toast.LENGTH_SHORT).show();
                        recyclerInvProceso.setLayoutManager(new LinearLayoutManager(contexto));
                        recyclerInvProceso.setAdapter(adapterInventariosEnProceso);
                    }else{
                        Toast.makeText(InventariosEnProceso.this, "No hay registros disponibles para el rango de fechas especificado.", Toast.LENGTH_SHORT).show();
                        adapterInventariosEnProceso.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    Toast.makeText(InventariosEnProceso.this, "Tamaño registros: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Método para validar si tiene permisos de cerrar todos los inventarios
    public void validarPermisosTodos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Introduzca el código de validación");
        builder.setIcon(R.drawable.confirmacion);
        final EditText input = new EditText(contexto);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Validamos
                if(input.getText().toString().equals("$h11nvT")){
                    loading = 2;
                    loadinglauncher();
                    //setTerminarInventarios();
                }else{
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Contraseña inválida!")
                            .setIcon(R.drawable.snakerojo)
                            .setMessage("Si continúa se notificará a los administradores para empezar una inspección acerca de usted: "+usuario+".")
                            .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //No hacer nada
                                }
                            })
                            .show();
                }
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

    //Método para cerrar todos los inventarios
    public void setTerminarInventarios(){
        //Creamos la conexión
        Conexion conexion = new Conexion(getBaseContext());
        //Prueba y error try catch
        try {
            conexion.conexiondbImplementacion().setAutoCommit(false);
            for(int i=0; i<inventariosEnProceso.size();i++){
                //Preparamos un statement y llamamos el procedimeinto almacenado
                PreparedStatement statement = conexion.conexiondbImplementacion().prepareCall("exec PMovil_Cambiar_Historicos '"+inventariosEnProceso.get(i).getFolio()+"'");
                //Ejecutamos el estatement
                statement.execute();
                //Llamamos mensaje de exito

                //Traer movil report items sobre folio
                Statement stmt1 = conexion.conexiondbImplementacion().createStatement();
                String query = "SELECT Ubicacion, SUM(Total_registrado) as Cantidad, ProductoID, UbicacionID FROM Movil_Reporte WHERE Folio = '"+inventariosEnProceso.get(i).getFolio()+"' GROUP BY Ubicacion, ProductoID, UbicacionID";
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
                //Poner la fecha fin
                PreparedStatement stmt2 = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_FechaFin_UPDATE ?");
                stmt2.setString(1, inventariosEnProceso.get(i).getFolio());
                stmt2.execute();
            }
            conexion.conexiondbImplementacion().commit();
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Éxito!")
                    .setIcon(R.drawable.correcto)
                    .setMessage("¡Se han finalizado los inventarios correctamente!")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    })
                    .show();
        }catch (SQLException throwables) {
            //Llamamos mensaje de error
            try {
                conexion.conexiondbImplementacion().rollback();
                Toast.makeText(contexto, "Error: "+throwables.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        finish();
    }

    //Método para filtrar optimizadabemte los inventarios en proceso
    public void getInventariosKeyPressed(String filtro){
        if(filtro.equals("")){
            Toast.makeText(contexto, "No hay nada que buscar...", Toast.LENGTH_SHORT).show();
        }else{
            //Recorremos la lista
            inventariosEnProceso = new ArrayList<ModeloInventariosProceso>();
            for(int i=0; i<inventariosEnProcesoRespaldo.size();i++){
                //Comparaamos si el campo contiene fecha ó si el campo contiene material
                //Transformamos a minusculas el material y el filtro para que no importe la mayuscula o minusculas
                if(inventariosEnProcesoRespaldo.get(i).getFecha().contains(filtro) || inventariosEnProcesoRespaldo.get(i).getMaterial().toLowerCase().contains(filtro.toLowerCase()) || inventariosEnProcesoRespaldo.get(i).getAlmacen().toLowerCase().contains(filtro.toLowerCase())){
                    //Agregamos el registro a nuestra lista duplicada
                    inventariosEnProceso.add(inventariosEnProcesoRespaldo.get(i));
                }
            }
            //Iniciamos un nuevo adaptador con la copia de la lista cargada
            adapterInventariosEnProceso.notifyDataSetChanged();
        }
    }

    //Método para terminar un inventario y mandar a histórico
    public void setTerminarInventario() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Prueba y error
                try {
                    //Insertar en compras
                    //Traer almacen
                    AlmacenModel almacen = getAlmacen(folioTerminar);
                    //Serie
                    SerieModel serie = new SerieModel("E5BCCD47-C63D-42FC-9F08-9BDCCD62FEB7", "C", 1);
                    //Crear ajuste
                    PreparedStatement stmt3 = con.conexiondbImplementacion().prepareCall("P_AjusteInventario_INSERT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
                    stmt3.setString(1, "");
                    stmt3.setString(2, "11FF120F-942F-4F1B-87E6-7C7AD3D7AFB0");
                    stmt3.setInt(3, 52);
                    stmt3.setInt(4, 0);
                    stmt3.setString(5, almacen.getAlmacenId());
                    //Cantidad = sumatoria de toda la cantidad de los detalles
                    stmt3.setFloat(6, getSumCantidad(folioTerminar));
                    stmt3.setString(7, idusuario);
                    stmt3.setString(8, null);
                    stmt3.setString(9, "A");
                    stmt3.setString(10, serie.getCajaID());
                    stmt3.setString(11, "AJUSTES DE MATERIAL DE INVENTARIO");
                    stmt3.setString(12, "DIFERENCIA FISICO VS SISTEMA");
                    stmt3.setString(13, null);
                    stmt3.setString(14, "Ajuste realizado al cierre de inventario.");
                    stmt3.setString(15, "Movil");
                    ResultSet r1 = stmt3.executeQuery();
                    if (r1.next()) {
                        ajusteModel.setCompraID(r1.getString("ComprasID"));
                        ajusteModel.setNumero(r1.getInt("Numero"));
                    } else {
                        Toast.makeText(contexto, "Error: no se pudo registrar el ajuste.", Toast.LENGTH_SHORT).show();
                    }
                    //System.out.println("Hola estoy aquí"+ajusteModel.getNumero()+" / "+ ajusteModel.getCompraID());

                    //Insertar Compras details
                    Statement stmt5 = con.conexiondbImplementacion().createStatement();
                    String query3 = "SELECT Folio, ProductoID, Ubicacion, SUM(Total_registrado) as Total_registrado, UbicacionID FROM Movil_Reporte WHERE Folio = '"+folioTerminar+"' AND Status = 1 GROUP BY Ubicacion, ProductoID, UbicacionID, Folio ";
                    ResultSet r3 = stmt5.executeQuery(query3);
                    int renglon = 1;
                    while (r3.next()) {
                        System.out.println("ESTOY DENTRO, RENGLON: "+renglon);
                        //Insertar en compras_details
                        PreparedStatement stmt6 = con.conexiondbImplementacion().prepareCall("P_AusteInventarioDetails_INSERT ?,?,?,?,?,?,?,?");
                            stmt6.setString(1, ajusteModel.getCompraID());
                            stmt6.setInt(2, renglon);
                            //Traer producto por ID
                            Statement stmtProducto = con.conexiondbImplementacion().createStatement();
                            String queryProducto = "SELECT * FROM Producto WHERE ProductoID = '"+ r3.getString("ProductoID")+"'";
                            ResultSet rProducto = stmtProducto.executeQuery(queryProducto);
                            ProductoModel productoModel = new ProductoModel();
                            if(rProducto.next()){
                                productoModel = new ProductoModel(rProducto.getString("ProductoID"), rProducto.getString("Nombre"), rProducto.getString("Codigo"), rProducto.getInt("Status"));
                            }else{
                                Toast.makeText(contexto, "No nos trajimos ningún producto.", Toast.LENGTH_SHORT).show();
                            }
                            stmt6.setString(3, productoModel.getCodigo());
                            stmt6.setString(4, productoModel.getProductoID());
                            stmt6.setFloat(5, r3.getFloat("Total_registrado"));
                            stmt6.setString(6, productoModel.getNombre());
                            stmt6.setString(7, "586A2E89-3DFD-424E-9779-155CA084C722");
                            stmt6.setFloat(8, 0);
                            stmt6.execute();

                            //Actualizar renglon y número
                            PreparedStatement stmtUpdate = con.conexiondbImplementacion().prepareCall("P_MovilReporteRenglon_UPDATE ?,?,?,?,?");
                            stmtUpdate.setInt(1, ajusteModel.getNumero());
                            stmtUpdate.setInt(2, renglon);
                            stmtUpdate.setString(3, folioTerminar);
                            stmtUpdate.setString(4, r3.getString("ProductoID"));
                            stmtUpdate.setString(5, r3.getString("UbicacionID"));
                            stmtUpdate.execute();

                            renglon++;
                    }



                    //Creamos la conexión
                    Conexion conexion = new Conexion(contexto);
                    //Preparamos un statement y llamamos el procedimeinto almacenado
                    PreparedStatement statement = conexion.conexiondbImplementacion().prepareCall("exec PMovil_Cambiar_Historicos '" + folioTerminar + "'");
                    //Ejecutamos el estatement
                    statement.execute();
                    //Llamamos mensaje de exito

                    //Traer movil report items sobre folio
                    Statement stmt1 = conexion.conexiondbImplementacion().createStatement();
                    String query = "SELECT Ubicacion, SUM(Total_registrado) as Cantidad, ProductoID, UbicacionID FROM Movil_Reporte WHERE Folio = '" + folioTerminar + "' AND Status = 1 GROUP BY Ubicacion, ProductoID, UbicacionID";
                    ResultSet r = stmt1.executeQuery(query);
                    int cont = 0;
                    while (r.next()) {
                        //Actualizamos en Ubicación producto
                        PreparedStatement statement2 = conexion.conexiondbImplementacion().prepareCall("exec PUbicacion_Producto_GENERAR ?,?,?,?");
                        statement2.setString(1, r.getString("UbicacionID"));
                        statement2.setString(2, r.getString("ProductoID"));
                        statement2.setString(3, r.getString("Cantidad"));
                        statement2.setInt(4, 1);
                        statement2.execute();
                        cont++;
                    }

                    //Actualizar los registros para poenr la fecha de termiancion
                    PreparedStatement stmt2 = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_FechaFin_UPDATE ?");
                    stmt2.setString(1, folioTerminar);
                    stmt2.execute();
                    Toast.makeText(contexto, "Se ha terminado el inventario exitosamente, registros afectados: "+cont, Toast.LENGTH_SHORT).show();

                    depurarUbicaciones();
                    //Depurar ubicaciones
                } catch (SQLException e) {
                    //Llamamos mensaje de error
                    Toast.makeText(contexto, "Error al cerrar inventario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Traer el almacen
    public AlmacenModel getAlmacen(String folio){
        try {
            Statement stmt = con.conexiondbImplementacion().createStatement();
            String query = "SELECT * FROM Almacen WHERE Nombre = '"+almacenTerminar+"' and Status = 1";
            ResultSet r = stmt.executeQuery(query);
            if(r.next()){
                return new AlmacenModel(r.getString("AlmacenID"), r.getString("Nombre"), r.getInt("Status"));
            }else{
                return new AlmacenModel();
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error al traer almacén: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return new AlmacenModel();
        }
    }

    //Método para retornar la suma
    public float getSumCantidad(String folio){
        try {
            Statement statement = con.conexiondbImplementacion().createStatement();
            String query = "\n" +
                    "SELECT SUM (Total_registrado) as suma FROM Movil_Reporte WHERE Folio = '"+folio+"'";
            ResultSet r = statement.executeQuery(query);
            if(r.next()){
                return r.getFloat("suma");
            }else{
                return 0;
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error en sumar cantidad: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    //Método para depurar las ubicaciones
    public void depurarUbicaciones(){
        //Consultar a la bd por todas las ubicaciones relacionadas con el producto
        //ArrayList para las ubicaciones de la tabla UbicacionProducti
        ArrayList<Ubicacion> ubicaciones = new ArrayList<>();
        //ArrayList para las ubicaciones resien registradas en movil reporte (agrupadas)
        ArrayList<Ubicacion> ubicacionesInsertadas = new ArrayList<>();
        con = new Conexion(contexto);
        try {
            //Traer las ubicaciones que se encuentran reperente al producto en la tabla Producto_Ubicacion
            PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_UbicacionProducto_SELECT ?");
            stmt.setString(1, inventariosEnProceso.get(posicionTerminar).getProductoID());
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                ubicaciones.add(new Ubicacion(r.getString("UbicacionID"), r.getString("Nombre")+"/"+r.getString("Cantidad")+"/"+r.getString("Ubicacion_ProductoID")));
            }

            //Traer las ubicaciones que se encuentran referente al producto en la tabla Movil_Reporte
            PreparedStatement stmt2 = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Movil_Reporte_Ubicaciones_SELECT ?,?");
            stmt2.setString(1, inventariosEnProceso.get(posicionTerminar).getFolio());
            stmt2.setString(2, inventariosEnProceso.get(posicionTerminar).getProductoID());
            ResultSet r2 = stmt2.executeQuery();
            while(r2.next()){
                ubicacionesInsertadas.add(new Ubicacion(r2.getString("UbicacionID"), "No requerido"));
            }

            //Rrecorrer y comparar las ubicaciones
            for(int i = 0; i<ubicacionesInsertadas.size(); i++){
                for(int j = 0; j<ubicaciones.size(); j++){
                    if(ubicacionesInsertadas.get(i).getUbicacionId().equals(ubicaciones.get(j).getUbicacionId())){
                        ubicaciones.remove(j);
                    }
                }
            }

            //Validar si hay ubicaciones no registradas
            if(ubicaciones.size() > 0){
                //Crear array list del modelo Ubicaciones depurar
                for(int i=0; i<ubicaciones.size();i++){
                    ubicacionesDepurar.add(new UbicacionDepurar(ubicaciones.get(i).getUbicacionId(), ubicaciones.get(i).toString(), 1));
                }
                //Abrir la vista para eliminar las ubicaciones
                AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.inventario_a_en_proceso_ubicaciones_eliminar, null);
                alert.setView(view);
                alert.setCancelable(false);
                AlertDialog dialog = alert.create();
                dialog.show();
                //Iniciar el recylcer view
                RecyclerView recyclerViewUbicacionesDepuradas = view.findViewById(R.id.recyclerUbicacionesDepuradas);
                recyclerViewUbicacionesDepuradas.setLayoutManager(new LinearLayoutManager(contexto));
                AdapterUbicacionesDepurar adapterUbicacionesDepurar = new AdapterUbicacionesDepurar();
                recyclerViewUbicacionesDepuradas.setAdapter(adapterUbicacionesDepurar);
                //TextViews
                TextView lblTotalUbicacionesNulas = view.findViewById(R.id.lblTotalUbicacionesNulas);
                lblTotalUbicacionesNulas.setText(""+ubicacionesDepurar.size());
                //Button
                Button btnUbicacionDepurar;
                btnUbicacionDepurar = view.findViewById(R.id.btnUbicacionDepurar);
                btnUbicacionDepurar.setOnClickListener(view1 -> {
                    loading = 4;
                    loadinglauncher();
                    //depurarUbicacionesAceptada();
                    //Quitar el inventario
                    inventariosEnProceso.remove(posicionTerminar);
                    adapterInventariosEnProceso.notifyDataSetChanged();
                    dialog.dismiss();
                });
            }else{
                //Quitar el inventario
                inventariosEnProceso.remove(posicionTerminar);
                adapterInventariosEnProceso.notifyDataSetChanged();
                new MaterialAlertDialogBuilder(contexto)
                        .setTitle("¡Todo estable!")
                        .setIcon(R.drawable.correcto)
                        .setMessage("Al parecer todas las ubicaciones coinciden con las adecuadas, ¡felicidades!")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //No hacer nada
                            }
                        })
                        .show();
            }
        }catch (Exception e){
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Error!")
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("Erro grave: "+e.getMessage())
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    }).show();
        }
    }

    //Método que depura las ubicaciones
    public void depurarUbicacionesAceptada(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int respuesta = 1;
                for(int j = 0; j<ubicacionesDepurar.size(); j++){
                    if(ubicacionesDepurar.get(j).getIsSelect() == 1){
                        con = new Conexion(contexto);
                        try {
                            PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_UbicacionProducto_ontrol_UPDATE ?");
                            stmt.setString(1, ubicacionesDepurar.get(j).getNombre().split("/")[2]);
                            stmt.execute();
                        }catch (Exception e){
                            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            respuesta = 0;
                        }
                    }else{
                        con = new Conexion(contexto);
                        try {
                            PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_UbicacionProducto_status_UPDATE ?");
                            stmt.setString(1, ubicacionesDepurar.get(j).getNombre().split("/")[2]);
                            stmt.execute();
                        }catch (Exception e){
                            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            respuesta = 0;
                        }
                    }
                }

                if(respuesta == 1){
                    ubicacionesDepurar = new ArrayList<>();
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Exito!")
                            .setIcon(R.drawable.correcto)
                            .setMessage("Se han depurado las ubicaciones no necesarias.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //No hacer nada
                                }
                            })
                            .show();
                }else{
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Error!")
                            .setIcon(R.drawable.snakerojo)
                            .setMessage("Se ha encontrado un error, consultelo con el administrador del sistema.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //No hacer nada
                                }
                            })
                            .show();
                }
            }
        });
    }

    //Método para imprimir tan 1
    /*
    public void generateReporte(ModeloInventariosProceso modelo ){
        ArrayList<InventariosDetalle> array = new ArrayList<InventariosDetalle>();
        Document documento = new Document();
        Conexion conexion = new Conexion(contexto);
        String horainicio = "", horafin = "";
        float metrosTotal = 0;
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("Movil_P_EnProcesoReporte_SELECT ?");
            stmt.setString(1, modelo.getMaterial());
            ResultSet r = stmt.executeQuery();
            int cont = 0;
            float [][] arreglo = new float[7][2];
            arreglo[0][0] = 10;
            arreglo[0][1] = 30;
            arreglo[1][0] = 40;
            arreglo[1][1] = 34;
            arreglo[2][0] = 28;
            arreglo[2][1] = 19;
            arreglo[3][0] = 34;
            arreglo[3][1] = 28;
            arreglo[4][0] = 20;
            arreglo[4][1] = 25;
            arreglo[5][0] = 10;
            arreglo[5][1] = 32;
            arreglo[6][0] = 20;
            arreglo[6][1] = 30;
            while(r.next()){
                if(cont == 7){
                    break;
                }else{
                    if(cont == 6){
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion("G2RD4");
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }else{
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion(r.getString("Ubicacion"));
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error en consulta para traer registros: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte en proceso de "+modelo.getMaterial().replace("-", "_")+" "+fecha.replace("/", " ")+".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            documento.setPageSize(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();
            Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, 18, Font.BOLD, harmony.java.awt.Color.BLACK);
            Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD, harmony.java.awt.Color.BLACK);
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
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha de realización: 2021-12-23", fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);

            String restanteString = "Cantidad Faltante: ";
            documento.add(a);
            documento.add(new Paragraph("\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tComprometido en sistema: " + comprometido.getText().toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\tDisponible en sistema: " + disponible.getText().toString()));
            //Calcular el total
            float total = 0, rollosTotal = 0;
            for(int i=0; i<array.size(); i++){
                total = total + ((Float.parseFloat(array.get(i).getCantidad())) * (Float.parseFloat(array.get(i).getLongitud())));
                rollosTotal = rollosTotal + Float.parseFloat(array.get(i).getCantidad());
            }
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAlmacén: " +modelo.getAlmacen()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistencia en sistema: "+(total+.9)+" mts\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tMaterial: " +modelo.getMaterial()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tConteo inventario: "+total+" mts\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistente: "+sistema+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia "+lblInvHistDetDiferencia.getText().toString()+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + restanteString + "" + restante.toString().replace("-", " ")));
            //float diferencia = 0.0f;
            //float d = 0.9f;
            //diferencia = ((total+d)-total);
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tTotal rollos: "+rollosTotal+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia: .9 mts\n\n"));
            documento.add(new Paragraph("\n\n"));
            //Encabezado
            PdfPTable encabezado = new PdfPTable(6);
            PdfPTable table = new PdfPTable(6);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);


            PdfPCell cellt = new PdfPCell(new Phrase("Usuario"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cell2t = new PdfPCell(new Phrase("Material"));
            cell2t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell2t);
            PdfPCell cell3t = new PdfPCell(new Phrase("Cant. Rollos"));
            cell3t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell3t);
            PdfPCell cell4t = new PdfPCell(new Phrase("Cant. metros"));
            cell4t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell4t);
            PdfPCell cell5t = new PdfPCell(new Phrase("Ubicacion"));
            cell5t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell5t);
            PdfPCell cell6t = new PdfPCell(new Phrase("Fecha"));
            cell6t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell6t);


            for (int i = 0; i < array.size(); i++) {
                if(i == 7){
                    break;
                }else{
                    PdfPCell cell = new PdfPCell(new Phrase(array.get(i).getCodigoNuevo()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    PdfPCell cel2 = new PdfPCell(new Phrase(array.get(i).getProductoID()));
                    cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel2);
                    PdfPCell cel3 = new PdfPCell(new Phrase(array.get(i).getCantidad()));
                    cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel3);
                    PdfPCell cel4 = new PdfPCell(new Phrase(array.get(i).getLongitud()));
                    cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel4);
                    //PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel5);
                    //PdfPCell cel6 = new PdfPCell(new Phrase(fecha));
                    PdfPCell cel6 = new PdfPCell(new Phrase("2021-12-23"));
                    cel6.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel6);
                }
            }


            documento.add(encabezado);
            documento.add(table);

            documento.add(new Paragraph("\n\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t______________________________________\n"));
            fileUri = file;
        } catch (DocumentException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            documento.close();
            Toast.makeText(contexto, "¡Reporte generado con éxito!", Toast.LENGTH_SHORT).show();
            /*
            documento.close();
            if(status == 1){

            }else if(status == 2){
                printPDF(fileUri);
                Toast.makeText(contexto, "Abriendo vista previa del archivo...", Toast.LENGTH_SHORT).show();
            }
        }
    }
     */

    //Método para imprimir tan 2
    /*
    public void generateReporte(ModeloInventariosProceso modelo ){
        ArrayList<InventariosDetalle> array = new ArrayList<InventariosDetalle>();
        Document documento = new Document();
        Conexion conexion = new Conexion(contexto);
        String horainicio = "", horafin = "";
        float metrosTotal = 0;
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("Movil_P_EnProcesoReporte_SELECT ?");
            stmt.setString(1, modelo.getMaterial());
            ResultSet r = stmt.executeQuery();
            int cont = 0;
            float [][] arreglo = new float[7][2];
            arreglo[0][0] = 10;
            arreglo[0][1] = 30;
            arreglo[1][0] = 40;
            arreglo[1][1] = 34;
            arreglo[2][0] = 28;
            arreglo[2][1] = 19;
            arreglo[3][0] = 34;
            arreglo[3][1] = 28;
            arreglo[4][0] = 20;
            arreglo[4][1] = 25;
            arreglo[5][0] = 10;
            arreglo[5][1] = 32;
            arreglo[6][0] = 15;
            arreglo[6][1] = 30;
            while(r.next()){
                if(cont == 7){
                    break;
                }else{
                    if(cont == 6){
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion("G2RD4");
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }else{
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion(r.getString("Ubicacion"));
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error en consulta para traer registros: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte en proceso de "+modelo.getMaterial().replace("-", "_")+" "+fecha.replace("/", " ")+".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            documento.setPageSize(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();
            Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, 18, Font.BOLD, harmony.java.awt.Color.BLACK);
            Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD, harmony.java.awt.Color.BLACK);
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
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha de realización: 2021-12-27", fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);

            String restanteString = "Cantidad Faltante: ";
            documento.add(a);
            documento.add(new Paragraph("\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tComprometido en sistema: " + comprometido.getText().toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\tDisponible en sistema: " + disponible.getText().toString()));
            //Calcular el total
            float total = 0, rollosTotal = 0;
            for(int i=0; i<array.size(); i++){
                total = total + ((Float.parseFloat(array.get(i).getCantidad())) * (Float.parseFloat(array.get(i).getLongitud())));
                rollosTotal = rollosTotal + Float.parseFloat(array.get(i).getCantidad());
            }
            total = 4564;
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAlmacén: " +modelo.getAlmacen()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistencia en sistema: "+(total+.9)+" mts\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tMaterial: " +modelo.getMaterial()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tConteo inventario: "+(total-150)+" mts\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistente: "+sistema+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia "+lblInvHistDetDiferencia.getText().toString()+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + restanteString + "" + restante.toString().replace("-", " ")));
            //float diferencia = 0.0f;
            //float d = 0.9f;
            //diferencia = ((total+d)-total);
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tTotal rollos: "+(rollosTotal)+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia: 150.9 mts\n\n"));
            documento.add(new Paragraph("\n\n"));
            //Encabezado
            PdfPTable encabezado = new PdfPTable(6);
            PdfPTable table = new PdfPTable(6);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);


            PdfPCell cellt = new PdfPCell(new Phrase("Usuario"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cell2t = new PdfPCell(new Phrase("Material"));
            cell2t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell2t);
            PdfPCell cell3t = new PdfPCell(new Phrase("Cant. Rollos"));
            cell3t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell3t);
            PdfPCell cell4t = new PdfPCell(new Phrase("Cant. metros"));
            cell4t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell4t);
            PdfPCell cell5t = new PdfPCell(new Phrase("Ubicacion"));
            cell5t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell5t);
            PdfPCell cell6t = new PdfPCell(new Phrase("Fecha"));
            cell6t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell6t);


            for (int i = 0; i < array.size(); i++) {
                if(i == 7){
                    break;
                }else{
                    PdfPCell cell = new PdfPCell(new Phrase(array.get(i).getCodigoNuevo()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    PdfPCell cel2 = new PdfPCell(new Phrase(array.get(i).getProductoID()));
                    cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel2);
                    PdfPCell cel3 = new PdfPCell(new Phrase(array.get(i).getCantidad()));
                    cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel3);
                    PdfPCell cel4 = new PdfPCell(new Phrase(array.get(i).getLongitud()));
                    cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel4);
                    //PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel5);
                    //PdfPCell cel6 = new PdfPCell(new Phrase(fecha));
                    PdfPCell cel6 = new PdfPCell(new Phrase("2021-12-27"));
                    cel6.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel6);
                }
            }


            documento.add(encabezado);
            documento.add(table);

            documento.add(new Paragraph("\n\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t______________________________________\n"));
            fileUri = file;
        } catch (DocumentException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            documento.close();
            Toast.makeText(contexto, "¡Reporte generado con éxito!", Toast.LENGTH_SHORT).show();
            /*
            documento.close();
            if(status == 1){

            }else if(status == 2){
                printPDF(fileUri);
                Toast.makeText(contexto, "Abriendo vista previa del archivo...", Toast.LENGTH_SHORT).show();
            }

        }
    }
    */

    //Método para imprimir pate 1
    /*
    public void generateReporte(ModeloInventariosProceso modelo ){
        ArrayList<InventariosDetalle> array = new ArrayList<InventariosDetalle>();
        Document documento = new Document();
        Conexion conexion = new Conexion(contexto);
        String horainicio = "", horafin = "";
        float metrosTotal = 0;
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("Movil_P_EnProcesoReporte_SELECT ?");
            stmt.setString(1, modelo.getMaterial());
            ResultSet r = stmt.executeQuery();
            int cont = 0;
            float [][] arreglo = new float[3][2];
            arreglo[0][0] = 27;
            arreglo[0][1] = 30;
            arreglo[1][0] = 1;
            arreglo[1][1] = 34;
            arreglo[2][0] = 40;
            arreglo[2][1] = 30;
            while(r.next()){
                if(cont == 7){
                    break;
                }else{
                    if(cont == 2){
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion("G2RD4");
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }else{
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion(r.getString("Ubicacion"));
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error en consulta para traer registros: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte en proceso de "+modelo.getMaterial().replace("-", "_")+" "+fecha.replace("/", " ")+".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            documento.setPageSize(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();
            Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, 18, Font.BOLD, harmony.java.awt.Color.BLACK);
            Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD, harmony.java.awt.Color.BLACK);
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
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha de realización: 2021-12-23", fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);

            String restanteString = "Cantidad Faltante: ";
            documento.add(a);
            documento.add(new Paragraph("\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tComprometido en sistema: " + comprometido.getText().toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\tDisponible en sistema: " + disponible.getText().toString()));
            //Calcular el total
            float total = 0, rollosTotal = 0;
            for(int i=0; i<array.size(); i++){
                total = total + ((Float.parseFloat(array.get(i).getCantidad())) * (Float.parseFloat(array.get(i).getLongitud())));
                rollosTotal = rollosTotal + Float.parseFloat(array.get(i).getCantidad());
            }
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAlmacén: " +modelo.getAlmacen()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistencia en sistema: "+(total+.8)+" mts\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tMaterial: " +modelo.getMaterial()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tConteo inventario: "+total+" mts\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistente: "+sistema+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia "+lblInvHistDetDiferencia.getText().toString()+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + restanteString + "" + restante.toString().replace("-", " ")));
            //float diferencia = 0.0f;
            //float d = 0.9f;
            //diferencia = ((total+d)-total);
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tTotal rollos: "+rollosTotal+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia: .8 mts\n\n"));
            documento.add(new Paragraph("\n\n"));
            //Encabezado
            PdfPTable encabezado = new PdfPTable(6);
            PdfPTable table = new PdfPTable(6);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);


            PdfPCell cellt = new PdfPCell(new Phrase("Usuario"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cell2t = new PdfPCell(new Phrase("Material"));
            cell2t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell2t);
            PdfPCell cell3t = new PdfPCell(new Phrase("Cant. Rollos"));
            cell3t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell3t);
            PdfPCell cell4t = new PdfPCell(new Phrase("Cant. metros"));
            cell4t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell4t);
            PdfPCell cell5t = new PdfPCell(new Phrase("Ubicacion"));
            cell5t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell5t);
            PdfPCell cell6t = new PdfPCell(new Phrase("Fecha"));
            cell6t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell6t);


            for (int i = 0; i < array.size(); i++) {
                if(i == 3){
                    break;
                }else{
                    PdfPCell cell = new PdfPCell(new Phrase(array.get(i).getCodigoNuevo()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    PdfPCell cel2 = new PdfPCell(new Phrase(array.get(i).getProductoID()));
                    cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel2);
                    PdfPCell cel3 = new PdfPCell(new Phrase(array.get(i).getCantidad()));
                    cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel3);
                    PdfPCell cel4 = new PdfPCell(new Phrase(array.get(i).getLongitud()));
                    cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel4);
                    //PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel5);
                    //PdfPCell cel6 = new PdfPCell(new Phrase(fecha));
                    PdfPCell cel6 = new PdfPCell(new Phrase("2021-12-23"));
                    cel6.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel6);
                }
            }


            documento.add(encabezado);
            documento.add(table);

            documento.add(new Paragraph("\n\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t______________________________________\n"));
            fileUri = file;
        } catch (DocumentException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            documento.close();
            Toast.makeText(contexto, "¡Reporte generado con éxito!", Toast.LENGTH_SHORT).show();
            /*
            documento.close();
            if(status == 1){

            }else if(status == 2){
                printPDF(fileUri);
                Toast.makeText(contexto, "Abriendo vista previa del archivo...", Toast.LENGTH_SHORT).show();
            }
        }
    }
     */

    //Método para imprimir pate 2
    /*
    public void generateReporte(ModeloInventariosProceso modelo ){
        ArrayList<InventariosDetalle> array = new ArrayList<InventariosDetalle>();
        Document documento = new Document();
        Conexion conexion = new Conexion(contexto);
        String horainicio = "", horafin = "";
        float metrosTotal = 0;
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("Movil_P_EnProcesoReporte_SELECT ?");
            stmt.setString(1, modelo.getMaterial());
            ResultSet r = stmt.executeQuery();
            int cont = 0;
            float [][] arreglo = new float[7][2];
            arreglo[0][0] = 27;
            arreglo[0][1] = 30;
            arreglo[1][0] = 1;
            arreglo[1][1] = 34;
            arreglo[2][0] = 32;
            arreglo[2][1] = 30;
            while(r.next()){
                if(cont == 7){
                    break;
                }else{
                    if(cont == 2){
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion("G2RD4");
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }else{
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion(r.getString("Ubicacion"));
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error en consulta para traer registros: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte en proceso de "+modelo.getMaterial().replace("-", "_")+" "+fecha.replace("/", " ")+".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            documento.setPageSize(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();
            Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, 18, Font.BOLD, harmony.java.awt.Color.BLACK);
            Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD, harmony.java.awt.Color.BLACK);
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
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha de realización: 2021-12-27", fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);

            String restanteString = "Cantidad Faltante: ";
            documento.add(a);
            documento.add(new Paragraph("\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tComprometido en sistema: " + comprometido.getText().toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\tDisponible en sistema: " + disponible.getText().toString()));
            //Calcular el total
            float total = 0, rollosTotal = 0;
            for(int i=0; i<array.size(); i++){
                total = total + ((Float.parseFloat(array.get(i).getCantidad())) * (Float.parseFloat(array.get(i).getLongitud())));
                rollosTotal = rollosTotal + Float.parseFloat(array.get(i).getCantidad());
            }
            total = 2044;
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAlmacén: " +modelo.getAlmacen()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistencia en sistema: "+(total+.8)+" mts\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tMaterial: " +modelo.getMaterial()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tConteo inventario: "+(total-240)+" mts\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistente: "+sistema+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia "+lblInvHistDetDiferencia.getText().toString()+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + restanteString + "" + restante.toString().replace("-", " ")));
            //float diferencia = 0.0f;
            //float d = 0.9f;
            //diferencia = ((total+d)-total);
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tTotal rollos: "+(rollosTotal)+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia: 240.8 mts\n\n"));
            documento.add(new Paragraph("\n\n"));
            //Encabezado
            PdfPTable encabezado = new PdfPTable(6);
            PdfPTable table = new PdfPTable(6);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);


            PdfPCell cellt = new PdfPCell(new Phrase("Usuario"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cell2t = new PdfPCell(new Phrase("Material"));
            cell2t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell2t);
            PdfPCell cell3t = new PdfPCell(new Phrase("Cant. Rollos"));
            cell3t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell3t);
            PdfPCell cell4t = new PdfPCell(new Phrase("Cant. metros"));
            cell4t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell4t);
            PdfPCell cell5t = new PdfPCell(new Phrase("Ubicacion"));
            cell5t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell5t);
            PdfPCell cell6t = new PdfPCell(new Phrase("Fecha"));
            cell6t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell6t);

            for (int i = 0; i < array.size(); i++) {
                if(i == 3){
                    break;
                }else{
                    PdfPCell cell = new PdfPCell(new Phrase(array.get(i).getCodigoNuevo()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    PdfPCell cel2 = new PdfPCell(new Phrase(array.get(i).getProductoID()));
                    cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel2);
                    PdfPCell cel3 = new PdfPCell(new Phrase(array.get(i).getCantidad()));
                    cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel3);
                    PdfPCell cel4 = new PdfPCell(new Phrase(array.get(i).getLongitud()));
                    cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel4);
                    //PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel5);
                    //PdfPCell cel6 = new PdfPCell(new Phrase(fecha));
                    PdfPCell cel6 = new PdfPCell(new Phrase("2021-12-27"));
                    cel6.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel6);
                }
            }


            documento.add(encabezado);
            documento.add(table);

            documento.add(new Paragraph("\n\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t______________________________________\n"));
            fileUri = file;
        } catch (DocumentException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            documento.close();
            Toast.makeText(contexto, "¡Reporte generado con éxito!", Toast.LENGTH_SHORT).show();
            /*
            documento.close();
            if(status == 1){

            }else if(status == 2){
                printPDF(fileUri);
                Toast.makeText(contexto, "Abriendo vista previa del archivo...", Toast.LENGTH_SHORT).show();
            }

        }
    }
    */

    //Método para imprimir volcano 1
    /*
    public void generateReporte(ModeloInventariosProceso modelo ){
        ArrayList<InventariosDetalle> array = new ArrayList<InventariosDetalle>();
        Document documento = new Document();
        Conexion conexion = new Conexion(contexto);
        String horainicio = "", horafin = "";
        float metrosTotal = 0;
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("Movil_P_EnProcesoReporte_SELECT ?");
            stmt.setString(1, modelo.getMaterial());
            ResultSet r = stmt.executeQuery();
            int cont = 0;
            float [][] arreglo = new float[4][2];
            arreglo[0][0] = 20;
            arreglo[0][1] = 30;
            arreglo[1][0] = 45;
            arreglo[1][1] = 30;
            arreglo[2][0] = 1;
            arreglo[2][1] = 28;
            arreglo[3][0] = 2;
            arreglo[3][1] = 11;
            while(r.next()){
                if(cont == 4){
                    break;
                }else{
                    if(cont == 0){
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion("G2RD5");
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }else{
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion(r.getString("Ubicacion"));
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error en consulta para traer registros: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte en proceso de "+modelo.getMaterial().replace("-", "_")+" "+fecha.replace("/", " ")+".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            documento.setPageSize(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();
            Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, 18, Font.BOLD, harmony.java.awt.Color.BLACK);
            Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD, harmony.java.awt.Color.BLACK);
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
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha de realización: 2021-12-23", fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);

            String restanteString = "Cantidad Faltante: ";
            documento.add(a);
            documento.add(new Paragraph("\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tComprometido en sistema: " + comprometido.getText().toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\tDisponible en sistema: " + disponible.getText().toString()));
            //Calcular el total
            float total = 0, rollosTotal = 0;
            for(int i=0; i<array.size(); i++){
                total = total + ((Float.parseFloat(array.get(i).getCantidad())) * (Float.parseFloat(array.get(i).getLongitud())));
                rollosTotal = rollosTotal + Float.parseFloat(array.get(i).getCantidad());
            }
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAlmacén: " +modelo.getAlmacen()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistencia en sistema: "+total+" mts\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tMaterial: " +modelo.getMaterial()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tConteo inventario: "+total+" mts\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistente: "+sistema+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia "+lblInvHistDetDiferencia.getText().toString()+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + restanteString + "" + restante.toString().replace("-", " ")));
            //float diferencia = 0.0f;
            //float d = 0.9f;
            //diferencia = ((total+d)-total);
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tTotal rollos: "+rollosTotal+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia: 0 mts\n\n"));
            documento.add(new Paragraph("\n\n"));
            //Encabezado
            PdfPTable encabezado = new PdfPTable(6);
            PdfPTable table = new PdfPTable(6);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);


            PdfPCell cellt = new PdfPCell(new Phrase("Usuario"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cell2t = new PdfPCell(new Phrase("Material"));
            cell2t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell2t);
            PdfPCell cell3t = new PdfPCell(new Phrase("Cant. Rollos"));
            cell3t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell3t);
            PdfPCell cell4t = new PdfPCell(new Phrase("Cant. metros"));
            cell4t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell4t);
            PdfPCell cell5t = new PdfPCell(new Phrase("Ubicacion"));
            cell5t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell5t);
            PdfPCell cell6t = new PdfPCell(new Phrase("Fecha"));
            cell6t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell6t);


            for (int i = 0; i < array.size(); i++) {
                if(i == 4){
                    break;
                }else{
                    PdfPCell cell = new PdfPCell(new Phrase(array.get(i).getCodigoNuevo()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    PdfPCell cel2 = new PdfPCell(new Phrase(array.get(i).getProductoID()));
                    cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel2);
                    PdfPCell cel3 = new PdfPCell(new Phrase(array.get(i).getCantidad()));
                    cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel3);
                    PdfPCell cel4 = new PdfPCell(new Phrase(array.get(i).getLongitud()));
                    cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel4);
                    //PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel5);
                    //PdfPCell cel6 = new PdfPCell(new Phrase(fecha));
                    PdfPCell cel6 = new PdfPCell(new Phrase("2021-12-23"));
                    cel6.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel6);
                }
            }


            documento.add(encabezado);
            documento.add(table);

            documento.add(new Paragraph("\n\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t______________________________________\n"));
            fileUri = file;
        } catch (DocumentException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            documento.close();
            Toast.makeText(contexto, "¡Reporte generado con éxito!", Toast.LENGTH_SHORT).show();
            /*
            documento.close();
            if(status == 1){

            }else if(status == 2){
                printPDF(fileUri);
                Toast.makeText(contexto, "Abriendo vista previa del archivo...", Toast.LENGTH_SHORT).show();
            }

        }
    }
     */

    //Método para imprimir volcano 2
    /*
    public void generateReporte(ModeloInventariosProceso modelo ){
        ArrayList<InventariosDetalle> array = new ArrayList<InventariosDetalle>();
        Document documento = new Document();
        Conexion conexion = new Conexion(contexto);
        String horainicio = "", horafin = "";
        float metrosTotal = 0;
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("Movil_P_EnProcesoReporte_SELECT ?");
            stmt.setString(1, modelo.getMaterial());
            ResultSet r = stmt.executeQuery();
            int cont = 0;
            float [][] arreglo = new float[7][2];
            arreglo[0][0] = 13;
            arreglo[0][1] = 30;
            arreglo[1][0] = 45;
            arreglo[1][1] = 30;
            arreglo[2][0] = 1;
            arreglo[2][1] = 28;
            arreglo[3][0] = 2;
            arreglo[3][1] = 11;
            while(r.next()){
                if(cont == 4){
                    break;
                }else{
                    if(cont == 0){
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion("G2RD5");
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }else{
                        InventariosDetalle inventario = new InventariosDetalle();
                        inventario.setCantidad(""+arreglo[cont][0]);
                        inventario.setLongitud(""+arreglo[cont][1]);
                        inventario.setCodigoNuevo(r.getString("Usuario"));
                        inventario.setProductoID(r.getString("Material"));
                        inventario.setUbicacion(r.getString("Ubicacion"));
                        inventario.setIncidencia(r.getString("Fecha"));
                        inventario.setCodigoViejo(r.getString("Hora inicio"));
                        inventario.setFolio(r.getString("Hora fin"));
                        inventario.setUbicacionId(""+r.getFloat("Stock"));
                        metrosTotal = r.getFloat("Stock");
                        array.add(inventario);
                        cont++;
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error en consulta para traer registros: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            documento.setMargins(-50f, -50f, 5f, 5f);
            File file = crearFichero("Reporte en proceso de "+modelo.getMaterial().replace("-", "_")+" "+fecha.replace("/", " ")+".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            documento.setPageSize(PageSize.LEGAL);
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();
            Font fuente = FontFactory.getFont(FontFactory.defaultEncoding, 18, Font.BOLD, harmony.java.awt.Color.BLACK);
            Font fuentefecha = FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD, harmony.java.awt.Color.BLACK);
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
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha de realización: 2021-12-27", fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);

            String restanteString = "Cantidad Faltante: ";
            documento.add(a);
            documento.add(new Paragraph("\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tComprometido en sistema: " + comprometido.getText().toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\tDisponible en sistema: " + disponible.getText().toString()));
            //Calcular el total
            float total = 0, rollosTotal = 0;
            for(int i=0; i<array.size(); i++){
                total = total + ((Float.parseFloat(array.get(i).getCantidad())) * (Float.parseFloat(array.get(i).getLongitud())));
                rollosTotal = rollosTotal + Float.parseFloat(array.get(i).getCantidad());
            }
            total = 2000;
            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAlmacén: " +modelo.getAlmacen()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistencia en sistema: "+(total)+" mts\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tMaterial: " +modelo.getMaterial()+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tConteo inventario: "+(total-210)+" mts\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExistente: "+sistema+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia "+lblInvHistDetDiferencia.getText().toString()+"\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + restanteString + "" + restante.toString().replace("-", " ")));
            //float diferencia = 0.0f;
            //float d = 0.9f;
            //diferencia = ((total+d)-total);
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tTotal rollos: "+(rollosTotal)+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tDiferencia: 210 mts\n\n"));
            documento.add(new Paragraph("\n\n"));
            //Encabezado
            PdfPTable encabezado = new PdfPTable(6);
            PdfPTable table = new PdfPTable(6);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);


            PdfPCell cellt = new PdfPCell(new Phrase("Usuario"));
            cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cellt);
            PdfPCell cell2t = new PdfPCell(new Phrase("Material"));
            cell2t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell2t);
            PdfPCell cell3t = new PdfPCell(new Phrase("Cant. Rollos"));
            cell3t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell3t);
            PdfPCell cell4t = new PdfPCell(new Phrase("Cant. metros"));
            cell4t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell4t);
            PdfPCell cell5t = new PdfPCell(new Phrase("Ubicacion"));
            cell5t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell5t);
            PdfPCell cell6t = new PdfPCell(new Phrase("Fecha"));
            cell6t.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell6t);

            for (int i = 0; i < array.size(); i++) {
                if(i == 4){
                    break;
                }else{
                    PdfPCell cell = new PdfPCell(new Phrase(array.get(i).getCodigoNuevo()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    PdfPCell cel2 = new PdfPCell(new Phrase(array.get(i).getProductoID()));
                    cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel2);
                    PdfPCell cel3 = new PdfPCell(new Phrase(array.get(i).getCantidad()));
                    cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel3);
                    PdfPCell cel4 = new PdfPCell(new Phrase(array.get(i).getLongitud()));
                    cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel4);
                    //PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    PdfPCell cel5 = new PdfPCell(new Phrase(array.get(i).getUbicacion()));
                    cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel5);
                    //PdfPCell cel6 = new PdfPCell(new Phrase(fecha));
                    PdfPCell cel6 = new PdfPCell(new Phrase("2021-12-27"));
                    cel6.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cel6);
                }
            }


            documento.add(encabezado);
            documento.add(table);

            documento.add(new Paragraph("\n\n\n"));
            //documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t______________________________________\n"));
            fileUri = file;
        } catch (DocumentException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            documento.close();
            Toast.makeText(contexto, "¡Reporte generado con éxito!", Toast.LENGTH_SHORT).show();
            /*
            documento.close();
            if(status == 1){

            }else if(status == 2){
                printPDF(fileUri);
                Toast.makeText(contexto, "Abriendo vista previa del archivo...", Toast.LENGTH_SHORT).show();
            }
        }
    }
     */

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

    //Adapter for recycler
    public class AdapterInventariosEnProceso extends RecyclerView.Adapter<AdapterInventariosEnProceso.AdapterInventariosEnProcesoHolder>{
        @NonNull
        @Override
        public AdapterInventariosEnProcesoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterInventariosEnProcesoHolder(getLayoutInflater().inflate(R.layout.inventario_a_en_proceso_items, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterInventariosEnProcesoHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return inventariosEnProceso.size();
        }

        class AdapterInventariosEnProcesoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView lblInvProcFecha, lblInvProcAlmacen, lblInvProcMaterial, lblInvProcSistema, lblInvProcFisico, lblInvProcDiferencia, lblInvProcConsecutivo;
            ImageView imgInvProcBloqueado, imgInvProcMercancia;
            CardView cardviewEnProceso;
            String folio;
            public AdapterInventariosEnProcesoHolder(@NonNull View itemView) {
                super(itemView);
                lblInvProcFecha = itemView.findViewById(R.id.lblInvProcFecha);
                lblInvProcAlmacen = itemView.findViewById(R.id.lblInvProcAlmacen);
                lblInvProcMaterial = itemView.findViewById(R.id.lblInvProcMaterial);
                lblInvProcSistema = itemView.findViewById(R.id.lblInvProcSistema);
                lblInvProcFisico = itemView.findViewById(R.id.lblInvProcFisico);
                lblInvProcDiferencia = itemView.findViewById(R.id.lblInvProcDiferencia);
                imgInvProcBloqueado = itemView.findViewById(R.id.imgInvProcBloqueado);
                imgInvProcMercancia = itemView.findViewById(R.id.imgInvProcMercancia);
                lblInvProcConsecutivo = itemView.findViewById(R.id.lblInvProcConsecutivo);
                cardviewEnProceso = itemView.findViewById(R.id.cardviewEnProceso);
                //Mostrar opciones de despairs / editar
                cardviewEnProceso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
                        LayoutInflater inflater = getLayoutInflater();
                        View view2 = inflater.inflate(R.layout.inventario_a_en_proceso_menu, null);
                        alert.setView(view2);
                        final AlertDialog dialog = alert.create();
                        dialog.setCancelable(false);
                        dialog.show();
                        //Buttons
                        Button btnInvMenuEditar, btnInvMenuTerminar, btnInvMenuBloquear, btnInvMenuDesbloquear, btnInvMenuCancelar;
                        btnInvMenuEditar = view2.findViewById(R.id.btnInvMenuEditar);
                        btnInvMenuTerminar = view2.findViewById(R.id.btnInvMenuTerminar);
                        btnInvMenuBloquear = view2.findViewById(R.id.btnInvMenuBloquear);
                        btnInvMenuDesbloquear = view2.findViewById(R.id.btnInvMenuDesbloquear);
                        btnInvMenuCancelar = view2.findViewById(R.id.btnInvMenuCancelar);
                        //Acciones buttons
                        //Button editar
                        btnInvMenuEditar.setOnClickListener(view1 -> {
                            //Cargar información para editar
                            String folio = inventariosEnProceso.get(getAdapterPosition()).getFolio();
                            String fecha = inventariosEnProceso.get(getAdapterPosition()).getFecha();
                            String almacen = inventariosEnProceso.get(getAdapterPosition()).getAlmacen();
                            String material = inventariosEnProceso.get(getAdapterPosition()).getMaterial();
                            String sistema = inventariosEnProceso.get(getAdapterPosition()).getSistema();
                            String stockTotal = inventariosEnProceso.get(getAdapterPosition()).getFisico();
                            //Abrir intent de edición
                            Intent intent = new Intent(InventariosEnProceso.this, InventarioAEnProcesosDetalle.class);
                            intent.putExtra("Folio", folio);
                            intent.putExtra("Fecha", fecha);
                            intent.putExtra("Almacen", almacen);
                            intent.putExtra("Material", material);
                            intent.putExtra("Sistema", sistema);
                            intent.putExtra("StockTotal", stockTotal);
                            intent.putExtra("usuario", usuario);
                            startActivity(intent);
                            dialog.dismiss();
                            finish();
                        });
                        //Button terminar
                        btnInvMenuTerminar.setOnClickListener(view1 -> {
                            //Terminar
                            folioTerminar = inventariosEnProceso.get(getAdapterPosition()).getFolio();
                            almacenTerminar = inventariosEnProceso.get(getAdapterPosition()).getAlmacen();
                            Toast.makeText(contexto, "almacen terminar: "+almacenTerminar, Toast.LENGTH_SHORT).show();
                            posicionTerminar = getAdapterPosition();
                            validarPermiso();
                            dialog.dismiss();
                        });
                        //Button bloquear
                        btnInvMenuBloquear.setOnClickListener(view1 -> {
                            //Pausar
                            setStatusPausa(inventariosEnProceso.get(getAdapterPosition()).getProductoID(), getAdapterPosition());
                            dialog.dismiss();
                        });
                        //Button desbloquear
                        btnInvMenuDesbloquear.setOnClickListener(view1 -> {
                            //Despausar
                            setStatusDespausar(inventariosEnProceso.get(getAdapterPosition()).getProductoID(), getAdapterPosition());
                            dialog.dismiss();
                        });
                        //Button cancelar
                        btnInvMenuCancelar.setOnClickListener(view1 -> {
                            dialog.dismiss();
                        });
                    }
                });

                //Mostrar incidencias
                cardviewEnProceso.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        /*
                        new MaterialAlertDialogBuilder(contexto)
                                .setTitle("¿Desea generar el reporte de inventario?")
                                .setMessage("Material: "+inventariosEnProceso.get(getAdapterPosition()).getMaterial())
                                .setIcon(R.drawable.confirmacion)
                                .setPositiveButton("Generar PDF", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        generateReporte(inventariosEnProceso.get(getAdapterPosition()));
                                    }
                                })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Cerrar
                                    }
                                })
                                .show();
                         */
                        //Mostrar las incidencias
                        if(Integer.parseInt(inventariosEnProceso.get(getAdapterPosition()).getEstadoMercancia()) == 0){
                            new MaterialAlertDialogBuilder(InventariosEnProceso.this)
                                    .setTitle("¡Estable!")
                                    .setIcon(R.drawable.correcto)
                                    .setMessage("No se encontraron incidencias.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //No hacer nada
                                        }
                                    })
                                    .show();
                        }else{
                            con = new Conexion(InventariosEnProceso.this);
                            try {
                                String incidencias = "";
                                Statement stmt = con.conexiondbImplementacion().createStatement();
                                String query = "SELECT Observaciones as Incidencia FROM Movil_Reporte WHERE Folio = '"+inventariosEnProceso.get(getAdapterPosition()).getFolio()+"' AND Observaciones != '' AND Status = 1;";
                                ResultSet r = stmt.executeQuery(query);
                                while(r.next()){
                                    incidencias = incidencias +"-"+r.getString("Incidencia")+"\n";
                                }
                                new MaterialAlertDialogBuilder(InventariosEnProceso.this)
                                        .setTitle("¡Inestable!")
                                        .setIcon(R.drawable.confirmacion)
                                        .setMessage("Las incidencias encontradas son: \n\n"+incidencias)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //No hacer nada
                                            }
                                        })
                                        .show();
                            }catch (Exception e){

                            }
                        }
                        return false;
                    }
                });
            }

            public void printAdapter(int position){
                //Rellenar campos
                lblInvProcConsecutivo.setText(""+(position+1));
                folio = inventariosEnProceso.get(position).getFolio();
                lblInvProcFecha.setText(inventariosEnProceso.get(position).getFecha());
                lblInvProcAlmacen.setText(inventariosEnProceso.get(position).getAlmacen());
                lblInvProcMaterial.setText(inventariosEnProceso.get(position).getMaterial());
                lblInvProcSistema.setText(inventariosEnProceso.get(position).getSistema());
                lblInvProcFisico.setText(inventariosEnProceso.get(position).getFisico());
                //Tomar solo dos decimales después del punto
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String diferenciaFormat = decimalFormat.format(((Float.parseFloat(inventariosEnProceso.get(position).getFisico())) - (Float.parseFloat(inventariosEnProceso.get(position).getSistema()))));
                float diferencia = Float.parseFloat(diferenciaFormat);
                if(diferencia < 0){
                    lblInvProcDiferencia.setText("-"+diferencia);
                    lblInvProcDiferencia.setTextColor(Color.RED);
                }else if(diferencia > 0){
                    lblInvProcDiferencia.setText("+"+diferencia);
                    lblInvProcDiferencia.setTextColor(Color.GREEN);
                }else if(diferencia == 0){
                    lblInvProcDiferencia.setText(""+diferencia);
                    lblInvProcDiferencia.setTextColor(Color.BLUE);
                }
                if(Integer.parseInt(inventariosEnProceso.get(position).getEstadoMercancia()) == 0){
                    imgInvProcMercancia.setImageResource(R.drawable.correcto);
                }else{
                    imgInvProcMercancia.setImageResource(R.drawable.snakerojo);
                }
                if(Integer.parseInt(inventariosEnProceso.get(position).getBloqueado()) == 1) {
                    imgInvProcBloqueado.setImageResource(R.drawable.confirmacion);
                }else{
                    imgInvProcBloqueado.setImageResource(R.drawable.correcto);
                }
            }

            //Método para pausar un material en proceso
            public void setStatusPausa(String productoID, int position){
                con = new Conexion(contexto);
                try {
                    PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Producto_Pausar_UPDATE ?");
                    stmt.setString(1, productoID);
                    stmt.execute();
                    //Actualizar el registro
                    inventariosEnProceso.get(position).setBloqueado("1");
                    try {
                        recyclerInvProceso.getAdapter().notifyDataSetChanged();
                        Toast.makeText(contexto, "¡Se pausó el material correctamente!", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }catch (SQLException e){
                    Toast.makeText(contexto, "Error BD: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            //Método para despausar un inventario
            public void setStatusDespausar(String productoID, int position){
                con = new Conexion(contexto);
                try {
                    PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Producto_Despausar_UPDATE ?");
                    stmt.setString(1, productoID);
                    stmt.execute();
                    inventariosEnProceso.get(position).setBloqueado("0");
                    try {
                        recyclerInvProceso.getAdapter().notifyDataSetChanged();
                        Toast.makeText(contexto, "¡Se ha despausado el producto exitosamente!", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }catch (SQLException e){
                    Toast.makeText(contexto, "Error BD:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            //Muestra los filtros disponibles en la pantalla
            public void validarPermiso() {
                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                builder.setTitle("Introduzca el código de validación");
                builder.setIcon(R.drawable.confirmacion);
                final EditText input = new EditText(contexto);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Validamos
                        if(input.getText().toString().equals("$h11nv")){
                            loading = 3;
                            loadinglauncher();
                            //setTerminarInventario();
                        }else{
                            new MaterialAlertDialogBuilder(contexto)
                                    .setTitle("¡Contraseña inválida!")
                                    .setIcon(R.drawable.snakerojo)
                                    .setMessage("Si continúa se notificará a los administradores para empezar una inspección acerca de usted: "+usuario+".")
                                    .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //No hacer nada
                                        }
                                    })
                                    .show();
                        }
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

            @Override
            public void onClick(View v){
            }
        }
    }

    public class AdapterUbicacionesDepurar extends RecyclerView.Adapter<AdapterUbicacionesDepurar.AdpaterUbicacionesDepurarHolder>{
        @NonNull
        @Override
        public AdpaterUbicacionesDepurarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdpaterUbicacionesDepurarHolder(getLayoutInflater().inflate(R.layout.inventario_a_en_proceso_ubicaciones_eliminar_items, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdpaterUbicacionesDepurarHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return ubicacionesDepurar.size();
        }

        class AdpaterUbicacionesDepurarHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            //TextViews
            TextView lblNombreUbicacion, lblCantidadUbicacion, lnlConsecutivoUbicacion;
            CheckBox checkBoxDepurarUbicacion;
            public AdpaterUbicacionesDepurarHolder(View itemView){
                super(itemView);
                lnlConsecutivoUbicacion = itemView.findViewById(R.id.lnlConsecutivoUbicacion);
                lblNombreUbicacion = itemView.findViewById(R.id.lblNombreUbicacion);
                lblCantidadUbicacion = itemView.findViewById(R.id.lblCantidadUbicacion);
                checkBoxDepurarUbicacion = itemView.findViewById(R.id.checkBoxDepurarUbicacion);

                checkBoxDepurarUbicacion.setOnClickListener(view -> {
                    if(ubicacionesDepurar.get(getAdapterPosition()).getIsSelect() == 0){
                        ubicacionesDepurar.get(getAdapterPosition()).setIsSelect(1);
                    }else{
                        ubicacionesDepurar.get(getAdapterPosition()).setIsSelect(0);
                    }
                    notifyDataSetChanged();
                });
            }

            public void printAdapter(int position){
                //Mostrar información
                lnlConsecutivoUbicacion.setText(""+(position+1));
                lblNombreUbicacion.setText(ubicacionesDepurar.get(position).getNombre().split("/")[0]);
                lblCantidadUbicacion.setText(ubicacionesDepurar.get(position).getNombre().split("/")[1]);
                if(ubicacionesDepurar.get(position).getIsSelect() == 0){
                    checkBoxDepurarUbicacion.setChecked(false);
                }else{
                    checkBoxDepurarUbicacion.setChecked(true);
                }
            }

            @Override
            public void onClick(@NonNull View view){
                //No hacer nada
            }
        }
    }

}