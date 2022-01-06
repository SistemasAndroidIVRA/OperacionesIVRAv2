package com.example.operacionesivra.Inventarios.Vistas.AjusteInventario;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.operacionesivra.R;

public class InventarioAjuste extends AppCompatActivity {
    //Contexto
    Context contexto = this;
    //Buttons
    Button btnAjusteMaterial, btnSolicitudAjuste, btnRegresarMenuAjuste;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_a_menu_ajuste);
        //Referenciar elementos
        btnAjusteMaterial = findViewById(R.id.btnAjusteMaterial);
        btnSolicitudAjuste = findViewById(R.id.btnSolicitudAjuste);
        btnRegresarMenuAjuste = findViewById(R.id.btnRegresarMenuAjuste);
        //Accioens botones
        btnAjusteMaterial.setOnClickListener(view -> {
            Intent intent = new Intent(contexto, AjusteMateriales.class);
            startActivity(intent);
        });
        btnSolicitudAjuste.setOnClickListener(view -> {
            Toast.makeText(contexto, "éste módulo está en desarrollo.", Toast.LENGTH_SHORT).show();
            /*
            Intent intent = new Intent(contexto, SolicitudAjuste.class);
            startActivity(intent);
             */
        });
        btnRegresarMenuAjuste.setOnClickListener(view -> {
            finish();
        });
    }
}