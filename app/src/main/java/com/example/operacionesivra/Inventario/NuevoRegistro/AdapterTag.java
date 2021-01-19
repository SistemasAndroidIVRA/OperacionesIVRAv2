package com.example.operacionesivra.Inventario.NuevoRegistro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;

import java.util.List;

public class AdapterTag extends RecyclerView.Adapter<AdapterTag.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView codigo, contenido, ubicacion;
        String asignada;

        //encargado de llenar la vista con los datod que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            codigo = itemView.findViewById(R.id.cantidaddemetros);
            contenido = itemView.findViewById(R.id.contenidotag);
            ubicacion = itemView.findViewById(R.id.ubicaciontag);

        }
    }

    public List<Modelotag> tags;

    public AdapterTag(List<Modelotag> lista) {
        this.tags = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventario_tag, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.codigo.setText(tags.get(position).getCodigo());
        holder.contenido.setText(tags.get(position).getContenido());
        holder.ubicacion.setText(tags.get(position).getUbicaión());
        holder.asignada = tags.get(position).getAsignada();
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }
}
