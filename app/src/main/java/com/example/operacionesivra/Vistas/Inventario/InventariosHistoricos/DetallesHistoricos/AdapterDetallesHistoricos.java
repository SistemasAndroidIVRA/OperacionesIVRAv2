package com.example.operacionesivra.Vistas.Inventario.InventariosHistoricos.DetallesHistoricos;

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

public class AdapterDetallesHistoricos extends RecyclerView.Adapter<AdapterDetallesHistoricos.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView lblCantidad, lblContenido, lblContenidoTotal, lblUbicacion, lblIncidencia;
        ImageView imgItem;
        Context contexto;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            contexto = itemView.getContext();
            lblCantidad = itemView.findViewById(R.id.lblCantidad);
            lblContenido = itemView.findViewById(R.id.lblContenido);
            lblContenidoTotal = itemView.findViewById(R.id.lblContenidoTotal);
            lblUbicacion = itemView.findViewById(R.id.lblUbicacion);
            lblIncidencia = itemView.findViewById(R.id.lblIncidencia);
            imgItem = itemView.findViewById(R.id.imgItem);
        }
    }

    public List<ModeloDetallesHistoricoItem> DetallesGistoricos;

    public AdapterDetallesHistoricos (List<ModeloDetallesHistoricoItem> lista){
        this.DetallesGistoricos = lista;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventarios_historicos_detalles_items, parent, false);
        ViewHolder holderView = new ViewHolder(view);
        return holderView;
    }

    //Asignación de valores a cada elemento del rexycler
    @Override
    public void onBindViewHolder(@NonNull AdapterDetallesHistoricos.ViewHolder holder, int position) {
        holder.lblCantidad.setText(DetallesGistoricos.get(position).getCantidad());
        holder.lblContenido.setText(DetallesGistoricos.get(position).getLongitud());
        holder.lblUbicacion.setText(DetallesGistoricos.get(position).getUbicacion());
        holder.lblContenidoTotal.setText(DetallesGistoricos.get(position).getMaterialregistrado());
        holder.lblIncidencia.setText(DetallesGistoricos.get(position).getIncidencias());
        if(DetallesGistoricos.get(position).getIncidencias().equals("")){
            holder.imgItem.setImageResource(R.drawable.correcto);
        }else if(!DetallesGistoricos.get(position).getIncidencias().equals("")){
            holder.imgItem.setImageResource(R.drawable.confirmacion);
        }
    }

    @Override
    public int getItemCount() {
        return DetallesGistoricos.size();
    }
}
