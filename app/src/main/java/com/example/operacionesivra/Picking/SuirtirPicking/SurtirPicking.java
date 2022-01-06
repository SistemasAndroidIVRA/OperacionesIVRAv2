package com.example.operacionesivra.Picking.SuirtirPicking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SurtirPicking extends AppCompatActivity {
    private RecyclerView recycleritem;
    private AdapterContenidodelPedido adaptador;
    List<ModeloContenidodelPedido> items = new ArrayList<>();
    TextInputEditText entrada, entradamanual;
    Context context;

    private TextView cliente, codigo, contadoritems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picking_activity_surtir_picking);
        inicializarVariables();
        iniciarlisteners();
    }

    //Listener del tipo de entrada que se le de, en este caso es para el correcto funcioamiento del scanner
    public void iniciarlisteners() {
        entradamanual.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    registrar(Objects.requireNonNull(entradamanual.getText()).toString());
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
                    registrar(Objects.requireNonNull(entrada.getText()).toString());
                    entrada.setText("");
                    entradamanual.requestFocus();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.terminar_sp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearetiquetas();
            }
        });
    }

    public void inicializarVariables() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        cliente = findViewById(R.id.cliente_sp);
        codigo = findViewById(R.id.codigo_sp);
        entrada = findViewById(R.id.scanner_sp);
        entradamanual = findViewById(R.id.entradamanual_sp);
        contadoritems = findViewById(R.id.contadoritems_sp);

        cliente.setText(getIntent().getStringExtra("cliente"));
        codigo.setText(getIntent().getStringExtra("id"));
        recycleritem = findViewById(R.id.itempedidoRecycler);
        recycleritem.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdapterContenidodelPedido(obtenerItemsPedidos());
        recycleritem.setAdapter(adaptador);
        contadoritems.setText(items.size() + "");
        entrada.setInputType(InputType.TYPE_NULL);
    }

    public List<ModeloContenidodelPedido> obtenerItemsPedidos() {
        Conexion conexion = new Conexion(this);
        String estado = "Incompleto";
        try {
            Statement qu = conexion.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PedidosPorSurtir_Productos '"
                    + getIntent().getStringExtra("id") + "', N'" + getIntent().getStringExtra("serie") + "'");
            while (r.next()) {
                items.add(new ModeloContenidodelPedido(0f, r.getString("Producto"),
                        r.getFloat("Cantidad"), estado, r.getString("CodProducto"),
                        R.drawable.noescaneado,
                        getIntent().getStringExtra("serie") + getIntent().getStringExtra("id"), ""));

            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Lo sentimos, error:\n" + e.toString() + "\nPor favor, reporte la falla con el area de sistemas")
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

    //Registra un codigo si es que pertenece al pedido actual
    public void registrar(final String codigo) {
        Conexion c = new Conexion(context);
        boolean existe = false;
        try {
            Statement s = c.conexiondbImplementacion().createStatement();
            ResultSet r = s.executeQuery("Execute PMovil_Item_Scaneados '" + codigo + "', '01 GAMMA'");
            if (r.next()) {
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getCodproducto().equals(r.getString("CodProducto"))) {
                        existe = true;
                        final float cantidadregistrada = items.get(i).getCantidad() + r.getFloat("longitud");
                        switch (comprobarEstado(cantidadregistrada, items.get(i).getCantidadsolicitada())) {
                            case 1:
                                final int finalI = i;
                                new MaterialAlertDialogBuilder(context)
                                        .setTitle("Confirmación")
                                        .setMessage("Al registrar este material está superando la cantidad solicitada.\n" +
                                                "¿Continuar?")
                                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                items.set(finalI, new ModeloContenidodelPedido(cantidadregistrada,
                                                        items.get(finalI).getNombredelmaterial(), items.get(finalI).getCantidadsolicitada(),
                                                        "Completo", items.get(finalI).getCodproducto(), R.drawable.correcto, items.get(finalI).getCodPedido()
                                                        , codigo));
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                                break;
                            case 2:
                                items.set(i, new ModeloContenidodelPedido(cantidadregistrada,
                                        items.get(i).getNombredelmaterial(), items.get(i).getCantidadsolicitada(),
                                        "Incompleto", items.get(i).getCodproducto(), R.drawable.noescaneado, items.get(i).getCodPedido()
                                        , codigo));
                                break;
                            case 3:
                                items.set(i, new ModeloContenidodelPedido(cantidadregistrada,
                                        items.get(i).getNombredelmaterial(), items.get(i).getCantidadsolicitada(),
                                        "Completo", items.get(i).getCodproducto(), R.drawable.correcto, items.get(i).getCodPedido()
                                        , codigo));
                                break;
                        }

                    }
                }
                if (!existe) {
                    Toast.makeText(context, "El material no es parte de este pedido", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Codigo no encontrado", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(context, "Codigo no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    //comprueba si el item ya está completo, si lo supera o no esta completo
    public int comprobarEstado(float cantidad, float cantidadsolicitada) {
        //1 mas material
        //2 menos material
        //3 material completo
        int resultado = 0;
        try {
            if (cantidad > cantidadsolicitada) {
                resultado = 1;
            } else if (cantidad < cantidadsolicitada) {
                resultado = 2;
            } else if (cantidad == cantidadsolicitada) {
                resultado = 3;
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error al realizar comprobacion: " + e, Toast.LENGTH_LONG).show();
        }
        return resultado;
    }

    //Guarda la informacion en una tabla
    public void crearetiquetas() {
        Conexion c = new Conexion(this);
        try (PreparedStatement p = c.conexiondbImplementacion().prepareCall("Execute PMovil_Crearetiquetas ?,?,?,?,?,?,?,?")) {
            for (int i = 0; i < items.size(); i++) {
                p.setString(1, items.get(i).getCodproducto());
                p.setString(2, items.get(i).getNombredelmaterial());
                p.setString(3, cliente.getText().toString());
                p.setString(4, getIntent().getStringExtra("serie") + getIntent().getStringExtra("id"));
                p.setFloat(5, items.get(i).getCantidad());
                p.setString(6, "1");
                p.setString(7, "2bcf");
                p.setString(8, items.get(i).getCodimpreso());
                p.execute();
            }
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Error al cargar informacion: " + e)
                    .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }


}
