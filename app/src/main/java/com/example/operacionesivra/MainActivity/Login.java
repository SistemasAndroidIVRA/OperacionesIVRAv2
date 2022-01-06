package com.example.operacionesivra.MainActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.ResultSet;
import java.sql.Statement;

public class Login extends AppCompatActivity {
    TextInputEditText user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_login);

        user = findViewById(R.id.usuarioet);
        pass = findViewById(R.id.passet);


        findViewById(R.id.loginb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()) {
                    comprobarusuario(user.getText().toString(), pass.getText().toString());
                } else {
                    Toast.makeText(Login.this, "Complete los campos para iniciar sesión", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Comprueba la existtencia del usuario en la base de datos
    public void comprobarusuario(String usuario, String pass) {
        Conexion conexion = new Conexion(this);
        try {
            Statement s = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("select Nombre_Completo,Contraseña,IDusuario from movil_usuarios where Usuario='" + usuario + "' and Contraseña='" + pass + "'");
            if (r.next()) {
                guardarusuario(r.getString("Nombre_Completo"), r.getString("Contraseña"), r.getString("IDUsuario"));
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Usuario o Contraseña Incorrectos", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al conectar con el servidor.\nIntentelo mas tarde", Toast.LENGTH_LONG).show();
        }
    }

    //Guarda los datos del usuario en la memoria del telefono
    public void guardarusuario(String usuario, String password, String idsuario) {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", usuario);
        editor.putString("pass", password);
        editor.putString("iduser", idsuario);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Salir de la aplicación")
                .setMessage("¿Quiere salir de la aplicación?")
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
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