package com.example.operacionesivra.Inventario.InventariosHistoricos;

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

import com.example.operacionesivra.Inventario.InventariosHistoricos.DetallesHistoricos.Detalles_Historicos;
import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AdapterHistoricoInventarios extends RecyclerView.Adapter<AdapterHistoricoInventarios.ViewHolder>{
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView lblFecha, lblUsuario, lblAlmacen, lblMaterial, lblTotalRegistro, lblDiferencia, lblHoraInicio, lblHoraFin, lblEntradas;
        Context contexto;
        String folio;
        CardView cardHistoricos;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            contexto = itemView.getContext();
            lblFecha = itemView.findViewById(R.id.lblFecha);
            lblUsuario = itemView.findViewById(R.id.lblUsuario);
            lblAlmacen = itemView.findViewById(R.id.lblAlmacen);
            lblEntradas  = itemView.findViewById(R.id.lblEntradas);
            lblMaterial = itemView.findViewById(R.id.lblMaterial);
            lblTotalRegistro = itemView.findViewById(R.id.lblTotalRegistrado);
            lblDiferencia = itemView.findViewById(R.id.lblDiferencia);
            lblHoraInicio = itemView.findViewById(R.id.lblHoraInicio);
            lblHoraFin = itemView.findViewById(R.id.lblHoraFin);
            cardHistoricos = (CardView) itemView.findViewById(R.id.cardHistoricos);
            cardHistoricos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("Confirmación")
                            .setIcon(R.drawable.confirmacion)
                            .setMessage("Seguro que desea confirmar")
                            .setCancelable(false)
                            .setPositiveButton("Ver detalles", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        Intent intent = new Intent(contexto, Detalles_Historicos.class);
                                        intent.putExtra("Folio", folio);
                                        contexto.startActivity(intent);
                                    }catch (Exception e){
                                        new MaterialAlertDialogBuilder(contexto)
                                                .setTitle("Erros")
                                                .setIcon(R.drawable.snakerojo)
                                                .setMessage("Por favor revice con su administrador el siguiente error: "+e.getMessage())
                                                .show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //NO hace nada
                                }
                            })
                            .show();
                }
            });
        }
    }

    public List<ModeloInventariosHistoricos> historicos;

    public AdapterHistoricoInventarios(List<ModeloInventariosHistoricos> lista){
        this.historicos = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventario_inventarioshistoricos_items, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistoricoInventarios.ViewHolder holder, int position) {
        holder.folio = historicos.get(position).getFolio();
        holder.lblFecha.setText(historicos.get(position).getFecha());
        holder.lblUsuario.setText(historicos.get(position).getUsuario());
        holder.lblAlmacen.setText(historicos.get(position).getAlmacen());
        holder.lblEntradas.setText(historicos.get(position).getEntradas());
        holder.lblMaterial.setText(historicos.get(position).getMaterial());
        holder.lblTotalRegistro.setText(historicos.get(position).getTotalRegistrado());
        holder.lblDiferencia.setText(historicos.get(position).getDiferencia());
        holder.lblHoraInicio.setText(historicos.get(position).getHoraInicio());
        holder.lblHoraFin.setText(historicos.get(position).getHoraFin());
        float diferenciah = Float.parseFloat(holder.lblDiferencia.getText().toString());
        if(diferenciah < 0){
            holder.lblDiferencia.setText("-" + holder.lblDiferencia.getText().toString());
            holder.lblDiferencia.setTextColor(Color.parseColor("#DD2C2B"));
        }else{
            holder.lblDiferencia.setText(holder.lblDiferencia.getText().toString().replace("-", "+"));
            holder.lblDiferencia.setTextColor(Color.parseColor("#4AA10D"));
        }
        if(diferenciah == 0){
            holder.lblDiferencia.setText(holder.lblDiferencia.getText().toString().replace("-", ""));
            holder.lblDiferencia.setTextColor(Color.parseColor("#1C9ACC"));
        }
    }

    @Override
    public int getItemCount() {
        return historicos.size();
    }
}
