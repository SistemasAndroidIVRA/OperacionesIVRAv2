package com.example.operacionesivra.Inventarios.Vistas.AjusteInventario;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.operacionesivra.Inventarios.Models.AjusteDetailsModel;
import com.example.operacionesivra.Inventarios.Models.AjusteModel;
import com.example.operacionesivra.Inventarios.Models.AlmacenModel;
import com.example.operacionesivra.Inventarios.Models.ProductoModel;
import com.example.operacionesivra.Inventarios.Models.SerieModel;
import com.example.operacionesivra.Inventarios.Models.UnidadMedidaModel;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Services.Conexion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class InventarioNuevoAjuste extends AppCompatActivity {
    //AjusteModel
    AjusteModel ajuste = new AjusteModel();
    //Contexto y conexión
    Context contexto = this;
    Conexion conexion = new Conexion();
    //Globales
    String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    String fechaHora = fecha + " " + hora;
    //ArrayLists
    ArrayList<AjusteDetailsModel> detallesAjuste = new ArrayList<>();
    ArrayList<SerieModel> series = new ArrayList<SerieModel>();
    ArrayList<AlmacenModel> almacenes = new ArrayList<AlmacenModel>();
    ArrayList<UnidadMedidaModel> medidas = new ArrayList<UnidadMedidaModel>();
    ArrayList<ProductoModel> productos = new ArrayList<ProductoModel>();
    ArrayList<String> observaciones1 = new ArrayList<>();
    ArrayList<String> observaciones2 = new ArrayList<>();
    //Seleccionados
    ProductoModel productoSeleccionado = new ProductoModel();
    //Spinners
    Spinner spinnerSerie, spinnerAlmacen, spinnerObs1, spinnerObs2, spinnerUnidadMedida;
    //Autocompletado
    AutoCompleteTextView txtProductoAutocompletado;
    //Button
    Button btnAgregar, btnAceptarAjuste, btnEliminarAjuste, btnRegresarNuevoAjuste;
    //EditText
    EditText txtCantidad, txtContenido;
    //TextCiew
    TextView lblContTotal;
    //Recyclervioew
    RecyclerView recyclerAjustes;
    //Adaptadores recycler
    AdapterAjuste adapterAjuste = new AdapterAjuste();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_a_nuevo_ajuste);
        //Referenciar elementos
        spinnerSerie = findViewById(R.id.spinnerSerie);
        spinnerAlmacen = findViewById(R.id.spinnerAlmacen);
        spinnerObs1 = findViewById(R.id.spinnerObs1);
        spinnerObs2 = findViewById(R.id.spinnerObs2);
        spinnerUnidadMedida = findViewById(R.id.spinnerUnidadMedida);
        txtProductoAutocompletado = findViewById(R.id.txtProductoAutocompletado);
        //Recyclerview
        recyclerAjustes = findViewById(R.id.recyclerAjustes);
        recyclerAjustes.setLayoutManager(new LinearLayoutManager(contexto));
        recyclerAjustes.setAdapter(adapterAjuste);
        //EditText
        txtCantidad = findViewById(R.id.txtCantidad);
        txtContenido = findViewById(R.id.txtContenido);
        //TextView
        lblContTotal = findViewById(R.id.lblContTotal);
        //Button
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAceptarAjuste = findViewById(R.id.btnAceptarAjuste);
        btnEliminarAjuste = findViewById(R.id.btnEliminarAjuste);
        btnRegresarNuevoAjuste = findViewById(R.id.btnRegresarNuevoAjuste);
        //Llenar spinners
        fullSpinners();
        txtProductoAutocompletado.setOnItemClickListener((adapterView, view, i, l) -> {
            productoSeleccionado = (ProductoModel) txtProductoAutocompletado.getAdapter().getItem(i);
        });

        //Acciones
        btnAgregar.setOnClickListener(view -> {
            //HYacer acción guardar
            agregarAjuste();
        });
        btnAceptarAjuste.setOnClickListener(view -> {
            subirBD();
        });
        btnEliminarAjuste.setOnClickListener(view -> {
            Toast.makeText(contexto, "Ésta acción está en desarrollo.", Toast.LENGTH_SHORT).show();
        });
        btnRegresarNuevoAjuste.setOnClickListener(view -> {
            cancelarAjuste();
        });
    }

    //Tecla de regresar
    @Override
    public void onBackPressed() {
        cancelarAjuste();
    }



    //Método para agregar un ajuste
    public void agregarAjuste() {
        if (productoSeleccionado == null || txtCantidad.getText().toString().equals("") || txtContenido.getText().toString().equals("")) {
            Toast.makeText(contexto, "Complete su información correctamente por favor.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                SerieModel serie = (SerieModel) spinnerSerie.getSelectedItem();
                AlmacenModel almacen = (AlmacenModel) spinnerAlmacen.getSelectedItem();
                String obs1 = spinnerObs1.getSelectedItem().toString();
                String obs2 = spinnerObs2.getSelectedItem().toString();
                UnidadMedidaModel unidadMedida = (UnidadMedidaModel) spinnerUnidadMedida.getSelectedItem();
                float cantidad = Float.parseFloat(txtCantidad.getText().toString()) * Float.parseFloat(txtContenido.getText().toString());
                detallesAjuste.add(new AjusteDetailsModel("", "", detallesAjuste.size() + 1, productoSeleccionado.getCodigo(), productoSeleccionado.getProductoID(), cantidad, productoSeleccionado.getNombre(), unidadMedida, 0, fecha));
                adapterAjuste.notifyDataSetChanged();
                Toast.makeText(contexto, "Registro agregado con éxito.", Toast.LENGTH_SHORT).show();
                vaciarCampos();
                lblContTotal.setText(""+getSumCantidad());
            } catch (Exception e) {
                Toast.makeText(contexto, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Método para subir la información a la BD
    public void subirBD() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        final String idusuario = preferences.getString("iduser", "Vacio");
        if (detallesAjuste.size() <= 0) {
            Toast.makeText(contexto, "No hay registros aún, acción no realizada.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                AlmacenModel almacen = (AlmacenModel) spinnerAlmacen.getSelectedItem();
                SerieModel serie = (SerieModel) spinnerSerie.getSelectedItem();
                //Crear ajuste
                PreparedStatement stmt1 = conexion.conexiondbImplementacion().prepareCall("P_AjusteInventario_INSERT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
                stmt1.setString(1, "");
                stmt1.setString(2, "11FF120F-942F-4F1B-87E6-7C7AD3D7AFB0");
                stmt1.setInt(3, 52);
                stmt1.setInt(4, 0);
                stmt1.setString(5, almacen.getAlmacenId());
                //Cantidad = sumatoria de toda la cantidad de los detalles
                stmt1.setFloat(6, getSumCantidad());
                stmt1.setString(7, idusuario);
                stmt1.setString(8, null);
                stmt1.setString(9, "A");
                stmt1.setString(10, serie.getCajaID());
                stmt1.setString(11, spinnerObs1.getSelectedItem().toString());
                stmt1.setString(12, spinnerObs2.getSelectedItem().toString());
                stmt1.setString(13, null);
                stmt1.setString(14, "Sin observaciones.");
                stmt1.setString(15, "Movil");
                ResultSet r1 = stmt1.executeQuery();
                if (r1.next()) {
                    ajuste.setCompraID(r1.getString("ComprasID"));
                    ajuste.setNumero(r1.getInt("Numero"));
                } else {
                    Toast.makeText(contexto, "Error: no se pudo registrar el ajuste.", Toast.LENGTH_SHORT).show();
                }

                //Validación para no hacer este proceso
                //Crear el detalle del ajuste
                PreparedStatement stmt2 = conexion.conexiondbImplementacion().prepareCall("P_AusteInventarioDetails_INSERT ?,?,?,?,?,?,?,?");
                for (int i = 0; i < detallesAjuste.size(); i++) {
                    stmt2.setString(1, ajuste.getCompraID());
                    stmt2.setInt(2, detallesAjuste.get(i).getRenglon());
                    stmt2.setString(3, detallesAjuste.get(i).getCodigo());
                    stmt2.setString(4, detallesAjuste.get(i).getProductoID());
                    stmt2.setFloat(5, detallesAjuste.get(i).getCantidad());
                    stmt2.setString(6, detallesAjuste.get(i).getNombre());
                    stmt2.setString(7, detallesAjuste.get(i).getUnidad().getUnidadID());
                    stmt2.setFloat(8, 0);
                    stmt2.execute();
                }

                //Insertar en Movil Reporte
                String uuid = UUID.randomUUID().toString().replace("-", "");
                PreparedStatement var = conexion.conexiondbImplementacion().prepareCall("PMovil_RegistrarMovilAjuste ?,?,?,?,?,?,?,?,?,?,?,?,?");
                for(int i = 0; i < detallesAjuste. size(); i++){
                    var.setFloat(1, detallesAjuste.get(i).getCantidad());
                    var.setFloat(2, detallesAjuste.get(i).getContenido());
                    var.setFloat(3, (detallesAjuste.get(i).getCantidad()) * (detallesAjuste.get(i).getContenido()));
                    var.setString(4, "Sin ubicación");
                    var.setString(5, idusuario);
                    var.setString(6, detallesAjuste.get(i).getNombre());
                    var.setString(7, fecha);
                    var.setString(8, ajuste.getAlmacen());
                    var.setString(9, "Ajuste de inventario");
                    var.setString(10, detallesAjuste.get(i).getProductoID());
                    var.setInt(11, ajuste.getNumero());
                    var.setInt(12, detallesAjuste.get(i).getRenglon());
                    var.setString(13, uuid);
                    var.execute();
                }


                //Validar si todo se ha ejecutado bien
                new MaterialAlertDialogBuilder(contexto)
                        .setTitle("¡Éxito!")
                        .setIcon(R.drawable.correcto)
                        .setCancelable(false)
                        .setMessage("Se ha registrado el ajuste exitosamente.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //No hacer nada
                                finish();
                            }
                        })
                        .show();

            } catch (Exception e) {
                Toast.makeText(contexto, "Error al subir a  BD: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Método que devuelve la suma de la cantidad de los detalles
    public float getSumCantidad() {
        float cantidad = 0;
        for (int i = 0; i < detallesAjuste.size(); i++) {
            cantidad = cantidad + detallesAjuste.get(i).getCantidad();
        }
        return cantidad;
    }

    //Método para cancelar el ajuste de inventario
    public void cancelarAjuste(){
        if(detallesAjuste.size() <= 0){
            Toast.makeText(contexto, "Sliendo...", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            new MaterialAlertDialogBuilder(contexto)
                    .setTitle("¡Acción necesaria!")
                    .setIcon(R.drawable.confirmacion)
                    .setMessage("¿Desea cancelar el ajuste y salir?")
                    .setPositiveButton("Cancelar acción", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                        }
                    })
                    .setNegativeButton("Guardar y salir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //No hacer nada
                            subirBD();
                        }
                    })
                    .setNeutralButton("Salir sin guardar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    //Método para activar todos los spinners
    public void fullSpinners(){
        fullSpinnerSerie();
        fullSpinnerAlmacen();
        fullSpinnerUnidadMedida();
        fullSpinnersObservaciones();
        fullAutocompletadoProducto();
    }

    //Método para llenar el filtro y spinner de productos
    public void fullAutocompletadoProducto(){
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("P_Productos_SELECT");
            ResultSet r = stmt.executeQuery();
            while (r.next()){
                productos.add(new ProductoModel(r.getString("ProductoID"), r.getString("Nombre"), r.getString("Codigo"), r.getInt("Status")));
            }
            ArrayAdapter<ProductoModel> arrayAdapter = new ArrayAdapter<ProductoModel>(contexto, R.layout.support_simple_spinner_dropdown_item, productos);
            txtProductoAutocompletado.setAdapter(arrayAdapter);
            Toast.makeText(contexto, "Productos: "+productos.size(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(contexto, "Error autocompletado: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Método para llenar spinner series
    public void fullSpinnerSerie(){
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("P_Caja_SELECT");
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                series.add(new SerieModel(r.getString("CajaID"), r.getString("Nombre"), r.getInt("Status")));
            }
            ArrayAdapter<SerieModel> arrayAdapter = new ArrayAdapter<SerieModel>(contexto, R.layout.support_simple_spinner_dropdown_item, series);
            spinnerSerie.setAdapter(arrayAdapter);
        }catch (Exception e){
            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //

    //Método para llenar el spinner de almacenes
    public void fullSpinnerAlmacen(){
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("P_Almacen_SELECT");
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                almacenes.add(new AlmacenModel(r.getString("AlmacenID"), r.getString("Nombre"), r.getInt("Status")));
            }
            ArrayAdapter<AlmacenModel> arrayAdapter = new ArrayAdapter<AlmacenModel>(contexto, R.layout.support_simple_spinner_dropdown_item, almacenes);
            spinnerAlmacen.setAdapter(arrayAdapter);
        }catch (Exception e){
            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Método para llenar el spinner de las observaciones
    public void fullSpinnersObservaciones(){
        try {
            //Traer observaciones 1
            PreparedStatement stmt1 = conexion.conexiondbImplementacion().prepareCall(" P_OBS1_SELECT");
            ResultSet r = stmt1.executeQuery();
            while(r.next()){
                observaciones1.add(r.getString("EnviarNombre"));
            }
            //Traer observaciones 2
            PreparedStatement stmt2 = conexion.conexiondbImplementacion().prepareCall(" P_OBS2_SELECT");
            ResultSet r2 = stmt2.executeQuery();
            while(r2.next()){
                observaciones2.add(r2.getString("EnviarDireccion"));
            }
            //Asignar al spinner
            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(contexto, R.layout.support_simple_spinner_dropdown_item, observaciones1);
            spinnerObs1.setAdapter(arrayAdapter1);
            ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(contexto, R.layout.support_simple_spinner_dropdown_item, observaciones2);
            spinnerObs2.setAdapter(arrayAdapter2);
        }catch (Exception e){
            Toast.makeText(contexto, "Error spinner observaciones: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Llenar spinner d eunidad d emedida
    public void fullSpinnerUnidadMedida(){
        try {
            PreparedStatement stmt = conexion.conexiondbImplementacion().prepareCall("P_UnidadMedida_SELECT");
            ResultSet r = stmt.executeQuery();
            while (r.next()){
                medidas.add(new UnidadMedidaModel(r.getString("UnidadID"), r.getString("Nombre"), r.getInt("Status")));
            }
            ArrayAdapter<UnidadMedidaModel> arrayAdapter = new ArrayAdapter<UnidadMedidaModel>(contexto, R.layout.support_simple_spinner_dropdown_item, medidas);
            spinnerUnidadMedida.setAdapter(arrayAdapter);
        }catch (Exception e){
            Toast.makeText(contexto, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Método para vaciar los campos
    public void vaciarCampos(){
        txtCantidad.setText("");
        txtCantidad.setHint("0.0");
        txtContenido.setText("");
        txtContenido.setHint("0.0");
        txtProductoAutocompletado.setText("");
        txtProductoAutocompletado.setHint("Teclear el producto");
        productoSeleccionado = null;
    }

    //Adapter ajuste
    public class AdapterAjuste extends RecyclerView.Adapter<AdapterAjuste.AdapterAjusteHolder> {
        @Override
        @NonNull
        public AdapterAjusteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new AdapterAjusteHolder(getLayoutInflater().inflate(R.layout.inventario_a_ajuste_nuevo_item, parent, false));
        }

        @Override
        public void onBindViewHolder(AdapterAjusteHolder holder, int position){
            holder.printAdapter(position);
        }

        @Override
        public int getItemCount(){
            return detallesAjuste.size();
        }

        class AdapterAjusteHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
            //Elementos
            TextView txtNumero, txtCodigo, txtProducto, txtUnidad, txtCantidadItem;
            CardView cardViewAjusteDetails;
            public AdapterAjusteHolder(@NonNull View itemView){
                super(itemView);
                txtNumero = itemView.findViewById(R.id.txtNumero);
                txtCodigo = itemView.findViewById(R.id.txtCodigo);
                txtProducto = itemView.findViewById(R.id.txtProducto);
                txtUnidad = itemView.findViewById(R.id.txtUnidad);
                txtCantidadItem = itemView.findViewById(R.id.txtCantidadItem);
                cardViewAjusteDetails = itemView.findViewById(R.id.cardViewAjusteDetails);
                //Acciones
                cardViewAjusteDetails.setOnClickListener(view -> {

                });
            }

            public void printAdapter(int position){
                //Imprimir datos que se van agregando
                txtNumero.setText(""+detallesAjuste.get(position).getRenglon());
                txtCodigo.setText(detallesAjuste.get(position).getCodigo());
                txtProducto.setText(detallesAjuste.get(position).getNombre());
                txtUnidad.setText(detallesAjuste.get(position).getUnidad().getNombre());
                txtCantidadItem.setText(""+detallesAjuste.get(position).getCantidad());
            }

            @Override
            public void onClick(View view){

            }
        }
    }
}