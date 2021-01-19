package com.example.operacionesivra.PantallaRecepcion;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;

import java.util.List;

import harmony.java.awt.Color;


public class AdapterRecepcion extends RecyclerView.Adapter<AdapterRecepcion.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder {
    public String estado,pedido;
    public TextView cliente;
    public ImageView f1,f2,f3,f4,f5;
    public ProgressBar p1,p2,p3,p4;
        @RequiresApi(api = Build.VERSION_CODES.N)
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cliente = itemView.findViewById(R.id.clienterecepcion);
            f1 = itemView.findViewById(R.id.registrado);
            p1 = itemView.findViewById(R.id.progressBarregistrado);
            f2 = itemView.findViewById(R.id.aprobado);
            p2 = itemView.findViewById(R.id.progressBaraprobado);
            f3 = itemView.findViewById(R.id.revisado);
            p3 = itemView.findViewById(R.id.progressBarrevisado);
            f4 = itemView.findViewById(R.id.surtido);
            p4 = itemView.findViewById(R.id.progressBarsurtido);
            f5 = itemView.findViewById(R.id.nota);
            cliente.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    llenar();
                }
            });
        }


        @RequiresApi(api = Build.VERSION_CODES.N)
        public void llenar(){
            switch (estado){
                case "1":
                    f1.setImageResource(R.drawable.vpedidonuevo);
                    p1.setProgress(100,true);
                    f2.setImageResource(R.drawable.play);
                    p2.setProgress(0,true);
                    f3.setImageResource(R.drawable.pedidorevisado);
                    p3.setProgress(0,true);
                    f4.setImageResource(R.drawable.surtiendo);
                    p4.setProgress(0,true);
                    f5.setImageResource(R.drawable.generarnota);
                    break;
                case "2":
                    f1.setImageResource(R.drawable.vpedidonuevo);
                    p1.setProgress(100,true);
                    f2.setImageResource(R.drawable.vpedidoaprobado);
                    p2.setProgress(100,true);
                    f3.setImageResource(R.drawable.play);
                    p3.setProgress(0,true);
                    f4.setImageResource(R.drawable.surtiendo);
                    p4.setProgress(0,true);
                    f5.setImageResource(R.drawable.generarnota);
                    break;
                case "3":
                    f1.setImageResource(R.drawable.vpedidonuevo);
                    p1.setProgress(100,true);
                    f2.setImageResource(R.drawable.vpedidoaprobado);
                    p2.setProgress(100,true);
                    f3.setImageResource(R.drawable.vpedidorevisado);
                    p3.setProgress(100,true);
                    f4.setImageResource(R.drawable.play);
                    p4.setProgress(0,true);
                    f5.setImageResource(R.drawable.generarnota);
                    break;
                case "4":
                    p1.setProgress(100,true);
                    f1.setImageResource(R.drawable.vpedidonuevo);
                    p2.setProgress(100,true);
                    f2.setImageResource(R.drawable.vpedidoaprobado);
                    p3.setProgress(100,true);
                    f3.setImageResource(R.drawable.vpedidorevisado);
                    p4.setProgress(100,true);
                    f4.setImageResource(R.drawable.vpedidosurtido);
                    f5.setImageResource(R.drawable.play);
                    break;
                case "5":
                    p1.setProgress(100,true);
                    f1.setImageResource(R.drawable.vpedidonuevo);
                    p2.setProgress(100,true);
                    f2.setImageResource(R.drawable.vpedidoaprobado);
                    p3.setProgress(100,true);
                    f3.setImageResource(R.drawable.vpedidorevisado);
                    p4.setProgress(100,true);
                    f4.setImageResource(R.drawable.vpedidosurtido);
                    f5.setImageResource(R.drawable.vpedidonota);
                    break;

            }

        }

    }

    public List<ModeloRecepcion> listaPedidos;

    public AdapterRecepcion(List<ModeloRecepcion> pedidolista){
        this.listaPedidos = pedidolista;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recepcion_item,parent,false);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.estado = listaPedidos.get(position).getEstado();
        holder.pedido = listaPedidos.get(position).getPedido();
        holder.cliente.setText(listaPedidos.get(position).getCliente());
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }
}
