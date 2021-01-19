package com.example.operacionesivra.PantallaDePrioridades;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;

import androidx.annotation.RequiresApi;

import com.example.operacionesivra.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DBListener extends Thread implements Runnable {
    int contadorgeneral = 0;
    public Context mContext;

    public DBListener(Context mContext) {
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void run() {
        do {
            if (estadodelaDB()) {
                ((PantalladePrioridades) mContext).añadiralalista();
                ((PantalladePrioridades) mContext).elimarlista();
            }
            ((PantalladePrioridades) mContext).cambiodevistas();
            try {
                Thread.sleep(numeroRandom(10000, 15000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (contadorgeneral >= 0);
    }

    public void terminar(){
        contadorgeneral=-1;
    }

    private static int numeroRandom(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public Connection conexiondbImplementacion() {
        Connection conexion = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.252:1433;databaseName=Orange;user=leonel;password=;");
            System.out.println("correcto base");
        } catch (Exception e) {
            System.out.println(e);
        }
        return conexion;
    }

    public boolean estadodelaDB() {
        boolean pruebasx = false;
        int contador = 0;
        try {
            Statement statement = conexiondbImplementacion().createStatement();
            ResultSet resultSet = statement.executeQuery("Execute PMovil_Prioridades");
            while (resultSet.next()) {
                contador++;
            }
            if (contador != ((PantalladePrioridades) mContext).listadepedidos.size()) {
                pruebasx = true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return pruebasx;
    }
}
