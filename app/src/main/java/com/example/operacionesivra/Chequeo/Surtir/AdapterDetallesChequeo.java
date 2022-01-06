package com.example.operacionesivra.Chequeo.Surtir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;

import java.util.List;

public class AdapterDetallesChequeo extends RecyclerView.Adapter<AdapterDetallesChequeo.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView material, cantidad, unidad;
        ImageView estado;
        boolean correcto;
        public Context contexto;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            material = itemView.findViewById(R.id.materialchequeod);
            cantidad = itemView.findViewById(R.id.cantidadchequeod);
            unidad = itemView.findViewById(R.id.unidadchequeod);
            estado = itemView.findViewById(R.id.estadochequeod);
            contexto = itemView.getContext();
        }

    }

    public List<ModeloDetallesChequeo> itemsChequeos;

    public AdapterDetallesChequeo(List<ModeloDetallesChequeo> listaChequeos) {
        this.itemsChequeos = listaChequeos;
    }

    @NonNull
    @Override
    public AdapterDetallesChequeo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chequeo_detalles_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDetallesChequeo.ViewHolder holder, int position) {
        holder.material.setText(itemsChequeos.get(position).getMaterial());
        holder.cantidad.setText(itemsChequeos.get(position).getCantidad());
        holder.unidad.setText(itemsChequeos.get(position).getUnidad());
        holder.estado.setImageResource(itemsChequeos.get(position).getEstado());
        holder.correcto = itemsChequeos.get(position).getCorrecto();
    }

    @Override
    public int getItemCount() {
        return itemsChequeos.size();
    }
}
