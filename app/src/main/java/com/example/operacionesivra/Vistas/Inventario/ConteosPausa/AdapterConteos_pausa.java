package com.example.operacionesivra.Vistas.Inventario.ConteosPausa;

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

import com.example.operacionesivra.Vistas.Inventario.Inventario;
import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AdapterConteos_pausa extends RecyclerView.Adapter<AdapterConteos_pausa.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        CardView cardView;
        TextView fecha, material, usuario, contador;
        String bloqueado, folio, stocktotal, almacen;
        Intent intent;

        //encargado de llenar la vista con los datod que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            context = itemView.getContext();
            intent = new Intent(context, Inventario.class);
            fecha = itemView.findViewById(R.id.fecha_pausa);
            material = itemView.findViewById(R.id.material_pausa);
            usuario = itemView.findViewById(R.id.usuario_pausa);
            contador = itemView.findViewById(R.id.contador_pausa);
            cardView = itemView.findViewById(R.id.card_pausa);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fragment_fade_enter));
                    cardView.setOnClickListener(this);
                    confirmacion();
                }
            });
        }

        //Lanza un alert y en caso de aceptar envia datos para iniciar el activity
        public void confirmacion() {
            new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_MaterialComponents_Title_Icon)
                    .setCancelable(false)
                    .setTitle("Elemento Seleccionado")
                    .setMessage("¿Continuar el inventario del material " + material.getText() + " con fecha del " + fecha.getText() + "?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            intent.putExtra("usuario", usuario.getText().toString());
                            intent.putExtra("folio", folio);
                            intent.putExtra("StockTotal", stocktotal);
                            intent.putExtra("disparadorpausa", 1);
                            intent.putExtra("almacen", almacen);
                            context.startActivity(intent);
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

    public List<Modelo_conteos_pausa> Item;

    public AdapterConteos_pausa(List<Modelo_conteos_pausa> lista) {
        this.Item = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventario_conteos_pausa_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterConteos_pausa.ViewHolder holder, int position) {
        holder.fecha.setText(Item.get(position).getFecha());
        holder.material.setText(Item.get(position).getMaterial());
        holder.usuario.setText(Item.get(position).getUsuario());
        holder.bloqueado = Item.get(position).getBloqueado();
        holder.contador.setText(Item.get(position).getContador() + "");
        holder.folio = Item.get(position).getFolio();
        holder.stocktotal = Item.get(position).getStocktotal();
        holder.almacen = Item.get(position).getAlmacen();
    }

    @Override
    public int getItemCount() {
        return Item.size();
    }
}
