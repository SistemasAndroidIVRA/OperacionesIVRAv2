package com.example.operacionesivra.Administrador.Permisos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.operacionesivra.Administrador.Administrador;
import com.example.operacionesivra.Monitoreo.ModeloTipoUsuario;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.example.operacionesivra.Services.ConexionMonitoreo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DetallesUsuario extends AppCompatActivity {
    TextView nombre, usuario, password, area;
    RecyclerView recyclerpermisos;
    AdapterPermisos adapter;
    Context context;
    LinearLayout opcionesusuario;
    public int loadingDetallesUsuario = 0;
    public List<ModeloPermisos> permisos = new ArrayList<>();
    List<ModeloTipoUsuario> tipo = new ArrayList<>();

    String[] opciones;
    String seleccion;
    String idtipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrador_detalles_usuario);
        context = this;
        loadingDetallesUsuario = 2;
        loadinglauncher();

    }

    public void inicializar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nombre = findViewById(R.id.nombreda);
                usuario = findViewById(R.id.usuarioda);
                password = findViewById(R.id.passwordda);
                area = findViewById(R.id.areada);
                opcionesusuario = findViewById(R.id.opcionusuario);
                recyclerpermisos = findViewById(R.id.recyclerpermisos);
                recyclerpermisos.setLayoutManager(new LinearLayoutManager(context));

                adapter = new AdapterPermisos(permisosactuales());
                recyclerpermisos.setAdapter(adapter);
                nuevooeditar();
                findViewById(R.id.guardarda).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //loadingDetallesUsuario = 1;
                        //loadinglauncher();
                        crear_actualizarusuario();
                    }
                });

                findViewById(R.id.aparecer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activaropcionesusuario();
                    }
                });

                findViewById(R.id.tipoUsuario).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vertipousuario();
                    }
                });
            }
        });
    }

    //Verifica el tipom de usuario
    public String vertipousuario() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Seleccione el tipo de usuario")
                .setCancelable(false)
                .setSingleChoiceItems(tiposUsuario(), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccion = opciones[which];
                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < tipo.size(); i++) {
                            if (tipo.get(i).getTipo().equals(seleccion)) {
                                idtipo = tipo.get(i).getIdusuario();
                            }
                        }
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .show();
        return idtipo;
    }

    //Carga el tipo de usuario según la base de datos
    public String[] tiposUsuario() {
        Conexion c = new Conexion(this);
        int contador = 0;
        try {
            Statement s = c.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM Movil_Tipo_Usuario");
            while (r.next()) {
                tipo.add(new ModeloTipoUsuario(r.getString("tipo"), r.getString("IDUsuario")));
                contador++;
            }
            opciones = new String[contador];
            for (int i = 0; i < tipo.size(); i++) {
                opciones[i] = tipo.get(i).getTipo();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return opciones;
    }

    //Actualiza los permisos de ususario, brindando acceso o negandolo segun sea el caso
    public int actualizarpermisosusuario(String idusuario, String idpermiso, boolean ischeck) {
        Conexion conexion = new Conexion(this);
        int ok = 0;
        try (PreparedStatement s = conexion.conexiondbImplementacion().prepareCall("Execute PMovil_UpOrAddPermisosdeUsuario ?,?,?")) {
            s.setString(1, idusuario);
            s.setString(2, idpermiso);
            if (ischeck) {
                s.setString(3, "1");
            } else {
                s.setString(3, "0");
            }
            s.execute();
            ok = 1;
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return ok;
    }

    //Comprueba el metodo de entrada para llenar o no los datos del usuario
    public void nuevooeditar() {
        if (getIntent().getStringExtra("idusuario") != null) {
            datosdelusuario(getIntent().getStringExtra("idusuario"));
        } else {
            System.out.println("Error");
        }
    }

    //Despliega de manera dinamica las caracteristicas del usuario
    public void activaropcionesusuario() {
        if (opcionesusuario.getVisibility() == View.GONE) {
            opcionesusuario.setVisibility(View.VISIBLE);
            opcionesusuario.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fragment_open_enter));
        } else {
            opcionesusuario.setVisibility(View.GONE);
            opcionesusuario.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fragment_open_exit));
        }

    }

    //Sube los datos a la base de datos
    public void crear_actualizarusuario() {
        Conexion conexion = new Conexion(this);
        int comprobar = 0;
        try (PreparedStatement s = conexion.conexiondbImplementacion().prepareCall("PMovil_UpOrAddUsuario ?,?,?,?,?,?")) {
            s.setString(1, getIntent().getStringExtra("idusuario"));
            s.setString(2, nombre.getText().toString());
            s.setString(3, usuario.getText().toString());
            s.setString(4, password.getText().toString());
            s.setString(5, area.getText().toString());
            s.setString(6, "728F24E0-529C-4059-9AE5-413D12B6E3F1");
            s.execute();
        } catch (SQLException e) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Error")
                    .setMessage("Imposible crear/actualizar usuario\nPor favor inténtelo más tarde. "+e.getMessage())
                    .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
        for (int i = 0; i < permisos.size(); i++) {
            comprobar = comprobar + actualizarpermisosusuario(getIntent().getStringExtra("idusuario"), permisos.get(i).getIdpermiso(), permisos.get(i).isCheck());
        }
        if (comprobar == permisos.size()) {
            Intent i = new Intent(context, Administrador.class);
            startActivity(i);
            finish();
        }
    }

    //Carga la informacion de la base de datos
    public void datosdelusuario(String codigo) {
        Conexion conexion = new Conexion(this);
        try {
            Statement s = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("Select * from Movil_usuarios where IDUsuario='" + codigo + "'");
            if (r.next()) {
                nombre.setText(r.getString("Nombre_Completo"));
                password.setText(r.getString("Contraseña"));
                usuario.setText(r.getString("Usuario"));
                area.setText(r.getString("Area"));
                Toast.makeText(this, "Datos cargados!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    //Carga los permisos que existen en la base de datos
    public List<ModeloPermisos> permisosactuales() {
        Conexion conexion = new Conexion(this);
        try {
            Statement s = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("Select * from Movil_permisos");
            while (r.next()) {
                permisos.add(new ModeloPermisos("NombrePermiso", r.getString("NombrePermiso"), r.getString("IDPermiso"), r.getString("Descripcion"), getIntent().getStringExtra("idusuario"), false));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return permisos;
    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

}