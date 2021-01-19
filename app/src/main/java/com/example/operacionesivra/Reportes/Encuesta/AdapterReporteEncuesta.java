package com.example.operacionesivra.Reportes.Encuesta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;

import java.util.List;


public class AdapterReporteEncuesta extends RecyclerView.Adapter<AdapterReporteEncuesta.ViewHolder> {
    public List<ModeloReporteEncuesta> reporteencuesta;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView r1,r2,r3,r4,r5cal1,r5cal2,r5cal3,r5cal4,r5cal5,r5cal6,r7,r8,r9,recomendacion,usuario,empresa,fecha;
        String idencuesta;

        //encargado de llenar la vista con los datod que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            r1=itemView.findViewById(R.id.r1_REI);
            r2=itemView.findViewById(R.id.r2_REI);
            r3=itemView.findViewById(R.id.r3_REI);
            r4=itemView.findViewById(R.id.r4_REI);
            r5cal1=itemView.findViewById(R.id.r5cal1_REI);
            r5cal2=itemView.findViewById(R.id.r5cal2_REI);
            r5cal3=itemView.findViewById(R.id.r5cal3_REI);
            r5cal4=itemView.findViewById(R.id.r5cal4_REI);
            r5cal5=itemView.findViewById(R.id.r5cal5_REI);
            r5cal6=itemView.findViewById(R.id.r5cal6_REI);
            r7=itemView.findViewById(R.id.r7_REI);
            r8=itemView.findViewById(R.id.r8_REI);
            r9=itemView.findViewById(R.id.r9_REI);
            usuario=itemView.findViewById(R.id.usuario_REI);
            empresa = itemView.findViewById(R.id.empresa_REI);
            fecha = itemView.findViewById(R.id.fecha_REI);
            recomendacion = itemView.findViewById(R.id.recomendacion_REI);
        }

    }

    public AdapterReporteEncuesta(List<ModeloReporteEncuesta> lista) {
        this.reporteencuesta = lista;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reportes_encuesta_item, parent, false);
       ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.r1.setText(reporteencuesta.get(position).getR1());
        holder.r2.setText(reporteencuesta.get(position).getR2());
        holder.r3.setText(reporteencuesta.get(position).getR3());
        holder.r4.setText(reporteencuesta.get(position).getR4());
        holder.r5cal1.setText(reporteencuesta.get(position).getR5cal1());
        holder.r5cal2.setText(reporteencuesta.get(position).getR5cal2());
        holder.r5cal3.setText(reporteencuesta.get(position).getR5cal3());
        holder.r5cal4.setText(reporteencuesta.get(position).getR5cal4());
        holder.r5cal5.setText(reporteencuesta.get(position).getR5cal5());
        holder.r5cal6.setText(reporteencuesta.get(position).getR5cal6());
        holder.r7.setText(reporteencuesta.get(position).getR7());
        holder.r8.setText(reporteencuesta.get(position).getR8());
        holder.r9.setText(reporteencuesta.get(position).getR9());
        holder.recomendacion.setText(reporteencuesta.get(position).getRecomendacion());
        holder.usuario.setText(reporteencuesta.get(position).getUsuario());
        holder.empresa.setText(reporteencuesta.get(position).getEmpresa());
        holder.fecha.setText(reporteencuesta.get(position).getFecha());
        holder.idencuesta = reporteencuesta.get(position).getIdencuesta();
    }

    @Override
    public int getItemCount() {
        return  reporteencuesta.size();
    }

}
