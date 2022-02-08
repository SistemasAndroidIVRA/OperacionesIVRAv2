package com.example.operacionesivra.Vistas.Services;

import android.content.Context;
import android.content.DialogInterface;
import android.os.StrictMode;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    public Context context;

    public Conexion(Context context) {
        this.context = context;
        conexiondbImplementacion();
    }

    public Conexion() {
        conexiondbImplementacion();
    }

    //Coneccion usuariotemporal la base de datos (Implementacion)
    public Connection conexiondbImplementacion() {
        Connection conexion = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            //Puertos 1480/1433
            conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://ivradns.ddns.net:1480;databaseName=Orange;user=leonel;password=;");
            System.out.println("correcto base");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.toString());
            String errorsininternet = "No existe conexión a internet. \npor favor verifique que se encuentre conectado a una red wi-fi o bien los datos de su dispositivo estén activos.";
            if (e.toString().equals("java.sql.SQLException: Unknown server host name 'ivradns.ddns.net'.")) {
                new MaterialAlertDialogBuilder(context)
                        .setCancelable(false)
                        .setTitle("Error al conectar con el servidor...")
                        .setMessage(errorsininternet)
                        .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                conexiondbImplementacion();
                            }
                        })
                        .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                
                            }
                        })
                        .show();
            } else {
                new MaterialAlertDialogBuilder(context)
                        .setCancelable(false)
                        .setTitle("Error al conectar con el servidor...")
                        .setMessage("Error:" + e.toString())
                        .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                conexiondbImplementacion();
                            }
                        })
                        .show();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return conexion;
    }

}
