package com.example.operacionesivra.Vistas.PantallaRecepcion;

import android.content.res.ColorStateList;
import android.graphics.Color;
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


public class AdapterRecepcion extends RecyclerView.Adapter<AdapterRecepcion.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String estado, pedido;
        public TextView cliente;
        public ImageView f1, f2, f3, f4, f5;
        public ProgressBar p1, p2, p3, p4;

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

        //Segun el itemn muestra el progreso y actuliza la imagen que lo representa
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void llenar() {
            switch (estado) {
                case "1":
                    f1.setImageResource(R.drawable.clienteverde);
                    final int[] i = {0};
                    Thread hilo1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(i[0] <=100){
                                p1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        p1.setProgress(i[0]);
                                    }
                                });
                                try{
                                    Thread.sleep(50);
                                }catch (Exception e){
                                    System.out.println(e.getMessage());
                                }
                                i[0]++;
                                if(i[0]>=99)i[0]=0;
                            }
                        }
                    });
                    hilo1.start();
                    f2.setImageResource(R.drawable.play);
                    p2.setProgress(0, true);
                    f3.setImageResource(R.drawable.pedido_autorizado);
                    p3.setProgress(0, true);
                    f4.setImageResource(R.drawable.surtiendo_pedido);
                    p4.setProgress(0, true);
                    f5.setImageResource(R.drawable.generando_nota);

                    break;
                case "2":
                    f1.setImageResource(R.drawable.clienteverde);
                    p1.setProgress(100, true);
                    p1.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#82D52A")));
                    f2.setImageResource(R.drawable.aprobadoverde);
                    final int[] i2 = {0};
                    Thread hilo2 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(i2[0] <=100){
                                p2.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        p2.setProgress(i2[0]);
                                    }
                                });
                                try{
                                    Thread.sleep(50);
                                }catch (Exception e){
                                    System.out.println(e.getMessage());
                                }
                                i2[0]++;
                                if(i2[0]>=99)i2[0]=0;
                            }
                        }
                    });
                    hilo2.start();
                    f3.setImageResource(R.drawable.play);
                    p3.setProgress(0, true);
                    f4.setImageResource(R.drawable.surtiendo_pedido);
                    p4.setProgress(0, true);
                    f5.setImageResource(R.drawable.generando_nota);
                    break;
                case "3":
                    f1.setImageResource(R.drawable.clienteverde);
                    p1.setProgress(100, true);
                    p1.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#82D52A")));
                    f2.setImageResource(R.drawable.aprobadoverde);
                    p2.setProgress(100, true);
                    p2.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#82D52A")));
                    f3.setImageResource(R.drawable.revisadoverde);
                    final int[] i3 = {0};
                    Thread hilo3 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(i3[0] <=100){
                                p3.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        p3.setProgress(i3[0]);
                                    }
                                });
                                try{
                                    Thread.sleep(50);
                                }catch (Exception e){
                                    System.out.println(e.getMessage());
                                }
                                i3[0]++;
                                if(i3[0]>=99)i3[0]=0;
                            }
                        }
                    });

                    hilo3.start();
                    f4.setImageResource(R.drawable.play);
                    p4.setProgress(0, true);
                    f5.setImageResource(R.drawable.generando_nota);
                    break;
                case "4":
                    p1.setProgress(100, true);
                    f1.setImageResource(R.drawable.clienteverde);
                    p1.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#82D52A")));
                    p2.setProgress(100, true);
                    f2.setImageResource(R.drawable.aprobadoverde);
                    p2.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#82D52A")));
                    p3.setProgress(100, true);
                    f3.setImageResource(R.drawable.revisadoverde);
                    p3.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#82D52A")));
                    f4.setImageResource(R.drawable.surtidoverde);
                    final int[] i4 = {0};
                    Thread hilo4 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(i4[0] <=100){
                                p4.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        p4.setProgress(i4[0]);
                                    }
                                });
                                try{
                                    Thread.sleep(50);
                                }catch (Exception e){
                                    System.out.println(e.getMessage());
                                }
                                i4[0]++;
                                if(i4[0]>=99)i4[0]=0;
                            }
                        }
                    });
                    hilo4.start();

                    f5.setImageResource(R.drawable.play);
                    break;
            }

        }

    }

    public List<ModeloRecepcion> listaPedidos;

    public AdapterRecepcion(List<ModeloRecepcion> pedidolista) {
        this.listaPedidos = pedidolista;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recepcion_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        holder.itemView.clearAnimation();
        super.onViewDetachedFromWindow(holder);
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

