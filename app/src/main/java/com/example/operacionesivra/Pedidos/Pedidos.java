package com.example.operacionesivra.Pedidos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.operacionesivra.Pedidos.Ventas.Ventas;
import com.example.operacionesivra.R;

public class Pedidos extends AppCompatActivity {
    //Contexto
    Context contexto = this;
    //Declarar elementos
    ImageButton btnPedidosRegresar;
    Button btnPedidosVentas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos_a_menu);
        //Referenciar elementos
        btnPedidosRegresar = findViewById(R.id.btnPedidosRegresar);
        btnPedidosVentas = findViewById(R.id.btnPedidosVentas);
        //Acciones
        btnPedidosRegresar.setOnClickListener(view -> {
            finish();
        });
        btnPedidosVentas.setOnClickListener(view -> {
            openActivity(Ventas.class);
        });
    }

    //Método para abrir cualquier activity
    public void openActivity(Class clase){
        Intent intent = new Intent(contexto, clase);
        startActivity(intent);
    }
}