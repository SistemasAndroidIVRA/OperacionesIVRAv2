package com.example.operacionesivra.Vistas.Pedidos.Ventas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Modelos.Venta;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Ventas extends AppCompatActivity {
    //Contexto
    Context contetxo = this;
    //Conexión
    Conexion conexion = new Conexion();
    //Declarar elementos
    ImageButton btnVentasRegresar;
    Button btnVentasNuevo;
    RecyclerView recyclerVentas;
    TextView lblVentasTotales;
    //ArrayList
    ArrayList<Venta> arrayVentas = new ArrayList<>();
    //Adapter
    AdapterVentas adapterVentas = new AdapterVentas();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos_a_ventas);
        //Referenciar elementos
        recyclerVentas = findViewById(R.id.recyclerVentas);
        btnVentasRegresar = findViewById(R.id.btnVentasRegresar);
        btnVentasNuevo = findViewById(R.id.btnVentasNuevo);
        //Acciones
        btnVentasRegresar.setOnClickListener(view -> {
            finish();
        });
        btnVentasNuevo.setOnClickListener(view -> {
            openActivity(VentasRegistrar.class);
        });
        lblVentasTotales = findViewById(R.id.lblVentasTotales);
        getVentas();
        lblVentasTotales.setText(""+arrayVentas.size());
        recyclerVentas.setLayoutManager(new LinearLayoutManager(contetxo));
        recyclerVentas.setAdapter(adapterVentas);
    }

    //Método para abrir cualquier intent
    public void openActivity(Class clase){
        Intent intent = new Intent(contetxo,clase);
        startActivity(intent);
    }

    //Método para traer las ventas hechas
    public void getVentas(){
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("P_Ventas_SELECT");
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                Venta venta = new Venta();
                venta.setComprasID(r.getString("ComprasID"));
                venta.setEmpresaID(r.getString("EmpresaID"));
                venta.setDocumentoID(r.getInt("DocumentoID"));
                venta.setEtapa(r.getInt("Etapa"));
                venta.setFecha(r.getString("Fecha"));
                venta.setHora(r.getString("Hora"));
                venta.setNumero(r.getString("Numero"));
                venta.setClienteID(r.getString("ClienteID"));
                venta.setCliente(r.getString("Cliente"));
                venta.setVenedorID(r.getString("VendedorID"));
                venta.setVendedor(r.getString("Vendedor"));
                venta.setEntregar_a(r.getString("Entregar_a"));
                venta.setTotal(r.getFloat("Total"));
                venta.setUsuarioID(r.getString("UsuarioID"));
                venta.setEnviarNombre(r.getString("EnviarNombre"));
                venta.setReferencia(r.getString("Referencia"));
                venta.setObservaciones(r.getString("EnviarDireccion"));
                arrayVentas.add(venta);
            }
        }catch (Exception e){
            Toast.makeText(contetxo, "Error al traer ventas: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Adapter ventas
    public class AdapterVentas extends RecyclerView.Adapter<AdapterVentas.AdapterVentasHolder>{
        @NonNull
        @Override
        public AdapterVentasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterVentasHolder(getLayoutInflater().inflate(R.layout.pedidos_a_ventas_items, parent, false));
        }

        @Override
        public void onBindViewHolder(AdapterVentasHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return arrayVentas.size();
        }

        class AdapterVentasHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            //Declarar elementos
            TextView lblVentaFolioItem, lblVentaFechaItem, lblVentaReferenciaItem, lblVentaTotalItem, lblVentaClienteItem, lblVentaVendedorItem, lblVentaObservacionesItem;
            ImageButton btnVentaDetalle;
            CardView cardviewPedidos;
            public AdapterVentasHolder(@NonNull View itemView){
                super(itemView);
                //Referenciar elementos
                lblVentaFolioItem = itemView.findViewById(R.id.lblVentaFolioItem);
                lblVentaFechaItem = itemView.findViewById(R.id.lblVentaFechaItem);
                lblVentaReferenciaItem = itemView.findViewById(R.id.lblVentaReferenciaItem);
                lblVentaTotalItem = itemView.findViewById(R.id.lblVentaTotalItem);
                lblVentaClienteItem = itemView.findViewById(R.id.lblVentaClienteItem);
                lblVentaVendedorItem = itemView.findViewById(R.id.lblVentaVendedorItem);
                lblVentaObservacionesItem = itemView.findViewById(R.id.lblVentaObservacionesItem);
                btnVentaDetalle = itemView.findViewById(R.id.btnVentaDetalle);
                cardviewPedidos = itemView.findViewById(R.id.cardviewPedidos);
                btnVentaDetalle.setOnClickListener(view -> {
                    //Abrir para editar
                    Intent intent = new Intent(contetxo, VentasEditar.class);
                    intent.putExtra("ComprasID", arrayVentas.get(getAdapterPosition()).getComprasID());
                    startActivity(intent);
                });
            }

            //Mostrar información
            public void printAdapter(int position){
                if(arrayVentas.get(position).getEtapa() == 5){
                    cardviewPedidos.setBackgroundColor(Color.parseColor("#DDFDFF"));
                }else if(arrayVentas.get(position).getEtapa() == 4){
                    cardviewPedidos.setBackgroundColor(Color.parseColor("#FEDDFF"));
                }else if(arrayVentas.get(position).getEtapa() == 3){
                    cardviewPedidos.setBackgroundColor(Color.parseColor("#FDFFDD"));
                }else if(arrayVentas.get(position).getEtapa() == 2){
                    cardviewPedidos.setBackgroundColor(Color.parseColor("#DEFFDD"));
                }else if(arrayVentas.get(position).getEtapa() == 1){
                    cardviewPedidos.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                lblVentaFolioItem.setText(arrayVentas.get(position).getNumero());
                lblVentaFechaItem.setText(arrayVentas.get(position).getFecha());
                lblVentaReferenciaItem.setText(arrayVentas.get(position).getReferencia());
                lblVentaTotalItem.setText(""+arrayVentas.get(position).getTotal());
                lblVentaClienteItem.setText(arrayVentas.get(position).getCliente());
                lblVentaVendedorItem.setText(arrayVentas.get(position).getVendedor());
                if(arrayVentas.get(position).getObservaciones().equals("") || arrayVentas.get(position).getObservaciones() == null){
                    lblVentaObservacionesItem.setText("Sin observaciones");
                }else{
                    lblVentaObservacionesItem.setText(arrayVentas.get(position).getObservaciones());
                }
            }

            @Override
            public void onClick(View view){

            }
        }
    }

}