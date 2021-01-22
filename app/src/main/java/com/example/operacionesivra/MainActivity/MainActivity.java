package com.example.operacionesivra.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.example.operacionesivra.Administrador.Administrador;
import com.example.operacionesivra.Chequeo.ListadePedidos.ListadeChequeo;
import com.example.operacionesivra.Monitoreo.Monitoreo_Mapa;
import com.example.operacionesivra.PantallaDePrioridades.PantalladePrioridades;
import com.example.operacionesivra.PantallaRecepcion.PantallaDeRecepcion;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.Reportes.SelectordeReportes;
import com.example.operacionesivra.Services.Conexion;
import com.example.operacionesivra.Inventario.Inventario;
import com.example.operacionesivra.Picking.ListapedidosPicking.ListaPicking;
import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.example.operacionesivra.ComprobaciondeDispositivo.TabletOTelefono.esTablet;

public class MainActivity extends AppCompatActivity{
    Context context;
    CardView inventario, picking, listadeprioridades, recepcion, chequeo, reportes, monitoreo,administrador;
    String usuario, password, idusuario;
    public int loadingMain=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        inventario = findViewById(R.id.inventario);
        picking = findViewById(R.id.picking);
        listadeprioridades = findViewById(R.id.prioridades);
        recepcion = findViewById(R.id.recepcion);
        chequeo = findViewById(R.id.chequeo);
        reportes = findViewById(R.id.reportes);
        monitoreo = findViewById(R.id.monitoreo);
        administrador = findViewById(R.id.administrador);
        Toolbar toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        listenersdeopcionesdisponibles();
        loadingMain =1;
        loadinglauncher();
        //Animacion de botton
        //v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.fragment_fade_enter));
    }


    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }




    public void listenersdeopcionesdisponibles(){
        inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.fragment_fade_enter));
                if(esTablet(context)){
                    Inventario();
                }
                else{
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Error")
                            .setMessage("El dispositivo actual no cuenta con las dimenciones correctas" +
                                    " para el correcto uso de este módulo.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        });

        picking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.fragment_fade_enter));
                Picking();
            }
        });

        listadeprioridades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.fragment_fade_enter));
                if(esTablet(context)){
                    PantallaPrioridades();
                }
                else{
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Error")
                            .setMessage("El dispositivo actual no cuenta con las dimenciones correctas" +
                                    " para el correcto uso de este módulo.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        });

        recepcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.fragment_fade_enter));
                if(esTablet(context)){
                    PantalladeRecepcion();
                }
                else{
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Error")
                            .setMessage("El dispositivo actual no cuenta con las dimenciones correctas" +
                                    " para el correcto uso de este módulo.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        });

        chequeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.fragment_fade_enter));
                Chequeo();
            }
        });

        reportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.fragment_fade_enter));
                Reportes();
            }
        });

        monitoreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.fragment_fade_enter));
                Monitoreo();
            }
        });

        administrador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.fragment_fade_enter));
                Administrador();
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return  true;

    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id==R.id.item1){
            cerrarsesion();
        }
        return super.onOptionsItemSelected(item);

    }

    public void cerrarsesion(){
        final Intent i = new Intent(this,Login.class);
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Quiere Cerrar la sesión actual?")
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       editor.clear();
                        editor.apply();
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

    public void Picking(){
        Intent picking = new Intent(getBaseContext(), ListaPicking.class);
        startActivity(picking);
    }

    public void PantalladeRecepcion(){
        Intent recepcion = new Intent(getBaseContext(), PantallaDeRecepcion.class);
        startActivity(recepcion);
    }

    public void PantallaPrioridades(){
        Intent prioridades = new Intent(getBaseContext(), PantalladePrioridades.class);
        startActivity(prioridades);
    }

    public void Inventario(){
        Intent inventario = new Intent(getBaseContext(), Inventario.class);
        inventario.putExtra("usuario",usuario);
        inventario.putExtra("contraseña", password);
        startActivity(inventario);
    }

    public void Chequeo(){
        Intent chequeo = new Intent(getBaseContext(), ListadeChequeo.class);
        startActivity(chequeo);
    }

    public void Reportes(){
        Intent reportes = new Intent(getBaseContext(), SelectordeReportes.class);
        startActivity(reportes);
    }

    public void Monitoreo(){
        Intent reportes = new Intent(getBaseContext(), Monitoreo_Mapa.class);
        startActivity(reportes);
    }

    public void Administrador(){
        Intent reportes = new Intent(getBaseContext(), Administrador.class);
        startActivity(reportes);
    }

    public void comprobarsesion(){
        SharedPreferences preferences = getSharedPreferences("credenciales",Context.MODE_PRIVATE);
        String user = preferences.getString("user","Vacio");
        String pass = preferences.getString("pass","Vacio");
        final String idusuario = preferences.getString("iduser","Vacio");
        if(user.equals("Vacio")){
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
        }else{
            usuario = user;
            password = pass;
            this.idusuario = idusuario;
            this.setTitle("Hola "+user);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
            habilitarmodulos(verpermisos(idusuario));
                }
            });
        }
    }

    public List<String> verpermisos(String idusuario){
        ArrayList<String> permisos = new ArrayList<>();
        try {
            Conexion conexion = new Conexion(this);
            Statement s = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("Select * from movil_usuario_permiso where idusuario='"+idusuario+"'");
            while(r.next()){
                permisos.add(r.getString("IDPermiso"));
            }
        }catch (Exception e){
            System.out.println("help"+ e);
        }
        return permisos;
    }

    public void habilitarmodulos(List<String> lista){
        for(int i=0; i< lista.size();i++){
            switch (lista.get(i)){
                case "B48F8EC0-6104-4C92-9336-01A90CEE1193":
                    inventario.setVisibility(View.VISIBLE);
                    break;
                case "17EB27AC-EC09-4FF7-AD50-3126695F3E39":
                    picking.setVisibility(View.VISIBLE);
                    break;
                case "0866AE80-D0EF-4AFB-BE64-5DFE7F2A82D1":
                    reportes.setVisibility(View.VISIBLE);
                    break;
                case "1DD1275B-96AB-4C06-AD88-764848BEA82E":
                    administrador.setVisibility(View.VISIBLE);
                    break;
                case "4307FA8F-1B95-4872-A530-A53DCBCBA4E8":
                    recepcion.setVisibility(View.VISIBLE);
                    break;
                case "5C4DB45B-A0BD-4155-A36C-BEAEBC8D97B9":
                    monitoreo.setVisibility(View.VISIBLE);
                    break;
                case "E5AABE8C-0E81-4236-BFF4-D3D945432B1A":
                    chequeo.setVisibility(View.VISIBLE);
                    break;
                case "812B0931-1986-4F4A-A439-FBD2357B8B8F":
                    listadeprioridades.setVisibility(View.VISIBLE);
                    break;

            }
        }
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Salir de la aplicación")
                .setMessage("¿Quiere finalizar la aplicación?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
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
