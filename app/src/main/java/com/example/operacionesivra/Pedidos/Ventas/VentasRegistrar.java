package com.example.operacionesivra.Pedidos.Ventas;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.operacionesivra.R;

public class VentasRegistrar extends AppCompatActivity {
    //Contexto
    Context contexto = this;
    //Declarar elementos
    ImageButton btnVentasRegRegresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos_a_ventas_registrar);
        //Referenciar elementos
        btnVentasRegRegresar = findViewById(R.id.btnVentasRegRegresar);
        //Acciones
        btnVentasRegRegresar.setOnClickListener(view -> {
            finish();
        });
    }
}