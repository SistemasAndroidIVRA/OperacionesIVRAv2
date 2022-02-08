package com.example.operacionesivra.Vistas.PantallaRecepcion;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.operacionesivra.Vistas.Services.Conexion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class DBListenerRecepcion extends Thread implements Runnable {
    int contadorgeneral = 0;
    public Context mContext;

    public DBListenerRecepcion(Context mContext) {
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void run() {
        do {
            if (estadodelaDB()) {
                ((PantallaDeRecepcion) mContext).actualizarlista();
                ((PantallaDeRecepcion) mContext).añadiralalista();
                ((PantallaDeRecepcion) mContext).elimarlista();
            }
            try {
                Thread.sleep(numeroRandom(10000, 15000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (contadorgeneral >= 0);
    }

    public void terminar() {
        contadorgeneral = -1;
    }

    private static int numeroRandom(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public boolean estadodelaDB() {
        boolean pruebasx = false;
        Conexion c = new Conexion();
        int contador = 0;
        try {
            Statement statement = c.conexiondbImplementacion().createStatement();
            ResultSet resultSet = statement.executeQuery("Execute PMovil_PantallaRecepcion");
            while (resultSet.next()) {
                for (int i = 0; i < ((PantallaDeRecepcion) mContext).pedidos.size(); i++) {
                    if (((PantallaDeRecepcion) mContext).pedidos.get(i).getPedido().equals(resultSet.getString("Pedido"))) {
                        if (!((PantallaDeRecepcion) mContext).pedidos.get(i).getEstado().equals(resultSet.getString("Estado_Clave"))) {
                            pruebasx = true;
                        }
                    }
                }
                contador++;
            }
            if (contador != ((PantallaDeRecepcion) mContext).pedidos.size()) {
                pruebasx = true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return pruebasx;
    }

}
