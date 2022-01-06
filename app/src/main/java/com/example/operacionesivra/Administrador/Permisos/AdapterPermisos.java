package com.example.operacionesivra.Administrador.Permisos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class AdapterPermisos extends RecyclerView.Adapter<AdapterPermisos.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        Switch aSwitch;
        TextView descripcion, nombrepermiso;
        Context context;
        String usuario, idpermiso;
        boolean check;

        //Según los permisos del usuario activa (o no) los switches
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            aSwitch = itemView.findViewById(R.id.switchp);
            descripcion = itemView.findViewById(R.id.descripcionp);
            context = itemView.getContext();
            nombrepermiso = itemView.findViewById(R.id.nombredelpermiso);

            if (!check)
                aSwitch.setThumbResource(R.drawable.switchof);

            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (int i = 0; i < ((DetallesUsuario) context).permisos.size(); i++) {
                        if (idpermiso.equals(((DetallesUsuario) context).permisos.get(i).getIdpermiso())) {
                            ((DetallesUsuario) context).permisos.set(i, new ModeloPermisos(((DetallesUsuario) context).permisos.get(i).getArea(), ((DetallesUsuario) context).permisos.get(i).getNombre(), ((DetallesUsuario) context).permisos.get(i).idpermiso, ((DetallesUsuario) context).permisos.get(i).getDescripcion(), ((DetallesUsuario) context).permisos.get(i).getUsuario(), isChecked));
                        }
                    }
                    if (!isChecked) {
                        aSwitch.setThumbResource(R.drawable.switchof);
                    } else {
                        aSwitch.setThumbResource(R.drawable.switchon);
                    }
                }
            });

        }

        //Verifica los permisos del usuario
        public void permisosdelusuario(String x) {
            Conexion c = new Conexion(context);
            try {
                Statement s = c.conexiondbImplementacion().createStatement();
                ResultSet r = s.executeQuery("select Movil_Permisos.IDPermiso from Movil_Usuario_permiso\n" +
                        "inner join Movil_Permisos on Movil_Permisos.IDPermiso = Movil_Usuario_permiso.IDPermiso\n" +
                        "where Movil_Usuario_permiso.IDUsuario='" + x + "'");
                while (r.next()) {
                    if (r.getString("IDPermiso").equals(idpermiso)) {
                        aSwitch.setChecked(true);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }

    }

    List<ModeloPermisos> permisos;

    public AdapterPermisos(List<ModeloPermisos> permisos) {
        this.permisos = permisos;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.administrador_permisos_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombrepermiso.setText(permisos.get(position).getNombre());
        holder.descripcion.setText(permisos.get(position).getDescripcion());
        holder.idpermiso = permisos.get(position).getIdpermiso();
        holder.check = permisos.get(position).isCheck();
        holder.aSwitch.setShowText(false);
        holder.permisosdelusuario(permisos.get(position).getUsuario());
    }

    @Override
    public int getItemCount() {
        return permisos.size();
    }


}
