package com.example.operacionesivra.Picking.SuirtirPicking;


import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AdapterContenidodelPedido extends RecyclerView.Adapter<AdapterContenidodelPedido.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Context contexto;
        public TextView cantidaddelpedido, nombreproducto, cantidadregistrada, estado;
        public String codpedido, coditem;
        ImageView imagen;
        CardView card;


        //encargado de llenar la vista con los datod que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            contexto = itemView.getContext();
            cantidaddelpedido = itemView.findViewById(R.id.cantidadsolicitada);
            nombreproducto = itemView.findViewById(R.id.nombredelmaterial);
            cantidadregistrada = itemView.findViewById(R.id.cantidadregistrada);
            estado = itemView.findViewById(R.id.estado);
            card = itemView.findViewById(R.id.carditempedido);
            imagen = itemView.findViewById(R.id.estadoimagen_picking);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("Editar")
                            .setMessage("¿Modificar entrada?")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (contexto instanceof SurtirPicking) {
                                        v.startAnimation(AnimationUtils.loadAnimation(contexto, R.anim.fragment_open_enter));
                                        ((SurtirPicking) contexto).editardialog(nombreproducto.getText().toString(), cantidadregistrada.getText().toString());
                                    }
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

    }


    public List<ModeloContenidodelPedido> contenidodelPedido;

    public AdapterContenidodelPedido(List<ModeloContenidodelPedido> pedidolista) {
        this.contenidodelPedido = pedidolista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picking_listaitempedido, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.nombreproducto.setText(contenidodelPedido.get(position).getNombredelmaterial());
        holder.cantidaddelpedido.setText(contenidodelPedido.get(position).getCantidadsolicitada());
        holder.cantidadregistrada.setText(contenidodelPedido.get(position).getCantidad());
        holder.estado.setText(contenidodelPedido.get(position).getEstado());
        holder.codpedido = contenidodelPedido.get(position).getCodpedido();
        holder.imagen.setImageResource(contenidodelPedido.get(position).getImagen());
        holder.coditem = contenidodelPedido.get(position).getCoditem();
    }


    @Override
    public int getItemCount() {
        return contenidodelPedido.size();
    }
}

