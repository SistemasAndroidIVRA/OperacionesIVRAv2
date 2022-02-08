package com.example.operacionesivra.Vistas.Administrador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.operacionesivra.Vistas.Administrador.Permisos.DetallesUsuario;
import com.example.operacionesivra.Vistas.MainActivity.MainActivity;
import com.example.operacionesivra.Vistas.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Administrador extends AppCompatActivity {
    List<ModeloDatosdeUsuarioAdministracion> usuarios = new ArrayList<>();
    AdapterModeloDatosDelUsuario adapter;
    RecyclerView recycler;
    Context context;
    public int loadingadministrador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);
        recycler = findViewById(R.id.recyclerusuario);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        context = this;
        loadingadministrador = 1;
        loadinglauncher();

    }

    public void inicializacion() {
        Toolbar toolbar = findViewById(R.id.toolbaradministracion);
        setSupportActionBar(toolbar);
        this.setTitle("Administración");
    }

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    //crea un hilo secundario para ejecutar una pantalla de carga
    public void cargandousuarios() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inicializacion();
                adapter = new AdapterModeloDatosDelUsuario(obtenerusuarios());
                recycler.setAdapter(adapter);
            }
        });
    }

    //rellena el menu con las opciones disponibles
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.administracion, menu);
        return true;
    }

    //Dota de acciones a los elementos del menu
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mas_administracion:
                Intent i = new Intent(context, DetallesUsuario.class);
                startActivity(i);
                break;
            case R.id.buscar_administracion:
                filtrousuario();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Elimina al usuario seleccionado
    public void eliminarusuario(String usuario) {
        Conexion conexion = new Conexion(this);
        try (PreparedStatement p = conexion.conexiondbImplementacion().prepareCall("Execute PMovil_DeleteUsuario ?")) {
            p.setString(1, usuario);
            p.execute();
            for (int i = 0; i < usuarios.size(); i++) {
                if (usuarios.get(i).getIdusuario().equals(usuario)) {
                    usuarios.remove(i);
                    recycler.getAdapter().notifyItemRemoved(i);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    //Carga los usuarios disponibles hasta el momento
    public List<ModeloDatosdeUsuarioAdministracion> obtenerusuarios() {
        Conexion conexion = new Conexion(this);
        if (!usuarios.isEmpty()) {
            usuarios.clear();
        }
        try {
            Statement s = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("select * from Movil_Usuarios");
            while (r.next()) {
                usuarios.add(new ModeloDatosdeUsuarioAdministracion(r.getString("Nombre_Completo"), r.getString("Usuario"), r.getString("Contraseña"), r.getString("Area"), direccion(r.getString("Latitud"), r.getString("Longitud")), r.getString("IDUsuario")));
            }
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
        return usuarios;
    }

    //Regresa la direccion del usuario en tiempo real
    public String direccion(String latitud, String longitud) {
        double lon, lat;
        String direccion = "Direccion no disponible";
        if (!latitud.isEmpty() || !longitud.isEmpty()) {
            lon = Double.parseDouble(longitud);
            lat = Double.parseDouble(latitud);
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(lat, lon, 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    String tempo = DirCalle.getAddressLine(0);
                    direccion = tempo;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return direccion;
    }

    //Muestra los filtros disponibles en la pantalla
    public void filtrousuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Buscar usuario");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buscarusuario(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Ver todos", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                obtenerusuarios();
                Objects.requireNonNull(recycler.getAdapter()).notifyDataSetChanged();
            }
        });
        builder.show();
    }

    //Busca usuarios con nombre parecidos
    public void buscarusuario(String nombre) {
        Conexion conexion = new Conexion(this);
        if (!usuarios.isEmpty()) {
            usuarios.clear();
        }
        try {
            Statement s = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("select * from Movil_Usuarios where Nombre_Completo like '%" + nombre + "%'");
            while (r.next()) {
                usuarios.add(new ModeloDatosdeUsuarioAdministracion(r.getString("Nombre_Completo"), r.getString("Usuario"), r.getString("Contraseña"), r.getString("Area"), direccion(r.getString("Latitud"), r.getString("Longitud")), r.getString("IDUsuario")));
            }
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
        Objects.requireNonNull(recycler.getAdapter()).notifyDataSetChanged();
    }

    //Override de boton atrás
    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmación")
                .setMessage("¿Quiere regresar a la pantalla principal?")
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, MainActivity.class);
                        startActivity(i);
                        finish();
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