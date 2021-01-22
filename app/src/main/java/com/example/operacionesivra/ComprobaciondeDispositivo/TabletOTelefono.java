package com.example.operacionesivra.ComprobaciondeDispositivo;

import android.content.Context;
import android.content.res.Configuration;


public class TabletOTelefono {

    public static boolean esTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
