package com.example.operacionesivra.PantallasCargando;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.example.operacionesivra.Administrador.Administrador;
import com.example.operacionesivra.Administrador.Permisos.DetallesUsuario;
import com.example.operacionesivra.Chequeo.ListadePedidos.ListadeChequeo;
import com.example.operacionesivra.Inventario.Inventario;
import com.example.operacionesivra.Inventario.ConteosPausa.Pausa;
import com.example.operacionesivra.Inventario.InventariosCerrados.Inventarioscerrados;
import com.example.operacionesivra.MainActivity.MainActivity;
import com.example.operacionesivra.PantallaDePrioridades.PantalladePrioridades;
import com.example.operacionesivra.PantallaRecepcion.PantallaDeRecepcion;
import com.example.operacionesivra.Picking.ListapedidosPicking.ListaPicking;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Reportes.Chequeo.ListaChequeoTerminado;
import com.example.operacionesivra.Reportes.Inventario.InventarioActual.InventarioActual;
import com.example.operacionesivra.Reportes.Inventario.InventariosCerrados.ReporteInventariosCerrados;
import com.example.operacionesivra.Reportes.Inventario.ReporteInventarioTerminado;
import com.example.operacionesivra.Reportes.Inventario.ReportesInventarioGeneral;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Loading extends AsyncTask<Void, Void, Void> {
    public ProgressDialog dialog;
    public Context mContext;

    public Loading(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Main Activity
        if(mContext instanceof MainActivity){
            try{
                if(((MainActivity) mContext).loadingMain==1){
                    ((MainActivity) mContext).comprobarsesion();
                }
            }catch (Exception e){
                System.out.println(e);
            }
        }

        //Inventario
        else  if (mContext instanceof Inventario) {
            try {
                if(((Inventario)mContext).loadingInventario==1) {
                    ((Inventario) mContext).subirtablaDB();
                }
                else if(((Inventario) mContext).loadingInventario ==3){
                    ((Inventario) mContext).subirtablaDB();
                    ((Inventario) mContext).Pausarmaterial("SI");
                    ((Inventario) mContext).bloquearmaterial("NO");
                }
                else if(((Inventario) mContext).loadingInventario ==5){
                    ((Inventario) mContext).activityMain();
                }
                else if(((Inventario) mContext).loadingInventario ==6){
                    ((Inventario) mContext).subirtablaDB();
                    ((Inventario) mContext).Pausarmaterial("SI");
                    ((Inventario) mContext).activityMain();
                }
                if(((Inventario)mContext).loadingInventario==2) {
                    ((Inventario) mContext).subirtablaDB();
                    ((Inventario) mContext).crearPDF();
                    ((Inventario) mContext).Pausarmaterial("NO");
                    ((Inventario) mContext).bloquearmaterial("NO");
                }
            }catch (Exception e){
                System.out.println(e);
            }

            //Ajustes de inventario
        }
        //Pausa
        else if (mContext instanceof Pausa) {
            if (((Pausa) mContext).loadingPausa == 1) {
                ((Pausa) mContext).cargardatos();
            }
            else if (((Pausa) mContext).loadingPausa == 2) {
                ((Pausa) mContext).activityMain();
            }
            else if (((Pausa) mContext).loadingPausa == 3) {
                ((Pausa) mContext).cargardatos();
            }
        }
        else if (mContext instanceof ListaPicking) {
            if (((ListaPicking) mContext).loadingpicking== 1) {
                ((ListaPicking) mContext).cargardatos();
            }
        }
        else if (mContext instanceof Inventarioscerrados) {
            if (((Inventarioscerrados) mContext).loadinginventarioscerrados == 1) {
                ((Inventarioscerrados) mContext).cargardatos();
            }
            else if (((Pausa) mContext).loadingPausa == 2) {
                ((Pausa) mContext).activityMain();
            }
            else if (((Pausa) mContext).loadingPausa == 3) {
                ((Pausa) mContext).cargardatos();
            }
        }
        else if (mContext instanceof ReporteInventariosCerrados) {
            if (((ReporteInventariosCerrados) mContext).loadingreportecerrados == 1) {
                ((ReporteInventariosCerrados) mContext).cargardatos();
            }
            else if (((Pausa) mContext).loadingPausa == 3) {
                ((Pausa) mContext).cargardatos();
            }
        }
        //PantalladePrioridades
        /*
        else if (mContext instanceof PantalladePrioridades) {
            if (((PantalladePrioridades) mContext).loadingprioridades== 1) {
                ((PantalladePrioridades) mContext).crearreporte();
            }
        }
         */
        //PantallaDeRecepcion
        else if (mContext instanceof PantallaDeRecepcion) {
            if (((PantallaDeRecepcion) mContext).loadingRecepcion== 1) {
                //((PantallaDeRecepcion) mContext).subirtablaDB();
            }
        }
        //ListadeChequeo
        else if (mContext instanceof ListadeChequeo) {
            if (((ListadeChequeo) mContext).loadingListaChequeo== 1) {
                ((ListadeChequeo) mContext).cargardatosbackgroud();
            }
        }
        //ListaChequeoTerminado
        else if (mContext instanceof ListaChequeoTerminado) {
            if (((ListaChequeoTerminado) mContext).loadingListaDeReportes== 1) {
                ((ListaChequeoTerminado) mContext).cargardatosbackgroud();
            }
        }
        //ReportesInventarioGeneral
        else if (mContext instanceof ReportesInventarioGeneral) {
            if (((ReportesInventarioGeneral) mContext).loadinginventariogeneral== 1) {
                ((ReportesInventarioGeneral) mContext).cargardatos();
            }
        }
        else if (mContext instanceof InventarioActual) {
            if (((InventarioActual) mContext).loadinginventarioactual== 1) {
                ((InventarioActual) mContext).doinbackgroud();
            }
        }
        else if (mContext instanceof ReporteInventarioTerminado) {
            if (((ReporteInventarioTerminado) mContext).loadingReporteterminado== 1) {
                ((ReporteInventarioTerminado) mContext).cargardatosbacgroud();
            }
        }
        else if(mContext instanceof DetallesUsuario){
            if(((DetallesUsuario)mContext).loadingDetallesUsuario==1) {
                ((DetallesUsuario) mContext).crear_actualizarusuario();
            }
            if(((DetallesUsuario)mContext).loadingDetallesUsuario==2) {
                ((DetallesUsuario) mContext).inicializar();
            }
        }

        else if(mContext instanceof Administrador){
            if(((Administrador)mContext).loadingadministrador==1)
                ((Administrador)mContext).cargandousuarios();

        }
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        //Main activity
        if(mContext instanceof MainActivity){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        //Inventario
        else if(mContext instanceof Inventario){
            try{
                if(((Inventario) mContext).loadingInventario==1){
                    dialog.dismiss();
                }
                else if(((Inventario) mContext).loadingInventario ==2) {
                    ((Inventario) mContext).mensajedeconfirmacion();
                    dialog.dismiss();
                }
                else  if(((Inventario) mContext).loadingInventario ==3){
                    if(((Inventario) mContext).comprobaciondeestado==1){
                        new MaterialAlertDialogBuilder(mContext)
                                .setTitle("Error guardar")
                                .setCancelable(false)
                                .setIcon(R.drawable.snakerojo)
                                .setMessage("No fue posible guardar los datos en el servidor debido a un error de conexión\n¿Quiere intentarlo de nuevo?")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                    else if(((Inventario) mContext).comprobaciondeestado==2){
                        new MaterialAlertDialogBuilder(mContext)
                                .setTitle("Datos guardados con exito")
                                .setIcon(R.drawable.correcto)
                                .setCancelable(false)
                                .setMessage("El stock del material esta disponible nuevamente")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
                    dialog.dismiss();
                }
                else if(((Inventario) mContext).loadingInventario ==4){
                    dialog.dismiss();
                }
                else if(((Inventario) mContext).loadingInventario ==5){
                    dialog.dismiss();
                }
                else if(((Inventario) mContext).loadingInventario ==6){
                    dialog.dismiss();
                }
                else if(((Inventario) mContext).loadingInventario ==7){
                    dialog.dismiss();
                }

            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if(mContext instanceof Pausa){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if(mContext instanceof Inventarioscerrados){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if(mContext instanceof ReporteInventariosCerrados){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if(mContext instanceof ListaPicking){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if(mContext instanceof PantalladePrioridades){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if(mContext instanceof PantallaDeRecepcion){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if(mContext instanceof ListadeChequeo){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if(mContext instanceof ListaChequeoTerminado){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if (mContext instanceof ReportesInventarioGeneral) {
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if (mContext instanceof InventarioActual) {
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if (mContext instanceof ReporteInventarioTerminado) {
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if (mContext instanceof DetallesUsuario) {
            try{

                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        } else if(mContext instanceof Administrador){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e);
            }
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Main activity
        if(mContext instanceof MainActivity){
            try{
                    dialog = ProgressDialog.show(mContext, "Conectando con el servidor", "Espere...", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        //Inventario
        if(mContext instanceof Inventario){
            try{
                    dialog = ProgressDialog.show(mContext, "Subiendo al servidor", "Espere...", false, false);

            }catch (Exception e){
                System.out.println(e);
            }
        }
        if(mContext instanceof ListaPicking){
            try{
                dialog = ProgressDialog.show(mContext, "Cargando datos", "Espere...", false, false);

            }catch (Exception e){
                System.out.println(e);
            }
        }
        //Pausa
        if(mContext instanceof Pausa){
            try{
                    dialog = ProgressDialog.show(mContext, "Cargando Datos", "Espere...", false, false);

            }catch (Exception e){
                System.out.println(e);
            }
        }
        if(mContext instanceof Inventarioscerrados){
            try{
                    dialog = ProgressDialog.show(mContext, "Cargando Datos", "Espere...", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        if(mContext instanceof ReporteInventariosCerrados){
            try{
                dialog = ProgressDialog.show(mContext, "Cargando Datos", "Espere...", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        if(mContext instanceof PantalladePrioridades){
            try{
                dialog = ProgressDialog.show(mContext, "Creando reporte", "Espere...", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        if(mContext instanceof PantallaDeRecepcion){
            try{
                dialog = ProgressDialog.show(mContext, "Subiendo información", "Espere...", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        if(mContext instanceof ListadeChequeo){
            try{
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        if(mContext instanceof ListaChequeoTerminado){
            try{
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        if(mContext instanceof ReportesInventarioGeneral){
            try{
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        if(mContext instanceof InventarioActual){
            try{
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        if (mContext instanceof ReporteInventarioTerminado) {
            try{
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        if (mContext instanceof DetallesUsuario) {
            try{
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        if(mContext instanceof  Administrador){
            try{
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }

    }
}
