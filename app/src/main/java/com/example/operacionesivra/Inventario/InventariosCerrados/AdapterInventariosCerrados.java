package com.example.operacionesivra.Inventario.InventariosCerrados;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Inventario.Inventario;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Reportes.Inventario.ReporteInventarioTerminado;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AdapterInventariosCerrados extends RecyclerView.Adapter<AdapterInventariosCerrados.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView material,fecha,fisico,sistema,diferencia;
        String folio,almacen, usuario;
        CardView card;
        Context contexto;

        //encargado de llenar la vista con los datod que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            contexto = itemView.getContext();
            material = itemView.findViewById(R.id.material_ic);
            fecha = itemView.findViewById(R.id.fecha_ic);
            card = itemView.findViewById(R.id.card_ic);
            fisico = itemView.findViewById(R.id.fisico_ic);
            sistema = itemView.findViewById(R.id.sistema_ic);
            diferencia = itemView.findViewById(R.id.diferencia_ic);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("Confirmación")
                            .setMessage("Seleccionaste el inventario del material "+material.getText().toString().toLowerCase()+" con fecha del "+fecha.getText().toString()+"\n¿Que quieres hacer?")
                            .setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(contexto, Inventario.class);
                                    intent.putExtra("folio",folio);
                                    intent.putExtra("usuario",usuario);
                                    intent.putExtra("almacen",almacen);
                                    intent.putExtra("opcion",1);
                                    contexto.startActivity(intent);

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


    public List<ModeloInventariosCerrados> inventarioscerrados;

    public AdapterInventariosCerrados(List<ModeloInventariosCerrados> lista) {
        this.inventarioscerrados = lista;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventario_inventarioscerrados_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.usuario = inventarioscerrados.get(position).getUsuario();
        holder.fecha.setText(inventarioscerrados.get(position).getFecha());
        holder.material.setText(inventarioscerrados.get(position).getMaterial());
        holder.folio = inventarioscerrados.get(position).getFolio();
        holder.almacen = inventarioscerrados.get(position).getAlmacen();
        holder.fisico.setText(inventarioscerrados.get(position).getFisico());
        holder.sistema.setText(inventarioscerrados.get(position).getSistema());
        holder.diferencia.setText(inventarioscerrados.get(position).getDiferencia());
        float diferenciat = Float.parseFloat(holder.diferencia.getText().toString());
        if(diferenciat>=0){
            holder.diferencia.setText("- "+holder.diferencia.getText().toString());
            holder.diferencia.setTextColor(Color.parseColor("#DD2C2B"));
        }
        else{
            holder.diferencia.setText(holder.diferencia.getText().toString().replace("-","+ "));
            holder.diferencia.setTextColor(Color.parseColor("#4AA10D"));
        }
        if(diferenciat==0){
            holder.diferencia.setText(holder.diferencia.getText().toString().replace("-",""));
            holder.diferencia.setTextColor(Color.parseColor("#1C9ACC"));
        }

    }


    @Override
    public int getItemCount() {
        return inventarioscerrados.size();
    }
}
