package com.example.operacionesivra.Chequeo.Surtir;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.operacionesivra.Chequeo.ListadePedidos.ListadeChequeo;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class DetallesChequeo extends AppCompatActivity {
    TextInputEditText manual, scanner;
    TextView cliente, pedido;
    String serie, referencia;
    Context context;
    String horainicio;
    String horafin;

    //Recycler
    Conexion conexionService = new Conexion(this);
    private RecyclerView recycerpedidos;
    private AdapterDetallesChequeo adaptador;
    List<ModeloDetallesChequeo> itemsdechequeo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chequeo_detalles_chequeo);

        inicializar();

    }

    //hace funcional los text input para que el usuario no tenga que ver un teclado
    public void inicializar() {
        //Definir y dale valor al pedido
        pedido = findViewById(R.id.pedidoChequeo);
        pedido.setText(getIntent().getStringExtra("pedidoChequeo"));
        serie = getIntent().getStringExtra("serieChequeo");
        cliente = findViewById(R.id.clienteChequeo);
        cliente.setText(getIntent().getStringExtra("clienteChequeo"));
        referencia = getIntent().getStringExtra("referenciaChequeo");

        //Recycler
        recycerpedidos = findViewById(R.id.RecyclerDetallesChequeo);
        recycerpedidos.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdapterDetallesChequeo(obtenerpedidosdbImplementacion());
        recycerpedidos.setAdapter(adaptador);

        //definiciones
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        scanner = findViewById(R.id.scannerchequeo);
        scanner.setInputType(InputType.TYPE_NULL);
        manual = findViewById(R.id.manualchequeo);

        //Scanner automatico
        scanner.requestFocus();
        scanner.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER))) {
                    comprobardatos(scanner.getText().toString());
                    scanner.setText("");
                    if (Objects.requireNonNull(scanner.getText()).toString().equals("")) {
                        scanner.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        manual.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    comprobardatos(manual.getText().toString());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(manual.getWindowToken(), 0);
                    manual.setText("");
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.guardarchequeo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminarchequeo();
            }
        });

        horainicio = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    //Obtiene los items del pedido actual
    public List<ModeloDetallesChequeo> obtenerpedidosdbImplementacion() {
        //Guarda el id del pedido de manera momentanea para determinar si el mismo pedido ya exite
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_PedidosPorSurtir_Productos '" + pedido.getText().toString() + "', N'" + serie + "'");
            while (r.next()) {
                itemsdechequeo.add(new ModeloDetallesChequeo(r.getString("Producto"), r.getString("Cantidad"), r.getString("Unidad"), R.drawable.noescaneado, false));
            }
        } catch (Exception e) {
            System.out.println(e + "a ver a ver");
        }
        return itemsdechequeo;
    }

    //Revisa el codigo leido y lo compara con los items a ver si forman parte del pedido
    public void comprobardatos(String codigo) {
        boolean comprobar = false;
        try {
            Statement qu = conexionService.conexiondbImplementacion().createStatement();
            ResultSet r = qu.executeQuery("Execute PMovil_Item_Scaneados '" + codigo + "', '01 GAMMA'");
            if (r.next()) {
                for (int i = 0; i < itemsdechequeo.size(); i++) {
                    if (r.getString("Producto").equals(itemsdechequeo.get(i).getMaterial())) {
                        itemsdechequeo.set(i, new ModeloDetallesChequeo(itemsdechequeo.get(i).getMaterial(), itemsdechequeo.get(i).getCantidad(), itemsdechequeo.get(i).getUnidad(), R.drawable.correcto, true));
                        Objects.requireNonNull(recycerpedidos.getAdapter()).notifyItemChanged(i);
                        comprobar = true;
                    }
                }
                if (!comprobar) {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Error")
                            .setIcon(R.drawable.noencontrado)
                            .setMessage("Este material no está dentro del pedido")
                            .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        } catch (SQLException e) {
            Toast.makeText(context, "El material no existe", Toast.LENGTH_SHORT).show();
        }
    }

    //Termina el chequeo guardando los datos en la tabla correspondiente (Ahora esta desactivada la comprobación)
    public void terminarchequeo() {
        int contadorcorrecto = 0;
        for (int i = 0; i < itemsdechequeo.size(); i++) {
            if (itemsdechequeo.get(i).getCorrecto()) {
                contadorcorrecto++;
            }
        }
        if (contadorcorrecto != itemsdechequeo.size()) {
            subirtablaDB();
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Todo listo!")
                    .setIcon(R.drawable.correcto)
                    .setCancelable(false)
                    .setMessage("Registro guardado con éxito!")
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            terminado();
                        }
                    })
                    .show();
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Datos Incompletos")
                    .setIcon(R.drawable.noescaneado)
                    .setCancelable(false)
                    .setMessage("Aún no se ha completado el registro de los materiales del pedido!")
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    //Termina la ejecición de la actividad
    public void terminado() {
        Intent i = new Intent(this, ListadeChequeo.class);
        startActivity(i);
        finish();
    }

    //Lanza un mensaje de confimación al avandonar la activity
    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmación")
                .setMessage("No se a terminado el proceso, ¿Quiere terminarlo?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        terminado();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    //Sube la informacion a la base de datos
    public void subirtablaDB() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        horafin = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        try {
            try (PreparedStatement var = conexionService.conexiondbImplementacion().prepareCall("Execute P_Movil_insertardatosChequeo ?,?,?,?,?,?,?)")) {
                try {
                    var.setString(1, pedido.getText().toString());
                    var.setString(2, serie);
                    var.setString(3, cliente.getText().toString());
                    var.setString(4, referencia);
                    var.setString(5, date);
                    var.setString(6, horainicio);
                    var.setString(7, horafin);
                    var.execute();
                } catch (SQLException e) {
                    System.out.println("Error" + e);
                }
            } catch (SQLException a) {
                System.out.println("Error2" + a);
            }
        } catch (Exception e) {
            System.out.println("Error3" + e);
        }
    }

}