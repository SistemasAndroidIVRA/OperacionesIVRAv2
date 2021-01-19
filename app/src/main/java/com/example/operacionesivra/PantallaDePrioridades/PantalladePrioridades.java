package com.example.operacionesivra.PantallaDePrioridades;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.DigitalClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.operacionesivra.MainActivity.MainActivity;
import com.example.operacionesivra.PantallasCargando.Loading;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import harmony.java.awt.Color;
import online.devliving.mobilevisionpipeline.FrameGraphic;

public class PantalladePrioridades extends AppCompatActivity {
    Conexion conexionService = new Conexion(this);
    public RecyclerView listadeprioridades;
    public AdapterPantallaPrioridades adaptador;
    public List<ModeloPantalladePrioridades> listadepedidos = new ArrayList<>();
    DigitalClock digitalClock;
    String hotatem;
    Date guardado, actual;
    Context context;
    String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    int disparador;
    TextView reporte, fechav, vistaactual, itemstotales;

    private final DBListener duckFactory = new DBListener(this);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prioridades_pantallade_prioridades);
        listadeprioridades = findViewById(R.id.recyclerprioridades);
        listadeprioridades.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdapterPantallaPrioridades(obtenerpedidosdbImplementacion());
        listadeprioridades.setAdapter(adaptador);
        digitalClock = findViewById(R.id.reloj);
        vistaactual = findViewById(R.id.vistaactual);
        itemstotales = findViewById(R.id.totaldeitems);
        duckFactory.start();
        context = this;
        disparador = 2;
        hotatem = digitalClock.getText().toString();
        reporte = findViewById(R.id.pedidoboton);
        fechav = findViewById(R.id.fechaprioridades);
        fechav.setText(fecha);
        fechav.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        vistaactual.setText("C1");

        digitalClock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void afterTextChanged(Editable s) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                try {
                    actual = format.parse(digitalClock.getText().toString());
                    if (disparador == 2) {
                        guardado = format.parse(digitalClock.getText().toString());
                        guardado.setTime(actual.getTime() + 5);
                        comprobarentrega();
                    }
                    disparador = 1;
                    long diff = guardado.getTime() - actual.getTime();//as given
                    final long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                    if (minutes == -5) {
                        guardado.setTime(actual.getTime() + 5);
                        comprobarentrega();
                    }
                    /*
                    String horaactual = digitalClock.getText() + "";
                    if (!disparadorautomatico) {
                        crearreporteautomatico(horaactual);
                    }

                     */

                } catch (ParseException e) {
                    Toast.makeText(context, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Lanza una notificación durante 5 segundos
    public void notificacion(String pedido, String cliente, String hora, String movimiento, String referencia) {
        try {
            final Movimientoitem movimientoitem = new Movimientoitem(pedido, cliente, hora, movimiento, referencia);
            movimientoitem.show(getSupportFragmentManager(), null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    movimientoitem.dismiss();
                }
            }, 5000);
        } catch (Exception e) {
            Toast.makeText(context, "Error al notificar: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    //Crea una lista que almacena los datos de la base de manera automatica (Implementacion)
    public List<ModeloPantalladePrioridades> obtenerpedidosdbImplementacion() {
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        int posicion = 0;
        String idTemporal = "a";
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            final ResultSet r = qu.executeQuery("Execute PMovil_Prioridades");
            while (r.next()) {
                if (posicion == 0) {
                    idTemporal = r.getString(2);
                    if (r.getString("Entrega").substring(0, 10).equals(fecha)) {
                        String horaactual=new SimpleDateFormat("HH: mm: ss", Locale.getDefault()).format(new Date());
                        listadepedidos.add(new ModeloPantalladePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), R.drawable.hoy, r.getString("Entrega").substring(0, 10), "On Time"));
                        //listadepedidos.add(new ModeloPantalladePrioridades(r.getString("Cliente"), r.getString("Pedido"), tiemportranscurrido(r.getString("Entrega").substring(11, 16)+":00",horaactual), r.getString("Referencia"), R.drawable.hoy, r.getString("Entrega").substring(0, 10), "On Time"));
                        //registrodeldia.add(new ModeloReportePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), r.getString("Entrega").substring(0, 10), "-", "NO", "-"));
                    } else {
                        listadepedidos.add(new ModeloPantalladePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), R.drawable.ayer, r.getString("Entrega").substring(0, 10), "On Delay"));
                        //registrodeldia.add(new ModeloReportePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), r.getString("Entrega").substring(0, 10), "-", "NO", "-"));
                    }

                } else if (!r.getString(2).equals(idTemporal)) {
                    if (r.getString("Entrega").substring(0, 10).equals(fecha)) {
                        listadepedidos.add(new ModeloPantalladePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), R.drawable.hoy, r.getString("Entrega").substring(0, 10), "On Time"));
                        //registrodeldia.add(new ModeloReportePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), r.getString("Entrega").substring(0, 10), "-", "NO", "-"));
                        idTemporal = r.getString(2);
                        posicion++;
                    } else {
                        listadepedidos.add(new ModeloPantalladePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), R.drawable.ayer, r.getString("Entrega").substring(0, 10), "On Delay"));
                        //registrodeldia.add(new ModeloReportePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), r.getString("Entrega").substring(0, 10), "-", "NO", "-"));
                        idTemporal = r.getString(2);
                        posicion++;
                    }
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return listadepedidos;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void comprobarentrega() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date actual;
        Date base;
        try {
            for (int i = 0; i < listadepedidos.size(); i++) {
                base = format.parse(listadepedidos.get(i).getEntrega().replace("Hrs", ""));
                actual = format.parse(digitalClock.getText().toString());
                long diff = base.getTime() - actual.getTime();//as given
                final long hours = TimeUnit.MILLISECONDS.toHours(diff);
                final long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);


                if (listadepedidos.get(i).getFecha().equals(fecha)) {
                    if (minutes > 20 && listadepedidos.get(i).getReferencia().equals("R3")) {
                        listadepedidos.set(i, new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido()
                                , listadepedidos.get(i).getEntrega(), listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), "On Delay"));
                        Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(i);
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                MediaPlayer mp = MediaPlayer.create(context, R.raw.error_text_message);
                                mp.start();
                                Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(finalI);
                                //cumbia(listadepedidos.get(finalI).getPedido() + "Retrasado");
                            }
                        });
                    }
                    if (minutes <= 20 && minutes >= 0 && listadepedidos.get(i).getReferencia().equals("R3")) {
                        listadepedidos.set(i, new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido()
                                , listadepedidos.get(i).getEntrega(), listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), "On Time"));
                        final int finalI1 = i;
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(finalI1);
                            }
                        });
                    } else if (minutes <= 0 && hours == 0) {
                        listadepedidos.set(i, new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido()
                                , listadepedidos.get(i).getEntrega(), listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), "On Delay"));

                        Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(i);
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(finalI);
                            }
                        });
                    } else if (hours < 0) {
                        if (hours == -1) {
                            final String horast = hours + ";";
                            listadepedidos.set(i, new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido()
                                    , listadepedidos.get(i).getEntrega(), listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), "On Delay"));

                            Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(i);
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void run() {
                                    Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(finalI);
                                }
                            });
                        } else {
                            listadepedidos.set(i, new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido()
                                    , listadepedidos.get(i).getEntrega(), listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), "On Delay"));

                            Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(i);
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void run() {
                                    Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(finalI);
                                }
                            });
                        }
                    }
                } else {
                    listadepedidos.set(i, new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido()
                            , listadepedidos.get(i).getEntrega(), listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), "On Delay"));
                    Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(i);
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemChanged(finalI);
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error: "+e);
        }
    }

    public void añadiralalista() {
        int contador = 0;
        String hour = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_Prioridades");
            while (r.next()) {
                boolean comprobarexistencia = false;
                for (int i = 0; i < listadepedidos.size(); i++) {
                    if (r.getString("Pedido").equals(listadepedidos.get(i).getPedido())) {
                        comprobarexistencia = true;
                    }
                }
                if (!comprobarexistencia) {
                    final int contador2 = contador;
                    if (r.getString("Entrega").substring(0, 10).equals(fecha) && r.getString("Referencia").equals("R3")) {
                        listadepedidos.add(0, new ModeloPantalladePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), R.drawable.hoy, r.getString("Entrega").substring(0, 10), "On Time"));
                        //registrodeldia.add(new ModeloReportePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), r.getString("Entrega").substring(0, 10), "-", "NO", "-"));
                        final String a = r.getString("Pedido");
                        final String b = r.getString("Cliente");
                        final String c = hour;
                        final String d = r.getString("Referencia");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                notificacion(a, b, c, "Nuevo pedido", d);

                            }
                        });
                    } else if (r.getString("Entrega").substring(0, 10).equals(fecha)) {
                        listadepedidos.add(new ModeloPantalladePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), R.drawable.hoy, r.getString("Entrega").substring(0, 10), "On Time"));
                        //registrodeldia.add(new ModeloReportePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), r.getString("Entrega").substring(0, 10), "-", "NO", "-"));
                        final String a = r.getString("Pedido");
                        final String b = r.getString("Cliente");
                        final String c = hour;
                        final String d = r.getString("Referencia");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                notificacion(a, b, c, "Nuevo pedido", d);

                            }
                        });
                    } else {
                        listadepedidos.add(new ModeloPantalladePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), R.drawable.ayer, r.getString("Entrega").substring(0, 10), "On Delay"));
                        //registrodeldia.add(new ModeloReportePrioridades(r.getString("Cliente"), r.getString("Pedido"), r.getString("Entrega").substring(11, 16).concat(" Hrs"), r.getString("Referencia"), r.getString("Entrega").substring(0, 10), "-", "NO", "-"));
                        final String a = r.getString("Pedido");
                        final String b = r.getString("Cliente");
                        final String c = hour;
                        final String d = r.getString("Referencia");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                notificacion(a, b, c, "Nuevo pedido", d);

                            }
                        });
                    }
                    final String pedido = r.getString("Pedido");
                    final String referencia = r.getString("Referencia");
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemInserted(contador2);
                        }
                    });
                    if (referencia.equals("R3")) {
                        MediaPlayer mp = MediaPlayer.create(this, R.raw.notification_ring);
                        mp.start();
                        //cumbia("Nuevo erre 3, numero" + pedido.substring(2));
                    } else {
                        MediaPlayer mp = MediaPlayer.create(this, R.raw.notification_ring);
                        mp.start();
                        //cumbia("Nuevo pedido");
                    }
                }
                contador++;
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show();
        }
    }

    public void elimarlista() {
        String hour = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            for (int i = 0; i < listadepedidos.size(); i++) {
                ResultSet r2 = qu.executeQuery("Execute PMovil_Prioridades");
                boolean comprobarexistencia = false;
                while (r2.next()) {
                    if (listadepedidos.get(i).getPedido().equals(r2.getString("Pedido"))) {
                        comprobarexistencia = true;
                    }
                }
                if (!comprobarexistencia) {
                    //String transcurrido = tiemportranscurrido(listadepedidos.get(i).entrega.replace("Hrs", ""), hour) + "";
                    //registrodeldia.set(i, new ModeloReportePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido(), listadepedidos.get(i).entrega, listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getFecha(), hour + " Hrs", "Si", transcurrido.replace("-", "")));

                    final String a = listadepedidos.get(i).getPedido();
                    final String b = listadepedidos.get(i).getCliente();
                    final String c = hour;
                    final String d = listadepedidos.get(i).getReferencia();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            notificacion(a, b, c, "Pedido surtido", d);

                        }
                    });
                    final String pedido = listadepedidos.get(i).pedido;
                    listadepedidos.remove(i);
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            Objects.requireNonNull(listadeprioridades.getAdapter()).notifyItemRemoved(finalI);
                        }
                    });
                    MediaPlayer mp = MediaPlayer.create(this, R.raw.notification_ring);
                    mp.start();
                    //cumbia("Pedido " + pedido.substring(2) + " Entregado");
                }
            }
        } catch (SQLException e) {
            Toast.makeText(context, "Error al actualizar tabla", Toast.LENGTH_SHORT).show();
        }
    }

    /*-------------------------Navegacion de vistas automatico------------------------------------*/

    public void cambiodevistas() {
        switch (vistaactual.getText().toString()) {
            case "Todos los pedidos":
                runOnUiThread(new Runnable() {
                    public void run() {
                        eleguirtipodepedido(r3(), "R3");

                    }
                });
                break;
            case "R3":
                runOnUiThread(new Runnable() {
                    public void run() {
                        eleguirtipodepedido(r2(), "R2");
                    }
                });
                break;
            case "R2":
                runOnUiThread(new Runnable() {
                    public void run() {
                        eleguirtipodepedido(r1(), "R1");
                    }
                });
                break;
            case "R1":
                runOnUiThread(new Runnable() {
                    public void run() {
                        eleguirtipodepedido(enviar(), "ENVIAR");
                    }
                });
                break;
            case "ENVIAR":
                runOnUiThread(new Runnable() {
                    public void run() {
                        eleguirtipodepedido(c1(), "C1");
                    }
                });
                break;
            case "C1":
                runOnUiThread(new Runnable() {
                    public void run() {
                        eleguirtipodepedido(todoslospedidos(), "Todos los pedidos");
                    }
                });
                break;

        }
    }

    public void eleguirtipodepedido(List<ModeloPantalladePrioridades> list, final String itemactual) {
        final List<ModeloPantalladePrioridades> actual = list;
        runOnUiThread(new Runnable() {
            public void run() {
                listadeprioridades = null;
                listadeprioridades = findViewById(R.id.recyclerprioridades);
                listadeprioridades.setLayoutManager(new LinearLayoutManager(context));
                adaptador = new AdapterPantallaPrioridades(actual);
                listadeprioridades.setAdapter(adaptador);
                listadeprioridades.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fragment_open_enter));
                Objects.requireNonNull(listadeprioridades.getAdapter()).notifyDataSetChanged();
                vistaactual.setText(itemactual);
            }
        });
    }

    public List<ModeloPantalladePrioridades> todoslospedidos() {
        List<ModeloPantalladePrioridades> r3 = new ArrayList<>();
        for (int i = 0; i < listadepedidos.size(); i++) {
            r3.add(new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido(), listadepedidos.get(i).getEntrega()
                    , listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), listadepedidos.get(i).getEstadotexto()));
        }
        itemstotales.setText(r3.size() + "");
        return r3;
    }

    public List<ModeloPantalladePrioridades> r3() {
        List<ModeloPantalladePrioridades> r3 = new ArrayList<>();
        for (int i = 0; i < listadepedidos.size(); i++) {
            if (listadepedidos.get(i).getReferencia().equals("R3")) {
                r3.add(new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido(), listadepedidos.get(i).getEntrega()
                        , listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), listadepedidos.get(i).getEstadotexto()));
            }
        }
        itemstotales.setText(r3.size() + "");
        return r3;
    }

    public List<ModeloPantalladePrioridades> r2() {
        List<ModeloPantalladePrioridades> r3 = new ArrayList<>();
        for (int i = 0; i < listadepedidos.size(); i++) {
            if (listadepedidos.get(i).getReferencia().equals("R2")) {
                r3.add(new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido(), listadepedidos.get(i).getEntrega()
                        , listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), listadepedidos.get(i).getEstadotexto()));
            }
        }
        itemstotales.setText(r3.size() + "");
        return r3;
    }

    public List<ModeloPantalladePrioridades> r1() {
        List<ModeloPantalladePrioridades> r3 = new ArrayList<>();
        for (int i = 0; i < listadepedidos.size(); i++) {
            if (listadepedidos.get(i).getReferencia().equals("R1")) {
                r3.add(new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido(), listadepedidos.get(i).getEntrega()
                        , listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), listadepedidos.get(i).getEstadotexto()));
            }
        }
        itemstotales.setText(r3.size() + "");
        return r3;
    }

    public List<ModeloPantalladePrioridades> enviar() {
        List<ModeloPantalladePrioridades> r3 = new ArrayList<>();
        for (int i = 0; i < listadepedidos.size(); i++) {
            if (listadepedidos.get(i).getReferencia().equals("ENVIAR")) {
                r3.add(new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido(), listadepedidos.get(i).getEntrega()
                        , listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), listadepedidos.get(i).getEstadotexto()));
            }
        }
        itemstotales.setText(r3.size() + "");
        return r3;
    }

    public List<ModeloPantalladePrioridades> c1() {
        List<ModeloPantalladePrioridades> r3 = new ArrayList<>();
        for (int i = 0; i < listadepedidos.size(); i++) {
            if (listadepedidos.get(i).getReferencia().equals("C1")) {
                r3.add(new ModeloPantalladePrioridades(listadepedidos.get(i).getCliente(), listadepedidos.get(i).getPedido(), listadepedidos.get(i).getEntrega()
                        , listadepedidos.get(i).getReferencia(), listadepedidos.get(i).getDia(), listadepedidos.get(i).getFecha(), listadepedidos.get(i).getEstadotexto()));
            }
        }
        itemstotales.setText(r3.size() + "");
        return r3;
    }

    /*------------------------------------Reporte-------------------------------------------------*/

    public void loadinglauncher() {
        Loading loading = new Loading(this);
        loading.execute();
    }

    @Override
    public void onBackPressed() {
        terminar();
    }

    public void terminar() {
        duckFactory.terminar();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}