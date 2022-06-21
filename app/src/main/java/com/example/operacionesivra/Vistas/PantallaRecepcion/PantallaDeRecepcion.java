package com.example.operacionesivra.Vistas.PantallaRecepcion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Vistas.MainActivity.MainActivity;
import com.example.operacionesivra.Vistas.PantallaRecepcion.administrar.RecepcionAdministrarVideos;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.google.android.gms.vision.text.Line;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PantallaDeRecepcion extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener,
        YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener {
    public static final String APY_KEY = "AIzaSyBz6_qZa1BXC7xl8Rn8NnOipqxrpMwunBM";
    Conexion conexionService = new Conexion(this);
    Context context;
    private RecyclerView recycerpedidos;
    private AdapterRecepcion adaptador;
    List<ModeloRecepcion> pedidos = new ArrayList<>();
    private final DBListenerRecepcion duckFactory = new DBListenerRecepcion(this);
    TextToSpeech tts;
    public int loadingRecepcion = 0;
    int listener2 = 0;
    DigitalClock recepcion;
    ImageView logoShimaco;
    String videoSeleccionado;
    int posicionSeleccionado = -1;
    String liga;
    PantallaDeRecepcion pantallaDeRecepcion;
    String[] opciones;
    Button btnReproductor;
    ImageButton imgButtonNext, imgButtonAtras, imageButtonAcercar, imageButtonAlejar;
    ArrayList<ModeloVideos> arrayVideos = new ArrayList();
    //Reproductor de youtube
    YouTubePlayer mPlayer;
    //Vista de reproductor de youtube
    YouTubePlayerView youTubePlayerView;

    float scale = 1;
    float dimension = 1.63f;
    LinearLayout linearLayoutVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recepcion_pantalla_de_recepcion);

        pantallaDeRecepcion = this;
        context = this;
        recycerpedidos = findViewById(R.id.recyclerRecepcion);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(context));
        adaptador = new AdapterRecepcion(obtenerpedidosdbImplementacion());
        recepcion = findViewById(R.id.relojrecepcion);
        recycerpedidos.setAdapter(adaptador);
        duckFactory.start();
        logoShimaco = findViewById(R.id.shimacologo);
        logoShimaco.setImageResource(R.drawable.logoshimaco);
        //se asigna la vista
        youTubePlayerView = findViewById(R.id.video);

        arrayVideos = getVideos();

        btnReproductor = findViewById(R.id.btnReproductor);

        imageButtonAcercar = findViewById(R.id.imageButtonAcercar);
        imageButtonAlejar = findViewById(R.id.imageButtonAlejar);
        linearLayoutVideo = findViewById(R.id.layoutVideo);

        seleccionarCancion();
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int lang = tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        btnReproductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarCancion();

            }
        });
        imageButtonAcercar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maximize();
            }
        });
        imageButtonAlejar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minimize();
            }
        });
    }
    //Método para seleccionar la canción
    public void seleccionarCancion(){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view2 = inflater.inflate(R.layout.recepcion_reproductor, null);
        alert.setView(view2);
        AlertDialog dialog = alert.create();
        dialog.show();
        //Referenciar elementos
        RecyclerView recyclerReproductor = view2.findViewById(R.id.recyclerReproductor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerReproductor.setLayoutManager(layoutManager);
        //Adaptador

        AdapterVideos adapterVideos = new AdapterVideos();
        recyclerReproductor.setAdapter(adapterVideos);
        //BtnAceptar
        Button btnAceptarReporductor, btnAdministrar, btnCerrarReproductor;
        btnAceptarReporductor = view2.findViewById(R.id.btnAceptarReporductor);
        btnAdministrar = view2.findViewById(R.id.btnAdministrar);
        btnCerrarReproductor = view2.findViewById(R.id.btnCerrarReproductor);
        imgButtonNext = view2.findViewById(R.id.imgButtonNext);
        imgButtonNext.setImageResource(R.drawable.next);
        imgButtonAtras = view2.findViewById(R.id.imgButtonAtras);
        imgButtonAtras.setImageResource(R.drawable.backbtn);

        //Acciones
        imgButtonAtras.setOnClickListener(view -> {
            ModeloVideos video = arrayVideos.get(arrayVideos.size()-1);
            arrayVideos.add(0, video);
            arrayVideos.remove(arrayVideos.size()-1);
            adapterVideos.notifyDataSetChanged();
        });
        imgButtonNext.setOnClickListener(view -> {
            ModeloVideos video = arrayVideos.get(0);
            arrayVideos.remove(0);
            arrayVideos.add(arrayVideos.size(), video);
            adapterVideos.notifyDataSetChanged();
        });
        btnAceptarReporductor.setOnClickListener(view1 -> {
            videoSeleccionado = arrayVideos.get(0).getNombre();
            youTubePlayerView.initialize(APY_KEY, this);
            playVideo(videoSeleccionado);
            dialog.dismiss();
        });
        btnAdministrar.setOnClickListener(view -> {
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
                    if(input.getText().toString().equals("AdminVideos")){
                        Intent intent = new Intent(context, RecepcionAdministrarVideos.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                        //setTerminarInventarios();
                    }else{
                        new MaterialAlertDialogBuilder(context)
                                .setTitle("¡Contraseña inválida!")
                                .setIcon(R.drawable.snakerojo)
                                .setMessage("Si continúa se notificará a los administradores.")
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
        });
        btnCerrarReproductor.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    //Crea una lista que almacena los datos de la base de manera automatica
    public List<ModeloRecepcion> obtenerpedidosdbImplementacion() {
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PantallaRecepcion");
            while (r.next()) {
                pedidos.add(new ModeloRecepcion(r.getString("Pedido"), r.getString("Estado_Clave"), r.getString("Cliente")));
            }
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
        return pedidos;
    }

    //Actualiza el registro segun el esta del pedido
    public void actualizarlista() {
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PantallaRecepcion");
            while (r.next()) {
                for (int i = 0; i < pedidos.size(); i++) {
                    if (pedidos.get(i).getPedido().equals(r.getString("Pedido"))) {
                        if (!pedidos.get(i).getEstado().equals(r.getString("Estado_Clave"))) {
                            pedidos.set(i, new ModeloRecepcion(r.getString("Pedido"), r.getString("Estado_Clave"), r.getString("Cliente")));
                            MediaPlayer mp = MediaPlayer.create(this, R.raw.notification_ring);
                            mp.start();
                            final int finalI = i;
                            final String cliente = r.getString("Cliente");
                            final String fase = r.getString("Estado_clave");
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void run() {
                                    switch (fase) {
                                        case "1":
                                            speech(cliente + ", pedido registrado");
                                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyItemChanged(finalI);
                                            break;
                                        case "2":
                                            speech(cliente + ", pedido autorizado");
                                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyItemChanged(finalI);
                                            break;
                                        case "3":
                                            speech(cliente + ", pedido en surtido");
                                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyItemChanged(finalI);
                                            break;
                                        case "4":
                                            speech(cliente + ", preparando nota");
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
            System.out.println("error: " + e);
        }
    }

    //Añade registros nuevos en caso de existir
    public void añadiralalista() {
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            final ResultSet r = qu.executeQuery("Execute PMovil_PantallaRecepcion");
            while (r.next()) {
                boolean comprobarexistencia = false;
                for (int i = 0; i < pedidos.size(); i++) {
                    if (r.getString("Pedido").equals(pedidos.get(i).getPedido())) {
                        comprobarexistencia = true;
                    }
                }
                if (!comprobarexistencia) {
                    MediaPlayer mp = MediaPlayer.create(this, R.raw.notification_ring);
                    mp.start();
                    final String cliente = r.getString("Cliente");
                    pedidos.add(new ModeloRecepcion(r.getString("Pedido"), r.getString("Estado_clave"), r.getString("Cliente")));
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyDataSetChanged();
                            speech("¡Pedido nuevo de " + cliente + "!");
                        }
                    });
                }
                //Metodo para reiniciar el recycler para resolver fallo de las progressbar
                resetRecycler();
            }
        } catch (Exception e) {
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
    }
    //Sirve para resetear el recycler debido al fallo en el progress bar
    public void resetRecycler(){
        recycerpedidos.setLayoutManager(null);
        recycerpedidos.setAdapter(null);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(context));
        recycerpedidos.setAdapter(adaptador);
    }

    //Elimina el elemto al estar completado o bien es eliminado
    public int elimarlista() {
        int listaner = 0;
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            for (int i = 0; i < pedidos.size(); i++) {
                ResultSet r2 = qu.executeQuery("Execute PMovil_PantallaRecepcion");
                boolean comprobarexistencia = false;
                while (r2.next()) {
                    if (pedidos.get(i).getPedido().equals(r2.getString("Pedido"))) {
                        comprobarexistencia = true;
                    }
                }
                if (!comprobarexistencia) {
                    MediaPlayer mp = MediaPlayer.create(this, R.raw.notification_ring);
                    mp.start();
                    listaner = 1;
                    final String cliente = pedidos.get(i).getCliente();
                    pedidos.remove(i);
                    //final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            //Objects.requireNonNull(recycerpedidos.getAdapter()).notifyItemRemoved(finalI);
                            Objects.requireNonNull(recycerpedidos.getAdapter()).notifyDataSetChanged();
                            speech(cliente + ", su nota está lista");
                        }
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        listener2 = listaner;
        return listaner;
    }

    //Lanza la voz
    public void speech(String text) {
        Locale locSpanish = new Locale("spa", "MEX");
        tts.setLanguage(locSpanish);
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    public ArrayList<ModeloVideos> getVideos(){
        try {
            ArrayList<ModeloVideos> array = new ArrayList<>();
            PreparedStatement stmt = conexionService.conexiondbImplementacion().prepareCall("PMovil_Recepcion_Videos_SELECT");
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                array.add(new ModeloVideos(r.getString("Nombre"), r.getString("URL"), 0));
            }
            return array;
        }catch (Exception e){
            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return new ArrayList<ModeloVideos>();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        duckFactory.terminar();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlaybackEventListener(this);
        youTubePlayer.setPlaybackEventListener(this);
        if (!b) {
            //Se asigna el reproductor de youtube para poder hacer el cambio de cancion
            mPlayer = youTubePlayer;
            for (int i = 0; i < arrayVideos.size(); i++) {
                            if (arrayVideos.get(i).getNombre().equals(videoSeleccionado)) {
                                if(youTubePlayer.isPlaying()){
                                    youTubePlayer.release();
                                    youTubePlayer.cuePlaylist(arrayVideos.get(i).getUrl());
                                    youTubePlayer.play();
                                }else{
                                    youTubePlayer.cuePlaylist(arrayVideos.get(i).getUrl());
                                    youTubePlayer.play();
                    }
                }
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Imposible cargar, por favor, inténtelo más tarde")
                .setIcon(R.drawable.snakerojo)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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

    @Override
    public void onPlaying() {

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Procedimientos para minimizar o maximizar el dialgo de youtube junto con los botones
    public void minimize() {
        //Se obtiene el contenedor donde se encuentra el dialogo de youtube y los botones
        ViewGroup.LayoutParams params = linearLayoutVideo.getLayoutParams();
        //Se mininiza, con un porcentaje mayor en el ancho para evitar deformaciones en la dimension del video
        params.width = (int) (linearLayoutVideo.getWidth()-(10*dimension));
        params.height = linearLayoutVideo.getHeight()-10;
        linearLayoutVideo.setLayoutParams(params);
    }
    public void maximize(){
        //Se obtiene el contenedor donde se encuentra el dialogo de youtube y los botones
        ViewGroup.LayoutParams params = linearLayoutVideo.getLayoutParams();
        //Se maximiza, con un porcentaje mayor en el ancho para evitar deformaciones en la dimension del video
        params.width = (int) (linearLayoutVideo.getWidth()+(10*dimension));
        params.height = linearLayoutVideo.getHeight()+10;
        linearLayoutVideo.setLayoutParams(params);

    }
//Metodo para cambiar la cancion que se seleccione
    public void playVideo(String url){
        if(mPlayer != null){
            for (int i = 0; i < arrayVideos.size(); i++) {
                if (arrayVideos.get(i).getNombre().equals(videoSeleccionado)) {
                    mPlayer.cuePlaylist(arrayVideos.get(i).getUrl());
                }
            }
        }
    }

    //Adapter videos

    public class AdapterVideos extends RecyclerView.Adapter<AdapterVideos.AdapterVideosHolder>{
        @NonNull
        @Override
        public AdapterVideosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterVideosHolder(getLayoutInflater().inflate(R.layout.recepcion_reproductor_item, parent, false));
        }

        @Override
        public void onBindViewHolder(AdapterVideosHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return arrayVideos.size();
        }

        class AdapterVideosHolder extends RecyclerView.ViewHolder implements View.OnClickListener, YouTubeThumbnailView.OnInitializedListener {
            TextView lblConsecutivoReproductor, lblNombreReproductor;
            LinearLayout layoutSeleccionar;

            //Miniatura de youtube para cargar imagenes en la seleccion de canciones
            YouTubeThumbnailView youTubeThumbnailView;
            YouTubeThumbnailLoader youTubeThumbnailLoadera;
            public AdapterVideosHolder(@NonNull View itemView){
                super(itemView);

                lblConsecutivoReproductor = itemView.findViewById(R.id.lblConsecutivoReproductor);
                lblNombreReproductor = itemView.findViewById(R.id.lblNombreReproductor);
                layoutSeleccionar = itemView.findViewById(R.id.layoutSeleccionar);

                youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtubethumbnailview);
                youTubeThumbnailView.initialize(APY_KEY, this);
                //Acciones

                layoutSeleccionar.setOnClickListener(view -> {
                    for (int i=0; i<arrayVideos.size();i++){
                        arrayVideos.get(i).setIsSelected(0);
                    }
                    arrayVideos.get(getAdapterPosition()).setIsSelected(1);
                    posicionSeleccionado = getAdapterPosition();
                    notifyDataSetChanged();
                });
            }

            public void printAdapter(int position){
                lblConsecutivoReproductor.setText(""+(position+1));
                lblNombreReproductor.setText(arrayVideos.get(position).getNombre());

               setYouTubeThumbnailView(position);
                if(arrayVideos.get(position).getIsSelected() == 0){
                    layoutSeleccionar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }else{
                    layoutSeleccionar.setBackgroundColor(Color.parseColor("#D8E7FF"));
                }
            }

            @Override
            public void onClick(View view){

            }
            public void setYouTubeThumbnailView(int position){
                //Se asigna la url a la miniatura para que la cargue
                if(youTubeThumbnailLoadera != null){
                    youTubeThumbnailLoadera.setPlaylist(arrayVideos.get(position).getUrl());
                }
            }
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoadera = youTubeThumbnailLoader;
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {

                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                    }
                });
                youTubeThumbnailLoadera.setPlaylist(arrayVideos.get(0).getUrl());

            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

            }

        }
    }

}