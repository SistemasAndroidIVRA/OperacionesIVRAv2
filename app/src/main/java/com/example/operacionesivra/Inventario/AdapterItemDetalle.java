package com.example.operacionesivra.Inventario;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import java.util.List;
import java.util.UUID;

public class AdapterItemDetalle extends RecyclerView.Adapter<AdapterItemDetalle.ViewHolder> {

    //Listas
    public static List<ModeloDetallesItem> detallesItem;

    public AdapterItemDetalle(List<ModeloDetallesItem> lista) {
        this.detallesItem = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Context contexto;
        public TextView codigo, longitud, ubicacion, estado, itemselect, total, incidenciatxt;
        public CardView card;
        public ImageView imagen;
        public String folio, incidencia;
        public LinearLayout linearRegistrosInventario;


        //encargado de llenar la vista con los datos que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            contexto = itemView.getContext();
            codigo = itemView.findViewById(R.id.codigodebarras);
            longitud = itemView.findViewById(R.id.longitud);
            ubicacion = itemView.findViewById(R.id.ubicacion);
            card = itemView.findViewById(R.id.carditemdetalle);
            imagen = itemView.findViewById(R.id.estadoimagen);
            total = itemView.findViewById(R.id.cantidadtotal);
            incidenciatxt = itemView.findViewById(R.id.incidencia);
            linearRegistrosInventario = itemView.findViewById(R.id.linearRegistrosInventario);

            card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(detallesItem.get(getAdapterPosition()).getIsSelected() == 0){
                        detallesItem.get(getAdapterPosition()).setIsSelected(1);
                    }else{
                        detallesItem.get(getAdapterPosition()).setIsSelected(0);
                    }
                    ((Inventario) contexto).adaptador.notifyDataSetChanged();
                    return false;
                }
            });
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Confirmación!")
                            .setCancelable(false)
                            .setIcon(R.drawable.confirmacion)
                            .setMessage("¿Desea editar el registro?")
                            .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //No hacer nada
                                }
                            })
                            .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //
                                    if (contexto instanceof Inventario) {
                                        v.startAnimation(AnimationUtils.loadAnimation(contexto, R.anim.fragment_open_enter));
                                        ((Inventario) contexto).editarregistro(codigo.getText().toString(), longitud.getText().toString(), ubicacion.getText().toString(), total.getText().toString(), folio, incidencia);
                                    }
                                }
                            })
                            .show();
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventario_codigo_escaneado, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(detallesItem.get(position).getIsSelected() == 1){
            holder.linearRegistrosInventario.setBackgroundColor(Color.parseColor("#FF9A9A"));
        }else{
            holder.linearRegistrosInventario.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.codigo.setText(detallesItem.get(position).getCantidad());
        holder.longitud.setText(detallesItem.get(position).getLongitud());
        holder.ubicacion.setText(detallesItem.get(position).getUbicacion());
        String incidencia = detallesItem.get(position).getIncidencia().toString();
        holder.incidenciatxt.setText(detallesItem.get(position).getIncidencia());
        if(incidencia.equals("")){
            holder.imagen.setImageResource(detallesItem.get(position).getImagen());
        }else if(!incidencia.equals("")){
            holder.imagen.setImageResource(R.drawable.confirmacion);
        }
        holder.total.setText(detallesItem.get(position).getMaterialregistrado());
        holder.folio = detallesItem.get(position).getFolioadd();
        holder.incidencia = detallesItem.get(position).getIncidencia();
    }


    @Override
    public int getItemCount() {
        return detallesItem.size();
    }
}

