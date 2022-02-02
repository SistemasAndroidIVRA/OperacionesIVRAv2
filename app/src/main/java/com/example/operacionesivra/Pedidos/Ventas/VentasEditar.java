package com.example.operacionesivra.Pedidos.Ventas;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Pedidos.Models.VentaDetalle;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class VentasEditar extends AppCompatActivity {
    //Contexto
    Context contexto = this;
    //Conexión
    Conexion conexion = new Conexion();
    //Declarar elementos
    ImageButton btnVentasEdiRegresar;
    RecyclerView recyclerVentasEdProductos;
    //Id de la compra;
    String ComprasID = "";
    //ArrayList
    ArrayList<VentaDetalle> arrayDetallesVenta = new ArrayList<>();
    //Adapter
    AdapterVentasDetalle adapterVentasDetalle = new AdapterVentasDetalle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos_a_ventas_editar);
        getInfoIntent();
        //Referenciar elementos
        btnVentasEdiRegresar = findViewById(R.id.btnVentasEdiRegresar);
        recyclerVentasEdProductos = findViewById(R.id.recyclerVentasEdProductos);
        //Acciones
        btnVentasEdiRegresar.setOnClickListener(view -> {
            finish();
        });
        getDetalleVentas(ComprasID);
        recyclerVentasEdProductos.setLayoutManager(new LinearLayoutManager(contexto));
        recyclerVentasEdProductos.setAdapter(adapterVentasDetalle);
    }

    //Método para traerd información intents
    public void getInfoIntent(){
        ComprasID = getIntent().getStringExtra("ComprasID");
    }

    //Método para traer los detalles de venta
    public void getDetalleVentas(String comprasID){
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("P_VentasDetails_SELECT ?");
            stmt.setString(1, comprasID);
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                VentaDetalle ventaDetalle = new VentaDetalle();
                ventaDetalle.setCompras_DetailsID(r.getString("Compras_DetailsID"));
                ventaDetalle.setComprasID(r.getString("ComprasID"));
                ventaDetalle.setRenglon(r.getInt("Renglon"));
                ventaDetalle.setCodigo(r.getString("Codigo"));
                ventaDetalle.setProductoID(r.getString("ProductoID"));
                ventaDetalle.setProducto(r.getString("Nombre"));
                ventaDetalle.setPrecio(r.getFloat("Precio"));
                ventaDetalle.setCantidad(r.getInt("P1"));
                ventaDetalle.setUnidadID(r.getString("UnidadID"));
                ventaDetalle.setUnidad(r.getString("UnidadMedida"));
                ventaDetalle.setFCaptura(r.getString("FCaptura"));
                arrayDetallesVenta.add(ventaDetalle);
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error al traer detalles: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Adapter detalles venta
    public class AdapterVentasDetalle extends RecyclerView.Adapter<AdapterVentasDetalle.AdapterVentasDetalleHolder>{
        @NonNull
        @Override
        public AdapterVentasDetalleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterVentasDetalleHolder(getLayoutInflater().inflate(R.layout.pedidos_a_ventas_registrar_items, parent, false));
        }

        @Override
        public void onBindViewHolder(AdapterVentasDetalleHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return arrayDetallesVenta.size();
        }

        class AdapterVentasDetalleHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
            //Declarar elementos
            TextView lblVCodigo, lblVProducto, lblVUnidad, lblVPrecio, lblVCantidad;
            CardView cardviewVProducto;
            public AdapterVentasDetalleHolder(@NonNull View itemView){
                super(itemView);
                //Referenciar elementos
                lblVCodigo = itemView.findViewById(R.id.lblVCodigo);
                lblVProducto = itemView.findViewById(R.id.lblVProducto);
                lblVUnidad = itemView.findViewById(R.id.lblVUnidad);
                lblVPrecio = itemView.findViewById(R.id.lblVPrecio);
                lblVCantidad = itemView.findViewById(R.id.lblVCantidad);
                cardviewVProducto = itemView.findViewById(R.id.cardviewVProducto);
            }

            //Mostrar la información
            public void printAdapter(int position){
                lblVCodigo.setText(arrayDetallesVenta.get(position).getCodigo());
                lblVProducto.setText(arrayDetallesVenta.get(position).getProducto());
                lblVUnidad.setText(arrayDetallesVenta.get(position).getUnidad());
                lblVCantidad.setText(""+arrayDetallesVenta.get(position).getCantidad());
                lblVPrecio.setText(""+arrayDetallesVenta.get(position).getPrecio());
            }

            @Override
            public void onClick(View view){

            }
        }
    }
}