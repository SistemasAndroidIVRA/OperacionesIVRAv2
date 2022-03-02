package com.example.operacionesivra.Vistas.Seguridad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Seguridad.Modulos.Modulos;
import com.example.operacionesivra.Vistas.Seguridad.Roles.Roles;

public class Seguridad extends AppCompatActivity {
    //Contexto
    Context contexto = this;
    //Declarar elementos
    Button btnSeguridadModulos, btnSeguridadRoles;
    ImageButton btnSeguridadRegresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seguridad_a_menu);
        //Referenciar elementos
        btnSeguridadModulos = findViewById(R.id.btnSeguridadModulos);
        btnSeguridadRoles = findViewById(R.id.btnSeguridadRoles);
        btnSeguridadRegresar = findViewById(R.id.btnSeguridadRegresar);
        //Acciones
        btnSeguridadRegresar.setOnClickListener(view -> {
            finish();
        });
        btnSeguridadModulos.setOnClickListener(view -> {
            openActivity(Modulos.class);
        });
        btnSeguridadRoles.setOnClickListener(view -> {
            openActivity(Roles.class);
        });
    }

    //Método para abrir la actividad
    public void openActivity(Class clase){
        Intent intent = new Intent(contexto, clase);
        startActivity(intent);
    }
}