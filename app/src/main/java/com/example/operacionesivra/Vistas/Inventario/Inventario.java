package com.example.operacionesivra.Vistas.Inventario;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Vistas.Inventario.ConteosPausa.Pausa;
import com.example.operacionesivra.Vistas.Inventario.EditarRegistro.EditarRegistro;
import com.example.operacionesivra.Vistas.Inventario.EditarRegistro.EditarRegistroInterface;
import com.example.operacionesivra.Vistas.Inventario.ItemEspecial.ItemSpecial;
import com.example.operacionesivra.Vistas.Inventario.NuevoRegistro.CustomDialogFragmentTag;
import com.example.operacionesivra.Vistas.Inventario.NuevoRegistro.CustomDialogInterfaceTag;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.InventariosMenu;
import com.example.operacionesivra.Vistas.MainActivity.MainActivity;
import com.example.operacionesivra.Vistas.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.example.operacionesivra.Modelos.VariablesGlobales.GlobalesInventario;
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
import java.util.UUID;

import harmony.java.awt.Color;

public class Inventario extends AppCompatActivity implements CustomDialogInterface, CustomDialogInterfaceTag, EditarRegistroInterface {
    public int loadingInventario = 0;
    //Variables del programa
    TextView resultadodelamultiplicacion;
    private RecyclerView recyceritems;
    public AdapterItemDetalle adaptador;
    public ArrayList<ModeloDetallesItem> pedidos = new ArrayList<>();
    public static Inventario i = new Inventario();
    //Variable que nos permnitirá validar si se elimina registro de la tabla o de la BD
    public static int opcion;
    public static float lonjitudMat;

    Context context;
    //Variable que nos permitirá tener siempre el códido del producto ya sea seleccionado o nuevo
    public static String ProductoId = "";

    //Variables del programa
    public TextView nombreitem, unidaddemedida, almacenView, contadorcontenedor, stocktotal_interface, fecha, contadorderollos;
    public Button aceptar, editar, nuevatag, guardar, cargar, incidencianueva, btnEliminarTags;
    public String codigo_material, codigodelmaterial, nombrelogin, stocktotalvar, folio, horainicio;
    public String estado = "Ok";
    public String uuid;
    public int comprobaciondeestado;
    DialogIniciarInventario dialogIniciarInventario;
    EditarRegistro editarRegistro;
    ItemSpecial itemSpecial;
    Pausa conteos_pausa;
    CustomDialogFragmentTag customDialogFragmentTag;
    Inventario inventario;

    //OpcionInv
    public static int opcionInv;

    //Variables que se bloquearán
    TextView comprometido, disponible, lblComprometido, lblDisponible;

    //Scanner
    public TextView text_cod_escaneado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_inventario);
        inventario = this;
        context = this;
        //Inicializacion de dialogs
        dialogIniciarInventario = new DialogIniciarInventario();
        customDialogFragmentTag = new CustomDialogFragmentTag(this, lonjitudMat);
        itemSpecial = new ItemSpecial();
        conteos_pausa = new Pausa();

        resultadodelamultiplicacion = findViewById(R.id.resultadoDFT);
        cargarusuario();

        definiciondevariables();

        opciones();

        inicializarlisteners();
        otorgarpermisos();

        //Invocar al método para ocultar las propiedades
        ocultarPropiedades();

        horainicio = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public void     definiciondevariables() {
        //Variables
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        guardar = findViewById(R.id.guardarinventario);
        cargar = findViewById(R.id.cargarinventario);
        contadorderollos = findViewById(R.id.cantidadrollos);
        almacenView = findViewById(R.id.almacenmate);
        text_cod_escaneado = findViewById(R.id.codigo_sp);
        nombreitem = findViewById(R.id.nombreitem);
        recyceritems = findViewById(R.id.detallesitem);
        unidaddemedida = findViewById(R.id.unidadmedida);
        stocktotal_interface = findViewById(R.id.stocktotal);
        aceptar = findViewById(R.id.aceptar_I);
        editar = findViewById(R.id.editar);
        nuevatag = findViewById(R.id.newtag);
        fecha = findViewById(R.id.fechaitemprioridades);
        contadorcontenedor = findViewById(R.id.codigosescaneados);
        btnEliminarTags = findViewById(R.id.btnEliminarTags);
        btnEliminarTags.setVisibility(View.VISIBLE);

        //Campos que se bloquearán
        comprometido = findViewById(R.id.txtComprometido);
        disponible = findViewById(R.id.txtDisponible);
        lblComprometido = findViewById(R.id.lblComprometido);
        lblDisponible = findViewById(R.id.lblDisponible);

        //Llenar adapter
        recyceritems.setLayoutManager(new LinearLayoutManager(this));
        fecha.setText(date);
        uuid = UUID.randomUUID().toString().replace("-", "");
        adaptador = new AdapterItemDetalle(crearlista());
        recyceritems.setAdapter(adaptador);

    }

    public void inicializarlisteners() {
        //Clicks Listeners
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmacionCambioMaterial();
            }
        });

        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmacionCambioMaterialPausa();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingInventario = 3;
                loadinglauncher();
            }
        });

        btnEliminarTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTags();
            }
        });

        aceptar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mostrarmensaje();
                return false;
            }
        });


        contadorcontenedor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                float contenidototal, contenidoescaneado;
                contenidoescaneado = Float.parseFloat(contadorcontenedor.getText().toString());
                contenidototal = Float.parseFloat(stocktotal_interface.getText().toString());
                if (contenidoescaneado > contenidototal) {
                    contadorcontenedor.setError("El contenido registrado supera el total del stock");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float contenidototal, contenidoescaneado;
                contenidoescaneado = Float.parseFloat(contadorcontenedor.getText().toString());
                contenidototal = Float.parseFloat(stocktotal_interface.getText().toString());
                if (contenidoescaneado > contenidototal) {
                    contadorcontenedor.setError("El contenido registrado supera el total del stock");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                float contenidototal, contenidoescaneado;
                contenidoescaneado = Float.parseFloat(contadorcontenedor.getText().toString());
                contenidototal = Float.parseFloat(stocktotal_interface.getText().toString());
                if (contenidoescaneado > contenidototal) {
                    contadorcontenedor.setError("El contenido registrado supera el total del stock");
                }
            }
        });

        nombreitem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (nombreitem.getText().length() != 0) {
                    activarbotones();
                } else {
                    desactivarbotones();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nombreitem.getText().length() != 0) {
                    activarbotones();
                } else {
                    desactivarbotones();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (nombreitem.getText().length() != 0) {
                    activarbotones();
                } else {
                    desactivarbotones();
                }
            }
        });
    }

    public void otorgarpermisos() {
        // Permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    1000);
        }
    }

    public void cargarusuario() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String user = preferences.getString("user", "Vacio");
        if (!user.equals("Vacio")) {
            nombrelogin = user;
        } else {
            nombrelogin = "Usuario no esppecificado";
        }
    }

    //Método que oculta propiedades no importantes en la interfaz
    public void ocultarPropiedades(){
        lblComprometido.setVisibility(View.GONE);
        comprometido.setVisibility(View.GONE);
        lblDisponible.setVisibility(View.GONE);
        disponible.setVisibility(View.GONE);
        editar.setVisibility(View.GONE);
        guardar.setVisibility(View.GONE);
        cargar.setVisibility(View.GONE);
    }

    public void activityPausa() {
        Intent a = new Intent(this, Pausa.class);
        startActivity(a);
        finish();
    }

    public static void reiniciarActivity(Activity actividad) {
        Intent intent = new Intent();
        intent.setClass(actividad, actividad.getClass());
        //llamamos a la actividad
        actividad.startActivity(intent);
        //finalizamos la actividad actual
        actividad.finish();
    }

    //Verifica los disparadores para el caso pausado, terminado
    public void opciones() {
        try {
            int cerrados;
            cerrados = getIntent().getIntExtra("opcion", 0);
            folio = getIntent().getStringExtra("folio");
            if (folio != null && cerrados == 0) {
                stocktotal_interface.setText(getIntent().getStringExtra("StockTotal"));
                stocktotalvar = stocktotal_interface.getText().toString();
                almacenView.setText(getIntent().getStringExtra("almacen"));
                continuarInventario(folio);
                uuid = folio;
                folio = null;
            } else if (folio != null && cerrados == 1) {
                stocktotalvar = stocktotal_interface.getText().toString();
                almacenView.setText(getIntent().getStringExtra("almacen"));
                consultarInventario(folio);
                uuid = folio;
                folio = null;
            } else if (folio != null && cerrados == 2) {
            } else {
                //Aquí se abre el primer dialog
                dialogIniciarInventario.show(getSupportFragmentManager(), null);
                dialogIniciarInventario.setCancelable(false);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    //Rellena los datos de la pantalla con datos tomados de la base de datos
    public void continuarInventario(String folio) {
        Conexion conexionService = new Conexion(this);
        try {
            Statement es = conexionService.conexiondbImplementacion().createStatement();
            ResultSet resultado = es.executeQuery("Execute PMovil_Material_pausado '" + folio + "'");
            if (resultado.next()) {
                nombreitem.setText(resultado.getString(1));
                codigo_material = resultado.getString(6);
                unidaddemedida.setText(resultado.getString(7));
                codigodelmaterial = resultado.getString(6);
                segirinventario(almacenView.getText().toString(), folio);
                if (!pedidos.isEmpty()) {
                    activarbotones();
                } else {
                    desactivarbotones();
                }
            }
            recyceritems.getAdapter().notifyDataSetChanged();
        } catch (SQLException e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
        sumadecontenidoyrollos();
    }

    //Revisado
    //Muestra los datos recabados de un inventario terminado
    public void consultarInventario(String folio) {
        Conexion conexionService = new Conexion(this);
        try {
            Statement es = conexionService.conexiondbImplementacion().createStatement();
            ResultSet resultado = es.executeQuery("select * from Movil_Reporte where Folio='" + folio + "'");

            while (resultado.next()) {
                try {
                    //Recibimos el id del producto
                    ProductoId = getIntent().getStringExtra("productoID");
                    nombreitem.setText(resultado.getString("Material"));
                    codigo_material = resultado.getString(3);
                    unidaddemedida.setText("METRO LINE");
                    codigodelmaterial = resultado.getString(3);
                    stocktotal_interface.setText(resultado.getString("StockTotal"));
                    //almacenView.setText(getIntent().getStringExtra("Almacen"));
                    almacenView.setText(getIntent().getStringExtra("almacen"));
                    pedidos.add(new ModeloDetallesItem(resultado.getString("Codigo_unico"),
                            resultado.getString("Cantidad"), resultado.getString("Total_registrado"),
                            resultado.getString("Ubicacion"), resultado.getString("Estado"), R.drawable.correcto,
                            resultado.getString("Observaciones"), resultado.getString("Id_registro"), resultado.getString("UbicacionID"), 0));
                } catch (Exception e) {
                    Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
                }

            }
            if (!pedidos.isEmpty()) {
                activarbotones();
            } else {
                desactivarbotones();
            }
            recyceritems.getAdapter().notifyDataSetChanged();
            sumadecontenidoyrollos();
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
        sumadecontenidoyrollos();
    }

    public void activarbotones() {
        aceptar.setEnabled(true);
        nuevatag.setEnabled(true);
        //incidencianueva.setEnabled(true);
    }

    public void desactivarbotones() {
        aceptar.setEnabled(false);
        nuevatag.setEnabled(false);
        //incidencianueva.setEnabled(false);
    }

    //Revisado (Correcto)
    //Elije el material en el primer dialog y pasa a la siguiente
    //Lanza un dialog y rrellena el formulario inicial
    //text_cod_escaneado Contiene el valor del campo de texto con el codigo
    public int ElegirMaterialPrincipal() {
        Conexion conexionService = new Conexion(this);
        try {
            Statement es = conexionService.conexiondbImplementacion().createStatement();
            final ResultSet resultado = es.executeQuery("execute PMovil_Item_Scaneados_Nuevo '" + text_cod_escaneado.getText() + "', '" + almacenView.getText().toString() + "'");
            if (resultado.next()) {
                nombreitem.setText(resultado.getString("Producto"));
                //AHH
                //contadorderollos.setText(""+getIntent().getIntExtra("OpcionInv", 0));

                unidaddemedida.setText(resultado.getString("Unidad"));
                stocktotal_interface.setText(resultado.getString("Fisico"));
                comprometido.setText(resultado.getString("Comprometido"));
                disponible.setText(resultado.getString("Disponible"));
                codigodelmaterial = resultado.getString("CodProducto");
            }
            /*
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Quiere comenzar el inventario de: " + resultado.getString(2) + "?\nEl material dejará de estar disponible para venta hasta finalizar el inventario")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //bloqueogeneral(codigodelmaterial);
                            horainicio = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmacionCambioMaterial();
                        }
                    })
                    .show();
             */
            opcion = 0;
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    //Desactivado por cambio en la base de datos
    public boolean bloqueogeneral(String codigo) {
        Conexion conexionService = new Conexion(this);
        boolean realizado = false;
        try {
            Statement es = conexionService.conexiondbImplementacion().createStatement();
            ResultSet resultado = es.executeQuery("execute PMovil_Bloqueado '" + codigo + "'");
            if (resultado.next()) {
                realizado = true;
            }

        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
        return realizado;
    }

    //Revisado (Correcto)
    //Bloquea el material que se haya elegido
    public void bloquearmaterial(String bloquear) {
        Conexion conexionService = new Conexion(this);
        try {
            for (int i = 0; i < pedidos.size(); i++) {
                try (PreparedStatement var = conexionService.conexiondbImplementacion().prepareCall("execute P_Movil_bloquear ?,?")) {
                    try {
                        var.setString(1, uuid);
                        var.setString(2, bloquear);
                        var.execute();
                    } catch (SQLException e) {
                        Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
                System.out.println("listo bloqueo");
            }
        } catch (SQLException e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

    }

    //Revisado (Correcto)
    //Pausa el material que se haya elegido
    public void Pausarmaterial(String pausar) {
        Conexion conexionService = new Conexion(this);
        try {
            for (int i = 0; i < pedidos.size(); i++) {
                try (PreparedStatement var = conexionService.conexiondbImplementacion().prepareCall("execute P_Movil_Pausar ?,?")) {
                    try {
                        var.setString(1, uuid);
                        var.setString(2, pausar);
                        var.execute();
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

    }

    //Crea una lista con los datos de un inventario anterior
    public ArrayList<ModeloDetallesItem> segirinventario(String almacen, String folio) {
        Conexion conexionService = new Conexion(this);
        int contador = 0;
        float contenidotemporal = 0F;
        almacenView.setText(almacen);
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_Material_pausado2 '" + folio + "'");
            while (r.next()) {
                try {
                    pedidos.add(new ModeloDetallesItem(r.getString("Cantidad"), r.getString("Contenido")
                            , r.getString("Total_Registrado"), r.getString("Ubicacion"), r.getString("Estado")
                            , R.drawable.correcto, r.getString("Observaciones"), r.getString("Id_registro"), r.getString("UbicacionId"), 0));
                } catch (Exception e) {
                    Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
                }

            }
            recyceritems.getAdapter().notifyDataSetChanged();
            sumadecontenidoyrollos();
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
        sumadecontenidoyrollos();
        return pedidos;
    }

    //Creo que no es util
    //Crear la lista de items
    public ArrayList<ModeloDetallesItem> crearlista() {
        return pedidos;
    }

    //Muestra mensaje de confirmacion al intentar salir de la activity sin haber terminado el inventario
    public void mostrarmensaje() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("¿Está seguro?")
                .setIcon(R.drawable.correcto)
                .setMessage("¿Quiere finalizar el inventario del material " + nombreitem.getText() + " ahora?\nRecuerde comprobar que cuenta con una buena conexión a internet")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingInventario = 2;
                        loadinglauncher();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    //Pregunta sobre el inicio de otro inventario
    public void mensajedeconfirmacion() {
        if (comprobaciondeestado == 2) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Datos guardados con éxito")
                    .setIcon(R.drawable.correcto)
                    .setCancelable(false)
                    .setMessage("¿Quiere comenzar un nuevo inventario?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            reiniciarActivity(inventario);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            menuScreen();
                        }
                    })
                    .show();
        } else if (comprobaciondeestado == 1) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Error guardar")
                    .setCancelable(false)
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("No fue posible guardar los datos en el servidor debido a un error de conexión\n¿Quiere intentarlo de nuevo?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            subirtablaDB();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }

    //Revisado (Correcto)
    //Sube la lista a la base de datos
    public void subirtablaDB() {
        //Consultar productoID
        try {
            Conexion conexionService2 = new Conexion(this);
            Statement stmt = conexionService2.conexiondbImplementacion().createStatement();
            String query = "SELECT ProductoID FROM CodGeneral WHERE Codigo = '"+text_cod_escaneado.getText().toString()+"'";
            ResultSet r = stmt.executeQuery(query);
            while(r.next()){
                ProductoId = r.getString("ProductoID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            Conexion conexionService = new Conexion(this);
            //conexionService.conexiondbImplementacion().setAutoCommit(false);
            comprobaciondeestado = 0;

            String horafin = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            for (int i = 0; i < pedidos.size(); i++) {
                try (PreparedStatement var = conexionService.conexiondbImplementacion().prepareCall("execute P_Movil_reporte2 ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?")) {
                    try {
                        var.setString(1, pedidos.get(i).getCantidad());
                        var.setString(2, pedidos.get(i).getLongitud());
                        var.setString(3, pedidos.get(i).getMaterialregistrado());
                        var.setString(4, pedidos.get(i).getUbicacion());
                        var.setString(5, pedidos.get(i).getEstado());
                        var.setString(6, nombrelogin);
                        var.setString(7, uuid);
                        var.setString(8, nombreitem.getText().toString());
                        var.setString(9, fecha.getText().toString());
                        var.setString(10, stocktotal_interface.getText().toString());
                        var.setString(11, almacenView.getText().toString());
                        var.setString(12, pedidos.get(i).getIncidencia());
                        var.setString(13, pedidos.get(i).getFolioadd());
                        var.setString(14, horainicio);
                        var.setString(15,horafin);
                        var.setString(16, ProductoId);
                        var.setString(17, pedidos.get(i).getUbicacionId());

                        var.execute();
                    } catch (SQLException e) {
                        comprobaciondeestado = 1;
                    }
                } catch (SQLException a) {
                    comprobaciondeestado = 1;
                }

            }
            //conexionService.conexiondbImplementacion().commit();
            comprobaciondeestado = 2;
        } catch (Exception e) {
                comprobaciondeestado = 1;
                //conexionService.conexiondbImplementacion().rollback();
        }
    }

    //revisado (Correcto)
    //Intent to Inventario

    //revisado (Correcto)
    //Intent to Main Activity
    public void activityMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("usuario", nombrelogin);
        startActivity(intent);
        finish();
    }

    public void menuScreen(){
        Intent intent = new Intent(this, InventariosMenu.class);
        startActivity(intent);
        finish();
    }

    //revisado (Correcto)
    //Realiza una copia de la insformacion antes regresar al menu
    @Override
    public void onBackPressed() {
        if(pedidos.size() == 0){
            Intent intent = new Intent(getBaseContext(), Inventario.class);
            startActivity(intent);
        }else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("¡Confirmación!")
                    .setIcon(R.drawable.confirmacion)
                    .setMessage("¿Seguro que desea salir?\n Elija cuidadosamente una opción.")

                    .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //No hacer nada
                        }
                    })
                    .setPositiveButton("Guardar y salir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Invocar método loading launcher
                            loadingInventario = 6;
                            loadinglauncher();
                        }
                    })
                    .setNegativeButton("Salir sin guardar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getBaseContext(), InventariosMenu.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .show();
        }
    }

    /*-------------------------------------Interfaces---------------------------------------------*/

    //Crea una nueva tag
    public void crearnuevatag(View vista) {
        customDialogFragmentTag.show(getSupportFragmentManager(), null);
        customDialogFragmentTag.setCancelable(false);
    }

    public void nuevoespecial(View vista) {
        itemSpecial.show(getSupportFragmentManager(), null);
        itemSpecial.setCancelable(false);
    }

    public void editarregistro(String tag, String contenido, String ubicacion, String total, String posicion, String incidencia) {
        editarRegistro = new EditarRegistro(this, tag, contenido, ubicacion, total, posicion, incidencia);
        editarRegistro.show(getSupportFragmentManager(), null);
        editarRegistro.setCancelable(false);
    }

    public void quitarRegistro(){
        recyceritems.getAdapter().notifyDataSetChanged();
    }

    //Limpia el formulario y lanza uno de eleguir material
    public void confirmacionCambioMaterial() {
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_MaterialComponents_Title_Icon)
                .setTitle("Confirmación")
                .setMessage("¿Está seguro que quiere cambiar de material?\n(El progreso se perderá)")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reiniciarActivity(inventario);
                        dialogIniciarInventario.show(getSupportFragmentManager(), null);
                        dialogIniciarInventario.setCancelable(false);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })
                .show();
    }

    //Revisado (Correcto)
    //Reinicia la actividad y/o guarda el progreso
    public void confirmacionCambioMaterialPausa() {
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_MaterialComponents_Title_Icon)
                .setTitle("Confirmación")
                .setMessage("¿Está seguro que quiere cambiar de material?\n(El progreso se perderá)")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nombreitem.setText(null);
                        text_cod_escaneado.setText(null);
                        unidaddemedida.setText(null);
                        almacenView.setText(null);
                        bloquearmaterial("NO");
                        uuid = UUID.randomUUID().toString().replace("-", "");
                        pedidos.clear();
                        stocktotal_interface.setText(pedidos.size() + "");
                        recyceritems.getAdapter().notifyDataSetChanged();
                        dialogIniciarInventario.show(getSupportFragmentManager(), null);
                        dialogIniciarInventario.setCancelable(false);
                        activityPausa();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    //Revisado (Correcto)
    //Carga los datos y actualiza al nterface de Alert Dialog
    @Override
    public void datos(String codigo, String almacen) {
        text_cod_escaneado.setText(codigo);

        //Consultar lonjitud
        try {
            Conexion c = new Conexion(getApplicationContext());
            Statement statement = c.conexiondbImplementacion().createStatement();
            String query = "SELECT CG.Longitud, P.Nombre FROM CodGeneral CG INNER JOIN Producto P ON (CG.ProductoID = P.ProductoID) WHERE CG.Codigo = '"+text_cod_escaneado.getText().toString()+"'";
            ResultSet r = statement.executeQuery(query);
            while(r.next()){
                GlobalesInventario.globalContMaterial = r.getFloat("Longitud");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        almacenView.setText(almacen);
        if(ElegirMaterialPrincipal()==0){
            new MaterialAlertDialogBuilder(this)
                    .setTitle("¡ERROR!")
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("El código que ingresó es inválido o no fue encontrado\n¿Desea intentarlo nuevamente?")
                    .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getBaseContext(), Inventario.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getBaseContext(), InventariosMenu.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    public void deleteTags(){
        if(pedidos.size() == 0){
            new MaterialAlertDialogBuilder(this)
                    .setTitle("¡Error!")
                    .setIcon(R.drawable.snakerojo)
                    .setMessage("Por favor seleccione algún registro para eliminar")
                    .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //No hacer nada
                        }
                    })
                    .show();
        }else{
            int hayEliminar = 0;
            ArrayList<ModeloDetallesItem> lista = new ArrayList<>();
            //pedidos / lista
            for(int i=0; i<pedidos.size(); i++){
                if(pedidos.get(i).getIsSelected() == 0){
                    lista.add(pedidos.get(i));
                }else{
                    hayEliminar = 1;
                }
            }
            if(hayEliminar == 1){
                pedidos = lista;
                adaptador = new AdapterItemDetalle(crearlista());
                recyceritems.setAdapter(adaptador);
                new MaterialAlertDialogBuilder(this)
                        .setTitle("¡Éxito!")
                        .setIcon(R.drawable.correcto)
                        .setMessage("Registros eliminados correctamente")
                        .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //No hacer nada
                                recyceritems.getAdapter().notifyDataSetChanged();
                            }
                        })
                        .show();
            }else{
                new MaterialAlertDialogBuilder(this)
                        .setTitle("¡Error!")
                        .setIcon(R.drawable.snakerojo)
                        .setMessage("No hay registros para elimianar.")
                        .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //No hacer nada
                            }
                        })
                        .show();
            }
        }
    }

    //Revisado (Correcto)
    //Permite la entrada de datos de algún material
    @Override
    public void datostag(String cantidad, String contenido, String ubicacion, String codproducto, String contenidot, String ubicacionID, String incidencia) {
        try {
            String folio = UUID.randomUUID().toString().replace("-", "");
            pedidos.add(new ModeloDetallesItem(cantidad, contenido, contenidot, ubicacion, "Ok", R.drawable.correcto, incidencia, folio, ubicacionID, 0));
            MediaPlayer mp = MediaPlayer.create(this, R.raw.definite);
            mp.start();
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
            //Snackbar.make(layout_inventario, "Error al eleguir material (fp200)", Snackbar.LENGTH_INDEFINITE);
        }
        sumadecontenidoyrollos();
        recyceritems.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fragment_open_enter));
        recyceritems.getAdapter().notifyDataSetChanged();
    }

    //Revisado (Correcto)
    //Editar registro invenatrio tag
    //Lanza un context que permite la mofidicación de un campo tomando una interfaz
    @Override
    public void editar(String cantidad, String contenido, String ubicacion, String total, String folio, String ubicacioId, String incidencia) {

        for (int i = 0; i < pedidos.size(); i++) {
            if (folio == pedidos.get(i).getFolioadd()) {
                pedidos.set(i, new ModeloDetallesItem(cantidad, contenido, total, ubicacion, "Ok", R.drawable.correcto, incidencia, folio, ubicacioId, 0));
                Toast.makeText(this, "Registro actualizado", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        sumadecontenidoyrollos();
        recyceritems.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fragment_open_enter));
        recyceritems.getAdapter().notifyDataSetChanged();
    }

    //Revisado (Correcto)
    //Lanza la pantalla de cargando según el contexto de la actividad
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    //Revisado (Correcto) "Mejorado"
    //Realiza la suma de los rollos segun su metraje y numero de items tomando como base el array
    public void sumadecontenidoyrollos() {
        //float contenidofloat = 0f;
        float contenidofloat = 0;
        for (int i = 0; i < pedidos.size(); i++) {
            try {
                contenidofloat = contenidofloat + Float.parseFloat(pedidos.get(i).materialregistrado);
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();

            }
        }
        contadorderollos.setText(pedidos.size() + "");
        contadorcontenedor.setText(contenidofloat + "");
    }

    //-------------------------------------------PDF------------------------------------------------
    public void crearPDF() {
        Document documento = new Document();
        Conexion conexion = new Conexion(this);
        String horainicio = "", horafin = "";
        try {
            Statement statement = conexion.conexiondbImplementacion().createStatement();
            String query = "SELECT CONVERT(VARCHAR, horainicio, 108) as horainicio, CONVERT(VARCHAR, horafin, 108) as horafin FROM Movil_Reporte WHERE Folio = '"+uuid+"' group by horainicio, horafin;";
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
            File file = crearFichero("Reporte de: " + nombreitem.getText().toString().replace("/", "-") + " " + fecha.getText().toString() + " a las " + hora + ".pdf");
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
            PdfPCell cellencabezado = new PdfPCell(new Phrase("\nReporte de Inventario", fuente));
            cellencabezado.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.setTotalWidth(1000);
            a.addCell(imagen);
            a.addCell(cellencabezado);
            PdfPCell cellencabezadofecha = new PdfPCell(new Phrase("\nFecha:" + fecha.getText().toString()+"\n"+horainicio+" - "+horafin, fuentefecha));
            cellencabezadofecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            a.addCell(cellencabezadofecha);


            PdfPTable table = new PdfPTable(5);
            table.setHorizontalAlignment(Cell.ALIGN_CENTER);

            String restanteString = "Cantidad Faltante: ";
            documento.add(a);
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tNombre del Material: " + nombreitem.getText().toString() + "\n\n"));
            Float restante = null;
            try {
                float total = Float.parseFloat(stocktotal_interface.getText().toString());
                float escaneado = Float.parseFloat(contadorcontenedor.getText().toString());
                restante = total - escaneado;
                if (restante < 0) {
                    restanteString = "Sobrante de: ";
                }
            } catch (Exception e) {
                documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tError al completar la creación del PDF, por favor acuda al area de desarrollo para mas información"));
            }


            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tComprometido en sistema: " + comprometido.getText().toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\tDisponible en sistema: " + disponible.getText().toString()));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tFísico en sistema: " + stocktotal_interface.getText().toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\tCantidad registrada: " + contadorcontenedor.getText().toString()));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + restanteString + "" + restante.toString().replace("-", " ")));

            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tRegistros Realizados: "+pedidos.size()+"\n\n"));

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


            for (int i = 0; i < pedidos.size(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(pedidos.get(i).getCantidad()));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                PdfPCell cel2 = new PdfPCell(new Phrase(pedidos.get(i).getLongitud()));
                cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel2);
                PdfPCell cel3 = new PdfPCell(new Phrase(pedidos.get(i).getMaterialregistrado()));
                cel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel3);
                PdfPCell cel4 = new PdfPCell(new Phrase(pedidos.get(i).getUbicacion()));
                cel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel4);
                PdfPCell cel5 = new PdfPCell(new Phrase(pedidos.get(i).getIncidencia()));
                cel5.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cel5);
            }

            documento.add(encabezado);
            documento.add(table);

            documento.add(new Paragraph("\n\n\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t______________________________________\n"));
            documento.add(new Paragraph("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tInventario realizado por: " + nombrelogin));

        } catch (DocumentException e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
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

}
