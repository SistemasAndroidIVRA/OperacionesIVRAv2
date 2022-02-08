package com.example.operacionesivra.Vistas.Monitoreo.Detalles;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AdapterDetallesMonitoreo extends RecyclerView.Adapter<AdapterDetallesMonitoreo.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ubicacion, fechayhora;
        Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            ubicacion = itemView.findViewById(R.id.ubicacion_dm);
            fechayhora = itemView.findViewById(R.id.fechahora_dm);
        }

        //Regresa la direccion recibiendo las coordenadas de la ubicacion
        public String direccion(Float lat, float lon) {
            String direccion = "Direccion no disponible";
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(lat, lon, 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    String tempo = DirCalle.getAddressLine(0);
                    direccion = tempo;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return direccion;
        }
    }

    List<ModeloDetallesMonitoreo> detallesMonitoreos;

    public AdapterDetallesMonitoreo(List<ModeloDetallesMonitoreo> detallesMonitoreos) {
        this.detallesMonitoreos = detallesMonitoreos;
    }

    @NonNull
    @Override
    public AdapterDetallesMonitoreo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monitoreo_detalles_monitoreo_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDetallesMonitoreo.ViewHolder holder, int position) {
        float latf = Float.parseFloat(detallesMonitoreos.get(position).getLatitud());
        float lonf = Float.parseFloat(detallesMonitoreos.get(position).getLongitud());
        holder.ubicacion.setText(holder.direccion(latf, lonf));
        holder.fechayhora.setText(detallesMonitoreos.get(position).fechahora);
    }

    @Override
    public int getItemCount() {
        return detallesMonitoreos.size();
    }
}
