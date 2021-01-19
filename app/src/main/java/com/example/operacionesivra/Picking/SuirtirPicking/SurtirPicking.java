package com.example.operacionesivra.Picking.SuirtirPicking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SurtirPicking extends AppCompatActivity implements EditarRegistroPickingInterface {
    private RecyclerView recycleritem;
    private AdapterContenidodelPedido adaptador;
    List<ModeloContenidodelPedido> items = new ArrayList<>();
    TextInputEditText entrada, entradamanual;
    Context context;

    float cantidadregistrada = 0;
    String estado;

    //Scanner
    private TextView clienteT, codigoT, contadoritems;
    String id, serie, cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picking_activity_surtir_picking);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        id = getIntent().getStringExtra("id");
        serie = getIntent().getStringExtra("serie");
        cliente = getIntent().getStringExtra("cliente");
        clienteT = findViewById(R.id.cliente);
        codigoT = findViewById(R.id.codigo);
        entrada = findViewById(R.id.entrada);
        entradamanual = findViewById(R.id.entradamanual);
        clienteT.setText(cliente);
        codigoT.setText(id);
        contadoritems = findViewById(R.id.contadoritems);
        recycleritem = findViewById(R.id.itempedidoRecycler);
        recycleritem.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdapterContenidodelPedido(obtenerpedidosdbImplementacion1());
        recycleritem.setAdapter(adaptador);
        contadoritems.setText(items.size() + "");
        entrada.setInputType(InputType.TYPE_NULL);
        entradamanual.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    actualizar(Objects.requireNonNull(entradamanual.getText()).toString());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(entradamanual.getWindowToken(), 0);
                    entradamanual.setText("");
                    return true;
                }
                return false;
            }
        });

        entrada.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Si el comando enter es enviado
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    actualizar(Objects.requireNonNull(entrada.getText()).toString());
                    entrada.setText("");
                    entradamanual.requestFocus();
                    return true;
                }
                return false;
            }
        });

      /*entrada.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
          }

          @Override
          public void afterTextChanged(Editable s) {
              Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                  public void run() {
                      if(!Objects.requireNonNull(entrada.getText()).toString().equals("")) {
                          actualizar(entrada.getText().toString());
                          entrada.setText("");
                          entrada.setTextInputLayoutFocusedRectEnabled(true);
                      }
                  }
              }, 1000);


          }

      });
      */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels; // ancho absoluto en pixels
        int height = metrics.heightPixels; // alto absoluto en pixels

        System.out.println(width+ "+"+height);

    }

    /*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int newOrientation = newConfig.orientation;

        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Do certain things when the user has switched to landscape.
        }
    }

     */
    public List<ModeloContenidodelPedido> obtenerpedidosdbImplementacion1() {
        Conexion conexion = new Conexion(this);
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        String idTemporal = "vacio";
        estado = "Incompleto";
        try {
            Statement qu = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PedidosPorSurtir_Productos '" + id + "', N'" + serie + "'");
            while (r.next()) {
                if (!r.getString(2).equals(idTemporal)) {
                    items.add(new ModeloContenidodelPedido("" + cantidadregistrada, r.getString(3), r.getString(5), estado, r.getString(1), R.drawable.noescaneado, ""));
                    idTemporal = r.getString(2);
                }
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Lo sentimos, error:\n"+e.toString()+"\nPor favor, reporte la falla con el area de sistemas")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    })
                    .show();
        }
        return items;
    }

    public void actualizar(String codigo) {
        Conexion conexion = new Conexion(this);
        boolean existe = false;
        try {
            Statement qu = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_Item_Scaneados '" + codigo + "', '01 GAMMA'");
            if (r.next()) {
                for (int i = 0; i < items.size(); i++) {
                    if (r.getString("Producto").equals(items.get(i).getNombredelmaterial())) {
                        existe = true;
                        float valordb;
                        String valordbstring;
                        float valorlista;
                        float valorlistacomparar;
                        valordbstring = r.getString("Longitud");
                        valordb = Float.parseFloat(valordbstring);
                        valorlistacomparar = Float.parseFloat(items.get(i).getCantidadsolicitada());
                        valorlista = Float.parseFloat(items.get(i).getCantidad());
                        valorlista = valordb + valorlista;
                        if (valorlista == valorlistacomparar) {
                            items.set(i, new ModeloContenidodelPedido(valorlista + "", items.get(i).getNombredelmaterial(), items.get(i).getCantidadsolicitada(), "Completo", items.get(i).getCodpedido(), R.drawable.correcto, items.get(i).getCoditem()));
                            recycleritem.getAdapter().notifyDataSetChanged();
                            MediaPlayer mp = MediaPlayer.create(this, R.raw.definite);
                            mp.start();
                            break;
                        } else if (valorlista < valorlistacomparar) {
                            items.set(i, new ModeloContenidodelPedido(valorlista + "", items.get(i).getNombredelmaterial(), items.get(i).getCantidadsolicitada(), "Incompleto", items.get(i).getCodpedido(), R.drawable.noescaneado, items.get(i).getCoditem()));
                            recycleritem.getAdapter().notifyDataSetChanged();
                            MediaPlayer mp = MediaPlayer.create(this, R.raw.definite);
                            mp.start();
                            break;

                        } else if (valorlista > valorlistacomparar) {
                            final float finalValorlista = valorlista;
                            final int finalI = i;
                            new MaterialAlertDialogBuilder(this)
                                    .setTitle("Atención")
                                    .setMessage("Al ingresar este item se sobrepasa la cantidad solicitada\n¿Desea continuar?")
                                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            items.set(finalI, new ModeloContenidodelPedido(finalValorlista + "", items.get(finalI).getNombredelmaterial(), items.get(finalI).getCantidadsolicitada(), "Completo", items.get(finalI).getCodpedido(), R.drawable.correcto, items.get(finalI).getCoditem()));
                                            recycleritem.getAdapter().notifyDataSetChanged();
                                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.definite);
                                            mp.start();
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
                }
                if (!existe) {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Error")
                            .setMessage("El material escaneado no corresponde con los que se encuentran en el pedido")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            } else {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("El material escaneado no corresponde con los que se encuentran en el pedido")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        } catch (SQLException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void editardialog(String material, String contenido) {
        EditarRegistroPicking editarRegistroPicking = new EditarRegistroPicking(material, contenido);
        editarRegistroPicking.show(getSupportFragmentManager(), null);
        editarRegistroPicking.setCancelable(false);
    }

    @Override
    public void editar(String material, String contenido) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getNombredelmaterial().equals(material)) {
                items.set(i, new ModeloContenidodelPedido(contenido, items.get(i).getNombredelmaterial(), items.get(i).getCantidadsolicitada(), items.get(i).getEstado(), items.get(i).getCodpedido(), items.get(i).getImagen(), items.get(i).getCoditem()));
                Objects.requireNonNull(recycleritem.getAdapter()).notifyDataSetChanged();
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.definite);
                mp.start();
            }
        }
    }
}
