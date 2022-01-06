package com.example.operacionesivra.Reportes.Inventario.InventarioActual;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Reportes.Inventario.ReporteInventarioTerminado;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class AdapterInventarioActual extends RecyclerView.Adapter<AdapterInventarioActual.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fisico,disponible,comprometido,almacen;
        LinearLayout inventario;
        Context context;

        //encargado de llenar la vista con los datos que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            context = itemView.getContext();
            almacen = itemView.findViewById(R.id.almacen_ia);
            fisico = itemView.findViewById(R.id.fisico_ai);
            disponible = itemView.findViewById(R.id.disponible_ia);
            comprometido = itemView.findViewById(R.id.comprometido_ia);

            inventario = itemView.findViewById(R.id.inventarioactual_item);

            inventario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof InventarioActual){
                        ((InventarioActual)context).respuesta2(almacen.getText().toString());
                        ((InventarioActual)context).barChart.setVisibility(View.VISIBLE);
                        v.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fragment_open_enter));
                    }
                }
            });

        }
    }

    public List<ModeloInventarioActual> inventarioscerrados;

    public  AdapterInventarioActual (List<ModeloInventarioActual> lista) {
        this.inventarioscerrados = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reportes_inventario_actual_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterInventarioActual.ViewHolder holder, int position) {
        holder.almacen.setText(inventarioscerrados.get(position).getAlmacen());
        holder.comprometido.setText(inventarioscerrados.get(position).getComprometido());
        holder.fisico.setText(inventarioscerrados.get(position).getFisico());
        holder.disponible.setText(inventarioscerrados.get(position).getDisponible());
    }



    @Override
    public int getItemCount() {
        return inventarioscerrados.size();
    }
}
