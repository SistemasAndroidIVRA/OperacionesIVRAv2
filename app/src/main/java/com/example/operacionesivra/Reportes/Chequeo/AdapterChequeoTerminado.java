package com.example.operacionesivra.Reportes.Chequeo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Reportes.Chequeo.TerminadosListaPedido.CrearReporte;
import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AdapterChequeoTerminado extends RecyclerView.Adapter<AdapterChequeoTerminado.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pedido, cliente, referencia, fecha;
        String serie, horainicio, horafin;
        CardView card;
        public Context contexto;
        Intent intent2;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            pedido = itemView.findViewById(R.id.pedidoChequeoTerminado);
            cliente = itemView.findViewById(R.id.clientechequeoTerminado);
            referencia = itemView.findViewById(R.id.referenciaChequeoTerminado);
            fecha = itemView.findViewById(R.id.fechaChequeoTerminado);
            card = itemView.findViewById(R.id.cardChequeoTerminado);
            contexto = itemView.getContext();
            intent2 = new Intent(contexto, CrearReporte.class);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemselecionadoreporte();
                }
            });


        }

        public void itemselecionadoreporte() {
            new MaterialAlertDialogBuilder(contexto)
                    .setCancelable(false)
                    .setTitle("Confirmación")
                    .setMessage("Comenzar revisión del pedido " + pedido.getText())
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            intent2.putExtra("pedidoChequeo", pedido.getText());
                            intent2.putExtra("serieChequeo", serie);
                            intent2.putExtra("clienteChequeo", cliente.getText());
                            intent2.putExtra("referenciaChequeo", referencia.getText());
                            intent2.putExtra("horainicioChequeo", horainicio);
                            System.out.println(horainicio);
                            intent2.putExtra("horafinChequeo", horafin);
                            contexto.startActivity(intent2);
                            if (contexto instanceof ListaChequeoTerminado) {
                                ((ListaChequeoTerminado) contexto).finish();

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

    }

    public List<ModeloChequeoterminado> listaChequeos;

    public AdapterChequeoTerminado(List<ModeloChequeoterminado> listaChequeos) {
        this.listaChequeos = listaChequeos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reportes_chequeo_lista_terminados_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.pedido.setText(listaChequeos.get(position).getPedido());
        holder.cliente.setText(listaChequeos.get(position).getCliente());
        holder.referencia.setText(listaChequeos.get(position).getReferencia());
        holder.fecha.setText(listaChequeos.get(position).getFecha());
        holder.serie = listaChequeos.get(position).getSerie();

        holder.horainicio = listaChequeos.get(position).getHorainicio();
        holder.horafin = listaChequeos.get(position).getHorafin();
    }

    @Override
    public int getItemCount() {
        return listaChequeos.size();
    }
}
