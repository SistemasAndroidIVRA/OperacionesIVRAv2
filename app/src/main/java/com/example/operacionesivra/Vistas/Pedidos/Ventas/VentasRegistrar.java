package com.example.operacionesivra.Vistas.Pedidos.Ventas;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.operacionesivra.Modelos.Cliente;
import com.example.operacionesivra.Modelos.Moneda;
import com.example.operacionesivra.Modelos.Producto;
import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Services.Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class VentasRegistrar extends AppCompatActivity {
    //Contexto
    Context contexto = this;
    //Conexión
    Conexion con = new Conexion();
    //Declarar elementos
    ImageButton btnVentasRegRegresar;
    //Spinners
    Spinner spinnerVentaMoneda, spinnerVentaFacturas, spinnerVentaReferencia, spinnerVentaDocumento;
    AutoCompleteTextView txtVentaProductos, txtVentaCliente;
    //AutoCompleteTextView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos_a_ventas_registrar);
        //Referenciar elementos
        //Spinners
        spinnerVentaMoneda = findViewById(R.id.pinnerVentaMoneda);
        spinnerVentaFacturas = findViewById(R.id.spinnerVentaFacturas);
        spinnerVentaReferencia = findViewById(R.id.spinnerVentaReferencia);
        spinnerVentaDocumento = findViewById(R.id.spinnerVentaDocumento);
        txtVentaProductos = findViewById(R.id.txtVentaProductos);
        txtVentaCliente = findViewById(R.id.txtVentaCliente);
        //Botones
        btnVentasRegRegresar = findViewById(R.id.btnVentasRegRegresar);
        //Acciones
        btnVentasRegRegresar.setOnClickListener(view -> {
            finish();
        });

        fullSpinners();
    }

    //Método para llenar los spinners
    public void fullSpinners(){
        ArrayAdapter adapterMoneda = new ArrayAdapter(contexto, R.layout.support_simple_spinner_dropdown_item, getTipoMoneda());
        spinnerVentaMoneda.setAdapter(adapterMoneda);
        ArrayAdapter adapterFacturarPiezas = new ArrayAdapter(contexto, R.layout.support_simple_spinner_dropdown_item, getFacturarPiezas());
        spinnerVentaFacturas.setAdapter(adapterFacturarPiezas);
        ArrayAdapter adapterProductos = new ArrayAdapter(contexto, R.layout.support_simple_spinner_dropdown_item, getProductos());
        txtVentaProductos.setAdapter(adapterProductos);
        ArrayAdapter adapterReferencia = new ArrayAdapter(contexto, R.layout.support_simple_spinner_dropdown_item, getReferencias());
        spinnerVentaReferencia.setAdapter(adapterReferencia);
        ArrayAdapter adapterTipoDocumento = new ArrayAdapter(contexto, R.layout.support_simple_spinner_dropdown_item, getTipoDocumento());
        spinnerVentaDocumento.setAdapter(adapterTipoDocumento);
        ArrayAdapter adapterCliente = new ArrayAdapter(contexto, R.layout.support_simple_spinner_dropdown_item, getClientes());
        txtVentaCliente.setAdapter(adapterCliente);
    }

    //Método para traer los tipos de moneda
    public ArrayList<Moneda> getTipoMoneda(){
        try {
            ArrayList<Moneda> array = new ArrayList<>();
            PreparedStatement statement = con.conexiondbImplementacion().prepareStatement("P_Moneda_SELECT");
            ResultSet r = statement.executeQuery();
            while(r.next()){
                Moneda moneda = new Moneda(r.getString("MonedaID"), r.getString("Nombre"), r.getString("Abreviatura"), r.getInt("Status"));
                array.add(moneda);
            }
            return array;
        }catch (Exception e){
            Toast.makeText(contexto, "Error al traer moneda: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }
    }

    //Método para traer facturar por piezas
    public ArrayList<String> getFacturarPiezas(){
        ArrayList<String> lista = new ArrayList<>();
        lista.add("0");
        lista.add("2000");
        lista.add("5000");
        return lista;
    }

    //Método para traer los productos
    public ArrayList<Producto> getProductos(){
        try {
            ArrayList<Producto> array = new ArrayList<>();
            PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("P_Productos2_SELECT");
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                Producto producto = new Producto();
                producto.setProductoID("ProductoID");
                producto.setNombre(r.getString("Nombre"));
                producto.setCodigo(r.getString("Codigo"));
                producto.setStatus(r.getInt("Status"));
                producto.setBloqueado(r.getInt("Bloqueado"));
                array.add(producto);
            }
            return array;
        }catch (Exception e){
            Toast.makeText(contexto, "Error al traer productos: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }
    }

    //Método para traer las referencias
    public ArrayList<String> getReferencias(){
        try {
            ArrayList<String> array = new ArrayList<>();
            PreparedStatement statement = con.conexiondbImplementacion().prepareStatement("P_Referencia_SELECT");
            ResultSet r = statement.executeQuery();
            while(r.next()){
                array.add(r.getString("Referencia"));
            }
            return array;
        }catch (Exception e){
            Toast.makeText(contexto, "Error al traer referencias: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }
    }

    //Método para traer los tipos de documento
    public ArrayList<String> getTipoDocumento(){
        try {
            ArrayList<String> array = new ArrayList<>();
            PreparedStatement statement = con.conexiondbImplementacion().prepareStatement("P_TipoDocumento_SELECT");
            ResultSet r = statement.executeQuery();
            while (r.next()){
                array.add(r.getString("EnviarNombre"));
            }
            return array;
        }catch (Exception e){
            Toast.makeText(contexto, "Error al traer tipo de documento: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }
    }

    //Método para traer los clientes
    public ArrayList<Cliente> getClientes(){
        try {
            ArrayList<Cliente> array = new ArrayList<>();
            PreparedStatement stmt = con.conexiondbImplementacion().prepareCall("P_Clientes_SELECT");
            ResultSet r = stmt.executeQuery();
            while(r.next()){
                Cliente cliente = new Cliente();
                cliente.setClienteID(r.getString("ClienteID"));
                cliente.setNombre(r.getString("Nombre"));
                array.add(cliente);
            }
            return array;
        }catch (Exception e){
            Toast.makeText(contexto, "Error al traer clientes: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }
    }
}