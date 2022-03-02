package com.example.operacionesivra.Vistas.PantallaDePrioridades;

import android.content.Context;

import com.example.operacionesivra.Vistas.Services.Conexion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class DBListener extends Thread implements Runnable {
    int contadorgeneral = 0;
    public Context mContext;

    public DBListener(Context mContext) {
        this.mContext = mContext;
    }

    //Crea un ciclo para actualizar la tabla
    public void run() {
        do {
            if (estadodelaDB()) {
                ((PantalladePrioridades) mContext).añadiralalista();
                ((PantalladePrioridades) mContext).elimarlista();
            }
            ((PantalladePrioridades) mContext).cambiodevistas();
            try {
                Thread.sleep(15000);
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

    //Comprueba los pedidos actuales
    public boolean estadodelaDB() {
        boolean pruebasx = false;
        Conexion c = new Conexion();
        int contador = 0;
        try {
            Statement statement = c.conexiondbImplementacion().createStatement();
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
