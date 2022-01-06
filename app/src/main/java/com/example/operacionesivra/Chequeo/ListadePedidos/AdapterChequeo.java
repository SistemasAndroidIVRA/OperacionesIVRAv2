package com.example.operacionesivra.Chequeo.ListadePedidos;

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

import com.example.operacionesivra.Chequeo.Surtir.DetallesChequeo;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Reportes.Chequeo.TerminadosListaPedido.CrearReporte;
import com.example.operacionesivra.Reportes.Chequeo.ListaChequeoTerminado;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AdapterChequeo extends RecyclerView.Adapter<AdapterChequeo.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pedido, cliente, referencia, ruta, fecha, estado;
        String serie;
        CardView card;
        public Context contexto;
        Intent intent, intent2;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            pedido = itemView.findViewById(R.id.pedidoChequeo);
            cliente = itemView.findViewById(R.id.clientechequeo);
            referencia = itemView.findViewById(R.id.referenciaChequeo);
            ruta = itemView.findViewById(R.id.rutaChequeo);
            fecha = itemView.findViewById(R.id.fechaChequeo);
            estado = itemView.findViewById(R.id.estadoChequeo);
            card = itemView.findViewById(R.id.cardChequeo);
            contexto = itemView.getContext();
            intent = new Intent(contexto, DetallesChequeo.class);
            intent2 = new Intent(contexto, CrearReporte.class);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contexto instanceof ListadeChequeo) {
                        itemselecionado();
                    } else {
                        itemselecionadoreporte();
                    }
                }
            });


        }

        //Confirma y envia las credenciales necesarias para el chequeo
        public void itemselecionado() {
            new MaterialAlertDialogBuilder(contexto)
                    .setCancelable(false)
                    .setTitle("Confirmación")
                    .setMessage("Comenzar revisión del pedido " + pedido.getText())
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            intent.putExtra("pedidoChequeo", pedido.getText());
                            intent.putExtra("serieChequeo", serie);
                            intent.putExtra("clienteChequeo", cliente.getText());
                            intent.putExtra("referenciaChequeo", referencia.getText());
                            contexto.startActivity(intent);
                            if (contexto instanceof ListadeChequeo) {
                                ((ListadeChequeo) contexto).finish();
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

        //Muestra mensaje para el inicio del reporte
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

    public List<ModeloListaChequeo> listaChequeos;

    public AdapterChequeo(List<ModeloListaChequeo> listaChequeos) {
        this.listaChequeos = listaChequeos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chequeo_lista_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.pedido.setText(listaChequeos.get(position).getPedido());
        holder.cliente.setText(listaChequeos.get(position).getCliente());
        holder.referencia.setText(listaChequeos.get(position).getReferencia());
        holder.ruta.setText(listaChequeos.get(position).getRuta());
        holder.fecha.setText(listaChequeos.get(position).getFecha());
        holder.estado.setText(listaChequeos.get(position).getEstado());
        holder.serie = listaChequeos.get(position).getSerie();
    }

    @Override
    public int getItemCount() {
        return listaChequeos.size();
    }
}
