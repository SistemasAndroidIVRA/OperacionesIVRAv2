package com.example.operacionesivra.Vistas.Inventarios.Vistas;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.operacionesivra.Vistas.Inventarios.Vistas.AjusteInventario.InventarioAjuste;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.EnHistorico.InventariosEnHistorico;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.EnProceso.InventariosEnProceso;
import com.example.operacionesivra.Vistas.MainActivity.MainActivity;
import com.example.operacionesivra.Vistas.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class InventariosMenu extends AppCompatActivity{
    //Botones segunda activity
    public Button btnAtrasInventarioPrincipal, btnAceptarInventarioPrincipal, btnAjusteInventario;
    public Spinner spinnerAlmacenesPrincipal;
    public EditText txtCodigoMaterialPrincipal;
    //Alert dialog segunda activity
    public AlertDialog dialog;

    public int loading = 0;
    public Button btnRegresar, btnInfo;
    public CheckBox cbAjusteInventario;
    public ImageButton btnRegistrarInventarios, btnInventariosTerminados, btnHistoricoInventarios;
    public Context context;
    public String usuario, contrasenia, usuarioID;
    //Variables del intent
    public String material = "", unidadMedida = "", stockTotal = "", almacen ="", productoID = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_a_menu);
        otorgarpermisos();
        usuario = getIntent().getStringExtra("usuario");
        contrasenia = getIntent().getStringExtra("pass");
        usuarioID = getIntent().getStringExtra("idusuario");
        context = this;
        btnRegistrarInventarios = findViewById(R.id.btnRegistrarInventarios);
        btnInventariosTerminados = findViewById(R.id.btnInventariosTerminados);
        btnHistoricoInventarios = findViewById(R.id.btnHistoricoInventarios);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnInfo = findViewById(R.id.btnInfo);
        btnAjusteInventario = findViewById(R.id.btnAjusteInventario);
        //Acciones botones
        btnAjusteInventario.setOnClickListener(view -> {
            Intent intent = new Intent(context, InventarioAjuste.class);
            startActivity(intent);
            this.finish();
        });
        //
        btnRegistrarInventarios.setOnClickListener(view -> {
            /*
                Intent intent = new Intent(getBaseContext(), Inventario.class);
                startActivity(intent);
            */
            //Preparar el AlertDialog
            AlertDialog.Builder alert = new AlertDialog.Builder(InventariosMenu.this);
            LayoutInflater inflater = getLayoutInflater();
            //Traer la vista que se va a abrir
            View view2 = inflater.inflate(R.layout.inventario_a_principal, null);
            alert.setView(view2);
            //Abrir la vista
            dialog = alert.create();
            dialog.show();
            dialog.setCancelable(false);
            //Código del inventario
            cbAjusteInventario = view2.findViewById(R.id.cbAjusteInventario);
            btnAtrasInventarioPrincipal = view2.findViewById(R.id.btnCancelarInventarioPrincipal);
            spinnerAlmacenesPrincipal = view2.findViewById(R.id.spinnerAlmacenesPrincipal);
            txtCodigoMaterialPrincipal = view2.findViewById(R.id.txtCodigoMaterialPrincipal);
            btnAceptarInventarioPrincipal = view2.findViewById(R.id.btnAceptarInventarioPrincipal);
            txtCodigoMaterialPrincipal.setHintTextColor(Color.parseColor("#FFFFFF"));
            fullSpinnerAlmacenes(spinnerAlmacenesPrincipal, view2.getContext());

            btnAtrasInventarioPrincipal.setOnClickListener(view1 -> {
                dialog.dismiss();
            });

            btnAceptarInventarioPrincipal.setOnClickListener(view1 -> {
                if(cbAjusteInventario.isChecked()){
                    //Checar clave
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Introduzca el código de validación");
                    builder.setIcon(R.drawable.confirmacion);
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Validamos
                            if(input.getText().toString().equals("$h14js")){
                                loading = 1;
                                loadinglauncher();
                                //setTerminarInventarios();
                            }else{
                                new MaterialAlertDialogBuilder(context)
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
                }else{
                    loading = 1;
                    loadinglauncher();
                }
                //validateCodigo();
            });
        });

        btnInventariosTerminados.setOnClickListener(view -> {
            ///*
            Intent intent = new Intent(getBaseContext(), InventariosEnProceso.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("idusuario", usuarioID);
            startActivity(intent);
            //*/
                /*
                Intent intent = new Intent(getBaseContext(), Inventarioscerrados.class);
                startActivity(intent);
                */
        });

        btnHistoricoInventarios.setOnClickListener(view -> {
            ///*
            Intent intent = new Intent(getBaseContext(), InventariosEnHistorico.class);
            startActivity(intent);

            //*/
                /*
                Intent intent = new Intent(getBaseContext(), Inventario_InventariosHistoricos.class);
                startActivity(intent);

                 */
        });

        btnRegresar.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        });

        btnInfo.setOnClickListener(view -> {
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
            dialog.setTitle("Éste es el módulo informativo");
            dialog.setMessage("Las opciones sirven para:\n\n" +
                    "1.- Registrar inventarios: Nos permite empezar un proceso de inventariado sobre algún material.\n" +
                    "2.- Inventarios en proceso: Podemos ver todos los inventarios que están siendo inventariados, en espera para poder determinarlos como terminados, también permita editar un inventario. \n" +
                    "3.- Históricos de Inventarios: Muestra información detallada sobre las acciones que se realizaron durante los inventarios, puede generar reportes");
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        });
    }

    //Pantalla de cargando
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    //Método para otorgar permisos a la app
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

    public void fullSpinnerAlmacenes(Spinner spinner, Context contexto){
        String[] opciones = {"00 ALMACEN GAMMA", "07 KAPPA","14 DELTA","17 CORTES", "02 GAMMA", "03 GAMMA", "04 ALMACEN ALPHA", "05 GAMMA MUESTRAS", "06 GAMMA SURTIDO",  "08 GAMMA ARETINA", "09 GAMMA BLOQUEADO", "10 GAMMA OP", "12 MONJARAZ", "18 DELTA OP"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.inventario_a_spinner, opciones);
        spinner.setAdapter(adapter);
    }

    public void validateCodigo(){
        if(txtCodigoMaterialPrincipal.getText().toString().equals("")){
            Toast.makeText(context, "¡Error! Información incompleta", Toast.LENGTH_LONG).show();
            txtCodigoMaterialPrincipal.setHint("Campo requerido");
            txtCodigoMaterialPrincipal.setHintTextColor(Color.parseColor("#FF0000"));
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Conexion conexionService = new Conexion(context);
                    try {
                        Statement es = conexionService.conexiondbImplementacion().createStatement();
                        final ResultSet resultado = es.executeQuery("execute PMovil_Item_Scaneados_Nuevo '" + txtCodigoMaterialPrincipal.getText() + "', '" + spinnerAlmacenesPrincipal.getSelectedItem().toString() + "'");
                        if (resultado.next()) {
                            //Mandar por intent
                            material = resultado.getString("Producto");
                            productoID = resultado.getString("ProductoID");
                            unidadMedida = resultado.getString("Unidad");
                            stockTotal = resultado.getString("Fisico");
                            almacen = spinnerAlmacenesPrincipal.getSelectedItem().toString();
                            estadoBloqueado(productoID);
                            Intent intent = new Intent(context, InventariosRegistro.class);
                            intent.putExtra("Material", material);
                            intent.putExtra("UnidadMedida", unidadMedida);
                            intent.putExtra("StockTotal", stockTotal);
                            intent.putExtra("Almacen", almacen);
                            intent.putExtra("ProductoID", productoID);
                            if(cbAjusteInventario.isChecked())
                            {
                                intent.putExtra("Ajuste", 1);
                            }else{
                                intent.putExtra("Ajuste", 0);
                            }
                            //Info usuario
                            intent.putExtra("usuario", usuario);
                            intent.putExtra("pass", contrasenia);
                            //Bloqueado
                            intent.putExtra("estadoBloqueo", 1);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                 }
            });
        }
    }

    //Método para bloquear el material a inventariar
    public void estadoBloqueado(String productoID){
        Conexion con = new Conexion(context);
        try {
            PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("PMovil_Inventarios_Producto_Pausar_UPDATE ?");
            stmt.setString(1, productoID);
            stmt.execute();
            Toast.makeText(context, "¡Material encontrado y bloqueado exitosamente!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
