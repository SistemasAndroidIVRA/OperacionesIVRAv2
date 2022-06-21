package com.example.operacionesivra.Vistas.PantallaRecepcion.administrar;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Vistas.PantallaRecepcion.ModeloVideos;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class RecepcionAdministrarVideos extends AppCompatActivity {
    Context contexto = this;
    Conexion conexion = new Conexion(contexto);
    Button btnVAdminAgregar, btnVARegresar;
    RecyclerView recyclervAdmin;
    ArrayList<ModeloVideos> videos = new ArrayList<>();
    AdapterVideosA adapterVideosA = new AdapterVideosA();
    //Dialog referencias
    EditText txtNListaNombre, txtNListaURL;
    Button btnCerrarLista, btnGuardarLista, btnVAdminEliminar;

    public static final String APY_KEY = "AIzaSyBz6_qZa1BXC7xl8Rn8NnOipqxrpMwunBM";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recepcion_administrar_videos);
        btnVAdminAgregar = findViewById(R.id.btnVAdminAgregar);
        btnVARegresar = findViewById(R.id.btnVARegresar);
        btnVAdminEliminar = findViewById(R.id.btnVAdminEliminar);
        recyclervAdmin = findViewById(R.id.recyclervAdmin);
        //Llenar adapter
        videos = getVideos();
        recyclervAdmin.setLayoutManager(new LinearLayoutManager(contexto));

        recyclervAdmin.setAdapter(adapterVideosA);
        Toast.makeText(contexto, "hOLA "+videos.size(), Toast.LENGTH_SHORT).show();
        //Acciones
        btnVARegresar.setOnClickListener(view -> {
            finish();
        });
        btnVAdminAgregar.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
            LayoutInflater inflater = getLayoutInflater();
            View view1 = inflater.inflate(R.layout.recepcion_administrador_videos_nuevo, null);
            alert.setView(view1);
            AlertDialog dialog = alert.create();
            dialog.show();
            //Referenciar elementos
            txtNListaNombre = view1.findViewById(R.id.txtNListaNombre);
            txtNListaURL = view1.findViewById(R.id.txtNListaURL);
            //Buttons
            btnCerrarLista = view1.findViewById(R.id.btnCerrarLista);
            btnGuardarLista = view1.findViewById(R.id.btnGuardarLista);
            //Accdiones botones
            btnCerrarLista.setOnClickListener(view2 -> {
                dialog.dismiss();
            });
            btnGuardarLista.setOnClickListener(view2 -> {
                if(saveVideo() == 1){
                    dialog.dismiss();
                }
            });
        });
        btnVAdminEliminar.setOnClickListener(view -> {
            deleteVideos();
        });
    }


    //Método para guardar un video
    public int saveVideo(){
        if(txtNListaNombre.getText().toString().equals("") || txtNListaURL.getText().toString().equals("")){
            Toast.makeText(contexto, "Llene la información correctamente, error.", Toast.LENGTH_SHORT).show();
            return 0;
        }else{
            ModeloVideos video = new ModeloVideos(txtNListaNombre.getText().toString(), txtNListaURL.getText().toString(), 0);
            try {
                PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("PMovil_Videos_INSERT ?,?");
                stmt.setString(1, video.getNombre());
                stmt.setString(2, video.getUrl());
                stmt.execute();
                videos.add(0, video);
                adapterVideosA.notifyDataSetChanged();
                new MaterialAlertDialogBuilder(contexto)
                        .setTitle("¡Éxito!")
                        .setIcon(R.drawable.correcto)
                        .setMessage("Se ha agregado exitosamente.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //No hacer nada
                            }
                        })
                        .show();
                return 1;
            }catch (Exception e){
                new MaterialAlertDialogBuilder(contexto)
                        .setTitle("¡Error!")
                        .setIcon(R.drawable.snakerojo)
                        .setMessage("Error: "+e.getMessage())
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //No hacer nada
                            }
                        })
                        .show();
                return 0;
            }
        }
    }

    //Método para eliminar videos
    public void deleteVideos(){
        try {
        ArrayList<ModeloVideos> array = new ArrayList<>();
        int hayEliminar = 0;
            for(int i=0; i<videos.size(); i++){
                if(videos.get(i).getIsSelected() == 1){
                    PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("PMovil_Videos_DELETE ?");
                    stmt.setString(1, videos.get(i).getVideoID());
                    stmt.execute();
                    hayEliminar = 1;
                }else{
                    array.add(videos.get(i));
                }
            }
            if(hayEliminar == 1){
                videos = array;
                adapterVideosA.notifyDataSetChanged();
                Toast.makeText(contexto, "Se han elimiando exitosamente.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(contexto, "No hay nada que eliminar.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Traer videos
    public ArrayList<ModeloVideos> getVideos(){
        try {
            ArrayList<ModeloVideos> array = new ArrayList<>();
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("PMovil_Recepcion_Videos_SELECT");
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                array.add(new ModeloVideos(r.getString("VideoID"), r.getString("Nombre"), r.getString("URL"), 0));
            }
            return array;
        }catch (Exception e){
            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return new ArrayList<ModeloVideos>();
        }
    }

    public class AdapterVideosA extends RecyclerView.Adapter<AdapterVideosA.AdapterVideosAHolder>{
        @NonNull
        @Override
        public AdapterVideosAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterVideosAHolder(getLayoutInflater().inflate(R.layout.recepcion_administrador_videos_item, parent, false));
        }

        @Override
        public void onBindViewHolder(AdapterVideosAHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return videos.size();
        }

        class AdapterVideosAHolder extends RecyclerView.ViewHolder implements View.OnClickListener, YouTubeThumbnailView.OnInitializedListener {
            TextView lblAVConsItem, lblAVNombreItem, lblAVURLItem;
            CheckBox cbAVEliminar;
            CardView cardVideo;

            YouTubeThumbnailView youTubeThumbnailView;
            YouTubeThumbnailLoader youTubeThumbnailLoadera;
            public AdapterVideosAHolder(@NonNull View itemView){
                super(itemView);
                lblAVConsItem = itemView.findViewById(R.id.lblAVConsItem);
                lblAVNombreItem = itemView.findViewById(R.id.lblAVNombreItem);
                lblAVURLItem = itemView.findViewById(R.id.lblAVURLItem);
                cbAVEliminar = itemView.findViewById(R.id.cbAVEliminar);
                cardVideo = itemView.findViewById(R.id.cardVideo);

                youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtubethumbnailviewAdmin);
                youTubeThumbnailView.initialize(APY_KEY, this);


                //Acciones
                cbAVEliminar.setOnClickListener(view -> {
                    if(cbAVEliminar.isChecked()){
                        videos.get(getAdapterPosition()).setIsSelected(1);
                    }else{
                        videos.get(getAdapterPosition()).setIsSelected(0);
                    }
                    adapterVideosA.notifyDataSetChanged();
                });
                cardVideo.setOnClickListener(view -> {
                    AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
                    LayoutInflater inflater = getLayoutInflater();
                    View view1 = inflater.inflate(R.layout.recepcion_administrador_videos_nuevo, null);
                    alert.setView(view1);
                    AlertDialog dialog = alert.create();
                    dialog.show();
                    //Referenciar elementos
                    txtNListaNombre = view1.findViewById(R.id.txtNListaNombre);
                    txtNListaURL = view1.findViewById(R.id.txtNListaURL);
                    //Buttons
                    btnCerrarLista = view1.findViewById(R.id.btnCerrarLista);
                    btnGuardarLista = view1.findViewById(R.id.btnGuardarLista);
                    //Adignar valores
                    String id = videos.get(getAdapterPosition()).getVideoID();
                    txtNListaNombre.setText(videos.get(getAdapterPosition()).getNombre());
                    txtNListaURL.setText(videos.get(getAdapterPosition()).getUrl());
                    //Accdiones botones
                    btnCerrarLista.setOnClickListener(view2 -> {
                        dialog.dismiss();
                    });
                    btnGuardarLista.setOnClickListener(view2 -> {
                        if(txtNListaNombre.getText().toString().equals("") || txtNListaURL.getText().toString().equals("")){
                            Toast.makeText(contexto, "Complete la información correctamente, error.", Toast.LENGTH_SHORT).show();
                        }else{
                            try {
                                PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("PMovil_Videos_UPDATE ?,?,?");
                                stmt.setString(1, txtNListaNombre.getText().toString());
                                stmt.setString(2, txtNListaURL.getText().toString());
                                stmt.setString(3, id);
                                stmt.execute();
                                videos.get(getAdapterPosition()).setNombre(txtNListaNombre.getText().toString());
                                videos.get(getAdapterPosition()).setUrl(txtNListaURL.getText().toString());
                                adapterVideosA.notifyDataSetChanged();
                                new MaterialAlertDialogBuilder(contexto)
                                        .setTitle("¡Éxito!")
                                        .setIcon(R.drawable.correcto)
                                        .setMessage("Se ha actualizado el registro exitosamente.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //No hacer nada
                                            }
                                        })
                                        .show();
                                dialog.dismiss();
                            }catch (Exception e){
                                new MaterialAlertDialogBuilder(contexto)
                                        .setTitle("¡Error!")
                                        .setIcon(R.drawable.snakerojo)
                                        .setMessage("Error: "+e.getMessage())
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
                });
            }

            public void printAdapter(int position){
                setYouTubeThumbnailView(position);
                lblAVConsItem.setText(""+(position+1));
                lblAVNombreItem.setText(videos.get(position).getNombre());
                lblAVURLItem.setText(videos.get(position).getUrl());

                if(videos.get(position).getIsSelected() == 0){
                    cbAVEliminar.setChecked(false);
                }else{
                    cbAVEliminar.setChecked(true);
                }
            }

            @Override
            public void onClick(View view){

            }

            public void setYouTubeThumbnailView(int position){
                if(youTubeThumbnailLoadera != null){
                    youTubeThumbnailLoadera.setPlaylist(videos.get(position).getUrl());
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
                        Toast.makeText(contexto, "Error",Toast.LENGTH_SHORT).show();
                    }
                });
               // youTubeThumbnailLoadera.setPlaylist(videos.get(0).getUrl());
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

            }
        }
    }
}