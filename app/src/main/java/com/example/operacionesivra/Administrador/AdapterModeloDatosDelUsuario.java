package com.example.operacionesivra.Administrador;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Administrador.Permisos.DetallesUsuario;
import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AdapterModeloDatosDelUsuario extends RecyclerView.Adapter<AdapterModeloDatosDelUsuario.ViewHolder> {

    public static class ViewHolder extends  RecyclerView.ViewHolder{
    public TextView nombre,usuario,area, ubicacion,nombrepermiso;
    public LinearLayout card;
    public Context context;
    public String idusuario;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombrecompletoa);
            usuario = itemView.findViewById(R.id.usuarioa);
            area = itemView.findViewById(R.id.areaa);
            ubicacion = itemView.findViewById(R.id.ubicaciona);
            card = itemView.findViewById(R.id.cardusuario);
            context = itemView.getContext();

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("id:"+ idusuario );
                    seleccionarusuario(idusuario);
                }
            });

            card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    eliminarusuario(idusuario);
                    return false;
                }
            });
        }

        public void seleccionarusuario(String id){
            Intent i = new Intent(context, DetallesUsuario.class);
            i.putExtra("idusuario",id);
            context.startActivity(i);
            if(context instanceof DetallesUsuario){
                ((DetallesUsuario)context).finish();
            }
        }

        public void eliminarusuario(final String idusuario){
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Confimación")
                    .setMessage("¿Está seguro que desea eliminar al usuario: " +nombre.getText())
                    .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(context instanceof Administrador){
                                ((Administrador)context).eliminarusuario(idusuario);
                            }
                        }
                    })
                    .setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }


    List<ModeloDatosdeUsuarioAdministracion> usuarios;

    public AdapterModeloDatosDelUsuario(List<ModeloDatosdeUsuarioAdministracion> usuarios){
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.administrador_lista_item,parent,false);
       ViewHolder viewHolder = new ViewHolder(view);
       return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombre.setText(usuarios.get(position).getNombre());
        holder.usuario.setText(usuarios.get(position).getUsuario());
        holder.area.setText(usuarios.get(position).getArea());
        holder.ubicacion.setText(usuarios.get(position).getUbicacion());
        holder.idusuario = usuarios.get(position).getIdusuario();
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }


}
