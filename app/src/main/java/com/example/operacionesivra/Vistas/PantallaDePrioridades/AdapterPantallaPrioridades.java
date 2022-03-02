package com.example.operacionesivra.Vistas.PantallaDePrioridades;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AdapterPantallaPrioridades extends RecyclerView.Adapter<AdapterPantallaPrioridades.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cliente, pedido, entrega, referencia, estadotext, fechavision;
        String fecha;
        public ImageView dia;
        String horaactual;
        Chronometer cp;

        //encargado de llenar la vista con los datod que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            cliente = itemView.findViewById(R.id.cliente_pp);
            pedido = itemView.findViewById(R.id.pedido_pp);
            entrega = itemView.findViewById(R.id.entrega_pp);
            referencia = itemView.findViewById(R.id.referencia_pp);
            dia = itemView.findViewById(R.id.diadelpedido);
            estadotext = itemView.findViewById(R.id.estadotexto);
            fechavision = itemView.findViewById(R.id.fechaitemprioridades);
            cp = itemView.findViewById(R.id.cp);

        }

        //Calculan el tiempo transcurrido
        @RequiresApi(api = Build.VERSION_CODES.O)
        public int diastranscurridos(String fechapedido) {
            String minutostranscurridos = (2 * 60f) / 100f + "";
            System.out.println(minutostranscurridos.substring(minutostranscurridos.indexOf(".") + 1));
            if (minutostranscurridos.length() > 1) {
                minutostranscurridos = minutostranscurridos.substring(0, 1);
            }
            int minutosr = Integer.parseInt(minutostranscurridos);
            return minutosr;
        }



        public int minutostranscurridos(float minutos) {
            String minutostranscurridos = (minutos * 60f) / 100f + "";
            System.out.println(minutostranscurridos.substring(minutostranscurridos.indexOf(".") + 1));
            if (minutostranscurridos.length() > 1) {
                minutostranscurridos = minutostranscurridos.substring(0, 1);
            }
            int minutosr = Integer.parseInt(minutostranscurridos);
            return minutosr;
        }

        public int segundostranscurridos(float segundos) {
            String segundostranscurridos = (segundos * 60f) / 100f + "";
            System.out.println(segundostranscurridos.substring(segundostranscurridos.indexOf(".") + 1));
            if (segundostranscurridos.length() > 1) {
                segundostranscurridos = segundostranscurridos.substring(0, 1);
            }
            int segundosr = Integer.parseInt(segundostranscurridos);
            return segundosr;
        }

        public String tiempotranscurrido(String actuals, String fecha, String fechaactual) {
            String tiempotranscurrido = "Imposible calcular";
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                int dias=0;
                if(!fecha.equals(fechaactual)){
                    Date fechaInicial=dateFormat.parse(fecha);
                    Date fechaFinal=dateFormat.parse(fechaactual);
                    dias=(int) ((fechaFinal.getTime()-fechaInicial.getTime())/86400000);
                }
                String bases = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date actual = format.parse(actuals);
                Date base = format.parse(bases);
                long diff = base.getTime() - actual.getTime();//as given
                final long days = TimeUnit.MILLISECONDS.toDays(diff);
                final long hours = TimeUnit.MILLISECONDS.toHours(diff);
                final long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                final long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
                //int diastranscurridos = diastranscurridos(fecha);
                System.out.println("Dias "+hours);
                if (dias>0) {
                    tiempotranscurrido = "Dias: " + dias;
                } else if (hours > 0) {
                    String horastranscurridos = hours + "";
                    if (horastranscurridos.length() == 1) {
                        horastranscurridos = "0" + horastranscurridos;
                    }
                    String minutostranscurridos = minutostranscurridos(minutes) + "";
                    if (minutostranscurridos.length() == 1) {
                        minutostranscurridos = "0" + minutostranscurridos;
                    }
                    String segundostranscurridos = segundostranscurridos(seconds) + "";
                    if (segundostranscurridos.length() == 1) {
                        segundostranscurridos = "0" + segundostranscurridos;
                    }

                    tiempotranscurrido = horastranscurridos + ":"
                            + minutostranscurridos + ":" + segundostranscurridos;
                } else if (minutes > 0) {
                    String minutostranscurridos = minutostranscurridos(minutes) + "";
                    if (minutostranscurridos.length() == 1) {
                        minutostranscurridos = "0" + minutostranscurridos;
                    }
                    String segundostranscurridos = segundostranscurridos(seconds) + "";
                    if (segundostranscurridos.length() == 1) {
                        segundostranscurridos = "0" + segundostranscurridos;
                    }

                    tiempotranscurrido = "00:"
                            + minutostranscurridos + ":" + segundostranscurridos;
                } else {
                    tiempotranscurrido = "Segundos" + seconds;
                }

            } catch (Exception e) {
                System.out.println("Error" + e);
            }

            return tiempotranscurrido;
        }

    }


    public List<ModeloPantalladePrioridades> detallesajuste;

    public AdapterPantallaPrioridades(List<ModeloPantalladePrioridades> lista) {
        this.detallesajuste = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prioridades_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cliente.setText(detallesajuste.get(position).getCliente());
        holder.pedido.setText(detallesajuste.get(position).getPedido());
        holder.entrega.setText(detallesajuste.get(position).getEntrega());
        holder.referencia.setText(detallesajuste.get(position).getReferencia());
        holder.dia.setImageResource(detallesajuste.get(position).getDia());
        holder.fecha = detallesajuste.get(position).getFecha();
        holder.estadotext.setText(detallesajuste.get(position).getEstadotexto());
        holder.fechavision.setText(detallesajuste.get(position).getFecha());
        holder.horaactual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        holder.entrega.setText(holder.tiempotranscurrido(
                holder.entrega.getText().toString().replace(" Hrs", ":00"),
                holder.fechavision.getText().toString(),new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())));

        if (detallesajuste.get(position).getEstadotexto().equals("On Time")) {
            holder.estadotext.setBackgroundColor(Color.parseColor("#4AA10D"));
        } else {
            holder.estadotext.setBackgroundColor(Color.parseColor("#DD2C2B"));
        }
    }

    @Override
    public int getItemCount() {
        return detallesajuste.size();
    }
}
