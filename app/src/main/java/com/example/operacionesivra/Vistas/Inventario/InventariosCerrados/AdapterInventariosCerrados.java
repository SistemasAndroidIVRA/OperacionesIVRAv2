package com.example.operacionesivra.Vistas.Inventario.InventariosCerrados;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Vistas.Inventario.Inventario;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;
import com.example.operacionesivra.Modelos.VariablesGlobales.GlobalesInventario;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class AdapterInventariosCerrados extends RecyclerView.Adapter<AdapterInventariosCerrados.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView material, fecha, fisico, sistema, diferencia, txtAlmacen;
        String folio, almacen, usuario;
        CardView card;
        Context contexto;
        RecyclerView recyclerView;
        ImageView imgIncidencia;

        //encargado de llenar la vista con los datos que se le envien
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            contexto = itemView.getContext();
            material = itemView.findViewById(R.id.material_ic);
            fecha = itemView.findViewById(R.id.fecha_ic);
            card = itemView.findViewById(R.id.card_ic);
            fisico = itemView.findViewById(R.id.fisico_ic);
            sistema = itemView.findViewById(R.id.sistema_ic);
            diferencia = itemView.findViewById(R.id.diferencia_ic);
            recyclerView = itemView.findViewById(R.id.recyclercerrados);
            txtAlmacen = itemView.findViewById(R.id.almacen_ic);
            imgIncidencia = itemView.findViewById(R.id.imgIncidencia);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialAlertDialogBuilder(contexto)
                            .setTitle("¡Confirmación!")
                            .setCancelable(false)
                            .setMessage("Qué desea hacer con el material: "+material.getText().toString().toLowerCase()+"?")
                            .setIcon(R.drawable.confirmacion)
                            .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //No hacer nada
                                }
                            })
                            .setNegativeButton("Terminar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Lo de terminar
                                    updateHistoricoEspecifico(folio, contexto);
                                    //Actualizar Ubicación reporte
                                }
                            })
                            .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Traer producto Id del inventario seleccionado
                                    String productoID = "";
                                    Conexion conexion = new Conexion(contexto);
                                    try {
                                        Statement stmt = conexion.conexiondbImplementacion().createStatement();
                                        String query = "SELECT ProductoID FROM Movil_Reporte WHERE Folio = '"+folio+"' GROUP BY ProductoID";
                                        ResultSet r = stmt.executeQuery(query);
                                        while (r.next()){
                                            productoID = r.getString("ProductoID");
                                        }
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                    //Guardar el nuevo contenido según el producto seleccionado
                                    traerContenido(contexto, folio);
                                    //Lo del editar
                                    Intent intent = new Intent(contexto, Inventario.class);
                                    intent.putExtra("folio", folio);
                                    intent.putExtra("usuario", usuario);
                                    intent.putExtra("almacen", almacen);
                                    intent.putExtra("opcion", 1);
                                    //Mandar el id del producto seleccionado
                                    intent.putExtra("productoID", productoID);
                                    contexto.startActivity(intent);
                                }
                            })
                            .show();
                }
            });
        }
    }

    //Método para traer la cantidad
    public static void traerContenido(Context context, String folio){
        try {
            Conexion con = new Conexion(context);
            Statement stmt = null;
            stmt = con.conexiondbImplementacion().createStatement();
            String query = "SELECT Cantidad FROM Movil_Reporte WHERE Folio = '"+folio+"' GROUP BY Folio, Cantidad;";
            ResultSet r = stmt.executeQuery(query);
            while(r.next()){
                GlobalesInventario.globalContMaterial = r.getFloat("Cantidad");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void updateHistoricoEspecifico(String folio, Context contexto){
        //Prueba y error
            try {
                //Creamos la conexión
                Conexion conexion = new Conexion(contexto);
                //Preparamos un statement y llamamos el procedimeinto almacenado
                PreparedStatement statement = conexion.conexiondbImplementacion().prepareCall("exec PMovil_Cambiar_Historicos '"+folio+"'");
                //Ejecutamos el estatement
                statement.execute();
                //Llamamos mensaje de exito

                //Traer movil report items sobre folio
                Statement stmt1 = conexion.conexiondbImplementacion().createStatement();
                String query = "SELECT Ubicacion, SUM(Total_registrado) as Cantidad, ProductoID, UbicacionID FROM Movil_Reporte WHERE Folio = '"+folio+"' GROUP BY Ubicacion, ProductoID, UbicacionID";
                ResultSet r = stmt1.executeQuery(query);
                int cont = 0;
                while(r.next()){
                    //Actualizamos en Ubicación producto
                    PreparedStatement statement2 = conexion.conexiondbImplementacion().prepareCall("exec PUbicacion_Producto_GENERAR ?,?,?,?");
                    statement2.setString(1, r.getString("UbicacionID"));
                    statement2.setString(2, r.getString("ProductoID"));
                    statement2.setString(3, r.getString("Cantidad"));
                    statement2.setInt(4, 1);
                    statement2.execute();
                    cont++;
                }
                mensajeExitoso(contexto, cont);
            } catch (SQLException throwables) {
                //Llamamos mensaje de error
                mensajeError(throwables, contexto);
            }
    }

    /*
    public static int comprobarIncidencia(String folio, Context contexto){
        Conexion con = new Conexion(contexto);
        int estadoIncidencia = 0;
        try {
            Statement stmt = con.conexiondbImplementacion().createStatement();
            String query = "SELECT COUNT(Observaciones) estadoIncidencia FROM Movil_Reporte WHERE Folio = '"+folio+"' AND historico = 0 AND Observaciones != ''";
            ResultSet r = stmt.executeQuery(query);
            while(r.next()){
                estadoIncidencia = r.getInt("estadoIncidencia");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return estadoIncidencia;
    }
     */

    //Método de mensaje exitoso
    public static void mensajeExitoso(Context contexto, int cont){
        new MaterialAlertDialogBuilder(contexto)
                .setTitle("¡Cambio realizado exitosamente!")
                .setIcon(R.drawable.correcto)
                .setCancelable(false)
                .setMessage("Se recargará la vistan\n" +
                        "Ahora puede consultar el inventario en Historico de Inventarios, registros afectados: "+cont)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Recargamos la actividad
                        Intent intent = new Intent(contexto, Inventarioscerrados.class);
                        contexto.startActivity(intent);
                        //Le mandamos el contexto a la actividad para poder finalizarla
                        Activity activity = (Activity) contexto;
                        activity.finish();
                    }
                })
                .show();
    }

    //Método de mensaje de error
    public static void mensajeError(SQLException throwables, Context contexto){
        new MaterialAlertDialogBuilder(contexto)
                .setTitle("¡Error!")
                .setIcon(R.drawable.snakerojo)
                .setMessage("Por favor revice que tenga acceso a internet y vuelva a intentar.\n" +
                        "Error: "+throwables.getMessage())
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Solo cerra dialogo
                    }
                })
                .show();
    }
    public List<ModeloInventariosCerrados> inventarioscerrados;

    public AdapterInventariosCerrados(List<ModeloInventariosCerrados> lista) {
        this.inventarioscerrados = lista;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventario_inventarioscerrados_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    //el if dentro de la funcion determina el estado del pedido y cambia el color segun el estado
    //No se ocupa realmente los datos con un comentario al final
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.usuario = inventarioscerrados.get(position).getUsuario();//No se muestra
        holder.fecha.setText(inventarioscerrados.get(position).getFecha());
        holder.material.setText(inventarioscerrados.get(position).getMaterial());
        holder.folio = inventarioscerrados.get(position).getFolio();//No se muestra
        holder.almacen = inventarioscerrados.get(position).getAlmacen();
        holder.fisico.setText(inventarioscerrados.get(position).getFisico());
        holder.sistema.setText(inventarioscerrados.get(position).getSistema());
        holder.diferencia.setText(inventarioscerrados.get(position).getDiferencia());
        holder.txtAlmacen.setText(inventarioscerrados.get(position).getAlmacen());
        float diferenciat = Float.parseFloat(holder.diferencia.getText().toString());
        //La comparación con el = no tiene sentido
        //Antes >=
        if (diferenciat < 0) {
            holder.diferencia.setText("-" + holder.diferencia.getText().toString());
            holder.diferencia.setTextColor(Color.parseColor("#DD2C2B"));
        } else {
            holder.diferencia.setText(holder.diferencia.getText().toString().replace("-", "+"));
            holder.diferencia.setTextColor(Color.parseColor("#4AA10D"));
        }
        if (diferenciat == 0) {
            holder.diferencia.setText(holder.diferencia.getText().toString().replace("-", ""));
            holder.diferencia.setTextColor(Color.parseColor("#1C9ACC"));
        }
        if(inventarioscerrados.get(position).getEstadoIncidencia() == 0){
            holder.imgIncidencia.setImageResource(R.drawable.correcto);
        }else{
            holder.imgIncidencia.setImageResource(R.drawable.confirmacion);
        }

    }


    @Override
    public int getItemCount() {
        return inventarioscerrados.size();
    }
}

//