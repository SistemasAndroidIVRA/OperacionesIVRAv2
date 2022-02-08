package com.example.operacionesivra.Vistas.Services;

import android.content.Context;
import android.content.DialogInterface;
import android.os.StrictMode;

import com.example.operacionesivra.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMonitoreo {
    public Context context;

    public ConexionMonitoreo(Context context) {
        this.context = context;
        conexiondbImplementacion();
    }

    //Coneccion usuariotemporal la base de datos (Implementacion)
    public Connection conexiondbImplementacion() {
        Connection conexion = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://ivradns.ddns.net:1480;databaseName=Monitoreo;user=leonel;password=;");
            System.out.println("correcto base");
        } catch (SQLException | ClassNotFoundException e) {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(false)
                    .setTitle("Error al conectar con el servidor...")
                    .setMessage("Por favor verifique que existe una conexión wi-fi y presione 'Reintentar'.\n Si esto no soluciona el problema cierre la aplicacción y reportelo en el área de desarrollo.\n" + e)

                    .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            conexiondbImplementacion();
                        }
                    })
                    .setIcon(R.drawable.snakerojo)
                    .setNegativeButton("Esperar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return conexion;
    }
}
