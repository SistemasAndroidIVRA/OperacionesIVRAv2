package com.example.operacionesivra.Vistas.PantallasCargando;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.operacionesivra.Vistas.Minuta.MinutaConsultarMinutas;
import com.example.operacionesivra.Vistas.Administrador.Administrador;
import com.example.operacionesivra.Vistas.Administrador.Permisos.DetallesUsuario;
import com.example.operacionesivra.Vistas.Chequeo.ListadePedidos.ListadeChequeo;
import com.example.operacionesivra.Vistas.Inventario.Inventario;
import com.example.operacionesivra.Vistas.Inventario.ConteosPausa.Pausa;
import com.example.operacionesivra.Vistas.Inventario.InventariosCerrados.Inventarioscerrados;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.EnHistorico.DetallesHistorico.InventarioAEnHistoricosDetalle;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.EnHistorico.InventariosEnHistorico;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.EnProceso.DetallesProceso.InventarioAEnProcesosDetalle;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.EnProceso.InventariosEnProceso;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.InventariosMenu;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.InventariosRegistro;
import com.example.operacionesivra.Vistas.MainActivity.MainActivity;
//import com.example.operacionesivra.Vistas.Minuta.Vistas.MinutaConsultarMinutas;
import com.example.operacionesivra.Vistas.Minuta.MinutaMenu;
import com.example.operacionesivra.Vistas.Minuta.MinutaReunionDetalle;
import com.example.operacionesivra.Vistas.Minuta.MinutaReunionRegistro;
import com.example.operacionesivra.Vistas.PantallaDePrioridades.PantalladePrioridades;
import com.example.operacionesivra.Vistas.PantallaRecepcion.PantallaDeRecepcion;
import com.example.operacionesivra.Vistas.Picking.ListapedidosPicking.ListaPicking;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Reportes.Chequeo.ListaChequeoTerminado;
import com.example.operacionesivra.Vistas.Reportes.Inventario.InventarioActual.InventarioActual;
import com.example.operacionesivra.Vistas.Reportes.Inventario.InventariosCerrados.ReporteInventariosCerrados;
import com.example.operacionesivra.Vistas.Reportes.Inventario.ReporteInventarioTerminado;
import com.example.operacionesivra.Vistas.Reportes.Inventario.ReportesInventarioGeneral;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Loading extends AsyncTask<Void, Void, Void> {
    public ProgressDialog dialog;
    public Context mContext;

    public Loading(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Inventarios
        //Inventaqrios registro
        if(mContext instanceof InventariosRegistro){
            try{
                if (((InventariosRegistro) mContext).loading == 1) {
                    ((InventariosRegistro) mContext).addCodigoEscaneado(((InventariosRegistro) mContext).txtInvCodEscanear.getText().toString());
                }else if(((InventariosRegistro) mContext).loading == 2){
                    ((InventariosRegistro) mContext).generateEtiquetasViejas();
                }else if(((InventariosRegistro) mContext).loading == 3){
                    ((InventariosRegistro) mContext).pausarProducto();
                }else if(((InventariosRegistro) mContext).loading == 4){
                    ((InventariosRegistro) mContext).despausarProducto();
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        //Inventarios Menu
        if(mContext instanceof InventariosMenu){
            try{
                if (((InventariosMenu) mContext).loading == 1) {
                    ((InventariosMenu) mContext).validateCodigo();
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        //InventariosEnProceso
        if(mContext instanceof InventariosEnProceso){
            try{
                if (((InventariosEnProceso) mContext).loading == 1) {
                    ((InventariosEnProceso) mContext).getByFechas();
                } else if (((InventariosEnProceso) mContext).loading == 2) {
                    ((InventariosEnProceso) mContext).setTerminarInventarios();
                }else if (((InventariosEnProceso) mContext).loading == 3) {
                    ((InventariosEnProceso) mContext).setTerminarInventario();
                }else if (((InventariosEnProceso) mContext).loading == 4) {
                    ((InventariosEnProceso) mContext).depurarUbicacionesAceptada();
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        //InventariosEnProcesoDetalle
        if(mContext instanceof InventarioAEnProcesosDetalle){
            try{
                if (((InventarioAEnProcesosDetalle) mContext).loading == 1) {
                    ((InventarioAEnProcesosDetalle) mContext).getRegistros();
                }else  if (((InventarioAEnProcesosDetalle) mContext).loading == 2) {
                    ((InventarioAEnProcesosDetalle) mContext).setEditInventario();
                }else if (((InventarioAEnProcesosDetalle) mContext).loading == 3) {
                    ((InventarioAEnProcesosDetalle) mContext).pausarProducto();
                }else if (((InventarioAEnProcesosDetalle) mContext).loading == 4) {
                    ((InventarioAEnProcesosDetalle) mContext).despausarProducto();
                }
            }catch (Exception e){
                Toast.makeText(mContext, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        //Inventarios en historico
        if(mContext instanceof InventariosEnHistorico){
            try{
                if (((InventariosEnHistorico) mContext).loading == 1) {
                    ((InventariosEnHistorico) mContext).getByFechas();
                }else if (((InventariosEnHistorico) mContext).loading == 2) {
                    ((InventariosEnHistorico) mContext).generateReporte(1);
                }else if (((InventariosEnHistorico) mContext).loading == 3) {
                    ((InventariosEnHistorico) mContext).generateReporte(2);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        //Detalle sinventarios en historico
        if(mContext instanceof InventarioAEnHistoricosDetalle){
            try{
                if (((InventarioAEnHistoricosDetalle) mContext).loading == 1) {
                    ((InventarioAEnHistoricosDetalle) mContext).getDetalles();
                }else if (((InventarioAEnHistoricosDetalle) mContext).loading == 2) {
                    ((InventarioAEnHistoricosDetalle) mContext).generateReporte(1);
                }else if (((InventarioAEnHistoricosDetalle) mContext).loading == 3) {
                    ((InventarioAEnHistoricosDetalle) mContext).generateReporte(2);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        //Main Activity
        if (mContext instanceof MainActivity) {
            try {
                if (((MainActivity) mContext).loadingMain == 1) {
                    ((MainActivity) mContext).comprobarsesion();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventario
        else if (mContext instanceof Inventario) {
            try {
                if (((Inventario) mContext).loadingInventario == 1) {
                    ((Inventario) mContext).subirtablaDB();
                } else if (((Inventario) mContext).loadingInventario == 3) {
                    ((Inventario) mContext).subirtablaDB();
                    ((Inventario) mContext).Pausarmaterial("SI");
                    ((Inventario) mContext).bloquearmaterial("NO");
                } else if (((Inventario) mContext).loadingInventario == 5) {
                    ((Inventario) mContext).activityMain();
                } else if (((Inventario) mContext).loadingInventario == 6) {
                    ((Inventario) mContext).subirtablaDB();
                    ((Inventario) mContext).Pausarmaterial("SI");
                    ((Inventario) mContext).menuScreen();
                }
                if (((Inventario) mContext).loadingInventario == 2) {
                    ((Inventario) mContext).subirtablaDB();
                    ((Inventario) mContext).crearPDF();
                    ((Inventario) mContext).Pausarmaterial("NO");
                    ((Inventario) mContext).bloquearmaterial("NO");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            //Ajustes de inventario
        }
        //Pausa
        else if (mContext instanceof Pausa) {
            if (((Pausa) mContext).loadingPausa == 1) {
                ((Pausa) mContext).cargardatos();
            } else if (((Pausa) mContext).loadingPausa == 2) {
                ((Pausa) mContext).activityMain();
            } else if (((Pausa) mContext).loadingPausa == 3) {
                ((Pausa) mContext).cargardatos();
            }
        } else if (mContext instanceof ListaPicking) {
            if (((ListaPicking) mContext).loadingpicking == 1) {
                ((ListaPicking) mContext).cargardatos();
            }
        } else if (mContext instanceof Inventarioscerrados) {
            if (((Inventarioscerrados) mContext).loadinginventarioscerrados == 1) {
                ((Inventarioscerrados) mContext).cargardatos();
            } else if (((Pausa) mContext).loadingPausa == 2) {
                ((Pausa) mContext).activityMain();
            } else if (((Pausa) mContext).loadingPausa == 3) {
                ((Pausa) mContext).cargardatos();
            }
        } else if (mContext instanceof ReporteInventariosCerrados) {
            if (((ReporteInventariosCerrados) mContext).loadingreportecerrados == 1) {
                ((ReporteInventariosCerrados) mContext).cargardatos();
            } else if (((Pausa) mContext).loadingPausa == 3) {
                ((Pausa) mContext).cargardatos();
            }
        }
        //PantallaDeRecepcion
        else if (mContext instanceof PantallaDeRecepcion) {
            if (((PantallaDeRecepcion) mContext).loadingRecepcion == 1) {
                //((PantallaDeRecepcion) mContext).subirtablaDB();
            }
        }
        //ListadeChequeo
        else if (mContext instanceof ListadeChequeo) {
            if (((ListadeChequeo) mContext).loadingListaChequeo == 1) {
                ((ListadeChequeo) mContext).cargardatosbackgroud();
            }
        }
        //ListaChequeoTerminado
        else if (mContext instanceof ListaChequeoTerminado) {
            if (((ListaChequeoTerminado) mContext).loadingListaDeReportes == 1) {
                ((ListaChequeoTerminado) mContext).cargardatosbackgroud();
            }
        }
        //ReportesInventarioGeneral
        else if (mContext instanceof ReportesInventarioGeneral) {
            if (((ReportesInventarioGeneral) mContext).loadinginventariogeneral == 1) {
                ((ReportesInventarioGeneral) mContext).cargardatos();
            }
        } else if (mContext instanceof InventarioActual) {
            if (((InventarioActual) mContext).loadinginventarioactual == 1) {
                ((InventarioActual) mContext).doinbackgroud();
            }
        } else if (mContext instanceof ReporteInventarioTerminado) {
            if (((ReporteInventarioTerminado) mContext).loadingReporteterminado == 1) {
                ((ReporteInventarioTerminado) mContext).cargardatosbacgroud();
            }
        } else if (mContext instanceof DetallesUsuario) {
            if (((DetallesUsuario) mContext).loadingDetallesUsuario == 1) {
                ((DetallesUsuario) mContext).crear_actualizarusuario();
            }
            if (((DetallesUsuario) mContext).loadingDetallesUsuario == 2) {
                ((DetallesUsuario) mContext).inicializar();
            }
        } else if (mContext instanceof Administrador) {
            if (((Administrador) mContext).loadingadministrador == 1)
                ((Administrador) mContext).cargandousuarios();

        }
        //Minutas menu
        else if(mContext instanceof MinutaMenu){
            try{
                ((MinutaMenu) mContext).openDialogReunion();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        //Minuta consultar
        else if(mContext instanceof MinutaConsultarMinutas){
            try{
                if(((MinutaConsultarMinutas) mContext).loadingMinutaConsulta == 1){
                    ((MinutaConsultarMinutas) mContext).fillRecyclerMinutas();
                }else if(((MinutaConsultarMinutas) mContext).loadingMinutaConsulta == 2){
                    ((MinutaConsultarMinutas) mContext).getMinutasPorFecha();
                }else if(((MinutaConsultarMinutas) mContext).loadingMinutaConsulta == 3){
                    ((MinutaConsultarMinutas) mContext).generateReporte(0);
                }else if(((MinutaConsultarMinutas) mContext).loadingMinutaConsulta == 4){
                    ((MinutaConsultarMinutas) mContext).generateReporte(1);
                }else if(((MinutaConsultarMinutas) mContext).loadingMinutaConsulta == 5){
                    ((MinutaConsultarMinutas) mContext).generateReporteExcel(0);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        //MINUTAS REGISTRAR
        else if(mContext instanceof MinutaReunionRegistro){
            try{
                ((MinutaReunionRegistro) mContext).setMinutaBD();

            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
        //MINUTAS DETALLE
        else if(mContext instanceof MinutaReunionDetalle){
            try{
                ((MinutaReunionDetalle) mContext).startGets();

            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        //Inventarios
        //Inventarios registro
        if (mContext instanceof InventariosRegistro) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventarios menú
        if (mContext instanceof InventariosMenu) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventarios en proceso
        if (mContext instanceof InventariosEnProceso) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventarios en proceso detalle
        if (mContext instanceof InventarioAEnProcesosDetalle) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventarios en historico
        if (mContext instanceof InventariosEnHistorico) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventarios en historico detalles
        if (mContext instanceof InventarioAEnHistoricosDetalle) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Main activity
        if (mContext instanceof MainActivity) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventario
        else if (mContext instanceof Inventario) {
            try {
                if (((Inventario) mContext).loadingInventario == 1) {
                    dialog.dismiss();
                } else if (((Inventario) mContext).loadingInventario == 2) {
                    ((Inventario) mContext).mensajedeconfirmacion();
                    dialog.dismiss();
                } else if (((Inventario) mContext).loadingInventario == 3) {
                    if (((Inventario) mContext).comprobaciondeestado == 1) {
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
                    } else if (((Inventario) mContext).comprobaciondeestado == 2) {
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
                } else if (((Inventario) mContext).loadingInventario == 4) {
                    dialog.dismiss();
                } else if (((Inventario) mContext).loadingInventario == 5) {
                    dialog.dismiss();
                } else if (((Inventario) mContext).loadingInventario == 6) {
                    dialog.dismiss();
                } else if (((Inventario) mContext).loadingInventario == 7) {
                    dialog.dismiss();
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof Pausa) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof Inventarioscerrados) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof ReporteInventariosCerrados) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof ListaPicking) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof PantalladePrioridades) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof PantallaDeRecepcion) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof ListadeChequeo) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof ListaChequeoTerminado) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof ReportesInventarioGeneral) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof InventarioActual) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof ReporteInventarioTerminado) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof DetallesUsuario) {
            try {

                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (mContext instanceof Administrador) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Minutas menu get lugares
        else if(mContext instanceof MinutaMenu){
            try{
                dialog.dismiss();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        //Minutas registro
        else if (mContext instanceof MinutaReunionRegistro) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Minutas consultar
        else if (mContext instanceof MinutaConsultarMinutas) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Minutas detalle
        else if (mContext instanceof MinutaReunionDetalle) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Inventarios
        //Inventarios registro
        if (mContext instanceof InventariosRegistro) {
            try {
                dialog = ProgressDialog.show(mContext, "Procesando", "Por favor, espere...", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventarios menú
        if (mContext instanceof InventariosMenu) {
            try {
                dialog = ProgressDialog.show(mContext, "Comprobando material", "Espere...", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventarios en proceso
        if (mContext instanceof InventariosEnProceso) {
            try {
                dialog = ProgressDialog.show(mContext, "Procesando", "Por favor, Espere...", false, false);
            } catch (Exception e) {
                new MaterialAlertDialogBuilder(mContext)
                        .setTitle("¡Error!")
                        .setIcon(R.drawable.snakerojo)
                        .setMessage("No hay conexión a internet, conectese e intentelo nuevamente.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //No hacer nada
                            }
                        })
                        .show();
            }
        }
        //Inventarios en proceso
        if (mContext instanceof InventarioAEnProcesosDetalle) {
            try {
                dialog = ProgressDialog.show(mContext, "Procesando", "Por favor, Espere...", false, false);
            } catch (Exception e) {
                new MaterialAlertDialogBuilder(mContext)
                        .setTitle("¡Error!")
                        .setIcon(R.drawable.snakerojo)
                        .setMessage("No hay conexión a internet, conectese e intentelo nuevamente.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //No hacer nada
                            }
                        })
                        .show();
            }
        }
        //Inventarios en historico
        if (mContext instanceof InventariosEnHistorico) {
            try {
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere...", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventarios en historico detalles
        if (mContext instanceof InventarioAEnHistoricosDetalle) {
            try {
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere...", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Main activity
        if (mContext instanceof MainActivity) {
            try {
                dialog = ProgressDialog.show(mContext, "Conectando con el servidor", "Espere...", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Inventario
        if (mContext instanceof Inventario) {
            try {
                dialog = ProgressDialog.show(mContext, "Subiendo al servidor", "Espere...", false, false);

            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof ListaPicking) {
            try {
                dialog = ProgressDialog.show(mContext, "Cargando datos", "Espere...", false, false);

            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Pausa
        if (mContext instanceof Pausa) {
            try {
                dialog = ProgressDialog.show(mContext, "Cargando Datos", "Espere...", false, false);

            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof Inventarioscerrados) {
            try {
                dialog = ProgressDialog.show(mContext, "Cargando Datos", "Espere...", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof ReporteInventariosCerrados) {
            try {
                dialog = ProgressDialog.show(mContext, "Cargando Datos", "Espere...", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof PantalladePrioridades) {
            try {
                dialog = ProgressDialog.show(mContext, "Creando reporte", "Espere...", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof PantallaDeRecepcion) {
            try {
                dialog = ProgressDialog.show(mContext, "Subiendo información", "Espere...", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof ListadeChequeo) {
            try {
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof ListaChequeoTerminado) {
            try {
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof ReportesInventarioGeneral) {
            try {
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof InventarioActual) {
            try {
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof ReporteInventarioTerminado) {
            try {
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof DetallesUsuario) {
            try {
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (mContext instanceof Administrador) {
            try {
                dialog = ProgressDialog.show(mContext, "Obteniendo información", "Espere un momento, Cargando datos", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Minuta menu get Lugares
        if(mContext instanceof  MinutaMenu){
            try{
                dialog = ProgressDialog.show(mContext, "Cargando pantalla", "Por favor espere un momento y verifique que tenga buena conexión a internet", false, false);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        //Registrar minutas
        if(mContext instanceof  MinutaReunionRegistro){
            try {
                dialog = ProgressDialog.show(mContext, "Guardando información", "Por favor espere un momento y verifique que tenga buena conexión a internet.", false, false);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        //Consultar minutas
        if (mContext instanceof MinutaConsultarMinutas) {
            try {
                dialog = ProgressDialog.show(mContext, "Cargando información", "Por favor espere un momento y verifique que tenga buena conexión a internet.", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //Minuta detalle
        if (mContext instanceof MinutaReunionDetalle) {
            try {
                dialog = ProgressDialog.show(mContext, "Cargando información", "Por favor espere un momento y verifique que tenga buena conexión a internet.", false, false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }
}
