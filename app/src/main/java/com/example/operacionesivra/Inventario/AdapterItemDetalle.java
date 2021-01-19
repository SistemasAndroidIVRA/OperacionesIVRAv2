package com.example.operacionesivra.Inventario;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import java.util.List;
import java.util.UUID;

public class AdapterItemDetalle extends RecyclerView.Adapter<AdapterItemDetalle.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Context contexto;
        public TextView codigo, longitud, ubicacion, estado, itemselect,total;
        public CardView card;
        public ImageView imagen;
        public String folio;


        //encargado de llenar la vista con los datod que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            contexto = itemView.getContext();
            codigo = itemView.findViewById(R.id.codigodebarras);
            longitud = itemView.findViewById(R.id.longitud);
            ubicacion = itemView.findViewById(R.id.ubicacion);
            estado = itemView.findViewById(R.id.estado);
            card = itemView.findViewById(R.id.carditemdetalle);
            imagen = itemView.findViewById(R.id.estadoimagen);
            itemselect = itemView.findViewById(R.id.itemselect);
            total = itemView.findViewById(R.id.cantidadtotal);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(contexto instanceof Inventario){
                        v.startAnimation(AnimationUtils.loadAnimation(contexto,R.anim.fragment_open_enter));
                        ((Inventario) contexto).editarregistro( codigo.getText().toString(), longitud.getText().toString(),ubicacion.getText().toString(), total.getText().toString(),folio);
                    }
                }
            });
        }
    }


    public List<ModeloDetallesItem> detallesItem;

    public AdapterItemDetalle(List<ModeloDetallesItem> lista) {
        this.detallesItem = lista;
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
        holder.codigo.setText(detallesItem.get(position).getCantidad());
        holder.longitud.setText(detallesItem.get(position).getLongitud());
        holder.ubicacion.setText(detallesItem.get(position).getUbicacion());
        holder.estado.setText(detallesItem.get(position).getEstado());
        holder.imagen.setImageResource(detallesItem.get(position).getImagen());
        holder.total.setText(detallesItem.get(position).getMaterialregistrado());
        holder.folio = detallesItem.get(position).getFolioadd();
    }


    @Override
    public int getItemCount() {
        return detallesItem.size();
    }
}
