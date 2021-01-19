package com.example.operacionesivra.Picking.ListapedidosPicking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Picking.SuirtirPicking.SurtirPicking;
import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AdapterListaPedidos extends RecyclerView.Adapter<AdapterListaPedidos.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Context contexto;
        public TextView referencia, cliente, fecha, numerodepedido, hora, cantidad;
        public String serie;
        CardView card;
        Intent intent;

        //encargado de llenar la vista con los datod que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            contexto = itemView.getContext();
            intent = new Intent(contexto, SurtirPicking.class);
            referencia = itemView.findViewById(R.id.referencia);
            cliente = itemView.findViewById(R.id.cliente);
            fecha = itemView.findViewById(R.id.fechaitemprioridades);
            numerodepedido = itemView.findViewById(R.id.numerodepedido);
            cantidad = itemView.findViewById(R.id.cantidadtotal);
            hora = itemView.findViewById(R.id.hora);
            card = itemView.findViewById(R.id.carditem);


            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(contexto, R.anim.fragment_fade_enter));
                    card.setOnClickListener(this);
                    confirmacion();
                }
            });
        }

        public void confirmacion() {
            new MaterialAlertDialogBuilder(contexto, R.style.MaterialAlertDialog_MaterialComponents_Title_Icon)
                    .setCancelable(false)
                    .setTitle("Elemento Seleccionado")
                    .setMessage("¿Surtir el pedido " + numerodepedido.getText() + " Ahora?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            intent.putExtra("id", numerodepedido.getText());
                            intent.putExtra("serie", serie);
                            intent.putExtra("cliente", cliente.getText().toString());
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
    }


    public List<ModeloListaPedido> listaPedidos;

    public AdapterListaPedidos(List<ModeloListaPedido> pedidolista) {
        this.listaPedidos = pedidolista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picking_listapedido, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.referencia.setText(listaPedidos.get(position).getReferencia());
        holder.cliente.setText(listaPedidos.get(position).getCliente());
        holder.fecha.setText(listaPedidos.get(position).getFecha());
        holder.numerodepedido.setText(listaPedidos.get(position).getNumerodepedido());
        holder.hora.setText(listaPedidos.get(position).getHora());
        holder.cantidad.setText(listaPedidos.get(position).getCantidad());
        holder.serie = listaPedidos.get(position).getSerie();
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }
}
