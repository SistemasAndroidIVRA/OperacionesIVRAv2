package com.example.operacionesivra.PantallaRecepcion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.MainActivity.MainActivity;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import repack.org.bouncycastle.cms.CMSAttributeTableGenerationException;

public class PantallaDeRecepcion extends  YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final  String APY_KEY = "AIzaSyBz6_qZa1BXC7xl8Rn8NnOipqxrpMwunBM";
    Conexion conexionService = new Conexion(this);
    Context context;
    private RecyclerView recycerpedidos;
    private AdapterRecepcion adaptador;
    List<ModeloRecepcion> pedidos = new ArrayList<>();
    List<ModeloVideos> videos = new ArrayList<>();
    //List<ModeloRecepcion_Sinfiltro>  = new ArrayList<>();
    //List<ModeloReporteMovimientos> reportedemovimientos = new ArrayList<>();
    private final DBListenerRecepcion duckFactory = new DBListenerRecepcion(this);
    TextToSpeech tts;
    YouTubePlayerView youTubePlayerView;
    boolean disparadorautomatico =false;
    public int loadingRecepcion =0;
    int listener2=0;
    DigitalClock recepcion;
    ImageView logoShimaco,surtiendo,revisado, liberado, generandonota,registrado;
    String seleccion;
    String liga;
    PantallaDeRecepcion pantallaDeRecepcion;
    String[] opciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recepcion_pantalla_de_recepcion);
        pantallaDeRecepcion = this;
        context = this;
        recycerpedidos = findViewById(R.id.recyclerRecepcion);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdapterRecepcion(obtenerpedidosdbImplementacion());
        recepcion = findViewById(R.id.relojrecepcion);
        recycerpedidos.setAdapter(adaptador);
        duckFactory.start();
        logoShimaco = findViewById(R.id.shimacologo);
        logoShimaco.setImageResource(R.drawable.logoshimaco);
        surtiendo = findViewById(R.id.psurtido);
        surtiendo.setImageResource(R.drawable.surtiendo);
        revisado = findViewById(R.id.previsado);
        revisado.setImageResource(R.drawable.pedidorevisado);
        liberado= findViewById(R.id.pliberado);
        liberado.setImageResource(R.drawable.pedidoaprobado);
        generandonota = findViewById(R.id.pnota);
        generandonota.setImageResource(R.drawable.generarnota);
        registrado= findViewById(R.id.pregistrado);
        registrado.setImageResource(R.drawable.nuevopedido);

        seleccionarplaylist();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    int lang = tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        logoShimaco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Confirmación")
                        .setMessage("¿Quiere cambiar la playlist actual?")
                        .setCancelable(false)
                        .setIcon(R.drawable.confirmacion)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingRecepcion=1;
                                //loadinglauncher();
                                reiniciarActivity(pantallaDeRecepcion);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }

    //Lanza un alert dialog con las listas de reproducción disponibles
    public String seleccionarplaylist(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("¿Qué vamos a escuchar hoy?")
                .setCancelable(false)
                .setSingleChoiceItems(cargarplaylist(), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            seleccion = opciones[which];
                            youTubePlayerView=null;
                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccionarvideo();
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .show();
        return liga;
    }

    public void seleccionarvideo(){
        youTubePlayerView = findViewById(R.id.video);
        youTubePlayerView.initialize(APY_KEY, this);
    }

    /*
    public void actualizarRegistros(String pedido, String movimiento, String cliente, String referencia) {
        boolean existe=false;
        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        for (int i = 0; i < reportedemovimientos.size(); i++) {
            if (reportedemovimientos.get(i).getPedido().equals(pedido)) {
                switch (movimiento) {
                    case "1":
                        reportedemovimientos.set(i, new ModeloReporteMovimientos(reportedemovimientos.get(i).getPedido()
                                , reportedemovimientos.get(i).getCliente(),movimiento,reportedemovimientos.get(i).getReferencia(), hora, reportedemovimientos.get(i).getHoradelmovimiento2()
                                , reportedemovimientos.get(i).getHoradelmovimiento3(), reportedemovimientos.get(i).getHoradelmovimiento4()));
                        break;
                    case "2":
                        reportedemovimientos.set(i, new ModeloReporteMovimientos(reportedemovimientos.get(i).getPedido()
                                , reportedemovimientos.get(i).getCliente(),movimiento,reportedemovimientos.get(i).getReferencia(), reportedemovimientos.get(i).getHoradelmovimiento1(), hora
                                , reportedemovimientos.get(i).getHoradelmovimiento3(), reportedemovimientos.get(i).getHoradelmovimiento4()));
                        break;
                    case "3":
                        reportedemovimientos.set(i, new ModeloReporteMovimientos(reportedemovimientos.get(i).getPedido()
                                , reportedemovimientos.get(i).getCliente(),movimiento,reportedemovimientos.get(i).getReferencia(), reportedemovimientos.get(i).getHoradelmovimiento1(), reportedemovimientos.get(i).getHoradelmovimiento2()
                                , hora, reportedemovimientos.get(i).getHoradelmovimiento4()));
                        break;
                    case "4":
                        reportedemovimientos.set(i, new ModeloReporteMovimientos(reportedemovimientos.get(i).getPedido()
                                , reportedemovimientos.get(i).getCliente(), movimiento, reportedemovimientos.get(i).getReferencia(),reportedemovimientos.get(i).getHoradelmovimiento1(), reportedemovimientos.get(i).getHoradelmovimiento2()
                                , reportedemovimientos.get(i).getHoradelmovimiento3(), hora));
                        break;
                }
                existe=true;
            }
        }
        if(!existe){
            switch (movimiento){
                case "1":
                    reportedemovimientos.add(new ModeloReporteMovimientos(pedido
                            , cliente, movimiento,referencia, hora, ""
                            , "",""));
                    break;
                case "2":
                    reportedemovimientos.add(new ModeloReporteMovimientos(pedido
                            , cliente,movimiento,referencia, "", hora
                            , "",""));
                    break;
                case "3":
                    reportedemovimientos.add(new ModeloReporteMovimientos(pedido
                            , cliente, movimiento,referencia, "", ""
                            , hora,""));
                    break;
                case "4":
                    reportedemovimientos.add(new ModeloReporteMovimientos(pedido
                            , cliente,movimiento, referencia,"", ""
                            , "",hora));
                    break;
            }
        }


    }

     */

    //Crea una lista que almacena los datos de la base de manera automatica (Implementacion)
    public List<ModeloRecepcion> obtenerpedidosdbImplementacion() {
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PantallaRecepcion");
            while (r.next()) {
                    pedidos.add(new ModeloRecepcion(r.getString("Pedido"),r.getString("Estado_Clave"),r.getString("Cliente")) );
                                }
        } catch (Exception e) {
            System.out.println("error");
        }
        //Crearlistadepedidos_sinfiltro();
        return pedidos;
    }

    /*
    public List<ModeloRecepcion_Sinfiltro> Crearlistadepedidos_sinfiltro() {
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PantallaRecepcion_sinfiltro");
            while (r.next()) {
                pedidossinfiltro.add(new ModeloRecepcion_Sinfiltro(r.getString("Pedido"),r.getString("Estado_Clave"),r.getString("Cliente"),r.getString("Referencia")) );
                actualizarRegistros(r.getString("Pedido"),r.getString("Estado_Clave"),r.getString("Cliente"),r.getString("Referencia"));
            }
        } catch (Exception e) {
            System.out.println("error");
        }
        return pedidossinfiltro;
    }

     */

    //Actualiza el registro segun el esta del pedido
    public void actualizarlista(){
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PantallaRecepcion");
            while (r.next()) {
                for (int i =0;i<pedidos.size();i ++){
                    if(pedidos.get(i).getPedido().equals(r.getString("Pedido"))){
                        if(!pedidos.get(i).getEstado().equals(r.getString("Estado_Clave"))){
                                pedidos.set(i, new ModeloRecepcion(r.getString("Pedido"),r.getString("Estado_Clave"),r.getString("Cliente")) );
                                MediaPlayer mp = MediaPlayer.create(this, R.raw.notification_ring);
                            mp.start();
                                final int finalI = i;
                            final String cliente = r.getString("Cliente");
                                  final String fase = r.getString("Estado_clave");
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void run() {
                                    switch (fase){
                                        case "1":
                                            speech(cliente+", pedido registrado");
                                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyItemChanged(finalI);
                                            break;
                                        case "2":
                                            speech(cliente+", pedido autorizado");
                                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyItemChanged(finalI);
                                            break;
                                        case "3":
                                            speech(cliente+", pedido en surtido");
                                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyItemChanged(finalI);
                                            break;
                                        case "4":
                                            speech(cliente+", preparando nota");
                                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyItemChanged(finalI);
                                            break;
                                    }
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error");
        }
        //actualizarlista_sinfiltro();
    }

    /*
    public void actualizarlista_sinfiltro(){
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PantallaRecepcion_sinfiltro");
            while (r.next()) {
                for (int i =0;i<pedidossinfiltro.size();i ++){
                    if(pedidossinfiltro.get(i).getPedido().equals(r.getString("Pedido"))){
                        if(!pedidossinfiltro.get(i).getEstado().equals(r.getString("Estado_Clave"))){
                            pedidossinfiltro.set(i, new ModeloRecepcion_Sinfiltro(r.getString("Pedido"),r.getString("Estado_Clave"),r.getString("Cliente"),r.getString("Referencia")) );
                            actualizarRegistros(r.getString("Pedido"),r.getString("Estado_Clave"),r.getString("Cliente"),r.getString("Referencia"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error");
        }
    }
     */

    //Añade registros nuevos en caso de existir
    public void añadiralalista(){
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            final ResultSet r = qu.executeQuery("Execute PMovil_PantallaRecepcion");
            while (r.next()){
                boolean comprobarexistencia=false;
                for(int i=0;i<pedidos.size();i++){
                    if(r.getString("Pedido").equals(pedidos.get(i).getPedido())){
                        comprobarexistencia=true;
                    }
                }
                if(!comprobarexistencia){
                    MediaPlayer mp = MediaPlayer.create(this, R.raw.notification_ring);
                    mp.start();
                    final String cliente=r.getString("Cliente");
                        pedidos.add(new ModeloRecepcion(r.getString("Pedido"), r.getString("Estado_clave"), r.getString("Cliente")));
                        runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyDataSetChanged();
                            speech("¡Pedido nuevo de "+cliente+ "!");
                        }
                    });
                }
            }
        }catch (Exception e){
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Error al cargar")
                    .setMessage("Por favor intentelo mas tarde")
                    .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
        //añadiralalista_sinfiltro();
    }

    /*
    public void añadiralalista_sinfiltro(){
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            final ResultSet r = qu.executeQuery("Execute PMovil_PantallaRecepcion_sinfiltro");
            while (r.next()){
                boolean comprobarexistencia=false;
                for(int i=0;i<pedidossinfiltro.size();i++){
                    if(r.getString("Pedido").equals(pedidossinfiltro.get(i).getPedido())){
                        comprobarexistencia=true;
                    }
                }
                if(!comprobarexistencia){
                    pedidossinfiltro.add(new ModeloRecepcion_Sinfiltro(r.getString("Pedido"), r.getString("Estado_clave"), r.getString("Cliente"),r.getString("Referencia")));
                    actualizarRegistros(r.getString("Pedido"),r.getString("Estado_Clave"),r.getString("Cliente"),r.getString("Referencia"));
                }
            }
        }catch (Exception e){
            Toast.makeText(context,"Error al cargar",Toast.LENGTH_SHORT);
        }
    }
     */

    //Elimina el elemto al estar completado o bien es eliminado
    public int elimarlista(){
        int listaner=0;
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            for(int i =0;i<pedidos.size();i++){
                ResultSet r2 = qu.executeQuery("Execute PMovil_PantallaRecepcion");
                boolean comprobarexistencia=false;
                while (r2.next()){
                    if(pedidos.get(i).getPedido().equals(r2.getString("Pedido"))){
                        comprobarexistencia=true;
                    }
                }
                if(!comprobarexistencia){
                    MediaPlayer mp = MediaPlayer.create(this, R.raw.notification_ring);
                    mp.start();
                    listaner=1;
                    final String cliente = pedidos.get(i).getCliente();
                    pedidos.remove(i);
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyItemRemoved(finalI);
                            speech(cliente+", su nota está lista");
                        }
                    });
                }
            }
        }catch (SQLException e){
            System.out.println(e);
        }
        //elimarlista_sinfiltro();
        listener2=listaner;
        return listaner;
    }

    /*
    public int elimarlista_sinfiltro(){
        int listaner=0;
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            for(int i =0;i<pedidossinfiltro.size();i++){
                ResultSet r2 = qu.executeQuery("Execute PMovil_PantallaRecepcion_sinfiltro");
                boolean comprobarexistencia=false;
                while (r2.next()){
                    if(pedidossinfiltro.get(i).getPedido().equals(r2.getString("Pedido"))){
                        comprobarexistencia=true;
                    }
                }
                if(!comprobarexistencia){
                    actualizarRegistros(pedidossinfiltro.get(i).getPedido(),pedidossinfiltro.get(i).getEstado(),pedidossinfiltro.get(i).getCliente(),pedidossinfiltro.get(i).getReferencia());
                    pedidossinfiltro.remove(i);
                }
            }
        }catch (SQLException e){
            System.out.println(e);
        }
        listener2=listaner;
        return listaner;
    }
     */

    //Lanza la voz
    public void speech(String text){
        Locale locSpanish = new Locale("spa", "MEX");
        tts.setLanguage(locSpanish);
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    //Util pero no lo uso ahora
    public static void reiniciarActivity(Activity actividad){
        Intent intent=new Intent();
        intent.setClass(actividad, actividad.getClass());
        //llamamos a la actividad
        actividad.startActivity(intent);
        //finalizamos la actividad actual
        actividad.finish();
    }

    /*
    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

     */

    /*

    public void subirtablaDB() {
        String uuid = UUID.randomUUID().toString();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        try {
            for (int i = 0; i < reportedemovimientos.size(); i++) {
                try (PreparedStatement var = conexionService.conexiondbImplementacion().prepareCall("execute PMovil_Crearinformedemovimientos ?,?,?,?,?,?,?,?,?")) {
                    try {
                        var.setString(1, reportedemovimientos.get(i).getPedido());
                        var.setString(2, reportedemovimientos.get(i).getCliente());
                        var.setString(3, reportedemovimientos.get(i).getReferencia());
                        var.setString(4, reportedemovimientos.get(i).getHoradelmovimiento1());
                        var.setString(5, reportedemovimientos.get(i).getHoradelmovimiento2());
                        var.setString(6, reportedemovimientos.get(i).getHoradelmovimiento3());
                        var.setString(7, reportedemovimientos.get(i).getHoradelmovimiento4());
                        var.setString(8, date);
                        var.setString(9, uuid);
                        var.execute();
                    } catch (SQLException e) {
                        System.out.println("Error"+e);
                    }
                } catch (SQLException a) {
                    System.out.println("Error2"+a);
                }

            }
        } catch (Exception e) {
            System.out.println("Error3"+e);
        }
    }

     */

    @Override
    public void onBackPressed() {
        finish();
        duckFactory.terminar();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public String[] cargarplaylist(){
        Conexion c = new Conexion(this);
        int contador=0;
        try{
            Statement s = c.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("select * from Movil_Recepcion_videos");
            while (r.next()){
                videos.add(new ModeloVideos(r.getString("Nombre"),r.getString("URL")));
                contador++;
            }
            opciones = new String[contador];
            for(int i = 0;i<videos.size();i++){
                opciones[i]=videos.get(i).getNombre();
            }
        }catch (Exception e){
            System.out.println("nel"+ e);
        }
        return opciones;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(null== youTubePlayer) return;
        if (!b) {
            for (int i = 0; i < videos.size(); i++) {
                if (videos.get(i).getNombre().equals(seleccion)) {
                    youTubePlayer.cuePlaylist(videos.get(i).getUrl());
                    youTubePlayer.play();
                }
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Omposible cargar, por favor, inténtelo más tarde")
                .setIcon(R.drawable.snakerojo)
                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reiniciarActivity(pantallaDeRecepcion);
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .show();
    }
}