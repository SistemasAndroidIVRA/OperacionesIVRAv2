package com.example.operacionesivra.Inventarios.Vistas.AjusteInventario;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Inventarios.Models.AjusteModel;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class AjusteMateriales extends AppCompatActivity {
    Context contexto = this;
    Conexion conexion = new Conexion();
    //ArrayList
    ArrayList<AjusteModel> ajustes = new ArrayList<>();
    //RecyclerView
    RecyclerView recyclerAjustes;
    AdapterAjusteInventario adapterAjusteInventario = new AdapterAjusteInventario();
    //Buttons
    Button btnNuevoAjuste, btnRegresarAjustes, btnFiltros;
    //LinearLayput
    LinearLayout linearFiltros;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_a_ajuste_materiales);
        linearFiltros = findViewById(R.id.linearFiltros);
        //RecyclerView
        recyclerAjustes = findViewById(R.id.recyclerAjustes);
        recyclerAjustes.setLayoutManager(new LinearLayoutManager(contexto));
        recyclerAjustes.setAdapter(adapterAjusteInventario);
        //Llenar la tabla
        getAjustesInventario();
        //Referenciar elementos
        btnNuevoAjuste = findViewById(R.id.btnNuevoAjuste);
        btnRegresarAjustes = findViewById(R.id.btnRegresarAjustes);
        btnFiltros = findViewById(R.id.btnFiltros);
        //Acciones
        btnNuevoAjuste.setOnClickListener(view -> {
            Intent intent = new Intent(this, InventarioNuevoAjuste.class);
            startActivity(intent);
            finish();
        });
        btnRegresarAjustes.setOnClickListener(view -> {
            finish();
        });
        btnFiltros.setOnClickListener(view -> {
            if(linearFiltros.getVisibility() == View.GONE){
                linearFiltros.setVisibility(View.VISIBLE);
            }else{
                linearFiltros.setVisibility(View.GONE);
            }
        });
    }
    //martín tapia

    //Método para realizar ajustes de inventario
    public void getAjustesInventario(){
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("P_AjustesMovil_SELECT ?");
            stmt.setString(1, "11FF120F-942F-4F1B-87E6-7C7AD3D7AFB0");
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                AjusteModel ajuste = new AjusteModel();
                ajuste.setNumero(r.getInt("Numero"));
                ajuste.setCompraID(r.getString("ComprasID"));
                ajuste.setObs1(r.getString("EnviarNombre"));
                ajuste.setCantidad(r.getFloat("Cantidad"));
                ajuste.setFechaHora(r.getString("Fecha"));
                ajuste.setFechaAceptacion("Campo en desarrollo");
                ajuste.setElaboradoPor("Campo en desarrollo");
                ajuste.setReferencia(r.getString("Entregar_a"));
                ajustes.add(ajuste);
            }
        }catch (Exception e){
            Toast.makeText(contexto, "Error al traer datos BD: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //AdapterAjuste de inventario
    public class AdapterAjusteInventario extends RecyclerView.Adapter<AdapterAjusteInventario.AdapterAjusteInventarioHolder>{
        @Override
        @NonNull
        public AdapterAjusteInventarioHolder onCreateViewHolder(@NonNull ViewGroup itemView, int viewType){
            return new AdapterAjusteInventarioHolder(getLayoutInflater().inflate(R.layout.inventario_a_ajuste_materiales_item, itemView, false));
        }

        @Override
        public void onBindViewHolder(AdapterAjusteInventarioHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return ajustes.size();
        }

        class AdapterAjusteInventarioHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            //Elementos
            TextView lblFolioAjuste, lblAlmacenAjuste, lblObsAjuste, lblCantidadAjuste, lblFAltaAjuste, lblFAceptAjuste, lblElaboradoAjuste, lblReferenciaAjuste;
            LinearLayout linearAjuste;
            public AdapterAjusteInventarioHolder(@NonNull View itemView){
                super(itemView);
                lblFolioAjuste = itemView.findViewById(R.id.lblFolioAjuste);
                lblAlmacenAjuste = itemView.findViewById(R.id.lblAlmacenAjuste);
                lblObsAjuste = itemView.findViewById(R.id.lblObsAjuste);
                lblCantidadAjuste = itemView.findViewById(R.id.lblCantidadAjuste);
                lblFAltaAjuste = itemView.findViewById(R.id.lblFAltaAjuste);
                lblFAceptAjuste = itemView.findViewById(R.id.lblFAceptAjuste);
                lblElaboradoAjuste = itemView.findViewById(R.id.lblElaboradoAjuste);
                lblReferenciaAjuste = itemView.findViewById(R.id.lblReferenciaAjuste);
                linearAjuste = itemView.findViewById(R.id.linearAjuste);
                //Acciones
                linearAjuste.setOnClickListener(view -> {

                });
            }

            //Mostrar la información del sistema
            public void printAdapter(int position){
                lblFolioAjuste.setText(""+ajustes.get(position).getNumero());
                lblAlmacenAjuste.setText("Campo en desarrollo");
                lblObsAjuste.setText(ajustes.get(position).getObs1());
                lblCantidadAjuste.setText(""+ajustes.get(position).getCantidad());
                lblFAltaAjuste.setText(""+ajustes.get(position).getFechaHora());
                lblFAceptAjuste.setText(""+ajustes.get(position).getFechaAceptacion());
                lblElaboradoAjuste.setText(""+ajustes.get(position).getElaboradoPor());
                lblReferenciaAjuste.setText(ajustes.get(position).getReferencia());
                if(ajustes.get(position).getReferencia() == null || ajustes.get(position).getReferencia().equals("")){
                    lblReferenciaAjuste.setText("Sistema");
                    linearAjuste.setBackgroundColor(Color.parseColor("#FFEBE5"));
                }else{
                    lblReferenciaAjuste.setText(ajustes.get(position).getReferencia());
                    linearAjuste.setBackgroundColor(Color.parseColor("#E5F0FF"));
                }
            }

            @Override
            public void onClick(View view){
                //No hace nada
            }
        }
    }
}